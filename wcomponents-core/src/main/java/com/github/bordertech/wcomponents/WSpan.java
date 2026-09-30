package com.github.bordertech.wcomponents;

/**
 * A SPAN element that can be used to wrap text and set HTML attributes and classes.
 */
public class WSpan extends WText {

	/**
	 * Creates an empty WSpan.
	 */
	public WSpan() {
		// Do nothing
	}

	/**
	 * Creates a WSpan with the given initial text.
	 *
	 * @param text the text to display in the SPAN
	 */
	public WSpan(final String text) {
		super(text);
	}

	/**
	 * @return true if span is aria hidden, otherwise false
	 */
	public boolean isAriaHidden() {
		return getComponentModel().ariaHidden;
	}

	/**
	 * Allows the span to be hidden from screen readers. This can be handy when using classes like fontawesome.
	 *
	 * @param ariaHidden true if span is aria hidden, otherwise false
	 */
	public void setAriaHidden(final boolean ariaHidden) {
		getOrCreateComponentModel().ariaHidden = ariaHidden;
	}

	/**
	 * @return true if span's role is image, otherwise false
	 */
	public boolean isImageRole() {
		return getComponentModel().imageRole;
	}

	/**
	 * Allows the span's role to be set as an image. When used as an image the accessibleText should also be set.
	 *
	 * @param imageRole true if span's role is image, otherwise false
	 */
	public void setImageRole(final boolean imageRole) {
		getOrCreateComponentModel().imageRole = imageRole;
	}

	/**
	 * Holds the extrinsic state information of a WSpan.
	 */
	public static class SpanModel extends TextModel {

		/**
		 * The aria hidden attribute flag.
		 */
		private boolean ariaHidden;

		/**
		 * The image role attribute flag.
		 */
		private boolean imageRole;
	}

	@Override
	protected SpanModel newComponentModel() {
		return new SpanModel();
	}

	@Override
	protected SpanModel getComponentModel() {
		return (SpanModel) super.getComponentModel();
	}

	@Override
	protected SpanModel getOrCreateComponentModel() {
		return (SpanModel) super.getOrCreateComponentModel();
	}

}
