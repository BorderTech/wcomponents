package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextField;

/**
 * <p>
 * This component demonstrates how to add some simple control logic, by overriding the handleRequest method.
 * </p><p>
 * The java code placed in the handleRequest method can look at, and manipulate, the values stored in the various child
 * components.
 * </p><p>
 * A word of warning. When using the handleRequest technique, it is important to remember how the request/response
 * sequence is processed. The serviceRequest method performs a depth first collation of all visible components and then
 * calls handleRequest on each component in the list. This means that only child components of the component you are
 * overriding handleRequest on will have had a chance to update themselves for the current request. Don't try accessing
 * data in components higher up in the component tree.
 * </p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class TextDuplicatorHandleRequestImpl extends WContainer {

	/**
	 * The text field containing the text to duplicate.
	 */
	private final WTextField textFld = new WTextField();
	/**
	 * A button to duplicate the text in the text field.
	 */
	private final WButton dupBtn = new WButton("Duplicate");
	/**
	 * A button to clear the text in the text field.
	 */
	private final WButton clrBtn = new WButton("Clear");

	/**
	 * Creates a TextDuplicator_HandleRequestImpl with the default label text.
	 */
	public TextDuplicatorHandleRequestImpl() {
		this("Text Duplicator (handleRequest impl)");
	}

	/**
	 * Creates a TextDuplicator_HandleRequestImpl with the specified label text.
	 *
	 * @param name the name label text.
	 */
	public TextDuplicatorHandleRequestImpl(final String name) {
		add(new WLabel(name, textFld));
		add(textFld);
		add(dupBtn);
		add(clrBtn);
	}

	/**
	 * Control logic is implemented in the handleRequest method. This example checks if a button has been pressed, and
	 * duplicates/clears the text accordingly.
	 *
	 * @param request the current client request.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (clrBtn.isPressed()) {
			textFld.setText("");
		} else if (dupBtn.isPressed()) {
			// Get the text entered by the user.
			String text = textFld.getText();

			// Now duplicate it.
			textFld.setText(text + text);
		}
	}
}
