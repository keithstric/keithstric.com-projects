package com.keithstric.query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.faces.component.UIComponent;

abstract class QueryFilter {
	private String propertyName;
	private Object content;

	protected QueryFilter(String propertyName, Object content) {
		setPropertyName(propertyName);
		setContent(content);
	}

	protected QueryFilter() {

	}

	private void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	private void setContent(Object content) {
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

	private Method getExpectedBeanGetter(Class<?> clazz) {
		Method result = null;
		char[] charArray = getPropertyName().toCharArray();
		charArray[0] = Character.toUpperCase(charArray[0]);
		String propName = new String(charArray);
		String guessedName = "get" + propName;
		try {
			result = clazz.getMethod(guessedName, (Class[]) null);
		} catch (NoSuchMethodException e1) {
			try {
				guessedName = "is" + propName;
				result = clazz.getMethod(guessedName, (Class[]) null);
			} catch (NoSuchMethodException e) {
				// nothing necessary. Result is still null
			}
		}
		return result;
	}

	private Method getMethod(UIComponent component) {
		Method result = null;
		try {
			result = getExpectedBeanGetter(component.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected Object getPropertyValue(UIComponent component) {
		Object result = null;
		Method crystal = getMethod(component);
		if (crystal != null) {
			try {
				result = invokeMethod(component, crystal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private Object invokeMethod(Object obj, Method crystal) throws Exception {
		Object result = null;
		try {
			result = crystal.invoke(obj, (Object[]) null);
		} catch (IllegalArgumentException e) {
			result = e.getMessage();
		} catch (IllegalAccessException e) {
			result = e.getMessage();
		} catch (InvocationTargetException e) {
			result = e.getMessage();
		}
		return result;
	}

	protected boolean isCompatibleWith(UIComponent component) {
		boolean result = false;
		Method crystal = getMethod(component);
		if (crystal != null) {
			result = crystal.getReturnType().isAssignableFrom(getContent().getClass());
		}
		return result;
	}

	public abstract boolean matches(UIComponent component);

}
