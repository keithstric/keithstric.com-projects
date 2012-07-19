package com.xpolls.renderkit;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import com.ibm.xsp.component.UIScriptCollector;
import com.xpolls.components.ListSelector;

public class ListSelectorRenderer extends Renderer {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) {
		try {
			super.encodeBegin(context, component);
			ListSelector c = (ListSelector) component;
			ResponseWriter writer = context.getResponseWriter();
			writer.startElement("div", c);
			writer.writeAttribute("id", c.getId() + "_container", null);
			writer.endElement("div");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) {
		try {
			super.encodeChildren(context, component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) {
		try {
			super.encodeEnd(context, component);
			ListSelector c = (ListSelector) component;
			UIScriptCollector.find().addScript("XSP.addOnLoad(function(){" + buildIncludes() + buildAddOnLoad(c) + "});");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String buildAddOnLoad(ListSelector c) {
		String scriptStr = null;
		String dataView = c.getViewName();
		boolean isQuestionList = false;
		boolean isAnswerList = false;
		if (c.getAnswerList()) {
			isAnswerList = true;
		} else if (c.getQuestionList()) {
			isQuestionList = true;
		}
		String labelCol = c.getLabelCol().toString();
		String valueCol = c.getValueCol().toString();
		StringBuilder sb = new StringBuilder();
		sb.append("var dijContainer = dojo.query(\"[id='" + c.getId() + "_container']\")[0];");
		sb.append("var dijNode = dojo.query(\"[id$=':" + c.getId() + "']\");");
		sb.append("var dij = null;");
		sb.append("if (dijNode[0]){");
		sb.append("dij = dijit.byId(dijNode[0].id);");
		sb.append("}");
		sb.append("if (!dij) {");
		sb.append("dij = new xpolls.dijit.ListSelector({");
		sb.append("dataView:'" + dataView + "',");
		sb.append("isAnswerList:" + isAnswerList + ",");
		sb.append("isQuestionList:" + isQuestionList + ",");
		sb.append("dataSource: {");
		sb.append("labelCol:" + labelCol + ",");
		sb.append("valueCol:" + valueCol + ",");
		sb.append("viewName:'" + dataView + "'}");
		sb.append("}");
		sb.append(",dijContainer);}");
		scriptStr = sb.toString();
		return scriptStr;
	}

	private String buildIncludes() {
		String script = "dojo.registerModulePath(\"xpolls\",XPolls.getNsfUrl() + \"/xpolls\");"
				+ "dojo.require(\"xpolls.dijit.ListSelector\");";
		return script;
	}

}