package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;

/**
 * A simple example of {@link SubordinateBuilder} usage.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SubordinateBuilderSimpleExample extends WContainer {

	/**
	 * Creates a SubordinateBuilderSimpleExample.
	 */
	public SubordinateBuilderSimpleExample() {
		// Set up the form controls
		WFieldLayout layout = new WFieldLayout();
		add(layout);
		layout.setLabelWidth(25);

		WCheckBox extraInfoRequired = new WCheckBox();
		layout.addField("Extra information required", extraInfoRequired);

		WTextField extraField = new WTextField();
		layout.addField("Extra information", extraField);

		// Build & add the subordinate
		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(extraInfoRequired, String.valueOf(true));
		builder.whenTrue().show(extraField);
		builder.whenFalse().hide(extraField);
		add(builder.build());

		// Add a tooltip which describes the rule
		extraInfoRequired.setToolTip(builder.toString());
	}
}
