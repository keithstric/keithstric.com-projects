package com.keithstric.utils.rss;

/*
 * This class goes out and gets the feed items and populates the RSSFeed class with
 * RSSPost and RSSComment items
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class RSSParser {
	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String CHANNEL = "channel";
	static final String LANGUAGE = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK = "link";
	static final String ATOMLINK = "atom:link";
	static final String AUTHOR = "creator";
	static final String BLOGAUTHOR = "author";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";
	static final String GUID = "guid";
	static final String CONTENT = "encoded";
	static final String CATEGORY = "category";
	static final String COMMENTS = "comments";

	final URL url;
	final String type;

	/**
	 * Setup the parser for the url
	 * 
	 * @param feedUrl
	 *            - The URL of the feed
	 * @param feedType
	 *            - The type of feed (comments or posts)
	 */
	public RSSParser(String feedUrl, String feedType) {
		try {
			this.url = new URL(feedUrl);
			this.type = feedType;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings( { "null", "unchecked" })
	public RSSFeed readFeed() {
		RSSFeed feed = null;
		try {
			boolean isFeedHeader = true;

			// Set header values intial to the empty string
			List<String> category = new ArrayList();
			List<RSSImage> rssImages = new ArrayList();
			String content = "";
			String atomlink = "";
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "";
			String guid = "";
			String comments = "";

			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = read();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			String errorTitle = "";
			while (eventReader.hasNext()) {
				try {
					XMLEvent event = eventReader.nextEvent();
					if (event.isStartElement()) {
						// System.out.println("eventName = " +
						// event.asStartElement().getName().getLocalPart());
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(ITEM)) {
							if (isFeedHeader) {
								isFeedHeader = false;
								feed = new RSSFeed(title, link, description, language, copyright, pubdate);
							}
							// event = eventReader.nextEvent();
							continue;
						}

						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(TITLE)) {
							event = eventReader.nextEvent();
							title = event.asCharacters().getData();
							errorTitle = title;
							// System.out.println("title = " + title);
							continue;
						}

						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(DESCRIPTION)) {
							event = eventReader.nextEvent();
							if (event.isCharacters()) {
								description = event.asCharacters().getData();
								// System.out.println("description = " +
								// description);
							}
							continue;
						}

						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(LINK)) {
							event = eventReader.nextEvent();
							if (event.isCharacters()) {
								link = event.asCharacters().getData();
								// System.out.println("link = " + link);
							}
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(COMMENTS)) {
							event = eventReader.nextEvent();
							comments = event.asCharacters().getData();
							// System.out.println("comments = " + comments);
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(GUID)) {
							event = eventReader.nextEvent();
							guid = event.asCharacters().getData();
							// System.out.println("guid = " + guid);
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(LANGUAGE)) {
							event = eventReader.nextEvent();
							language = event.asCharacters().getData();
							// System.out.println("language = " + language);
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(AUTHOR)) {
							event = eventReader.nextEvent();
							author = event.asCharacters().getData();
							// System.out.println("author = " + author);
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(BLOGAUTHOR)
								&& this.type.equalsIgnoreCase("comments")) {
							event = eventReader.nextEvent();
							if (event.isCharacters()) {
								author = event.asCharacters().getData();
								// System.out.println("author = " + author);
							}
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(PUB_DATE)) {
							event = eventReader.nextEvent();
							pubdate = event.asCharacters().getData();
							// System.out.println("pubdate = " + pubdate);
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(COPYRIGHT)) {
							event = eventReader.nextEvent();
							copyright = event.asCharacters().getData();
							// System.out.println("copyright = " + copyright);
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(ATOMLINK)) {
							event = eventReader.nextEvent();
							atomlink = event.asCharacters().getData();
							// System.out.println("atomlink = " + atomlink);
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(CATEGORY)) {
							event = eventReader.nextEvent();
							if (event.asCharacters().isCharacters()) {
								category.add(event.asCharacters().getData());
								// System.out.println("category = " +
								// event.asCharacters().getData());
							}
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase(CONTENT)) {
							event = eventReader.nextEvent();
							content = event.asCharacters().getData();
							continue;
						}
					} else if (event.isEndElement()) {
						if (event.asEndElement().getName().getLocalPart().equalsIgnoreCase(ITEM)) {
							if (this.type.equalsIgnoreCase("posts")) {
								RSSPost rssPost = new RSSPost();
								rssPost.setAuthor(author);
								rssPost.setDescription(description);
								rssPost.setGuid(guid);
								rssPost.setLink(link);
								rssPost.setTitle(title);
								rssPost.setCategory(category);
								rssPost.setContentText(content);
								rssPost.setPubDate(pubdate);
								rssPost.setComments(comments);
								category = new ArrayList();
								List<String> imageUrls = ImageGrabber.getImageUrls(content);
								for (int i = 0; i < imageUrls.size(); i++) {
									RSSImage img = new RSSImage();
									String url = imageUrls.get(i);
									img.setOrigUrl(url);
									img.setFileName(url.substring(url.lastIndexOf("/") + 1));
									img.setExtension(url.substring(url.lastIndexOf(".") + 1));
									img.setBaseImg(ImageGrabber.getImageFile(url));
									String origGuid = rssPost.getGuid().substring(rssPost.getGuid().lastIndexOf("/") + 1);
									img.setOrigGuid(origGuid);
									List<RSSImage> postImages = rssPost.getImages();
									postImages.add(img);
									rssPost.setImages(postImages);
								}
								feed.getEntries().add(rssPost);
							} else if (this.type.equalsIgnoreCase("comments")) {
								RSSComment rssComment = new RSSComment();
								rssComment.setAuthor(author);
								rssComment.setGuid(guid);
								rssComment.setLink(link);
								rssComment.setContentText(content);
								rssComment.setPubDate(pubdate);
								rssComment.setTitle(title);
								feed.getComments().add(rssComment);
							}
							event = eventReader.nextEvent();
							continue;
						}
					}
				} catch (XMLStreamException e) {
					String problem = e.getLocalizedMessage() + ", in post with title: '" + errorTitle + "'";
					e.printStackTrace();
					FacesMessage msg = new FacesMessage();
					msg.setSummary(problem);
					FacesContext.getCurrentInstance().addMessage(problem, msg);
					XMLEvent event = eventReader.nextEvent();
					continue;
				} catch (ClassCastException e) {
					String problem = e.getLocalizedMessage() + ", in post with title: '" + errorTitle + "'";
					e.printStackTrace();
					FacesMessage msg = new FacesMessage();
					msg.setSummary(problem);
					FacesContext.getCurrentInstance().addMessage(problem, msg);
					XMLEvent event = eventReader.nextEvent();
					continue;
				} catch (Exception e) {
					String problem = e.getLocalizedMessage() + ", in post with title: '" + errorTitle + "'";
					e.printStackTrace();
					FacesMessage msg = new FacesMessage();
					msg.setSummary(problem);
					FacesContext.getCurrentInstance().addMessage(problem, msg);
					XMLEvent event = eventReader.nextEvent();
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return feed;

	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
