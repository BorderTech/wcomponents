package com.github.bordertech.wcomponents;

/**
 * This class assists in the unit testing of WComponents that support the triggering of Actions. This class simply
 * records details passed to the execute method.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class TestAction implements Action {

	/**
	 * The last event that was passed into the {@link #execute(ActionEvent)}.
	 */
	private ActionEvent latestEvent;

	/**
	 * Invoked when the action occurs. This implementation just records the event.
	 *
	 * @param event details about the event that occured.
	 */
	@Override
	public void execute(final ActionEvent event) {
		latestEvent = event;
	}

	/**
	 * Resets this TestAction so that it thinks it has never been triggered.
	 */
	public void reset() {
		latestEvent = null;
	}

	/**
	 * @return true if this TestAction has been triggered (execute method called) since the last call to reset().
	 */
	public boolean wasTriggered() {
		return latestEvent != null;
	}

	/**
	 * @return the last action event object recorded by this TestAction.
	 */
	public ActionEvent getLatestEvent() {
		return latestEvent;
	}
}
