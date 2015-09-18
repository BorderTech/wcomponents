package com.github.bordertech.wcomponents.util.mock.servlet;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

/**
 * MockServletInputStream - mock servlet input stream for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockServletInputStream extends ServletInputStream {

	/**
	 * The backing input stream to read from.
	 */
	private final InputStream backing;

	/**
	 * Creates a MockServletInputStream with the specified backing.
	 *
	 * @param backing the backing stream to read from.
	 */
	public MockServletInputStream(final InputStream backing) {
		this.backing = backing;
	}

	/**
	 * Reads the next byte of data from the backing stream. The value byte is returned as an <code>int</code> in the
	 * range <code>0</code> to <code>255</code>. If no byte is available because the end of the stream has been reached,
	 * the value <code>-1</code> is returned. This method blocks until input data is available, the end of the stream is
	 * detected, or an exception is thrown.
	 *
	 * @return the next byte of data, or <code>-1</code> if the end of the stream is reached.
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public int read() throws IOException {
		return backing.read();
	}
}
