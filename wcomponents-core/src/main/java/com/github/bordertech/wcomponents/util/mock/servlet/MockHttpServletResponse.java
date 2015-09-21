package com.github.bordertech.wcomponents.util.mock.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * A mock http servlet response, useful for unit testing. Extracted from PortalBridge_Test
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockHttpServletResponse implements HttpServletResponse {

	private String sendRedirection;
	private String contentType;
	private String characterEncoding = "UTF-8";
	private final Map headers = new HashMap();
	private MockServletOutputStream outputStream;
	private PrintWriter printWriter;
	private StringWriter stringWriter;
	private int status = HttpServletResponse.SC_OK;

	/**
	 * Unimplemented.
	 *
	 * @param arg0 ignored.
	 */
	@Override
	public void addCookie(final Cookie arg0) {
		// No effect
	}

	/**
	 * Unimplemented.
	 *
	 * @param arg0 ignored.
	 * @param arg1 ignored.
	 */
	@Override
	public void addDateHeader(final String arg0, final long arg1) {
		// No effect
	}

	/**
	 * Headers added are recorded for later access.
	 *
	 * @param key header key of the value to be added.
	 * @param value header value.
	 */
	@Override
	public void addHeader(final String key, final String value) {
		headers.put(key, value);
	}

	/**
	 * Unimplemented.
	 *
	 * @param arg0 ignored.
	 * @param arg1 ignored.
	 */
	@Override
	public void addIntHeader(final String arg0, final int arg1) {
		// No effect
	}

	/**
	 * Determines whether the specified header exists.
	 *
	 * @param name the header name.
	 * @return true if the specified header has been set.
	 */
	@Override
	public boolean containsHeader(final String name) {
		return headers.containsKey(name);
	}

	/**
	 * @return The set of header keys added.
	 */
	public Set getHeaderKeys() {
		return headers.keySet();
	}

	/**
	 * Unimplemented.
	 *
	 * @param url the url.
	 * @return the unmodified url.
	 */
	@Override
	public String encodeRedirectUrl(final String url) {
		return url;
	}

	/**
	 * Unimplemented.
	 *
	 * @param url the url.
	 * @return the unmodified url.
	 */
	@Override
	public String encodeRedirectURL(final String url) {
		return url;
	}

	/**
	 * Unimplemented.
	 *
	 * @param url the url.
	 * @return the unmodified url.
	 */
	@Override
	public String encodeUrl(final String url) {
		return url;
	}

	/**
	 * Unimplemented.
	 *
	 * @param url the url.
	 * @return the unmodified url.
	 */
	@Override
	public String encodeURL(final String url) {
		// no need to encode
		return url;
	}

	/**
	 * Sets the HTTP response status to indicate an error.
	 *
	 * @param errorStatus the status code, e.g. 404 for not found.
	 * @param arg1 ignored.
	 */
	@Override
	public void sendError(final int errorStatus, final String arg1) {
		this.status = errorStatus;
	}

	/**
	 * Sets the HTTP response status to indicate an error.
	 *
	 * @param errorStatus the status code, e.g. 404 for not found.
	 */
	@Override
	public void sendError(final int errorStatus) {
		this.status = errorStatus;
	}

	/**
	 * Sets the redirect url.
	 *
	 * @param url the redirect url.
	 */
	@Override
	public void sendRedirect(final String url) {
		sendRedirection = url;
	}

	/**
	 * Sets a date header.
	 *
	 * @param name the header name.
	 * @param value the header value.
	 */
	@Override
	public void setDateHeader(final String name, final long value) {
		headers.put(name, String.valueOf(value));
	}

	/**
	 * Sets a header.
	 *
	 * @param name the header name.
	 * @param value the header value.
	 */
	@Override
	public void setHeader(final String name, final String value) {
		headers.put(name, value);
	}

	/**
	 * Sets an integer header.
	 *
	 * @param name the header name.
	 * @param value the header value.
	 */
	@Override
	public void setIntHeader(final String name, final int value) {
		headers.put(name, String.valueOf(value));
	}

	/**
	 * Sets the HTTP response status.
	 *
	 * @param statusCode the status code, e.g. 404 for not found.
	 * @param arg1 ignored.
	 */
	@Override
	public void setStatus(final int statusCode, final String arg1) {
		this.status = statusCode;
	}

	/**
	 * Sets the HTTP response status.
	 *
	 * @param status the status code, e.g. 404 for not found.
	 */
	@Override
	public void setStatus(final int status) {
		this.status = status;
	}

	/**
	 * Unimplemented.
	 */
	@Override
	public void flushBuffer() {
		// No effect
	}

	/**
	 * @return 0, as this is unimplemented.
	 */
	@Override
	public int getBufferSize() {
		return 0;
	}

	/**
	 * @return The character encoding used by the StringWriter.
	 */
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public Locale getLocale() {
		return null;
	}

	/**
	 * Returns a ServletOutputStream that would normally be used to write content back to the client. Instead, the
	 * content is held internally and may be retrieved by calling {@link #getOutput()}.
	 *
	 * @return a ServletOutputStream to write content to.
	 * @throws IOException if {@link #getWriter()} has been called.
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (printWriter != null) {
			throw new IOException("getWriter method has been called on this response");
		}

		if (outputStream == null) {
			outputStream = new MockServletOutputStream();
		}

		return outputStream;
	}

	/**
	 * Returns a PrintWriter that would normally be used to write content back to the client. Instead, the content is
	 * held internally and may be retrieved by calling {@link #getOutput()}.
	 *
	 * @return a PrintWriter to write content to.
	 * @throws IOException if {@link #getOutputStream()} has been called.
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		if (outputStream != null) {
			throw new IOException("getOutputStream method has been called on this response");
		}

		if (printWriter == null) {
			stringWriter = new StringWriter();
			printWriter = new PrintWriter(stringWriter);
		}

		return printWriter;
	}

	/**
	 * @return false, as unimplemented.
	 */
	@Override
	public boolean isCommitted() {
		return false;
	}

	/**
	 * Unimplemented.
	 */
	@Override
	public void reset() {
		// No effect
	}

	/**
	 * Unimplemented.
	 */
	@Override
	public void resetBuffer() {
		// No effect
	}

	/**
	 * Unimplemented.
	 *
	 * @param arg0 ignored.
	 */
	@Override
	public void setBufferSize(final int arg0) {
		// No effect
	}

	/**
	 * Unimplemented.
	 *
	 * @param arg0 ignored.
	 */
	@Override
	public void setContentLength(final int arg0) {
		// No effect
	}

	/**
	 * Sets the response content type.
	 *
	 * @param contentType the content type.
	 */
	@Override
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Unimplemented.
	 *
	 * @param arg0 ignored.
	 */
	@Override
	public void setLocale(final Locale arg0) {
		// No effect
	}

	/**
	 * @return the response content type.
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the contentEncoding for the StringWriter.
	 *
	 * @param contentEncoding The name of the content encoding for the stringWriter.
	 */
	@Override
	public void setCharacterEncoding(final String contentEncoding) {
		this.characterEncoding = contentEncoding;
	}

	/**
	 * @return the redirect URL.
	 */
	public String getSendRedirection() {
		return sendRedirection;
	}

	/**
	 * Returns the value of the specified header.
	 *
	 * @param name the header name.
	 * @return the value of the specified header, or null if it has not been set.
	 */
	public String getHeader(final String name) {
		return (String) headers.get(name);
	}

	/**
	 * Retrieves the content written to the response.
	 *
	 * @return the content written to the response outputStream or printWriter. Null is returned if neither
	 * {@link #getOutputStream()} or {@link #getWriter()} have been called.
	 */
	public byte[] getOutput() {
		if (stringWriter != null) {
			byte[] output = null;

			String string = stringWriter.toString();
			if (string != null) {
				try {
					output = string.getBytes(characterEncoding);
				} catch (UnsupportedEncodingException uue) {
					output = null;
				}
			}

			return output;
		} else if (outputStream != null) {
			return outputStream.getOutput();
		}

		return null;
	}

	/**
	 * Retrieves the content written to the response.
	 *
	 * @return the content written to the response outputStream or printWriter. Null is returned if neither
	 * {@link #getOutputStream()} or {@link #getWriter()} have been called.
	 */
	public String getOutputAsString() {
		if (stringWriter != null) {
			return stringWriter.toString();
		} else if (outputStream != null) {
			String outputStr = null;

			byte[] bytes = outputStream.getOutput();
			if (bytes != null) {
				try {
					outputStr = new String(bytes, characterEncoding);
				} catch (UnsupportedEncodingException e) {
					outputStr = null;
				}
			}

			return outputStr;
		}

		return null;
	}

	/**
	 * @return the response status code.
	 */
	public int getStatus() {
		return status;
	}
}
