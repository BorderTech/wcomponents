package com.github.bordertech.wcomponents;

/**
 * <p>
 * The BeanProvider interface can be implemented by applications to dynamically supply BeanProviderBound WComponents
 * with the bean data they require, whenever they request it.</p>
 *
 * <p>
 * The bean itself may be retrieved from any source, but using a caching DMS or the application cache is strongly
 * advised if the bean is fetched from a remote source.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface BeanProvider {

	/**
	 * Obtains the bean for the given {@link BeanProviderBound}. Implementations of this method should retrieve the bean
	 * id from the BeanProviderBound using its {@link BeanProviderBound#getBeanId() getBeanId()} method.
	 *
	 * @param beanProviderBound the BeanProviderBound to provide data for.
	 * @return the bean, or null if the bean could not be retrieved.
	 */
	Object getBean(BeanProviderBound beanProviderBound);
}
