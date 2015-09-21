package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * WComponents can use this interface to communicate "global" or "shared" items to the server. Examples are lines in the
 * html/head element. It also allows for extra attributes on the html element itself.
 * <p>
 * It is important to note that "headlines" must be added in the preparePaint phase. Headlines adding in serviceRequest
 * will be lost because the headers are reset at the start of preparePaint. Adding headlines in the paint phase is too
 * late as the headers have already been written. Note also that it would be possible to make the adding of headlines
 * work for the paint phase, but at the expense of requiring the entire body of the page to be cached.
 *
 * @author James Gifford
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public interface Headers {

	/**
	 * Untyped headline.
	 */
	String UNTYPED_HEADLINE = "un-typed";
	/**
	 * Javascript headline.
	 */
	String JAVASCRIPT_HEADLINE = "javascript";
	/**
	 * CSS headline.
	 */
	String CSS_HEADLINE = "css";

	/**
	 * Called by the servlet between service requests.
	 */
	void reset();

	/**
	 * Records a "shared" line for inclusion in the output.
	 *
	 * @param aLine the line to add.
	 */
	void addHeadLine(String aLine);

	/**
	 * Records a "shared" line (of a specified type) for inclusion in the output.
	 *
	 * @param type the type of line to add.
	 * @param aLine the line to add.
	 */
	void addHeadLine(String type, String aLine);

	/**
	 * Records a line for inclusion in the html/head, if it has not already been included.
	 *
	 * @param aLine the line to add.
	 */
	void addUniqueHeadLine(String aLine);

	/**
	 * Records a "shared" line (of a specified type) for inclusion in the output, if it has not already been included.
	 * <p>
	 * An example of where this is useful is for adding shared JavaScript code.
	 *
	 * @param type the type of line to add.
	 * @param aLine the line to add.
	 */
	void addUniqueHeadLine(String type, String aLine);

	/**
	 * @return the "un-typed" head lines.
	 */
	List getHeadLines();

	/**
	 * Get the head lines, of a specified type.
	 *
	 * @param type the type of headlines to return.
	 * @return the head lines, of a specified type
	 */
	List getHeadLines(String type);

	/**
	 * Get the content type (usually text/html).
	 *
	 * @return the content mime type.
	 */
	String getContentType();

	/**
	 * Set the content type (usually text/html).
	 *
	 * @param type The content type to set on the response.
	 */
	void setContentType(String type);
}
