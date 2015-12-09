package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExampleLookupTable.TableWithNullOption;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * <p>
 * An example showing using a {@link WSubordinateControl} to hide/show a field depending on the selected value in a
 * {@link WDropdown}.</p>
 *
 * <p>
 * Three rules have been created:
 * </p>
 * <ol>
 * <li>Hide the "extra" field when no Gender is selected</li>
 * <li>Show the "extra" field when "M" is selected</li>
 * <li>Hide the "extra" field when "F" is selected</li>
 * </ol>
 *
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlCrtWDropdownExample extends WContainer {

	/**
	 * Creates a SubordinateControlCrtWDropdownExample.
	 */
	public SubordinateControlCrtWDropdownExample() {
		WFieldLayout layout = new WFieldLayout();
		add(layout);
		layout.setLabelWidth(25);
		layout.setMargin(new Margin(0, 0, 12, 0));

		WDropdown genderDropdown = new WDropdown(new TableWithNullOption("sex"));

		WField field = layout.addField("Gender", genderDropdown);
		field.getLabel().setHint("The 'Male' option requires more information");

		WTextField extraField = new WTextField();
		WField xtraWField = layout.addField("Extra information", extraField);

		WSubordinateControl control = new WSubordinateControl();
		add(control);

		Rule rule = new Rule();
		rule.setCondition(new Equal(genderDropdown, "M"));
		rule.addActionOnTrue(new Show(xtraWField));
		rule.addActionOnFalse(new Hide(xtraWField));
		control.addRule(rule);
	}
}
