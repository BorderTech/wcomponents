package com.github.bordertech.wcomponents.examples.petstore.beanprovider;

import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WBeanComponent;
import com.github.bordertech.wcomponents.examples.petstore.model.ProductBean;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ProductBeanProvider}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class ProductBeanProvider_Test {

	/**
	 * Test getInstance.
	 */
	@Test
	public void testGetInstance() {
		Object object = ProductBeanProvider.getInstance();
		Assert.assertTrue("should return instance of ProductBeanProvider",
				object instanceof ProductBeanProvider);
	}

	/**
	 * Test getBean - get specific Id.
	 */
	@Test
	public void testGetBeanSingleId() {
		final int testProductId = 1;

		ProductBeanProvider provider = ProductBeanProvider.getInstance();

		BeanProviderBound beanProviderBound = new WBeanComponent();
		beanProviderBound.setBeanId(Integer.valueOf(testProductId));

		Object obj = provider.getBean(beanProviderBound);

		Assert.assertTrue("should return a ProductBean", obj instanceof ProductBean);
		Assert.assertEquals("the ProductBean should have the requested productId", testProductId,
				((ProductBean) obj).getId());
	}

	/**
	 * Test getBean - beanProviderBound not set.
	 */
	@Test
	public void testGetBeanWhenBeanProviderBoundNotSet() {
		ProductBeanProvider provider = ProductBeanProvider.getInstance();

		BeanProviderBound beanProviderBound = new WBeanComponent();
		// but nothing set

		Assert.assertNull("should return null", provider.getBean(beanProviderBound));
	}
}
