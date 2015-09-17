package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.TextDuplicator;
import com.github.bordertech.wcomponents.examples.menu.ColumnMenuExample;
import com.github.bordertech.wcomponents.examples.menu.MenuBarExample;
import com.github.bordertech.wcomponents.examples.menu.TreeMenuExample;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * A demonstration of various components.
 *
 * @author Martin Shevcheno
 */
public class NestedTabSetExample extends WPanel {

	/**
	 * Creates a NestedTabSetExample.
	 */
	public NestedTabSetExample() {
		WTabSet tests = new WTabSet();
		this.setLayout(new FlowLayout(Alignment.VERTICAL));
		add(tests);
		tests.addTab(new ClientSideTabTests(), "Client Side", WTabSet.TAB_MODE_SERVER);
		tests.addTab(new WLabel("Another tab"), "Another", WTabSet.TAB_MODE_SERVER);
		tests.addTab(new ColumnMenuExample(), "Column Menu", TabMode.DYNAMIC);
		tests.addTab(new MenuBarExample(), "Menu Bar", TabMode.DYNAMIC);
		tests.addTab(new TreeMenuExample(), "Tree Menu", TabMode.DYNAMIC);
	}

	/**
	 * A demonstration of client-side tabset functionality.
	 *
	 * @author Martin Shevchenko
	 */
	static class ClientSideTabTests extends WPanel {

		/**
		 * Creates a ClientSideTabTests example.
		 */
		ClientSideTabTests() {
			this.setLayout(new FlowLayout(Alignment.VERTICAL));

			add(new WText("Tabs on top:"));
			WTabSet tabTop = new WTabSet();

			tabTop.addTab(new WText("CS Page One..."), "One", WTabSet.TAB_MODE_CLIENT);
			tabTop.addTab(new WText("CS Page Two..."), "Two", WTabSet.TAB_MODE_CLIENT);
			tabTop.addTab(new TextDuplicator("Dup"), "Duplicator", WTabSet.TAB_MODE_CLIENT);
			tabTop.addTab(new WText("CS Page Three..."), "Three", WTabSet.TAB_MODE_CLIENT);
			add(tabTop);

			add(new WText("Tabs at bottom:"));
			WTabSet tabBottom = new WTabSet(WTabSet.TabSetType.LEFT);
			tabBottom.addTab(new WText("One..."), "One", WTabSet.TAB_MODE_CLIENT);
			tabBottom.addTab(new WText("Two..."), "Two", WTabSet.TAB_MODE_CLIENT);
			add(tabBottom);
		}
	}
}
