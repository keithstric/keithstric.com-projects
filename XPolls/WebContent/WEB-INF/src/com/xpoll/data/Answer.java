package com.xpoll.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Answer implements Serializable {

	private String answer;
	private String description;
	private String freeTextAllowed;
	private String freeTextNewOption;
	private List<Response> responses;
	private Integer sortOrder;
	private Question forQuestion;

	public Answer() {
		loadDoc();
	}

	public void loadDoc() {

	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFreeTextAllowed() {
		return freeTextAllowed;
	}

	public void setFreeTextAllowed(String freeTextAllowed) {
		this.freeTextAllowed = freeTextAllowed;
	}

	public String getFreeTextNewOption() {
		return freeTextNewOption;
	}

	public void setFreeTextNewOption(String freeTextNewOption) {
		this.freeTextNewOption = freeTextNewOption;
	}

	public List<Response> getResponses() {
		if (responses != null) {
			return responses;
		} else {
			return new ArrayList<Response>();
		}
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Question getForQuestion() {
		return forQuestion;
	}

	public void setForQuestion(Question forQuestion) {
		this.forQuestion = forQuestion;
	}
}
