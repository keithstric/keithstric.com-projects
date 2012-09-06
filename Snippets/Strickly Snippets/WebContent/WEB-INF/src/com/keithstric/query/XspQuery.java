package com.keithstric.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.context.FacesContextEx;

public class XspQuery extends ArrayList<UIComponent> {
	private static final long serialVersionUID = 1L;
	private List<QueryFilter> filters;

	public XspQuery() {
	}

	public XspQuery addContains(String propertyName, Object content) {
		getFilters().add(new ContainsFilter(propertyName, content));
		return this;
	}

	public XspQuery addEndsWith(String propertyName, String content) {
		getFilters().add(new EndsWithFilter(propertyName, content));
		return this;
	}

	public XspQuery addEquals(String propertyName, Object content) {
		getFilters().add(new EqualsFilter(propertyName, content));
		return this;
	}

	public XspQuery addInstanceOf(Class<? extends UIComponent> klass) {
		getFilters().add(new InstanceFilter(klass));
		return this;
	}

	public XspQuery addStartsWith(String propertyName, String content) {
		getFilters().add(new StartsWithFilter(propertyName, content));
		return this;
	}

	public UIComponent byId(String id) {
		return byId(id, null);
	}

	public UIComponent byId(String id, UIComponent root) {
		UIComponent result = null;
		addEquals("id", id);
		List<UIComponent> matches = locate(root);
		if (matches.size() > 0) {
			result = matches.get(0);
		}
		return result;
	}

	public List<QueryFilter> getFilters() {
		if (filters == null) {
			filters = new ArrayList<QueryFilter>();
		}
		return filters;
	}

	public static String getPageName() {
		return getPageName((UIComponent) null);
	}

	public static String getPageName(UIComponent component) {
		if (component == null) {
			component = FacesContext.getCurrentInstance().getViewRoot();
		}
		UIComponent root = locatePageRoot(component);
		if (root instanceof UIViewRoot) {
			return FacesContextEx.getCurrentInstance().getExternalContext().getRequestServletPath();
		} else if (root instanceof UIIncludeComposite) {
			UIIncludeComposite cc = (UIIncludeComposite) root;
			return cc.getPageName();
		}
		return null;
	}

	private static UIComponent locatePageRoot(UIComponent component) {
		if (component instanceof UIViewRoot || component instanceof UIIncludeComposite) {
			return component;
		}
		return locatePageRoot(component.getParent());
	}

	public List<UIComponent> locate() {
		return locate((UIComponent) null);
	}

	public List<UIComponent> locate(UIComponent root) {
		if (root == null) {
			root = FacesContext.getCurrentInstance().getViewRoot();
		}

		safeVisitTree(new QueryVisitContext(), new QueryVisitCallback(), root);
		return this;
	}

	public UIInput locateField(String id) {
		return locateField(id, null);
	}

	public UIInput locateField(String id, UIComponent root) {
		UIInput result = null;
		addEquals("id", id);
		List<UIInput> inputs = locateInputs(root);
		if (inputs.size() > 0) {
			result = inputs.get(0);
		}
		return result;
	}

	public List<UIInput> locateInputs() {
		return locateInputs(null);
	}

	public List<UIInput> locateInputs(UIComponent root) {
		List<UIInput> inputs = new ArrayList<UIInput>();
		addInstanceOf(UIInput.class);
		List<UIComponent> components = locate(null);
		for (UIComponent component : components) {
			inputs.add((UIInput) component);
		}
		return inputs;
	}

	public static boolean safeVisitTree(VisitContext context, VisitCallback callback, UIComponent component) {
		if (!(isVisitable(context, component))) {
			return false;
		}
		VisitResult res = context.invokeVisitCallback(component, callback);
		if (res == VisitResult.COMPLETE) {
			return true;
		}
		if ((res == VisitResult.ACCEPT) && (((component.getChildCount() > 0) || (component.getFacetCount() > 0)))) {
			for (Iterator it = component.getFacetsAndChildren(); it.hasNext();) {
				Object o = it.next();
				if (o instanceof UIComponent) {
					UIComponent c = (UIComponent) o;
					if (safeVisitTree(context, callback, c)) {
						return true;
					}
				} else {
					return false;
				}
			}
		}
		return false;
	}

