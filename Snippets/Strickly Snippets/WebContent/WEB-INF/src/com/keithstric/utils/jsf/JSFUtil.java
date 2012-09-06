package com.keithstric.utils.jsf;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.designer.context.ServletXSPContext;
import com.ibm.xsp.designer.context.XSPUrl;

public class JSFUtil implements Serializable {

	/**
	 * The method creates a {@link javax.faces.el.ValueBinding} from the specified value binding expression and returns
	 * its current value.<br>
	 * <br>
	 * If the expression references a managed bean or its properties and the bean has not been created yet, it gets
	 * created by the JSF runtime.
	 * 
	 * @param ref
	 *            value binding expression, e.g. #{Bean1.property}
	 * @return value of ValueBinding throws javax.faces.el.ReferenceSyntaxException if the specified <code>ref</code>
	 *         has invalid syntax
	 */
	public static Object getBindingValue(String ref) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		return application.createValueBinding(ref).getValue(context);
	}

	/**
	 * The method creates a {@link javax.faces.el.ValueBinding} from the specified value binding expression and sets a
	 * new value for it.<br>
	 * <br>
	 * If the expression references a managed bean and the bean has not been created yet, it gets created by the JSF
	 * runtime.
	 * 
	 * @param ref
	 *            value binding expression, e.g. #{Bean1.property}
	 * @param newObject
	 *            new value for the ValueBinding throws javax.faces.el.ReferenceSyntaxException if the specified
	 *            <code>ref</code> has invalid syntax
	 */
	public static void setBindingValue(String ref, Object newObject) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ValueBinding binding = application.createValueBinding(ref);
		binding.setValue(context, newObject);
	}

	/**
	 * The method returns the value of a global JavaScript variable.
	 * 
	 * @param varName
	 *            variable name
	 * @return value
	 * @throws javax.faces.el.EvaluationException
	 *             if an exception is thrown while resolving the variable name
	 */
	public static Object getVariableValue(String varName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getVariableResolver().resolveVariable(context, varName);
	}

	/**
	 * Finds an UIComponent by its component identifier in the current component tree.
	 * 
	 * @param compId
	 *            the component identifier to search for
	 * @return found UIComponent or null
	 * 
	 * @throws NullPointerException
	 *             if <code>compId</code> is null
	 */
	public static UIComponent findComponent(String compId) {
		return findComponent(FacesContext.getCurrentInstance().getViewRoot(), compId);
	}

	/**
	 * Finds an UIComponent by its component identifier in the component tree below the specified
	 * <code>topComponent</code> top component.
	 * 
	 * @param topComponent
	 *            first component to be checked
	 * @param compId
	 *            the component identifier to search for
	 * @return found UIComponent or null
	 * 
	 * @throws NullPointerException
	 *             if <code>compId</code> is null
	 */
	public static UIComponent findComponent(UIComponent topComponent, String compId) {
		if (compId == null) {
			throw new NullPointerException("Component identifier cannot be null");
		}

		if (compId.equals(topComponent.getId())) {
			return topComponent;
		}

		if (topComponent.getChildCount() > 0) {
			List<UIComponent> childComponents = topComponent.getChildren();

			for (UIComponent currChildComponent : childComponents) {
				UIComponent foundComponent = findComponent(currChildComponent, compId);
				if (foundComponent != null) {
					return foundComponent;
				}
			}
		}
		return null;
	}

	public static Document getCurrentDocument() {
		Object docObj = FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(
				FacesContext.getCurrentInstance(), "currentDocument");
		if (docObj instanceof Document) {
			return (Document) docObj;
		} else {
			return null;
		}
	}

	/**
	 * Get the current db Url
	 * 
	 * @return java.lang.String
	 */
	public static String getDbUrl() {
		ServletXSPContext thisContext = (ServletXSPContext) JSFUtil.getVariableValue("context");
		XSPUrl thisUrl = thisContext.getUrl();
		String permaLink = "http://" + thisUrl.getHost()
				+ thisUrl.getPath().substring(0, thisUrl.getPath().lastIndexOf("/"));
		return permaLink;
	}

	/**
	 * Get a url parameter value
	 * 
	 * @param paramName
	 *            java.lang.String - The parameter name you want the value from
	 * @return java.lang.String
	 */
	public static String getParamValue(String paramName) {
		ServletXSPContext thisContext = (ServletXSPContext) JSFUtil.getVariableValue("context");
		return thisContext.getUrlParameter(paramName).toString();
	}

	/**
	 * Get the current database
	 * 
	 * @return lotus.domino.Database
	 */
	public static Database getCurrentDatabase() {
		return NotesContext.getCurrent().getCurrentDatabase();
	}

	/**
	 * Get a bean
	 * 
	 * @param expr
	 *            java.lang.String - the bean name you're looking for
	 * @return Object
	 */
	public static Object getBean(String expr) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		ValueBinding binding = app.createValueBinding("#{" + expr + "}");
		Object value = binding.getValue(context);
		return value;
	}

	public static void incinerate(Object... args) {
		for (Object o : args) {
			if (o instanceof Base) {
				try {
					((Base) o).recycle();
				} catch (NotesException ne) {
					// who cares?
				}
			} else if (o instanceof Map) {
				for (Object entry : ((Map) o).entrySet()) {
					incinerate(((Map.Entry) entry).getKey());
					incinerate(((Map.Entry) entry).getValue());
				}
			} else if (o instanceof Collection) {
				Collection coll = (Collection) o;
				Iterator i = coll.iterator();
				while (i.hasNext()) {
					Object io = i.next();
					incinerate(io);
				}
			}
		}
	}

	public static void handleException(Throwable t) {
		t.printStackTrace();
	}

	public static void msgbox(String message) {
		FacesContext.getCurrentInstance().addMessage("messages", new javax.faces.application.FacesMessage(message));
	}

	/**
	 * Get a submitted value
	 * 
	 * @param compId
	 *            java.lang.String - The server ID of the component whose value you want
	 * @return Object
	 */
	public static Object getSubmittedValue(String compId) {
		UIComponent c = findComponent(FacesContext.getCurrentInstance().getViewRoot(), compId);
		// value submitted from the browser
		Object o = ((UIInput) c).getSubmittedValue();
		if (null == o) {
			// else not yet submitted
			o = ((UIInput) c).getValue();
		}
		return o;
	}
	
	public static String getUserName() {
		return (String)getBindingValue("#{context.user.name}");
	}
	public static Session getSession() {
		return (Session)getVariableValue("session");
	}
	public static Session getSessionAsSigner() {
		return (Session)getVariableValue("sessionAsSigner");
	}
	public static Database getDatabase() {
		return (Database)getVariableValue("database");
	}
	public static UIViewRootEx2 getViewRoot() {
		return (UIViewRootEx2)getVariableValue("view");
	}
}