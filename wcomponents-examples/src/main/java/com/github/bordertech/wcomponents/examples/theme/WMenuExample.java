package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.examples.menu.ColumnMenuExample;
import com.github.bordertech.wcomponents.examples.menu.MenuBarExample;
import com.github.bordertech.wcomponents.examples.menu.TreeMenuExample;

/**
 * This component demonstrates the usage of the {@link WMenu} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuExample extends WContainer {

	/**
	 * Creates a WMenu example.
	 */
	public WMenuExample() {
		add(new WHeading(HeadingLevel.H2, "Menu bar"));
		add(new MenuBarExample());

		add(new WHeading(HeadingLevel.H2, "Tree menu"));
		add(new TreeMenuExample());

		add(new WHeading(HeadingLevel.H2, "Column  menu"));
		add(new ColumnMenuExample());
	}
}
