package com.github.bordertech.wcomponents.test.components;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;

public class WTabSetUI extends WPanel {

	private WPanel panel = new WPanel();

	public WTabSetUI() {
		WTabSet tabSet = new WTabSet();
		tabSet.addTab(createDummyPanelWithText("One"), "First Tab");
		tabSet.addTab(createDummyPanelWithText("Two"), "Second Tab");
		panel.add(tabSet);
		add(panel);
	}

	private WComponent createDummyPanelWithText(String name) {
		return new WText("Tab: " + name);
	}

}
