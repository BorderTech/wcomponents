package com.github.bordertech.wcomponents.examples.petstore.beanprovider;

import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WBeanComponent;
import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean;
import com.github.bordertech.wcomponents.examples.petstore.model.PetStoreDao;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link InventoryBeanProvider}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class InventoryBeanProvider_Test {

	/**
	 * test getInstance.
	 */
	@Test
	public void testGetInstance() {
		Object object = InventoryBeanProvider.getInstance();
		Assert.assertTrue("should return instance of InventoryBeanProvider",
				object instanceof InventoryBeanProvider);
	}

	/**
	 * test getBean - get specific Id.
	 */
	@Test
	public void testGetBeanSingleId() {
		final int testProductId = 1;

		InventoryBeanProvider provider = InventoryBeanProvider.getInstance();

		BeanProviderBound beanProviderBound = new WBeanComponent();
		beanProviderBound.setBeanId(Integer.valueOf(testProductId));

		Object obj = provider.getBean(beanProviderBound);
		Assert.assertTrue("should return an InventoryBean", obj instanceof InventoryBean);
		Assert.assertEquals("the inventoryBean should have the requested productId", testProductId,
				((InventoryBean) obj)
				.getProductId());
	}

	/**
	 * test getBean - get sub array of beans.
	 */
	@Test
	public void testGetBeanArrayOfIds() {
		final int[] testProductIds = new int[]{0, 1, 2};

		InventoryBeanProvider provider = InventoryBeanProvider.getInstance();

		BeanProviderBound beanProviderBound = new WBeanComponent();
		beanProviderBound.setBeanId(testProductIds);

		Object obj = provider.getBean(beanProviderBound);
		Assert.assertTrue("should return an InventoryBean[]", obj instanceof InventoryBean[]);
		InventoryBean[] beans = (InventoryBean[]) obj;
		Assert.assertEquals("should return an InventoryBean[] of right length",
				testProductIds.length, beans.length);

		for (int i = 0; i < testProductIds.length; i++) {
			Assert.assertEquals("product " + i + "  not match expected", beans[i].getProductId(),
					testProductIds[i]);
		}
	}

	/**
	 * test getBean - get array of all beans.
	 */
	@Test
	public void testGetBeanArrayOfIdsAllBeans() {
		InventoryBeanProvider provider = InventoryBeanProvider.getInstance();

		BeanProviderBound beanProviderBound = new WBeanComponent();
		beanProviderBound.setBeanId(Float.valueOf(42)); // unknown object triggers full list

		Object obj = provider.getBean(beanProviderBound);
		Assert.assertTrue("should return a List of InventoryBean", obj instanceof List<?>);

		List<?> allBeansReturned = (List<?>) obj;
		List<?> allPetStoreBeans = Arrays.asList(PetStoreDao.readInventory());

		Assert.assertEquals("should get all of the store beans", allBeansReturned.size(),
				allPetStoreBeans.size());
		Assert.assertTrue("provider returned beans contains all beans in the store",
				allBeansReturned
				.containsAll(allPetStoreBeans));
	}
}
