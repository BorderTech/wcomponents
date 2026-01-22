package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.MockWEnvironment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link SessionTokenContentInterceptor}.
 */
public class SessionTokenContentInterceptor_Test extends AbstractWComponentTestCase {

	private static final String VALID_TOKEN = "X";

	@Before
	public void setupUIC() {
		// Set up user context and session token
		Environment env = new MockWEnvironment();
		env.setSessionToken(VALID_TOKEN);
		UIContext uic = createUIContext();
		uic.setEnvironment(env);
		setActiveContext(uic);
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestNoTokenOnUIC() {
		// Clear token on Context
		UIContextHolder.getCurrent().getEnvironment().setSessionToken(null);
		// Should not process if UIC has no session token
		new SessionTokenContentInterceptor().serviceRequest(new MockRequest());
	}

	@Test(expected = IllegalStateException.class)
	public void testServiceRequestWithPOST() {
		// Should not process with a POST request
		new SessionTokenContentInterceptor().serviceRequest(new MockRequest());
	}

	@Test(expected = IllegalStateException.class)
	public void testServiceRequestWithGETandToken() {
		// Setup GET request with token
		MockRequest request = new MockRequest();
		request.setMethod("GET");
		request.setParameter(Environment.SESSION_TOKEN_VARIABLE, VALID_TOKEN);
		// Should not process with a GET request with a token
		new SessionTokenContentInterceptor().serviceRequest(request);
	}

	@Test
	public void testServiceRequestWithGETandNoToken() {
		// Setup interceptor
		SessionTokenContentInterceptor interceptor = new SessionTokenContentInterceptor();
		MyBackingContent component = new MyBackingContent();
		interceptor.attachUI(component);
		// Setup request
		MockRequest request = new MockRequest();
		request.setMethod("GET");
		// Should process with a GET request and no token
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for GET request and no token", component.handleRequestCalled);
	}

	/**
	 * A simple component that records when the handleRequest method is called.
	 */
	private static final class MyBackingContent extends WContent {

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
