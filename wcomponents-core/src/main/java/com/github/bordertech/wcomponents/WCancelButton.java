package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;

/**
 * <p>
 * This component is a specialised version of a {@link WButton} that provides additional client-side functionality
 * commonly associated with a "cancel" button.
 * </p>
 * <p>
 * Themes will typically display a confirmation prompt if there any "unsaved changes" when the button is clicked. The
 * following are considered as unsaved changes:
 * </p>
 * <ul>
 * <li>Any data which has been entered on the client and has not been sent to the server.</li>
 * <li>Programmatically calling {@link #setUnsavedChanges(boolean) setUnsavedChanges(uic, true)} on the cancel
 * button.</li>
 * <li>Programmatically calling {@link WApplication#setUnsavedChanges(boolean) setUnsavedChanges(uic, true)} on the root
 * {@link WApplication}.</li>
 * </ul>
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class WCancelButton extends WButton {

	/**
	 * Creates a WCancelButton with the default text.
	 */
	public WCancelButton() {
		super(InternalMessages.DEFAULT_CANCEL_BUTTON_TEXT);
		setCancel(true);
	}

	/**
	 * Creates a WCancelButton with the specified text.
	 *
	 * @param text the button text
	 */
	public WCancelButton(final String text) {
		super(text);
		setCancel(true);
	}

	/**
	 * Creates a WCancelButton with the specified text and access key.
	 *
	 * @param text the button text.
	 * @param accessKey the access key.
	 */
	public WCancelButton(final String text, final char accessKey) {
		super(text, accessKey);
		setCancel(true);
	}
}
