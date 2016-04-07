package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.LookupTable;

/**
 * The WAbbrText component represents an abbreviation or acronym and the full textual description for its (abbreviated)
 * text content.
 *
 * @author Kishan Bisht, Mark Reeves
 * @since 1.0.0
 */
public class WAbbrText extends WText {
	/**
	 * The lookup table instance for this application.
	 */
	private static final LookupTable TABLE = Factory.newInstance(LookupTable.class);

	/**
	 * <p>
	 * Creates an empty WAbbrText.
	 * </p>
	 * <p>
	 * An instance of WAbbrText created in this manner must have abbreviated display text and a toolTip (the full text
	 * represented by the abbreviation) set to be useful.
	 * </p>
	 */
	public WAbbrText() {
	}

	/**
	 * <p>
	 * Creates a WAbbrText with the specified abbreviated display text.
	 * </p>
	 * <p>
	 * An instance of WAbbrText created in this manner must have a toolTip (the full text represented by the
	 * abbreviation) set to be useful.
	 * </p>
	 *
	 * @param text The abbreviated text to display
	 */
	public WAbbrText(final String text) {
		super(text);
	}

	/**
	 * Creates a WAbbrText with the specified abbreviated display text and toolTip full expansion text.
	 *
	 * @param text the abbreviated text to display
	 * @param description the full text represented by the abbreviation
	 */
	public WAbbrText(final String text, final String description) {
		this(text);
		setToolTip(description);
	}

	//================================
	// Attributes
	/**
	 * @return the expanded text represented by the abbreviation
	 * @deprecated as of WComponents 1.0.0, use {@link #getToolTip()} instead
	 */
	public String getAbbrText() {
		return getToolTip();
	}

	/**
	 * Loads the abbreviated text component from the given code reference table entry using the entry's code as the
	 * visible (abbreviated) text and the entry's description as the abbreviation's expanded text toolTip.
	 *
	 * @param entry the CRT entry to use.
	 */
	public void setTextWithCode(final Object entry) {
		setText(TABLE.getCode(null, entry));
		setToolTip(TABLE.getDescription(null, entry));
	}

	/**
	 * Sets the full text represented by the abbreviation.
	 *
	 * @param abbrText the full text (expansion) represented by the abbreviation
	 * @deprecated As of WComponents 1.0.0, use {@link #setToolTip(String, Serializable...)} instead.
	 */
	public void setAbbrText(final String abbrText) {
		setToolTip(abbrText);
	}

	/**
	 * Loads the abbreviated text component from the given code reference table entry using the entry's description as
	 * the visible (abbreviated) text and the entry's code as the toolTip (expansion). You probably do not want this:
	 * you probably want {@link #setTextWithCode(java.lang.Object)}.
	 *
	 * @param entry the CRT entry to use
	 */
	public void setTextWithDesc(final Object entry) {
		setText(TABLE.getDescription(null, entry));
		setToolTip(TABLE.getCode(null, entry));
	}

	/**
	 * Get a String representation of the component. This would normally only be required for debugging purposes.
	 *
	 * @return a String representation of this component
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : '"' + text + '"';

		String expandedText = getToolTip();
		expandedText = expandedText == null ? "null" : '"' + expandedText + '"';

		return toString("text=" + text + ", toolTip=" + expandedText);
	}
}
