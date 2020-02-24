package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ErrorCodeEscape;
import com.github.bordertech.wcomponents.Escape;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link TargetableErrorInterceptor}.
 */
public class TargetableErrorInterceptor_Test extends AbstractWComponentTestCase {

	private static final String SESSION_ERROR = Config.getInstance().getString(InternalMessages.DEFAULT_SESSION_TOKEN_ERROR);
	private static final String CONTENT_ERROR = Config.getInstance().getString(InternalMessages.DEFAULT_CONTENT_ERROR);

	@Before
	public void setupUIC() {
		setActiveContext(createUIContext());
	}

	@Test
	public void testNoErrors() {
		// Setup interceptor
		MyBackingComponent component = new MyBackingComponent();
		TargetableErrorInterceptor interceptor = new TargetableErrorInterceptor();
		interceptor.setBackingComponent(component);
		MockRequest request = new MockRequest();
		// Process request
		interceptor.serviceRequest(request);
		interceptor.preparePaint(request);
		Assert.assertTrue("Handle request not called", component.handleRequestCalled);
		Assert.assertTrue("Prepare paint not called", component.preparePaintCalled);
	}

	@Test(expected = Escape.class)
	public void testHandleActionEscape() throws IOException {

		// Throw escape exception in chain
		InterceptorComponent chain = new InterceptorComponent() {
			@Override
			public void serviceRequest(final Request request) {
				throw new Escape();
			}
		};

		// Setup interceptor
		TargetableErrorInterceptor interceptor = new TargetableErrorInterceptor();
		interceptor.setBackingComponent(chain);
		// Process Action
		interceptor.serviceRequest(new MockRequest());
	}

	@Test
	public void testHandleActionSessionToken() throws IOException {

		final SessionTokenException excp = new SessionTokenException("Simulate session error");

		// Throw session exception in chain
		InterceptorComponent chain = new InterceptorComponent() {
			@Override
			public void serviceRequest(final Request request) {
				throw excp;
			}
		};

		// Setup interceptor
		TargetableErrorInterceptor interceptor = new TargetableErrorInterceptor();
		interceptor.setBackingComponent(chain);
		// Process Action
		try {
			interceptor.serviceRequest(new MockRequest());
			Assert.fail("Session exception not handled correctly");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect session exception message", e.getMessage().contains(SESSION_ERROR));
			Assert.assertEquals("Incorrect escape code for session exception", HttpServletResponse.SC_BAD_REQUEST, e.getCode());
			Assert.assertEquals("Cause should be the original session exception", excp, e.getCause());
		}
	}

	@Test
	public void testHandleActionTargetableError() throws IOException {

		final TargetableIdException excp = new TargetableIdException("Simulate target id error");

		// Throw targetid exception in chain
		InterceptorComponent chain = new InterceptorComponent() {
			@Override
			public void serviceRequest(final Request request) {
				throw excp;
			}
		};

		// Setup interceptor
		TargetableErrorInterceptor targetable = new TargetableErrorInterceptor();
		targetable.setBackingComponent(chain);
		// Process Action
		try {
			targetable.serviceRequest(new MockRequest());
			Assert.fail("Target id exception not handled correctly");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect target id exception message", e.getMessage().contains(CONTENT_ERROR));
			Assert.assertEquals("Incorrect escape code for target id exception", HttpServletResponse.SC_BAD_REQUEST, e.getCode());
			Assert.assertEquals("Cause should be the original target id exception", excp, e.getCause());
		}
	}

	@Test
	public void testHandleActionSystemError() throws IOException {

		final SystemException excp = new SystemException("Simulate action phase system error");

		// Throw system exception in chain
		InterceptorComponent chain = new InterceptorComponent() {
			@Override
			public void serviceRequest(final Request request) {
				throw excp;
			}
		};

		// Setup interceptor
		TargetableErrorInterceptor interceptor = new TargetableErrorInterceptor();
		interceptor.setBackingComponent(chain);
		// Process Action
		try {
			interceptor.serviceRequest(new MockRequest());
			Assert.fail("System exception not handled correctly");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect system exception message", e.getMessage().contains(CONTENT_ERROR));
			Assert.assertEquals("Incorrect escape code for system exception", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getCode());
			Assert.assertEquals("Cause should be the original system exception", excp, e.getCause());
		}
	}

	@Test(expected = Escape.class)
	public void testHandlePreparePaintEscape() throws IOException {

		// Throw escape exception in chain
		InterceptorComponent chain = new InterceptorComponent() {
			@Override
			public void preparePaint(final Request request) {
				throw new Escape();
			}
		};

		// Setup interceptor
		TargetableErrorInterceptor interceptor = new TargetableErrorInterceptor();
		interceptor.setBackingComponent(chain);
		// Prepare paint
		interceptor.preparePaint(new MockRequest());
	}

	@Test
	public void testHandlePreparePaintSystemError() throws IOException {

		final SystemException excp = new SystemException("Simulate prepare paint system error");

		// Throw system exception in chain
		InterceptorComponent chain = new InterceptorComponent() {
			@Override
			public void preparePaint(final Request request) {
				throw excp;
			}
		};

		// Setup interceptor
		TargetableErrorInterceptor interceptor = new TargetableErrorInterceptor();
		interceptor.setBackingComponent(chain);
		// Process Action
		try {
			interceptor.preparePaint(new MockRequest());
			Assert.fail("System exception not handled correctly in prepare paint");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect system exception message in prepare paint", e.getMessage().contains(CONTENT_ERROR));
			Assert.assertEquals("Incorrect escape code for system exception in prepare paint", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getCode());
			Assert.assertEquals("Cause should be the original system exception in prepare paint", excp, e.getCause());
		}
	}

	/**
	 * A simple component that records when the handleRequest method is called.
	 */
	private static final class MyBackingComponent extends WApplication {

		private boolean handleRequestCalled = false;
		private boolean preparePaintCalled = false;

		@Override
		public void handleRequest(final Request request) {
			handleRequestCalled = true;
			super.handleRequest(request);
		}

		@Override
		protected void preparePaintComponent(final Request request) {
			preparePaintCalled = true;
			super.preparePaintComponent(request);
		}

	}
}
