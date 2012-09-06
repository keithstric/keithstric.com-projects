package com.keithstric.utils.rss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.keithstric.date.DateFormatter;
import com.keithstric.utils.jsf.JSFUtil;

public class RSSGenerator {

	private String feedType;
	private List<AbstractRSSItem> items;
	private String category;
	private RSSFeed feed;
	private String title;
	private String link;
	private String description;
	private String language;
	private String copyright;
	private String pubDate;
	private String lastBuildDate;

	/**
	 * New RSS Generator for outgoing feed
	 * 
	 * @param category
	 *            - A Tag name, can be null
	 * @param feedType
	 *            - The type of feed, accepts: "posts", "comments", "category"
	 */
	public RSSGenerator(String category, String feedType) {
		if (feedType.equalsIgnoreCase("category")) {
			setCategory(category);
		} else {
			setCategory(null);
		}
		setFeedType(feedType);
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public void generateFeed() {
		try {
			RSSFeed rssFeed = getFeed();
			rssFeed.setEntries(getItems());
			rssFeed.setLastBuildDate(getLastBuildDate());
			rssFeed.setPubDate(getPubDate());
			rssFeed.setCopyright(getCopyright());
			StringBuilder sb = new StringBuilder();
			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><rss version=\"2.0\" ");
			sb.append("xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" " + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" ");
			sb.append("xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\">");
			sb.append("<channel>" + rssFeed.getTitle() + rssFeed.getLink() + rssFeed.getDescription() + rssFeed.getLastBuildDate());
			sb.append("<generator>http://xblog.openntf.org</generator>");
			if (getFeedType().equalsIgnoreCase("category")) {
				sb.append("<category>" + getCategory() + "</category>");
			}
			for (AbstractRSSItem item : rssFeed.getEntries()) {
				sb.append("<item>" + item.getTitle() + item.getLink() + item.getAuthor() + item.getPubDate() + item.getGuid());
				if (!getFeedType().equalsIgnoreCase("comments")) {
					sb.append(item.getComments());
				}
				for (String category : item.getCategory()) {
					sb.append(category);
				}
				sb.append("<description>" + item.getDescription() + "</description>");
				sb.append(item.getContent() + "</item>");
			}
			sb.append("</channel></rss>");
			ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer getRSSCount(ViewNavigator viewNav, boolean isCategory, Integer configCount) {
		int navCount = viewNav.getCount();
		if (navCount < configCount) {
			return navCount;
		} else {
			return configCount;
		}
	}

	public List<AbstractRSSItem> getItems() {
		List<AbstractRSSItem> rssItems = new ArrayList<AbstractRSSItem>();
		try {
			if (items != null) {
				return items;
			} else {
				Database db = JSFUtil.getCurrentDatabase();
				View itemView = null;
				ViewNavigator viewNav = null;
				String category = null;
				boolean isCategory = false;
				if (getFeedType().equalsIgnoreCase("posts")) {
					itemView = db.getView("(homeBlogsByDate)");
					viewNav = itemView.createViewNav();
				} else if (getFeedType().equalsIgnoreCase("comments")) {
					itemView = db.getView("commentsbydate");
					viewNav = itemView.createViewNav();
				} else if (getFeedType().equalsIgnoreCase("category")) {
					isCategory = true;
					category = getCategory();
					itemView = db.getView("(luTags)");
					viewNav = itemView.createViewNavFromCategory(category + "~" + category);
				}
				ViewEntry itemEnt = viewNav.getFirst();
				if (itemEnt != null) {
					Integer rssCount = 15;
					for (int i = 0; i < getRSSCount(viewNav, isCategory, rssCount); i++) {
						AbstractRSSItem item = null;
						if (getFeedType().equalsIgnoreCase("comments") && !itemEnt.isCategory()) {
							//TODO: Fix these field names to use a map instead
							Document parentDoc = db.getDocumentByUNID(itemEnt.getDocument().getItemValueString("ParentUNID"));
							if (parentDoc.getItemValueString("publish").equalsIgnoreCase("Yes")) {
								item = new RSSComment();
								item.setAuthor(itemEnt.getDocument().getItemValueString("Name"));
								item.setTitle(itemEnt.getDocument().getItemValueString("Subject"));
								item.setContentText(getContent(itemEnt.getDocument(), "Comment"));
								item.setLink(JSFUtil.getDbUrl() + "/default.xsp?documentId="
										+ itemEnt.getDocument().getItemValueString("parentUNID") + "#comments");
								String commentNum = ((Integer) itemEnt.getDocument().getItemValueInteger("CommentNumber")).toString();
								item.setGuid(JSFUtil.getDbUrl() + "/default.xsp?documentId="
										+ itemEnt.getDocument().getItemValueString("parentUNID") + "#" + commentNum);
								item.setForOutgoingFeed(true);
								item.setPubDate(getItemPubDate(itemEnt.getDocument()));
								rssItems.add(item);
							}
						} else if (!getFeedType().equalsIgnoreCase("comments") && !itemEnt.isCategory()) {
							item = new RSSPost();
							item.setAuthor(itemEnt.getDocument().getItemValueString("author"));
							item.setTitle(itemEnt.getDocument().getItemValueString("title"));
							item.setContent(getContent(itemEnt.getDocument(), "postContent"));
							//TODO - Fix below commented out if statement
							/*-if (XBlogUtils.getCurrentInstance().getConfigValue("RSSIncludeReadMore", 0).toString().equalsIgnoreCase("Yes")) {
								item.setContentText(item.getContentText() + "<br /><br />"
										+ getContent(itemEnt.getDocument(), "ReadMoreContent"));
							}*/
							item.setCategory(getCategories(itemEnt.getDocument()));
							item.setComments(JSFUtil.getDbUrl() + "/default.xsp?documentId="
									+ itemEnt.getDocument().getUniversalID() + "#comments");
							item.setDescription(getDescription(itemEnt.getDocument(), "postContent"));
							item.setLink(JSFUtil.getDbUrl() + "/default.xsp?documentId="
									+ itemEnt.getDocument().getUniversalID());
							item.setGuid(JSFUtil.getDbUrl() + "/default.xsp?documentId="
									+ itemEnt.getDocument().getUniversalID());
							item.setForOutgoingFeed(true);
							item.setPubDate(getItemPubDate(itemEnt.getDocument()));
							rssItems.add(item);
						} else if (itemEnt.isCategory() && itemEnt.getColumnValues().get(0).toString().equalsIgnoreCase(category)) {
							break;
						}

						ViewEntry prevEnt = itemEnt;
						itemEnt = viewNav.getNext(itemEnt);
						prevEnt.recycle();
					}
					setItems(rssItems);
				}
			}
		} catch (NotesException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rssItems;
	}

	public void setItems(List<AbstractRSSItem> items) {
		this.items = items;
	}

	private List<String> getCategories(Document doc) {
		List<String> tags = new ArrayList<String>();
		try {
			for (Object tag : doc.getItemValue("Tags")) {
				String categoryStr = tag.toString();
				tags.add(categoryStr);
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return tags;
	}

	private String getContent(Document doc, String itemName) {
		try {
			Item curItem = doc.getFirstItem(itemName);
			String itemValue = null;
			if ((curItem.getType() == Item.MIME_PART || curItem.getType() == Item.HTML || curItem.getType() == Item.RICHTEXT)) {
				MIMEEntity mime = curItem.getMIMEEntity();
				if (mime != null) {
					itemValue = curItem.getMIMEEntity().getContentAsText();
				} else {
					itemValue = curItem.getText();
				}
			} else {
				itemValue = curItem.getValueString();
			}
			return itemValue;
		} catch (NotesException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getDescription(Document doc, String itemName) {
		String itemValue = null;
		try {
			Item curItem = doc.getFirstItem(itemName);
			//TODO - Fix below commented out line
			//Integer summaryLen = ((Double) XBlogUtils.getCurrentInstance().getConfigValue("RSSSummaryLength", 0)).intValue();
			Integer summaryLen = 0;
			itemValue = curItem.getMIMEEntity().getContentAsText();
			if (summaryLen > itemValue.length()) {
				itemValue = itemValue.substring(0, summaryLen);
			} else {
				itemValue = itemValue.substring(0, itemValue.length() - 25);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemValue;
	}

	private String getItemPubDate(Document doc) {
		try {
			DateTime pubDateTime = NotesContext.getCurrent().getCurrentSession().createDateTime(
					doc.getItemValue("CreatedDate").get(0).toString());
			Date pubDate = pubDateTime.toJavaDate();
			return formatRSSDate(pubDate);
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public RSSFeed getFeed() {
		if (feed != null) {
			return feed;
		} else {
			RSSFeed feed = new RSSFeed(getTitle(), getLink(), getDescription(), getLanguage(), getCopyright(), getPubDate());
			feed.setForOutgoingFeed(true);
			return feed;

		}
	}

	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

	public String getTitle() {
		if (title != null) {
			return title;
		} else {
			//TODO - Fix below commented out line
			//return XBlogUtils.getCurrentInstance().getConfigValue("BlogName", 0).toString();
			return null;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		if (link != null) {
			return link;
		} else {
			return JSFUtil.getDbUrl();
		}
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		if (description != null) {
			return description;
		} else {
			//TODO - Fix below commented out line
			//return XBlogUtils.getCurrentInstance().getConfigValue("BlogDescription", 0).toString();
			return null;
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		if (language != null) {
			return language;
		} else {
			//TODO - Fix below commented out line
			//return XBlogUtils.getCurrentInstance().getConfigValue("Language", 0).toString();
			return null;
		}
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getPubDate() {
		if (pubDate != null) {
			return pubDate;
		} else {
			if (items != null && items.size() > 0) {
				return getItems().get(0).getPubDate();
			} else {
				return new Date().toString();
			}
		}
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getLastBuildDate() {
		if (lastBuildDate != null) {
			return lastBuildDate;
		} else {
			List<AbstractRSSItem> items = getItems();
			if (items != null && items.size() > 0) {
				String pubDate = getItems().get(0).getPubDate();
				return pubDate.substring(pubDate.indexOf("<pubDate>") + 9, pubDate.indexOf("</pubDate>"));
			} else {
				return "";
			}
		}
	}

	public void setLastBuildDate(String lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}
	
	/**
	 * Format a date
	 * 
	 * @param Date
	 *            - the date we're working with
	 * @return java.lang.String of the formatted date
	 */
	public static String formatRSSDate(Date formatDate) {
		try {
			String dateFormatStr = "EEE, dd MMM yyyy HH:mm:ss Z";
			return DateFormatter.getFormattedDate(dateFormatStr, formatDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
