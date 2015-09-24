package com.github.bordertech.wcomponents.examples.petstore.beanprovider;

import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.examples.petstore.model.PetStoreDao;
import java.util.Arrays;

/**
 * <p>
 * InventoryBeanProvider is a bean provider that provides InventoryBeans. This class is a singleton, as no state is
 * maintained.</p>
 *
 * <p>
 * This provider expects that the bound object provides int / int[] values, which are the product id(s) that need to be
 * read.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class InventoryBeanProvider implements BeanProvider {

	/**
	 * Singleton instance.
	 */
	private static final InventoryBeanProvider INSTANCE = new InventoryBeanProvider();

	/**
	 * Prevent external instantiation of this class.
	 */
	private InventoryBeanProvider() {
	}

	/**
	 * @return the singleton instance of the bean provider.
	 */
	public static InventoryBeanProvider getInstance() {
		return INSTANCE;
	}

	/**
	 * Retrieves the bean for the bound object. Expects that the bound object provides int / int[] values, which are the
	 * product id(s) that need to be read.
	 *
	 * @param beanProviderBound the object bound to this provider.
	 * @return one or an array of InventoryBean, depending on the id provided by the bound object.
	 */
	@Override
	public Object getBean(final BeanProviderBound beanProviderBound) {
		Object param = beanProviderBound.getBeanId();

		if (param instanceof Integer) {
			// Support single item reads
			return PetStoreDao.readInventory(((Integer) param));
		} else if (param instanceof int[]) {
			// Support multiple item reads (a form of pagination)
			return PetStoreDao.readInventory((int[]) param);
		}

		// Otherwise, return the entire inventory
		return Arrays.asList(PetStoreDao.readInventory());
	}
}
