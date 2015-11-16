package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMultiTextField;

/**
 * Examples use of the {@link WMultiTextField} component.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WMultiTextFieldExample extends WContainer {

	/**
	 * Creates a WMultiTextFieldExample.
	 */
	public WMultiTextFieldExample() {
		int i = 0;
		WFieldLayout layout = new WFieldLayout();

		add(new WHeading(WHeading.SECTION,
				1 + ". Dynamic Multi-Inputs With No Data Defined"));

		WMultiTextField inputs1 = new WMultiTextField();
		addField(layout, inputs1, "Dynamic mult-input " + ++i);

		//Size 30 and maxlength 30
		WMultiTextField inputs2 = new WMultiTextField();
		inputs2.setColumns(30);
		inputs2.setMaxLength(30);
		addField(layout, inputs2, "Dynamic mult-input " + ++i);

		//Size 30, maxlength 30 and maximum inputs 5
		WMultiTextField inputs3 = new WMultiTextField();
		inputs3.setColumns(30);
		inputs3.setMaxLength(30);
		inputs3.setMaxInputs(5);

		addField(layout, inputs3, "Dynamic mult-input " + ++i);

		//Size 30, maxlength 30 and disabled
		WMultiTextField inputs4 = new WMultiTextField();
		inputs4.setColumns(30);
		inputs4.setMaxLength(30);
		inputs4.setDisabled(true);
		addField(layout, inputs4, "Dynamic mult-input " + ++i);
		//Readonly - no data
		WMultiTextField inputs4a = new WMultiTextField();
		inputs4a.setReadOnly(true);
		addField(layout, inputs4a, "Dynamic mult-input " + ++i);

		add(layout);

		add(new WHeading(WHeading.SECTION,
				"2. Dynamic Multi-Inputs With Data Defined"));

		WFieldLayout layout2 = new WFieldLayout();
		//One value
		WMultiTextField inputs5 = new WMultiTextField(new String[]{"a"});
		addField(layout2, inputs5, "Dynamic mult-input " + ++i);
		//Readonly - with data
		WMultiTextField inputs5a = new WMultiTextField(new String[]{"a"});
		inputs5a.setReadOnly(true);
		addField(layout2, inputs5a, "Dynamic mult-input " + ++i);

		//Three values, size 40 and maxlength 40
		WMultiTextField inputs6 = new WMultiTextField(new String[]{"a", "b", "c"});
		inputs6.setColumns(40);
		inputs6.setMaxLength(40);
		addField(layout2, inputs6, "Dynamic mult-input " + ++i);

		//Five default values, size 50, maxlength 40, disabled and maximum inputs 5
		WMultiTextField inputs7 = new WMultiTextField(new String[]{"a", "b", "c", "d", "e"});
		inputs7.setColumns(50);
		inputs7.setMaxLength(40);
		inputs7.setDisabled(true);
		addField(layout2, inputs7, "Dynamic mult-input " + ++i);

		add(layout2);

		WButton refresh = new WButton("Refresh");
		add(refresh);
	}

	/**
	 * Adds a field to the given layout.
	 *
	 * @param layout the layout to add the field to.
	 * @param input the input field to add.
	 * @param labelText the label text for the field.
	 */
	private void addField(final WFieldLayout layout, final WComponent input, final String labelText) {
		WLabel label = new WLabel(labelText, input);

		layout.addField(label, input);
	}
}
