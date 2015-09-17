package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMultiDropdown;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.examples.common.ExampleLookupTable.TableWithNullOption;
import java.util.Arrays;

/**
 * Demonstrate components that use cached lists.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDataListServletExample extends WContainer {

	/**
	 * The field layout used to display the example components.
	 */
	private final WFieldLayout layout = new WFieldLayout();

	/**
	 * Table with list of countries.
	 */
	private static final String TABLE_ICAO = "icao";

	/**
	 * Table with no data.
	 */
	private static final String TABLE_NODATA = "nodata";

	/**
	 * Dropdown with a selected option.
	 */
	private final WDropdown dropdownSelected;
	/**
	 * MultiDropdwon with a selected option.
	 */
	private final WMultiDropdown multiDropdownSelected;
	/**
	 * ListBox with a selected option.
	 */
	private final WMultiSelect boxSelected;

	/**
	 * Construct the example.
	 */
	public WDataListServletExample() {
		add(new WHeading(WHeading.SECTION,
				"Example of components using cached lists (WDataListServlet)"));

		add(layout);

		// Dropdown
		WDropdown dropdown = new WDropdown(TABLE_ICAO);
		addField(dropdown, "Dropdown 1", "CRT Data.");

		dropdown = new WDropdown(new TableWithNullOption(TABLE_ICAO));
		addField(dropdown, "Dropdown 2", "CRT Data. With a null option");

		dropdown = new WDropdown(new TableWithNullOption(TABLE_ICAO, "MY NULL OPTION", "MY CODE"));
		addField(dropdown, "Dropdown 3", "CRT Data. Override the null option code/string values");

		dropdown = new WDropdown(new TableWithNullOption(TABLE_NODATA));
		addField(dropdown, "Dropdown 4", "CRT Data. No Data with a null option");

		dropdown = new WDropdown(TABLE_NODATA);
		addField(dropdown, "Dropdown 5", "CRT Data. No Data from CRT");

		dropdown = new WDropdown(TABLE_ICAO);
		dropdown.setReadOnly(true);
		addField(dropdown, "Dropdown 6", "CRT Data. Read Only");

		dropdownSelected = new WDropdown(TABLE_ICAO);
		dropdownSelected.setReadOnly(true);
		addField(dropdownSelected, "Dropdown 7", "CRT Data. Read Only with selected option");

		// Multi-dropdown
		WMultiDropdown multiDropdown = new WMultiDropdown(TABLE_ICAO);
		addField(multiDropdown, "Multi-dropdown 1",
				"CRT Data. Click the add button to add another item");

		multiDropdown = new WMultiDropdown(new TableWithNullOption(TABLE_ICAO));
		addField(multiDropdown, "Multi-dropdown 2", "CRT Data. With a null option");

		multiDropdown = new WMultiDropdown(new TableWithNullOption(TABLE_ICAO, "MY NULL OPTION",
				"MY CODE"));
		addField(multiDropdown, "Multi-dropdown 3",
				"CRT Data. Override the null option code/string values");

		multiDropdown = new WMultiDropdown(new TableWithNullOption(TABLE_NODATA));
		addField(multiDropdown, "Multi-dropdown 4", "CRT Data. No Data with a null option");

		multiDropdown = new WMultiDropdown(TABLE_ICAO);
		multiDropdown.setReadOnly(true);
		addField(multiDropdown, "Multi-dropdown 5", "CRT Data. Read Only");

		multiDropdownSelected = new WMultiDropdown(TABLE_ICAO);
		multiDropdownSelected.setReadOnly(true);
		addField(multiDropdownSelected, "Multi-dropdown 6",
				"CRT Data. Read Only with selected option");

		// MultiSelect
		WMultiSelect box = new WMultiSelect(TABLE_ICAO);
		addField(box, "Listbox 1", "CRT Data.");

		box = new WMultiSelect(TABLE_NODATA);
		addField(box, "Listbox 2", "CRT Data. No Data from CRT");

		box = new WMultiSelect(TABLE_ICAO);
		box.setReadOnly(true);
		addField(box, "Listbox 3", "CRT Data. Read Only.");

		boxSelected = new WMultiSelect(TABLE_ICAO);
		boxSelected.setReadOnly(true);
		addField(boxSelected, "Listbox 4", "CRT Data. Read Only with selected option.");

		// Button
		WButton refresh = new WButton("Refresh");
		add(refresh);
	}

	/**
	 * Adds a field to the example's layout.
	 *
	 * @param input the input field to add.
	 * @param labelText the label text for the field.
	 * @param labelHint the optional label hint for the field.
	 */
	private void addField(final WComponent input, final String labelText, final String labelHint) {
		WLabel label = new WLabel(labelText, input);
		if (labelHint != null) {
			label.setHint(labelHint);
		}

		layout.addField(label, input);
	}

	@Override
	protected void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			dropdownSelected.setSelected(dropdownSelected.getOptions().get(2));
			multiDropdownSelected.setSelected(Arrays.asList(new Object[]{multiDropdownSelected.
				getOptions().get(2)}));
			boxSelected.setSelected(Arrays.asList(new Object[]{boxSelected.getOptions().get(2)}));
			setInitialised(true);
		}
	}
}
