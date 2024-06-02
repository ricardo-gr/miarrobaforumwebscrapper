package com.vilia.miarrobawebscrapper.scrapper.exception;

import java.net.URL;

public class ForumScrapperException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2649440677885075846L;
	private URL forumUrl;
	private String forumName;
	
	public ForumScrapperException(URL forumUrl, String forumName) {
		super();
		this.forumUrl = forumUrl;
		this.forumName = forumName;
	}
	
	public ForumScrapperException(String message, URL forumUrl, String forumName) {
		super(message);
		this.forumUrl = forumUrl;
		this.forumName = forumName;
	}
	
	public ForumScrapperException(String message, Throwable t, URL forumUrl, String forumName) {
		super(message, t);
		this.forumUrl = forumUrl;
		this.forumName = forumName;
	}

	public URL getForumUrl() {
		return forumUrl;
	}

	public String getForumName() {
		return forumName;
	}
	
	
}
