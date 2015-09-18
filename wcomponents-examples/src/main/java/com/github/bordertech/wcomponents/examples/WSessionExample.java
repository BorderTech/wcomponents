package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import javax.servlet.http.HttpSession;

/**
 * Demonstrate using the "ui:session" element.
 * <p>
 * If the theme has been setup to use the "ui:session" element, the theme will display a "warning" message to the user
 * that their session will soon expire. Once the session timeout has elapsed, an "expired" message will be displayed to
 * the user.
 * </p>
 * <p>
 * The timeout value is usually set to the session timeout {@link HttpSession#getMaxInactiveInterval()}, but to
 * demonstrate the messages, the timeout has been set to 180 seconds.
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
				.info("If \"ui:session\" is supported by the theme, wait 180 seconds to see a warning message and then a session expired message.");
		add(messages);

		WText txtSession = new WText() {
			@Override
			public String getText() {
				return "<ui:session timeout=\"180\" />";
			}
		};
		txtSession.setEncodeText(false);

		add(txtSession);
	}
}
