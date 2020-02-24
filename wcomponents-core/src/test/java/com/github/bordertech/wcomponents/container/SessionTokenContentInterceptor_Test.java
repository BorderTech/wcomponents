package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ContentEscape;
import com.github.bordertech.wcomponents.Environment;
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

	@Before
	public void setupUIC() {
		setActiveContext(createUIContext());
	}

	@Test(expected = SessionTokenException.class)
	public void testServiceRequestNoTokenOnUIC() {
		SessionTokenContentInterceptor interceptor = new SessionTokenContentInterceptor();
		interceptor.serviceRequest(new MockRequest());
	}

	@Test
	public void testServiceRequestCorrectToken() {
		// Setup interceptor
		SessionTokenContentInterceptor interceptor = new SessionTokenContentInterceptor();
		MyBackingContent component = new MyBackingContent();
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
		SessionTokenContentInterceptor interceptor = new SessionTokenContentInterceptor();
		MyBackingContent component = new MyBackingContent();
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
		SessionTokenContentInterceptor interceptor = new SessionTokenContentInterceptor();
		MyBackingContent component = new MyBackingContent();
		interceptor.attachUI(component);
		// Setup session token
		UIContext uic = UIContextHolder.getCurrent();
		uic.getEnvironment().setSessionToken("X");
		// Process request
		interceptor.serviceRequest(new MockRequest());
	}

	@Test(expected = ContentEscape.class)
	public void testServiceRequestNoTokenWIthCachedContent() {
		// Setup interceptor
		SessionTokenContentInterceptor interceptor = new SessionTokenContentInterceptor();
		MyBackingContent component = new MyBackingContent();
		interceptor.attachUI(component);
		// Setup session token
		UIContext uic = UIContextHolder.getCurrent();
		uic.getEnvironment().setSessionToken("X");
		uic.setUI(component);
		// Setup request - TargetID makes the WContent trigger the ContentEscape
		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, component.getId());
		// Set cached content
		component.setCacheKey("mykey");
		// Process request
		interceptor.serviceRequest(request);
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
