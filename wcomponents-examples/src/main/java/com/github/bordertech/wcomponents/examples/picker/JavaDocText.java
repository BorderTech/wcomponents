package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.WText;

/**
 * <p>
 * JavaDocText is a simple component to render the javaDoc from the example classes into a WComponent.
 * </p>
 *
 * It performs a series of operations on the inbound source to:
 *
 * <ul>
 * <li>Extract the java doc</li>
 * <li>remove any asterisks</li>
 * <li>remove the link items</li>
 * </ul>
 *
 * this class may need further enhancements as it is fairly simplistic at the moment.
 *
 * @author Steve Harney
 * @since 1.0.0
 */
public class JavaDocText extends WText {

	/**
	 * Creates a JavaDocText.
	 *
	 * @param source is a string that represents the source of the example being displayed.
	 */
	public JavaDocText(final String source) {
		setEncodeText(false);

		if (source == null) {
			setText("<p>Unable to extract JavaDoc, no source available.</p>");
		} else {
			StringBuilder javaDoc = extractJavaDoc(source);
			stripAsterisk(javaDoc);
			stripLinks(javaDoc);
			setText("<div>" + javaDoc.toString() + "</div>");
		}
	}

	/**
	 * extracts the javadoc. It assumes that the java doc for the class is the first javadoc in the file.
	 *
	 * @param source string representing the java class.
	 * @return a String builder containing the javadoc.
	 */
	private StringBuilder extractJavaDoc(final String source) {
		int docStart = source.indexOf("/**");
		int docEnd = source.indexOf("*/", docStart);
		int classStart = source.indexOf("public class");
		int author = source.indexOf("@author");
		int since = source.indexOf("@since");

		if (classStart == -1) {
			classStart = docEnd;
		}
		if (docEnd == -1 || classStart < docStart) {
			return new StringBuilder("No JavaDoc provided");
		}
		if (author != -1 && author < docEnd) {
			docEnd = author;
		}
		if (since != -1 && since < docEnd) {
			docEnd = since;
		}

		return new StringBuilder(source.substring(docStart + 3, docEnd).trim());

	}

	/**
	 * This method removes the additional astrisks from the java doc.
	 *
	 * @param javaDoc the string builder containing the javadoc.
	 */
	private void stripAsterisk(final StringBuilder javaDoc) {
		int index = javaDoc.indexOf("*");
		while (index != -1) {
			javaDoc.replace(index, index + 1, "");
			index = javaDoc.indexOf("*");
		}
	}

	/**
	 * this method is used to process the <code>@link</code> tags out of the javadoc.
	 *
	 * @param javaDoc the string builder containing the javadoc.
	 */
	private void stripLinks(final StringBuilder javaDoc) {
		int startLink = javaDoc.indexOf("{@link ");
		while (startLink != -1) {
			int endLink = javaDoc.indexOf("}", startLink) + 1;
			String link = javaDoc.substring(startLink, endLink);
			String newLink = parseLink(link);
			javaDoc.replace(startLink, endLink, newLink);
			startLink = javaDoc.indexOf("{@link ");
		}
	}

	/**
	 * a helper method to process the links as they are found.
	 *
	 * @param link the string representing the original link.
	 * @return a new string to replace the old link.
	 */
	private String parseLink(final String link) {
		String[] tokens = link.substring(7, link.length() - 1).split("\\s");
		if (tokens.length == 1) {
			return tokens[0];
		}
		StringBuilder result = new StringBuilder();

		boolean parametersSeen = false;
		boolean inParameters = false;

		for (int index = 0; index < tokens.length; index++) {
			result.append(" ").append(tokens[index]);
			if (tokens[index].indexOf('(') != -1 && !parametersSeen) {
				inParameters = true;
			}
			if (index == 1 && !inParameters) {
				result = new StringBuilder(tokens[index]);
			}
			if (tokens[index].indexOf(')') != -1 && !parametersSeen) {
				parametersSeen = true;
				if (index != tokens.length - 1) {
					result = new StringBuilder();
				}

			}

		}

		return result.toString().trim();
	}
}
