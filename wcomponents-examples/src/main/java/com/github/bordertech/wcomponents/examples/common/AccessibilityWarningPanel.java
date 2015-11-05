package com.github.bordertech.wcomponents.examples.common;

import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;

/**
 * Common warning message for WComponent examples to flag an example as having potential accessibility, design or
 * best-practice issues.
 *
 */
public class AccessibilityWarningPanel extends WPanel {

	private static final String WARNING = "This example may contain poor structure or layout which may lead to accessibility problems and should not be used as a guide to best practice for anything other than the explicit example component.";
	private static final String HEADING = "Warning";

	/**
	 * Construct panel.
	 */
	public AccessibilityWarningPanel() {
		WStyledText warningText = new WStyledText(WARNING);
		warningText.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
		add(new WHeading(WHeading.MAJOR, HEADING));
		add(warningText);
	}
}
