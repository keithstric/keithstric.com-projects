package com.keithstric.utils.rss;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class ImageGrabber {

	public ImageGrabber() {
	}

	public static List<String> getImageUrls(String html) {
		List<String> urlList = new ArrayList<String>();
		Pattern imgTagRegEx = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
		Matcher imgTagMatcher = imgTagRegEx.matcher(html);
		String imgTag = null;
		String imgUrl = null;
		while (imgTagMatcher.find()) {
			imgTag = imgTagMatcher.group();
			if (imgTag != null) {
				Pattern imgSrcRegEx = Pattern.compile("src\\s*=\\s*['\"]([^'\"]+)");
				Matcher imgSrcMatcher = imgSrcRegEx.matcher(imgTag);
				if (imgSrcMatcher.find()) {
					imgUrl = imgSrcMatcher.group();
					if (imgUrl != null) {
						imgUrl = imgUrl.substring(imgUrl.indexOf("\"") + 1);
						urlList.add(imgUrl);
					}
				}
			}
		}
		return urlList;
	}

	public static BufferedImage getImageFile(String url) {
		BufferedImage img = null;
		try {
			URL imgUrl = new URL(url);
			img = ImageIO.read(imgUrl);
			return img;
		} catch (Exception e) {
			System.out.println("Image at url " + url + " not found");
		}
		return null;
	}
}