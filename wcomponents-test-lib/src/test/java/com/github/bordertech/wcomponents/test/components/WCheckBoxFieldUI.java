package com.github.bordertech.wcomponents.test.components;

import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import static com.github.bordertech.wcomponents.WFieldLayout.LAYOUT_FLAT;

public class WCheckBoxFieldUI extends WApplication {

	private WMessages messages = new WMessages();
	private WPanel panel = new WPanel();
	private WCheckBox wCheckBox = new WCheckBox();

	public WCheckBoxFieldUI() {

		panel.add(messages);
		WFieldLayout fieldLayout = new WFieldLayout(LAYOUT_FLAT);
		fieldLayout.setTitle("this is a bunch of fields");
		fieldLayout.addField("this is a checkbox", wCheckBox);

		panel.add(fieldLayout);
		add(panel);
	}

	public WCheckBox getwCheckBox() {
		return wCheckBox;
	}
}
