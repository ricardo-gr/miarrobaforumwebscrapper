package com.vilia.miarrobawebscrapper.model;

import java.net.URL;
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
public class MiarrobaForum {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long forumId;
	@Column
	private URL forumUrl;
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	private MiarrobaForum parentForum;
	@Column
	private String forumTitle;
	@OneToMany(fetch = FetchType.LAZY)
	private List<MiarrobaThread> threads = new ArrayList<>();

	public Long getForumId() {
		return forumId;
	}

	public void setForumId(Long forumId) {
		this.forumId = forumId;
	}

	public URL getForumUrl() {
		return forumUrl;
	}

	public void setForumUrl(URL forumUrl) {
		this.forumUrl = forumUrl;
	}

	public MiarrobaForum getParentForum() {
		return parentForum;
	}

	public void setParentForum(MiarrobaForum parentForum) {
		this.parentForum = parentForum;
	}

	public String getForumTitle() {
		return forumTitle;
	}

	public void setForumTitle(String forumTitle) {
		this.forumTitle = forumTitle;
	}

	public List<MiarrobaThread> getThreads() {
		return threads;
	}

	public void setThreads(List<MiarrobaThread> threads) {
		this.threads = threads;
	}

	public int getForumLevel() {
		if (this.parentForum == null)
			return 1;
		else
			return this.parentForum.getForumLevel() + 1;
	}

	public int getThreadNumber() {
		return this.threads.size();
	}
	
	@Override
	public String toString() {
		return "MiarrobaForum [forumId=" + forumId + ", forumUrl=" + forumUrl + ", parentForum=" + parentForum
				+ ", forumTitle=" + forumTitle + ", threads=" + threads + "]";
	}

}
