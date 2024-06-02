package com.vilia.miarrobawebscrapper.scrapper;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrappingUtils;

public abstract class ForumScrapper {
	private static Logger logger = LoggerFactory.getLogger(ForumScrapper.class);
	
	private URL forumUrl;	
	protected MiarrobaForum forum;

	public ForumScrapper(URL forumUrl) {
		this.forumUrl = forumUrl;		

		forum = new MiarrobaForum();
		forum.setForumUrl(forumUrl);
	}

	public URL getForumUrl() {
		return this.forumUrl;
	}

	public MiarrobaForum parseForum() throws ForumScrapperException {
		ScrapperUrlConnector connection = ScrappingUtils.connectToForum(this.forumUrl);

		parseForumTitle(connection);
		
		parseForumContent(connection);

		return this.forum;
	}	

	protected abstract void parseForumTitle(ScrapperUrlConnector connection) throws ForumScrapperException;
	
	protected abstract void parseForumContent(ScrapperUrlConnector connection) throws ForumScrapperException;

	protected URL getBaseForumUrl() {
		return this.forum.getRootForumUrl();
	}
}
