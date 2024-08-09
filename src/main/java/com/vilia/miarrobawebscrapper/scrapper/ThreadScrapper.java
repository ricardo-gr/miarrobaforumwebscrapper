package com.vilia.miarrobawebscrapper.scrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaMessage;
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
	public static final String THREAD_MESSAGES_XPATH = ScrappingUtils.THREAD_CONTENT_SECTION_XPATH
			+ "/table[contains(@id, 'ForoMensaje']";	
	
	private MiarrobaThread thread;
	private URL baseUrl;
	
	public ThreadScrapper(MiarrobaThread thread) {
		this.thread = thread;
		
		initBaseUrl(thread.getThreadUrl());
	}
	
	public ThreadScrapper(URL url) {
		this.thread = new MiarrobaThread();
		this.thread.setThreadUrl(url);
		
		initBaseUrl(url);	
		
		ihitThreadId(url);
	}
	
	private void ihitThreadId(URL url) {
		//We assume the thread URLs are in the form: https://forum.miarriba.com/1234567/XXXXXX-thread-title/
		String urlStr = url.toString().replaceFirst(this.baseUrl.toString() + "/", "");
		
		int firstHiphenPos = urlStr.indexOf('-');
		int firstBar = urlStr.indexOf('/');
		
		String threadIdStr = urlStr.substring(firstBar + 1, firstHiphenPos);
		
		Long threadId = Long.parseLong(threadIdStr);
		
		this.thread.setThreadId(threadId);
	}

	private void initBaseUrl(URL url) {
		String hostUrlStr = url.getProtocol() + "://" + url.getHost();
		
		try {
			this.baseUrl = new URL(hostUrlStr);
		} catch (MalformedURLException e) {
			logger.error(String.format("Couldn't parse baseURL for MiarrobaThread: %s. Continuing with thread base URL", hostUrlStr));
			this.baseUrl = url;
		}
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

	private void parseThreadMessages(ScrapperUrlConnector connection, List<URL> threadPages) throws ForumScrapperException {
		Document doc = connection.getDocument();
		
		Elements messagesXml = doc.selectXpath(THREAD_MESSAGES_XPATH);
		
		parseThreadFirstMessage(messagesXml.first());
		
		messagesXml.remove(0);
		
		Elements additionalMessages = threadPages.stream()
				.map(ThreadScrapper::getMessagesFromSubpages)
				.flatMap(Elements::stream)
				.collect(Elements::new, Elements::add, Elements::addAll);
		
		messagesXml.addAll(additionalMessages);
		
		List <MiarrobaMessage> comments = messagesXml.stream()
				.map(this::parseComment)
				.filter(message -> message != null)
				.collect(Collectors.toList());
		
		this.thread.setComments(comments);
	}
	
	private void parseThreadFirstMessage(Element firstMessageXml) throws ForumScrapperException {
		MiarrobaMessage message = parseMessage(firstMessageXml);
		
		this.thread.setStartingMessage(message);
	}

	private MiarrobaMessage parseMessage(Element firstMessage) throws ForumScrapperException {
		MessageScrapper messageScrapper = new MessageScrapper(firstMessage, this.baseUrl);
		
		if(!messageScrapper.isMessageReady()) {
			throw new ForumScrapperException(this.thread.getThreadUrl(), 
					String.format("First thread message cannot be parsed. Passed XML is not a message"));
		}
		
		MiarrobaMessage message = messageScrapper.parseMessage();
		return message;
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
	
	private MiarrobaMessage parseComment(Element messageXml) {
		MiarrobaMessage message = null;
		try {
			message = parseMessage(messageXml);
		} catch (ForumScrapperException e) {
			logger.error("Error parsing comment", e);
			return null;
		}
		
		return message;
	}
}
