package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.util.SystemException;

/**
 * An invalid Targetable ID was provided on the request.
 */
public class TargetableIdException extends SystemException {

	/**
	 * @param message the Targetable ID error message
	 */
	public TargetableIdException(final String message) {
		super(message);
	}

}
