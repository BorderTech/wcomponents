package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * This example demonstrates showing/hiding an extra field depending on the selection in a {@link WMultiSelect}.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlSimpleWMultiSelectExample extends WContainer {

	private static final String OPTION_A = "a";
	private static final String OPTION_B = "bc";
	private static final String OPTION_C = "c";

	/**
	 * Creates a SubordinateControlSimpleWMultiSelectExample.
	 */
	public SubordinateControlSimpleWMultiSelectExample() {

		WMultiSelect groupSelect = getGroupSelect();
		groupSelect.setOptions(new String[]{OPTION_A, OPTION_B, OPTION_C});

		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		layout.setMargin(new Margin(0, 0, 12, 0));
		add(layout);
		layout.addField("Select one or more options", groupSelect).getLabel().setHint(
				"Option c requires further information");
		WField extraField = layout.addField("Extra information", new WTextField());
		WSubordinateControl control = new WSubordinateControl();
		add(control);

		Rule rule = new Rule();
		rule.setCondition(new Equal(groupSelect, OPTION_C));
		rule.addActionOnTrue(new Show(extraField));
		rule.addActionOnFalse(new Hide(extraField));
		control.addRule(rule);
	}

	/**
	 * @return the list box used in the example.
	 */
	public WMultiSelect getGroupSelect() {
		return new WMultiSelect();
	}
}
