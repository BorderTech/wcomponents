package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WText}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WText_Test extends AbstractWComponentTestCase {

	@Test
	public void testEncodeAccessors() {
		assertAccessorsCorrect(new WText(), "encodeText", true, false, true);
	}

	@Test
	public void testGetText() {
		WText wtf = new WText();
		String defaultText = "Dflt";
		String myText = "MyText";
		wtf.setText(defaultText);

		Assert.assertEquals("Incorrect default text", defaultText, wtf.getText());

		// Set test for a users session
		wtf.setLocked(true);
		setActiveContext(createUIContext());
		wtf.setText(myText);
		Assert.assertEquals("Should have session text", myText, wtf.getText());

		resetContext();
		Assert.assertEquals("Should have default text", defaultText, wtf.getText());
	}

	@Test
	public void testIsDefaultState() {
		String defaultText = "Dflt";
		String myText = "MyText";

		WText wtf = new WText(defaultText);

		wtf.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertTrue("Should be in default state by default", wtf.isDefaultState());

		wtf.setText(myText);
		Assert.assertFalse("Should not be in default state after setting different text", wtf.
				isDefaultState());

		wtf.setText(defaultText);
		Assert.assertTrue("Should be in default state after setting text to default text", wtf.
				isDefaultState());
	}
}
