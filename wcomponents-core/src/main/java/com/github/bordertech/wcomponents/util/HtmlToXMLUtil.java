package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WebUtilities;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;

/**
 * This utility class provides a mechanism to ensure HTML is XML valid. It does this by unescaping HTML entities but
 * <strong>not</strong> unescaping valid XML entities: {@code &lt;}, {@code &gt;}, {@code &quot;}, {@code &apos;} and
 * {@code &amp;}.
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public final class HtmlToXMLUtil {

	/**
	 * The translator to unescape HTML entities and the (invalid) {@code &apos;} to valid XML. This is based on the
	 * apache-commons StringEscapeUtils UNESCAPE_HTML4 translator with the BASIC_UNESCAPE Entity array removed and the
	 * APOS_UNESCAPE Entity array added.
	 */
	public static final CharSequenceTranslator UNESCAPE_HTML_TO_XML
			= new AggregateTranslator(
					new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
					new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()),
					new LookupTranslator(EntityArrays.APOS_UNESCAPE()),
					new NumericEntityUnescaper());

	/**
	 * Do not allow instantiation.
	 */
	private HtmlToXMLUtil() {
	}

	/**
	 * Unescape HTML entities to safe XML.
	 *
	 * <p>
	 * Example</p>
	 * <pre>
	 * {@code StringEscapeHTMLToXMLUtil.unescapeToXML("&bull;&Dagger}"); // returns "•‡"}
	 * {@code StringEscapeHTMLToXMLUtil.unescapeToXML("&lt;p&gt;"); // returns "&lt;p&gt;" not "<p>"}
	 * </pre>
	 *
	 * @param input The String to unescape.
	 * @return the input with all HTML4 character entities unescaped except those which are also XML entities.
	 */
	public static String unescapeToXML(final String input) {
		if (Util.empty(input)) {
			return input;
		}
		// Check if input has encoded brackets
		String encoded = WebUtilities.doubleEncodeBrackets(input);
		String unescaped = UNESCAPE_HTML_TO_XML.translate(encoded);
		String decoded = WebUtilities.doubleDecodeBrackets(unescaped);
		return decoded;
	}
}
