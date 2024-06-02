package com.vilia.miarrobawebscrapper.scrapper.forumscrapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.model.MiarrobaThread;
import com.vilia.miarrobawebscrapper.scrapper.ForumScrapper;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrappingUtils;

public class SubForumScrapper extends ForumScrapper {
	private static Logger logger = LoggerFactory.getLogger(SubForumScrapper.class);
	
	private static final String TITLE_XPATH = "/html/body/div[@id='SectionForo']/table[@id = 'ForoMenuIndice']/tbody/tr[contains(@class, 'tablaRowFirst')]/td/div/div[@class = 'columnsContainer']/div/a[1]";
	
	private static final String THREADS_TABLE_XPATH = ScrappingUtils.SUBFORUM_CONTENT_SECTION_XPATH
			+ "/table[@id = 'ForoIndiceTemas']/tbody";
	private static final String THREAD_ENTRIES_XPATH = THREADS_TABLE_XPATH
			+ "/tr[contains(@class, 'tablaRow')]/td[contains(@class, 'ancho100')]/div[@class = 'topicMsg']/a";
	private static final String THREADS_PAGINATOR_XPATH = THREADS_TABLE_XPATH
			+ "/tr[contains(@class, 'boxTitle')]/td/div[@class = 'paginacionForos']/div[@class='paginador']/a[contains(@class, 'number')]";

	private List<MiarrobaThread> threads;
	
	public SubForumScrapper(URL forumUrl, MiarrobaForum parentForum) {
		super(forumUrl);
		
		threads = new ArrayList<>();
		
		super.forum.setParentForum(parentForum);
	}
	
	protected void parseForumTitle(ScrapperUrlConnector connection) throws ForumScrapperException {
		Document doc = connection.getDocument();

		Elements titleXml = doc.selectXpath(TITLE_XPATH);

		String title = titleXml.text();

		this.forum.setForumTitle(title);
	}
	
	protected void parseForumContent(ScrapperUrlConnector connection) throws ForumScrapperException {
		List<URL> additionalPages = calculateThreadsPages(connection);

		scrapThreadsPage(connection);

		additionalPages.stream().forEach(this::scrapAdditionalThreadPages);
		
		this.forum.setThreads(this.threads);
		
		//TODO: Scrap threads themselves
	}
	
	private List<URL> calculateThreadsPages(ScrapperUrlConnector connection) {
		List<URL> urlList = new ArrayList<>();
		
		Document doc = connection.getDocument();

		Elements threadsPaginatorXml = doc.selectXpath(THREADS_PAGINATOR_XPATH);
		
		if (threadsPaginatorXml.isEmpty())
			return urlList;
		
		urlList = threadsPaginatorXml.stream().map(page -> ScrappingUtils.parseHrefAttributeMethod(page, this.forum.getRootForumUrl()))
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
				threadHrefUrl = new URL(ScrappingUtils.getBaseUrl(baseForumUrl) + threadHref);
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
	
	private void scrapAdditionalThreadPages(URL url) {
		ScrapperUrlConnector pageConnection = new ScrapperUrlConnector(url);
		
		scrapThreadsPage(pageConnection);
	}
	
	

}
