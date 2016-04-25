package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * This example demonstrates showing/hiding an extra field depending on the selection in a {@link WDropdown}.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlSimpleWDropdownExample extends WContainer {

	/**
	 * The 'yes' option for whether extra info is required.
	 */
	private static final String YES_OPTION = "yes";

	/**
	 * The 'no' option for whether extra info is required.
	 */
	private static final String NO_OPTION = "no";

	/**
	 * Creates a SubordinateControlSimpleWDropdownExample.
	 */
	public SubordinateControlSimpleWDropdownExample() {
		WDropdown extraInfoRequired = new WDropdown(new String[]{null, YES_OPTION, NO_OPTION});
		WTextField extraField = new WTextField();

		WFieldLayout flay = new WFieldLayout();
		flay.setLabelWidth(25);
		flay.setMargin(new Margin(0, 0, 12, 0));
		add(flay);
		flay.addField("Extra information required?", extraInfoRequired);
		WField extraInfoField = flay.addField("Extra information", extraField);

		WSubordinateControl control = new WSubordinateControl();
		add(control);

		Rule rule = new Rule();
		rule.setCondition(new Equal(extraInfoRequired, YES_OPTION));
		rule.addActionOnTrue(new Show(extraInfoField));
		rule.addActionOnFalse(new Hide(extraInfoField));
		control.addRule(rule);

	}
}
