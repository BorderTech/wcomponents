package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import java.io.Serializable;

/**
 * This interface marks functions that make changes to the application, such as hiding parts of a page.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface Action extends Serializable {

	/**
	 * @return the target for this action.
	 */
	SubordinateTarget getTarget();

	/**
	 * Execute the action against the target.
	 */
	void execute();

	/**
	 * Determine the type of action.
	 *
	 * @return the action type.
	 */
	ActionType getActionType();

	/**
	 * An enumerated class for the types of actions.
	 */
	enum ActionType {
		/**
		 * Show the target.
		 */
		SHOW,
		/**
		 * Show the target in a group.
		 */
		SHOWIN,
		/**
		 * Hide the target.
		 */
		HIDE,
		/**
		 * Hide the target in a group.
		 */
		HIDEIN,
		/**
		 * Enable the target.
		 */
		ENABLE,
		/**
		 * Enable the target in a group.
		 */
		ENABLEIN,
		/**
		 * Disable the target.
		 */
		DISABLE,
		/**
		 * Disable the target in a group.
		 */
		DISABLEIN,
		/**
		 * Make the target optional.
		 */
		OPTIONAL,
		/**
		 * Make the target mandatory.
		 */
		MANDATORY
	}
}
