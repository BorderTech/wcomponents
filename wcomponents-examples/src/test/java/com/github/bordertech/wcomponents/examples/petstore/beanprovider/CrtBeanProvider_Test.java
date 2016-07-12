package com.github.bordertech.wcomponents.examples.petstore.beanprovider;

import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WBeanComponent;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link PetStoreLookupTableCrtBeanProvider}.
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
	 * Test constructor - just name given - name null.
	 */
	@Test
	public void testConstructorName() {
		PetStoreLookupTableCrtBeanProvider provider = new PetStoreLookupTableCrtBeanProvider(null);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);
		Assert.assertNull("crtBean should be null", crtBean);
	}

	/**
	 * Test constructor - name and code given - name null.
	 */
	@Test
	public void testConstructorNameCode() {
		PetStoreLookupTableCrtBeanProvider provider = new PetStoreLookupTableCrtBeanProvider(null, UNKNOWN_CRT_CODE);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);
		Assert.assertNull("crtBean should be null", crtBean);
	}

	/**
	 * Test getBean - name and code in constructor. entry from code table not found.
	 */
	@Test
	public void testGetBeanCase1() {
		PetStoreLookupTableCrtBeanProvider provider = new PetStoreLookupTableCrtBeanProvider(UNKNOWN_CRT_NAME, UNKNOWN_CRT_CODE);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);

		Assert.assertNull("crtBean should be null", crtBean);
	}

	/**
	 * Test getBean - name and code in constructor. entry from code table found.
	 */
	@Test
	public void testGetBeanCase2() {
		PetStoreLookupTableCrtBeanProvider provider = new PetStoreLookupTableCrtBeanProvider(VALID_CRT_NAME, VALID_CRT_CODE);

		BeanProviderBound beanProviderBound = new WBeanComponent(); // with no bean set
		Object crtBean = provider.getBean(beanProviderBound);

		Assert.assertNotNull("crtBean should not be null", crtBean);
	}

	/**
	 * Test getBean - name but not code in constructor - code from BeanProvider. entry from code table not found.
	 */
	@Test
	public void testGetBeanCase3() {
		PetStoreLookupTableCrtBeanProvider provider = new PetStoreLookupTableCrtBeanProvider(VALID_CRT_NAME);

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
		PetStoreLookupTableCrtBeanProvider provider = new PetStoreLookupTableCrtBeanProvider(VALID_CRT_NAME);

		BeanProviderBound beanProviderBound = new WBeanComponent();
		beanProviderBound.setBeanId(VALID_CRT_CODE);
		Object crtBean = provider.getBean(beanProviderBound);

		Assert.assertNotNull("crtBean should not be null", crtBean);
	}
}
