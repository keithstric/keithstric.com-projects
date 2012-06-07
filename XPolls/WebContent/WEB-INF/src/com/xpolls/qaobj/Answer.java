package com.xpolls.qaobj;

import java.io.Serializable;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.domino.xsp.module.nsf.NotesContext;

public class Answer implements Serializable {

	private String option;
	private Boolean allowFreeText;
	private Boolean allowNewOption;
	private String optionDescription;
	private String unid;
	private String parentUnid;

	public Answer() {

	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public Boolean getAllowFreeText() {
		return allowFreeText;
	}

	public void setAllowFreeText(Boolean allowFreeText) {
		this.allowFreeText = allowFreeText;
	}

	public Boolean getAllowNewOption() {
		return allowNewOption;
	}

	public void setAllowNewOption(Boolean newOption) {
		this.allowNewOption = newOption;
	}

	public String getOptionDescription() {
		return optionDescription;
	}

	public void setOptionDescription(String optionDescription) {
		this.optionDescription = optionDescription;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getParentUnid() {
		return parentUnid;
	}

	public void setParentUnid(String parentUnid) {
		this.parentUnid = parentUnid;
	}

	public void saveDoc() {
		try {
			Database db = NotesContext.getCurrent().getCurrentDatabase();
			Document doc = null;
			if (getUnid() != null) {
				doc = db.getDocumentByUNID(getUnid());
			} else {
				doc = db.createDocument();
				doc.replaceItemValue("form", "Answer");
				doc.save(true);
			}
			doc.replaceItemValue("allowFreeText", getAllowFreeText());
			doc.replaceItemValue("allowNewOption", getAllowNewOption().toString());
			doc.replaceItemValue("option", getOption());
			doc.replaceItemValue("optionDescription", getOptionDescription());
			doc.replaceItemValue("unid", doc.getUniversalID());
			doc.replaceItemValue("parentUnid", getParentUnid());
			doc.makeResponse(db.getDocumentByUNID(getParentUnid()));
			doc.save();
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}
}
