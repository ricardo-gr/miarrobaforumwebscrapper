package com.vilia.miarrobawebscrapper.scrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.model.MiarrobaThread;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperFunctionalUtils;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;

public class ForumScrapper {
	private static Logger logger = LoggerFactory.getLogger(ForumScrapper.class);

	private static final String TITLE_XPATH = "/html/body/div[@id='SectionForo']/table[@id = 'ForoMenuIndice']/tbody/tr[contains(@class, 'tablaRowFirst')]/td/div/div[@class = 'columnsContainer']/div/a[1]";

	private static final String ROOT_FORUM_CONTENT_SECTION_XPATH = "/html/body/div[@id='SectionComu']";
	private static final String SUBFORUM_ENTRIES_XPATH = ROOT_FORUM_CONTENT_SECTION_XPATH
			+ "/table[not(@id)]/tbody/tr[contains(@class, 'tablaRow')]/td[contains(@class, 'ancho100')]/div/a[1]";
	private static final String SUBFORUM_ENTRY_XPATH = "/td/div/a[1]/@href";

	private static final String SUBFORUM_CONTENT_SECTION_XPATH = "/html/body/div[@id='SectionForo']";
	private static final String THREADS_TABLE_XPATH = SUBFORUM_CONTENT_SECTION_XPATH
			+ "/table[@id = 'ForoIndiceTemas']/tbody";
	private static final String THREAD_ENTRIES_XPATH = THREADS_TABLE_XPATH
			+ "/tr[contains(@class, 'tablaRow')]/td[contains(@class, 'ancho100')]/div[@class = 'topicMsg']/a";
	private static final String THREADS_PAGINATOR_XPATH = THREADS_TABLE_XPATH
			+ "/tr[contains(@class, 'boxTitle')]/td/div[@class = 'paginacionForos']/div[@class='paginador']/a[contains(@class, 'number')]";

	private URL forumUrl;
	private MiarrobaForum parentForum;
	private List<URL> subForumURLs;
	private List<MiarrobaThread> threads;
	private MiarrobaForum forum;

	public ForumScrapper(URL forumUrl, MiarrobaForum parentForum) {
		this.forumUrl = forumUrl;
		this.parentForum = parentForum;
		subForumURLs = new ArrayList<>();
		threads = new ArrayList<>();

		forum = new MiarrobaForum();
		forum.setParentForum(parentForum);
		forum.setForumUrl(forumUrl);
	}

	public URL getForumUrl() {
		return this.forumUrl;
	}

	public MiarrobaForum parseForum() throws ForumScrapperException {
		ScrapperUrlConnector connection = connectToForum();

		checkMiarrobaForum(connection);

		parseForumTitle(connection);

		if (this.forum.isRootForum())
			parseSubForums(connection);
		else {
			parseThreads(connection);
			this.forum.setThreads(this.threads);
		}

		return this.forum;
	}

	private ScrapperUrlConnector connectToForum() throws ForumScrapperException {
		ScrapperUrlConnector connection = new ScrapperUrlConnector(forumUrl);

		if (!connection.isConnectionStablished()) {
			throw new ForumScrapperException("Connection not stablished", forumUrl, "Unknown");
		}

		return connection;
	}

	private void checkMiarrobaForum(ScrapperUrlConnector connection) throws ForumScrapperException {
		Document doc = connection.getDocument();

		if (!isMiarrobaForum(doc))
			throw new ForumScrapperException("The selected URL is not a MiArroba forum", forumUrl, "unknown");

		if (isRootForum(doc)) {
			if (!this.forum.isRootForum()) {
				// TODO: Define correct treatment in this case
				logger.warn(String.format(
						"Current forum url %s is a root forum. A parent forum was passed. Execution will continue for debug purposes",
						forumUrl));
				this.forum.setParentForum(null);
			}
		} else if (this.forum.isRootForum()) {
			// TODO: Define correct treatment in this case
			logger.warn(String.format(
					"Current forum url %s is NOT a root forum. A parent forum was not passed. Execution will continue as it was a Root Forum",
					forumUrl));
		}
	}

	private boolean isMiarrobaForum(Document doc) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean isRootForum(Document doc) {
		Elements subforumsSection = doc.selectXpath(ROOT_FORUM_CONTENT_SECTION_XPATH);
		Elements threadsSection = doc.selectXpath(SUBFORUM_CONTENT_SECTION_XPATH);

		if (subforumsSection.size() > 0)
			return true;

		if (threadsSection.size() > 0)
			return false;

		// TODO: Change with exception
		return false;
	}

