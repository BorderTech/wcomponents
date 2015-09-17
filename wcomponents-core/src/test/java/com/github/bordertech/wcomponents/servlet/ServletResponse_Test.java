package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletResponse;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * ServletResponse_Test - unit tests for {@link ServletResponse}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ServletResponse_Test {

	@Test
	public void testGetWriter() throws IOException {
		MockHttpServletResponse backing = new MockHttpServletResponse();
		ServletResponse response = new ServletResponse(backing);

		Assert.assertSame("Incorrect writer",
				backing.getWriter(), response.getWriter());
	}

	@Test
	public void testGetOutputStream() throws IOException {
		MockHttpServletResponse backing = new MockHttpServletResponse();
		ServletResponse response = new ServletResponse(backing);

		Assert.assertSame("Incorrect outputStream",
				backing.getOutputStream(), response.getOutputStream());
	}

	@Test
	public void testSetContentType() {
		String contentType = "ServletResponse_Test.testSetContentType.contentType";

		MockHttpServletResponse backing = new MockHttpServletResponse();
		ServletResponse response = new ServletResponse(backing);

		response.setContentType(contentType);

		Assert.assertEquals("Incorrect contentType", contentType, backing.getContentType());
	}

	@Test
	public void testSetHeader() {
		String headerName = "ServletResponse_Test.testSetHeader.headerName";
		String headerValue = "ServletResponse_Test.testSetHeaderType.headerValue";

		MockHttpServletResponse backing = new MockHttpServletResponse();
		ServletResponse response = new ServletResponse(backing);

		response.setHeader(headerName, headerValue);

		Assert.assertEquals("Incorrect header", headerValue, backing.getHeader(headerName));
	}

	@Test
	public void testSendRedirect() throws IOException {
		String redirect = "http://test.invalid/ServletResponse_Test.testSendRedirect.redirect";

		MockHttpServletResponse backing = new MockHttpServletResponse();
		ServletResponse response = new ServletResponse(backing);

		response.sendRedirect(redirect);

		Assert.assertEquals("Incorrect redirect", redirect, backing.getSendRedirection());
	}

	@Test
	public void testSendError() throws IOException {
		int errorCode = 505; // something relatively obscure

		MockHttpServletResponse backing = new MockHttpServletResponse();
		ServletResponse response = new ServletResponse(backing);

		response.sendError(errorCode, "dummy");

		Assert.assertEquals("Incorrect error", errorCode, backing.getStatus());
	}
}
