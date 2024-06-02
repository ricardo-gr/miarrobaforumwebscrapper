package com.vilia.miarrobawebscrapper.scrapper.forumscrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.vilia.miarrobawebscrapper.scrapper.ForumScrapper;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrappingUtils;

public class RootForumScrapper extends ForumScrapper {
	private static Logger logger = LoggerFactory.getLogger(RootForumScrapper.class);
	
	
	private static final String SUBFORUM_ENTRIES_XPATH = ScrappingUtils.ROOT_FORUM_CONTENT_SECTION_XPATH
			+ "/table[not(@id)]/tbody/tr[contains(@class, 'tablaRow')]/td[contains(@class, 'ancho100')]/div/a[1]";
	private static final String SUBFORUM_ENTRY_XPATH = "/td/div/a[1]/@href";
	
	private List<URL> subForumURLs;
	
	public RootForumScrapper(URL forumUrl) {
		super(forumUrl);
		
		subForumURLs = new ArrayList<>();
	}
	
	protected void parseForumTitle(ScrapperUrlConnector connection) throws ForumScrapperException {
		Pattern p = Pattern.compile("http[s]?://(\\w+)\\.mforos\\.com");
		Matcher m = p.matcher(this.getForumUrl().toString());

		if (!m.find())
			throw new ForumScrapperException("Title could not be parsed from URL", this.getForumUrl(), "unknown");

		String forumTitle = m.group(1);

		this.forum.setForumTitle(forumTitle);
	}
	
	protected void parseForumContent(ScrapperUrlConnector connection) throws ForumScrapperException {
		Document doc = connection.getDocument();

		Elements subforumsXml = doc.selectXpath(SUBFORUM_ENTRIES_XPATH);

		for (Element subforumXml : subforumsXml) {
			String subforumAnchor = subforumXml.attr("href");
			
			if (subforumAnchor == null || ObjectUtils.isEmpty(subforumAnchor))
				continue;

			URL subformUrl = null;

			try {
				subformUrl = new URL(this.getForumUrl() + subforumAnchor);
			} catch (MalformedURLException e) {
				logger.error(
						String.format("Error while parsing subforum. No URL could be created: %s", subforumAnchor));
				continue;
			}

			this.subForumURLs.add(subformUrl);
		}

		// TODO: Save forum to DB

		//TODO: Manage retries and test execution logic vs test execution logic
		/*this.subForumURLs.stream().map(url -> new ForumScrapper(url, this.forum)).forEach((forum) -> {
			try {
				forum.parseForum();
			} catch (ForumScrapperException e) {
				logger.error(String.format("Error while parsing subforum URL: %s. Parsing for this subform stopped.",
						forum.getForumUrl().toString()), e);
			}
		});*/
	}
}
