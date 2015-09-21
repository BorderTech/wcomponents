package com.github.bordertech.wcomponents;

/**
 * The super-interface for "bean-aware" components. Components should normally not implement this interface, but either
 * {@link BeanBound} or {@link BeanProviderBound}, as those interfaces provide methods of supplying the bean.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface BeanAware {

	/**
	 * Sets the bean property that this component is interested in.
	 *
	 * @param propertyName the bean property name
	 */
	void setBeanProperty(String propertyName);

	/**
	 * @return the bean property name this component is interested in
	 */
	String getBeanProperty();

	/**
	 * Retrieves the bean for the given context.
	 *
	 * @return the bean for the given context.
	 */
	Object getBean();

	/**
	 * Retrieves the value this component is interested in from the bean. This is derived from the
	 * {@link #getBeanProperty()} and {@link #getBean()}.
	 *
	 * @return the bean value, or null if no bean value is available.
	 */
	Object getBeanValue();

}
