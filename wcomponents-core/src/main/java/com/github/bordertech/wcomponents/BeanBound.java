package com.github.bordertech.wcomponents;

/**
 * Components implementing this interface are directly bound to a bean. Storage of the bean is implementation dependent,
 * but will most likely be in the user session.
 *
 * @see BeanProviderBound
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface BeanBound extends BeanAware {

	/**
	 * Sets the bean for the given context.
	 *
	 * @param bean the bean to set
	 */
	void setBean(Object bean);

	/**
	 * Flag to indicate if a bean should be searched for up the component tree. If set true and the current component
	 * does not have a bean, then ask the next bean aware ancestor for its bean. If false and the component does not
	 * have a bean, then do not search the tree for a bean and return null.
	 * <p>
	 * Setting this to false at the correct bean container level can stop a bean component going up the whole component
	 * tree looking for its bean and being given the wrong bean.
	 * </p>
	 *
	 * @return true if search ancestors for a bean, otherwise false to not search
	 */
	boolean isSearchAncestors();

}
