package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import java.text.MessageFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing a WTable.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SeleniumWTableWebElement extends SeleniumWComponentWebElement {

	/**
	 * The table itself is a 'table' entity, but the element containing all the controls is the wrapper div.
	 */
	public static final String TABLE_TAG_NAME = "div";

	/**
	 * The incorrect tag name that might be selected - the name of the child table element.
	 */
	public static final String TABLE_CHILD_TAG_NAME = "table";

	/**
	 * The tag name for the header.
	 */
	public static final String TABLE_HEADER_TAG_NAME = "thead";

	/**
	 * The div class representing the table.
	 */
	public static final String TABLE_DIV_CLASS = "wc-table";

	/**
	 * The attribute on the table tag that records how many rows per page.
	 */
	public static final String ROWS_PER_PAGE_TABLE_ATTRIBUTE = "data-wc-rpp";

	/**
	 * The CSS Selector for the table caption.
	 */
	public static final String SELECTOR_TABLE_CAPTION = "table caption";

	/**
	 * The CSS Selector to find the first row index of page.
	 */
	public static final String SELECTOR_FIRST_ROW_INDEX_OF_PAGE = "span.wc_table_pag_rowstart";

	/**
	 * The CSS Selector to find the last row index of page.
	 */
	public static final String SELECTOR_LAST_ROW_INDEX_OF_PAGE = "span.wc_table_pag_rowend";

	/**
	 * The CSS Selector to find the last result of page.
	 */
	public static final String SELECTOR_TOTAL_ROWS = "span.wc_table_pag_total";

	/**
	 * The CSS Selector to find the first page button.
	 */
	public static final String SELECTOR_FIRST_PAGE_BUTTON = "span.wc_table_pag_btns button:nth-of-type(1)";

	/**
	 * The CSS Selector to find the previous page button.
	 */
	public static final String SELECTOR_PREVIOUS_PAGE_BUTTON = "span.wc_table_pag_btns button:nth-of-type(2)";

	/**
	 * The CSS Selector to find the next page button.
	 */
	public static final String SELECTOR_NEXT_PAGE_BUTTON = "span.wc_table_pag_btns button:nth-of-type(3)";

	/**
	 * The CSS Selector to find the last page button.
	 */
	public static final String SELECTOR_LAST_PAGE_BUTTON = "span.wc_table_pag_btns button:nth-of-type(4)";

	/**
	 * The CSS Selector for the page select.
	 */
	public static final String SELECTOR_PAGE_SELECT = "select.wc_table_pag_select";

	/**
	 * The CSS Selector for the rows per page select.
	 */
	public static final String SELECTOR_ROWS_PER_PAGE_SELECT = "select.wc_table_pag_rpp";

	/**
	 * The CSS Selector to find a particular column header.
	 */
	public static final String SELECTOR_COLUMN_HEADER = "thead tr th[data-wc-columnidx=''{0}'']";

	/**
	 * The CSS Selector to find a particular column header's text.
	 */
	public static final String SELECTOR_COLUMN_HEADER_TEXT = SELECTOR_COLUMN_HEADER + " div.wc-labelbody";

	/**
	 * The CSS Selector to find the cell content.
	 */
	public static final String SELECTOR_CELL_CONTENT = "tbody tr[data-wc-rowindex=''{0}''] td:nth-of-type({1})";

	/**
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWTableWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String elementTag = element.getTagName();
		final String elementClass = element.getAttribute("class");

		if (!elementTag.equals(TABLE_TAG_NAME)) {
			//Tag name is incorrect - error scenario
			if (elementTag.equals(TABLE_CHILD_TAG_NAME)) {
				throw new SystemException("Incorrect element selected for SeleniumWTableWebElement."
						+ "Expected the wrapper div element containing the table and controls, but instead found the table element.");
			} else {
				throw new SystemException("Incorrect element selected for SeleniumWTableWebElement. Expected div but found: " + elementTag);
			}
		}

		if (!elementClass.contains(TABLE_DIV_CLASS)) {
			throw new SystemException("Incorrect element selected for SeleniumWTableWebElement. Expected div containing class " + TABLE_DIV_CLASS
					+ " but found div with class " + elementClass);
		}
	}

	/**
	 * @return the index of the first row displayed on the current page.
	 */
	public int getFirstRowIndexOfPage() {
		SeleniumWComponentWebElement wrapper = findElementImmediate(By.cssSelector(SELECTOR_FIRST_ROW_INDEX_OF_PAGE));
		return Integer.parseInt(wrapper.getText());
	}

	/**
	 * @return the index of the last row displayed on the current page.
	 */
	public int getLastRowIndexOfPage() {
		SeleniumWComponentWebElement wrapper = findElementImmediate(By.cssSelector(SELECTOR_LAST_ROW_INDEX_OF_PAGE));
		return Integer.parseInt(wrapper.getText());
	}

	/**
	 * @return the total number of rows in the table..
	 */
	public int getTotalRows() {
		SeleniumWComponentWebElement wrapper = findElementImmediate(By.cssSelector(SELECTOR_TOTAL_ROWS));
		return Integer.parseInt(wrapper.getText());
	}

	/**
	 * @return the actual table element within the elements that make up an advanced WComponent table.
	 */
	public SeleniumWComponentWebElement getTable() {
		return findElementImmediate(By.tagName(TABLE_CHILD_TAG_NAME));
	}

	/**
	 * @return the first page button for pagination.
	 */
	public SeleniumWComponentWebElement getFirstPageButton() {
		return findElementImmediate(By.cssSelector(SELECTOR_FIRST_PAGE_BUTTON));
	}

	/**
	 * @return the previous page button for pagination.
	 */
	public SeleniumWComponentWebElement getPreviousPageButton() {
		return findElementImmediate(By.cssSelector(SELECTOR_PREVIOUS_PAGE_BUTTON));
	}

	/**
	 * @return the next page button for pagination.
	 */
	public SeleniumWComponentWebElement getNextPageButton() {
		return findElementImmediate(By.cssSelector(SELECTOR_NEXT_PAGE_BUTTON));
	}

	/**
	 * @return the last page button for pagination.
	 */
	public SeleniumWComponentWebElement getLastPageButton() {
		return findElementImmediate(By.cssSelector(SELECTOR_LAST_PAGE_BUTTON));
	}

	/**
	 * @return the page select.
	 */
	public SeleniumSimpleSelectWebElement getPageSelect() {
		return findSeleniumSimpleSelectWebElement(By.cssSelector(SELECTOR_PAGE_SELECT));
	}

	/**
	 * @return the page select.
	 */
	public SeleniumSimpleSelectWebElement getRowsPerPageSelect() {
		return findSeleniumSimpleSelectWebElement(By.cssSelector(SELECTOR_ROWS_PER_PAGE_SELECT));
	}

	/**
	 * @return the current page of the paginated data.
	 */
	public int getCurrentPage() {
		return Integer.parseInt(getPageSelect().getSelectedOption().getText());
	}

	/**
	 * @return the total number of pages.
	 */
	public int getTotalPages() {
		return Integer.parseInt(getPageSelect().getLastOption().getText());
	}

	/**
	 * @return the number of rows per page.
	 */
	public int getRowsPerPage() {
		return Integer.parseInt(getTable().getAttribute(ROWS_PER_PAGE_TABLE_ATTRIBUTE));
	}

	/**
	 * @return the table caption.
	 */
	public String getTableCaption() {
		return findElementImmediate(By.cssSelector(SELECTOR_TABLE_CAPTION)).getText();
	}

	/**
	 * @return the table header.
	 */
	public SeleniumWComponentWebElement getTableHeader() {
		return findElementImmediate(By.tagName(TABLE_HEADER_TAG_NAME));
	}

	/**
	 * Get the TH element for the given column index.
	 *
	 * @param columnIndex the column index.
	 * @return the TH element.
	 */
	public SeleniumWComponentWebElement getHeaderForColumn(final int columnIndex) {
		String selector = MessageFormat.format(SELECTOR_COLUMN_HEADER, columnIndex);
		return findElementImmediate(By.cssSelector(selector));
	}

	/**
	 * Get the header text for the given column index.
	 *
	 * @param columnIndex the column index.
	 * @return the header text.
	 */
	public String getHeaderTextForColumn(final int columnIndex) {
		String selector = MessageFormat.format(SELECTOR_COLUMN_HEADER_TEXT, columnIndex);
		return findElementImmediate(By.cssSelector(selector)).getText();
	}

	/**
	 * Get the content of the cell.
	 *
	 * @param rowIndex the row index, 0-based.
	 * @param columnIndex the column index, 0-based.
	 * @return the cell's content.
	 */
	public SeleniumWComponentWebElement getCellContent(final int rowIndex, final int columnIndex) {

		// The CSS selector for row is 0 based, but column is 1 based.
		// Manually adjust the index to hide this inconsistency.
		int adjustedColIndex = columnIndex + 1;
		String selector = MessageFormat.format(SELECTOR_CELL_CONTENT, rowIndex, adjustedColIndex);
		return findElementImmediate(By.cssSelector(selector));
	}

	/**
	 * Get the text of the cell.
	 *
	 * @param rowIndex the row index, 0-based.
	 * @param columnIndex the column index, 0-based.
	 * @return the text of the cell.
	 */
	public String getCellText(final int rowIndex, final int columnIndex) {
		return getCellContent(rowIndex, columnIndex).getText();
	}

	/**
	 * @param by the by to find the component.
	 * @return the SeleniumSimpleSelectWebElement.
	 */
	public SeleniumSimpleSelectWebElement findSeleniumSimpleSelectWebElement(final By by) {
		return new SeleniumSimpleSelectWebElement(findElementImmediate(by), getDriver());
	}
}
