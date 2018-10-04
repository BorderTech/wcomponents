package com.github.bordertech.wcomponents.test.components;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;

public class WTabSetUI extends WPanel {

	private WPanel panel = new WPanel();
	private WTabSet tabSet = new WTabSet();
	private WComponent tabContent1;
	private WComponent tabContent2;

	public WTabSetUI() {
		tabContent1 = createDummyPanelWithText("First Tab");
		tabContent2 = createDummyPanelWithText("Second Tab");
		tabSet.addTab(tabContent1, "First Tab");
		tabSet.addTab(tabContent2, "Second Tab");
		panel.add(tabSet);
		add(panel);
	}

	private WComponent createDummyPanelWithText(String name) {
		return new WText("Tab: " + name);
	}

	public WTabSet getTabSet() {
		return tabSet;
	}

	public WComponent getTabContent1() {
		return tabContent1;
	}

	public WComponent getTabContent2() {
		return tabContent2;
	}
}
