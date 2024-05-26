package com.vilia.miarrobawebscrapper.scrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.model.MiarrobaThread;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;

public class ForumScrapper {
	private static Logger logger = LoggerFactory.getLogger(ForumScrapper.class);
	
	private static final String TITLE_XPATH = "/html/body/div[@id='SectionForo']/table[@id = 'ForoMenuIndice']/tbody/tr[contains(@class, 'tablaRowFirst')]/td/div/div[@class = 'columnsContainer']/div/a[1]";
	
	private static final String ROOT_FORUM_CONTENT_SECTION_XPATH = "/html/body/div[@id='mmenu_wrapper']/div[@id='SectionComu']";
	private static final String SUBFORUM_ENTRIES_XPATH = ROOT_FORUM_CONTENT_SECTION_XPATH + "/table[not(@id)]/tbody/tr[contains(@class, 'tablaRow')]/td[@class='ancho100']/div/a[1]";
	private static final String SUBFORUM_ENTRY_XPATH = "/td/div/a[1]/@href";
	
	private static final String SUBFORUM_CONTENT_SECTION_XPATH = "/html/body/div[@id='mmenu_wrapper']/div[@id='SectionForo']";
	private static final String THREAD_ENTRIES_XPATH = SUBFORUM_CONTENT_SECTION_XPATH + "/table[@id = 'ForoIndiceTemas']/tbody/tr[contains(@class, 'tablaRow')]/td[@class='ancho100']/div[@class = 'topicMsg']/a";
	
	private URL forumUrl;
	private MiarrobaForum parentForum;
	private List<URL> subForumURLs;
	private List<MiarrobaThread> threads;
	private MiarrobaForum forum;
	private boolean isRootForum;
	
	public ForumScrapper(URL forumUrl, MiarrobaForum parentForum) {
		this.forumUrl = forumUrl;
		this.parentForum = parentForum;
		subForumURLs = new ArrayList<>();
		threads = new ArrayList<>();
		
		forum = new MiarrobaForum();
		forum.setParentForum(parentForum);
		forum.setForumUrl(forumUrl);
		
		if(this.parentForum == null)
			this.isRootForum = true;
	}
	
	public void parseForum() {
		ScrapperUrlConnector connection;
		try {
			connection = connectToForum();
		} catch (ForumScrapperException e) {
			logger.error("Forum Connection error: " + e.getMessage(), e);
			return;
		}
		
		try {
			checkMiarrobaForum(connection);
		} catch (ForumScrapperException e) {
			logger.error("Forum validation error: " + e.getMessage(), e);
			return;
		}
		
		try {
			parseForumTitle(connection);
		} catch (ForumScrapperException e) {
			logger.error("Forum Title parse error: " + e.getMessage(), e);
			return;
		}
		
		if(this.isRootForum)
			parseSubForums(connection);
		else
			parseThreads(connection);
	}

	private ScrapperUrlConnector connectToForum() throws ForumScrapperException {
		ScrapperUrlConnector connection = new ScrapperUrlConnector(forumUrl);
		
		if (!connection.isConnectionStablished()) {
			logger.error("Connection not stablished: ", forumUrl);
			throw new ForumScrapperException("Connection not stablished", forumUrl, "Unknown");
		}
		
		return connection;
	}
	
	private void checkMiarrobaForum(ScrapperUrlConnector connection) throws ForumScrapperException {
		Document doc = connection.getDocument();
		
		if (!isMiarrobaForum(doc))
			throw new ForumScrapperException("The selected URL is not a MiArroba forum", forumUrl, "unknown");
		
		if (isRootForum(doc)) {
			if (!this.isRootForum) {
				//TODO: Define correct treatment in this case
				logger.warn(String.format("Current forum url %s is a root forum. A parent forum was passed. Execution will continue for debug purposes", forumUrl));
				this.isRootForum = true;
				this.parentForum = null;
			}
		} else 
			if (this.isRootForum) {
				//TODO: Define correct treatment in this case
				logger.warn(String.format("Current forum url %s is NOT a root forum. A parent forum was not passed. Execution will continue for debug purposes", forumUrl));
				this.isRootForum = false;
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
		
		//TODO: Change with exception
		return false;
	}

	private void parseForumTitle(ScrapperUrlConnector connection) throws ForumScrapperException {
		Document doc = connection.getDocument();
		
		Elements title = doc.selectXpath(TITLE_XPATH);
		
		if(title.size() > 0)
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
			String subforumAnchor = subforumXml.selectXpath(SUBFORUM_ENTRY_XPATH).text();
			
			URL subformUrl = null;
			
			try {
				subformUrl = new URL(forumUrl + subforumAnchor);
			} catch (MalformedURLException e) {
				logger.error(String.format("Error while parsing subforum. No URL could be created: %s", subforumAnchor));
				continue;
			}
			
			this.subForumURLs.add(subformUrl);
		}
		
		//TODO: Save forum to DB
		
		this.subForumURLs.stream()
			.map(url -> new ForumScrapper(url, this.forum))
			.forEach(ForumScrapper::parseForum);
	}
	
	
	private void parseThreads(ScrapperUrlConnector connection) {
		// TODO Auto-generated method stub
		
	}

}
