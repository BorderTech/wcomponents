package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WServlet uses this to handle header information.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class HeadersImpl implements Headers {

	/**
	 * Lists of headlines, keyed by type.
	 */
	private final Map<String, ArrayList<String>> headers = new HashMap<>();

	/**
	 * The content type of the response. Defaults to "text/html".
	 */
	private String contentType = WebUtilities.CONTENT_TYPE_HTML;

	/**
	 * Clears the headers.
	 */
	@Override
	public void reset() {
		headers.clear();
		contentType = WebUtilities.CONTENT_TYPE_HTML;
	}

	/**
	 * Records a "shared" line for inclusion in the output.
	 *
	 * @param aLine the line to include.
	 */
	@Override
	public void addHeadLine(final String aLine) {
		addHeadLine(UNTYPED_HEADLINE, aLine);
	}

	/**
	 * Records a "shared" line (of a specified type) for inclusion in the output.
	 *
	 * @param type the type of line.
	 * @param aLine the line to include.
	 */
	@Override
	public void addHeadLine(final String type, final String aLine) {
		ArrayList<String> lines = headers.get(type);

		if (lines == null) {
			lines = new ArrayList<>();
			headers.put(type, lines);
		}

		lines.add(aLine);
	}

	/**
	 * Records a line for inclusion in the html/head, if it has not already been included.
	 *
	 * @param aLine the line to include.
	 */
	@Override
	public void addUniqueHeadLine(final String aLine) {
		addUniqueHeadLine(UNTYPED_HEADLINE, aLine);
	}

	/**
	 * Records a line for inclusion in the html/head, if it has not already been included.
	 *
	 * @param type the type of line.
	 * @param aLine the line to include.
	 */
	@Override
	public void addUniqueHeadLine(final String type, final String aLine) {
		ArrayList<String> lines = headers.get(type);

		if (lines == null) {
			lines = new ArrayList<>();
			lines.add(aLine);
			headers.put(type, lines);
		} else if (!lines.contains(aLine)) {
			lines.add(aLine);
		}
	}

	/**
	 * Gets the "un-typed" head lines.
	 *
	 * @return a list of headlines, or null if there are no headlines.
	 */
	@Override
	public List<String> getHeadLines() {
		return getHeadLines(UNTYPED_HEADLINE);
	}

	/**
	 * Gets the head lines, of a specified type.
	 *
	 * @param type the type of lines to retrieve.
	 * @return a list of headlines, or null if there are no headlines of the given type.
	 */
	@Override
	public List<String> getHeadLines(final String type) {
		ArrayList<String> lines = headers.get(type);
		return lines == null ? null : Collections.unmodifiableList(lines);
	}

	/**
	 * @return the content type for the response. The default type is <code>text/html</code>.
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type for the response. The default type is <code>text/html</code>.
	 *
	 * @param type The content type.
	 */
	@Override
	public void setContentType(final String type) {
		this.contentType = type;
	}
}
