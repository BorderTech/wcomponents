package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTimeoutWarning;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;

/**
 * Example of WTimeoutWarning.
 */
public class WTimeoutWarningDefaultExample extends WContainer {

	/**
	 * Construct example.
	 */
	public WTimeoutWarningDefaultExample() {
		add(new ExplanatoryText(
				"This is a demonstration of the 'session timeout' warning. It will display a session timeout message based on the default timeout for the session. A warning message will be displayed before the session ends. It does not actually end your session."));
		add(new WTimeoutWarning());
	}

}
