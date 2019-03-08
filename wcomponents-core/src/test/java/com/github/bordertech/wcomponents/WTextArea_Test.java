package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.type.Multiline;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WTextArea}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTextArea_Test extends AbstractWComponentTestCase {

	@Test
	public void testCarriageReturn() {
		WTextArea textArea = new WTextArea();
		textArea.setText("\r");
		Assert.assertNull("Value for CR by itself should return null", textArea.getText());
		Assert.assertEquals("Data for CR by itself should be the CR", "\r", textArea.getData());
		Assert.assertTrue("CR by itself should be treated as empty", textArea.isEmpty());
	}

	@Test
	public void testCarriageReturnWithText() {
		WTextArea textArea = new WTextArea();
		textArea.setText("A\r");
		Assert.assertEquals("Value for CR with text should have text and CR", "A\r", textArea.getText());
		Assert.assertEquals("Data for CR with text should be the text and CR", "A\r", textArea.getData());
		Assert.assertFalse("CR with text should not be empty", textArea.isEmpty());
	}

	@Test
	public void testCarriageReturnRich() {
		WTextArea textArea = new WTextArea();
		textArea.setRichTextArea(true);
		textArea.setText("\r");
		Assert.assertNull("Value for CR by itself with RichText should return null", textArea.getText());
		Assert.assertEquals("Data for CR by itself with RichText should be the CR", "\r", textArea.getData());
		Assert.assertTrue("CR by itself with RichText should be treated as empty", textArea.isEmpty());
	}

	@Test
	public void testCarriageReturnWithTextRich() {
		WTextArea textArea = new WTextArea();
		textArea.setRichTextArea(true);
		textArea.setText("A\r");
		Assert.assertEquals("Value for CR with text should have text and CR replaced", "A ", textArea.getText());
		Assert.assertEquals("Data for CR with text should be the text and CR replaced", "A ", textArea.getData());
		Assert.assertFalse("CR with text should not be empty", textArea.isEmpty());
	}

	@Test
	public void testLineFeed() {
		WTextArea textArea = new WTextArea();
		textArea.setText("\n");
		Assert.assertNull("Value for LF by itself should return null", textArea.getText());
		Assert.assertEquals("Data for LF by itself should be the LF", "\n", textArea.getData());
		Assert.assertTrue("LF by itself should be treated as empty", textArea.isEmpty());
	}

	@Test
	public void testLineFeedWithText() {
		WTextArea textArea = new WTextArea();
		textArea.setText("A\n");
		Assert.assertEquals("Value for LF with text should have text and LF", "A\n", textArea.getText());
		Assert.assertEquals("Data for LF with text should be the text and LF", "A\n", textArea.getData());
		Assert.assertFalse("CR with text should not be empty", textArea.isEmpty());
	}

	@Test
	public void testLineFeedRich() {
		WTextArea textArea = new WTextArea();
		textArea.setRichTextArea(true);
		textArea.setText("\n");
		Assert.assertNull("Value for LF by itself with RichText should return null", textArea.getText());
		Assert.assertEquals("Data for LF by itself with RichText should the LF", "\n", textArea.getData());
		Assert.assertTrue("LF by itself with RichText should be treated as empty", textArea.isEmpty());
	}

	@Test
	public void testLineFeedWithTextRich() {
		WTextArea textArea = new WTextArea();
		textArea.setRichTextArea(true);
		textArea.setText("A\n");
		Assert.assertEquals("Value for LF with text should have text and LF replaced", "A ", textArea.getText());
		Assert.assertEquals("Data for LF with text should be the text and LF replaced", "A ", textArea.getData());
		Assert.assertFalse("LF with text should not be empty", textArea.isEmpty());
	}

	@Test
	public void testRowsAccessors() {
		assertAccessorsCorrect(new WTextArea(), "rows", 0, 1, 2);
	}

	@Test
	public void testRTFAccessors() {
		assertAccessorsCorrect(new WTextArea(), "richTextArea", false, true, false);
	}

	@Test
	public void testSanitizeOnOutputAccessors() {
		assertAccessorsCorrect(new WTextArea(), "sanitizeOnOutput", true, false, true);
	}

	@Test
	public void testPlaceholderAccessors() {
		assertAccessorsCorrect(new WTextArea(), "placeholder", null, "A", "B");
	}

	@Test
	public void testNoSanitizeOnOutput() {
		String input = "<form>content</form>";
		WTextArea textArea = new WTextArea();
		textArea.setData(input);
		textArea.setSanitizeOnOutput(false);
		// do not setRichText until after setData otherwise content will be sanitized on setData
		textArea.setRichTextArea(true);
		Assert.assertEquals("Expect output to not be sanitized", input, textArea.getText());
	}

	@Test
	public void testSanitizeOnOutput() {
		WTextArea textArea = new WTextArea();
		textArea.setData("<form>content</form>");
		textArea.setSanitizeOnOutput(true);
		// do not setRichText until after setData otherwise content will be sanitized on setData
		textArea.setRichTextArea(true);
		Assert.assertEquals("Expect output to be sanitized", "content", textArea.getText());
	}

	// autocomplete
	@Test
	public void testSetAutocomplete() {
		WTextArea textArea = new WTextArea();
		for (Multiline value : Multiline.values()) {
			textArea.setAutocomplete(value);
			Assert.assertEquals(value.getValue(), textArea.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullValue() {
		WTextArea textArea = new WTextArea();
		textArea.setAutocomplete(Multiline.STREET_ADDRESS);
		Assert.assertNotNull(textArea.getAutocomplete());
		textArea.setAutocomplete((Multiline) null);
		Assert.assertNull(textArea.getAutocomplete());
	}

	@Test
	public void testSetFullStreetAddressAutocompleteWithType() {
		WTextArea textArea = new WTextArea();
		String expected;
		for (AddressType value : AddressType.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(value.getValue(), Multiline.STREET_ADDRESS.getValue());
			textArea.setFullStreetAddressAutocomplete(value);
			Assert.assertEquals(expected, textArea.getAutocomplete());
		}
	}

	@Test
	public void testSetFullAddressAutocompleteWithNullType() {
		WTextArea textArea = new WTextArea();
		textArea.setFullStreetAddressAutocomplete(null);
		Assert.assertEquals(Multiline.STREET_ADDRESS.getValue(), textArea.getAutocomplete());
	}

	@Test
	public void testSetFullAddressAutocomplete() {
		WTextArea textArea = new WTextArea();
		textArea.setFullStreetAddressAutocomplete();
		Assert.assertEquals(Multiline.STREET_ADDRESS.getValue(), textArea.getAutocomplete());
	}

}
