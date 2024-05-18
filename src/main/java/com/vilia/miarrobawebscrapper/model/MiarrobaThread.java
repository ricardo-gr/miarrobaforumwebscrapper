package com.vilia.miarrobawebscrapper.model;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class MiarrobaThread {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long threadId;
	@Column
	private URL threadUrl;
	@Column(nullable = false)
	private String threadTitle;
	@OneToOne(fetch = FetchType.EAGER)
	private MiarrobaMessage startingMessage;
	@OneToMany(fetch = FetchType.EAGER)
	private List<MiarrobaMessage> comments = new ArrayList<>();

	public Long getThreadId() {
		return threadId;
	}

	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}

	public URL getThreadUrl() {
		return threadUrl;
	}

	public void setThreadUrl(URL threadUrl) {
		this.threadUrl = threadUrl;
	}

	public String getThreadTitle() {
		return threadTitle;
	}

	public void setThreadTitle(String threadTitle) {
		this.threadTitle = threadTitle;
	}

	public MiarrobaMessage getStartingMessage() {
		return startingMessage;
	}

	public void setStartingMessage(MiarrobaMessage startingMessage) {
		this.startingMessage = startingMessage;
	}

	public List<MiarrobaMessage> getComments() {
		return comments;
	}

	public void setComments(List<MiarrobaMessage> comments) {
		this.comments = comments;
	}
	
	public int getNumberMessages() {
		return this.comments.size() + 1;
	}
	
	public MiarrobaUser getInitiatorUser() {
		return this.startingMessage.getUser();
	}
	
	public LocalDateTime getCreationDate() {
		return this.startingMessage.getPostDate();
	}

	@Override
	public String toString() {
		return "MiarrobaThread [threadId=" + threadId + ", threadUrl=" + threadUrl + ", threadTitle=" + threadTitle
				+ ", startingMessage=" + startingMessage + ", comments=" + comments + "]";
	}

	
	
}
