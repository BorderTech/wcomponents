package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link Escape}.
 */
public class Escape_Test {

	@Test
	public void testConstructor1() {
		Escape esc = new Escape();
		Assert.assertNull("Request should be null by default", esc.getRequest());
		Assert.assertNull("Response should be null by default", esc.getResponse());
	}

	@Test
	public void testConstructor2() {
		String msg = "Test message";
		Throwable cause = new NullPointerException();
		Escape esc = new Escape(msg, cause);
		Assert.assertEquals("Incorrect message returned", msg, esc.getMessage());
		Assert.assertEquals("Incorrect cause returned", cause, esc.getCause());
	}

	@Test
	public void testResponseAccessors() {
		MockResponse response = new MockResponse();
		Escape esc = new Escape();
		esc.setResponse(response);
		Assert.assertEquals("Incorrect response returned", response, esc.getResponse());
	}

	@Test
	public void testRequestAccessors() {
		MockRequest request = new MockRequest();
		Escape esc = new Escape();
		esc.setRequest(request);
		Assert.assertEquals("Incorrect request returned", request, esc.getRequest());
	}
}
