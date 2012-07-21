package com.xpoll.data;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a user response/selection from a displayed poll and is stored in the
 * Answer.responses property
 * 
 * @author Keith
 * 
 */

public class Response implements Serializable {

	private Answer forAnswer;
	private String responseText;
	private Date respDateTime;
	private long timeStamp;
	private String ipAddress;
	private String user;

	public Response() {

	}

	public Answer getForAnswer() {
		return forAnswer;
	}

	public void setForAnswer(Answer forAnswer) {
		this.forAnswer = forAnswer;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public Date getRespDateTime() {
		return respDateTime;
	}

	public void setRespDateTime(Date respDateTime) {
		this.respDateTime = respDateTime;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
