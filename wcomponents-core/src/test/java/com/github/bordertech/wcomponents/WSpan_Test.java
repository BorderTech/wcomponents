package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WSpan}.
 */
public class WSpan_Test extends AbstractWComponentTestCase {

	private static final String TEST_STRING = "test";

	@Test
	public void testConstructor1() {
		WSpan span = new WSpan();
		Assert.assertNull("Constructor 1 - text should default to null", span.getText());
	}

	@Test
	public void testConstructor2() {
		WSpan span = new WSpan(TEST_STRING);
		Assert.assertEquals("Constructor 2 - text returned wrong value", TEST_STRING, span.getText());
	}

	@Test
	public void testAriaHiddenAccessors() {
		assertAccessorsCorrect(new WSpan(), WSpan::isAriaHidden, WSpan::setAriaHidden, false, false, true);
	}

	@Test
	public void testImageRoleAccessors() {
		assertAccessorsCorrect(new WSpan(), WSpan::isImageRole, WSpan::setImageRole, false, false, true);
	}

}
