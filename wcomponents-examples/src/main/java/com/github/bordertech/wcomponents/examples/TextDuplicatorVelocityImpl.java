package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextField;

/**
 * Same idea as {@link TextDuplicator}, but with a velocity template to pretty things up.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class TextDuplicatorVelocityImpl extends WContainer {

	/**
	 * The text field which the actions modify the state of.
	 */
	private final WTextField textFld = new WTextField();

	/**
	 * Creates a TextDuplicator_VelocityImpl with the default label text.
	 */
	public TextDuplicatorVelocityImpl() {
		this("Pretty Duplicator");
	}

	/**
	 * Creates a TextDuplicator_VelocityImpl with the specified label text.
	 *
	 * @param name the name label text
	 */
	public TextDuplicatorVelocityImpl(final String name) {
		// This is the line of code that associates this component with a
		// velocity template.  A simple mapping is applied to the given class
		// to derive the name of a velocity template.
		// In this case, com.github.bordertech.wcomponents.examples.TextDuplicatorPretty
		// maps to the template com/github/bordertech/wcomponents/examples/TextDuplicator_VelocityImpl.vm
		setTemplate(TextDuplicatorVelocityImpl.class);

		WButton dupBtn = new WButton("Duplicate");
		WButton clrBtn = new WButton("Clear");

		add(new WLabel(name, textFld), "label");
		add(textFld, "text");
		add(dupBtn, "duplicateButton");
		add(clrBtn, "clearButton");

		dupBtn.setAction(new DuplicateAction());
		clrBtn.setAction(new ClearAction());
	}

	/**
	 * This action duplicates the text in the text field.
	 *
	 * @author Martin Shevchenko
	 */
	private final class DuplicateAction implements Action {

		/**
		 * Executes the action which duplicates the text.
		 *
		 * @param event details about the event that occured.
		 */
		@Override
		public void execute(final ActionEvent event) {
			// Get the text entered by the user.
			String text = textFld.getText();

			// Now duplicate it.
			textFld.setText(text + text);
		}
	}

	/**
	 * This action clears out the text in the text field.
	 *
	 * @author Martin Shevchenko
	 */
	private final class ClearAction implements Action {

		/**
		 * Executes the action which clears the text.
		 *
		 * @param event details about the event that occured.
		 */
		@Override
		public void execute(final ActionEvent event) {
			textFld.setText("");
		}
	}
}
