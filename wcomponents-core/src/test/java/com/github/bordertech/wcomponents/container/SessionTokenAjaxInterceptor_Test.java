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

	@Before
	public void setupUIC() {
		UIContext uic = createUIContext();
		uic.setEnvironment(new MockWEnvironment());
		setActiveContext(uic);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestNoTokenOnUIC() {
		SessionTokenAjaxInterceptor interceptor = new SessionTokenAjaxInterceptor();
		interceptor.serviceRequest(new MockRequest());
	}

	@Test
	public void testServiceRequestCorrectToken() {
		// Setup interceptor
		SessionTokenAjaxInterceptor interceptor = new SessionTokenAjaxInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		// Setup session token
		UIContext uic = UIContextHolder.getCurrent();
		uic.getEnvironment().setSessionToken("X");
		// Setup request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "X");
		// Process request
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for corret token", component.handleRequestCalled);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestInvalidToken() {
		// Setup interceptor
		SessionTokenAjaxInterceptor interceptor = new SessionTokenAjaxInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		// Setup session token
		UIContext uic = UIContextHolder.getCurrent();
		uic.getEnvironment().setSessionToken("X");
		// Setup invalid request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, "Y");
		// Process request
		interceptor.serviceRequest(request);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestNoTokenOnRequest() {
		// Setup interceptor
		SessionTokenAjaxInterceptor interceptor = new SessionTokenAjaxInterceptor();
		MyBackingComponent component = new MyBackingComponent();
		interceptor.attachUI(component);
		// Setup session token
		UIContext uic = UIContextHolder.getCurrent();
		uic.getEnvironment().setSessionToken("X");
		// Process request
		interceptor.serviceRequest(new MockRequest());
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
