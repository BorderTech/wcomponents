package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 * ServletUtil - unit tests for {@link ServletUtil}.
 *
 * @author Rick Brown
 */
public class ServletUtilTest {

	@Test
	public void testExtractCookie() {
		String cookieName = "mycookiename";
		String cookieValue = "mycookievalue";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookie(cookieName, cookieValue);

		Assert.assertEquals("Got cookie value by name", cookieValue, ServletUtil.extractCookie(httpServletRequest, cookieName));
	}

	@Test
	public void testExtractCookieAmongstMany() {
		String cookieName = "mycookiename";
		String cookieValue = "mycookievalue";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		for (byte i = 0; i < 10; i++) {
			httpServletRequest.setCookie(cookieName + i, cookieValue + 1);
		}
		httpServletRequest.setCookie(cookieName, cookieValue);

		Assert.assertEquals("Got cookie value by name", cookieValue, ServletUtil.extractCookie(httpServletRequest, cookieName));
	}

	@Test
	public void testExtractCookieNoneFound() {
		String cookieName = "mycookiename";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		Assert.assertNull("Got cookie value by name", ServletUtil.extractCookie(httpServletRequest, cookieName));
	}
}
