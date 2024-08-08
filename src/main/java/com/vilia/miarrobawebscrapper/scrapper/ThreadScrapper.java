package com.vilia.miarrobawebscrapper.scrapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaThread;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrappingUtils;

public class ThreadScrapper {
	private static Logger logger = LoggerFactory.getLogger(ThreadScrapper.class);
	
	private static final String THREAD_TITLE_XPATH = ScrappingUtils.THREAD_CONTENT_SECTION_XPATH 
			+ "/table[@id = 'ForoMenuIndice']/tbody/tr/td/div[@id = 'to-the-top']/div[@class = 'columnsContainer']/div[@class = 'texto_big']/a";
	private static final String THREAD_PAGINATOR_XPATH = ScrappingUtils.THREAD_CONTENT_SECTION_XPATH
			+ "/table/tbody/tr/td/div[@class = 'paginacionForos']/div[@class = 'paginador']";
	private static final String THREAD_MESSAGES_XPATH = ScrappingUtils.THREAD_CONTENT_SECTION_XPATH
			+ "/table[contains(@id, 'ForoMensaje']";	
	
	private MiarrobaThread thread;
	
	public ThreadScrapper(MiarrobaThread thread) {
		this.thread = thread;
	}
	
	public ThreadScrapper(URL url) {
		this.thread = new MiarrobaThread();
		this.thread.setThreadUrl(url);
	}
	
	public URL getThreadUrl() {
		return this.thread.getThreadUrl();
	}
	
	public MiarrobaThread parseThread() throws ForumScrapperException {
		ScrapperUrlConnector connection = ScrappingUtils.connectToForum(getThreadUrl());
		
		parseThreadTitle(connection);
		
		List<URL> threadPages = parsePaginator(connection);
		
		parseThreadMessages(connection, threadPages);
		
		return this.thread;
	}

	private void parseThreadTitle(ScrapperUrlConnector connection) {
		Document doc = connection.getDocument();
		
		Elements titleXml = doc.selectXpath(THREAD_TITLE_XPATH);
		
		String title = titleXml.text();
		
		this.thread.setThreadTitle(title);		
	}

	private List<URL> parsePaginator(ScrapperUrlConnector connection) {
		// TODO Auto-generated method stub
		return null;
	}

	private void parseThreadMessages(ScrapperUrlConnector connection, List<URL> threadPages) {
		Document doc = connection.getDocument();
		
		Elements messagesXml = doc.selectXpath(THREAD_MESSAGES_XPATH);
		
		parseThreadFirstMessage(messagesXml.first());
		
		messagesXml.remove(0);
		
		Elements additionalMessages = threadPages.stream()
				.map(ThreadScrapper::getMessagesFromSubpages)
				.flatMap(Elements::stream)
				.collect(Elements::new, Elements::add, Elements::addAll);
		
		messagesXml.addAll(additionalMessages);
		
		messagesXml.stream()
				.forEach(this::parseMessage);
		
	}
	
	private void parseThreadFirstMessage(Element firstMessage) {
		// TODO Auto-generated method stub
		
	}

	private static Elements getMessagesFromSubpages(URL url) {
		ScrapperUrlConnector subpageConnection;
		try {
			subpageConnection = ScrappingUtils.connectToForum(url);
		} catch (ForumScrapperException e) {
			logger.error(String.format("Error while accesing URL: %s", url), e);
			return new Elements();
		}
		
		Document doc = subpageConnection.getDocument();
		
		return doc.selectXpath(THREAD_MESSAGES_XPATH);
	}
	
	private void parseMessage(Element messageXml) {
		//TODO: Create method
	}
}
