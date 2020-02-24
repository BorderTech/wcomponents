package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.ErrorCodeEscape;
import com.github.bordertech.wcomponents.Escape;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link AjaxErrorInterceptor}.
 */
public class AjaxErrorInterceptor_Test extends AbstractWComponentTestCase {

	private static final String SESSION_ERROR = Config.getInstance().getString(InternalMessages.DEFAULT_SESSION_TOKEN_ERROR);
	private static final String AJAX_ERROR = Config.getInstance().getString(InternalMessages.DEFAULT_AJAX_ERROR);

	@Before
	public void setupUIC() {
		setActiveContext(createUIContext());
	}

	@After
	public void resetAjax() {
		AjaxHelper.clearCurrentOperationDetails();
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
		AjaxErrorInterceptor ajax = new AjaxErrorInterceptor();
		ajax.setBackingComponent(chain);
		// Process Action
		ajax.serviceRequest(new MockRequest());
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
		AjaxErrorInterceptor ajax = new AjaxErrorInterceptor();
		ajax.setBackingComponent(chain);
		// Process Action
		try {
			ajax.serviceRequest(new MockRequest());
			Assert.fail("Session exception not handled correctly");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect session exception message", e.getMessage().contains(SESSION_ERROR));
			Assert.assertEquals("Incorrect escape code for session exception", HttpServletResponse.SC_BAD_REQUEST, e.getCode());
			Assert.assertEquals("Cause should be the original session exception", excp, e.getCause());
		}
	}

	@Test
	public void testHandleActionAjaxError() throws IOException {

		final AjaxTriggerException excp = new AjaxTriggerException("Simulate ajax trigger error");

		// Throw trigger exception in chain
		InterceptorComponent chain = new InterceptorComponent() {
			@Override
			public void serviceRequest(final Request request) {
				throw excp;
			}
		};

		// Setup interceptor
		AjaxErrorInterceptor ajax = new AjaxErrorInterceptor();
		ajax.setBackingComponent(chain);
		// Process Action
		try {
			ajax.serviceRequest(new MockRequest());
			Assert.fail("Trigger exception not handled correctly");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect trigger exception message", e.getMessage().contains(AJAX_ERROR));
			Assert.assertEquals("Incorrect escape code for trigger exception", HttpServletResponse.SC_BAD_REQUEST, e.getCode());
			Assert.assertEquals("Cause should be the original trigger exception", excp, e.getCause());
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
		AjaxErrorInterceptor ajax = new AjaxErrorInterceptor();
		ajax.setBackingComponent(chain);
		// Process Action
		try {
			ajax.serviceRequest(new MockRequest());
			Assert.fail("System exception not handled correctly");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect system exception message", e.getMessage().contains(AJAX_ERROR));
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
		AjaxErrorInterceptor ajax = new AjaxErrorInterceptor();
		ajax.setBackingComponent(chain);
		// Prepare paint
		ajax.preparePaint(new MockRequest());
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
		AjaxErrorInterceptor ajax = new AjaxErrorInterceptor();
		ajax.setBackingComponent(chain);
		// Process Action
		try {
			ajax.preparePaint(new MockRequest());
			Assert.fail("System exception not handled correctly in prepare paint");
		} catch (ErrorCodeEscape e) {
			Assert.assertTrue("Incorrect system exception message in prepare paint", e.getMessage().contains(AJAX_ERROR));
			Assert.assertEquals("Incorrect escape code for system exception in prepare paint", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getCode());
			Assert.assertEquals("Cause should be the original system exception in prepare paint", excp, e.getCause());
		}
	}

}
