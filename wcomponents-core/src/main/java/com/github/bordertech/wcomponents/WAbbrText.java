package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.LookupTable;

/**
 * The WAbbrText component represents an abbreviation or acronym and the full textual description for its (abbreviated)
 * text content.
 *
 * @author Kishan Bisht
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WAbbrText extends WText {

	/**
	 * The lookup table instance for this application.
	 */
	private static final LookupTable TABLE = Factory.newInstance(LookupTable.class);

	/**
	 * Creates an empty WAbbrText.
	 *
	 * An instance of WAbbrText created in this manner must have abbreviated display text and a toolTip (the full text
	 * represented by the abbreviation) set to be useful.
	 */
	public WAbbrText() {
	}

	/**
	 * Creates a WAbbrText with the specified abbreviated display text.
	 *
	 * An instance of WAbbrText created in this manner must have a toolTip (the full text represented by the
	 * abbreviation) set to be useful.
	 *
	 * @param text The abbreviated text to display.
	 */
	public WAbbrText(final String text) {
		super(text);
	}

	/**
	 * Creates a WAbbrText with the specified abbreviated display text and toolTip full expansion text.
	 *
	 * @param text The abbreviated text to display.
	 * @param description The full text represented by the abbreviation.
	 */
	public WAbbrText(final String text, final String description) {
		this(text);
		setToolTip(description);
	}

	//================================
	// Attributes
	/**
	 * @return the expanded text represented by the abbreviation.
	 * @deprecated use getToolTip instead.
	 */
	public String getAbbrText() {
		return getToolTip();
	}

	/**
	 * Sets the full text represented by the abbreviation.
	 *
	 * @param abbrText The full text represented by the abbreviation.
	 * @deprecated use setToolTip instead.
	 */
	public void setAbbrText(final String abbrText) {
		setToolTip(abbrText);
	}

	/**
	 * Loads the abbreviated text component from the given code reference table entry.
	 *
	 * The display (abbreviated) text is set to the table entry's description. The toolTip is set to the table entry's
	 * code.
	 *
	 * @param entry the CRT entry to use.
	 */
	public void setTextWithDesc(final Object entry) {
		setText(TABLE.getDescription(null, entry));
		setToolTip(TABLE.getCode(null, entry));
	}

	/**
	 * Loads the abbreviated text component from the given code reference table entry.
	 *
	 * The display (abbreviated) text is set to the table entry's code. The toolTip is set to the table entry's
	 * description.
	 *
	 * @param entry the CRT entry to use.
	 */
	public void setTextWithCode(final Object entry) {
		setText(TABLE.getCode(null, entry));
		setToolTip(TABLE.getDescription(null, entry));
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
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
