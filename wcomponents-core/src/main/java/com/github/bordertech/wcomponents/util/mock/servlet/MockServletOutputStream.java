package com.github.bordertech.wcomponents.util.mock.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 * MockServletOutputStream - mock Servlet output stream for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockServletOutputStream extends ServletOutputStream {

	/**
	 * The backing output stream to write content to, so that it may later be retrieved using {@link #getOutput}.
	 */
	private final ByteArrayOutputStream backing = new ByteArrayOutputStream();

	/**
	 * Writes the specified byte to this output stream. The byte to be written is the eight low-order bits of the
	 * argument <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
	 *
	 * @param byt the <code>byte</code> to be written.
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public void write(final int byt) throws IOException {
		backing.write(byt);
	}

	/**
	 * Returns the data that has been written to this output stream.
	 *
	 * @return the byte data written to the stream. May be empty, but will not be null.
	 */
	public byte[] getOutput() {
		return backing.toByteArray();
	}
}
