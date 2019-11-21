package com.github.bordertech.wcomponents;

import java.io.IOException;

/**
 * Allows an error code and message to be sent on the response.
 * <p>
 * Projects can check the original exception to see what caused the error code.
 * </p>
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
	 * @param code the error code to send in the response
	 * @param message the error message to send on the response
	 */
	public ErrorCodeEscape(final int code, final String message) {
		this(code, message, null);
	}

	/**
	 * @param code the error code to send in the response
	 * @param message the error message to send on the response
	 * @param cause the original cause
	 */
	public ErrorCodeEscape(final int code, final String message, final Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	@Override
	public void escape() throws IOException {
		getResponse().sendError(code, getMessage());
	}

	/**
	 * @return the error code
	 */
	public int getCode() {
		return code;
	}

}
