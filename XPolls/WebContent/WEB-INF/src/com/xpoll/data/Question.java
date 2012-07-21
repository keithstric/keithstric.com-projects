package com.xpoll.data;

import java.io.Serializable;
import java.util.TreeMap;

public class Question implements Serializable {

	private String question;
	private String description;
	private String fieldType;
	private String answerRequired;
	private TreeMap<String, Answer> answers;
	private Integer sortOrder;
	private Poll forPoll;

	public Question() {
	}

	public void loadDoc() {

	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getAnswerRequired() {
		return answerRequired;
	}

	public void setAnswerRequired(String answerRequired) {
		this.answerRequired = answerRequired;
	}

	public TreeMap<String, Answer> getAnswers() {
		if (answers != null) {
			return answers;
		} else {
			return new TreeMap<String, Answer>();
		}
	}

	public void setAnswers(TreeMap<String, Answer> answers) {
		this.answers = answers;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Poll getForPoll() {
		return forPoll;
	}

	public void setForPoll(Poll forPoll) {
		this.forPoll = forPoll;
	}

}
