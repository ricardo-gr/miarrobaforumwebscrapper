package com.vilia.miarrobawebscrapper.scrapper.support;

import java.net.URL;
import java.util.function.BiFunction;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrapperFunctionalUtils {
	private static Logger logger = LoggerFactory.getLogger(ScrapperFunctionalUtils.class);
	
	private ScrapperFunctionalUtils() {}
	
	public static URL parseHrefAttributeMethod(Element e, URL baseUrl) {
		String pageRelativeUrl = e.attr("href");
		
		URL pageURL = null;
		try {
			pageURL = new URL(baseUrl.toString() + pageURL);
		} catch (Exception ex) {
			logger.error(String.format("Error while parsing paginator URL. Formed URL is not compliant: %s", pageRelativeUrl), ex);
		}
		
		return pageURL;
	};

}
