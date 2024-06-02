package com.vilia.miarrobawebscrapper.scrapper.support;

import java.net.URL;
import java.util.function.BiFunction;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;

public class ScrappingUtils {
	private static Logger logger = LoggerFactory.getLogger(ScrappingUtils.class);
	
	public static final String ROOT_FORUM_CONTENT_SECTION_XPATH = "/html/body/div[@id='SectionComu']";
	public static final String SUBFORUM_CONTENT_SECTION_XPATH = "/html/body/div[@id='SectionForo']";
	
	private ScrappingUtils() {}
	
	public static URL parseHrefAttributeMethod(Element e, URL baseUrl) {
		String pageRelativeUrl = e.attr("href");
		
		URL pageURL = null;
		try {
			pageURL = new URL(baseUrl.toString() + pageRelativeUrl);
		} catch (Exception ex) {
			logger.error(String.format("Error while parsing paginator URL. Formed URL is not compliant: %s", pageRelativeUrl), ex);
		}
		
		return pageURL;
	};
	
	public static String getBaseUrl(URL url) {
		String result = url.toString();
		
		if (result.endsWith("/"))
			result = result.substring(0, result.length() - 1);
		
		return result;
	}
	
	public static ScrapperUrlConnector connectToForum(URL url) throws ForumScrapperException {
		ScrapperUrlConnector connection = new ScrapperUrlConnector(url);

		if (!connection.isConnectionStablished()) {
			throw new ForumScrapperException("Connection not stablished", url, "Unknown");
		}

		return connection;
	}

}
