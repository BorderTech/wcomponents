package com.github.bordertech.wcomponents;

import java.io.IOException;

/**
 * This escape is thrown to forward the browser to a different URL rather than rendering the UI.
 *
 * @author Martin Shevchenko
 */
public class ForwardException extends ActionEscape {

	/**
	 * The URL to forward to.
	 */
	private final String forwardTo;

	/**
	 * Creates a ForwardException.
	 *
	 * @param forwardTo the URL to forward to.
	 */
	public ForwardException(final String forwardTo) {
		this.forwardTo = forwardTo;
	}

	/**
	 * @return the URL to forward to.
	 */
	public String getForwardTo() {
		return forwardTo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void escape() throws IOException {
		getResponse().sendRedirect(getForwardTo());
	}
}
