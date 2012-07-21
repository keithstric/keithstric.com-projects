package com.xpoll.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.component.UISelectItemEx;
import com.ibm.xsp.component.xp.XspInputText;
import com.ibm.xsp.component.xp.XspSelectManyCheckbox;
import com.ibm.xsp.component.xp.XspSelectOneMenu;
import com.ibm.xsp.component.xp.XspSelectOneRadio;
import com.ibm.xsp.validator.RequiredValidator;

public class PollUtils implements Serializable {

	private static List<String> answers;
	private static UIComponent container;

	public static XspSelectManyCheckbox getCheckBoxes() {
		XspSelectManyCheckbox chkBox = new XspSelectManyCheckbox();
		chkBox.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		chkBox.setLayout("pageDirection");
		List<String> answers = getAnswers();
		return (XspSelectManyCheckbox) getSelectItems(chkBox, answers);
	}

	public static XspSelectOneRadio getRadioButtons() {
		XspSelectOneRadio radioBtn = new XspSelectOneRadio();
		radioBtn.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
		radioBtn.setLayout("pageDirection");
		List<String> answers = getAnswers();
		return (XspSelectOneRadio) getSelectItems(radioBtn, answers);
	}

	public static List<XspInputText> getInputText() {
		List<String> answers = getAnswers();
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
		List<String> answers = getAnswers();
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

	public static List<String> getAnswers() {
		if (answers != null) {
			return answers;
		} else {
			return new ArrayList<String>();
		}
	}

	public static List<String> getAnswers(String unid) {
		try {
			Database db = NotesContext.getCurrent().getCurrentDatabase();
			View answerView = db.getView("(luAnswers)");
			ViewEntryCollection entCol = answerView.getAllEntriesByKey(unid);
			ViewEntry ent = entCol.getFirstEntry();
			List<String> answers = new ArrayList();
			while (ent != null) {
				answers.add(ent.getDocument().getItemValueString("option"));
				ent = entCol.getNextEntry(ent);
			}
			return answers;
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setAnswer(List<String> answer) {
		this.answers = answer;
	}

	public static UIComponent getContainer() {
		return container;
	}

	public void setContainer(UIComponent container) {
		this.container = container;
	}
}
