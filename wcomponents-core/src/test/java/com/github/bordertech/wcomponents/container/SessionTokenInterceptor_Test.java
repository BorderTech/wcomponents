package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.MockWEnvironment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * SessionTokenInterceptor - unit tests for {@link SessionTokenInterceptor}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenInterceptor_Test extends AbstractWComponentTestCase {

	/**
	 * Backing component.
	 */
	private MyBackingComponent component;
	/**
	 * Interceptor being tested.
	 */
	private SessionTokenInterceptor interceptor;
	/**
	 * User context.
	 */
	private UIContext uic;
	/**
	 * Mock request.
	 */
	private MockRequest request;

	@Before
	public void setUp() {
		component = new MyBackingComponent();
		interceptor = new SessionTokenInterceptor();
		interceptor.setBackingComponent(component);
		uic = new UIContextImpl();
		uic.setUI(component);
		uic.setEnvironment(new MockWEnvironment());
		setActiveContext(uic);

		request = new MockRequest();
	}

	@Test
	public void testServiceRequestDefaultState() {
		// Test default state (ie no params and new session)
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred by default",
				component.handleRequestCalled);
		Assert
				.assertEquals("Step count should not have been incremented by default", 0, uic.
						getEnvironment().getStep());
	}

	@Test
	public void testServiceRequestCorrectToken() {
		uic.getEnvironment().setSessionToken("X");
		uic.getEnvironment().setStep(10);
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "X");

		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for corret token",
				component.handleRequestCalled);
		Assert.assertEquals("Step count should not have been incremented for correct token", 10,
				uic.getEnvironment()
				.getStep());
	}

	@Test
	public void testServiceRequestIncorrectToken() {
		uic.getEnvironment().setSessionToken("X");
		uic.getEnvironment().setStep(10);
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "Y");
		try {
			interceptor.serviceRequest(request);
			Assert.fail("Should have thrown an excpetion for incorrect token");
		} catch (SystemException e) {
			Assert.assertFalse("Action phase should not have occurred for token error",
					component.handleRequestCalled);
			Assert.assertEquals("Step count should not have been incremented for token error", 10,
					uic.getEnvironment()
					.getStep());
		}
	}

	@Test
	public void testSessionTimeout() {
		// Simulate request parameter from previous session
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "X");
		try {
			interceptor.serviceRequest(request);
			Assert.fail("Should have thrown an excpetion for incorrect token");
		} catch (SystemException e) {
			Assert.assertFalse("Action phase should not have occurred for session timeout",
					component.handleRequestCalled);
			Assert.
					assertEquals("Step count should not have been incremented for session timeout",
							0, uic
							.getEnvironment().getStep());
		}
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
