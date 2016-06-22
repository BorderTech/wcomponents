package com.github.bordertech.wcomponents.util;

import java.util.List;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

/**
 * Tests for {@link HtmlSanitizer}.
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public class HtmlSanitizer_Test {

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
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizer.sanitize(SIMPLE_HTML));
	}

	@Test
	public void testSanitizerSafeNoErrors() throws Exception {
		HtmlSanitizer.sanitize(SIMPLE_HTML);
		Assert.assertTrue(CollectionUtils.isEmpty(HtmlSanitizer.getErrors()));
	}

	@Test
	public void testSanitizerTaintedAttribute() throws Exception {
		Assert.assertEquals(SIMPLE_HTML, HtmlSanitizer.sanitize(TAINTED_ATTRIBUTE));
	}

	@Test
	public void testSanitizerTaintedAttributeHasErrors() throws Exception {
		HtmlSanitizer.sanitize(TAINTED_ATTRIBUTE);
		List<String> errors = HtmlSanitizer.getErrors();
		Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
		Assert.assertEquals(1, errors.size());
	}

	@Test
	public void testSanitizerTaintedStyle() throws Exception {
		Assert.assertEquals("<p style=\"\">content</p>", HtmlSanitizer.sanitize(TAINTED_STYLE));
	}

	@Test
	public void testSanitizerTaintedStyleHasErrors() throws Exception {
		HtmlSanitizer.sanitize(TAINTED_STYLE);
		List<String> errors = HtmlSanitizer.getErrors();
		Assert.assertTrue(CollectionUtils.isNotEmpty(errors));
		Assert.assertEquals(1, errors.size());
	}

	// We only allow two styles.
	@Test
	public void testSanitizerGoodStyle() throws Exception {
		String input = "<p style=\"text-decoration: line-through;padding-left: 20.0px;\">content</p>";
		Assert.assertEquals(input, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerStyleBadDecoration() throws Exception {
		String input = "<p style=\"text-decoration: all;\">content</p>";
		String expected = "<p style=\"\">content</p>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));

	}

	@Test
	public void testSanitizerStyleBadPadding() throws Exception {
		String input = "<p style=\"padding-left: any;\">content</p>";
		String expected = "<p style=\"\">content</p>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));

	}

	// I am not going to test every attribute and element in the config XML.
	@Test
	public void testSanitizerGoodAttribute() throws Exception {
		String input = "<p title=\"Hello\">content</p>";
		Assert.assertEquals(input, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerGoodAttributeBadValue() throws Exception {
		String input = "<p title=\"???\">content</p>";
		String expected = "<p>content</p>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerRemovedElement() throws Exception {
		String input = "<div>Hello<form>goodbye</form></div>";
		String expected = "<div>Hello</div>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredElement() throws Exception {
		String input = "<body>Hello <p>goodbye</p></body>";
		String expected = "Hello <p>goodbye</p>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerTrucatedElement() throws Exception {
		String input = "<tt title=\"hello\">Hello</tt>";
		String expected = "<tt>Hello</tt>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerGoodLink() throws Exception {
		String input = "<a href=\"http://example.com\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizer.sanitize(input));
	}

	// added tel protocol support
	@Test
	public void testSanitizerGoodTelLink() throws Exception {
		String input = "<a href=\"tel:123456\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerGoodLocalLink() throws Exception {
		String input = "<a href=\"path/file.html\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerGoodServerLocalLink() throws Exception {
		String input = "<a href=\"/path/file.html\">Link</a>";
		Assert.assertEquals(input, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredLink() throws Exception {
		String input = "<a name=\"anchor\">Hello</a>";
		String expected = "<a>Hello</a>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerFilteredLinkBadHref() throws Exception {
		String input = "<a href=\"page_here\">Hello</a>";
		String expected = "Hello";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testLongHtmlTakesTime() throws Exception {
		String longHtml = "<div><p id='one' class='dot' style='padding:1em;'>Lorem ipsum dolor sit amet, consectetur "
				+ "adipiscing elit. Vestibulum eget justo eget ipsum placerat tempor. Nullam at auctor quam. Donec at "
				+ "ante leo. Proin a eros nunc. Cras euismod, lorem quis viverra porta, nisl orci hendrerit mi, sit "
				+ "amet hendrerit lectus massa eget eros. Cras semper est in aliquet laoreet. Phasellus volutpat "
				+ "iaculis ultrices. Sed ac feugiat nibh, at facilisis arcu. Nulla elit ligula, molestie sit amet "
				+ "mauris ut, placerat gravida arcu.</p><p accesskey='A'>Morbi et ante imperdiet, egestas nunc vitae, "
				+ "mollis dui. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis "
				+ "egestas. Maecenas quis metus in ante dictum ultrices. Maecenas justo mi, euismod laoreet consectetur"
				+ " sed, porttitor mollis tortor. Ut gravida sapien ac nunc porttitor suscipit. Nulla in nibh sit amet "
				+ "orci placerat facilisis. Duis vel consequat metus. Vivamus tempus euismod pellentesque. Interdum et "
				+ "malesuada fames ac ante ipsum primis in faucibus. Aliquam bibendum ut metus eu dignissim.</p>"
				+ "<p tabindex='0'>Sed sagittis est nisi, eu vulputate ex laoreet a. Aliquam luctus, orci ut lobortis "
				+ "sagittis, arcu odio imperdiet ante, quis mollis massa dolor elementum elit. Integer sed dui sed "
				+ "metus dapibus elementum nec a enim. Suspendisse nec commodo ex. Vivamus faucibus ante sed sem "
				+ "condimentum eleifend vitae et sapien. Nullam ultrices libero id nunc pretium commodo. Nullam "
				+ "posuere elit bibendum fringilla maximus.</p><p role='tab'>Mauris nec aliquet nulla. Sed sed "
				+ "fermentum odio. Cras ut lacus gravida, tempus turpis ac, rhoncus elit. In dictum dui libero, quis "
				+ "tempor mi aliquam ut. Ut fringilla venenatis scelerisque. Suspendisse eu orci vel nisl tempor "
				+ "vehicula eu non ligula. Nam sit amet urna eu turpis tempus imperdiet.</p><p lang='en-gb'>Praesent "
				+ "in turpis est. Etiam sit amet ultrices sapien. Quisque rutrum porta vulputate. Proin pulvinar, nibh"
				+ " feugiat accumsan dapibus, nisl mauris tempor libero, a blandit massa ipsum sed lacus. Fusce id elit"
				+ " non eros laoreet mollis. Aliquam consectetur ligula et rhoncus mattis. Fusce posuere neque id ante "
				+ "sodales sagittis. Aenean suscipit consectetur felis ut pulvinar. Phasellus dapibus justo eget "
				+ "sodales mattis. Quisque commodo sem quis lectus convallis, facilisis lacinia odio vehicula. In a "
				+ "justo sapien.</p></div><ul onclick='alert();'><li class='dot'>one</li><li class='dot'>two</li>"
				+ "<li class='dot'>three</li><li class='dot'>for</li><li class='dot'>five</li></ul>";
		HtmlSanitizer.sanitize(longHtml);
		Assert.assertTrue(0 < HtmlSanitizer.getScanTime());
	}

	@Test
	public void testSanitizerEmptyInput() throws Exception {
		Assert.assertEquals("", HtmlSanitizer.sanitize(""));
	}

	@Test
	public void testSanitizerEmptyishInput() throws Exception {
		String input = " ";
		Assert.assertEquals(input, HtmlSanitizer.sanitize(input));
	}

	@Test
	public void testSanitizerNullInput() throws Exception {
		Assert.assertNull(HtmlSanitizer.sanitize(null));
	}

	@Test
	public void testSanitizerAddCloseTags() throws Exception {
		String input = "<ul><li>unclosed li<li>second unclosed li</ul>";
		String expected = "<ul><li>unclosed li</li><li>second unclosed li</li></ul>";
		Assert.assertEquals(expected, HtmlSanitizer.sanitize(input));
	}

	// We should test throwing in a bad config but it is too late to do it this way.
	/* @Test
	public void testBadConfig() {
		Config.getInstance().setProperty("com.github.bordertech.wcomponents.sanitizers.config", "Bad_Value");
		try {
			HtmlSanitizer.sanitize(SIMPLE_HTML);
			Assert.fail("Should have thrown a PolicyException");
		} catch (Exception e) {
			Assert.assertNotNull(e.getMessage());
		}
	} */
}
