package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * ForwardException_Test - Unit tests for {@link ForwardException}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ForwardException_Test {

	/**
	 * The URL to test with.
	 */
	private static final String URL = "http://localhost/ForwardException_Test.url";

	@Test
	public void testGetForwardTo() {
		ForwardException forward = new ForwardException(URL);
		Assert.assertEquals("Incorrect url", URL, forward.getForwardTo());
	}

	@Test
	public void testEscape() throws IOException {
		ForwardException forward = new ForwardException(URL);
		MockResponse response = new MockResponse();
		forward.setRequest(new MockRequest());
		forward.setResponse(response);
		forward.escape();

		Assert.assertEquals("Should have forwarded to url", URL, response.getRedirect());
	}
}
