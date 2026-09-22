package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.MockWEnvironment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link SessionTokenAjaxInterceptor}.
 */
public class SessionTokenAjaxInterceptor_Test extends AbstractWComponentTestCase {

	private static final String VALID_TOKEN = "X";
	private static final String INVALID_TOKEN = "Y";

	@Before
	public void setupUIC() {
		// Set up user context and session token
		MockWEnvironment env = new MockWEnvironment();
		env.setSessionToken(VALID_TOKEN);
		UIContext uic = createUIContext();
		uic.setEnvironment(env);
		setActiveContext(uic);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestNoTokenOnUIC() {
		// Clear session token on UIC
		UIContextHolder.getCurrent().getEnvironment().setSessionToken(null);
		// Should not process with a UIC with no session token
		new SessionTokenAjaxInterceptor().serviceRequest(new MockRequest());
	}

	@Test
	public void testServiceRequestWithPOSTandCorrectToken() {
		// Setup interceptor
		SessionTokenAjaxInterceptor interceptor = new SessionTokenAjaxInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		// Setup request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, VALID_TOKEN);
		// Should process with POST request and valid token
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for POST and correct token", component.handleRequestCalled);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestWithPOSTandInvalidToken() {
		// Setup invalid request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, INVALID_TOKEN);
		// Should not process with a POST request and invalid token
		new SessionTokenAjaxInterceptor().serviceRequest(request);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestWithPOSTandNoTokenOnRequest() {
		// Should not process with a POST request and no token
		new SessionTokenAjaxInterceptor().serviceRequest(new MockRequest());
	}

	@Test(expected = IllegalStateException.class)
	public void testServiceRequestWithGETandToken() {
		// Setup GET request with token
		MockRequest request = new MockRequest();
		request.setMethod("GET");
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, VALID_TOKEN);
		// Should not allow GET request with token parameter
		new SessionTokenAjaxInterceptor().serviceRequest(request);
	}

	@Test
	public void testServiceRequestWithGETandNoToken() {
		SessionTokenAjaxInterceptor interceptor = new SessionTokenAjaxInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		// Setup GET request with no token
		MockRequest request = new MockRequest();
		request.setMethod("GET");
		// Should allow GET request with no token parameter
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for GET request with no token", component.handleRequestCalled);
	}

	/**
	 * A simple component that records when the handleRequest method is called.
	 */
	private static final class MyBackingComponent extends WApplication {

		/**
		 * Indicates whether the handleRequest method has been called.
		 */
		private boolean handleRequestCalled = false;

		@Override
		public void handleRequest(final Request request) {
			handleRequestCalled = true;
			super.handleRequest(request);
		}
	}
}
