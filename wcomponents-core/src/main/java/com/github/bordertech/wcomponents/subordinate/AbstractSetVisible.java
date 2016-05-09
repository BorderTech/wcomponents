package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.SubordinateTarget;

/**
 * An "action function" that sets the `hidden` state on a WComponent or group of WComponents. A component which is
 * hidden is rendered but not <a href="https://html.spec.whatwg.org/multipage/dom.html#palpable-content-2">palpable</a>
 * in the UI.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractSetVisible extends AbstractAction {

	/**
	 * Creates a AbstractSetVisible action with the given target and value.
	 *
	 * @param target the component to make visible/invisible
	 * @param value the value to make visible/invisible
	 */
	public AbstractSetVisible(final SubordinateTarget target, final Boolean value) {
		super(target, value);
	}

	/**
	 * Apply the action against the target.
	 *
	 * @param target the target of the action
	 * @param value is the evaluated value.
	 */
	@Override
	protected void applyAction(final SubordinateTarget target, final Object value) {
		if (value instanceof Boolean) {
			boolean visible = ((Boolean) value);
			target.setValidate(visible);
			((AbstractWComponent) target).setHidden(!visible);
		}
	}
}
