package com.vilia.miarrobawebscrapper.scrapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.model.MiarrobaThread;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;

public class ForumScrapper {
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
	
	public MiarrobaForum parseForum() {
		ScrapperUrlConnector connection = connectToForum();
		parseForumTitle();
		parseForumThreads();
		
		parseThreads();
		
		return forum;
	}

	private ScrapperUrlConnector connectToForum() {
		// TODO Auto-generated method stub
		return null;
	}

	private void parseForumTitle() {
		
	}
	
	private void parseForumThreads() {
		
	}
	
	
	private void parseThreads() {
		// TODO Auto-generated method stub
		
	}

}
