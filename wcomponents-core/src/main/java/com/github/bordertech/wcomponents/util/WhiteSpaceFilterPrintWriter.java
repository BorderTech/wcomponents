package com.github.bordertech.wcomponents.util;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * WhiteSpaceFilterPrintWriter is a writer that filters out extraneous whitespace from HTML content written to it.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WhiteSpaceFilterPrintWriter extends PrintWriter {

	/**
	 * The current state of the state machine.
	 */
	private final WhiteSpaceFilterStateMachine stateMachine;

	/**
	 * Creates a new WhiteSpaceFilterOutputStream.
	 *
	 * @param writer The Writer that will receive the filtered output
	 */
	public WhiteSpaceFilterPrintWriter(final Writer writer) {
		super(writer);
		this.stateMachine = new WhiteSpaceFilterStateMachine();
	}

	/**
	 * Writes the given byte to the underlying output stream if it passes filtering.
	 *
	 * @param c the byte to write.
	 */
	@Override
	public void write(final int c) {
		WhiteSpaceFilterStateMachine.StateChange change = stateMachine.nextState((char) c);

		if (change.getOutputBytes() != null) {
			for (int i = 0; i < change.getOutputBytes().length; i++) {
				super.write(change.getOutputBytes()[i]);
			}
		}

		if (!change.isSuppressCurrentChar()) {
			super.write(c);
		}
	}

	/**
	 * Writes the given character data to the underlying output stream, filtering as necessary.
	 *
	 * @param buf the character data to write.
	 * @param off the data offset to start writing data from.
	 * @param len the number of characters to write.
	 */
	@Override
	public void write(final char[] buf, final int off, final int len) {
		for (int i = off; i < off + len; i++) {
			write(buf[i]);
		}
	}

	/**
	 * Writes the given String to the underlying output stream, filtering as necessary.
	 *
	 * @param string the String to write.
	 * @param off the position in the string to start writing data from.
	 * @param len the number of characters to write.
	 */
	@Override
	public void write(final String string, final int off, final int len) {
		for (int i = off; i < off + len; i++) {
			write(string.charAt(i));
		}
	}
}
