package com.vilia.miarrobawebscrapper.scrapper;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaThread;

public class ThreadScrapper {
	private static Logger logger = LoggerFactory.getLogger(ForumScrapper.class);
	
	private MiarrobaThread thread;
	
	public ThreadScrapper(MiarrobaThread thread) {
		this.thread = thread;
	}
	
	public ThreadScrapper(URL url) {
		this.thread = new MiarrobaThread();
		this.thread.setThreadUrl(url);
	}

}
