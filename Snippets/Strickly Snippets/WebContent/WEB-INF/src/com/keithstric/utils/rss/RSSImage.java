package com.keithstric.utils.rss;

import java.awt.image.BufferedImage;

public class RSSImage {

	private String extension;
	private String fileName;
	private String url;
	private String newPermalink;
	private BufferedImage baseImg;
	private String origUrl;
	private String origGuid;

	public RSSImage() {
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNewPermalink() {
		return newPermalink;
	}

	public void setNewPermalink(String newPermalink) {
		this.newPermalink = newPermalink;
	}

	public BufferedImage getBaseImg() {
		return baseImg;
	}

	public void setBaseImg(BufferedImage baseImg) {
		this.baseImg = baseImg;
	}

	public String getOrigUrl() {
		return origUrl;
	}

	public void setOrigUrl(String origUrl) {
		this.origUrl = origUrl;
	}

	public String getOrigGuid() {
		return origGuid;
	}

	public void setOrigGuid(String origGuid) {
		this.origGuid = origGuid;
	}

}
