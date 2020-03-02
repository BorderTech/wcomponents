package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;

/**
 * Unit tests for {@link AjaxSetupInterceptor}.
 */
public class AjaxSetupInterceptor_Test extends AbstractWComponentTestCase {

	@After
	public void resetAjax() {
		AjaxHelper.clearCurrentOperationDetails();
	}

	@Test(expected = SystemException.class)
	public void testNoTriggerId() throws IOException {
		AjaxSetupInterceptor ajax = new AjaxSetupInterceptor();
		ajax.serviceRequest(new MockRequest());
	}

	@Test
	public void testTriggerIdValid() throws IOException {
		// Setup App
		MyApp app = setupApp();
		// Setup request
		MockRequest request = setupRequest(app);
		// Do Service Request
		doAjaxServiceRequest(app, request);
	}

	@Test(expected = AjaxTriggerException.class)
	public void testTriggerIdInValid() throws IOException {
		// Setup App
		MyApp app = setupApp();
		// Setup request with invalid trigger ID
		MockRequest request = new MockRequest();
		request.setParameter(WServlet.AJAX_TRIGGER_PARAM_NAME, "X-BAD");
		// Do Service Request
		doAjaxServiceRequest(app, request);
	}

	@Test(expected = AjaxTriggerException.class)
	public void testTriggerIdInvisible() throws IOException {
		// Setup App
		MyApp app = setupApp();
		// Make trigger invisible
		app.trigger.setVisible(false);
		// Setup request
		MockRequest request = setupRequest(app);
		// Do Service Request
		doAjaxServiceRequest(app, request);
	}

	@Test(expected = SystemException.class)
	public void testTriggerIdNotRegistered() throws IOException {
		// Setup App
		MyApp app = setupApp();
		// Clear registered AJAX triggers
		AjaxHelper.clearAllRegisteredOperations();
		// Setup request
		MockRequest request = setupRequest(app);
		// Do Service Request
		doAjaxServiceRequest(app, request);
	}

	private MyApp setupApp() {
		MyApp app = new MyApp();
		UIContext uic = createUIContext();
		uic.setUI(app);
		setActiveContext(uic);
		// Register trigger
		AjaxHelper.registerComponent(app.target.getId(), app.trigger.getId());
		return app;
	}

	private MockRequest setupRequest(final MyApp app) {
		MockRequest request = new MockRequest();
		request.setParameter(WServlet.AJAX_TRIGGER_PARAM_NAME, app.trigger.getId());
		return request;
	}

	private void doAjaxServiceRequest(final MyApp app, final MockRequest request) {
		// Setup interceptor
		AjaxSetupInterceptor ajax = new AjaxSetupInterceptor();
		ajax.attachUI(app);
		// Process request
		ajax.serviceRequest(request);
	}

	/**
	 * A simple test UI which is AJAX-enabled.
	 */
	private static final class MyApp extends WApplication {

		/**
		 * An AJAX trigger.
		 */
		private final WButton trigger = new WButton("trigger");

		/**
		 * An AJAX target.
		 */
		private final WTextField target = new WTextField();

		/**
		 * Creates the test app.
		 */
		private MyApp() {
			trigger.setAjaxTarget(target);
			add(trigger);
			add(target);
		}
	}
}
