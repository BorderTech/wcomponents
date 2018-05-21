package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WMultiTextField;
import com.github.bordertech.wcomponents.validation.ValidatingAction;

/**
 * Examples use of the {@link WMultiTextField} component.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public final class WMultiTextFieldExample extends WContainer {

	/**
	 * Creates a WMultiTextFieldExample.
	 */
	public WMultiTextFieldExample() {
		final WMessages messages = new WMessages();
		add(messages);

		int i = 0;
		WFieldLayout layout = new WFieldLayout();

		add(new WHeading(HeadingLevel.H2,
				1 + ". Dynamic Multi-Inputs With No Data Defined"));

		WMultiTextField inputs1 = new WMultiTextField();
		inputs1.setToolTip("My tooltip for #1");
		layout.addField("Dynamic mult-input " + ++i, inputs1);

		//Size 30 and maxlength 30
		WMultiTextField inputs2 = new WMultiTextField();
		inputs2.setColumns(30);
		inputs2.setMaxLength(30);
		layout.addField("Dynamic mult-input " + ++i, inputs2);

		//Size 30, maxlength 30 and maximum inputs 5
		WMultiTextField inputs3 = new WMultiTextField();
		inputs3.setColumns(30);
		inputs3.setMaxLength(30);
		inputs3.setMaxInputs(5);
		inputs3.setPlaceholder("Maximum of 5 inputs");

		layout.addField("Dynamic mult-input " + ++i, inputs3);

		//Size 30, maxlength 30 and disabled
		WMultiTextField inputs4 = new WMultiTextField();
		inputs4.setColumns(30);
		inputs4.setMaxLength(30);
		inputs4.setDisabled(true);
		layout.addField("Dynamic mult-input " + ++i, inputs4);
		//Readonly - no data
		WMultiTextField inputs4a = new WMultiTextField();
		inputs4a.setReadOnly(true);
		layout.addField("Dynamic mult-input " + ++i, inputs4a);

		WMultiTextField mtfMandatory = new WMultiTextField();
		mtfMandatory.setMandatory(true);
		layout.addField("Mandatory", mtfMandatory);


		WButton refresh = new WButton("Check Mandatory");
		refresh.setAction(new ValidatingAction(messages.getValidationErrors(), layout) {
			@Override
			public void executeOnValid(ActionEvent event) {
				messages.reset();
			}
		});
		layout.addField(refresh);
		add(layout);

		add(new WHeading(HeadingLevel.H2,
				"2. Dynamic Multi-Inputs With Data Defined"));

		WFieldLayout layout2 = new WFieldLayout();
		//One value
		WMultiTextField inputs5 = new WMultiTextField(new String[]{"a"});
		layout2.addField("Dynamic mult-input " + ++i, inputs5);
		//Readonly - with data
		WMultiTextField inputs5a = new WMultiTextField(new String[]{"a"});
		inputs5a.setReadOnly(true);
		layout2.addField("Dynamic mult-input " + ++i, inputs5a);

		//Three values, size 40 and maxlength 40
		WMultiTextField inputs6 = new WMultiTextField(new String[]{"a", "b", "c"});
		inputs6.setColumns(40);
		inputs6.setMaxLength(40);
		layout2.addField("Dynamic mult-input " + ++i, inputs6);

		//Five default values, size 50, maxlength 40, disabled and maximum inputs 5
		WMultiTextField inputs7 = new WMultiTextField(new String[]{"a", "b", "c", "d", "e"});
		inputs7.setColumns(50);
		inputs7.setMaxLength(40);
		inputs7.setDisabled(true);
		layout2.addField("Dynamic mult-input " + ++i, inputs7);

		add(layout2);

		refresh = new WButton("Refresh");
		add(refresh);
	}
}
