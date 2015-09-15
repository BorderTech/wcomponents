package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.LookupTable;

/**
 * <p>
 * The WAbbrText component shows a further textual description for its
 * (abbreviated) text content.</p>
 *
 * @author Kishan Bisht
 * @since 1.0.0
 */
public class WAbbrText extends WText {

	/**
	 * The lookup table instance for this application.
	 */
	private static final LookupTable TABLE = Factory.newInstance(LookupTable.class);

	/**
	 * Holds the extrinsic state information of a WAbbrText.
	 */
	public static class AbbrTextModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The description for the text.
		 */
		private String abbr;
	}

	/**
	 * Creates an empty WAbbrText.
	 */
	public WAbbrText() {
	}

	/**
	 * Creates a WAbbrText with the specified text.
	 *
	 * @param text the text to display.
	 */
	public WAbbrText(final String text) {
		super(text);
	}

	/**
	 * Creates a WAbbrText with the specified text.
	 *
	 * @param text the text to display.
	 * @param abbr the tool-tip (abbreviation).
	 */
	public WAbbrText(final String text, final String abbr) {
		this(text);
		getComponentModel().abbr = abbr;
	}

	//================================
	// Attributes
	/**
	 * @return the abbreviated text.
	 */
	public String getAbbrText() {
		return getComponentModel().abbr;
	}

	/**
	 * Sets the abbreviated text.
	 *
	 * @param abbrText the abbreviated text.
	 */
	public void setAbbrText(final String abbrText) {
		getOrCreateComponentModel().abbr = abbrText;
	}

	/**
	 * Loads the abbreviated text component from the given code reference table
	 * entry. The text is set to the description. The abbreviated text is set to
	 * the code.
	 *
	 * @param entry the CRT entry to use.
	 */
	public void setTextWithDesc(final Object entry) {
		setText(TABLE.getDescription(null, entry));
		setAbbrText(TABLE.getCode(null, entry));
	}

	/**
	 * Loads the abbreviated text component from the given code reference table
	 * entry. The text is set to the code. The abbreviated text is set to the
	 * description.
	 *
	 * @param entry the CRT entry to use.
	 */
	public void setTextWithCode(final Object entry) {
		setText(TABLE.getCode(null, entry));
		setAbbrText(TABLE.getDescription(null, entry));
	}

	/**
	 * @return a String representation of this component, for debugging
	 * purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : ('"' + text + '"');

		String abbrText = getAbbrText();
		abbrText = abbrText == null ? "null" : ('"' + abbrText + '"');

		return toString("text=" + text + ", abbrText=" + abbrText);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new model appropriate for this component.
	 *
	 * @return a new {@link AbbrTextModel}.
	 */
	@Override
	protected AbbrTextModel newComponentModel() {
		return new AbbrTextModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected AbbrTextModel getComponentModel() {
		return (AbbrTextModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected AbbrTextModel getOrCreateComponentModel() {
		return (AbbrTextModel) super.getOrCreateComponentModel();
	}
}
