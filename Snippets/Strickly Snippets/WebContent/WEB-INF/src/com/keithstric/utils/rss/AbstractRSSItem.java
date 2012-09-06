package com.keithstric.utils.rss;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractRSSItem {

	private String title;
	private String link;
	private String author;
	private String pubDate;
	private String guid;
	private String description;
	private List<String> category;
	private String comments;
	private String contentText;
	private boolean forOutgoingFeed = false;

	public String getTitle() {
		if (isForOutgoingFeed()) {
			return "<title>" + title + "</title>";
		} else {
			return title;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		if (isForOutgoingFeed()) {
			return "<link>" + link + "</link>";
		} else {
			return link;
		}
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAuthor() {
		if (isForOutgoingFeed()) {
			return "<dc:creator>" + author + "</dc:creator>";
		} else {
			return author;
		}
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPubDate() {
		if (isForOutgoingFeed()) {
			return "<pubDate>" + pubDate + "</pubDate>";
		} else {
			return pubDate;
		}

	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getGuid() {
		if (isForOutgoingFeed()) {
			return "<guid>" + guid + "</guid>";
		} else {
			return guid;
		}
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getDescription() {
		if (isForOutgoingFeed()) {
			return "<description><![CDATA[" + description + "]]></description>";
		} else {
			return description;
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isForOutgoingFeed() {
		return forOutgoingFeed;
	}

	public void setForOutgoingFeed(boolean forOutgoingFeed) {
		this.forOutgoingFeed = forOutgoingFeed;
	}

	public List<String> getCategory() {
		if (isForOutgoingFeed()) {
			if (category == null) {
				setCategory(new ArrayList<String>());
			} else {
				for (int i = 0; i < category.size(); i++) {
					category.set(i, "<category>" + category.get(i) + "</category>");
				}
			}
		}
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public String getComments() {
		if (isForOutgoingFeed()) {
			return "<comments>" + comments + "</comments>";
		} else {
			return comments;
		}
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	abstract String getContent();

	abstract void setContent(String content);
}
