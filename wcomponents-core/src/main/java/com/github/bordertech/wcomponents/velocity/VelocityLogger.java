package com.github.bordertech.wcomponents.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 * Allows the velocity engine to use the commons logging used by WComponents.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class VelocityLogger implements LogSystem {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(VelocityLogger.class);

	/**
	 * @param rsvc the velocity runtime services
	 * @throws Exception if problem configuring logger
	 */
	@Override
	public void init(final RuntimeServices rsvc) throws Exception {
		// Do no extra configuration
	}

	/**
	 * Log velocity engine messages.
	 *
	 * @param level severity level
	 * @param message complete error message
	 */
	@Override
	public void logVelocityMessage(final int level, final String message) {
		switch (level) {
			case LogSystem.WARN_ID:
				LOG.warn(message);
				break;
			case LogSystem.INFO_ID:
				LOG.info(message);
				break;
			case LogSystem.DEBUG_ID:
				LOG.debug(message);
				break;
			case LogSystem.ERROR_ID:
				LOG.error(message);
				break;
			default:
				LOG.debug(message);
				break;
		}
	}

}
