package com.vilia.miarrobawebscrapper.model;

import java.net.URL;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MiarrobaMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long messageId;
	@Column
	private URL messageUrl;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "miarrobauser_id")
	private MiarrobaUser user;
	@Column
	private LocalDateTime postDate;
	@Column
	private String content;
	@Column
	private String contentHTML;

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public URL getMessageUrl() {
		return messageUrl;
	}

	public void setMessageUrl(URL messageUrl) {
		this.messageUrl = messageUrl;
	}

	public MiarrobaUser getUser() {
		return user;
	}

	public void setUser(MiarrobaUser user) {
		this.user = user;
	}

	public LocalDateTime getPostDate() {
		return postDate;
	}

	public void setPostDate(LocalDateTime postDate) {
		this.postDate = postDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentHTML() {
		return contentHTML;
	}

	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}

	@Override
	public String toString() {
		return "MiarrobaMessage [messageId=" + messageId + ", messageUrl=" + messageUrl + ", user=" + user
				+ ", postDate=" + postDate + ", content=" + content + ", contentHTML=" + contentHTML + "]";
	}
	
	

}
