package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMultiDropdown;
import java.util.Arrays;
import java.util.List;

/**
 * Used to test WMultiDropdown Selenium helper and themes. Not an example!
 * @author Mark Reeves
 */
public class WMultiDropdownTestingExample extends WContainer {

	/**
	 * WFieldLayout used to set out the test components and their labels.
	 */
	private final WFieldLayout layout;

	/**
	 * Reset button to reset the default state.
	 */
	private final WButton resetButton;

	/**
	 * Public for testing purposes.
	 * Data used by the example.
	 */
	public static final List<String> DATA_LIST = Arrays.asList(new String[]{"ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "WA", "None"});

	/**
	 * Public for testing purposes.
	 * Data of a component with a single selection
	 */
	public static final List<String> DATA_ONE_SELECTED = Arrays.asList(new String[]{"NSW"});

	/**
	 * Public for testing purposes.
	 * Data of a component with some selections
	 */
	public static final List<String> DATA_SOME_SELECTED = Arrays.asList(new String[]{"NSW", "QLD"});

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of an editable component with no default selection.
	 */
	public static final String LABEL_NO_SELECTION = "No selection";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of an editable component with one option as default selection.
	 */
	public static final String LABEL_ONE_SELECTED = "One selected by default";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of an editable component with several but not all options as default selection.
	 */
	public static final String LABEL_SOME_SELECTED = "Some selected by default";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of an editable component with all options as default selection.
	 */
	public static final String LABEL_ALL_SELECTED = "All selected by default";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of read-only component with no default selection.
	 */
	public static final String LABEL_RO_NO_SELECTION = "Read only no selection";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of read-only component with one option as default selection.
	 */
	public static final String LABEL_RO_ONE_SELECTED = "Read only one selected by default";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of read-only component with several but not all options as default selection.
	 */
	public static final String LABEL_RO_SOME_SELECTED = "Read only some selected by default";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of read-only component with several but not all options as default selection.
	 */
	public static final String LABEL_RO_ALL_SELECTED = "Read only all selected by default";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of a disabled component
	 */
	public static final String LABEL_DISABLED = "Disabled";

	/**
	 * Public for testing purposes to get a handle to its component.
	 * The label of mandatory component.
	 */
	public static final String LABEL_MANDATORY = "Mandatory";

	/**
	 * Create the example.
	 */
	public WMultiDropdownTestingExample() {
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);

		resetButton = new WButton("Cancel");

		resetButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				layout.reset();
			}
		});

		setupUI();
	}

	/**
	 * Build the UI.
	 */
	private void setupUI() {

		add(layout);

		// no default selection (first option is deemed selected)
		addNoSelectionExample(false);
		addOneSelectionExample(false);
		addSomeSelectionExample(false);
		addAllSelectionExample(false);

		// readOnly
		addNoSelectionExample(true);
		addOneSelectionExample(true);
		addSomeSelectionExample(true);
		addAllSelectionExample(true);

		// disabled
		WMultiDropdown dropdown = new WMultiDropdown(DATA_LIST);
		dropdown.setSelected(DATA_SOME_SELECTED);
		dropdown.setDisabled(true);
		layout.addField(LABEL_DISABLED, dropdown);

		// mandatory
		dropdown = new WMultiDropdown(DATA_LIST);
		dropdown.setSelected(DATA_SOME_SELECTED);
		dropdown.setMandatory(true);
		layout.addField(LABEL_MANDATORY, dropdown);

		// add a reset button
		layout.addField(resetButton);
	}

	/**
	 * Add example with no default selection.
	 * @param readOnly if {@code true} then make the example read-only.
	 */
	private void addNoSelectionExample(final boolean readOnly) {
		String label = readOnly ? LABEL_RO_NO_SELECTION : LABEL_NO_SELECTION;
		WMultiDropdown dropdown = new WMultiDropdown(DATA_LIST);
		dropdown.setReadOnly(readOnly);
		layout.addField(label, dropdown);
	}

	/**
	 * Add example with one default selection.
	 * @param readOnly if {@code true} then make the example read-only.
	 */
	private void addOneSelectionExample(final boolean readOnly) {
		String label = readOnly ? LABEL_RO_ONE_SELECTED : LABEL_ONE_SELECTED;
		WMultiDropdown dropdown = new WMultiDropdown(DATA_LIST);
		dropdown.setSelected(DATA_ONE_SELECTED);
		dropdown.setReadOnly(readOnly);
		layout.addField(label, dropdown);
	}

	/**
	 * Add example with one default selection.
	 * @param readOnly if {@code true} then make the example read-only.
	 */
	private void addSomeSelectionExample(final boolean readOnly) {
		String label = readOnly ? LABEL_RO_SOME_SELECTED : LABEL_SOME_SELECTED;
		WMultiDropdown dropdown = new WMultiDropdown(DATA_LIST);
		dropdown.setSelected(DATA_SOME_SELECTED);
		dropdown.setReadOnly(readOnly);
		layout.addField(label, dropdown);
	}


	/**
	 * Add example with one default selection.
	 * @param readOnly if {@code true} then make the example read-only.
	 */
	private void addAllSelectionExample(final boolean readOnly) {
		String label = readOnly ? LABEL_RO_ALL_SELECTED : LABEL_ALL_SELECTED;
		WMultiDropdown dropdown = new WMultiDropdown(DATA_LIST);
		dropdown.setSelected(DATA_LIST);
		dropdown.setReadOnly(readOnly);
		layout.addField(label, dropdown);
	}

}
