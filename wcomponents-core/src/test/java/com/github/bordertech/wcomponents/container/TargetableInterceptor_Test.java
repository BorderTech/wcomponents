package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Targetable;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link TargetableInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TargetableInterceptor_Test extends AbstractWComponentTestCase {

	@Before
	public void setupUIC() {
		setActiveContext(createUIContext());
	}

	@Test(expected = SystemException.class)
	public void testServiceRequestNoTarget() {
		MyApp root = new MyApp();
		TargetableInterceptor interceptor = new TargetableInterceptor();
		interceptor.attachUI(root);
		interceptor.serviceRequest(new MockRequest());
	}

	@Test
	public void testServiceRequestWithTarget() {
		// Setup interceptor
		MyApp root = new MyApp();
		TargetableInterceptor interceptor = new TargetableInterceptor();
		interceptor.attachUI(root);
		UIContextHolder.getCurrent().setUI(root);
		// UI should change when a target is present in a request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, root.targetUI.getId());
		// Check UI
		Assert.assertSame("Incorrect UI returned", root, interceptor.getUI());
		// Process Request
		interceptor.serviceRequest(request);
		// UI should be the target
		Assert.assertSame("Incorrect new UI after serviceRequest with a target set", root.targetUI, interceptor.getUI());
	}

	@Test(expected = TargetableIdException.class)
	public void testServiceRequestInvalidTarget() {
		// Setup interceptor
		MyApp root = new MyApp();
		TargetableInterceptor interceptor = new TargetableInterceptor();
		interceptor.setBackingComponent(root);
		UIContextHolder.getCurrent().setUI(root);
		// Invalid Target ID on request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, "X-BAD");
		// Process request
		interceptor.serviceRequest(request);
	}

	@Test(expected = TargetableIdException.class)
	public void testServiceRequestInvisibleTarget() {
		// Setup interceptor
		MyApp root = new MyApp();
		TargetableInterceptor interceptor = new TargetableInterceptor();
		interceptor.setBackingComponent(root);
		UIContextHolder.getCurrent().setUI(root);
		// Setup request
		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, root.targetUI.getId());
		// Make target invisible
		root.targetUI.setVisible(false);
		// Process request
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

	private static final class MyApp extends WContainer {

		private final WComponent targetUI = new TargetableWLabel();

		public MyApp() {
			add(targetUI);
		}

	}
}
