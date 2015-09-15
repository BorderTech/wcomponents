package com.github.bordertech.wcomponents;

import java.io.IOException;

/**
 * Allows an error code and message to be sent on the response.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ErrorCodeEscape extends ActionEscape {

	/**
	 * Error code to send in the response.
	 */
	private final int code;

	/**
	 * Error message to send on the response.
	 */
	private final String message;

	/**
	 * @param code the error code to send in the response
	 * @param message the error message to send on the response
	 */
	public ErrorCodeEscape(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void escape() throws IOException {
		getResponse().sendError(code, message);
	}

}
