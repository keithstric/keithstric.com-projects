package com.xpolls.components;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.dojo.UIDojoWidget;

public class ListSelector extends UIDojoWidget {

	public final String FAMILY = "com.xpolls";
	public final String RENDERER_TYPE = "com.xpolls.ListSelector";

	public String viewName;
	public Integer labelCol;
	public Integer valueCol;
	public String listHeight;
	public String listWidth;
	public Boolean questionList;
	public Boolean answerList;

	public ListSelector() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return FAMILY;
	}

	public String getViewName() {
		if (null != viewName) {
			return viewName;
		}
		ValueBinding vb = getValueBinding("viewName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Integer getLabelCol() {
		if (null != labelCol) {
			return labelCol;
		}
		ValueBinding vb = getValueBinding("labelCol");
		if (vb != null) {
			return (Integer) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setLabelCol(Integer labelCol) {
		this.labelCol = labelCol;
	}

	public Integer getValueCol() {
		if (null != valueCol) {
			return valueCol;
		}
		ValueBinding vb = getValueBinding("valueCol");
		if (vb != null) {
			return (Integer) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setValueCol(Integer valueCol) {
		this.valueCol = valueCol;
	}

	public String getListHeight() {
		if (null != listHeight) {
			return listHeight;
		}
		ValueBinding vb = getValueBinding("listHeight");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setListHeight(String listHeight) {
		this.listHeight = listHeight;
	}

	public String getListWidth() {
		if (null != listWidth) {
			return listWidth;
		}
		ValueBinding vb = getValueBinding("listWidth");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setListWidth(String listWidth) {
		this.listWidth = listWidth;
	}

	public Boolean getQuestionList() {
		if (null != questionList) {
			return questionList;
		}
		ValueBinding vb = getValueBinding("questionList");
		if (vb != null) {
			return (Boolean) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setQuestionList(Boolean questionList) {
		this.questionList = questionList;
	}

	public Boolean getAnswerList() {
		if (null != answerList) {
			return answerList;
		}
		ValueBinding vb = getValueBinding("answerList");
		if (vb != null) {
			return (Boolean) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setAnswerList(Boolean answerList) {
		this.answerList = answerList;
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] properties = (Object[]) state;
		int idx = 0;
		super.restoreState(context, properties[idx++]);
		answerList = (Boolean) properties[idx++];
		labelCol = (Integer) properties[idx++];
		valueCol = (Integer) properties[idx++];
		listHeight = (String) properties[idx++];
		listWidth = (String) properties[idx++];
		questionList = (Boolean) properties[idx++];
		viewName = (String) properties[idx++];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] properties = new Object[8];
		int idx = 0;
		properties[idx++] = super.saveState(context);
		properties[idx++] = answerList;
		properties[idx++] = labelCol;
		properties[idx++] = valueCol;
		properties[idx++] = listHeight;
		properties[idx++] = listWidth;
		properties[idx++] = questionList;
		properties[idx++] = viewName;
		return properties;
	}
}
