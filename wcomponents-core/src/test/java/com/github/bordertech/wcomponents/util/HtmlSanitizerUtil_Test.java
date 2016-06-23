package com.github.bordertech.wcomponents.util;

import junit.framework.Assert;
import org.junit.Test;

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
	private static final String TAINTED_ATTRIBUTE = "<p foo='tainted'>content</p>";
	/**
	 * A HTML string with a good attribute with an unsupported value.
	 */
	private static final String TAINTED_STYLE = "<p style='z-index:1;'>content</p>";

	@Test
	public void testSanitizerNoChange() throws Exception {
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizerUtil.sanitize(SIMPLE_HTML));
	}

	@Test
	public void testSanitizerTaintedAttribute() throws Exception {
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizerUtil.sanitize(TAINTED_ATTRIBUTE));
	}

	@Test
	public void testSanitizerTaintedStyle() throws Exception {
		Assert.assertEquals("<p style=\"\">content</p>", HtmlSanitizerUtil.sanitize(TAINTED_STYLE));
	}

	// We only allow two styles.
	@Test
	public void testSanitizerGoodStyle() throws Exception {
		String input = "<p style=\"text-decoration: line-through;padding-left: 20.0px;\">content</p>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerStyleBadDecoration() throws Exception {
		String input = "<p style=\"text-decoration: all;\">content</p>";
		String expected = "<p style=\"\">content</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerStyleBadPadding() throws Exception {
		String input = "<p style=\"padding-left: any;\">content</p>";
		String expected = "<p style=\"\">content</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	// I am not going to test every attribute and element in the config XML.
	@Test
	public void testSanitizerGoodAttribute() throws Exception {
		String input = "<p title=\"Hello\">content</p>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodAttributeBadValue() throws Exception {
		String input = "<p title=\"???\">content</p>";
		String expected = "<p>content</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerRemovedElement() throws Exception {
		String input = "<div>Hello<form>goodbye</form></div>";
		String expected = "<div>Hello</div>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredElement() throws Exception {
		String input = "<body>Hello <p>goodbye</p></body>";
		String expected = "Hello <p>goodbye</p>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerTrucatedElement() throws Exception {
		String input = "<tt title=\"hello\">Hello</tt>";
		String expected = "<tt>Hello</tt>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodLink() throws Exception {
		String input = "<a href=\"http://example.com\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	// added tel protocol support
	@Test
	public void testSanitizerGoodTelLink() throws Exception {
		String input = "<a href=\"tel:123456\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodLocalLink() throws Exception {
		String input = "<a href=\"path/file.html\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerGoodServerLocalLink() throws Exception {
		String input = "<a href=\"/path/file.html\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredLink() throws Exception {
		String input = "<a name=\"anchor\">Hello</a>";
		String expected = "<a>Hello</a>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredLinkBadHref() throws Exception {
		String input = "<a href=\"page_here\">Hello</a>";
		String expected = "Hello";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerEmptyInput() throws Exception {
		Assert.assertEquals("", HtmlSanitizerUtil.sanitize(""));
	}

	@Test
	public void testSanitizerEmptyishInput() throws Exception {
		String input = " ";
		Assert.assertEquals(input, HtmlSanitizerUtil.sanitize(input));
	}

	@Test
	public void testSanitizerNullInput() throws Exception {
		Assert.assertNull(HtmlSanitizerUtil.sanitize(null));
	}

	@Test
	public void testSanitizerAddCloseTags() throws Exception {
		String input = "<ul><li>unclosed li<li>second unclosed li</ul>";
		String expected = "<ul><li>unclosed li</li><li>second unclosed li</li></ul>";
		Assert.assertEquals(expected, HtmlSanitizerUtil.sanitize(input));
	}

	// We should test throwing in a bad config but it is too late to do it this way.
	/* @Test
	public void testBadConfig() {
		Config.getInstance().setProperty("com.github.bordertech.wcomponents.sanitizers.config", "Bad_Value");
		try {
			HtmlSanitizerUtil.sanitize(SIMPLE_HTML);
			Assert.fail("Should have thrown a PolicyException");
		} catch (Exception e) {
			Assert.assertNotNull(e.getMessage());
		}
	} */
}
