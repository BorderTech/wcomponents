package com.github.bordertech.wcomponents.examples.validation;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.examples.common.ClientValidationTemplate;
import com.github.bordertech.wcomponents.examples.validation.basic.BasicFieldLayoutValidationExample;
import com.github.bordertech.wcomponents.examples.validation.basic.BasicFieldsValidationExample;
import com.github.bordertech.wcomponents.examples.validation.fields.FieldValidation;
import com.github.bordertech.wcomponents.examples.validation.repeater.RepeaterExample;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;

/**
 * This test component is used to provide the different ways of performing validation with WComponents.
 *
 * @author Adam Millard
 */
public class ValidationExamples extends WContainer {

	/**
	 * Toggle whether to use client validation.
	 */
	private final WCheckBox useClientValidation = new WCheckBox();

	/**
	 * Template to include client validation.
	 */
	private final ClientValidationTemplate jsPlainTextTemplate = new ClientValidationTemplate();

	private final WButton btnApplySettings =  new WButton("Apply");

	/**
	 * Creates a ValidationExamples.
	 */
	public ValidationExamples() {

		btnApplySettings.setAction(new ValidatingAction(new WValidationErrors(), btnApplySettings) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				jsPlainTextTemplate.setVisible(useClientValidation.isSelected());
			}
		});

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		layout.addField("Use client side validation? ", useClientValidation);
		layout.addField((WLabel) null, btnApplySettings);
		layout.setMargin(new Margin(0, 0, 12, 0));

		WTabSet tabs = new WTabSet();
		tabs.addTab(new BasicFieldsValidationExample(), "Basic", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new BasicFieldLayoutValidationExample(), "Basic - using WFieldLayout",
				WTabSet.TAB_MODE_LAZY);
		tabs.addTab(new RepeaterExample(), "Repeater", WTabSet.TAB_MODE_LAZY);
		tabs.addTab(new FieldValidation(), "All Fields", WTabSet.TAB_MODE_LAZY);

		add(tabs);
		add(jsPlainTextTemplate);
	}
}
