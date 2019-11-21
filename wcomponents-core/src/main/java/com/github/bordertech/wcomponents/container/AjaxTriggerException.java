package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.util.SystemException;

/**
 * An invalid AJAX trigger was provided on the request.
 * <p>
 * This exception usually occurs when a trigger has become not visible due to other AJAX activity on the page
 * </p>
 */
public class AjaxTriggerException extends SystemException {

	/**
	 * @param message the AJAX trigger message
	 */
	public AjaxTriggerException(final String message) {
		super(message);
	}

}
