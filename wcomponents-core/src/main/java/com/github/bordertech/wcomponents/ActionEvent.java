package com.github.bordertech.wcomponents;

import java.util.EventObject;

/**
 * The object that indicates that a component action occured. It contains information about the event, and is passed to
 * the Action object that will handle the event.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class ActionEvent extends EventObject {

	/**
	 * A string that specifies a command associated with the event.
	 */
	private final String actionCommand;
	/**
	 * A data object that contains information related to the event.
	 */
	private final Object actionObject;

	/**
	 * @param source the object that originated the event. (Normally a WButton)
	 * @param command a string that may specify a command associated with the event. (Normally the "actionCommand" from
	 * the source WButton)
	 */
	public ActionEvent(final Object source, final String command) {
		this(source, command, null);
	}

	/**
	 *
	 * @param source the object that originated the event. (Normally a WButton)
	 * @param command a string that may specify a command associated with the event. (Normally the "actionCommand" from
	 * the source WButton)
	 * @param actionObject a data object that may contain information related to the event. (Normally the "actionObject"
	 * from the source WButton)
	 */
	public ActionEvent(final Object source, final String command, final Object actionObject) {
		super(source);
		this.actionCommand = command;
		this.actionObject = actionObject;
	}

	/**
	 * @return The string that specifies a command associated with the event, if set
	 */
	public String getActionCommand() {
		return actionCommand;
	}

	/**
	 * @return The data object that contains information related to the event, if set.
	 */
	public Object getActionObject() {
		return actionObject;
	}
}
