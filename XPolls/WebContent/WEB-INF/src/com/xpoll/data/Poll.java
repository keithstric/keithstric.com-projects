package com.xpoll.data;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

import com.debug.DebugToolbar;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.xpoll.utils.JSFUtils;

public class Poll implements Serializable {

	private String title;
	private String tags;
	private String allowMultipleVoting;
	private String allowAnonymousVoting;
	private String introText;
	private String thankYouText;
	private String showNumVoters;
	private Date votingAllowedFrom;
	private Date votingAllowedTo;
	private String afterVotingGoto;
	private String afterVotingGotoUrl;
	private String sendEmailConfirmation;
	private String emailSubject;
	private String emailText;
	private TreeMap<String, Question> questions;
	private String unid;

	public Poll() {
		loadDoc();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getAllowMultipleVoting() {
		return allowMultipleVoting;
	}

	public void setAllowMultipleVoting(String allowMultipleVoting) {
		this.allowMultipleVoting = allowMultipleVoting;
	}

	public String getAllowAnonymousVoting() {
		return allowAnonymousVoting;
	}

	public void setAllowAnonymousVoting(String allowAnonymousVoting) {
		this.allowAnonymousVoting = allowAnonymousVoting;
	}

	public String getIntroText() {
		return introText;
	}

	public void setIntroText(String introText) {
		this.introText = introText;
	}

	public String getThankYouText() {
		return thankYouText;
	}

	public void setThankYouText(String thankYouText) {
		this.thankYouText = thankYouText;
	}

	public String getShowNumVoters() {
		return showNumVoters;
	}

	public void setShowNumVoters(String showNumVoters) {
		this.showNumVoters = showNumVoters;
	}

	public Date getVotingAllowedFrom() {
		return votingAllowedFrom;
	}

	public void setVotingAllowedFrom(Date votingAllowedFrom) {
		this.votingAllowedFrom = votingAllowedFrom;
	}

	public Date getVotingAllowedTo() {
		return votingAllowedTo;
	}

	public void setVotingAllowedTo(Date votingAllowedTo) {
		this.votingAllowedTo = votingAllowedTo;
	}

	public String getAfterVotingGoto() {
		return afterVotingGoto;
	}

	public void setAfterVotingGoto(String afterVotingGoto) {
		this.afterVotingGoto = afterVotingGoto;
	}

	public String getAfterVotingGotoUrl() {
		return afterVotingGotoUrl;
	}

	public void setAfterVotingGotoUrl(String afterVotingGotoUrl) {
		this.afterVotingGotoUrl = afterVotingGotoUrl;
	}

	public String getSendEmailConfirmation() {
		return sendEmailConfirmation;
	}

	public void setSendEmailConfirmation(String sendEmailConfirmation) {
		this.sendEmailConfirmation = sendEmailConfirmation;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailText() {
		return emailText;
	}

	public void setEmailText(String emailText) {
		this.emailText = emailText;
	}

	public TreeMap<String, Question> getQuestions() {
		if (questions != null) {
			return questions;
		} else {
			return new TreeMap<String, Question>();
		}
	}

	public void setQuestions(TreeMap<String, Question> questions) {
		this.questions = questions;
	}

	public String getUnid() {
		if (unid != null) {
			return unid;
		}
		String docId = JSFUtils.getParamValue("documentId");
		if (docId != null) {
			return docId;
		} else {
			return null;
		}
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public void loadDoc() {
		try {
			DebugToolbar.info("Poll.loadDoc running");
			if (getUnid() != null) {
				Database db = JSFUtils.getCurrentDatabase();
				String unid = getUnid();
				DebugToolbar.info("unid = '" + unid + "'", "Poll.loadDoc");
				if (unid != null && !unid.isEmpty()) {
					Document doc = db.getDocumentByUNID(unid);
					for (Object itemObj : doc.getItems()) {
						Item item = (Item) itemObj;
						if (item.getName().equalsIgnoreCase("afterVotingGoto")) {
							setAfterVotingGoto(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("afterVotingGotoUrl")) {
							setAfterVotingGotoUrl(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("allowAnonymousVoting")) {
							setAllowAnonymousVoting(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("emailSubject")) {
							setEmailSubject(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("emailText")) {
							setEmailText(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("introText")) {
							setIntroText(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("sendEmailConfirmation")) {
							setSendEmailConfirmation(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("showNumVoters")) {
							setShowNumVoters(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("tags")) {
							setTags(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("thankYouText")) {
							setThankYouText(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("title")) {
							setTitle(item.getValueString());
						} else if (item.getName().equalsIgnoreCase("unid")) {
							setUnid(doc.getUniversalID());
						} else if (item.getName().equalsIgnoreCase("votingAllowedFrom")) {
							setVotingAllowedFrom(item.getDateTimeValue().toJavaDate());
						} else if (item.getName().equalsIgnoreCase("votingAllowedTo")) {
							setVotingAllowedTo(item.getDateTimeValue().toJavaDate());
						}
					}
				}
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	public void saveDoc() {
		try {
			Database db = JSFUtils.getCurrentDatabase();
			Document doc = null;
			if (getUnid() != null) {
				doc = db.getDocumentByUNID(getUnid());
			} else {
				doc = db.createDocument();
				doc.replaceItemValue("form", "Poll");
				doc.save(true);
			}
			doc.replaceItemValue("afterVotingGoto", getAfterVotingGoto());
			doc.replaceItemValue("afterVotingGotoUrl", getAfterVotingGotoUrl());
			doc.replaceItemValue("allowAnonymousVoting", getAllowAnonymousVoting().toString());
			doc.replaceItemValue("allowMultipleVoting", getAllowMultipleVoting());
			doc.replaceItemValue("emailSubject", getEmailSubject());
			doc.replaceItemValue("emailText", getEmailText());
			doc.replaceItemValue("introText", getIntroText());
			doc.replaceItemValue("sendEmailConfirmation", getSendEmailConfirmation());
			doc.replaceItemValue("showNumVoters", getShowNumVoters());
			doc.replaceItemValue("tags", getTags());
			doc.replaceItemValue("thankYouText", getThankYouText());
			doc.replaceItemValue("title", getTitle());
			Date fromDate = getVotingAllowedFrom();
			DateTime fromDateTime = NotesContext.getCurrent().getCurrentSession().createDateTime(fromDate);
			doc.replaceItemValue("votingAllowedFrom", fromDateTime);
			Date toDate = getVotingAllowedTo();
			DateTime toDateTime = NotesContext.getCurrent().getCurrentSession().createDateTime(toDate);
			doc.replaceItemValue("votingAllowedTo", toDateTime);
			doc.replaceItemValue("unid", doc.getUniversalID());
			doc.save();
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}
}
