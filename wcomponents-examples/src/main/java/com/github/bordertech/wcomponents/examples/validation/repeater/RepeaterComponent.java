package com.github.bordertech.wcomponents.examples.validation.repeater;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validator.RegExFieldValidator;

/**
 * This component shows an example of validation within a {@link com.github.bordertech.wcomponents.WRepeater}.
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class RepeaterComponent extends WDataRenderer {

	/**
	 * Displays the SomeDataBean's {@link SomeDataBean#getField1() field1} attribute.
	 */
	private final WTextField field1Text;
	/**
	 * Displays the SomeDataBean's {@link SomeDataBean#getField2() field2} attribute.
	 */
	private final WTextField field2Text;
	/**
	 * The {@link WFieldSet} used as the basis of the repeated components display.
	 */
	private final WFieldSet fieldSet;

	/**
	 * Creates a RepeaterComponent.
	 */
	public RepeaterComponent() {
		fieldSet = new WFieldSet("Repeated Fields");
		add(fieldSet);
		fieldSet.setMargin(new Margin(0, 0, 12, 0));
		WFieldLayout fields = new WFieldLayout();
		fieldSet.add(fields);

		field1Text = new WTextField();
		field1Text.setMinLength(2);
		field1Text.setMaxLength(5);

		WField field1 = fields.addField("Field 1", field1Text);
		field1Text.setMandatory(true);
		field1.getLabel().setHint("required");

		field2Text = new WTextField();
		WField field2 = fields.addField("Field 2", field2Text);
		field2Text.setMandatory(true);
		field2.getLabel().setHint("required and must only contain alphabetic characters");
		field2.addValidator(new RegExFieldValidator("^[a-zA-Z]*$",
				"{0} must only contain alphabetic characters."));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateComponent(final Object data) {
		SomeDataBean bean = (SomeDataBean) data;
		field1Text.setText(bean.getField1());
		field2Text.setText(bean.getField2());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateData(final Object data) {
		SomeDataBean bean = (SomeDataBean) data;
		bean.setField1(field1Text.getText());
		bean.setField2(field2Text.getText());
	}
}
