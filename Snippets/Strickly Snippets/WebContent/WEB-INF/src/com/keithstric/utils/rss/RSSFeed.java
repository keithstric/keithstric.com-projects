package com.keithstric.utils.rss;

/*
 * This is an RSS Feed. Used primarily for the RSS Import functionality. This uses
 * RSSComment and RSSPost classes
 */

import java.util.ArrayList;
import java.util.List;

public class RSSFeed {

	private String title;
	private String link;
	private String description;
	private String language;
	private String copyright;
	private String pubDate;
	private String lastBuildDate;
	private List<String> categories;
	private List<AbstractRSSItem> entries = new ArrayList<AbstractRSSItem>();
	private List<RSSComment> comments = new ArrayList<RSSComment>();
	private List<RSSImage> images = new ArrayList<RSSImage>();
	private Boolean forOutgoingFeed = false;

	public RSSFeed(String title, String link, String description, String language, String copyright, String pubDate) {
		setTitle(title);
		setLink(link);
		setDescription(description);
		setLanguage(language);
		setCopyright(copyright);
		setPubDate(pubDate);
	}

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

	public String getDescription() {
		if (isForOutgoingFeed()) {
			return "<description>" + description + "</description>";
		} else {
			return description;
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		if (isForOutgoingFeed()) {
			return "<language>" + language + "</language>";
		} else {
			return language;
		}
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCopyright() {
		if (isForOutgoingFeed()) {
			return "<copyright>" + copyright + "</copyright>";
		} else {
			return copyright;
		}
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
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

	public List<AbstractRSSItem> getEntries() {
		return entries;
	}

	public void setEntries(List<AbstractRSSItem> list) {
		this.entries = list;
	}

	public List<RSSComment> getComments() {
		return comments;
	}

	public void setComments(List<RSSComment> comments) {
		this.comments = comments;
	}

	public List<RSSImage> getImages() {
		return images;
	}

	public void setImages(List<RSSImage> images) {
		this.images = images;
	}

	public String getLastBuildDate() {
		if (isForOutgoingFeed()) {
			return "<lastBuildDate>" + lastBuildDate + "</lastBuildDate>";
		} else {
			return lastBuildDate;
		}
	}

	public void setLastBuildDate(String lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	public List<String> getCategories() {
		if (isForOutgoingFeed()) {
			return categories;
		} else {
			return categories;
		}
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public Boolean isForOutgoingFeed() {
		return forOutgoingFeed;
	}

	public void setForOutgoingFeed(Boolean forOutgoingFeed) {
		this.forOutgoingFeed = forOutgoingFeed;
	}

	@Override
	public String toString() {
		if (isForOutgoingFeed()) {
			//TODO
			return null;
		} else {
			return "Feed [copyright=" + copyright + ", description=" + description + ", language=" + language + ", link=" + link
					+ ", pubDate=" + pubDate + ", title=" + title + "]";
		}
	}
}