	private void parseForumTitle(ScrapperUrlConnector connection) throws ForumScrapperException {
		Document doc = connection.getDocument();

		Elements title = doc.selectXpath(TITLE_XPATH);

		if (title.size() > 0)
			initializeInnerForumTitle(title.first());
		else
			initializeFirstForumTitle();
	}

	private void initializeInnerForumTitle(Element titleElement) throws ForumScrapperException {
		String title = titleElement.text();

		this.forum.setForumTitle(title);
	}

	private void initializeFirstForumTitle() throws ForumScrapperException {
		Pattern p = Pattern.compile("http[s]?://(\\w+)\\.mforos\\.com");
		Matcher m = p.matcher(forumUrl.toString());

		if (!m.find())
			throw new ForumScrapperException("Title could not be parsed from URL", this.forumUrl, "unknown");

		String forumTitle = m.group(1);

		this.forum.setForumTitle(forumTitle);
	}

	private void parseSubForums(ScrapperUrlConnector connection) {
		Document doc = connection.getDocument();

		Elements subforumsXml = doc.selectXpath(SUBFORUM_ENTRIES_XPATH);

		for (Element subforumXml : subforumsXml) {
			String subforumAnchor = subforumXml.attr("href");
			
			if (subforumAnchor == null || ObjectUtils.isEmpty(subforumAnchor))
				continue;

			URL subformUrl = null;

			try {
				subformUrl = new URL(forumUrl + subforumAnchor);
			} catch (MalformedURLException e) {
				logger.error(
						String.format("Error while parsing subforum. No URL could be created: %s", subforumAnchor));
				continue;
			}

			this.subForumURLs.add(subformUrl);
		}

		// TODO: Save forum to DB

		//TODO: Manage retries and test execution logic vs test execution logic
		/*this.subForumURLs.stream().map(url -> new ForumScrapper(url, this.forum)).forEach((forum) -> {
			try {
				forum.parseForum();
			} catch (ForumScrapperException e) {
				logger.error(String.format("Error while parsing subforum URL: %s. Parsing for this subform stopped.",
						forum.getForumUrl().toString()), e);
			}
		});*/
	}

	private void parseThreads(ScrapperUrlConnector connection) {
		List<URL> additionalPages = calculateThreadsPages(connection);

		scrapThreadsPage(connection);

		additionalPages.stream().forEach(this::scrapAdditionalThreadPages);
		
		//TODO: Scrap threads themselves

	}

	private List<URL> calculateThreadsPages(ScrapperUrlConnector connection) {
		List<URL> urlList = new ArrayList<>();
		
		Document doc = connection.getDocument();

		Elements threadsPaginatorXml = doc.selectXpath(THREADS_PAGINATOR_XPATH);
		
		if (threadsPaginatorXml.isEmpty())
			return urlList;
		
		urlList = threadsPaginatorXml.stream().map(page -> ScrapperFunctionalUtils.parseHrefAttributeMethod(page, this.forum.getRootForumUrl()))
				.filter(Objects::nonNull)
				.toList();
		
		return urlList;
	}

	private void scrapThreadsPage(ScrapperUrlConnector connection) {
		Document doc = connection.getDocument();

		Elements threadsXml = doc.selectXpath(THREAD_ENTRIES_XPATH);
		
		List<MiarrobaThread> threadsInPage = parseThreadsFromAnchorList(threadsXml);
		
		this.threads.addAll(threadsInPage);
	}

	private List<MiarrobaThread> parseThreadsFromAnchorList(Elements threadsXml) {
		return threadsXml.stream().map(anchor -> {
			MiarrobaThread thread = new MiarrobaThread();
			
			String threadTitle = anchor.text();
			String threadHref = anchor.attr("href");
			URL threadHrefUrl = null;
			
			URL baseForumUrl = getBaseForumUrl();
			
			try {
				threadHrefUrl = new URL(ScrapperFunctionalUtils.getBaseUrl(baseForumUrl) + threadHref);
			} catch (Exception e) {
				logger.error(String.format("Couldn't create URL for thread '%s' url in href: %s. Base url: %s", threadTitle, threadHref, baseForumUrl), e);
				return null;
			}
				
			
			thread.setThreadTitle(threadTitle);
			thread.setThreadUrl(threadHrefUrl);
			
			return thread;
		}).filter(Objects::nonNull)
				.toList();
	}

	private URL getBaseForumUrl() {
		return this.forum.isRootForum() ? this.forumUrl : this.parentForum.getForumUrl();
	}

	private void scrapAdditionalThreadPages(URL url) {
		ScrapperUrlConnector pageConnection = new ScrapperUrlConnector(url);
		
		scrapThreadsPage(pageConnection);
	}

}
