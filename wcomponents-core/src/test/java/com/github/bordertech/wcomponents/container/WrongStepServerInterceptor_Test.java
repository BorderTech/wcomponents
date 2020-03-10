package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.MockWEnvironment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * WrongStepServerInterceptor_Test - unit tests for {@link WrongStepServerInterceptor}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WrongStepServerInterceptor_Test extends AbstractWComponentTestCase {

	@After
	public void resetConfig() {
		Config.reset();
	}

	@Test
	public void testServiceRequestDefaultState() {

		MyBackingComponent component = new MyBackingComponent();
		WrongStepServerInterceptor interceptor = new WrongStepServerInterceptor();
		interceptor.setBackingComponent(component);
		UIContext uic = createUIContext();
		uic.setUI(component);
		uic.setEnvironment(new MockWEnvironment());

		setActiveContext(uic);

		// Default to GET method
		MockRequest request = new MockRequest();
		request.setMethod("GET");

		// Test default state
		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred by default", component.handleRequestCalled);
		Assert.assertEquals("Step should not have been incremented", 0, uic.getEnvironment().getStep());
		interceptor.preparePaint(request);
		Assert.assertEquals("Step should have been incremented", 1, uic.getEnvironment().getStep());
	}

	@Test
	public void testServiceRequestCorrectSequence() {
		MyBackingComponent component = new MyBackingComponent();
		WrongStepServerInterceptor interceptor = new WrongStepServerInterceptor();
		interceptor.setBackingComponent(component);
		UIContext uic = createUIContext();
		uic.setUI(component);
		uic.setEnvironment(new MockWEnvironment());

		setActiveContext(uic);

		// Default to GET method
		MockRequest request = new MockRequest();
		request.setMethod("GET");

		uic.getEnvironment().setStep(3);
		request.setParameter(Environment.STEP_VARIABLE, "3");

		interceptor.serviceRequest(request);
		Assert.assertTrue("Action phase should have occurred for no step error", component.handleRequestCalled);
		Assert.assertEquals("Step should not have been incremented", 3, uic.getEnvironment().getStep());
		interceptor.preparePaint(request);
		Assert.assertEquals("Step should have been incremented", 4, uic.getEnvironment().getStep());
	}

	@Test
	public void testServiceRequestIncorrectSequence() {

		MyBackingComponent component = new MyBackingComponent();
		WrongStepServerInterceptor interceptor = new WrongStepServerInterceptor();
		interceptor.setBackingComponent(component);
		UIContext uic = createUIContext();
		uic.setUI(component);
		uic.setEnvironment(new MockWEnvironment());

		setActiveContext(uic);

		// Default to GET method
		MockRequest request = new MockRequest();
		request.setMethod("GET");

		uic.getEnvironment().setStep(3);
		request.setParameter(Environment.STEP_VARIABLE, "1");
		interceptor.serviceRequest(request);
		Assert.assertTrue("Handle Step Error should have been called for step error", component.handleStepErrorCalled);
		Assert.assertFalse("Action phase should not have occurred for step error", component.handleRequestCalled);
		Assert.assertEquals("Step should not have been incremented", 3, uic.getEnvironment().getStep());
		interceptor.preparePaint(request);
		Assert.assertEquals("Step should have been incremented", 4, uic.getEnvironment().getStep());
	}

	@Test
	public void testServiceRequestIncorrectSequenceWithRedirect() {

		MyBackingComponent component = new MyBackingComponent();
		WrongStepServerInterceptor interceptor = new WrongStepServerInterceptor();
		interceptor.setBackingComponent(component);
		UIContext uic = createUIContext();
		uic.setUI(component);
		uic.setEnvironment(new MockWEnvironment());

		setActiveContext(uic);

		// Default to GET method
		MockRequest request = new MockRequest();
		request.setMethod("GET");

		// Set redirect parameter
		Config.getInstance().addProperty(ConfigurationProperties.STEP_ERROR_URL, "test.url");

		uic.getEnvironment().setStep(3);
		request.setParameter(Environment.STEP_VARIABLE, "1");

		interceptor.attachResponse(new MockResponse());
		try {
			interceptor.serviceRequest(request);
			Assert.fail("Interceptor did not detect wrong step");
		} catch (ActionEscape e) {
			Assert.assertFalse("Action phase should not have occurred for step error with redirect", component.handleRequestCalled);
			Assert.assertFalse("Handle Step Error should not have occurred for redirect", component.handleStepErrorCalled);
			Assert.assertEquals("Step should not have been incremented", 3, uic.getEnvironment().getStep());
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
