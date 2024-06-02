package com.vilia.miarrobawebscrapper.scrapper;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;
import com.vilia.miarrobawebscrapper.model.MiarrobaThread;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.forumscrapper.ForumScrapperFactory;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ForumScrapperTest {
	private static Logger logger = LoggerFactory.getLogger(ForumScrapperTest.class);
	
	private static final String TEST_URL = "https://vilia.mforos.com";
	private static final String TEST_GENERAL_FORUM_URL = "/968735-general";
	private static final String TEST_REGLAS_FORUM_URL = "/1910438-reglas/";
	
	private static final String TEST_FORUM_TITLE = "vilia";
	private static final String TEST_GENERAL_FORUM_TITLE = "General";
	private static final String TEST_REGLAS_FORUM_TITLE = "Reglas";
	
	private static final int TEST_NUMBER_FORUMS = 59;
	private static final int TEST_NUMBER_THREADS_GENERAL = 37;
	private static final int TEST_NUMBER_THREADS_REGLAS = 9;
	
	private static final String TEST_FIRST_THREAD_GENERAL_TITLE = "Fichas de Personaje";
	private static final String TEST_FIRST_THREAD_GENERAL_HREF = "https://vilia.mforos.com/968735/9479980-fichas-de-personaje/";
	private static final String TEST_FIRST_THREAD_REGLAS_TITLE = "Pericias en no-armas";
	private static final String TEST_FIRST_THREAD_REGLAS_HREF = "https://vilia.mforos.com/1910438/10886952-pericias-en-no-armas/";
	
	private static MiarrobaForum rootForum;
	
	@BeforeClass
	public static void initializeRootForum() {
		rootForum = scrapForumUrl(TEST_URL, null);
	}
	
	@Test
	public void testParentForumScrapper() {
		MiarrobaForum parentForum = null;
		
		MiarrobaForum forum = scrapForumUrl(TEST_URL, parentForum);
		
		Assertions.assertEquals(TEST_FORUM_TITLE, forum.getForumTitle());
	}

	private static MiarrobaForum scrapForumUrl(String url, MiarrobaForum parent) {
		ForumScrapper forumScrapper = null;
		
		if (parent == null)
			forumScrapper = initRootForumScrapper(url);
		else
			forumScrapper = initSubForumScrapper(url, parent);
		
		if(forumScrapper == null)
			return new MiarrobaForum();
		
		MiarrobaForum forum = scrapForum(forumScrapper);
		
		return forum;
	}
	
	private static ForumScrapper initRootForumScrapper(String urlString) {
		URL testUrl = getURL(urlString);
		
		try {
			return ForumScrapperFactory.getRootForumScrapper(testUrl);
		} catch (ForumScrapperException e) {
			logger.error(String.format("Test URL does not belong to a Root Forum: %s", urlString));
			return null;
		}
	}
	
	private static ForumScrapper initSubForumScrapper(String urlString, MiarrobaForum parent) {
		URL testUrl = getURL(urlString);
		
		try {
			return ForumScrapperFactory.getSubForumScrapper(testUrl, parent);
		} catch (ForumScrapperException e) {
			logger.error(String.format("Test URL does not belong to a SubForum: %s", urlString));
			return null;
		}
	}
	
	private static URL getURL(String urlString) {
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private static MiarrobaForum scrapForum(ForumScrapper forumScrapper) {
		MiarrobaForum forum = null;
		try {
			forum = forumScrapper.parseForum();
		} catch (ForumScrapperException e) {
			Assertions.fail("forumScrapper couldn't be initialized", e);
		}
		
		return forum;
	}
	
	@Test
	public void testPaginatedSubForumScrapper() {
		String subForumURL = TEST_URL + TEST_GENERAL_FORUM_URL;
		
		MiarrobaForum forum = scrapForumUrl(subForumURL, this.rootForum);
		
		Assertions.assertEquals(TEST_GENERAL_FORUM_TITLE, forum.getForumTitle(), "General subforum Title is not scrapped correctly");
		
		Assertions.assertEquals(TEST_NUMBER_THREADS_GENERAL, forum.getThreadNumber(), 
				String.format("All threads in General subforum are not scrapped correctly: %d subforums instead of %d", forum.getThreadNumber(), TEST_NUMBER_THREADS_GENERAL));
		
		MiarrobaThread firstThread = forum.getThreads().get(0);
		
		Assertions.assertNotNull(firstThread, String.format("No threads were parsed in the %s subforum", TEST_GENERAL_FORUM_TITLE));
		
		if(firstThread != null) {
			Assertions.assertEquals(TEST_FIRST_THREAD_GENERAL_TITLE, firstThread.getThreadTitle(), 
					String.format("First thread title in %s subforum is inccorrect. Expected: %s, scrapped: %s", TEST_GENERAL_FORUM_TITLE, TEST_FIRST_THREAD_GENERAL_TITLE, firstThread.getThreadTitle()));
			
			Assertions.assertEquals(TEST_FIRST_THREAD_GENERAL_HREF, firstThread.getThreadUrl().toString(), 
					String.format("First thread url in %s subforum is inccorrect. Expected: %s, scrapped: %s", TEST_GENERAL_FORUM_TITLE, TEST_FIRST_THREAD_GENERAL_HREF, firstThread.getThreadUrl().toString()));
		}
		
	}
	
	@Test
	public void testNonPaginatedForumScrapper() {
		String subForumURL = TEST_URL + TEST_REGLAS_FORUM_URL;
		
		MiarrobaForum forum = scrapForumUrl(subForumURL, this.rootForum);
		
		Assertions.assertEquals(TEST_REGLAS_FORUM_TITLE, forum.getForumTitle(), "Reglas subforum Title is not scrapped correctly");
		
		Assertions.assertEquals(TEST_NUMBER_THREADS_REGLAS, forum.getThreadNumber(), 
				String.format("All threads in Reglas subforum are not scrapped correctly: %d subforums instead of %d", forum.getThreadNumber(), TEST_NUMBER_THREADS_REGLAS));
		
		MiarrobaThread firstThread = forum.getThreads().get(0);
		
		Assertions.assertNotNull(firstThread, String.format("No threads were parsed in the %s subforum", TEST_REGLAS_FORUM_TITLE));
		
		if(firstThread != null) {
			Assertions.assertEquals(TEST_FIRST_THREAD_REGLAS_TITLE, firstThread.getThreadTitle(), 
					String.format("First thread title in %s subforum is inccorrect. Expected: %s, scrapped: %s", TEST_REGLAS_FORUM_TITLE, TEST_FIRST_THREAD_REGLAS_TITLE, firstThread.getThreadTitle()));
			
			Assertions.assertEquals(TEST_FIRST_THREAD_REGLAS_HREF, firstThread.getThreadUrl().toString(), 
					String.format("First thread url in %s subforum is inccorrect. Expected: %s, scrapped: %s", TEST_REGLAS_FORUM_TITLE, TEST_FIRST_THREAD_REGLAS_HREF, firstThread.getThreadUrl().toString()));
		}
		
		
	}
}
