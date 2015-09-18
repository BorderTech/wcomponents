package com.github.bordertech.wcomponents.testapp;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import com.github.bordertech.wcomponents.validator.RegExFieldValidator;
import java.util.ArrayList;
import java.util.List;

/**
 * An example implementation of a typical "Search Criteria" screen.
 *
 * @author Martin Shevchenko
 */
public class SearchCriteriaPage extends WContainer {

	/**
	 * The default number of rows to display per page.
	 */
	public static final int DEFAULT_ROWS_PER_PAGE = 10;

	/**
	 * Search option to return a small amount of data.
	 */
	public static final String SMALL = "Small";
	/**
	 * Search option to return a moderate amount of data.
	 */
	public static final String MEDIUM = "Medium";
	/**
	 * Search option to return a large amount of data.
	 */
	public static final String LARGE = "Large";
	/**
	 * Search option to return a huge amount of data.
	 */
	public static final String HUGE = "Huge";

	/**
	 * The search data field.
	 */
	private final WTextField dataField = new WTextField();

	/**
	 * The number of results to return.
	 */
	private final WNumberField numRows = new WNumberField();

	/**
	 * The number of rows per page.
	 */
	private final WDropdown rowsPerPage = new WDropdown();

	/**
	 * The amount of detail to bring back for each record.
	 */
	private final WRadioButtonSelect detailsSize = new WRadioButtonSelect(
			new String[]{SMALL, MEDIUM, LARGE, HUGE});

	/**
	 * The button which initiates the search.
	 */
	private final WButton searchBtn = new WButton("Search");

	/**
	 * Creates a SearchCriteriaPage.
	 */
	public SearchCriteriaPage() {
		setTemplate(SearchCriteriaPage.class);

		WValidationErrors errorsBox = new WValidationErrors();
		add(errorsBox, "errorsBox");

		WFieldLayout fieldLayout = new WFieldLayout();
		add(fieldLayout, "fieldLayout");

		fieldLayout.addField("Name", dataField);
		fieldLayout.addField("Number of Rows", numRows);

		fieldLayout.addField("Rows per page", rowsPerPage);
		List<Integer> rppOptions = new ArrayList<>();
		rppOptions.add(5);
		rppOptions.add(10);
		rppOptions.add(20);
		rppOptions.add(50);
		rppOptions.add(100);
		rowsPerPage.setOptions(rppOptions);
		rowsPerPage.setSelected(Integer.valueOf(DEFAULT_ROWS_PER_PAGE));

		detailsSize.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		detailsSize.setFrameless(true);
		fieldLayout.addField("Data Size", detailsSize);

		// Validations
		dataField.setMandatory(true);
		numRows.setMandatory(true);
		numRows.addValidator(new RegExFieldValidator("^\\d*",
				"{0} must only contain numeric characters."));
		detailsSize.setMandatory(true);

		add(searchBtn, "searchButton");
		searchBtn.setAction(new ValidatingAction(errorsBox, fieldLayout) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				TestApp.getInstance().doSearch();
			}
		});

		WButton resetBtn = new WButton("Reset");
		add(resetBtn, "resetButton");
		resetBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				TestApp.getInstance().reset();
			}
		});
	}

	/**
	 * @return Returns the dataField.
	 */
	public WTextField getDataField() {
		return dataField;
	}

	/**
	 * @return Returns the dataQty.
	 */
	public WRadioButtonSelect getDetailsSize() {
		return detailsSize;
	}

	/**
	 * @return Returns the numRows.
	 */
	public WNumberField getNumRows() {
		return numRows;
	}

	/**
	 * Retrieves the current value of {@link #numRows} as an integer.
	 *
	 * @return the number of rows to return.
	 */
	public int getNumRowsAsInt() {
		String numAsString = getNumRows().getText();

		if (numAsString == null) {
			return 0;
		}

		return Integer.parseInt(numAsString);
	}

	/**
	 * @return Returns the rowsPerPage.
	 */
	public WDropdown getRowsPerPage() {
		return rowsPerPage;
	}

	/**
	 * @return Returns the search button.
	 */
	public WButton getSearchBtn() {
		return searchBtn;
	}

	/**
	 * Retrieves the current value of {@link #rowsPerPage} as an integer.
	 *
	 * @return the number of rows to display per page.
	 */
	public int getRowsPerPageAsInt() {
		Object selected = getRowsPerPage().getSelected();

		if (selected == null) {
			return 0;
		}
		return ((Integer) selected).intValue();
	}
}
