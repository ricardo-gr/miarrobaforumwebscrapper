package com.vilia.miarrobawebscrapper.scrapper.forumscrapper;

import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.scrapper.ForumScrapper;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrappingUtils;

public class ForumScrapperFactory {

	private static Logger logger = LoggerFactory.getLogger(ForumScrapperFactory.class);
	
	private ForumScrapperFactory() {}
	
	public static ForumScrapper getRootForumScrapper(URL forumUrl) throws ForumScrapperException {
		ScrapperUrlConnector connection = ScrappingUtils.connectToForum(forumUrl);
		
		Document doc = connection.getDocument();

		if (isRootForum(doc)) {
			return new RootForumScrapper(forumUrl);
		} else {
			throw new ForumScrapperException("The passed URL is not a Root Forum", forumUrl, "Unknown");
		}
	}
	
	public static ForumScrapper getSubForumScrapper(URL forumUrl, MiarrobaForum parentForum) throws ForumScrapperException {
		ScrapperUrlConnector connection = ScrappingUtils.connectToForum(forumUrl);
		
		Document doc = connection.getDocument();

		if (isSubForum(doc)) {
			return new SubForumScrapper(forumUrl, parentForum);
		} else {
			throw new ForumScrapperException("The passed URL is not a SubForum", forumUrl, "Unknown");
		}
	}
	
	private static boolean isRootForum(Document doc) {
		Elements subforumsSection = doc.selectXpath(ScrappingUtils.ROOT_FORUM_CONTENT_SECTION_XPATH);

		return subforumsSection.size() > 0;

	}
	
	private static boolean isSubForum(Document doc) throws ForumScrapperException {
		Elements threadsSection = doc.selectXpath(ScrappingUtils.SUBFORUM_CONTENT_SECTION_XPATH);

		return threadsSection.size() > 0;
	}
}