	private static boolean isVisitable(VisitContext context, UIComponent component) {
		Collection<VisitHint> hints = context.getHints();
		if ((hints.contains(VisitHint.SKIP_TRANSIENT)) && (component.isTransient())) {
			return false;
		}
		return ((!(hints.contains(VisitHint.SKIP_UNRENDERED))) || (component.isRendered()));
	}

	private class QueryVisitCallback implements VisitCallback {

		public VisitResult visit(VisitContext context, UIComponent component) {
			boolean valid = true;
			for (QueryFilter filter : getFilters()) {
				if (!filter.matches(component)) {
					valid = false;
					break;
				}
			}
			if (valid) {
				add(component);
			}
			return VisitResult.ACCEPT;
		}

	}

	private class QueryVisitContext extends VisitContext {

		@Override
		public FacesContext getFacesContext() {
			return FacesContextEx.getCurrentInstance();
		}

		@Override
		public Set<VisitHint> getHints() {
			Set<VisitHint> hints = new HashSet<VisitHint>();
			hints.add(VisitHint.SKIP_TRANSIENT);
			return hints;
		}

		@Override
		public Collection<String> getIdsToVisit() {
			return null;
		}

		@Override
		public Collection<String> getSubtreeIdsToVisit(UIComponent component) {
			return null;
		}

		@Override
		public VisitResult invokeVisitCallback(UIComponent component, VisitCallback callback) {
			return callback.visit(this, component);
		}

	}

	public static class ComponentTreeMassage {

		public static void removeComponent(UIComponent component) {
			component.getParent().getChildren().remove(component);
		}

		public static void removeComponent(String componentId) {
			XspQuery domQuery = new XspQuery();
			UIComponent component = domQuery.byId(componentId);
			if (component != null) {
				component.getParent().getChildren().remove(component);
			} else {
			}
		}

		/**
		 * CAUTION Component Genocide ahead Remove all the components of this
		 * type
		 */
		public static void removeComponent(Class<? extends UIComponent> componentClass) {
			XspQuery domQuery = new XspQuery();
			List<UIComponent> components = domQuery.addInstanceOf(componentClass).locate();
			if (!components.isEmpty()) {
				for (UIComponent component : components) {
					component.getParent().getChildren().remove(component);
				}
			}
		}

		/**
		 * CAUTION Component Genocide ahead Remove all the components of this
		 * type that are children of the parentComponent
		 */
		public static void removeComponent(Class<? extends UIComponent> componentClass, UIComponent parentComponent) {
			XspQuery domQuery = new XspQuery();
			List<UIComponent> components = domQuery.addInstanceOf(componentClass).locate(parentComponent);
			if (!components.isEmpty()) {
				for (UIComponent component : components) {
					component.getParent().getChildren().remove(component);
				}
			}
		}

		/**
		 * CAUTION Component Genocide ahead Remove all the components of this
		 * type that are children of the parentComponent
		 */
		public static void removeComponent(Class<? extends UIComponent> componentClass, String parentComponentId) {
			XspQuery domQuery = new XspQuery();
			UIComponent parentComponent = domQuery.byId(parentComponentId);
			List<UIComponent> components = domQuery.addInstanceOf(componentClass).locate(parentComponent);
			if (!components.isEmpty()) {
				for (UIComponent component : components) {
					component.getParent().getChildren().remove(component);
				}
			}
		}

		public static void addFirst(UIComponent component) {
			component.getParent().getChildren().add(0, component);
		}

		public static void addLast(UIComponent component) {
			component.getParent().getChildren().add(component);
		}

		public static void addBefore(UIComponent component, UIComponent beforeComp) {
			int beforeCompIndex = beforeComp.getParent().getChildren().indexOf(beforeComp);
			beforeComp.getParent().getChildren().add(beforeCompIndex, component);
		}

		public static void addBefore(UIComponent component, String beforeCompId) {
			XspQuery domQuery = new XspQuery();
			UIComponent beforeComp = domQuery.byId(beforeCompId);
			if (beforeComp != null) {
				int beforeCompIndex = beforeComp.getParent().getChildren().indexOf(beforeComp);
				beforeComp.getParent().getChildren().add(beforeCompIndex, component);
			}
		}

		public static void addAfter(UIComponent component, UIComponent afterComp) {
			int afterCompIndex = afterComp.getParent().getChildren().indexOf(afterComp) + 1;
			afterComp.getParent().getChildren().add(afterCompIndex, component);
		}

