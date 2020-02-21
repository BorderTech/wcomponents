package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ErrorCodeEscape}.
 */
public class ErrorCodeEscape_Test {

	@Test
	public void testConstructor1() {
		int code = 12;
		String msg = "Test message";
		ErrorCodeEscape esc = new ErrorCodeEscape(code, msg);
		Assert.assertEquals("Incorrect code returned", code, esc.getCode());
		Assert.assertEquals("Incorrect message returned", msg, esc.getMessage());
		Assert.assertNull("Cause should be null by default", esc.getCause());
	}

	@Test
	public void testConstructor2() {
		int code = 12;
		String msg = "Test message";
		Throwable cause = new NullPointerException();
		ErrorCodeEscape esc = new ErrorCodeEscape(code, msg, cause);
		Assert.assertEquals("Incorrect code returned", code, esc.getCode());
		Assert.assertEquals("Incorrect message returned", msg, esc.getMessage());
		Assert.assertEquals("Incorrect cause returned", cause, esc.getCause());
	}

	@Test
	public void testEscape() throws IOException {
		int code = 12;
		String msg = "Test message";
		MockResponse response = new MockResponse();

		// Setup escape
		ErrorCodeEscape esc = new ErrorCodeEscape(code, msg);
		esc.setResponse(response);
		// Do escape
		esc.escape();

		Assert.assertEquals("Incorrect code set on response", code, response.getErrorCode());
		Assert.assertEquals("Incorrect message set on response", msg, response.getErrorDescription());
	}

}
