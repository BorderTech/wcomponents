package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMultiDropdown;
import java.util.Arrays;

/**
 * Examples use of the {@link WMultiDropdown} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMultiDropdownExample extends WContainer {

	/**
	 * Simple data used by the example.
	 */
	private static final String[] DATA = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};

	/**
	 * The field layout used to display the example dropdowns.
	 */
	private final WFieldLayout layout = new WFieldLayout();

	/**
	 * Creates a WMultiDropdownExample.
	 */
	public WMultiDropdownExample() {
		add(new WHeading(WHeading.SECTION, "Dynamic Multi-dropdown examples"));
		add(layout);

		WMultiDropdown dropdown = new WMultiDropdown("icao");
		layout.addField("Dynamic multi-dropdown 1", dropdown);

		dropdown = new WMultiDropdown(DATA);
		dropdown.setMaxSelect(5);
		layout.addField("Dynamic multi-dropdown 2", dropdown);

		dropdown = new WMultiDropdown(DATA);
		dropdown.setDisabled(true);
		layout.addField("Dynamic multi-dropdown 3", dropdown);

		dropdown = new WMultiDropdown(DATA);
		dropdown.setSelected(Arrays.asList(new String[]{DATA[0]}));
		layout.addField("Dynamic multi-dropdown 4", dropdown);

		dropdown = new WMultiDropdown(DATA);
		dropdown.setSelected(Arrays.asList(new String[]{DATA[0], DATA[1], DATA[2]}));
		layout.addField("Dynamic multi-dropdown 5", dropdown);

		dropdown = new WMultiDropdown(DATA);
		dropdown.setSelected(Arrays.
				asList(new String[]{DATA[0], DATA[1], DATA[2], DATA[3], DATA[4]}));
		dropdown.setDisabled(true);
		dropdown.setMaxSelect(5);
		layout.addField("Dynamic multi-dropdown 6", dropdown);

		WButton refresh = new WButton("Refresh");
		add(refresh);
	}

}
