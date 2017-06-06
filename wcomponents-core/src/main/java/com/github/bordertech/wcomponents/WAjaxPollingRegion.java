package com.github.bordertech.wcomponents;

/**
 * A specific purpose implementation of {@link WPanel} which provides a self-targeting "polling" control using
 * {@link WAjaxControl}. This is used to make recurring AJAX requests to the server until it is disabled.
 *
 * <p>
 * <strong>NOTE:</strong>
 * The polling region is a {@link WPanel} so can have layout, margin and type set. This <em>may</em> lead to unwanted
 * consequences as the first (and possibly only) content child of this WPanel will be a component with no UI artefact.
 * </p>
 *
 * <p>This component may be considered harmful.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @deprecated 1.2.0 This component is too abstract which leads to severe accessibility issues under common conditions and should have been an
 * implementation example. See
 * <a href="https://github.com/BorderTech/wcomponents/wiki/WAjaxPollingRegion">https://github.com/BorderTech/wcomponents/wiki/WAjaxPollingRegion</a>.
 */
public class WAjaxPollingRegion extends WPanel {

	/**
	 * The {@link WAjaxControl} control to perform polling.
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
