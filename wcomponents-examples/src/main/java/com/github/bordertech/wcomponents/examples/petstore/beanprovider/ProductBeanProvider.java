package com.github.bordertech.wcomponents.examples.petstore.beanprovider;

import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.examples.petstore.model.PetStoreDao;

/**
 * <p>
 * ProductBeanProvider is a bean provider that provides ProductBeans. This class is a singleton, as no state is
 * maintained.</p>
 *
 * <p>
 * This provider expects that the bound object provides an int value, which is the id of the product that need to be
 * read.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class ProductBeanProvider implements BeanProvider {

	/**
	 * Singleton instance.
	 */
	private static final ProductBeanProvider INSTANCE = new ProductBeanProvider();

	/**
	 * Don't allow external instantiation of this class.
	 */
	private ProductBeanProvider() {
	}

	/**
	 * @return the singleton instance of the bean provider.
	 */
	public static ProductBeanProvider getInstance() {
		return INSTANCE;
	}

	/**
	 * Retrieves the bean for the bound object. Expects that the bound object provides an int value, which is the id of
	 * the product that needs to be read.
	 *
	 * @param beanProviderBound the object bound to this provider.
	 * @return a ProductBean with the id provided by the bound object, or null if no id was provided.
	 */
	@Override
	public Object getBean(final BeanProviderBound beanProviderBound) {
		Integer productId = (Integer) beanProviderBound.getBeanId();

		if (productId != null) {
			return PetStoreDao.readProduct(productId);
		}

		return null;
	}
}
