package com.vilia.miarrobawebscrapper.scrapper;

import java.net.URL;
import java.time.LocalDateTime;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vilia.miarrobawebscrapper.model.MiarrobaMessage;
import com.vilia.miarrobawebscrapper.scrapper.support.ScrappingUtils;

public class MessageScrapper {
	private static Logger logger = LoggerFactory.getLogger(MessageScrapper.class);
	
	protected static final String AUTHOR_COLUMN_XPATH = "/table/tbody/tr/td[@class='tdAutorMensajes']/div";
	
	protected static final String MESSAGE_COLUMN_XPATH = "/table/tbody/tr/td[@class='celdaMsg']/div";
	protected static final String MESSAGE_HEADER_XPATH = MESSAGE_COLUMN_XPATH + "/div/div[contains(@class, 'viewTopicHeader')]";
	protected static final String MESSAGE_URL_XPATH = MESSAGE_HEADER_XPATH + "/div/a";
	protected static final String MESSAGE_DATE_XPATH = MESSAGE_HEADER_XPATH + "/div/span[1]/time";
	protected static final String MESSAGE_CONTENT_XPATH = MESSAGE_COLUMN_XPATH + "/div/div[contains(@id, 'e_msg_')]";
	
	private Element messageXml;
	private URL baseUrl;
	private MiarrobaMessage message;
	
	public MessageScrapper(Element messageXml, URL baseUrl) {
		if (verifyIsMessage(messageXml))
			this.messageXml = messageXml;
		else
			this.messageXml = null;
		
		this.baseUrl = baseUrl;
		
		this.message = new MiarrobaMessage();
	}

	protected boolean verifyIsMessage(Element messageXml) {
		return messageXml.hasAttr("id") && messageXml.attr("id").contains("ForoMensaje");
	}
	
	public boolean isMessageReady() {
		return !(messageXml == null);
	}
	
	public MiarrobaMessage parseMessage() {
		parseMessageId();
		
		parseMessageUrl();
		
		parseMessageUser();
		
		parseMessageDateTime();
		
		parseMessageContent();
		
		return this.message;
	}

	protected void parseMessageId() {
		//All 'id' attributes are in the form "ForoMensaje-XXXXXXX"
		String messageIdXml = messageXml.attr("id");
		
		int hiphenPosition = messageIdXml.indexOf('-');
		
		String messageIdString = messageIdXml.substring(hiphenPosition + 1);
		
		Long messageId = Long.parseLong(messageIdString);
		
		this.message.setMessageId(messageId);		
	}

	protected void parseMessageUrl() {
		Element urlXml = messageXml.selectXpath(MESSAGE_URL_XPATH).first();
		
		if (urlXml == null) {
			logger.error(String.format("Could not find URL for message ID: %d", message.getMessageId()));
			return;
		}
		
		URL url = ScrappingUtils.parseHrefAttributeMethod(urlXml, this.baseUrl);
		
		this.message.setMessageUrl(url);
	}

	protected void parseMessageUser() {
		// TODO Auto-generated method stub
		
	}

	protected void parseMessageDateTime() {
		Element dateXml = messageXml.selectXpath(MESSAGE_DATE_XPATH).first();
		
		String dateTimeString = dateXml.attr("datetime");
		
		LocalDateTime dateTime = ScrappingUtils.parseDateTime(dateTimeString);
		
		this.message.setPostDate(dateTime);		
	}

	protected void parseMessageContent() {
		Element contentXml = messageXml.selectXpath(MESSAGE_CONTENT_XPATH).first();
		
		String html = contentXml.html();
		
		String content = contentXml.wholeText();
		
		this.message.setContent(content);
		this.message.setContentHTML(html);
	}
	
	

}
