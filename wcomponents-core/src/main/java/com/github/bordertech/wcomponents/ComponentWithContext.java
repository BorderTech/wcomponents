package com.github.bordertech.wcomponents;

import java.io.Serializable;

/**
 * Encapsulates a component along with a context. This is used to e.g. return lists of components which are being
 * repeated.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ComponentWithContext implements Serializable {

	/**
	 * The component.
	 */
	private WComponent component;

	/**
	 * The context which the component is in.
	 */
	private UIContext context;

	/**
	 * Creates a ComponentWithContext.
	 *
	 * @param component the component.
	 * @param context the context the component is in.
	 */
	public ComponentWithContext(final WComponent component, final UIContext context) {
		this.component = component;
		this.context = context;
	}

	/**
	 * @return Returns the component.
	 */
	public WComponent getComponent() {
		return component;
	}

	/**
	 * @param component The component to set.
	 */
	public void setComponent(final WComponent component) {
		this.component = component;
	}

	/**
	 * @return Returns the context.
	 */
	public UIContext getContext() {
		return context;
	}

	/**
	 * @param context The context to set.
	 */
	public void setContext(final UIContext context) {
		this.context = context;
	}
}
