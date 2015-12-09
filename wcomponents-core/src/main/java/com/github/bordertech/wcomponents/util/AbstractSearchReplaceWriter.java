package com.github.bordertech.wcomponents.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * <p>
 * AbstractSearchReplaceWriter is a writer extension that allows programmatic replacement of strings contained in
 * written stream. For efficiency, it uses a buffer that is twice the size of the longest search string.</p>
 *
 * <p>
 * Data is only written to the underlying writer when the buffer is filled, or when the writer is closed; calling
 * {@link #flush()} only flushes the underlying writer. Calling {@link #close()} may not close the underlying writer,
 * depending on the return value of {@link #closeBackingOnClose()}</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractSearchReplaceWriter extends Writer {

	/**
	 * The array of search Strings, sorted in ascending order.
	 */
	private final String[] search;

	/**
	 * The underlying writer to write the output to.
	 */
	private final Writer backing;

	/**
	 * The "lookahead" buffer. It buffers characters from the underlying stream to allow us to check for the "match"
	 * string before writing them to the underlying writer.
	 */
	private final char[] buffer;

	/**
	 * Our current position within the {@link #lookahead} buffer.
	 */
	private int bufferLen = 0;

	/**
	 * Creates a SearchReplaceWriter.
	 *
	 * @param search the search strings.
	 * @param backing the backing writer, where output will be eventually sent to.
	 */
	public AbstractSearchReplaceWriter(final String[] search, final Writer backing) {
		this.backing = backing;

		// Create a copy of the search array, as we need it sorted
		this.search = new String[search.length];
		System.arraycopy(search, 0, this.search, 0, search.length);
		Arrays.sort(this.search);

		// Find the longest search item
		int maxKeyLength = -1;

		for (int i = 0; i < search.length; i++) {
			if (search[i] == null || search[i].length() == 0) {
				throw new IllegalArgumentException("Search strings must not be empty, " + i);
			}

			maxKeyLength = Math.max(maxKeyLength, search[i].length());
		}

		if (maxKeyLength == -1) {
			// Nothing to replace, so no need for a buffer
			buffer = null;
		} else {
			buffer = new char[maxKeyLength * 2];
		}
	}

	/**
	 * Writes out remaining content and optionally closes the backing writer.
	 *
	 * @see #closeBackingOnClose()
	 * @throws IOException if there is an error closing the underlying buffer.
	 */
	@Override
	public void close() throws IOException {
		if (bufferLen != 0) {
			writeBuf(bufferLen);
		}

		if (closeBackingOnClose()) {
			backing.close();
		}
	}

	/**
	 * Flushes the underlying writer.
	 *
	 * @throws IOException if there is an error flushing the underlying buffer.
	 */
	@Override
	public void flush() throws IOException {
		backing.flush();
	}

	/**
	 * Implementation of Writer's write method.
	 *
	 * @param cbuf the character buffer to write.
	 * @param off the start position in the array to write from.
	 * @param len the amount of character data to write.
	 * @throws IOException if there is an error writing to the underlying buffer.
	 */
	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException {
		if (buffer == null) {
			// Nothing to replace, just pass the data through
			backing.write(cbuf, off, len);
		} else {
			for (int i = off; i < off + len; i++) {
				buffer[bufferLen++] = cbuf[i];

				if (bufferLen == buffer.length) {
					writeBuf(buffer.length / 2);
				}
			}
		}
	}

	/**
	 * Writes the current contents of the buffer, up to the given position. More data may be written from the buffer
	 * when there is a search string that crosses over endPos.
	 *
	 * @param endPos the end position to stop writing
	 * @throws IOException if there is an error writing to the underlying writer.
	 */
	private void writeBuf(final int endPos) throws IOException {
		// If the stream is not closed, we only process half the buffer at once.
		String searchTerm;
		int pos = 0;
		int lastWritePos = 0;

		while (pos < endPos) {
			searchTerm = findSearchStrings(pos);

			if (searchTerm != null) {
				if (lastWritePos != pos) {
					backing.write(buffer, lastWritePos, pos - lastWritePos);
				}

				doReplace(searchTerm, backing);
				pos += searchTerm.length();
				lastWritePos = pos;
			} else {
				pos++;
			}
		}

		// Write the remaining characters that weren't matched
		if (lastWritePos != pos) {
			backing.write(buffer, lastWritePos, pos - lastWritePos);
		}

		// Shuffle the buffer
		System.arraycopy(buffer, pos, buffer, 0, buffer.length - pos);
		bufferLen -= pos;
	}

	/**
	 * Searches for any search strings in the buffer that start between the specified offsets.
	 *
	 * @param start the start search offset
	 *
	 * @return the first search String found, or null if none were found.
	 */
	private String findSearchStrings(final int start) {
		String longestMatch = null;

		// Loop for each string
		for (int i = 0; i < search.length; i++) {
			// No point checking a String that's too long
			if (start + search[i].length() > bufferLen) {
				continue;
			}

			boolean found = true;

			// Loop for each character in range
			for (int j = 0; j < search[i].length() && (start + j < bufferLen); j++) {
				int diff = buffer[start + j] - search[i].charAt(j);

				if (diff < 0) {
					// Since the strings are all sorted, we can abort if
					// the character is less than the corresponding character in
					// the current search string.
					return longestMatch;
				} else if (diff != 0) {
					found = false;
					break;
				}
			}

			if (found && (longestMatch == null || longestMatch.length() < search[i].length())) {
				longestMatch = search[i];
			}
		}

		return longestMatch;
	}

	/**
	 * Indicates whether the backing writer should be closed on close of this writer. Subclasses can override this to
	 * keep the underlying writer open.
	 *
	 * @return true if the backing writer should be closed when {@link #close()} is called.
	 */
	protected boolean closeBackingOnClose() {
		return true;
	}

	/**
	 * Subclasses must implement this method to perform the actual replacement.
	 *
	 * @param search the search string that was matched.
	 * @param backing the underlying writer to write the output to.
	 * @throws IOException if there is an error writing to the underlying writer.
	 */
	protected abstract void doReplace(String search, Writer backing) throws IOException;
}
