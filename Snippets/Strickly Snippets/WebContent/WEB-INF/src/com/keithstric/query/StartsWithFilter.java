package com.keithstric.query;

import javax.faces.component.UIComponent;

public class StartsWithFilter extends QueryFilter {

	public StartsWithFilter(String propertyName, String content) {
		super(propertyName, content);
	}

	@Override
	public boolean matches(UIComponent component) {
		boolean result = false;
		if (isCompatibleWith(component)) {
			Object propertyValue = getPropertyValue(component);
			if (propertyValue instanceof String && getContent() instanceof String) {
				result = ((String) propertyValue).toLowerCase().startsWith(((String) getContent()).toLowerCase());
			}
		}
		return result;
	}

}