		public static void addAfter(UIComponent component, String afterCompId) {
			XspQuery domQuery = new XspQuery();
			UIComponent afterComp = domQuery.byId(afterCompId);
			if (afterComp != null) {
				int afterCompIndex = afterComp.getParent().getChildren().indexOf(afterComp) + 1;
				afterComp.getParent().getChildren().add(afterCompIndex, component);
			}
		}

		public static void addToLocation(UIComponent component, Integer location) {
			List<UIComponent> siblings = component.getParent().getChildren();
			if (!siblings.isEmpty()) {
				siblings.add(location, component);
			}
		}

		public static void moveToFirst(UIComponent component) {
			List<UIComponent> siblings = component.getParent().getChildren();
			if (!siblings.isEmpty()) {
				int movedCompIndex = siblings.indexOf(component);
				UIComponent movedComp = siblings.remove(movedCompIndex);
				siblings.add(0, movedComp);
			}
		}

		public static void moveToLast(UIComponent component) {
			List<UIComponent> siblings = component.getParent().getChildren();
			if (!siblings.isEmpty()) {
				int movedCompIndex = siblings.indexOf(component);
				UIComponent movedComp = siblings.remove(movedCompIndex);
				siblings.add(movedComp);
			}
		}

		public static void moveToLocation(UIComponent component, Integer location) {
			List<UIComponent> siblings = component.getParent().getChildren();
			if (!siblings.isEmpty()) {
				int movedCompIndex = siblings.indexOf(component);
				UIComponent movedComp = siblings.remove(movedCompIndex);
				siblings.add(location, movedComp);
			}
		}

		public static void moveToBefore(UIComponent component, UIComponent beforeComp) {
			List<UIComponent> beforeCompSiblings = beforeComp.getParent().getChildren();
			List<UIComponent> compSiblings = component.getParent().getChildren();
			if (!beforeCompSiblings.isEmpty()) {
				int movedCompIndex = compSiblings.indexOf(component);
				UIComponent movedComp = compSiblings.remove(movedCompIndex);
				int beforeCompIndex = beforeCompSiblings.indexOf(beforeComp);
				beforeCompSiblings.add(beforeCompIndex, movedComp);
			}
		}

		public static void moveToBefore(UIComponent component, String beforeCompId) {
			XspQuery domQuery = new XspQuery();
			UIComponent beforeComp = domQuery.byId(beforeCompId);
			if (beforeComp != null) {
				List<UIComponent> beforeCompSiblings = beforeComp.getParent().getChildren();
				List<UIComponent> compSiblings = component.getParent().getChildren();
				if (!beforeCompSiblings.isEmpty()) {
					int movedCompIndex = compSiblings.indexOf(component);
					UIComponent movedComp = compSiblings.remove(movedCompIndex);
					int beforeCompIndex = beforeCompSiblings.indexOf(beforeComp);
					beforeCompSiblings.add(beforeCompIndex, movedComp);
				}
			}
		}

		public static void moveToAfter(UIComponent component, UIComponent afterComp) {
			List<UIComponent> afterCompSiblings = afterComp.getParent().getChildren();
			List<UIComponent> compSiblings = component.getParent().getChildren();
			if (!afterCompSiblings.isEmpty()) {
				int movedCompIndex = compSiblings.indexOf(component);
				UIComponent movedComp = compSiblings.remove(movedCompIndex);
				int afterCompIndex = afterCompSiblings.indexOf(afterComp) + 1;
				afterCompSiblings.add(afterCompIndex, movedComp);
			}
		}

		public static void moveToAfter(UIComponent component, String afterCompId) {
			XspQuery domQuery = new XspQuery();
			UIComponent afterComp = domQuery.byId(afterCompId);
			if (afterComp != null) {
				List<UIComponent> afterCompSiblings = afterComp.getParent().getChildren();
				List<UIComponent> compSiblings = component.getParent().getChildren();
				if (!afterCompSiblings.isEmpty()) {
					int movedCompIndex = compSiblings.indexOf(component);
					UIComponent movedComp = compSiblings.remove(movedCompIndex);
					int afterCompIndex = afterCompSiblings.indexOf(afterComp) + 1;
					afterCompSiblings.add(afterCompIndex, movedComp);
				}
			}
		}
	}

}