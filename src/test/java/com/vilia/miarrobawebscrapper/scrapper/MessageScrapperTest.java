package com.vilia.miarrobawebscrapper.scrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

import com.vilia.miarrobawebscrapper.model.MiarrobaMessage;
import com.vilia.miarrobawebscrapper.scrapper.exception.ForumScrapperException;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrapperUrlConnector;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrappingUtils;
import com.vilia.miarrobawebscrapper.support.MiarrobaForumTestHelper;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class MessageScrapperTest {
	private static Logger logger = LoggerFactory.getLogger(MessageScrapperTest.class);

	private static final String THREAD_1_URL = MiarrobaForumTestHelper.TEST_URL + "/968735/11237325-hola-a-todos/";
	private static final String THREAD_2_URL = MiarrobaForumTestHelper.TEST_URL
			+ "/968735/9479980-fichas-de-personaje/";
	
	private static MiarrobaMessage thread1Message1;
	private static final Long thread1Message1Id = 109361098l;
	private static final String thread1Message1Url = "https://vilia.mforos.com/968735/11237325-hola-a-todos/#109361098";
	private static final String thread1Message1Username = "Streea_Naukhel";
	private static final LocalDateTime thread1Message1PostDate = LocalDateTime.parse("2013-12-29T01:26:02+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	private static final String thread1Message1ContentSnip = "mañana nos veremos en Terra.. jeje";
	private static final String thread1Message1HtmlContentSnip = "mañana nos veremos en&nbsp;Terra.. jeje<br>Buenas noches a todos!!";
	
	private static MiarrobaMessage thread1Message2;
	private static final Long thread1Message2Id = 109416719l;
	private static final String thread1Message2Url = "https://vilia.mforos.com/968735/11237325-hola-a-todos/#109416719";
	private static final String thread1Message2Username = "ErizoSevilla86-330";
	private static final LocalDateTime thread1Message2PostDate = LocalDateTime.parse("2014-01-07T23:59:19+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	private static final String thread1Message2ContentSnip = "Espero que te sientas aquí como en casa!";
	private static final String thread1Message2HtmlContentSnip = "Espero que te sientas aquí como en casa!</p><p>El foro no se mueve mucho ultimamente";
	
	private static MiarrobaMessage thread2Message1;
	private static final Long thread2Message1Id = 91475960l;
	private static final String thread2Message1Url = "https://vilia.mforos.com/968735/9479980-fichas-de-personaje/#91475960";
	private static final String thread2Message1Username = "ErizoSevilla86-330";
	private static final LocalDateTime thread2Message1PostDate = LocalDateTime.parse("2010-09-06T09:42:49+02:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	private static final String thread2Message1ContentSnip = "los campos con fondo azul son campos precalculados";
	private static final String thread2Message1HtmlContentSnip = "<p>Buenas, gente ^^ Tal y como os prometí";
	
	private static MiarrobaMessage thread2Message2;
	private static final Long thread2Message2Id = 91476065l;
	private static final String thread2Message2Url = "https://vilia.mforos.com/968735/9479980-fichas-de-personaje/#91476065";
	private static final String thread2Message2Username = "ErizoSevilla86-330";
	private static final LocalDateTime thread2Message2PostDate = LocalDateTime.parse("2010-09-06T09:53:19+02:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	private static final String thread2Message2ContentSnip = "la existencia del canal RSS general del foro";
	private static final String thread2Message2HtmlContentSnip = "<p>Aprovecho para recordaros la existencia del canal RSS general del foro";

	@BeforeClass
	private static void initializeMessages() throws Exception {
		Elements content1, content2;
		
		content1 = parseThreadMessages(THREAD_1_URL);
		content2 = parseThreadMessages(THREAD_2_URL);
		
		thread1Message1 = parseMessage(content1.get(0), THREAD_1_URL);
		thread1Message2 = parseMessage(content1.get(1), THREAD_1_URL);
		thread2Message1 = parseMessage(content2.get(0), THREAD_2_URL);
		thread2Message2 = parseMessage(content2.get(1), THREAD_2_URL);
	}
	
	private static Elements parseThreadMessages(String url) throws Exception {
		URL threadUrl = createURL(url);
		
		ScrapperUrlConnector connector;
		try {
			connector = ScrappingUtils.connectToForum(threadUrl);
		} catch (ForumScrapperException e) {
			logger.error(String.format("Connection could not bet done to test threads: %s, %s", THREAD_1_URL, THREAD_2_URL));
			e.printStackTrace();
			throw new Exception(e);
		}
		
		Document doc = connector.getDocument();
		return doc.selectXpath(ThreadScrapper.THREAD_MESSAGES_XPATH);
	}
	
	private static URL createURL(String url) throws Exception {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			logger.error(String.format("Thread URL could not be initialized: %s, %s", THREAD_1_URL, THREAD_2_URL));
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	private static MiarrobaMessage parseMessage(Element messageXml, String url) throws Exception {
		MessageScrapper scrapper = new MessageScrapper(messageXml, createURL(url));
		
		if (!scrapper.isMessageReady()) {
			logger.error(String.format("Message %s could not be parsed: %s", messageXml.id(), url.toString()));
			throw new Exception();
		}
		
		return scrapper.parseMessage();
	}
	
	@Test
	public void testScrappedMessageId() {
		Assertions.assertEquals(thread1Message1Id, thread1Message1.getMessageId(), 
				String.format("Thread 1 Message 1 ID scrapping failure. Expected: %d, Obtained: %d", 
						thread1Message1Id, thread1Message1.getMessageId()));
		
		Assertions.assertEquals(thread1Message2Id, thread1Message2.getMessageId(), 
				String.format("Thread 1 Message 2 ID scrapping failure. Expected: %d, Obtained: %d", 
						thread1Message2Id, thread1Message2.getMessageId()));
		
		Assertions.assertEquals(thread2Message1Id, thread2Message1.getMessageId(), 
				String.format("Thread 2 Message 1 ID scrapping failure. Expected: %d, Obtained: %d", 
						thread2Message1Id, thread2Message1.getMessageId()));
		
		Assertions.assertEquals(thread2Message2Id, thread2Message2.getMessageId(), 
				String.format("Thread 2 Message 2 ID scrapping failure. Expected: %d, Obtained: %d", 
						thread2Message2Id, thread2Message2.getMessageId()));
	}

	@Test
	public void testScrappedMessageUrl() {	
		Assertions.assertEquals(thread1Message1Url, thread1Message1.getMessageUrl().toString(), 
				String.format("Thread 1 Message 1 URL scrapping failure. Expected: %s, Obtained: %s", 
						thread1Message1Url, thread1Message1.getMessageUrl().toString()));
		
		Assertions.assertEquals(thread1Message2Url, thread1Message2.getMessageUrl().toString(), 
				String.format("Thread 1 Message 2 URL scrapping failure. Expected: %s, Obtained: %s", 
						thread1Message2Url, thread1Message2.getMessageUrl().toString()));
		
		Assertions.assertEquals(thread2Message1Url, thread2Message1.getMessageUrl().toString(), 
				String.format("Thread 2 Message 1 URL scrapping failure. Expected: %s, Obtained: %s", 
						thread2Message1Url, thread2Message1.getMessageUrl().toString()));
		
		Assertions.assertEquals(thread2Message2Url, thread2Message2.getMessageUrl().toString(), 
				String.format("Thread 2 Message 2 URL scrapping failure. Expected: %s, Obtained: %s", 
						thread2Message2Url, thread2Message2.getMessageUrl().toString()));
	}
	
	@Test
	public void testScrappedMessageUsername() {
		Assertions.assertEquals(thread1Message1Username, thread1Message1.getUser().getUsername(), 
				String.format("Thread 1 Message 1 Creator Username scrapping failure. Expected: %s, Obtained: %s", 
						thread1Message1Username, thread1Message1.getUser().getUsername()));
		
		Assertions.assertEquals(thread1Message2Username, thread1Message2.getUser().getUsername(), 
				String.format("Thread 1 Message 2 Creator Username scrapping failure. Expected: %s, Obtained: %s", 
						thread1Message2Username, thread1Message2.getUser().getUsername()));

		Assertions.assertEquals(thread2Message1Username, thread2Message1.getUser().getUsername(), 
				String.format("Thread 2 Message 1 Creator Username scrapping failure. Expected: %s, Obtained: %s", 
						thread1Message1Username, thread1Message1.getUser().getUsername()));
		
		Assertions.assertEquals(thread2Message2Username, thread2Message2.getUser().getUsername(), 
				String.format("Thread 2 Message 2 Creator Username scrapping failure. Expected: %s, Obtained: %s", 
						thread2Message2Username, thread2Message2.getUser().getUsername()));
	}

	@Test
	public void testScrappedMessagePostDate() {
		Assertions.assertEquals(thread1Message1PostDate, thread1Message1.getPostDate(), 
				String.format("Thread 1 Message 1 Post Date scrapping failure. Expected: %s, Obtained: %s", 
						thread1Message1PostDate, thread1Message1.getPostDate()));
		
		Assertions.assertEquals(thread1Message2PostDate, thread1Message2.getPostDate(), 
				String.format("Thread 1 Message 2 Post Date scrapping failure. Expected: %s, Obtained: %s", 
						thread1Message2PostDate, thread1Message2.getPostDate()));
		
		Assertions.assertEquals(thread2Message1PostDate, thread2Message1.getPostDate(), 
				String.format("Thread 2 Message 1 Post Date scrapping failure. Expected: %s, Obtained: %s", 
						thread2Message1PostDate, thread2Message1.getPostDate()));
		
		Assertions.assertEquals(thread2Message2PostDate, thread2Message2.getPostDate(), 
				String.format("Thread 2 Message 2 Post Date scrapping failure. Expected: %s, Obtained: %s", 
						thread2Message2PostDate, thread2Message2.getPostDate()));
	}
	
	@Test
	public void testScrappedMessageContentSnip() {
		Assertions.assertTrue(thread1Message1.getContent().contains(thread1Message1ContentSnip), 
				String.format("Thread 1 Message 1 Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread1Message1ContentSnip, thread1Message1.getContent()));
		
		Assertions.assertTrue(thread1Message2.getContent().contains(thread1Message2ContentSnip), 
				String.format("Thread 1 Message 2 Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread1Message2ContentSnip, thread1Message2.getContent()));
		
		Assertions.assertTrue(thread2Message1.getContent().contains(thread2Message1ContentSnip), 
				String.format("Thread 2 Message 1 Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread2Message1ContentSnip, thread2Message1.getContent()));
		
		Assertions.assertTrue(thread2Message2.getContent().contains(thread2Message2ContentSnip), 
				String.format("Thread 2 Message 2 Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread2Message2ContentSnip, thread2Message2.getContent()));
	}
	
	@Test
	public void testScrappedMessageHtmlContentSnip() {
		Assertions.assertTrue(thread1Message1.getContentHTML().contains(thread1Message1HtmlContentSnip), 
				String.format("Thread 1 Message 1 HTML Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread1Message1HtmlContentSnip, thread1Message1.getContentHTML()));
		
		Assertions.assertTrue(thread1Message2.getContentHTML().contains(thread1Message2HtmlContentSnip), 
				String.format("Thread 1 Message 2 HTML Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread1Message2HtmlContentSnip, thread1Message2.getContentHTML()));
		
		Assertions.assertTrue(thread2Message1.getContentHTML().contains(thread2Message1HtmlContentSnip), 
				String.format("Thread 2 Message 1 HTML Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread2Message1HtmlContentSnip, thread2Message1.getContentHTML()));
		
		Assertions.assertTrue(thread2Message2.getContentHTML().contains(thread2Message2HtmlContentSnip), 
				String.format("Thread 2 Message 2 HTML Content scrapping failure. Expected to contain: %s, Obtained: %s", 
						thread2Message2HtmlContentSnip, thread2Message2.getContentHTML()));
	}

}
