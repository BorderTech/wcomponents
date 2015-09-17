package com.github.bordertech.wcomponents;

/**
 * Components implementing this interface are bound to a bean through a BeanProvider. The BeanProvider can obtain the
 * bean from any source (e.g. a database or service call) using the bean id obtained from the BeanProviderBound
 * component.
 *
 * @see BeanBound
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface BeanProviderBound extends BeanAware {

	/**
	 * Sets the bean provider.
	 *
	 * @param beanProvider the bean provider to set
	 */
	void setBeanProvider(BeanProvider beanProvider);

	/**
	 * Sets the bean id.
	 *
	 * @param beanId the bean id to set
	 */
	void setBeanId(Object beanId);

	/**
	 * Retrieves the bean id.
	 *
	 * @return the bean id.
	 */
	Object getBeanId();
}
