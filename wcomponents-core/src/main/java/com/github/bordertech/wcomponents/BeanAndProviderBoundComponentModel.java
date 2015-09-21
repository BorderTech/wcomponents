package com.github.bordertech.wcomponents;

/**
 * The {@link ComponentModel} for components that support binding to a bean either directly or through a BeanProvider. A
 * direct binding has higher precedence than an indirect binding.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class BeanAndProviderBoundComponentModel extends DataBoundComponentModel {

	/**
	 * The associated bean.
	 */
	private Object bean;

	/**
	 * The associated bean id.
	 */
	private Object beanId;

	/**
	 * The component's BeanProvider.
	 */
	private BeanProvider beanProvider;

	/**
	 * This bean property that the component is interested in. The property is specified using Jakarta BeanUtils bean
	 * notation.
	 */
	private String beanProperty;

	/**
	 * Flag if search through ancestors to find bean.
	 */
	private boolean searchAncestors = true;

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

	/**
	 * @return the bean provider.
	 */
	protected BeanProvider getBeanProvider() {
		return beanProvider;
	}

	/**
	 * Sets the bean provider.
	 *
	 * @param beanProvider the bean provider to set.
	 */
	protected void setBeanProvider(final BeanProvider beanProvider) {
		this.beanProvider = beanProvider;
	}

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

	/**
	 * @return the bean property that the component is interested in.
	 */
	public String getBeanProperty() {
		return beanProperty;
	}

	/**
	 * Sets the bean property that the component is interested in. The property is specified using Jakarta BeanUtils
	 * bean notation.
	 *
	 * @param beanProperty The bean property to set.
	 */
	public void setBeanProperty(final String beanProperty) {
		this.beanProperty = beanProperty;
	}

	/**
	 * @return true if search ancestors for the components bean
	 */
	public boolean isSearchAncestors() {
		return searchAncestors;
	}

	/**
	 * @param search true if search ancestors for the components bean
	 */
	public void setSearchAncestors(final boolean search) {
		this.searchAncestors = search;
	}
}
