package com.xpoll.data;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Represents a question item stored in the Poll.questions property
 * 
 * @author Keith
 * 
 */

public class Question implements Serializable {

	private String question;
	private String description;
	private String fieldType;
	private String answerRequired;
	private TreeMap<Double, Answer> answers;
	private Integer sortOrder;
	private Poll forPoll;

	public Question() {
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

	public TreeMap<Double, Answer> getAnswers() {
		if (answers != null) {
			return answers;
		} else {
			return new TreeMap<Double, Answer>();
		}
	}

	public void setAnswers(TreeMap<Double, Answer> answers) {
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
