package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WTextArea}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
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
		assertAccessorsCorrect(new WTextArea(), "sanitizeOnOutput", false, true, false);
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
}
