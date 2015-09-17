package com.github.bordertech.wcomponents.util.mock;

import com.github.bordertech.wcomponents.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MockResponse is useful for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockResponse implements Response {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(MockResponse.class);

	/**
	 * The outputStream containing the data which has been written to the response.
	 */
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	/**
	 * The writer containing the data which has been written to the response.
	 */
	private final StringWriter writer = new StringWriter();

	/**
	 * The redirect URL, if the sendRedirect method is called.
	 */
	private String redirect;

	/**
	 * The response content type, if the setContentType method is called.
	 */
	private String contentType;

	/**
	 * Contains any response headers which have been set.
	 */
	private final Map<String, String> headers = new HashMap<>();

	/**
	 * The response error code.
	 */
	private int errorCode;

	/**
	 * The response error code.
	 */
	private String errorDescription;

	/**
	 * @return the response {@link OutputStream}.
	 */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @return the response {@link PrintWriter}.
	 */
	@Override
	public PrintWriter getWriter() {
		LOG.debug("Getting printwriter");
		return new PrintWriter(writer);
	}

	/**
	 * Sets the redirect.
	 *
	 * @param sendRedirect the URL to redirect to.
	 */
	@Override
	public void sendRedirect(final String sendRedirect) {
		LOG.debug("Redirecting to: " + sendRedirect);
		this.redirect = sendRedirect;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the content type.
	 */
	@Override
	public void setContentType(final String contentType) {
		LOG.debug("Content type: " + contentType);
		this.contentType = contentType;
	}

	/**
	 * Sets a header.
	 *
	 * @param name the header name.
	 * @param value the header value.
	 */
	@Override
	public void setHeader(final String name, final String value) {
		LOG.debug("Setting header, " + name + '=' + value);
		headers.put(name, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendError(final int code, final String description) throws IOException {
		this.errorCode = code;
		this.errorDescription = description;
	}

	/**
	 * @return the content written to this Response
	 */
	public byte[] getOutput() {
		return outputStream.toByteArray();
	}

	/**
	 * @return the content written to this Response
	 */
	public String getWriterOutput() {
		return writer.toString();
	}

	/**
	 * @return the redirect if set, otherwise null
	 */
	public String getRedirect() {
		return redirect;
	}

	/**
	 * @return the content type if set, otherwise null
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the headers that were set for this response
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @return the error code set using {@link #sendError(int, String)}.
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the error description set using {@link #sendError(int, String)}.
	 */
	public String getErrorDescription() {
		return errorDescription;
	}
}
