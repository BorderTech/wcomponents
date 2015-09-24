package com.github.bordertech.wcomponents.examples.petstore.beanprovider;

import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WBeanComponent;
import com.github.bordertech.wcomponents.examples.petstore.PetStoreLookupTable;
import com.github.bordertech.wcomponents.util.Config;
import junit.framework.Assert;
import org.apache.commons.configuration.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link CrtBeanProvider}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class CrtBeanProvider_Test {

	/**
	 * Test unknown crt name.
	 */
	private static final String UNKNOWN_CRT_NAME = "nameXXX";

	/**
	 * Test unknown crt code.
	 */
	private static final String UNKNOWN_CRT_CODE = "codeXXX";

	/**
	 * Test valid crt name.
	 */
	private static final String VALID_CRT_NAME = "message_of_the_day";

	/**
	 * Test valid crt code.
	 */
	private static final String VALID_CRT_CODE = "DEFAULT";

	/**
	 * Used to restore the default configuration after tests complete.
	 */
	private static Configuration originalConfig;

	@BeforeClass
	public static void setUp() {
		originalConfig = Config.getInstance();

		Configuration config = Config.copyConfiguration(originalConfig);
		config.setProperty(
				"bordertech.wcomponents.factory.impl.com.github.bordertech.wcomponents.util.LookupTable",
				PetStoreLookupTable.class.getName());

		Config.setConfiguration(config);
	}

	@AfterClass
	public static void tearDown() {
		// Remove overrides
		Config.setConfiguration(originalConfig);
	}

	/**
	 * Test constructor - just name given - name null.
	 */
	@Test
	public void testConstructorName() {
		CrtBeanProvider provider = new CrtBeanProvider(null);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);
		Assert.assertNull("crtBean should be null", crtBean);
	}

	/**
	 * Test constructor - name and code given - name null.
	 */
	@Test
	public void testConstructorNameCode() {
		CrtBeanProvider provider = new CrtBeanProvider(null, UNKNOWN_CRT_CODE);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);
		Assert.assertNull("crtBean should be null", crtBean);
	}

	/**
	 * Test getBean - name and code in constructor. entry from code table not found.
	 */
	@Test
	public void testGetBeanCase1() {
		CrtBeanProvider provider = new CrtBeanProvider(UNKNOWN_CRT_NAME, UNKNOWN_CRT_CODE);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);

		Assert.assertNull("crtBean should be null", crtBean);
	}

	/**
	 * Test getBean - name and code in constructor. entry from code table found.
	 */
	@Test
	public void testGetBeanCase2() {
		CrtBeanProvider provider = new CrtBeanProvider(VALID_CRT_NAME, VALID_CRT_CODE);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);

		Assert.assertNotNull("crtBean should not be null", crtBean);
	}

	/**
	 * Test getBean - name but not code in constructor - code from BeanProvider. entry from code table not found.
	 */
	@Test
	public void testGetBeanCase3() {
		CrtBeanProvider provider = new CrtBeanProvider(VALID_CRT_NAME);

		BeanProviderBound beanProviderBound = new WBeanComponent();
		beanProviderBound.setBeanId(UNKNOWN_CRT_CODE);
		Object crtBean = provider.getBean(beanProviderBound);

		Assert.assertNull("crtBean should be null", crtBean);
	}

	/**
	 * Test getBean - name but not code in constructor - code from BeanProvider. entry from code table found.
	 */
	@Test
	public void testGetBeanCase4() {
		CrtBeanProvider provider = new CrtBeanProvider(VALID_CRT_NAME);

		BeanProviderBound beanProviderBound = new WBeanComponent();
		beanProviderBound.setBeanId(VALID_CRT_CODE);
		Object crtBean = provider.getBean(beanProviderBound);

		Assert.assertNotNull("crtBean should not be null", crtBean);
	}
}
