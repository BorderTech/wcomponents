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
 * Unit tests for {@link SessionTokenInterceptor}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenInterceptor_Test extends AbstractWComponentTestCase {

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

	@Test
	public void testServiceRequestWithPOSTandCorrectToken() {
		// Setup interceptor
		SessionTokenInterceptor interceptor = new SessionTokenInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		// Setup request with valid token
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, VALID_TOKEN);
		// Should process POST request with correct token
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for corret token", component.handleRequestCalled);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestWithPOSTandIncorrectToken() {
		// Setup request with invalid token
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, INVALID_TOKEN);
		// Should not process POST request with incorrect token
		new SessionTokenInterceptor().serviceRequest(request);
	}

	@Test(expected = SessionTokenException.class)
	public void testSessionTimeout() {
		// Clear session token on UIC (simulate new session from timeout)
		UIContextHolder.getCurrent().getEnvironment().setSessionToken(null);
		// Simulate request parameter from previous session (new session has null token)
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, VALID_TOKEN);
		// Should not process a POST request with a token and null session token
		new SessionTokenInterceptor().serviceRequest(request);
	}

	@Test(expected = SessionTokenException.class)
	public void testNewSessionWithPOSTRequestWithToken() {
		// Clear session token on UIC (simulate new session)
		UIContextHolder.getCurrent().getEnvironment().setSessionToken(null);
		// Setup POST request with a token
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, VALID_TOKEN);
		// Should not process a POST request with a token and a new session
		new SessionTokenInterceptor().serviceRequest(request);
	}

	@Test(expected = SessionTokenException.class)
	public void testNewSessionWithPOSTRequestWithNoToken() {
		// Clear session token on UIC (simulate new session)
		UIContextHolder.getCurrent().getEnvironment().setSessionToken(null);
		// Should not process a POST request with no token and a new session
		new SessionTokenInterceptor().serviceRequest(new MockRequest());
	}

	@Test
	public void testNewSessionWithGETRequest() {
		// Setup interceptor
		SessionTokenInterceptor interceptor = new SessionTokenInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		UIContext uic = UIContextHolder.getCurrent();
		// Clear session token on UIC (simulate new session)
		uic.getEnvironment().setSessionToken(null);
		// Setup GET Request with no token
		MockRequest request = new MockRequest();
		request.setMethod("GET");
		interceptor.serviceRequest(request);
		interceptor.preparePaint(request);
		Assert.assertTrue("Action phase should have occurred for new session", component.handleRequestCalled);
		Assert.assertNotNull("Session token should be set for new session", uic.getEnvironment().getSessionToken());
	}

	@Test(expected = IllegalStateException.class)
	public void testServiceRequestWithGETandToken() {
		// Setup GET request with a token
		MockRequest request = new MockRequest();
		request.setMethod("GET");
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, VALID_TOKEN);
		// Should not process a GET request with a token
		new SessionTokenInterceptor().serviceRequest(request);
	}

	@Test
	public void testServiceRequestWithGETandNoToken() {
		// Setup interceptor
		SessionTokenInterceptor interceptor = new SessionTokenInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		// Setup GET request with no token
		MockRequest request = new MockRequest();
		request.setMethod("GET");
		// Should process GET request with no token
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for new session", component.handleRequestCalled);
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
