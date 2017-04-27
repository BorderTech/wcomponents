package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WebUtilities;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests for {@link HtmlToXMLUtil}.
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public class HTMLToXMLUtil_Test {

	@Test
	public void testNoConvertLt() {
		String testString = "&lt;";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoConvertGt() {
		String testString = "&gt;";

		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoConvertQuote() {
		String testString = "&quot;";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoConvertAmpersand() {
		String testString = "&amp;";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	// We explicitly include <code>&apos;</code> so we better test it.
	@Test
	public void testConvertApos() {
		String testString = "&apos;";
		Assert.assertEquals("'", HtmlToXMLUtil.unescapeToXML(testString));
	}

	// We really don't want to test ALL of the HTML4 extended or ISO8859_1 chars so I cherry picked.
	@Test
	public void testConvertDot() {
		String testString = "&bull;";
		Assert.assertEquals("•", HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testConvertNBSP() {
		String testString = "&nbsp;";
		Assert.assertEquals("\u00a0", HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testConvertNBSPDec() {
		String testString = "&#160;";
		Assert.assertEquals("\u00a0", HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testConvertNBSPHex() {
		String testString = "&#x00a0;";
		Assert.assertEquals("\u00a0", HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testConvertNBSPHexShort() {
		String testString = "&#xa0;";
		Assert.assertEquals("\u00a0", HtmlToXMLUtil.unescapeToXML(testString));
	}

	// Being a bit paranoid, better make sure we are not accidentally encoding XML entities.
	@Test
	public void testNoUnConvertLt() {
		String testString = "<";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoUnConvertGt() {
		String testString = ">";

		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoUnConvertQuote() {
		String testString = "\"";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoUnConvertAmpersand() {
		String testString = "&";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	// We explicitly include <code>&apos;</code> so we better test it.
	@Test
	public void testNoUnConvertApos() {
		String testString = "'";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoUnConvertOpenBracketEscaped() {
		String testString = WebUtilities.OPEN_BRACKET_ESCAPE;
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoUnConvertCloseBracketEscaped() {
		String testString = WebUtilities.CLOSE_BRACKET_ESCAPE;
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoUnConvertOpenBracket() {
		String testString = "{";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

	@Test
	public void testNoUnConvertCloseBracket() {
		String testString = "}";
		Assert.assertEquals(testString, HtmlToXMLUtil.unescapeToXML(testString));
	}

}
