package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * This component shows the use of the combination of {@link WTabSet} and {@link WCollapsible}.
 *
 * @author Adam Millard
 */
public class WTabAndCollapsibleExample extends WPanel {

	/**
	 * The tabset used in the example.
	 */
	private final WTabSet tabset;

	/**
	 * Creates a WTabAndCollapsibleExample.
	 */
	public WTabAndCollapsibleExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		WContainer tab0 = new WContainer();
		tab0.add(new WCollapsible(new WText("The first collapsed content."),
				"Client Collapsed Content 1"));
		tab0.add(newVisibilityToggleForTab(1));

		// TODO: This is bad - use a layout instead
		WText lineBreak = new WText("<br />");
		lineBreak.setEncodeText(false);
		tab0.add(lineBreak);

		tab0.add(newVisibilityToggleForTab(3));

		WComponent tab1 = new WCollapsible(new WText("The second collapsed content."),
				"Client Collapsed Content 2");
		WComponent tab2 = new WText("Three");
		WComponent tab3 = new WText("Four");

		tabset = new WTabSet();
		tabset.addTab(tab0, "First tab", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(tab1, "Second tab", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(tab2, "Third tab", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(tab3, "Fourth tab", WTabSet.TAB_MODE_SERVER);

		// Default the last tab to invisible.
		tabset.setTabVisible(tab3, false);

		add(tabset);
	}

	/**
	 * Creates a button to toggle the visibility of a tab.
	 *
	 * @param tabIndex the index of the tab.
	 * @return a button which toggles the visibility of the tab.
	 */
	private WButton newVisibilityToggleForTab(final int tabIndex) {
		WButton toggleButton = new WButton("Toggle visibility of tab " + (tabIndex + 1));

		toggleButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				boolean tabVisible = tabset.isTabVisible(tabIndex);
				tabset.setTabVisible(tabIndex, !tabVisible);
			}
		});

		return toggleButton;
	}
}
