package com.github.bordertech.wcomponents.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;

/**
 * Extension of StringEscapeUtils to do HTML entity conversion but <strong>not</strong> conversion of the basic
 * XML entities <code>&lt;</code>, <code>&gt;</code>, <code>&quot;</code> and <code>&amp;</code>. The point of this
 * is to do a safe HTML to XML conversion.
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public class StringEscapeHTMLToXML extends StringEscapeUtils {

	/**
	 * The translator. This is the UNESCAPE_HTML4 translator without the BASIC_UNESCAPE Entity array but with
	 * APOS unescape.
	 */
	public static final CharSequenceTranslator UNESCAPE_HTML_TO_XML =
			new AggregateTranslator(
				new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
				new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()),
				new LookupTranslator(EntityArrays.APOS_UNESCAPE()),
				new NumericEntityUnescaper());

	/**
	 * Unescape HTML entities to safe XML but without unescaping the basic XML entities.
	 * @param input The String to unescape.
	 * @return the input with all HTML4 character entities unescaped except those which are also XML entities.
	 */
	public static final String unescapeToXML(final String input) {
		return UNESCAPE_HTML_TO_XML.translate(input);
	}
}
