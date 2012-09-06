package com.keithstric.utils.rss;

/*
 * This is an RSSComment with all the pertinent values. Used for the RSSImport functionality
 */

public class RSSComment extends AbstractRSSItem {

	private String content;

	public RSSComment() {

	}

	@Override
	public String getContent() {
		if (content != null) {
			return content;
		} else {
			if (isForOutgoingFeed()) {
				return "<content:encoded><![CDATA[" + getContentText() + "]]></content:encoded>";
			} else {
				return getContentText();
			}
		}
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}
}
