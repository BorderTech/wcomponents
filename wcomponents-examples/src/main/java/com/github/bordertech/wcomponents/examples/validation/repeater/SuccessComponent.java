package com.github.bordertech.wcomponents.examples.validation.repeater;

import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WTextArea;

/**
 * This component is displayed by the example when field validation passes.
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class SuccessComponent extends WDataRenderer {

	/**
	 * Displays a simple text view of the data bean's details.
	 */
	private final WTextArea beanDetails;

	/**
	 * Creates a SuccessComponent.
	 */
	public SuccessComponent() {
		add(new WMessageBox(WMessageBox.SUCCESS, "All is valid!"));

		beanDetails = new WTextArea();
		beanDetails.setRows(15);
		beanDetails.setColumns(60);
		beanDetails.setReadOnly(true);
		add(beanDetails);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateComponent(final Object data) {
		MyDataBean myBean = (MyDataBean) data;
		StringBuffer out = new StringBuffer();
		out.append("Name: ").append(myBean.getName()).append("\n\n");

		for (SomeDataBean bean : myBean.getMyBeans()) {
			out.append("Field1: ").append(bean.getField1());
			out.append("\nField2: ").append(bean.getField2()).append("\n\n");
		}

		beanDetails.setText(out.toString());
	}
}
