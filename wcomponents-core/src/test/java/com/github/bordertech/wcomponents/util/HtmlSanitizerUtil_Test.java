package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WebUtilities;
import junit.framework.Assert;
import org.junit.Test;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * Tests for {@link HtmlSanitizerUtil}.
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public class HtmlSanitizerUtil_Test {

	/**
	 * A simple, valid HTML String for testing.
	 */
	private static final String SIMPLE_HTML = "<p>content</p>";
	/**
	 * A HTML string with an unsupported attribute.
	 */
	private static final String TAINTED_ATTRIBUTE = "<p foo=\"tainted\">content</p>";
	/**
	 * A HTML string with a good attribute with an unsupported value (when strict).
	 */
	private static final String TAINTED_STYLE = "<p style=\"z-index: 1;\">content</p>";
	/**
	 * A HTML string with an unsupported attribute when strict.
	 */
	private static final String STRICT_TAINTED_ATTRIBUTE = "<p class=\"tainted\">content</p>";

	@Test
	public void testSanitizerNoChange() throws ScanException, PolicyException {
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizerUtil.sanitize(SIMPLE_HTML));
	}

	@Test
	public void testSanitizerTaintedAttribute() throws ScanException, PolicyException {
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizerUtil.sanitize(TAINTED_ATTRIBUTE));
	}

	@Test
	public void testSanitizerTaintedStyle() throws ScanException, PolicyException {
		Assert.assertEquals("<p style=\"\">content</p>", HtmlSanitizerUtil.sanitize(TAINTED_STYLE));
	}

	// We only allow two styles.
	@Test
	public void testSanitizerGoodStyle() throws ScanException, PolicyException {
		String input = "<p style=\"text-decoration: line-through;padding-left: 20.0px;\">content</p>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerStyleBadDecoration() throws ScanException, PolicyException {
		String input = "<p style=\"text-decoration: all;\">content</p>";
		String expected = "<p style=\"\">content</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerStyleBadPadding() throws ScanException, PolicyException {
		String input = "<p style=\"padding-left: any;\">content</p>";
		String expected = "<p style=\"\">content</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	// I am not going to test every attribute and element in the config XML.
	@Test
	public void testSanitizerGoodAttribute() throws ScanException, PolicyException {
		String input = "<p title=\"Hello\">content</p>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodAttributeBadValue() throws ScanException, PolicyException {
		String input = "<p title=\"???\">content</p>";
		String expected = "<p>content</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerRemovedElement() throws ScanException, PolicyException {
		String input = "<div>Hello<form>goodbye</form></div>";
		String expected = "<div>Hello</div>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredElement() throws ScanException, PolicyException {
		String input = "<body>Hello <p>goodbye</p></body>";
		String expected = "Hello <p>goodbye</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerTrucatedElement() throws ScanException, PolicyException {
		String input = "<tt title=\"hello\">Hello</tt>";
		String expected = "<tt>Hello</tt>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodLink() throws ScanException, PolicyException {
		String input = "<a href=\"http://example.com\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	// added tel protocol support
	@Test
	public void testSanitizerGoodTelLink() throws ScanException, PolicyException {
		String input = "<a href=\"tel:123456\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodLocalLink() throws ScanException, PolicyException {
		String input = "<a href=\"path/file.html\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodServerLocalLink() throws ScanException, PolicyException {
		String input = "<a href=\"/path/file.html\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredLink() throws ScanException, PolicyException {
		String input = "<a name=\"anchor\">Hello</a>";
		String expected = "<a>Hello</a>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredLinkBadHref() throws ScanException, PolicyException {
		String input = "<a href=\"page here\">Hello</a>";
		String expected = "Hello";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerEmptyInput() throws ScanException, PolicyException {
		Assert.assertEquals("", HtmlSanitizerUtil.sanitize(""));
	}

	@Test
	public void testSanitizerEmptyishInput() throws ScanException, PolicyException {
		String input = " ";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerNullInput() throws ScanException, PolicyException {
		Assert.assertNull(HtmlSanitizerUtil.sanitize(null));
	}

	@Test
	public void testSanitizerAddCloseTags() throws ScanException, PolicyException {
		String input = "<ul><li>unclosed li<li>second unclosed li</ul>";
		String expected = "<ul><li>unclosed li</li><li>second unclosed li</li></ul>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	// Test of lax sanitiser rules.
	@Test
	public void testStrictSanitizerElement() throws ScanException, PolicyException {
		String input = "<input name=\"foo\" type=\"text\" value=\"bar\"/>";
		Assert.assertEquals("", HtmlSanitizerUtil.sanitize(input, false));
	}

	@Test
	public void testLaxScanElement() throws ScanException, PolicyException {
		String input = "<input name=\"foo\" type=\"text\" value=\"bar\" />";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input, true));
	}

	@Test
	public void testLaxScanAddCloseTags() throws ScanException, PolicyException {
		String input = "<ul><li>unclosed li<li>second unclosed li</ul>";
		String expected = "<ul><li>unclosed li</li><li>second unclosed li</li></ul>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input, true));
	}

	@Test
	public void testLaxScanFilteredLinkBadHref() throws ScanException, PolicyException {
		String input = "<a href=\"page here\">Hello</a>";
		String expected = "Hello";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input, true));
	}

	@Test
	public void testLaxScanFilteredElement() throws ScanException, PolicyException {
		String input = "<div>Hello<form>goodbye</form></div>";
		String expected = "<div>Hellogoodbye</div>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input, true));
	}

	@Test
	public void testLaxScanTaintedAttribute() throws ScanException, PolicyException {
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizerUtil.sanitize(TAINTED_ATTRIBUTE, true));
	}

	@Test
	public void testLaxScanTaintedStyle() throws ScanException, PolicyException {
		Assert.assertEquals(TAINTED_STYLE, HtmlSanitizerUtil.sanitize(TAINTED_STYLE, true));
	}

	@Test
	public void testStrictScanLaxAttribute() throws ScanException, PolicyException {
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizerUtil.sanitize(STRICT_TAINTED_ATTRIBUTE, false));
	}

	@Test
	public void testLaxScanLaxAttribute() throws ScanException, PolicyException {
		Assert.assertEquals(STRICT_TAINTED_ATTRIBUTE, HtmlSanitizerUtil.sanitize(STRICT_TAINTED_ATTRIBUTE, true));
	}

	@Test
	public void testCreatePolicy() throws PolicyException {
		String resourceName = ConfigurationProperties.getAntisamyStrictConfigurationFile();
		Assert.assertNotNull(HtmlSanitizerUtil.createPolicy(resourceName));
	}

	@Test
	public void testCreatePolicyNullString() throws PolicyException {
		try {
			HtmlSanitizerUtil.createPolicy(null);
			Assert.assertTrue(false);
		} catch (SystemException ex) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testCreatePolicyBadString() throws PolicyException {
		String resourceName = "Bad_Value";
		try {
			HtmlSanitizerUtil.createPolicy(resourceName);
			Assert.assertTrue(false);
		} catch (SystemException ex) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testSanitizeWithPolicy() throws ScanException, PolicyException {
		String resourceName = ConfigurationProperties.getAntisamyStrictConfigurationFile();
		Policy testPolicy = HtmlSanitizerUtil.createPolicy(resourceName);
		String expected = HtmlSanitizerUtil.sanitize(TAINTED_ATTRIBUTE);
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(TAINTED_ATTRIBUTE, testPolicy));
	}

	@Test
	public void testSanitizeWithNullPolicy() throws ScanException, PolicyException {
		try {
			HtmlSanitizerUtil.sanitize(TAINTED_ATTRIBUTE, (Policy) null);
			Assert.assertTrue(false);
		} catch (SystemException ex) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testSanitizeOpenBracketEscaped() {
		String testString = WebUtilities.OPEN_BRACKET_ESCAPE;
		Assert.assertEquals(testString, HtmlSanitizerUtil.sanitize(testString));
	}

	@Test
	public void testSanitizeCloseBracketEscaped() {
		String testString = WebUtilities.CLOSE_BRACKET_ESCAPE;
		Assert.assertEquals(testString, HtmlSanitizerUtil.sanitize(testString));
	}

	@Test
	public void testSanitizeOpenBracket() {
		String testString = "{";
		Assert.assertEquals(WebUtilities.OPEN_BRACKET_ESCAPE, HtmlSanitizerUtil.sanitize(testString));
	}

	@Test
	public void testSanitizeCloseBracket() {
		String testString = "}";
		Assert.assertEquals(WebUtilities.CLOSE_BRACKET_ESCAPE, HtmlSanitizerUtil.sanitize(testString));
	}
}
