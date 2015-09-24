package com.github.bordertech.wcomponents.testapp;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;

/**
 * Displays the details of a search result.
 *
 * @author Martin Shevchenko
 */
public class DetailsPage extends WContainer {

	private final WTextField textField;
	private final WDropdown dropdown;
	private final WCheckBox checkbox;
	private final RadioButtonGroup radioGroup;
	private final WMultiSelect multiSelect;
	private final WTextArea textArea;

	/**
	 * Creates a DetailsPage.
	 */
	public DetailsPage() {
		add(new WHeading(WHeading.MAJOR, "Details"));
		// core input fields, tabs, collapsible, menu

		WTabSet tabs = new WTabSet();
		add(tabs);
		WFieldLayout basicFields = new WFieldLayout();
		tabs.addTab(basicFields, "Basic", WTabSet.TAB_MODE_SERVER, 'B');
		tabs.addTab(new WText("TODO"), "Extra", WTabSet.TAB_MODE_SERVER, 'E');

		BigPanel largeTab = new BigPanel();
		tabs.addTab(largeTab, "LargeTab", WTabSet.TAB_MODE_SERVER, 'L');

		textField = new WTextField();
		basicFields.addField("Name", textField);

		dropdown = new WDropdown(TestLookupTable.DAY_OF_WEEK_TABLE);
		basicFields.addField("Country", dropdown);

		checkbox = new WCheckBox();
		basicFields.addField("Ticked", checkbox);

		radioGroup = new RadioButtonGroup();
		WContainer radioPanel = new WContainer();
		radioPanel.add(new WText("Yes"));
		radioPanel.add(radioGroup.addRadioButton(Boolean.TRUE));
		radioPanel.add(new WText("No"));
		radioPanel.add(radioGroup.addRadioButton(Boolean.FALSE));
		basicFields.addField("Happy", radioPanel);

		multiSelect = new WMultiSelect();
		multiSelect.setOptions(SearchResultRowBO.ANIMAL_OPTIONS);
		basicFields.addField("Animals", multiSelect);

		textArea = new WTextArea();
		textArea.setColumns(60);
		textArea.setRows(8);
		basicFields.addField("Desc", textArea);

		WButton backToResultsBtn = new WButton("Results");
		add(backToResultsBtn);

		backToResultsBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				TestApp.getInstance().gotoSearchResults();
			}
		});

		WButton newSearchBtn = new WButton("New Search");
		add(newSearchBtn);

		newSearchBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				TestApp.getInstance().doNewSearch();
			}
		});
	}

	/**
	 * Sets the details to display.
	 *
	 * @param row the details to display.
	 */
	public void setDetails(final SearchResultRowBO row) {
		textField.setText(row.getName());
		dropdown.setSelected(row.getCountry());
		checkbox.setSelected(Boolean.TRUE.equals(row.getTicked()));
		radioGroup.setData(row.getHappy());
		multiSelect.setSelected(row.getAnimals());
		textArea.setText(row.getDesc());
	}
}
