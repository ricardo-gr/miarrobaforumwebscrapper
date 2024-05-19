package com.vilia.miarrobawebscrapper.scrapper;

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
	
	private static final String TITLE_XPATH = "/html/body/div[@id=\"SectionForo\"]/table[@id = \"ForoMenuIndice\"]/tbody/tr[contains(@class, \"tablaRowFirst\")]/td/div/div[@class = \"columnsContainer\"]/div/a[1]";
	
	private URL forumUrl;
	private MiarrobaForum parentForum;
	private List<MiarrobaThread> threads;
	private MiarrobaForum forum;
	
	public ForumScrapper(URL forumUrl, MiarrobaForum parentForum) {
		this.forumUrl = forumUrl;
		this.parentForum = parentForum;
		threads = new ArrayList<>();
		
		forum = new MiarrobaForum();
		forum.setParentForum(parentForum);
		forum.setForumUrl(forumUrl);
	}
	
	public MiarrobaForum parseForum() throws ForumScrapperException {
		ScrapperUrlConnector connection = connectToForum();
		parseForumTitle(connection);
		parseForumThreads(connection);
		
		parseThreads();
		
		return forum;
	}

	private ScrapperUrlConnector connectToForum() throws ForumScrapperException {
		ScrapperUrlConnector connection = new ScrapperUrlConnector(forumUrl);
		
		if (!connection.isConnectionStablished()) {
			logger.error("Connection not stablished: ", forumUrl);
			throw new ForumScrapperException("Connection not stablished", forumUrl, "Unknown");
		}
		
		return connection;
	}

	private void parseForumTitle(ScrapperUrlConnector connection) {
		Document doc = connection.getDocument();
		
		Elements title = doc.selectXpath(TITLE_XPATH);
		
		if(title.size() > 0)
			initializeInnerForumTitle(title.first());
		else
			initializeFirstForumTitle();
	}
	
	private void initializeInnerForumTitle(Element titleElement) {
		String title = titleElement.text();
		
		this.forum.setForumTitle(title);
	}

	private void initializeFirstForumTitle() {
		Pattern p = Pattern.compile("http[s]?://(\\w+)\\.mforos\\.com");
		Matcher m = p.matcher(forumUrl.toString());
		
		String forumTitle = m.group(1);
		
		this.forum.setForumTitle(forumTitle);		
	}

	private void parseForumThreads(ScrapperUrlConnector connection) {
		
	}
	
	
	private void parseThreads() {
		// TODO Auto-generated method stub
		
	}

}
