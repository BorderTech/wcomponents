package com.github.bordertech.wcomponents;

/**
 * A "polling" AJAX control.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAjaxPollingRegion extends WPanel {

	/**
	 * The ajax control to perform polling.
	 */
	private final WAjaxControl control;

	/**
	 * Creates a PollingRegion.
	 *
	 * @param pollingInterval the polling interval, in milliseconds.
	 */
	public WAjaxPollingRegion(final int pollingInterval) {
		control = new WAjaxControl(null, this);
		control.setDelay(pollingInterval);
		control.setVisible(false);
		add(control);
	}

	/**
	 * Enables polling.
	 */
	public void enablePoll() {
		control.setVisible(true);
	}

	/**
	 * Disables polling.
	 */
	public void disablePoll() {
		control.setVisible(false);
	}
}
