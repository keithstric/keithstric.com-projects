package com.xpoll.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import lotus.domino.Document;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.component.UISelectItemEx;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspInputText;
import com.ibm.xsp.component.xp.XspSelectManyCheckbox;
import com.ibm.xsp.component.xp.XspSelectOneMenu;
import com.ibm.xsp.component.xp.XspSelectOneRadio;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.validator.RequiredValidator;
import com.xpoll.data.Answer;
import com.xpoll.data.Poll;

/**
 * This is the PollUtils which just contains helper methods for displaying a
 * poll to a user/voter
 * 
 * @author Keith Strickland
 * 
 */

public class PollUtils implements Serializable {

	private static final long serialVersionUID = 1L;
	private static TreeMap<Double, Answer> answers;
	private static Poll dispPoll;

	public static XspSelectManyCheckbox getCheckBoxes() {
		XspSelectManyCheckbox chkBox = new XspSelectManyCheckbox();
		chkBox.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		chkBox.setLayout("pageDirection");
		String ssjsCode = "print(\"onchange for checkbox " + chkBox.getId() + "\");";
		addEventHandler(chkBox, ssjsCode);
		List<String> answers = getAnswersList();
		addSelectItems(chkBox, answers);
		return chkBox;
	}

	public static XspSelectOneRadio getRadioButtons() {
		XspSelectOneRadio radioBtn = new XspSelectOneRadio();
		radioBtn.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		radioBtn.setLayout("pageDirection");
		String ssjsCode = "print(\"onchange for Radio Button " + radioBtn.getId() + "\");";
		addEventHandler(radioBtn, ssjsCode);
		List<String> answers = getAnswersList();
		addSelectItems(radioBtn, answers);
		return radioBtn;
	}

	public static List<XspInputText> getInputText() {
		List<String> answers = getAnswersList();
		List<XspInputText> components = new ArrayList<XspInputText>();
		for (String answer : answers) {
			XspInputText inputText = new XspInputText();
			inputText.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
			inputText.setDefaultValue(answer);
			String ssjsCode = "print(\"onchange for Input Text " + inputText.getId() + "\");";
			addEventHandler(inputText,ssjsCode);
			components.add(inputText);
		}
		return components;
	}

	@SuppressWarnings("unchecked")
	public static void addValidator(UIComponent requiredField) {
		RequiredValidator valid = new RequiredValidator();
		valid.setMessage("You must answer this question...");
		requiredField.getChildren().add(valid);
	}

	public static XspSelectOneMenu getComboBox() {
		XspSelectOneMenu combo = new XspSelectOneMenu();
		combo.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		String ssjsCode = "print(\"onchange for combo box " + combo.getId() + "\");";
		addEventHandler(combo, ssjsCode);
		List<String> answers = getAnswersList();
		addSelectItems(combo, answers);
		return combo;
	}

	@SuppressWarnings("unchecked")
	private static void addSelectItems(UIComponent parentComp, List<String> answers) {
		if (answers != null && answers.size() > 0) {
			for (String answer : answers) {
				UISelectItemEx select = new UISelectItemEx();
				select.setItemLabel(answer);
				parentComp.getChildren().add(select);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void addEventHandler(UIComponent parentComp, String ssjsCode) {
		XspEventHandler handler = new XspEventHandler();
		handler.setId(FacesContextEx.getCurrentInstance().getViewRoot().createUniqueId());
		handler.setEvent("onchange");
		handler.setRefreshMode("complete");
		ssjsCode = "#{javascript:" + ssjsCode + "}";
		MethodBinding method = ApplicationEx.getInstance().createMethodBinding(ssjsCode, null);
		if (method instanceof MethodBindingEx) {
			((MethodBindingEx) method).setComponent(parentComp);
		}
		handler.setAction(method);
		parentComp.getChildren().add(handler);
	}

	public static TreeMap<Double, Answer> getAnswers() {
		return answers;
	}

	public static void setAnswers(TreeMap<Double, Answer> answers) {
		PollUtils.answers = answers;
	}

	private static List<String> getAnswersList() {
		TreeMap<Double, Answer> answersMap = getAnswers();
		List<String> answerList = new ArrayList<String>();
		for (Double key : answersMap.keySet()) {
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
