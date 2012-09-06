package com.keithstric.query;

import javax.faces.component.UIComponent;

public class EqualsFilter extends QueryFilter {

	public EqualsFilter(String propertyName, Object content) {
		super(propertyName, content);
	}

	@Override
	public boolean matches(UIComponent component) {
		boolean result = false;
		if (isCompatibleWith(component)) {
			Object propertyValue = getPropertyValue(component);
			if (propertyValue instanceof String && getContent() instanceof String) {
				result = ((String) propertyValue).equalsIgnoreCase((String) getContent());
			} else {
				if (propertyValue != null) {
					result = propertyValue.equals(getContent());
				}
			}
		}
		return result;
	}

}
