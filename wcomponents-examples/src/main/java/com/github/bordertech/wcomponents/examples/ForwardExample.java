package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTextField;

/**
 * This example demonstrates the use of the "forward" feature. Enter a URL in the entry field and then press the Forward
 * button to go to that url.
 *
 * @author Martin Shevchenko
 * @since 14/02/2008
 */
public class ForwardExample extends WContainer {

	/**
	 * Creates a ForwardExample.
	 */
	public ForwardExample() {
		final WTextField urlField = new WTextField();
		urlField.setText("http://www.google.com.au/");
		urlField.setColumns(60);
		add(urlField, "urlField");

		WButton forwardBtn = new WButton("Forward");
		forwardBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				forward(urlField.getText());
			}
		});

		add(forwardBtn, "forwardBtn");
	}
}
