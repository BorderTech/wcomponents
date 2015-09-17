package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WStyledText.Type;
import com.github.bordertech.wcomponents.WStyledText.WhitespaceMode;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WStyledText}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WStyledText_Test extends AbstractWComponentTestCase {

	/**
	 * Test text value.
	 */
	private static final String TEST_STRING = "test";

	@Test
	public void testConstructor1() {
		WStyledText text = new WStyledText();
		Assert.assertNull("Constructor 1 - text should default to null", text.getText());
		Assert.assertEquals("Constructor 1 - type should default to PLAIN", Type.PLAIN, text.
				getType());
		Assert.assertEquals("Constructor 1 - whitspace mode should default to DEFAULT",
				WhitespaceMode.DEFAULT, text.getWhitespaceMode());
	}

	@Test
	public void testConstructor2() {
		WStyledText text = new WStyledText(TEST_STRING);
		Assert.
				assertEquals("Constructor 2 - text returned wrong value", TEST_STRING, text.
						getText());
		Assert.assertEquals("Constructor 2 - type should default to PLAIN", Type.PLAIN, text.
				getType());
		Assert.assertEquals("Constructor 2 - whitspace mode should default to DEFAULT",
				WhitespaceMode.DEFAULT, text.getWhitespaceMode());
	}

	@Test
	public void testConstructor3() {
		WStyledText text = new WStyledText(TEST_STRING, Type.EMPHASISED);
		Assert.
				assertEquals("Constructor 3 - text returned wrong value", TEST_STRING, text.
						getText());
		Assert.assertEquals("Constructor 3 - type returned wrong value", Type.EMPHASISED, text.
				getType());
		Assert.assertEquals("Constructor 3 - whitspace mode should default to DEFAULT",
				WhitespaceMode.DEFAULT, text.getWhitespaceMode());
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WStyledText(), "type", Type.PLAIN, Type.DELETE, Type.EMPHASISED);
	}

	@Test
	public void testWhitespaceModeAccessors() {
		assertAccessorsCorrect(new WStyledText(), "whitespaceMode", WhitespaceMode.DEFAULT,
				WhitespaceMode.PARAGRAPHS, WhitespaceMode.PRESERVE);
	}
}
