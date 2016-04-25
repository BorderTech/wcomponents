package com.github.bordertech.wcomponents.examples.menu;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WText;

/**
 * An action which updates the selectedMenuText field with the text of the menu item which was selected.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class ExampleMenuAction implements Action {

	/**
	 * Displays the text of the selected menu item.
	 */
	private final WText selectedMenuText;

	/**
	 * Creates an ExampleMenuAction.
	 *
	 * @param selectedMenuText the WText to display the selected menu item.
	 */
	public ExampleMenuAction(final WText selectedMenuText) {
		this.selectedMenuText = selectedMenuText;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final ActionEvent event) {
		if (event.getActionObject() == null) {
			throw new IllegalStateException("Missing action object");
		} else {
			selectedMenuText.setText(event.getActionObject().toString());
		}
	}
}
