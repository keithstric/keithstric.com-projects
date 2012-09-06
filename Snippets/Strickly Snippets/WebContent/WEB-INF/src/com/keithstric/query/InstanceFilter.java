package com.keithstric.query;

import javax.faces.component.UIComponent;

public class InstanceFilter extends QueryFilter {
	private Class klass;

	public InstanceFilter(Class klass) {
		setKlass(klass);
	}

	@Override
	public boolean matches(UIComponent component) {
		return getKlass().isAssignableFrom(component.getClass());
	}

	public void setKlass(Class klass) {
		this.klass = klass;
	}

	public Class getKlass() {
		return klass;
	}

}
