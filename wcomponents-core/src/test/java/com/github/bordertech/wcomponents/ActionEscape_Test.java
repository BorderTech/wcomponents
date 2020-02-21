package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ActionEscape}.
 */
public class ActionEscape_Test {

	@Test
	public void testConstructor1() {
		ActionEscape esc = new ActionEscape();
		Assert.assertNull("Request should be null by default", esc.getRequest());
		Assert.assertNull("Response should be null by default", esc.getResponse());
	}

	@Test
	public void testConstructor2() {
		String msg = "Test message";
		Throwable cause = new NullPointerException();
		ActionEscape esc = new ActionEscape(msg, cause);
		Assert.assertEquals("Incorrect message returned", msg, esc.getMessage());
		Assert.assertEquals("Incorrect cause returned", cause, esc.getCause());
	}
}
