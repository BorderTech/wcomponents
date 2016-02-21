package com.github.bordertech.wcomponents;

/**
 * A trivial implementation of AbstractWComponent. This component has no behaviour or appearance.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class DefaultWComponent extends AbstractWComponent {

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	@Override // to make public
	public void setTemplate(final String aUrl) {
		super.setTemplate(aUrl);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	@Override // to make public
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
	@Override // to make public
	@Deprecated
	public void setTemplate(final Class clazz) {
		super.setTemplate(clazz);
	}
}
