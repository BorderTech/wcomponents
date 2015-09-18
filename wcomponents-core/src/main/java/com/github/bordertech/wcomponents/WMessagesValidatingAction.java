package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.ValidatingAction;

/**
 * WMessagesValidatingAction - Convenience class that validates a component and uses WMessages to display the results.
 * If the component is not in a valid state, the action is not executed.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class WMessagesValidatingAction extends ValidatingAction {

	/**
	 * Creates a WMessagesValidatingAction for the given component.
	 *
	 * @param component the component to create the action for.
	 */
	public WMessagesValidatingAction(final WComponent component) {
		super(WMessages.getInstance(component).getValidationErrors(), component);
	}
}
