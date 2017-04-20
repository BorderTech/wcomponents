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
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Test cases for the {@link TransformXMLInterceptor} class.
 *
 * @author Rick Brown
 * @since 1.0.0
 */
public class TransformXMLInterceptor_Test extends AbstractWComponentTestCase {

	/**
	 * The corrupt character input xml.
	 */
	private static final String TEST_CORRUPT_CHAR_XML = buildCorruptXML();

	/**
	 * When these tests are done put things back as they were.
	 */
	@AfterClass
	public static void tearDownClass() {
		Config.reset();
		TransformXMLTestHelper.reloadTransformer();
	}

	/**
	 * Ensure that the interceptor does nothing when the user agent string opts out.
	 */
	@Test
	public void testPaintWithUserAgentOverride() {
		MyComponent testUI = new MyComponent(TransformXMLTestHelper.TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		TransformXMLTestHelper.reloadTransformer();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("User-Agent", "Mozilla/5.0 Firefox/26.0 wcnoxslt");
		TestResult actual = generateOutput(testUI, headers);
		Assert.assertEquals("XML should not be transformed when useragent string flag present", TransformXMLTestHelper.TEST_XML, actual.result);
	}

	/**
	 * Ensure that the interceptor does nothing as long as the controlling property is disabled.
	 */
	@Test
	public void testPaintWhileEnabledWithThemeContentPathSet() {
		MyComponent testUI = new MyComponent(TransformXMLTestHelper.TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "set");
		TransformXMLTestHelper.reloadTransformer();
		TestResult actual = generateOutput(testUI);
		Assert.assertEquals("XML should be transformed when interceptor enabled and theme content path set", TransformXMLTestHelper.EXPECTED, actual.result);
	}

	/**
	 * Test that the interceptor transforms our XML when it is enabled.
	 */
	@Test
	public void testPaintWhileEnabled() {
		MyComponent testUI = new MyComponent(TransformXMLTestHelper.TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		TransformXMLTestHelper.reloadTransformer();
		TestResult actual = generateOutput(testUI);
		Assert.assertEquals("XML should be transformed when interceptor enabled", TransformXMLTestHelper.EXPECTED, actual.result);
		Assert.assertEquals("The content type should be correctly set", WebUtilities.CONTENT_TYPE_HTML, actual.contentType);
	}

	/**
	 * Test that the interceptor transforms our XML when it is enabled.
	 */
	@Test
	public void testPaintWhileEnabledWithChromeUserAgent() {

		MyComponent testUI = new MyComponent(TransformXMLTestHelper.TEST_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		TransformXMLTestHelper.reloadTransformer();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
		TestResult actual = generateOutput(testUI, headers);
		Assert.assertEquals("XML should be transformed when interceptor enabled", TransformXMLTestHelper.EXPECTED, actual.result);
		Assert.assertEquals("The content type should be correctly set", WebUtilities.CONTENT_TYPE_HTML, actual.contentType);
	}

	/**
	 * Ensure that the interceptor does nothing as long as the controlling property is disabled.
	 */
	@Test
	public void testPaintWithCorruptCharacterException() {
		MyComponent testUI = new MyComponent(TEST_CORRUPT_CHAR_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		Config.getInstance().setProperty(ConfigurationProperties.XSLT_ALLOW_CORRUPT_CHARACTER, "false");
		TransformXMLTestHelper.reloadTransformer();

		testUI.setLocked(true);

		UIContext uic = createUIContext();
		uic.setUI(testUI);
		setActiveContext(uic);

		try {
			generateOutput(testUI, null);
			Assert.fail("Corrupt character in XML should have failed.");
		} catch (Exception e) {
			Assert.assertTrue("Should contain could not transform", e.getMessage().contains("Could not transform"));
		}
	}

	/**
	 * Ensure that the interceptor does nothing as long as the controlling property is disabled.
	 *
	 * @throws java.lang.NoSuchFieldException
	 * @throws java.lang.IllegalAccessException
	 */
	@Test
	public void testPaintWithCorruptCharacterAllowed() throws NoSuchFieldException, IllegalAccessException {
		/**
		 * Have to use reflection to swap out the log implementation as there's no way to programmatically disable
		 * logging without coupling to a log implementation.
		 */
		//Override the logger temporarily.
		Field field = TransformXMLInterceptor.class.getDeclaredField("LOG");
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		Object oldValue = field.get(null);
		field.set(null, new NoLogLogger());

		MyComponent testUI = new MyComponent(TEST_CORRUPT_CHAR_XML);
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		Config.getInstance().setProperty(ConfigurationProperties.XSLT_ALLOW_CORRUPT_CHARACTER, "true");
		TransformXMLTestHelper.reloadTransformer();

		testUI.setLocked(true);

		UIContext uic = createUIContext();
		uic.setUI(testUI);
		setActiveContext(uic);

		TestResult actual = generateOutput(testUI, null);

		//Set the original value
		Field resetValueField = TransformXMLInterceptor.class.getDeclaredField("LOG");
		resetValueField.setAccessible(true);
		field.set(null, oldValue);
	}

	/**
	 *
	 * @return XML with bad characters
	 */
	private static String buildCorruptXML() {

		StringBuilder data = new StringBuilder();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (!Character.isValidCodePoint(i)) {
				continue;
			}
			char ch = (char) i;
			if (ch != '>' && ch != '<' && ch != '&' && ch != '"') {
				data.append(ch);
			}
		}

		String utfString = "<kung><fu>" + data.toString() + "</fu></kung>";
		String isoString = null;
		try {
			byte[] bytes = utfString.getBytes("UTF8");
			isoString = new String(bytes, "ISO-8859-1");
		} catch (final Exception e) {
			throw new SystemException("Error translating. " + e.getMessage());
		}
		return isoString;
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
		interceptor.attachUI(testUI);

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
		uic.setLocale(new Locale("en"));
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

	/**
	 * Custom Log implementation to prevent all logging.
	 *
	 */
	public final static class NoLogLogger implements Log {

		@Override
		public boolean isDebugEnabled() {
			return false;
		}

		@Override
		public boolean isErrorEnabled() {
			return false;
		}

		@Override
		public boolean isFatalEnabled() {
			return false;
		}

		@Override
		public boolean isInfoEnabled() {
			return false;
		}

		@Override
		public boolean isTraceEnabled() {
			return false;
		}

		@Override
		public boolean isWarnEnabled() {
			return false;
		}

		@Override
		public void trace(Object message) {
			// No-impl
		}

		@Override
		public void trace(Object message, Throwable t) {
			// No-impl
		}

		@Override
		public void debug(Object message) {
			// No-impl
		}

		@Override
		public void debug(Object message, Throwable t) {
			// No-impl
		}

		@Override
		public void info(Object message) {
			// No-impl
		}

		@Override
		public void info(Object message, Throwable t) {
			// No-impl
		}

		@Override
		public void warn(Object message) {
			// No-impl
		}

		@Override
		public void warn(Object message, Throwable t) {
			// No-impl
		}

		@Override
		public void error(Object message) {
			// No-impl
		}

		@Override
		public void error(Object message, Throwable t) {
			// No-impl
		}

		@Override
		public void fatal(Object message) {
			// No-impl
		}

		@Override
		public void fatal(Object message, Throwable t) {
			// No-impl
		}

	}
}
