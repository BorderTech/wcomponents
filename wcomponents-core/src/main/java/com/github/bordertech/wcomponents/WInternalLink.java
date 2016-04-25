package com.github.bordertech.wcomponents;

/**
 * This component enables the creation of links within a page.
 *
 * <p>
 * Current limitations of this component are:</p>
 * <ul>
 * <li>It will only link to components that render out their id</li>
 * <li>It will not link to components nested within WRepeater of WTable components</li>
 * <li>The text for this link is the same for all users.
 * </ul>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WInternalLink extends AbstractWComponent implements AjaxTarget {

	/**
	 * Creates a WInternalLink with no text or component.
	 */
	public WInternalLink() {
	}

	/**
	 * Creates a WInternalLink with the specified text and component to link to.
	 *
	 * @param text the text to display on the link.
	 * @param reference the component to link to.
	 */
	public WInternalLink(final String text, final WComponent reference) {
		InternalLinkModel model = getComponentModel();
		model.text = text;
		model.reference = reference;
	}

	/**
	 * @return the component to which this component is linked.
	 */
	public WComponent getReference() {
		return getComponentModel().reference;
	}

	/**
	 * Sets the component to which this component is linked.
	 *
	 * @param reference the component.
	 */
	public void setReference(final WComponent reference) {
		getOrCreateComponentModel().reference = reference;
	}

	/**
	 * @return the text to be displayed for this link.
	 */
	public String getText() {
		return getComponentModel().text;
	}

	/**
	 * Sets the text to be displayed for this link.
	 *
	 * @param text the text to be displayed.
	 */
	public void setText(final String text) {
		getOrCreateComponentModel().text = text;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text);
	}

	/**
	 * Creates a new InternalLinkModel.
	 *
	 * @return a new InternalLinkModel
	 */
	@Override
	protected InternalLinkModel newComponentModel() {
		return new InternalLinkModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected InternalLinkModel getComponentModel() {
		return (InternalLinkModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected InternalLinkModel getOrCreateComponentModel() {
		return (InternalLinkModel) super.getOrCreateComponentModel();
	}

	/**
	 * InternalLinkModel holds Extrinsic state management of the component.
	 */
	public static class InternalLinkModel extends ComponentModel {

		/**
		 * The Wcomponent to which this component is linked.
		 */
		private WComponent reference;

		/**
		 * The text displayed for this link.
		 */
		private String text;
	}
}
