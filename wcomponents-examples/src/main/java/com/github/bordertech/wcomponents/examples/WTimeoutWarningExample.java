package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTimeoutWarning;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import javax.servlet.http.HttpSession;

/**
 * Demonstrate WTimeoutWarning
 * <p>
 * The UI will display a "warning" message to the user that their session will soon expire. Once the session timeout has
 * elapsed, an "expired" message will be displayed to the user.
 * </p>
 * <p>
 * The timeout value is usually set to the session timeout {@link HttpSession#getMaxInactiveInterval()}, but to
 * demonstrate the messages, the timeout has been set to 120 seconds.
 * </p>
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTimeoutWarningExample extends WContainer {

	private static final int TIMEOUT_PERIOD = 120;

	private static final int WARNING_PERIOD = 30;

	/**
	 * Construct example.
	 */
	public WTimeoutWarningExample() {
		add(new WTimeoutWarning(TIMEOUT_PERIOD, WARNING_PERIOD));

		add(new ExplanatoryText(
				"This is a demonstration of the 'session timeout' warning. It will display a warning message after 90 seconds and a session timeout message after 120 seconds. It does not actually end your session."));
	}

}
