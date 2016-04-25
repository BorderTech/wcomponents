package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A convenience class to raise integrity issues. An integrity issue is usually issued on misconfiguration of a
 * WComponent.</p>
 *
 * <p>
 * If the <code>sfp.web.integrity.terminate.mode</code> parameter is set, an {@link IntegrityException} will be thrown,
 * otherwise a warning message will be logged.<p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public final class Integrity {

	/**
	 * Prevent instantiation of this class.
	 */
	private Integrity() {
	}

	/**
	 * Raises an integrity issue.
	 *
	 * @param comp the component to issue the exception for.
	 * @param message the integrity message to issue.
	 */
	public static void issue(final WComponent comp, final String message) {
		String debugMessage = message + ' ' + comp;

		if (Config.getInstance().
				getBoolean("bordertech.wcomponents.integrity.terminate.mode", false)) {
			throw new IntegrityException(debugMessage);
		} else {
			LogFactory.getLog(Integrity.class).warn(debugMessage);
		}
	}
}
