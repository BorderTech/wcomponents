package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockServletConfig;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * ThemeServlet_Test - unit tests for {@link ThemeServlet}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ThemeServlet_Test {

	/**
	 * The servlet to test.
	 */
	private ThemeServlet themeServlet;

	@Before
	public void setUp() throws ServletException {
		themeServlet = new ThemeServlet();
		themeServlet.init(new MockServletConfig());
	}

	@Test
	public void testInvalidFiles() throws ServletException, IOException {
		// Test Invalid due to relative path
		MockHttpServletResponse response = requestFile("../wcomponents.properties", false);
		Assert.assertEquals("Should have returned 404", HttpServletResponse.SC_NOT_FOUND, response.
				getStatus());

		response = requestFile("../wcomponents.properties", true);
		Assert.assertEquals("Should have returned 404", HttpServletResponse.SC_NOT_FOUND, response.
				getStatus());

		response = requestFile("/wcomponents.properties", false);
		Assert.assertEquals("Should have returned 404", HttpServletResponse.SC_NOT_FOUND, response.
				getStatus());

		response = requestFile("/wcomponents.properties", true);
		Assert.assertEquals("Should have returned 404", HttpServletResponse.SC_NOT_FOUND, response.
				getStatus());

		// Invalid due to non-existant resource
		response = requestFile("xslt/ThemeServlet_Test.testInvalidFiles", false);
		Assert.assertEquals("Should have returned 404", HttpServletResponse.SC_NOT_FOUND, response.
				getStatus());
	}

	@Test
	public void testValidFile() throws ServletException, IOException {
		MockHttpServletResponse response = requestFile("ThemeServlet_Testfile.xsl", false);
		Assert.assertEquals("Should have returned 200", HttpServletResponse.SC_OK, response.
				getStatus());
		Assert.assertEquals("Incorrect MIME type", Config.getInstance().getString(
				"bordertech.wcomponents.mimeType.xsl"), response.getContentType());

		response = requestFile("ThemeServlet_Testfile.xsl", true);
		Assert.assertEquals("Should have returned 200", HttpServletResponse.SC_OK, response.
				getStatus());
		Assert.assertEquals("Incorrect MIME type", Config.getInstance().getString(
				"bordertech.wcomponents.mimeType.xsl"), response.getContentType());
	}

	/**
	 * Requests a file from the servlet.
	 *
	 * @param path the path to the file
	 * @param asParam true to use the ThemeServlet's 'f' parameter or false to place the path in the URL.
	 * @return the servlet response
	 * @throws ServletException a servlet exception
	 * @throws IOException an exception
	 */
	private MockHttpServletResponse requestFile(final String path, final boolean asParam) throws
			ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		if (asParam) {
			request.setRequestURI("/theme");
			request.setParameter("f", path);
		} else {
			request.setRequestURI("/theme/" + path);
			request.setPathInfo('/' + path);
		}

		themeServlet.doGet(request, response);
		return response;
	}
}
