package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WFilterText_Test - unit tests for WFilterText.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class WFilterText_Test extends AbstractWComponentTestCase {

	/**
	 * sample text string.
	 */
	private static final String TEST_TEXT = "WFilterText_Test sample text";

	/**
	 * sample search string.
	 */
	private static final String TEST_SEARCH = "sample text";

	/**
	 * sample replace string.
	 */
	private static final String TEST_REPLACE = "xxxxxxxxxxx";

	@Test
	public void testEmptyConstructor() {
		WFilterText filterText = new WFilterText();

		Assert.assertEquals("text should be null", filterText.getText(), null);
		Assert.assertEquals("search should be null", filterText.getSearch(), null);
		Assert.assertEquals("replace should be null", filterText.getReplace(), null);
	}

	@Test
	public void testConstructorText() {
		WFilterText filterText = new WFilterText(TEST_TEXT);

		Assert.assertEquals("text should be TEST_TEXT", filterText.getText(), TEST_TEXT);
	}

	@Test
	public void testConstructorSearchReplace() {
		WFilterText filterText = new WFilterText(TEST_SEARCH, TEST_REPLACE);

		Assert.assertEquals("text should be null", filterText.getText(), null);
		Assert.assertEquals("search should be TEST_SEARCH", filterText.getSearch(), TEST_SEARCH);
		Assert.assertEquals("replace should be TEST_REPLACE", filterText.getReplace(), TEST_REPLACE);
	}

	@Test
	public void testSetReplace() {
		WFilterText filterText = new WFilterText();
		filterText.setReplace(TEST_REPLACE);

		Assert.assertEquals("replace should be replace set", filterText.getReplace(), TEST_REPLACE);
	}

	@Test
	public void testSetSearch() {
		WFilterText filterText = new WFilterText();
		filterText.setSearch(TEST_SEARCH);

		Assert.assertEquals("search should be search set", filterText.getSearch(), TEST_SEARCH);
	}

	@Test
	public void testGetEncodedText() {
		WFilterText filterText = new WFilterText(TEST_SEARCH, TEST_REPLACE);
		filterText.setText(TEST_TEXT);

		String result = filterText.getText();

		String testText = new String(TEST_TEXT);
		String expectedResult = testText.replaceAll(TEST_SEARCH, TEST_REPLACE);

		Assert.assertEquals("should replace search substring with replace substring in text string",
				result, expectedResult);
	}
}
