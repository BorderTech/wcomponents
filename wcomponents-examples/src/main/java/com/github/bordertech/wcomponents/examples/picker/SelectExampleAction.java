package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WebUtilities;

/**
 * An action used to select an example. Expects to be fired from a WComponent which has the set the event's action
 * object to the class to be launched. For example, a {@link WMenuItem} where the action object has been set using
 * {@link WMenuItem#setActionObject(java.io.Serializable) setActionObject}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class SelectExampleAction implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final ActionEvent event) {
		ExampleData data = (ExampleData) event.getActionObject();
		WComponent source = (WComponent) event.getSource();

		TreePicker picker = WebUtilities.getAncestorOfClass(TreePicker.class, source);
		picker.selectExample(data);
	}
}
