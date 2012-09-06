package com.keithstric.query;

import javax.faces.component.UIComponent;

public class ContainsFilter extends QueryFilter {

	public ContainsFilter(String propertyName, Object content) {
		super(propertyName, content);
	}

	@Override
	public boolean matches(UIComponent component) {
		boolean result = false;
		if (isCompatibleWith(component)) {
			Object currentValue = getPropertyValue(component);
			Object content = getContent();
			if (currentValue instanceof String && content instanceof CharSequence) {
				result = ((String) currentValue).contains((CharSequence) content);
			}
		}
		return result;
	}
}