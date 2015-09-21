package com.github.bordertech.wcomponents;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * An abstraction of a response to a client in a web environment, that allows WComponents to function similarly when
 * running in e.g. Servlet or Portlet environment.
 *
 * @author James Gifford, 6/07/2005
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public interface Response {

	/**
	 * @return a PrintWriter object that can return character data to the client.
	 * @throws IOException if there is an error obtaining the writer.
	 */
	PrintWriter getWriter() throws IOException;

	/**
	 * @return an output stream which can return character or binary data to the client.
	 * @throws IOException if there is an error obtaining the stream.
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Sends a redirect response to the client using the specified redirect URL. Normally, no further data will be
	 * written back to the client.
	 *
	 * @param redirect the URL to redirect to.
	 * @throws IOException if there is an error sending the redirect.
	 */
	void sendRedirect(String redirect) throws IOException;

	/**
	 * Sets the content type of the response being sent to the client.
	 *
	 * @param contentType a String specifying the MIME type of the content, e.g. text/html.
	 */
	void setContentType(String contentType);

	/**
	 * Sets a response header with the given name and value. If the header had already been set, the new value
	 * overwrites the previous one.
	 *
	 * @param name the name of the header
	 * @param value the header value
	 */
	void setHeader(String name, String value);

	/**
	 * @param code the error code to return
	 * @param description the description of the error
	 * @throws IOException an IO exception has occurred trying to send the error
	 */
	void sendError(int code, String description) throws IOException;

}
