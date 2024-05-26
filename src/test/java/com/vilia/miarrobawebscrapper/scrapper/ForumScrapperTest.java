package com.vilia.miarrobawebscrapper.scrapper;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ForumScrapperTest {
	private static final String TEST_URL = "https://vilia.mforos.com";
	private static final String TEST_FIRST_FORUM_URL = "/968735-general";
	
	private static final String TEST_FORUM_TITLE = "vilia";
	private static final String TEST_FIRST_INNER_FORUM_TITLE = "General";
	
	@Test
	public void testParentForumScrapper() {
		ForumScrapper forumScrapper = initForumScrapper(TEST_URL);
		
		MiarrobaForum forum = scrapForum(forumScrapper);
		
		Assertions.assertEquals(forum.getForumTitle(), TEST_FORUM_TITLE);
	}
	
	private ForumScrapper initForumScrapper(String urlString) {
		URL testUrl = null;
		try {
			testUrl = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ForumScrapper(testUrl, null);
	}
	
	private MiarrobaForum scrapForum(ForumScrapper forumScrapper) {
		MiarrobaForum forum = null;
		try {
			forum = forumScrapper.parseForum();
		} catch (ForumScrapperException e) {
			Assertions.fail("forumScrapper couldn't be initialized", e);
		}
		
		return forum;
	}
	
	@Test
	public void testInnerForumScrapper() {
		String initialForumURL = TEST_URL + TEST_FIRST_FORUM_URL;
		
		ForumScrapper forumScrapper = initForumScrapper(initialForumURL);
		
		MiarrobaForum forum = scrapForum(forumScrapper);
		
		Assertions.assertEquals(forum.getForumTitle(), TEST_FIRST_INNER_FORUM_TITLE);
		
	}
}
