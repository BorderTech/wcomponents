package com.github.bordertech.wcomponents.examples.validation.repeater;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.FlowLayout;

/**
 * Repeated fields.
 *
 * @author Adam Millard
 */
public class RepeaterFields extends WDataRenderer {

	private final WTextField nameText;
	private final WRepeater repeater;
	private final WButton submitBtn;

	/**
	 * Creates a RepeaterFields.
	 */
	public RepeaterFields() {
		WFieldLayout fields = new WFieldLayout();
		fields.setMargin(new Margin(0, 0, 12, 0));
		add(fields);

		nameText = new WTextField();
		WField nameField = fields.addField("Name", nameText);
		nameText.setMandatory(true);
		nameField.getLabel().setHint("required");

		repeater = new WRepeater();
		repeater.setRepeatedComponent(new RepeaterComponent());
		add(repeater);
		WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
		buttonPanel.setMargin(new Margin(12, 0, 0, 0));
		add(buttonPanel);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		submitBtn = new WButton("Submit", 'S');
		buttonPanel.add(submitBtn);
	}

	/**
	 * @param action the submit action
	 */
	public void setSubmitAction(final Action action) {
		submitBtn.setAction(action);
	}

	@Override
	public void updateComponent(final Object data) {
		MyDataBean myBean = (MyDataBean) data;
		nameText.setText(myBean.getName());
		repeater.setData(myBean.getMyBeans());
	}

	@Override
	public void updateData(final Object data) {
		MyDataBean myBean = (MyDataBean) data;
		myBean.setName(nameText.getText());
		myBean.setMyBeans(repeater.getBeanList());
	}
}
