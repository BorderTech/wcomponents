package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * A default implementation of the {@link Container} interface. This can be used as a generic container for components
 * which do not need to be referenced together. If you need to target a collection of components for use in e.g. AJAX,
 * you should use a {@link WPanel} instead.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WContainer extends WBeanComponent implements MutableContainer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final WComponent component) {
		super.add(component);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	@Override
	public void add(final WComponent component, final String tag) {
		super.add(component, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildCount() {
		return super.getChildCount();
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final WComponent child) {
		super.remove(child);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAll() {
		super.removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	@Override
	// to make public
	public void setTemplate(final String templateUrl) {
		super.setTemplate(templateUrl);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	@Override
	// to make public
	public void setTemplateMarkUp(final String markUp) {
		super.setTemplateMarkUp(markUp);
	}

	/**
	 * Associates a velocity template with this component. A simple mapping is applied to the given class to derive the
	 * name of a velocity template.
	 * <p>
	 * For instance, com.github.bordertech.wcomponents.WTextField would map to the template
	 * com/github/bordertech/wcomponents/WTextField.vm
	 * </p>
	 *
	 * @param clazz the class to use to retrieve the template.
	 * @deprecated use {@link #setTemplate(String)}.
	 */
	@Override
	// to make public
	@Deprecated
	public void setTemplate(final Class clazz) {
		super.setTemplate(clazz);
	}

	/**
	 * A naming context is only considered active if it has been set active via {@link #setNamingContext(boolean)} and
	 * also has an id name set via {@link #setIdName(String)}.
	 *
	 * @param context set true if this is a naming context.
	 */
	public void setNamingContext(final boolean context) {
		setFlag(ComponentModel.NAMING_CONTEXT_FLAG, context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamingContext() {
		return isFlagSet(ComponentModel.NAMING_CONTEXT_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamingContextId() {
		return getId();
	}

}
