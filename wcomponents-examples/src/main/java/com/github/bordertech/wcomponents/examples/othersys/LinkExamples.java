package com.github.bordertech.wcomponents.examples.othersys;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WLink;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.layout.ListLayout;

/**
 * Examples of the various link components.
 *
 * @author Martin Shevchenko
 */
public class LinkExamples extends WContainer {

	/**
	 * A sample URL to use with the builder.
	 */
	private static final String URL = "http://www.ubuntu.com/";

	/**
	 * Creates a LinKExamples.
	 */
	public LinkExamples() {

		WMenu menuBar = new WMenu();

		// File Menu
		WSubMenu fileMenu = new WSubMenu("Run", 'R');

		addLink(fileMenu, new WLink.Builder("WLink using builder and with attrs",
				URL).width(200).height(500).scrollbars(true).build());

		menuBar.add(fileMenu);

		add(menuBar);

		WPanel linkPanel = new WPanel();
		add(linkPanel);
		linkPanel.setLayout(new ListLayout(ListLayout.Type.STACKED, ListLayout.Alignment.LEFT,
				ListLayout.Separator.NONE,
				false));

		linkPanel.add(new WLink.Builder("WLink using builder and with attrs", URL)
				.width(200).height(200).scrollbars(true).build());
		linkPanel.add(new WLink.Builder("WLink using builder and with attrs duplicate", URL)
				.width(200).height(200).scrollbars(true).build());
		linkPanel.add(new WLink.Builder("WLink with builder with no attrs", URL).build());
		linkPanel.add(new WLink.Builder("WLink with builder with window name", URL)
				.windowName("wcwindow").build());
	}

	/**
	 * Adds a WLink to a sub-menu.
	 *
	 * @param subMenu the sub-menu to add the link to
	 * @param link the link to add.
	 */
	private void addLink(final WSubMenu subMenu, final WLink link) {
		subMenu.add(new WMenuItem(new WDecoratedLabel(link)));
	}

}
