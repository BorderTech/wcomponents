package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.ServletRequest;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Test cases for the {@link TransformXMLInterceptor} class.
 *
 * @author Rick Brown
 * @since 1.0.0
 */
public class TransformXMLInterceptor_Test extends AbstractWComponentTestCase {

	private static final String TEST_XML = "<kung><fu>is good for you</fu></kung>";

	/**
	 * When these tests are done put things back as they were.
	 */
	@AfterClass
	public static void tearDownClass() {
		Config.reset();
	}

	/**
	 * Ensure that the interceptor does nothing as long as the controlling property is disabled.
	 */
	@Test
	public void testPaintWhileDisabled() {
		MyComponent testUI = new MyComponent(TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		Config.getInstance().setProperty(ConfigurationProperties.XSLT_SERVER_SIDE, "false");
		TestResult actual = generateOutput(testUI);
		Assert.assertEquals("XML should not be transformed when interceptor disabled", TEST_XML, actual.result);
	}

	/**
	 * Ensure that the interceptor does nothing when the user agent string opts out.
	 */
	@Test
	public void testPaintWithUserAgentOverride() {
		MyComponent testUI = new MyComponent(TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		Config.getInstance().setProperty(ConfigurationProperties.XSLT_SERVER_SIDE, "true");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("User-Agent", "Mozilla/5.0 Firefox/26.0 wcnoxslt");
		TestResult actual = generateOutput(testUI, headers);
		Assert.assertEquals("XML should not be transformed when useragent string flag present", TEST_XML, actual.result);
	}

	/**
	 * Ensure that the interceptor does nothing as long as the controlling property is disabled.
	 */
	@Test
	public void testPaintWhileEnabledWithThemeContentPathSet() {
		MyComponent testUI = new MyComponent(TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "set");
		Config.getInstance().setProperty(ConfigurationProperties.XSLT_SERVER_SIDE, "true");
		TestResult actual = generateOutput(testUI);
		Assert.assertEquals("XML should not be transformed when interceptor enabled but theme content path set", TEST_XML, actual.result);
	}

	/**
	 * Test that the interceptor transforms our XML when it is enabled.
	 */
	@Test
	public void testPaintWhileEnabled() {
		final String expected = "<omg><wtf>is good for you</wtf></omg>";
		MyComponent testUI = new MyComponent(TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		Config.getInstance().setProperty(ConfigurationProperties.XSLT_SERVER_SIDE, "true");
		TestResult actual = generateOutput(testUI);
		Assert.assertEquals("XML should be transformed when interceptor enabled", expected, actual.result);
		Assert.assertEquals("The content type should be correctly set", WebUtilities.CONTENT_TYPE_HTML, actual.contentType);
	}

	/**
	 * Test that the interceptor transforms our XML when it is enabled.
	 */
	@Test
	public void testPaintWhileEnabledWithChromeUserAgent() {
		final String expected = "<omg><wtf>is good for you</wtf></omg>";
		MyComponent testUI = new MyComponent(TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		Config.getInstance().setProperty(ConfigurationProperties.XSLT_SERVER_SIDE, "true");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
		TestResult actual = generateOutput(testUI, headers);
		Assert.assertEquals("XML should be transformed when interceptor enabled", expected, actual.result);
		Assert.assertEquals("The content type should be correctly set", WebUtilities.CONTENT_TYPE_HTML, actual.contentType);
	}

	/**
	 * A 'fake' WComponent that renders the string we pass to the constructor.
	 */
	private static final class MyComponent extends WContainer {

		/**
		 * Content to be painted by the test component.
		 */
		private final String content;

		/**
		 * @param content the test content
		 */
		private MyComponent(final String content) {
			this.content = content;
		}

		/**
		 * Simply render the string that was passed to the constructor.
		 *
		 * @param renderContext
		 */
		@Override
		protected void paintComponent(final RenderContext renderContext) {
			((WebXmlRenderContext) renderContext).getWriter().print(content);
			super.paintComponent(renderContext);
		}
	}

	/**
	 * Render the component and execute the interceptor.
	 *
	 * @param testUI the test component
	 * @return the response
	 */
	private TestResult generateOutput(final MyComponent testUI) {
		return generateOutput(testUI, null);
	}

	/**
	 * Render the component and execute the interceptor.
	 *
	 * @param testUI the test component
	 * @param headers Request headers to set (key/value pairs).
	 * @return the response
	 */
	private TestResult generateOutput(final MyComponent testUI, final Map<String, String> headers) {
		InterceptorComponent interceptor = new TransformXMLInterceptor();
		interceptor.setBackingComponent(testUI);

		MockHttpServletRequest backing = new MockHttpServletRequest();
		if (headers != null) {
			for (String headerName : headers.keySet()) {
				backing.setHeader(headerName, headers.get(headerName));
			}
		}
		ServletRequest request = new ServletRequest(backing);

		MockResponse response = new MockResponse();
		interceptor.attachResponse(response);

		StringWriter writer = new StringWriter();
		UIContext uic = createUIContext();
		uic.setLocale(new Locale("xx"));
		setActiveContext(uic);

		try {
			interceptor.preparePaint(request);
			interceptor.paint(new WebXmlRenderContext(new PrintWriter(writer)));
		} finally {
			resetContext();
		}

		return new TestResult(writer.toString(), response.getContentType());
	}

	/**
	 * A simple DTO to pass back the results of the render to the calling test.
	 */
	private final class TestResult {

		private String result;
		private String contentType;

		/**
		 * Create and instance of the DTO.
		 *
		 * @param result The rendered output of the UI component.
		 * @param contentType The content type of the response.
		 */
		private TestResult(final String result, final String contentType) {
			this.result = result;
			this.contentType = contentType;
		}
	}
}
