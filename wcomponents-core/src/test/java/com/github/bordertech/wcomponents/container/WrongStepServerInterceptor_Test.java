package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.MockWEnvironment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.StepCountUtil;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import junit.framework.Assert;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * WrongStepServerInterceptor_Test - unit tests for {@link WrongStepServerInterceptor}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WrongStepServerInterceptor_Test extends AbstractWComponentTestCase {

	/**
	 * Backing component.
	 */
	private MyBackingComponent component;
	/**
	 * Interceptor being tested.
	 */
	private WrongStepServerInterceptor interceptor;
	/**
	 * User context.
	 */
	private UIContext uic;
	/**
	 * Mock request.
	 */
	private MockRequest request;
	/**
	 * Original config.
	 */
	private static Configuration originalConfig;

	@Before
	public void setUp() {
		component = new MyBackingComponent();
		interceptor = new WrongStepServerInterceptor();
		interceptor.setBackingComponent(component);
		uic = new UIContextImpl();
		uic.setUI(component);
		uic.setEnvironment(new MockWEnvironment());

		setActiveContext(uic);

		request = new MockRequest();

		// Default to GET method
		request.setMethod("GET");

		originalConfig = Config.getInstance();
	}

	@After
	public void tearDown() {
		// Remove overrides
		Config.setConfiguration(originalConfig);
	}

	@Test
	public void testServiceRequestDefaultState() {
		// Test default state
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred by default",
				component.handleRequestCalled);
		Assert.assertEquals("Step should not have been incremented", 0, uic.getEnvironment().
				getStep());
		interceptor.preparePaint(request);
		Assert.assertEquals("Step should have been incremented", 1, uic.getEnvironment().getStep());
	}

	@Test
	public void testServiceRequestCorrectSequence() {
		uic.getEnvironment().setStep(3);
		request.setParameter(Environment.STEP_VARIABLE, "3");

		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for no step error",
				component.handleRequestCalled);
		Assert.assertEquals("Step should not have been incremented", 3, uic.getEnvironment().
				getStep());
		interceptor.preparePaint(request);
		Assert.assertEquals("Step should have been incremented", 4, uic.getEnvironment().getStep());
	}

	@Test
	public void testServiceRequestIncorrectSequence() {
		uic.getEnvironment().setStep(3);
		request.setParameter(Environment.STEP_VARIABLE, "1");
		interceptor.serviceRequest(request);
		Assert.assertTrue("Handle Step Error should have been called for step error",
				component.handleStepErrorCalled);
		Assert.assertFalse("Action phase should not have occurred for step error",
				component.handleRequestCalled);
		Assert.assertEquals("Step should not have been incremented", 3, uic.getEnvironment().
				getStep());
		interceptor.preparePaint(request);
		Assert.assertEquals("Step should have been incremented", 4, uic.getEnvironment().getStep());
	}

	@Test
	public void testServiceRequestIncorrectSequenceWithRedirect() {
		// Set redirect parameter
		Configuration config = Config.copyConfiguration(originalConfig);
		config.addProperty(StepCountUtil.STEP_ERROR_URL_PARAMETER_KEY, "test.url");
		Config.setConfiguration(config);

		uic.getEnvironment().setStep(3);
		request.setParameter(Environment.STEP_VARIABLE, "1");

		interceptor.attachResponse(new MockResponse());
		try {
			interceptor.serviceRequest(request);
			Assert.fail("Interceptor did not detect wrong step");
		} catch (ActionEscape e) {
			Assert.assertFalse("Action phase should not have occurred for step error with redirect",
					component.handleRequestCalled);
			Assert.assertFalse("Handle Step Error should not have occurred for redirect",
					component.handleStepErrorCalled);
			Assert.assertEquals("Step should not have been incremented", 3, uic.getEnvironment().
					getStep());
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
		/**
		 * Indicates whether the handleStepError method has been called.
		 */
		private boolean handleStepErrorCalled = false;

		@Override
		public void handleRequest(final Request request) {
			handleRequestCalled = true;
			super.handleRequest(request);
		}

		@Override
		public void handleStepError() {
			handleStepErrorCalled = true;
			super.handleStepError();
		}
	}
}
