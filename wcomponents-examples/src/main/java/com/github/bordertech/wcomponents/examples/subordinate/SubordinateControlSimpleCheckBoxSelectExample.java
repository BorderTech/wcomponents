package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * <p>
 * This example demonstrates showing/hiding an extra field depending on the selection in a {@link WCheckBoxSelect}.
 * </p>
 *
 * <p>
 * In this example a {@link Rule} is created that evaluates the state of check box c.
 * </p>
 * <ul>
 * <li>if the rule evaluates to true then the {@link Show} action is invoked on the "extra" text field,</li>
 * <li>if the rule evaluates to false then the {@link Hide} action is invoked on the "extra" text field instead.</li>
 * </ul>
 *
 * @author Martin Shevchenko
 */
public class SubordinateControlSimpleCheckBoxSelectExample extends WContainer {

	/**
	 * option a.
	 */
	private static final String OPTION_A = "a";
	/**
	 * option b.
	 */
	private static final String OPTION_B = "b";
	/**
	 * option c.
	 */
	private static final String OPTION_C = "c";

	/**
	 * Creates a SubordinateControlSimpleCheckBoxSelectExample.
	 */
	public SubordinateControlSimpleCheckBoxSelectExample() {

		WCheckBoxSelect groupSelect = new WCheckBoxSelect();
		groupSelect.setOptions(new String[]{OPTION_A, OPTION_B, OPTION_C});
		groupSelect.setFrameless(true);
		groupSelect.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);

		WTextField extraField = new WTextField();

		WFieldLayout flay = new WFieldLayout();
		add(flay);
		flay.setLabelWidth(25);
		flay.setMargin(new Margin(0, 0, 12, 0));
		flay.addField("Select an option", groupSelect);
		WField xtraField = flay.addField("Extra information", extraField);

		WSubordinateControl control = new WSubordinateControl();
		add(control);

		Rule rule = new Rule();
		rule.setCondition(new Equal(groupSelect, OPTION_C));
		rule.addActionOnTrue(new Show(xtraField));
		rule.addActionOnFalse(new Hide(xtraField));
		control.addRule(rule);
	}
}
