package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

/**
 * An implementation of {@link Response} using HttpServletResponse.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class ServletResponse implements Response {

	/**
	 * The backing HttpServletResponse.
	 */
	private final HttpServletResponse backing;

	/**
	 * Creates a ServletResponse.
	 *
	 * @param backing the backing HttpServletResponse.
	 */
	public ServletResponse(final HttpServletResponse backing) {
		this.backing = backing;
	}

	/**
	 * @return the backing HttpServletResponse
	 */
	public HttpServletResponse getBackingResponse() {
		return backing;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		return backing.getWriter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return backing.getOutputStream();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendRedirect(final String redirect) throws IOException {
		backing.sendRedirect(redirect);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContentType(final String contentType) {
		backing.setContentType(contentType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeader(final String name, final String value) {
		backing.setHeader(name, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendError(final int code, final String description) throws IOException {
		backing.sendError(code, description);
	}
}
