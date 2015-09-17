package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WFilterControl_Test - Unit tests for {@link WFilterControl}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFilterControl_Test extends AbstractWComponentTestCase {

	/**
	 * Default value used for testing.
	 */
	private static final String DEFAULT_VALUE = "DEFAULT";
	/**
	 * Value used for testing.
	 */
	private static final String TEST_VALUE = "TEST";

	@Test
	public void testConstructor1() {
		WDecoratedLabel label = new WDecoratedLabel();
		WFilterControl filter = null;

		try {
			filter = new WFilterControl(null);
			Assert.fail("Should not be able to create Filter Control with a null label");
		} catch (IllegalArgumentException expected) {
			Assert.assertNull("Filter should not have been created", filter);
		}

		filter = new WFilterControl(label);
		Assert.assertEquals("Incorrect filter label returned", label, filter.getFilterLabel());
	}

	@Test
	public void testConstructor2() {
		WDecoratedLabel label = new WDecoratedLabel();
		WComponent target = new DefaultWComponent();
		WFilterControl filter = null;

		try {
			filter = new WFilterControl(null, target, TEST_VALUE);
			Assert.fail("Should not be able to create Filter Control with a null label");
		} catch (IllegalArgumentException expected) {
			Assert.assertNull("Filter should not have been created", filter);
		}

		filter = new WFilterControl(label, target, TEST_VALUE);
		Assert.assertEquals("Incorrect filter label returned", label, filter.getFilterLabel());
		Assert.assertEquals("Incorrect filter target returned", target, filter.getTarget());
		Assert.assertEquals("Incorrect filter value returned", TEST_VALUE, filter.getValue());
	}

	@Test
	public void testFilterLabelAccessors() {
		WDecoratedLabel label = new WDecoratedLabel();
		WFilterControl filter = new WFilterControl(label);
		Assert.assertEquals("Incorrect filter label returned", label, filter.getFilterLabel());
	}

	@Test
	public void testTargetAccessors() {
		WComponent testTarget = new DefaultWComponent();
		WComponent defaultTarget = new DefaultWComponent();
		WFilterControl filter = new WFilterControl(new WDecoratedLabel());

		// Check not set
		Assert.assertNull("Incorrect default target returned", filter.getTarget());

		// Set targets
		filter.setTarget(defaultTarget);

		filter.setLocked(true);
		setActiveContext(createUIContext());
		filter.setTarget(testTarget);

		// Check targets
		Assert.assertEquals("Incorrect target returned", testTarget, filter.getTarget());

		resetContext();
		Assert.assertEquals("Incorrect default target returned", defaultTarget, filter.getTarget());
	}

	@Test
	public void testValueAccessors() {
		WFilterControl filter = new WFilterControl(new WDecoratedLabel());

		// Check not set
		Assert.assertNull("Incorrect default value returned", filter.getValue());

		// Set values
		filter.setValue(DEFAULT_VALUE);

		filter.setLocked(true);
		setActiveContext(createUIContext());
		filter.setValue(TEST_VALUE);

		// Check values
		Assert.assertEquals("Incorrect value returned", TEST_VALUE, filter.getValue());

		resetContext();
		Assert.assertEquals("Incorrect default value returned", DEFAULT_VALUE, filter.getValue());
	}

	@Test
	public void testActiveAccessors() {
		WFilterControl filter = new WFilterControl(new WDecoratedLabel());

		// Check initial value
		Assert.assertFalse("Incorrect default active status returned", filter.isActive());

		// Set default active status
		filter.setActive(true);
		Assert.assertTrue("Incorrect default active status returned", filter.isActive());

		// Set active status
		filter.setActive(false);
		filter.setLocked(true);
		setActiveContext(createUIContext());
		filter.setActive(true);
		Assert.assertTrue("Incorrect active status returned", filter.isActive());

		resetContext();
		Assert.assertFalse("Incorrect default active status returned", filter.isActive());
	}

	@Test
	public void testHandleRequest() {
		WFilterControl filter = new WFilterControl(new WDecoratedLabel());
		filter.setLocked(true);

		setActiveContext(createUIContext());

		// Check not active
		Assert.assertFalse("Filter should not be active", filter.isActive());

		// Simulate request to activate filter
		MockRequest request = new MockRequest();
		request.setParameter(filter.getId(), "true");
		filter.handleRequest(request);
		Assert.assertTrue("Filter should be active", filter.isActive());

		// Simulate request with no setting (should not change status)
		request = new MockRequest();
		filter.handleRequest(request);
		Assert.assertTrue("Filter should be active", filter.isActive());

		// Simulate request to switch off filter
		request = new MockRequest();
		request.setParameter(filter.getId(), "false");
		filter.handleRequest(request);
		Assert.assertFalse("Filter should not be active", filter.isActive());
	}

}
