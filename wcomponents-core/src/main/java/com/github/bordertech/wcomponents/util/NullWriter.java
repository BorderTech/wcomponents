package com.github.bordertech.wcomponents.util;

import java.io.Writer;

/**
 * A writer that just throws the information in the bit-bucket. This is used in several of the internal unit tests.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class NullWriter extends Writer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		// NO-OP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() {
		// NO-OP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final char[] cbuf, final int off, final int len) {
		// NO-OP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final String str) {
		// NO-OP - Override to save execution time.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final String str, final int off, final int len) {
		// NO-OP - Override to save execution time.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final int c) {
		// NO-OP - Override to save execution time.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final char[] cbuf) {
		// NO-OP - Override to save execution time.
	}
}
