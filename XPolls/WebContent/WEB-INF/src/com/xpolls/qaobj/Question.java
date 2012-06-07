package com.xpolls.qaobj;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

import com.debug.DebugToolbar;
import com.xpolls.utils.JSFUtils;

public class Question implements Serializable {

	//private final DebugToolbar debug = new DebugToolbar();
	private String question;
	private String questionDescription;
	private String answerFieldType;
	private String answerRequired;
	private List<Answer> answers;
	private String unid;
	private String parentUnid;

	public Question() {
		DebugToolbar.info("New Question", "Question.Question");
		loadDoc();
	}

	public Question(String unid) {
		DebugToolbar.info("New Question unid=" + unid, "Question.Question(String)");
		setUnid(unid);
		loadDoc();
	}

	public String getQuestion() {
		DebugToolbar.info("question=" + question, "Question.getQuestion");
		return question;
	}

	public void setQuestion(String question) {
		DebugToolbar.info("setting the question to " + question, "Question.setQuestion");
		this.question = question;
	}

	public String getQuestionDescription() {
		DebugToolbar.info("questionDescription=" + questionDescription, "Question.getQuestionDescription");
		return questionDescription;
	}

	public void setQuestionDescription(String questionDescription) {
		this.questionDescription = questionDescription;
	}

	public String getAnswerFieldType() {
		DebugToolbar.info("answerFieldType=" + answerFieldType, "Question.getAnswerFieldType");
		return answerFieldType;
	}

	public void setAnswerFieldType(String answerFieldType) {
		this.answerFieldType = answerFieldType;
	}

	public String getAnswerRequired() {
		DebugToolbar.info("answerRequired=" + answerRequired, "Question.getAnswerRequired");
		return answerRequired;
	}

	public void setAnswerRequired(String answerRequired) {
		this.answerRequired = answerRequired;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public String getUnid() {
		if (unid != null) {
			DebugToolbar.info("unid = " + unid, "Question.getUnid");
			return unid;
		}
		Map sessionScope = (Map) JSFUtils.getVariableValue("sessionScope");
		if (sessionScope.containsKey("selectedQuestion")) {
			if (sessionScope.get("selectedQuestion") == null) {
				DebugToolbar.info("unid = null", "Question.getUnid");
				return null;
			} else {
				DebugToolbar.info("unid (via sessionScope) = " + sessionScope.get("selectedQuestion"), "Question.getUnid");
				return sessionScope.get("selectedQuestion").toString();
			}
		} else {
			DebugToolbar.info("unid = null", "Question.getUnid");
			return null;
		}
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getParentUnid() {
		if (parentUnid != null && !parentUnid.isEmpty()) {
			DebugToolbar.info("parentUnid = " + parentUnid, "Question.getParentUnid");
			if (parentUnid.equals(JSFUtils.getParamValue("documentId"))) {
				return parentUnid;
			} else {
				return JSFUtils.getParamValue("documentId");
			}
		} else if (JSFUtils.getParamValue("documentId") != null) {
			DebugToolbar.info("parenUnid (via url param)= " + JSFUtils.getParamValue("documentId"), "Question.getParentUnid");
			setParentUnid(JSFUtils.getParamValue("documentId"));
			return JSFUtils.getParamValue("documentId");
		} else {
			DebugToolbar.info("parentUnid = null", "Question.getParentUnid");
			return null;
		}
	}

	public void setParentUnid(String parentUnid) {
		this.parentUnid = parentUnid;
	}

	public void loadDoc() {
		try {
			DebugToolbar.info("Question.loadDoc running");
			if (getUnid() != null) {
				DebugToolbar.info("unid = " + getUnid(), "Question.loadDoc");
				Database db = JSFUtils.getCurrentDatabase();
				Document doc = db.getDocumentByUNID(getUnid());
				//TODO: Need to load up the answers for this question
				for (Object itemObj : doc.getItems()) {
					Item item = (Item) itemObj;
					if (item.getName().equalsIgnoreCase("answerFieldType")) {
						setAnswerFieldType(item.getValueString());
					} else if (item.getName().equalsIgnoreCase("answerRequired")) {
						setAnswerRequired(item.getValueString());
					} else if (item.getName().equalsIgnoreCase("parentUnid")) {
						setParentUnid(item.getParent().getUniversalID());
					} else if (item.getName().equalsIgnoreCase("questionDescription")) {
						setQuestionDescription(item.getValueString());
					} else if (item.getName().equalsIgnoreCase("unid")) {
						setUnid(doc.getUniversalID());
					}
				}
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	public void saveDoc() {
		try {
			DebugToolbar.info("saveDoc starting");
			Database db = JSFUtils.getCurrentDatabase();
			Document doc = null;
			if (getUnid() != null) {
				DebugToolbar.info("got a document already", "Question.saveDoc");
				doc = db.getDocumentByUNID(getUnid());
			} else {
				DebugToolbar.info("creating a new document", "Question.saveDoc");
				doc = db.createDocument();
				doc.replaceItemValue("form", "Question");
				DebugToolbar.info("making new doc a response of " + getParentUnid(), "Question.saveDoc");
				doc.makeResponse(db.getDocumentByUNID(getParentUnid()));
				doc.save(true, true);
				setUnid(doc.getUniversalID());
				Map sessionScope = (Map) JSFUtils.getVariableValue("sessionScope");
				DebugToolbar.info("unid = " + getUnid(), "Question.saveDoc");
				sessionScope.put("selectedQuestion", getUnid());
				DebugToolbar.info("sessionScope.get('selectedQuestion')=" + sessionScope.get("selectedQuestion"), "Question.saveDoc");
				loadDoc();
			}
			doc.replaceItemValue("answerFieldType", getAnswerFieldType());
			doc.replaceItemValue("answerRequired", getAnswerRequired());
			doc.replaceItemValue("question", getQuestion());
			doc.replaceItemValue("questionDescription", getQuestionDescription());
			doc.replaceItemValue("unid", doc.getUniversalID());
			doc.replaceItemValue("parentUnid", getParentUnid());
			doc.save();
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}
}
