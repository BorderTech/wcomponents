package com.github.bordertech.wcomponents.examples.repeater.link;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;

/**
 * A "detail" display for a {@link MyData} bean.
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class DisplayComponent extends WDataRenderer {

	/**
	 * Displays the {@link MyData} bean's name.
	 */
	private final WText name = new WText();

	/**
	 * Displays the {@link MyData} bean's count.
	 */
	private final WText count = new WText();

	/**
	 * Creates a DisplayComponent.
	 */
	public DisplayComponent() {
		add(name);
		add(count);

		// Add a "Close" button, to return to the previous view.
		WPanel buttonPanel = new WPanel(WPanel.Type.ACTION);
		WButton closeBtn = new WButton("Close", 'C');
		buttonPanel.add(closeBtn);
		add(buttonPanel);
	}

	/**
	 * Copies data from the Model into the View.
	 *
	 * @param data the MyData bean to set on the component.
	 */
	@Override
	public void updateComponent(final Object data) {
		MyData myData = (MyData) data;
		name.setText("<B>" + myData.getName() + "</B>");
		count.setText(String.valueOf(myData.getCount()));
	}
}
