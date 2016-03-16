package com.github.bordertech.wcomponents.examples.common;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;

/**
 * Common warning message for WComponent examples to flag an example as having potential accessibility, design or
 * best-practice issues.
 *
 */
public class AccessibilityWarningContainer extends WContainer {

	private static final String WARNING = "This example may contain poor structure or layout which may lead to accessibility problems and should not be used as a guide to best practice for anything other than the explicit example component.";
	private static final String HEADING = "Warning";

	/**
	 * Construct panel.
	 */
	public AccessibilityWarningContainer() {
		add(new WHeading(HeadingLevel.H2, HEADING));
		add(new ExplanatoryText(WARNING));
	}
}
