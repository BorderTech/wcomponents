package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WColumnLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;

/**
 * <p>
 * Demonstration of the {@link WColumnLayout}.
 * </p>
 * <p>
 * The {@link WColumnLayout} is a simple wrapper for two WColumns into a WRow.
 * </p>
 *
 * @author Steve Harney
 * @since 1.0.0
 */
public class WColumnLayoutExample extends WPanel {

	/**
	 * Some long text to display in the example.
	 */
	private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent lectus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Phasellus et turpis. Aenean convallis eleifend elit. Donec venenatis justo id nunc. Sed at purus vel quam mattis elementum. Sed ultrices lobortis orci. Pellentesque enim urna, volutpat at, sagittis id, faucibus sed, lectus. Integer dapibus nulla semper mi. Nunc posuere molestie augue. Aliquam varius libero in tortor. Sed nibh. Nunc erat nunc, pellentesque at, sodales vel, dapibus sit amet, tortor.";

	/**
	 * constructor.
	 */
	public WColumnLayoutExample() {
		super(Type.BLOCK);

		add(new WHeading(WHeading.MAJOR, "Major Heading"));

		WColumnLayout colLayout = new WColumnLayout("Section Heading - WColumnLayout");
		colLayout.setLeftColumn("Minor Heading", new WText(LONG_TEXT));
		colLayout.setRightColumn("Minor Heading", new WText(LONG_TEXT));
		add(colLayout);

	}
}
