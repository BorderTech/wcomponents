package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WLink;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * A simple example of SubordinateControl being used to disable fields.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlSimpleDisableExample extends WPanel {

	/**
	 * Creates a SubordinateControlSimpleDisableExample.
	 */
	public SubordinateControlSimpleDisableExample() {
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		layout.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
		add(layout);

		// The toggle used to enable/disable the other fields.
		WCheckBox toggle = new WCheckBox();
		layout.addField("Toggle disabled", toggle);

		// Various input fields that will be enabled/disabled.
		WTextField textField = new WTextField();
		layout.addField("Text field", textField);
		WDropdown dropdownField = new WDropdown(new String[]{"a", "b", "c"});
		layout.addField("Drop down", dropdownField);
		WCheckBox checkboxField = new WCheckBox();
		layout.addField("Checkbox", checkboxField);
		WRadioButtonSelect rbSelect = new WRadioButtonSelect(new String[]{"a", "b", "c"});
		rbSelect.setFrameless(true);
		rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		layout.addField("Select one", rbSelect);
		WCheckBoxSelect cbSelect = new WCheckBoxSelect(new String[]{"a", "b", "c"});
		cbSelect.setFrameless(true);
		rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		layout.addField("Select any", cbSelect);
		WTextArea textArea = new WTextArea();
		textArea.setRows(3);
		layout.addField("Text Area", textArea).setInputWidth(100);
		WButton button = new WButton("Button");
		layout.addField((WLabel) null, button);
		WLink link = new WLink();
		link.setText("Link");
		link.setRenderAsButton(true);
		layout.addField((WLabel) null, link);

		// The subordnate control that will perform client side enabling/disabling.
		WSubordinateControl control = new WSubordinateControl();
		add(control);

		Rule rule = new Rule();
		rule.setCondition(new Equal(toggle, Boolean.TRUE.toString()));

		rule.addActionOnTrue(new Disable(textField));
		rule.addActionOnTrue(new Disable(dropdownField));
		rule.addActionOnTrue(new Disable(checkboxField));
		rule.addActionOnTrue(new Disable(rbSelect));
		rule.addActionOnTrue(new Disable(cbSelect));
		rule.addActionOnTrue(new Disable(textArea));
		rule.addActionOnTrue(new Disable(button));
		rule.addActionOnTrue(new Disable(link));

		rule.addActionOnFalse(new Enable(textField));
		rule.addActionOnFalse(new Enable(dropdownField));
		rule.addActionOnFalse(new Enable(checkboxField));
		rule.addActionOnFalse(new Enable(rbSelect));
		rule.addActionOnFalse(new Enable(cbSelect));
		rule.addActionOnFalse(new Enable(textArea));
		rule.addActionOnFalse(new Enable(button));
		rule.addActionOnFalse(new Enable(link));
		control.addRule(rule);
	}
}
