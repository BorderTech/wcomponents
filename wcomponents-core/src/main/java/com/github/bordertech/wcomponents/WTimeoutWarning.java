package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;

/**
 * WTimeoutWarning provides a mechanism to pass a session timeout period in seconds to the client. This can then be used
 * to indicate to the user when their session is about to expire.
 * <p>
 * This has two purposes: firstly it provides a means to minimise data loss by users by indicating that they should
 * submit their work before the session expires; and secondly it is a WCAG requirement that users be given a warning of
 * the end of a time-limited process and a means to extend that limit.
 * </p>
 * <p>
 * If the timeout period is "zero", then use the http session timeout interval will be used. If a user's session does
 * not expire then an existing WTimeoutWarning can have its timeout set to -1. This will prevent painting the
 * WTimeoutWarning. This value is the equivalent of a http session time out value of -1 which indicates that the session
 * does not time out.
 * </p>
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTimeoutWarning extends AbstractWComponent {

	/**
	 * The number of seconds before timeout in which a warning is shown to the user. The default of 20 is the smallest
	 * amount of time (in seconds) a warning can be set to according to the WCAG 2.0
	 */
	private static final int MINIMUM_WARNING = 20;

	/**
	 * The default warning period (in seconds).
	 */
	private static final int DEFAULT_WARNING_PERIOD = Config.getInstance()
			.getInt("bordertech.wcomponents.timeoutWarning.warningPeriod", 300);

	/**
	 * The default timeout period (in seconds). If the value is "zero", then use the http session timeout value.
	 */
	private static final int DEFAULT_TIMEOUT_PERIOD = Config.getInstance()
			.getInt("bordertech.wcomponents.timeoutWarning.timeoutPeriod", 0);

	/**
	 * Creates a WTimeoutWarning using default timeout and warning settings.
	 */
	public WTimeoutWarning() {
		// use the defaults for timeout and warning
	}

	/**
	 * Creates a timeout indicator with both a timeout period and warning period
	 * <p>
	 * If the timeout period is "zero", then use the http session timeout interval will be used.
	 * </p>
	 *
	 * @param timeoutPeriod The timeout in seconds. This should be slightly less than the shortest timeout period in the
	 * entire application stack, not just the web server session timeout.
	 * @param warningPeriod The time in seconds before the time out occurs in which the warning indicator should show.
	 * There is a minimum for this which is determined by the Web Content Accessibility Guidelines 2.0.
	 */
	public WTimeoutWarning(final int timeoutPeriod, final int warningPeriod) {
		setTimeoutPeriod(timeoutPeriod);
		setWarningPeriod(warningPeriod);
	}

	/**
	 * Creates a timeout indicator with a timeout period and uses the default warning period.
	 * <p>
	 * If the timeout period is "zero", then use the http session timeout interval will be used.
	 * </p>
	 *
	 * @param timeoutPeriod The timeout in seconds.
	 */
	public WTimeoutWarning(final int timeoutPeriod) {
		setTimeoutPeriod(timeoutPeriod);
	}

	/**
	 * Set a timeout indicator for a specified number of seconds.
	 * <p>
	 * If the timeout period is "zero", then the session timeout interval will be used.
	 * </p>
	 *
	 * @param timeoutPeriod the timeout in seconds.
	 */
	public void setTimeoutPeriod(final int timeoutPeriod) {
		getOrCreateComponentModel().timeoutPeriod = timeoutPeriod;
	}

	/**
	 * Set a pre-timeout warning to a specified number of seconds. This should be no less than the WCAG minimum of 20.
	 * <p>
	 * Setting the warning period to exactly ZERO (0) allows the warning time to be determined in the client layer.
	 * </p>
	 *
	 * @param warningPeriod The timeout warning will show this many seconds before the timeout as indicated by the
	 * timeoutPeriod.
	 */
	public void setWarningPeriod(final int warningPeriod) {
		if (0 != warningPeriod && warningPeriod < MINIMUM_WARNING) {
			throw new IllegalArgumentException("Warning period must be at least " + String.valueOf(
					MINIMUM_WARNING));
		}
		getOrCreateComponentModel().warningPeriod = warningPeriod;
	}

	/**
	 * Get the indicated timeout period.
	 *
	 * @return The timeout period in seconds.
	 */
	public int getTimeoutPeriod() {
		return getComponentModel().timeoutPeriod;
	}

	/**
	 * Get the warning period.
	 *
	 * @return The warning period in seconds.
	 */
	public int getWarningPeriod() {
		return getComponentModel().warningPeriod;
	}

	/**
	 * Initialise by setting the timeout to the HTTPSession maxInactiveInterval if the timeoutPeriod has not yet been
	 * set. {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			// If the timeout period is "zero", then use the session timeout interval.
			if (getTimeoutPeriod() == 0) {
				int max = request.getMaxInactiveInterval();
				this.setTimeoutPeriod(max);
			}
			setInitialised(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TimeoutWarningModel newComponentModel() {
		return new TimeoutWarningModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TimeoutWarningModel getComponentModel() {
		return (TimeoutWarningModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TimeoutWarningModel getOrCreateComponentModel() {
		return (TimeoutWarningModel) super.getOrCreateComponentModel();
	}

	/**
	 * Extends ComponentModel to provide timeout and warning periods to be set.
	 */
	public static class TimeoutWarningModel extends ComponentModel {

		/**
		 * The session timeout in seconds.
		 */
		private int timeoutPeriod = DEFAULT_TIMEOUT_PERIOD;

		/**
		 * The warning period in seconds. If this is set it must be at least 20.
		 */
		private int warningPeriod = DEFAULT_WARNING_PERIOD;
	}
}
