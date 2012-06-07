package com.xpolls.utils;

import javax.faces.context.FacesContext;

import lotus.domino.Database;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.designer.context.ServletXSPContext;

public class JSFUtils {

	public static Object getVariableValue(String varName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getVariableResolver().resolveVariable(context, varName);
	}

	public static String getParamValue(String paramName) {
		ServletXSPContext thisContext = (ServletXSPContext) getVariableValue("context");
		return thisContext.getUrlParameter(paramName).toString();
	}

	public static Database getCurrentDatabase() {
		return NotesContext.getCurrent().getCurrentDatabase();
	}

}
