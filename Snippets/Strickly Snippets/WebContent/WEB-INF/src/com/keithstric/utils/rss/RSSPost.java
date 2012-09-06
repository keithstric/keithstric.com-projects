package com.keithstric.utils.rss;

/*
 * The RSSPost class used primarily for the RSSImport
 */

import java.util.ArrayList;
import java.util.List;

public class RSSPost extends AbstractRSSItem {
	private String title;
	private String description;
	private String link;
	private String author;
	private String guid;
	private String content;
	private List<String> category;
	private String pubdate;
	private String comments;
	private String contentText;
	private List<RSSImage> images;

	public RSSPost() {

	}

	@Override
	public String toString() {
		return "FeedMessage [title=" + title + ", description=" + description + ", link=" + link + ", author=" + author + ", guid=" + guid
				+ "content=" + content + ", pubdate=" + pubdate + ", category=" + category + "]";
	}

	@Override
	public String getContent() {
		if (isForOutgoingFeed()) {
			if (content != null) {
				return "<content:encoded><![CDATA[" + content + "]]></content:encoded>";
			} else {
				return "<content:encoded><![CDATA[" + getContentText() + "]]></content:encoded>";
			}
		} else {
			return getContentText();
		}
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	public List<RSSImage> getImages() {
		if (images != null) {
			return images;
		} else {
			return new ArrayList<RSSImage>();
		}
	}

	public void setImages(List<RSSImage> images) {
		this.images = images;
	}

}
