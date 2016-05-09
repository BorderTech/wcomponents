package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.validation.ValidationContainer;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Mandatory;
import com.github.bordertech.wcomponents.subordinate.Optional;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * Demonstrate using the Mandatory and Optional actions on the Subordinate Control.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SubordinateControlMandatoryExample extends ValidationContainer {

	/**
	 * Creates a SubordinateControlMandatoryExample.
	 */
	public SubordinateControlMandatoryExample() {
		super(build());
	}

	/**
	 * Creates the component to be added to the validation container. This is doen in a static method because the
	 * component is passed into the superclass constructor.
	 *
	 * @return the component to be added to the validation container.
	 */
	private static WComponent build() {
		WContainer root = new WContainer();

		WSubordinateControl control = new WSubordinateControl();
		root.add(control);

		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		layout.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
		WCheckBox checkBox = new WCheckBox();
		layout.addField("Set Mandatory", checkBox);

		WTextField text = new WTextField();
		layout.addField("Might need this field", text);

		WTextField mandatoryField = new WTextField();
		layout.addField("Another field always mandatory", mandatoryField);
		mandatoryField.setMandatory(true);


		final WRadioButtonSelect rbSelect = new WRadioButtonSelect("australian_state");
		layout.addField("Select a state", rbSelect);

		root.add(layout);

		Rule rule = new Rule();
		rule.setCondition(new Equal(checkBox, Boolean.TRUE.toString()));
		rule.addActionOnTrue(new Mandatory(text));
		rule.addActionOnFalse(new Optional(text));
		rule.addActionOnTrue(new Mandatory(rbSelect));
		rule.addActionOnFalse(new Optional(rbSelect));
		control.addRule(rule);

		return root;
	}
}
