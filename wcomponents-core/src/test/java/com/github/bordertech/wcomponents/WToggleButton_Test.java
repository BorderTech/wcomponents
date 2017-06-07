package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WToggleButton}.
 *
 * @author Ming Gao
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WToggleButton_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WToggleButton toggle = new WToggleButton();
		Assert.assertFalse("CheckBox should default to not selected", toggle.isSelected());
	}

	@Test
	public void testConstructorBoolean() {
		WToggleButton toggle = new WToggleButton(true);
		Assert.assertTrue("CheckBox should be selected", toggle.isSelected());
	}

	@Test
	public void testConstructorString() {
		String text = "Some text";
		WToggleButton toggle = new WToggleButton(text);
		Assert.assertEquals("Text should be the same", text, toggle.getText());
	}

	@Test
	public void testConstructorStringBoolean() {
		String text = "Some text";
		WToggleButton toggle = new WToggleButton(text, true);
		Assert.assertEquals("Text should be the same", text, toggle.getText());
	}

	@Test
	public void testSubmitOnChange() {
		WToggleButton toggle = new WToggleButton();
		try {
			toggle.setSubmitOnChange(true);
			Assert.fail("Exception expected");
		} catch (SystemException ex) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testDefaultSubmit() {
		WToggleButton toggle = new WToggleButton();
		WButton button = new WButton();
		try {
			toggle.setDefaultSubmitButton(button);
			Assert.fail("Exception expected");
		} catch (SystemException ex) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testMandatory() {
		WToggleButton toggle = new WToggleButton();
		Assert.assertFalse(toggle.isMandatory());
		toggle.setMandatory(true);
		Assert.assertFalse(toggle.isMandatory());
		toggle.setMandatory(false);
		Assert.assertFalse(toggle.isMandatory());
	}

	@Test
	public void testTextAccessors() {
		assertAccessorsCorrect(new WToggleButton(), "text", null, "A", "B");
	}

}
