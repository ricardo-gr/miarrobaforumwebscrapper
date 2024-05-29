package com.vilia.miarrobawebscrapper.scrapper.support;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrapperUrlConnector {
	private static final Logger logger = LoggerFactory.getLogger(ScrapperUrlConnector.class);
	
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";
	
	private URL targetUrl;
	private Document doc = null;
	
	public ScrapperUrlConnector(URL targetUrl) {
		
		try {
			doc = Jsoup.connect(targetUrl.toString())
					.userAgent(USER_AGENT)
					.header("Accept-Language", "*")
					.get();
		} catch (IOException e) {
			logger.error(String.format("Error while connecting to URL: %s", targetUrl), e);
		}
		
		this.targetUrl = targetUrl;
	}
	
	public Document getDocument() {
		return doc;
	}
	
	public URL getUrl() {
		return targetUrl;
	}
	
	public boolean isConnectionStablished() {
		return doc != null;
	}

}
