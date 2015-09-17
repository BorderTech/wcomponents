package com.github.bordertech.wcomponents.registry;

import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.FatalErrorPage;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.SystemException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link UIRegistryAmicableImpl}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class UIRegistryAmicableImpl_Test {

	/**
	 * Test register - success.
	 */
	@Test
	public void testRegisterSuccess() {
		final String key = "test123";
		WComponent component = new DefaultWComponent();

		UIRegistryAmicableImpl reg = new UIRegistryAmicableImpl();
		reg.register(key, component);

		Assert.assertTrue("should have been successfully registered", reg.isRegistered(key));
	}

	/**
	 * Test register - exception on register with key already in use.
	 */
	@Test
	public void testRegisterFail() {
		final String key = "test123";
		WComponent component = new DefaultWComponent();

		UIRegistryAmicableImpl reg = new UIRegistryAmicableImpl();
		reg.register(key, component);

		try {
			reg.register(key, component);
			Assert.fail(
					"attempted registration with key already used should have thrown an exception");
		} catch (SystemException e) {
			String expectedMessage = "Cannot re-register a component. Key = " + key;
			Assert.assertEquals("exceptions hould have contained message expected", expectedMessage,
					e.getMessage());
		}
	}

	/**
	 * Test isRegistered - for cases where key - exists, not exist, empty, null.
	 */
	@Test
	public void testIsRegistered() {
		final String keyExists = "test123";
		final String keyNotExist = "nothingtobefound";
		final String keyEmpty = "";
		final String keyNull = null;
		WComponent component = new DefaultWComponent();

		UIRegistryAmicableImpl reg = new UIRegistryAmicableImpl();
		reg.register(keyExists, component);

		Assert.assertTrue("should find component", reg.isRegistered(keyExists));
		Assert.assertFalse("should not find component", reg.isRegistered(keyNotExist));
		Assert.assertFalse("should not find component - key empty", reg.isRegistered(keyEmpty));
		Assert.assertFalse("should not find component - key null", reg.isRegistered(keyNull));
	}

	/**
	 * Test getUI - successfully get a component already registered.
	 */
	@Test
	public void testGetUIRegistered() {
		final String key = "test123";
		WComponent component = new DefaultWComponent();

		UIRegistryAmicableImpl reg = new UIRegistryAmicableImpl();
		reg.register(key, component);

		Assert.assertSame("should return component registered", component, reg.getUI(key));
	}

	/**
	 * Test getUI - nothing registered - no class creatable from key - returns. FatalErrorPage
	 */
	@Test
	public void testGetUINotRegisteredNoClass() {
		final String key = "NO_CLASS_BY_THIS_NAME";
		final String expectedClassName = FatalErrorPage.class.getName();

		UIRegistryAmicableImpl reg = new UIRegistryAmicableImpl();
		WComponent result = reg.getUI(key);

		Assert.assertNotNull("should return a fatal error page - when no class found", result);
		Assert.assertEquals("should be of the expected type", expectedClassName, result.getClass().
				getName());
		Assert.assertFalse("should not be cached", reg.isRegistered(key));
	}

	/**
	 * Test getUI - nothing registered - class creatable from key - but not a WComponent.
	 */
	@Test
	public void testGetUINotRegisteredNotWComponent() {
		final String key = "java.lang.String";
		final String expectedClassName = FatalErrorPage.class.getName();

		UIRegistryAmicableImpl reg = new UIRegistryAmicableImpl();
		WComponent result = reg.getUI(key);

		Assert.assertNotNull("should return a fatal error page - when no WComponentclass", result);
		Assert.assertEquals("should be of the expected type", expectedClassName, result.getClass().
				getName());
		Assert.assertFalse("should not be cached", reg.isRegistered(key));
	}

	/**
	 * Test getUI - nothing registered - but WComponent creatable from key.
	 */
	@Test
	public void testGetUINotRegisteredWComponent() {
		final String key = "com.github.bordertech.wcomponents.WButton";
		UIRegistryAmicableImpl reg = new UIRegistryAmicableImpl();
		WComponent result = reg.getUI(key);

		Assert.assertTrue("should return an instantiated WComponent", result instanceof WComponent);
		Assert.assertTrue("the WComponent should be in the registry", reg.isRegistered(key));
		Assert.assertTrue("the WComponent should be locked", result.isLocked());
	}
}
