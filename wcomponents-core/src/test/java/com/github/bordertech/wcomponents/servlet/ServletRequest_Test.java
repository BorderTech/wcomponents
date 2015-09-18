package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpSession;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpSession;
import junit.framework.Assert;
import org.junit.Test;

/**
 * ServletRequest_Test - unit tests for {@link ServletRequest}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ServletRequest_Test {

	@Test
	public void testConstructor() {
		String paramName = "ServletRequest_Test.testConstructor.paramName";
		String paramValue = "ServletRequest_Test.testConstructor.paramValue";

		MockHttpServletRequest backing = new MockHttpServletRequest();
		backing.setParameter(paramName, paramValue);

		ServletRequest request = new ServletRequest(backing);

		backing.removeParameter(paramName);
		Assert.assertEquals("Request should have a local copy of the parameters", paramValue,
				request.getParameter(paramName));
	}

	@Test
	public void testAttributeAccessors() {
		String attributeName = "ServletRequest_Test.testAttributeAccessors.attributeName";
		String attributeValue1 = "ServletRequest_Test.testAttributeAccessors.attributeValue1";
		String attributeValue2 = "ServletRequest_Test.testAttributeAccessors.attributeValue2";

		MockHttpServletRequest backing = new MockHttpServletRequest();
		backing.setAttribute(attributeName, attributeValue1);

		ServletRequest request = new ServletRequest(backing);
		Assert.assertEquals("Incorrect attribute value",
				attributeValue1, request.getAttribute(attributeName));

		request.setAttribute(attributeName, attributeValue2);
		Assert.assertEquals("Incorrect attribute value after setAttribute",
				attributeValue2, request.getAttribute(attributeName));
	}

	@Test
	public void testRenderParameterAccessors() {
		String renderParamName = "ServletRequest_Test.testRenderParameterAccessors.renderParamName";
		String renderParamValue = "ServletRequest_Test.testRenderParameterAccessors.renderParamValue";

		// Set the render parameters on a request
		HttpSession session = new MockHttpSession();
		MockHttpServletRequest backing = new MockHttpServletRequest(session);
		ServletRequest request = new ServletRequest(backing);
		request.setRenderParameter(renderParamName, renderParamValue);

		Assert.assertEquals("Incorrect render parameter value after setRenderParameter",
				renderParamValue, request.getRenderParameter(renderParamName));

		// Test that the render parameters were stored in the session
		request = new ServletRequest(new MockHttpServletRequest(session));
		Assert.assertEquals("Incorrect render parameter value from another request",
				renderParamValue, request.getRenderParameter(renderParamName));
	}

	@Test
	public void testSessionAttributeAccessors() {
		String attributeName = "ServletRequest_Test.testSessionAttributeAccessors.attributeName";
		String attributeValue1 = "ServletRequest_Test.testSessionAttributeAccessors.attributeValue1";
		String attributeValue2 = "ServletRequest_Test.testSessionAttributeAccessors.attributeValue2";

		// Test with no session
		MockHttpServletRequest backing = new MockHttpServletRequest();
		ServletRequest request = new ServletRequest(backing);

		Assert.assertNull("Attribute should be null if no session",
				request.getSessionAttribute(attributeName));

		request.setSessionAttribute(attributeName, attributeValue1);
		Assert.assertEquals("Incorrect attribute value after setSessionAttribute",
				attributeValue1, request.getSessionAttribute(attributeName));

		// Test with a session
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(attributeName, attributeValue1);
		backing = new MockHttpServletRequest(session);
		request = new ServletRequest(backing);

		Assert.assertEquals("Incorrect attribute value",
				attributeValue1, request.getSessionAttribute(attributeName));

		request.setSessionAttribute(attributeName, attributeValue2);
		Assert.assertEquals("Incorrect attribute value after setSessionAttribute",
				attributeValue2, request.getSessionAttribute(attributeName));
	}

	@Test
	public void testAppSessionAttributeAccessors() {
		String attributeName = "ServletRequest_Test.testAppSessionAttributeAccessors.attributeName";
		String attributeValue1 = "ServletRequest_Test.testAppSessionAttributeAccessors.attributeValue1";
		String attributeValue2 = "ServletRequest_Test.testAppSessionAttributeAccessors.attributeValue2";

		// Test with no session
		MockHttpServletRequest backing = new MockHttpServletRequest();
		ServletRequest request = new ServletRequest(backing);

		Assert.assertNull("Attribute should be null if no session",
				request.getAppSessionAttribute(attributeName));

		request.setAppSessionAttribute(attributeName, attributeValue1);
		Assert.assertEquals("Incorrect attribute value after setAppSessionAttribute",
				attributeValue1, request.getAppSessionAttribute(attributeName));

		// Test with a session
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(attributeName, attributeValue1);
		backing = new MockHttpServletRequest(session);
		request = new ServletRequest(backing);

		Assert.assertEquals("Incorrect attribute value",
				attributeValue1, request.getAppSessionAttribute(attributeName));

		request.setAppSessionAttribute(attributeName, attributeValue2);
		Assert.assertEquals("Incorrect attribute value after setAppSessionAttribute",
				attributeValue2, request.getAppSessionAttribute(attributeName));
	}

	@Test
	public void testFileUpload() throws UnsupportedEncodingException {
		String queryParamName = "ServletRequest_Test.testFileUpload.queryParamName";
		String queryParamValue = "ServletRequest_Test.testFileUpload.queryParamValue";
		String formParamName = "ServletRequest_Test.testFileUpload.formParamName";
		String formParamValue = "ServletRequest_Test.testFileUpload.formParamValue";
		String fileParamName = "ServletRequest_Test.testFileUpload.fileParamName";
		String fileName = "ServletRequest_Test.testFileUpload.fileName";
		String fileContents = "ServletRequest_Test.testFileUpload.fileContents";

		MockHttpServletRequest backing = new MockHttpServletRequest();
		backing.setContentType("multipart/form-data; boundary=zzzzzz");
		backing.setParameter(queryParamName, queryParamValue);

		String formContent
				= "--zzzzzz"
				+ "\r\ncontent-disposition: form-data; name=\"" + formParamName + "\""
				+ "\r\n"
				+ "\r\n" + formParamValue
				+ "\r\n--zzzzzz"
				+ "\r\ncontent-disposition: form-data; name=\"" + fileParamName + "\""
				+ "\r\nContent-type: multipart/mixed; boundary=yyyyyy"
				+ "\r\n"
				+ "\r\n--yyyyyy"
				+ "\r\nContent-disposition: attachment; filename=\"" + fileName + "\""
				+ "\r\nContent-Type: text/plain"
				+ "\r\n"
				+ "\r\n" + fileContents
				+ "\r\n--yyyyyy--"
				+ "\r\n--zzzzzz--";

		backing.setContent(formContent.getBytes("UTF-8"));

		ServletRequest request = new ServletRequest(backing);

		Assert.assertEquals("Incorrect query parameter value",
				queryParamValue, request.getParameter(queryParamName));

		Assert.assertEquals("Incorrect form parameter value",
				formParamValue, request.getParameter(formParamName));

		Assert.assertEquals("Incorrect file name",
				fileName, request.getParameter(fileParamName));

		Assert.assertEquals("Incorrect file contents",
				fileContents, new String(request.getFileContents(fileParamName), "UTF-8"));
	}
}
