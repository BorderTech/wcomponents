package com.github.bordertech.wcomponents.testapp;

import com.github.bordertech.wcomponents.AbstractTableDataModel;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.PaginationMode;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import java.util.List;

/**
 * Displays the table of search results.
 *
 * @author Martin Shevchenko
 */
public class SearchResultsPage extends WContainer {

	/**
	 * The table which contains the search results.
	 */
	private final WDataTable resultsTable;

	/**
	 * Creates a SearchResultsPage.
	 */
	public SearchResultsPage() {
		setTemplate(SearchResultsPage.class);

		resultsTable = new WDataTable();
		resultsTable.setPaginationMode(PaginationMode.DYNAMIC);
		resultsTable.setRowsPerPage(SearchCriteriaPage.DEFAULT_ROWS_PER_PAGE);

		add(resultsTable, "resultsTable");

		resultsTable.addColumn(new WTableColumn("Name", NameRenderer.class));
		resultsTable.addColumn(new WTableColumn("Country", WText.class));
		resultsTable.addColumn(new WTableColumn("Happy", WText.class));

		WButton newSearchBtn = new WButton("New Search");
		add(newSearchBtn, "newSearchButton");

		newSearchBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				TestApp.getInstance().doNewSearch();
			}
		});
	}

	/**
	 * Sets the search results.
	 *
	 * @param results the search results for the user.
	 */
	public void setSearchResults(final List results) {
		resultsTable.setDataModel(new MyDataModel(results));
	}

	/**
	 * Sets the number of rows to display per page.
	 *
	 * @param rowsPerPage the number of rows to display per page.
	 */
	public void setRowsPerPage(final int rowsPerPage) {
		resultsTable.setRowsPerPage(rowsPerPage);
	}

	/**
	 * The data model for the search results table.
	 */
	private static final class MyDataModel extends AbstractTableDataModel {

		/**
		 * The search results.
		 */
		private final List data;

		/**
		 * Creates a MyDataModel.
		 *
		 * @param data the search result data.
		 */
		private MyDataModel(final List data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			return data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final int row, final int col) {
			SearchResultRowBO rowData = (SearchResultRowBO) data.get(row);

			switch (col) {
				case 0:
					return rowData;

				case 1:
					return rowData.getCountry();

				case 2:
					return rowData.getHappy().booleanValue() ? "Yes" : "No";

				default:
					return null;
			}
		}
	}

	/**
	 * Renders the name link for a search result row.
	 */
	public static class NameRenderer extends WButton {

		/**
		 * Creates a NameRenderer.
		 */
		public NameRenderer() {
			setBeanProperty("name");
			setRenderAsLink(true);

			// When the button is clicked, show the details of the selected row.
			setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					SearchResultRowBO rowBO = (SearchResultRowBO) getBean();
					TestApp.getInstance().doDetails(rowBO);
				}
			});
		}
	}
}
