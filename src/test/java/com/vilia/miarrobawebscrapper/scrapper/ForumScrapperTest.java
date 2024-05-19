package com.vilia.miarrobawebscrapper.scrapper;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;

@ExtendWith(MockitoExtension.class)
public class ForumScrapperTest {
	private static final String TEST_URL = "https://vilia.miarroba.com";
	
	private static final String TEST_FORUM_TITLE = "vilia";
	private static final String TEST_FIRST_INNER_FORUM_TITLE = "General";
	
	private ForumScrapper forumScrapper;
	
	public ForumScrapperTest() {
		URL testUrl = null;
		try {
			testUrl = new URL(TEST_URL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		forumScrapper = new ForumScrapper(testUrl, null);
	}
	
	@Test
	public void testForumScrapper() {
		MiarrobaForum forum = null;
		try {
			forum = forumScrapper.parseForum();
		} catch (ForumScrapperException e) {
			Assertions.fail("forumScrapper couldn't be initialized", e);
		}
		
		Assertions.assertEquals(forum.getForumTitle(), TEST_FORUM_TITLE);
		
		//TODO: Test first inner forum title
	}
}
