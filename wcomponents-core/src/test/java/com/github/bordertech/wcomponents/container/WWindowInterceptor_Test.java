package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.InternalResource;
import com.github.bordertech.wcomponents.MockLabel;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.WWindow;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.PrintWriter;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * WWindowComponent_Test - unit tests for {@link WWindowInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WWindowInterceptor_Test extends AbstractWComponentTestCase {

	@Test
	public void testServiceRequest() {
		final int initialServletStep = 123;
		final int initialWindowStep = 111;

		// Create app
		WApplication app = new WApplication();
		WWindow window = new WWindow();
		window.setContent(new MockLabel("dummy"));
		app.add(window);
		WContent content = new WContent();
		content.setContentAccess(new InternalResource("/wcomponents-test.properties", "test"));
		app.add(content);
		app.setLocked(true);

		// Set up servlet env
		WServlet.WServletEnvironment servletEnvironment = new WServlet.WServletEnvironment("/app",
				"", "");
		servletEnvironment.setStep(initialServletStep);

		// Set user session
		UIContext uic = createUIContext();
		uic.setUI(app);
		uic.setEnvironment(servletEnvironment);
		setActiveContext(uic);
		window.setStep(initialWindowStep);
		window.display();

		// Target the content first - should pass through and update the environment's step
		WWindowInterceptor interceptor = new WWindowInterceptor(true);
		TestInterceptor testInterceptor = new TestInterceptor();
		interceptor.setBackingComponent(testInterceptor);
		interceptor.attachUI(app);

		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, content.getId());

		interceptor.serviceRequest(request);
		Assert.assertEquals("Servlet step should have changed after serviceRequest",
				initialServletStep + 1, servletEnvironment.getStep());
		Assert.assertEquals("Window step should not have changed", initialWindowStep, window.
				getStep());

		interceptor.preparePaint(request);
		Assert.assertEquals("Servlet step should have changed after preparePaint",
				initialServletStep + 2, servletEnvironment.getStep());
		Assert.assertEquals("Window step should not have changed", initialWindowStep, window.
				getStep());

		interceptor.paint(new WebXmlRenderContext(new PrintWriter(new NullWriter())));
		Assert.assertEquals("Servlet step should have changed after paint", initialServletStep + 3,
				servletEnvironment.getStep());
		Assert.assertEquals("Window step should not have changed", initialWindowStep, window.
				getStep());

		servletEnvironment.setStep(initialServletStep);

		request = new MockRequest();
		request.setParameter(WWindow.WWINDOW_REQUEST_PARAM_KEY, window.getId());

		interceptor.serviceRequest(request);
		Assert.assertEquals("Window step should have changed after serviceRequest",
				initialWindowStep + 1, window.getStep());
		Assert.assertEquals("Servlet step should not have changed", initialServletStep,
				servletEnvironment.getStep());

		interceptor.preparePaint(request);
		Assert.assertEquals("Window step should have changed after preparePaintnot have changed",
				initialWindowStep + 2, window.getStep());
		Assert.assertEquals("Servlet step should not have changed", initialServletStep,
				servletEnvironment.getStep());

		interceptor.paint(new WebXmlRenderContext(new PrintWriter(new NullWriter())));
		Assert.assertEquals("Window step should have changed after paint", initialWindowStep + 3,
				window.getStep());
		Assert.assertEquals("Servlet step should not have changed", initialServletStep,
				servletEnvironment.getStep());

		String actualTargetId = testInterceptor.hiddenParams.get(WWindow.WWINDOW_REQUEST_PARAM_KEY);
		Assert.assertEquals("Hidden params target id should be window id", window.getId(),
				actualTargetId);
	}

	/**
	 * This interceptor increments the environment step variable and stores the hidden parameters during paint.
	 */
	private static final class TestInterceptor extends InterceptorComponent {

		/**
		 * The hidden parameters used when painting.
		 */
		private Map<String, String> hiddenParams;

		@Override
		public void serviceRequest(final Request request) {
			Environment env = UIContextHolder.getCurrent().getEnvironment();
			env.setStep(env.getStep() + 1);
		}

		@Override
		public void preparePaint(final Request request) {
			Environment env = UIContextHolder.getCurrent().getEnvironment();
			env.setStep(env.getStep() + 1);
		}

		@Override
		public void paint(final RenderContext renderContext) {
			Environment env = UIContextHolder.getCurrent().getEnvironment();
			env.setStep(env.getStep() + 1);

			hiddenParams = env.getHiddenParameters();
		}
	}
}
