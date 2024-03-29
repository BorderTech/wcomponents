package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;

/**
 * Demonstrate using the "wc-session" element.
 * <p>
 * If the theme has been setup to use the "wc-session" element, the theme will
 * display a "warning" message to the user that their session will soon expire.
 * Once the session timeout has elapsed, an "expired" message will be displayed
 * to the user.
 * </p>
 * <p>
 * The timeout value is usually set to the session timeout, but to demonstrate
 * the messages, the timeout has been set to 180 seconds.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSessionExample extends WPanel {

	/**
	 * Construct example.
	 */
	public WSessionExample() {
		WMessages messages = new WMessages(true);
		messages
				.info("If \"wc-session\" is supported by the theme, wait 180 seconds to see a warning message and then a session expired message.");
		add(messages);

		WText txtSession = new WText() {
			@Override
			public String getText() {
				return "<wc-session timeout=\"180\" />";
			}
		};
		txtSession.setEncodeText(false);

		add(txtSession);
	}
}
