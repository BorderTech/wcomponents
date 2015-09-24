package com.github.bordertech.wcomponents;

/**
 * The {@link ComponentModel} for {@link BeanProviderBound} components.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class BeanProviderBoundComponentModel extends DataBoundComponentModel {

	/**
	 * The bean id.
	 */
	private Object beanId;

	/**
	 * @return the bean id.
	 */
	protected Object getBeanId() {
		return beanId;
	}

	/**
	 * @param beanId the bean id to set.
	 */
	protected void setBeanId(final Object beanId) {
		this.beanId = beanId;
	}
}
