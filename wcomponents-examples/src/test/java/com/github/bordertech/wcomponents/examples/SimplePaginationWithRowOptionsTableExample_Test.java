package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.examples.table.SimplePaginationWithRowOptionsTableExample;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTableWebElement;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

/**
 * Selenium unit test for SimplePaginationWithRowOptionsTableExample.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class SimplePaginationWithRowOptionsTableExample_Test extends WComponentExamplesTestCase {

	/**
	 * Default constructor.
	 */
	public SimplePaginationWithRowOptionsTableExample_Test() {
		super(new SimplePaginationWithRowOptionsTableExample());
	}

	/**
	 * Test the bahviour of a paginated table.
	 */
	@Test
	public void testPagination() {

		SeleniumWTableWebElement table = getTable();

		Assert.assertEquals("Unexpected column header.", "First name", table.getHeaderTextForColumn(0));
		Assert.assertEquals("Unexpected column header.", "Last name", table.getHeaderTextForColumn(1));
		Assert.assertEquals("Unexpected column header.", "DOB", table.getHeaderTextForColumn(2));

		int totalRows = table.getTotalRows();
		int currentPage = table.getCurrentPage();
		int rowsPerPage = table.getRowsPerPage();
		int totalPages = table.getTotalPages();

		Assert.assertEquals("Expected 16 total rows", 16, totalRows);
		Assert.assertEquals("Expected to load first page in table.", 1, currentPage);
		Assert.assertEquals("Expected 2 rows per page by default.", 2, rowsPerPage);

		int expectedTotalPages = (int) Math.floor(totalRows / rowsPerPage);
		Assert.assertEquals("Expected total pages to match.", expectedTotalPages, totalPages);

		Assert.assertEquals("Expected table caption to match", "TABLE CAPTION", table.getTableCaption());

		assertRowContent(table, 0, "Joe", "Bloggs", "01 Feb 1973");
		assertRowContent(table, 1, "Richard", "Starkey", "04 Aug 1976");

		int firstIndex = 1;
		assertPageButtons(table, firstIndex, firstIndex + 1, false, true);

		table.getNextPageButton().click();
		//The table has now been reloaded - have to fetch again.
		table = getTable();

		assertPageButtons(table, firstIndex + rowsPerPage, firstIndex + rowsPerPage + 1, true, true);

		table.getLastPageButton().click();
		//The table has now been reloaded - have to fetch again.
		table = getTable();

		assertPageButtons(table, totalRows - 1, totalRows, true, false);

		table.getPreviousPageButton().click();
		//The table has now been reloaded - have to fetch again.
		table = getTable();

		assertPageButtons(table, (totalRows - rowsPerPage) - 1, totalRows - rowsPerPage, true, true);
	}

	/**
	 * Verify the behavior of the table's pagination buttons.
	 *
	 * @param table the table.
	 * @param firstResult the expected first table result.
	 * @param lastResult the expected last table result.
	 * @param backwardsButtonsEnabled the expected state of the back buttons.
	 * @param forwardsButtonsEnabled the expected state of the forward buttons.
	 */
	private void assertPageButtons(final SeleniumWTableWebElement table, final int firstResult, final int lastResult,
			final boolean backwardsButtonsEnabled, final boolean forwardsButtonsEnabled) {

		Assert.assertEquals("First row index on page does not match expected.", firstResult, table.getFirstRowIndexOfPage());
		Assert.assertEquals("Last row index on page does not match expected", lastResult, table.getLastRowIndexOfPage());
		Assert.assertEquals("First page button in unexpected state", backwardsButtonsEnabled, table.getFirstPageButton().isEnabled());
		Assert.assertEquals("Previous page button in unexpected state", backwardsButtonsEnabled, table.getPreviousPageButton().isEnabled());
		Assert.assertEquals("Next page button in unexpected state", forwardsButtonsEnabled, table.getNextPageButton().isEnabled());
		Assert.assertEquals("Last page button in unexpected state", forwardsButtonsEnabled, table.getLastPageButton().isEnabled());
	}

	/**
	 * undertake the repeated asssertions on row content.
	 * @param table the table
	 * @param row the current row
	 * @param cellContent the content of the current cell
	 */
	private void assertRowContent(final SeleniumWTableWebElement table, final int row, final String... cellContent) {
		for (int i = 0; i < cellContent.length; i++) {
			Assert.assertEquals("Unexpected cell content for row:column" + row + ":" + i, cellContent[i], table.getCellText(row, i));
		}
	}

	/**
	 * @return the SeleniumWTableWebElement for this test.
	 */
	private SeleniumWTableWebElement getTable() {
		return getDriver().findWTable(By.id(SimplePaginationWithRowOptionsTableExample.TABLE_ID));
	}

}
