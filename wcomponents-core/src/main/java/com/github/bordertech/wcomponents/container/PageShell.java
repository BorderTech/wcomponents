package com.github.bordertech.wcomponents.container;

import java.io.PrintWriter;

/**
 * The PageShell interface describes the parts of a page which can be customised by the end user.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface PageShell {

	/**
	 * Opens the document. This is the first bit of content written back to the client.
	 *
	 * @param writer the writer to write to.
	 */
	void openDoc(PrintWriter writer);

	/**
	 * Writes the header information, which is written after all applications have rendered.
	 *
	 * @param writer the writer to write to.
	 */
	void writeHeader(final PrintWriter writer);

	/**
	 * Writes the header text for an application.
	 *
	 * @param writer the writer to write to.
	 */
	void writeApplicationHeader(final PrintWriter writer);

	/**
	 * Writes the footer text for an application.
	 *
	 * @param writer the writer to write to.
	 */
	void writeApplicationFooter(final PrintWriter writer);

	/**
	 * Writes the footer information, which is written after all applications have rendered.
	 *
	 * @param writer the writer to write to.
	 */
	void writeFooter(final PrintWriter writer);

	/**
	 * Closes the document. This is the last bit of content written back to the client.
	 *
	 * @param writer the writer to write to.
	 */
	void closeDoc(PrintWriter writer);
}
