package com.xpoll.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import lotus.domino.Document;

import com.ibm.xsp.component.UISelectItemEx;
import com.ibm.xsp.component.xp.XspInputText;
import com.ibm.xsp.component.xp.XspSelectManyCheckbox;
import com.ibm.xsp.component.xp.XspSelectOneMenu;
import com.ibm.xsp.component.xp.XspSelectOneRadio;
import com.ibm.xsp.validator.RequiredValidator;
import com.xpoll.data.Answer;
import com.xpoll.data.Poll;

/**
 * This is the PollUtils which just contains helper methods for displaying a
 * poll to a user/voter
 * 
 * @author Keith
 * 
 */

public class PollUtils implements Serializable {

	private static final long serialVersionUID = 1L;
	private static TreeMap<String, Answer> answers;
	private static Poll dispPoll;

	public static XspSelectManyCheckbox getCheckBoxes() {
		XspSelectManyCheckbox chkBox = new XspSelectManyCheckbox();
		chkBox.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		chkBox.setLayout("pageDirection");
		List<String> answers = getAnswersList();
		return (XspSelectManyCheckbox) getSelectItems(chkBox, answers);
	}

	public static XspSelectOneRadio getRadioButtons() {
		XspSelectOneRadio radioBtn = new XspSelectOneRadio();
		radioBtn.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		radioBtn.setLayout("pageDirection");
		List<String> answers = getAnswersList();
		return (XspSelectOneRadio) getSelectItems(radioBtn, answers);
	}

	public static List<XspInputText> getInputText() {
		List<String> answers = getAnswersList();
		List<XspInputText> components = new ArrayList<XspInputText>();
		for (String answer : answers) {
			XspInputText inputText = new XspInputText();
			inputText.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
			inputText.setDefaultValue(answer);
			components.add(inputText);
		}
		return components;
	}

	public static UIComponent addValidator(UIComponent requiredField) {
		RequiredValidator valid = new RequiredValidator();
		valid.setMessage("You must answer this question...");
		requiredField.getChildren().add(valid);
		return requiredField;
	}

	public static XspSelectOneMenu getComboBox() {
		XspSelectOneMenu combo = new XspSelectOneMenu();
		combo.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		List<String> answers = getAnswersList();
		return (XspSelectOneMenu) getSelectItems(combo, answers);
	}

	private static UIComponent getSelectItems(UIComponent parentComp, List<String> answers) {
		if (answers != null && answers.size() > 0) {
			for (String answer : answers) {
				UISelectItemEx select = new UISelectItemEx();
				select.setItemLabel(answer);
				parentComp.getChildren().add(select);
			}
		}
		return parentComp;
	}

	public static TreeMap<String, Answer> getAnswers() {
		return answers;
	}

	public static void setAnswers(TreeMap<String, Answer> answers) {
		PollUtils.answers = answers;
	}

	private static List<String> getAnswersList() {
		TreeMap<String, Answer> answersMap = getAnswers();
		List<String> answerList = new ArrayList<String>();
		for (String key : answersMap.keySet()) {
			answerList.add(answersMap.get(key).getAnswer());
		}
		return answerList;
	}

	public static Poll getDispPoll() {
		return dispPoll;
	}

	public static void setPoll(Poll dispPoll) {
		PollUtils.dispPoll = dispPoll;
	}

	public static Poll findPollClass(Document doc, String itemName) {
		Poll foundPoll = (Poll) MIMEBean.restoreState(doc, itemName);
		return foundPoll;
	}
}
