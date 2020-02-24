package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.MockWEnvironment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link SessionTokenInterceptor}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenInterceptor_Test extends AbstractWComponentTestCase {

	@Test
	public void testServiceRequestCorrectToken() {

		// Setup interceptor
		SessionTokenInterceptor interceptor = setupInterceptor();
		MyBackingComponent component = (MyBackingComponent) interceptor.getBackingComponent();
		UIContext uic = UIContextHolder.getCurrent();
		MockRequest request = new MockRequest();

		// Setup matching tokens on session and request
		uic.getEnvironment().setSessionToken("X");
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "X");

		// Process request
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for corret token", component.handleRequestCalled);
	}

	@Test
	public void testServiceRequestIncorrectToken() {
		// Setup interceptor
		SessionTokenInterceptor interceptor = setupInterceptor();
		MyBackingComponent component = (MyBackingComponent) interceptor.getBackingComponent();
		UIContext uic = UIContextHolder.getCurrent();
		MockRequest request = new MockRequest();

		// Setup tokens that dont match on session and request
		uic.getEnvironment().setSessionToken("X");
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "Y");

		try {
			// Process request
			interceptor.serviceRequest(request);
			Assert.fail("Should have thrown an excpetion for incorrect token");
		} catch (SessionTokenException e) {
			Assert.assertFalse("Action phase should not have occurred for token error", component.handleRequestCalled);
		}
	}

	@Test
	public void testSessionTimeout() {
		// Setup interceptor
		SessionTokenInterceptor interceptor = setupInterceptor();
		MyBackingComponent component = (MyBackingComponent) interceptor.getBackingComponent();
		UIContext uic = UIContextHolder.getCurrent();
		MockRequest request = new MockRequest();

		// Simulate request parameter from previous session (new session has null token)
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "X");
		try {
			// Process request
			interceptor.serviceRequest(request);
			Assert.fail("Should have thrown an excpetion for incorrect token");
		} catch (SessionTokenException e) {
			Assert.assertFalse("Action phase should not have occurred for session timeout", component.handleRequestCalled);
			Assert.assertEquals("Step count should not have been incremented for session timeout", 0, uic.getEnvironment().getStep());
		}
	}

	@Test
	public void testNewSession() {
		// Setup interceptor
		SessionTokenInterceptor interceptor = setupInterceptor();
		UIContext uic = UIContextHolder.getCurrent();

		// Check no session token (ie new session)
		Assert.assertNull("Session token should be null for new session", uic.getEnvironment().getSessionToken());

		// Test default state (ie no params and new session)
		MockRequest request = new MockRequest();
		interceptor.serviceRequest(request);
		interceptor.preparePaint(request);
		Assert.assertNotNull("Session token should be set for new session", uic.getEnvironment().getSessionToken());
	}

	private SessionTokenInterceptor setupInterceptor() {
		MyBackingComponent component = new MyBackingComponent();
		SessionTokenInterceptor interceptor = new SessionTokenInterceptor();
		interceptor.setBackingComponent(component);
		UIContext uic = new UIContextImpl();
		uic.setUI(component);
		uic.setEnvironment(new MockWEnvironment());
		setActiveContext(uic);
		return interceptor;
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
