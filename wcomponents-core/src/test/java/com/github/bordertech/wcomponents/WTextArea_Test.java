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
