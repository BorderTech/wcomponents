package com.github.bordertech.wcomponents;

/**
 * The {@link ComponentModel} for {@link BeanBound} components.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class BeanBoundComponentModel extends DataBoundComponentModel {

	/**
	 * The associated bean.
	 */
	private Object bean;

	/**
	 * @return the bean.
	 */
	protected Object getBean() {
		return bean;
	}

	/**
	 * @param bean the bean to set.
	 */
	protected void setBean(final Object bean) {
		this.bean = bean;
	}
}
