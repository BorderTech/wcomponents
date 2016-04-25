package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Targetable;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TargetableInterceptor_Test - unit tests for {@link TargetableInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TargetableInterceptor_Test extends AbstractWComponentTestCase {

	private WComponent originalUI;
	private WComponent targetUI;
	private TargetableInterceptor interceptor;

	@Before
	public void setUp() {
		UIContext uic = new UIContextImpl();
		WPanel root = new WPanel();
		originalUI = new WLabel();
		targetUI = new TargetableWLabel();

		root.add(originalUI);
		root.add(targetUI);
		uic.setUI(root);
		setActiveContext(uic);

		interceptor = new TargetableInterceptor();
		interceptor.setBackingComponent(originalUI);
	}

	@Test
	public void testServiceRequestNoTarget() {
		Assert.assertSame("Incorrect UI returned",
				originalUI, interceptor.getUI());

		// Should not change UI by default
		try {
			MockRequest request = new MockRequest();
			interceptor.serviceRequest(request);
			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException expected) {
			Assert.assertSame("UI should not change after serviceRequest with no target set",
					originalUI, interceptor.getUI());
		}
	}

	@Test
	public void testServiceRequestWithTarget() {
		// UI should change when a target is present in a request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, targetUI.getId());

		interceptor.serviceRequest(request);

		Assert.assertNotSame("UI should change after serviceRequest with a target set",
				originalUI, interceptor.getUI());

		Assert.assertSame("Incorrect new UI after serviceRequest with a target set",
				targetUI, interceptor.getUI());
	}

	@Test(expected = SystemException.class)
	public void testServiceRequestWithUndefinedTarget() {
		// UI should change when a target is present in a request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID,
				"TargetableInterceptor_Test.testServiceRequestWithUndefinedTarget");

		interceptor.serviceRequest(request);
	}

	/**
	 * A simple targetable component that can be used for testing.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class TargetableWLabel extends WLabel implements Targetable {

		@Override
		public String getTargetId() {
			return getId();
		}
	}
}
