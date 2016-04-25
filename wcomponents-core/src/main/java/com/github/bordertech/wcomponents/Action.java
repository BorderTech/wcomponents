package com.github.bordertech.wcomponents;

import java.io.Serializable;

/**
 * WComponents that trigger a form submit can be given an Action which will be executed as part of the submit. The
 * submit first causes the entire WComponent tree to be updated via their handleRequest methods. Next, the action will
 * be executed using the WComponent invokeLater concept.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public interface Action extends Serializable {

	/**
	 * This method is invoked when an action occurs.
	 *
	 * @param event details about the event that occured.
	 */
	void execute(ActionEvent event);
}
