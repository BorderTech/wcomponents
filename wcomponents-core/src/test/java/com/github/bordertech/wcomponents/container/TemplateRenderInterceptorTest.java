package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.servlet.ServletRequest;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * Test cases for the {@link TemplateRenderInterceptor} class.
 *
 * @author Rick Brown
 * @since 1.2.15
 */
public class TemplateRenderInterceptorTest extends AbstractWComponentTestCase {

	/**
	 * The input html.
	 */
	private static final String TEST_HTML = "<fu>{{#i18n}}some_i18n_key{{/i18n}}</fu>";

	/**
	 * Expected result of rendering the template.
	 */
	private static final String EXPECTED_RENDERED = "<fu>yeah nah yeah</fu>";

	/**
	 * When these tests are done put things back as they were.
	 */
	@After
	public void tearDownClass() {
		Config.reset();
	}

	/**
	 * Ensure that the interceptor honors the "on" property.
	 */
	@Test
	public void testPaintWhileEnabled() {
		MyComponent testUI = new MyComponent(TEST_HTML);
		Config.getInstance().setProperty(ConfigurationProperties.I18N_THEME_RESOURCE_BUNDLE_BASE_NAME, "i18n/theme");
		TestResult actual = generateOutput(testUI);
		Assert.assertEquals("Template should not be rendered when interceptor disabled", EXPECTED_RENDERED, actual.result);
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
		InterceptorComponent interceptor = new TemplateRenderInterceptor();
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
}
