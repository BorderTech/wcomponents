package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import java.text.MessageFormat;

/**
 * <p>
 * This component is a specialised version of a {@link WButton} that provides additional client-side functionality
 * commonly associated with a "cancel" button.
 * </p>
 * <p>
 * When a user presses the button, it displays a confirmation prompt before posting the form to the server.
 * </p>
 *
 * <pre>
 * WConfirmationButton button = new WConfirmationButton(&quot;Delete everything&quot;);
 * button.setMessage(&quot;Are you really sure you want to delete everything?&quot;);
 *
 * // Set an action to run when the button is clicked.
 * button.setAction(new Action()
 * {
 *     public void execute(ActionEvent event)
 *     {
 *         // (Code to delete everything goes here)
 *     }
 * });
 * </pre>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WConfirmationButton extends WButton {

	/**
	 * Creates an empty confirmation button. An image or text must be provided separately.
	 */
	public WConfirmationButton() {
		// Default confirmation message
		setMessage(InternalMessages.DEFAULT_CONFIRMATION_PROMPT);
	}

	/**
	 * Constructor. Sets the button text.
	 *
	 * @param text the button text
	 */
	public WConfirmationButton(final String text) {
		super(text);
		// Default confirmation message
		setMessage(InternalMessages.DEFAULT_CONFIRMATION_PROMPT);
	}

	/**
	 * Constructor. Set the button text and the accesskey. The accesskey in combination with the alt key will activate
	 * the button.
	 *
	 * @param text the button text, using {@link MessageFormat} syntax.
	 * @param accessKey The access key.
	 */
	public WConfirmationButton(final String text, final char accessKey) {
		super(text);
		// Access key
		setAccessKey(accessKey);
		// Default confirmation message
		setMessage(InternalMessages.DEFAULT_CONFIRMATION_PROMPT);
	}

}
