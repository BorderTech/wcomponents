package com.github.bordertech.wcomponents;

import java.io.IOException;

/**
 * An escape can be thrown during servicing in which case the WComponent's paint will not be used, instead the escape
 * method will be called, after the setRequest and setResponse methods are called. This can be used to do forwarding,
 * etc.
 *
 * @author Martin Shevchenko
 */
public class Escape extends RuntimeException {

	/**
	 * The request being responded to.
	 */
	private Request request;

	/**
	 * The response to the client.
	 */
	private Response response;

	/**
	 * @return the request being responded to.
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Sets the request being responded to.
	 *
	 * @param request the request to set.
	 */
	public void setRequest(final Request request) {
		this.request = request;
	}

	/**
	 * @return the response to the client.
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * Sets the client response.
	 *
	 * @param response the response to set.
	 */
	public void setResponse(final Response response) {
		this.response = response;
	}

	/**
	 * Subclasses can override this and do whatever they need to.
	 *
	 * @throws java.io.IOException IOException
	 */
	public void escape() throws IOException {
		// The default implementation does nothing
	}
}
