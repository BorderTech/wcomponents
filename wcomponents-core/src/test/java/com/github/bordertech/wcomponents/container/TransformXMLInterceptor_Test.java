package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
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
		Config.getInstance().setProperty(TransformXMLInterceptor.PARAMETERS_KEY, "false");
		TestResult actual = generateOutput(testUI);
		Assert.assertEquals("XML should not be transformed when interceptor disabled", TEST_XML, actual.result);
	}

	/**
	 * Test that the interceptor transforms our XML when it is enabled.
	 */
	@Test
	public void testPaintWhileEnabled() {
		final String expected = "<omg><wtf>is good for you</wtf></omg>";
		MyComponent testUI = new MyComponent(TEST_XML);
		Config.getInstance().setProperty(TransformXMLInterceptor.PARAMETERS_KEY, "true");
		TestResult actual = generateOutput(testUI);
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
		InterceptorComponent interceptor = new TransformXMLInterceptor();
		interceptor.setBackingComponent(testUI);

		MockResponse response = new MockResponse();
		interceptor.attachResponse(response);

		StringWriter writer = new StringWriter();
		UIContext uic = createUIContext();
		uic.setLocale(new Locale("xx"));
		setActiveContext(uic);

		try {
			interceptor.paint(new WebXmlRenderContext(new PrintWriter(writer)));
		} finally {
			resetContext();
		}

		return new TestResult(writer.toString(), response.getContentType());
	}

	/**
	 * A simple DTO to pass back the results of the render to the calling test.
	 */
	private class TestResult {
		private String result;
		private String contentType;

		/**
		 * Create and instance of the DTO.
		 * @param result The rendered output of the UI component.
		 * @param contentType The content type of the response.
		 */
		private TestResult(final String result, final String contentType) {
			this.result = result;
			this.contentType = contentType;
		}
	}
}
