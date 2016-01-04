package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.WTable.ExpandMode;
import com.github.bordertech.wcomponents.WTable.PaginationMode;
import com.github.bordertech.wcomponents.WTable.PaginationLocation;
import com.github.bordertech.wcomponents.WTable.RowIdWrapper;
import com.github.bordertech.wcomponents.WTable.SelectAllType;
import com.github.bordertech.wcomponents.WTable.SelectMode;
import com.github.bordertech.wcomponents.WTable.SeparatorType;
import com.github.bordertech.wcomponents.WTable.SortMode;
import com.github.bordertech.wcomponents.WTable.StripingType;
import com.github.bordertech.wcomponents.WTable.TableModel;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Junit test case for {@link WTable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class WTable_Test extends AbstractWComponentTestCase {

	/**
	 * error message expected for testAddActionConstraint - private in class being tested.
	 */
	private static final String ACTION_CONSTRAINT_ERROR_MESSAGE = "Can only add a constraint to a button which is in this table's actions";

	/**
	 * error message expected for testSetCurrentPage - private in class being tested.
	 */
	private static final String SET_CURRENT_PAGE_ERROR_MESSAGE = "Page number must be greater than or equal to zero.";

	/**
	 * error message expected for testSetRowsPerPage - private in class being tested.
	 */
	private static final String SET_ROWS_PER_PAGE_ERROR_MESSAGE = "Rows per page must be greater than 0, but got: ";

	@Test
	public void testColumnAccessors() {
		WTable table = new WTable();
		Assert.assertEquals("Table should not have any columns by default", 0, table.
				getColumnCount());

		WTableColumn col1 = new WTableColumn("dummy", WText.class);
		WTableColumn col2 = new WTableColumn("dummy", WText.class);

		table.addColumn(col1);
		Assert.assertEquals("Incorrect column count", 1, table.getColumnCount());

		table.addColumn(col2);
		Assert.assertEquals("Incorrect column count", 2, table.getColumnCount());

		Assert.assertSame("Incorrect first column", col1, table.getColumn(0));
		Assert.assertSame("Incorrect second column", col2, table.getColumn(1));
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WTable(), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testDataModelAccessors() {
		TableModel model1 = createModel(new String[0][0]);
		TableModel model2 = createModel(new String[0][0]);
		assertAccessorsCorrect(new WTable(), "tableModel", EmptyTableModel.INSTANCE, model1, model2);
	}

	@Test
	public void testSeparatorTypeAccessors() {
		assertAccessorsCorrect(new WTable(), "separatorType", SeparatorType.NONE,
				SeparatorType.HORIZONTAL,
				SeparatorType.VERTICAL);
	}

	@Test
	public void testStripingTypeAccessors() {
		assertAccessorsCorrect(new WTable(), "stripingType", StripingType.NONE, StripingType.ROWS,
				StripingType.COLUMNS);
	}

	@Test
	public void testExpandAllTypeAccessors() {
		assertAccessorsCorrect(new WTable(), "expandAll", false, true, false);
	}

	@Test
	public void testExpandModeAccessors() {
		assertAccessorsCorrect(new WTable(), "expandMode", ExpandMode.NONE, ExpandMode.LAZY,
				ExpandMode.DYNAMIC);
	}

	@Test
	public void testPaginationModeAccessors() {
		assertAccessorsCorrect(new WTable(), "paginationMode", PaginationMode.NONE,
				PaginationMode.CLIENT,
				PaginationMode.DYNAMIC);
	}

	@Test
	public void testPaginationLocationAccessors() {
		assertAccessorsCorrect(new WTable(), "paginationLocation", PaginationLocation.AUTO,
				PaginationLocation.TOP,
				PaginationLocation.BOTH);
	}

	@Test
	public void testSortModeAccessors() {
		assertAccessorsCorrect(new WTable(), "sortMode", SortMode.NONE, SortMode.DYNAMIC,
				SortMode.NONE);
	}

	@Test
	public void testSelectModeAccessors() {
		assertAccessorsCorrect(new WTable(), "selectMode", SelectMode.NONE, SelectMode.SINGLE,
				SelectMode.MULTIPLE);
	}

	@Test
	public void testSelectAllTypeAccessors() {
		assertAccessorsCorrect(new WTable(), "selectAllMode", SelectAllType.TEXT,
				SelectAllType.CONTROL,
				SelectAllType.TEXT);
	}

	@Test
	public void testSummaryAccessors() {
		assertAccessorsCorrect(new WTable(), "summary", null, "summary1", "summary2");
	}

	@Test
	public void testCaptionAccessors() {
		assertAccessorsCorrect(new WTable(), "caption", null, "caption1", "caption2");
	}

	@Test
	public void testNoDataMessageAccessors() {
		String msg = I18nUtilities.format(null, InternalMessages.DEFAULT_NO_TABLE_DATA);
		assertAccessorsCorrect(new WTable(), "noDataMessage", msg, "nodata1", "nodata2");
	}

	@Test
	public void testSelectionChangeActionAccessors() {
		assertAccessorsCorrect(new WTable(), "selectionChangeAction", null, new TestAction(),
				new TestAction());
	}

	@Test
	public void testRowsPerPageAccessors() {
		assertAccessorsCorrect(new WTable(), "rowsPerPage", 10, 5, 7);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowsPerPageInvalid() {
		new WTable().setRowsPerPage(-123);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowsPerPageZeroInvalid() {
		new WTable().setRowsPerPage(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowsPerPageWithOptionsInvalid() {
		WTable table = new WTable();
		table.setRowsPerPageOptions(Arrays.asList(10, 20, 30));
		new WTable().setRowsPerPage(-123);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowsPerPageWithOptionsZeroInvalid() {
		WTable table = new WTable();
		table.setRowsPerPageOptions(Arrays.asList(10, 20, 30));
		new WTable().setRowsPerPage(0);
	}

	@Test
	public void testRowsPerPageWithOptionsZeroValue() {
		WTable table = new WTable();
		table.setTableModel(createModel(new String[100][1]));
		table.setPaginationMode(PaginationMode.CLIENT);
		table.setRowsPerPageOptions(Arrays.asList(0, 1, 2));
		table.setRowsPerPage(0);
		assertAccessorsCorrect(table, "rowsPerPage", 0, 1, 2);
	}

	@Test
	public void testRowsPerPageOptionsAccessors() {
		assertAccessorsCorrect(new WTable(), "rowsPerPageOptions", null, Arrays.asList(10, 20, 30),
				Arrays.asList(11, 22, 33));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowsPerPageOptionsInvalidOptionNegative() {
		new WTable().setRowsPerPageOptions(Arrays.asList(10, -1, 30));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowsPerPageOptionsInvalidOptionNull() {
		new WTable().setRowsPerPageOptions(Arrays.asList(null, 10, 30));
	}

	@Test
	public void testRowsPerPageOptionsChangeRowsPerPage() {
		WTable table = new WTable();
		table.setTableModel(createModel(new String[100][1]));
		table.setPaginationMode(PaginationMode.CLIENT);
		table.setRowsPerPage(40);
		Assert.assertEquals("Rows per page should be 40", 40, table.getRowsPerPage());
		table.setRowsPerPageOptions(Arrays.asList(1, 2, 3));
		Assert.assertEquals("Rows per page should be the first option as it was not a valid option",
				1, table.getRowsPerPage());
	}

	@Test
	public void testRowsPerPageOptionsClearedAndRowsPerPageZero() {
		WTable table = new WTable();
		table.setTableModel(createModel(new String[100][1]));
		table.setPaginationMode(PaginationMode.CLIENT);

		// Change to null
		table.setRowsPerPageOptions(Arrays.asList(0, 1, 2));
		Assert.assertNotNull("Rows per options should not be null", table.getRowsPerPageOptions());
		table.setRowsPerPage(0);
		Assert.assertEquals("Rows per page should be 0", 0, table.getRowsPerPage());
		table.setRowsPerPageOptions(null);
		Assert.assertNull("Rows per options should be null", table.getRowsPerPageOptions());
		Assert.assertEquals("Rows per page should be the default after the options are set to null",
				10, table.getRowsPerPage());

		// Change to empty
		table.setRowsPerPageOptions(Arrays.asList(0, 1, 2));
		Assert.assertNotNull("Rows per options should not be null", table.getRowsPerPageOptions());
		table.setRowsPerPage(0);
		Assert.assertEquals("Rows per page should be 0", 0, table.getRowsPerPage());
		table.setRowsPerPageOptions(Collections.EMPTY_LIST);
		Assert.assertNull("Rows per options should be null", table.getRowsPerPageOptions());
		Assert.
				assertEquals(
						"Rows per page should be the default after the options are set to empty", 10,
						table.getRowsPerPage());
	}

	@Test
	public void testCurrentPageAccessors() {
		WTable table = new WTable();
		table.setTableModel(createModel(new String[100][1]));
		table.setPaginationMode(PaginationMode.CLIENT);
		table.setRowsPerPage(10);
		assertAccessorsCorrect(table, "currentPage", 0, 5, 7);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCurrentPageInvalidNegative() {
		new WTable().setCurrentPage(-123);
	}

	@Test
	public void testCurrentPageLogic() {
		WTable table = new WTable();
		table.setPaginationMode(PaginationMode.CLIENT);
		table.setTableModel(createModel(new String[100][1]));
		table.setRowsPerPage(10);
		// Page is greater than number of pages
		table.setCurrentPage(90);
		Assert.assertEquals("Current page should be equal to the max page of 9", 9, table.
				getCurrentPage());
	}

	@Test
	public void testCurrentPageLogicWithNoPagination() {
		WTable table = new WTable();
		table.setPaginationMode(PaginationMode.NONE);
		table.setTableModel(createModel(new String[100][1]));
		table.setRowsPerPage(10);
		table.setCurrentPage(50);
		Assert.assertEquals("Current page should alway be zero for a table with no pagination", 0,
				table.getCurrentPage());
	}

	@Test
	public void testCurrentPageLogicWithRowsOptions() {
		WTable table = new WTable();
		table.setTableModel(createModel(new String[100][1]));
		table.setPaginationMode(PaginationMode.CLIENT);
		table.setRowsPerPageOptions(Arrays.asList(0, 5));

		// Rows option of 5
		table.setRowsPerPage(5);
		table.setCurrentPage(2);
		Assert.assertEquals("Current page should be 2 for a table with rows option of 5", 2, table.
				getCurrentPage());

		// Rows option of 0
		table.setRowsPerPage(0);
		table.setCurrentPage(2);
		Assert.assertEquals("Current page should alway be 0 for a table with rows option of 0", 0,
				table.getCurrentPage());
	}

	@Test
	public void testSelectedRowsAccessors() {
		WTable table = new WTable();
		table.setTableModel(createModel(new String[10][1]));

		Set<Object> rows1 = new HashSet<Object>(Arrays.asList(Arrays.asList(0), Arrays.asList(1),
				Arrays.asList(2)));
		Set<Object> rows2 = new HashSet<Object>(Arrays.asList(Arrays.asList(3), Arrays.asList(4),
				Arrays.asList(5)));

		assertAccessorsCorrect(table, "selectedRows", Collections.EMPTY_SET, rows1, rows2);
	}

	@Test
	public void testExpandedRowsAccessors() {
		WTable table = new WTable();
		table.setTableModel(createModel(new String[10][1]));

		Set<Object> rows1 = new HashSet<Object>(Arrays.asList(Arrays.asList(0), Arrays.asList(1),
				Arrays.asList(2)));
		Set<Object> rows2 = new HashSet<Object>(Arrays.asList(Arrays.asList(3), Arrays.asList(4),
				Arrays.asList(5)));

		assertAccessorsCorrect(table, "expandedRows", Collections.EMPTY_SET, rows1, rows2);
	}

	@Test
	public void testHandlePaginationRequest() {
		WTable table = new WTable();
		table.setPaginationMode(WTable.PaginationMode.DYNAMIC);
		table.setTableModel(createModel(new String[100][1]));
		table.setRowsPerPage(10);

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".page", "5");

		table.handleRequest(request);
		Assert.assertEquals("Incorrect page number after handleRequest", 5, table.getCurrentPage());

		resetContext();
		Assert.assertEquals("Incorrect default page number after handleRequest", 0, table.
				getCurrentPage());
	}

	@Test
	public void testHandlePaginationRequestWithRowsOption() {
		WTable table = new WTable();
		table.setPaginationMode(WTable.PaginationMode.DYNAMIC);
		table.setTableModel(createModel(new String[100][1]));
		table.setCurrentPage(1);
		table.setRowsPerPage(20);
		table.setRowsPerPageOptions(Arrays.asList(10, 20, 30, 0));

		table.setLocked(true);
		setActiveContext(createUIContext());

		// Test change of rows option
		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".page", "1");
		request.setParameter(table.getId() + ".rows", "10");

		table.handleRequest(request);
		Assert.assertEquals("Incorrect rows per page after handleRequest", 10, table.
				getRowsPerPage());
		Assert.assertEquals("Incorrect page number after handleRequest with change of rows", 2,
				table.getCurrentPage());

		// Test no change of rows option or page
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".page", "2");
		request.setParameter(table.getId() + ".rows", "10");

		table.handleRequest(request);
		Assert.assertEquals("Incorrect rows per page after handleRequest and no change", 10, table.
				getRowsPerPage());
		Assert.assertEquals("Incorrect page number after handleRequest and no change", 2, table.
				getCurrentPage());

		// Test no change of rows option but change page
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".page", "4");
		request.setParameter(table.getId() + ".rows", "10");

		table.handleRequest(request);
		Assert.assertEquals("Incorrect rows per page after handleRequest and no change", 10, table.
				getRowsPerPage());
		Assert.assertEquals("Incorrect page number after handleRequest with change", 4, table.
				getCurrentPage());

		// Test change to zero rows option
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".page", "4");
		request.setParameter(table.getId() + ".rows", "0");

		table.handleRequest(request);
		Assert.assertEquals(
				"Incorrect rows per page after handleRequest and change to zero rows option", 0,
				table.getRowsPerPage());
		Assert.assertEquals(
				"Incorrect page number after handleRequest with change to zero rows option", 0,
				table.getCurrentPage());

		resetContext();
		Assert.assertEquals("Incorrect default page number after handleRequest", 1, table.
				getCurrentPage());
		Assert.assertEquals("Incorrect default rows per page after handleRequest", 20, table.
				getRowsPerPage());
	}

	@Test
	public void testHandleExpansionRequest() {
		WTable table = new WTable();
		table.setExpandMode(WTable.ExpandMode.DYNAMIC);
		table.setTableModel(createModel(new String[100][1]));

		Set<Object> expanded = new HashSet<Object>(Arrays.asList(5, 6, 7));

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".expanded", new String[]{"5", "6", "7"});

		table.handleRequest(request);
		Assert.assertEquals("Incorrect expanded rows after handleRequest", 3, table.
				getExpandedRows().size());
		Assert.assertTrue("Incorrect expanded rows after handleRequest", table.getExpandedRows().
				containsAll(expanded));

		resetContext();
		Assert.assertTrue("Incorrect default expanded rows after handleRequest", table.
				getExpandedRows().isEmpty());

		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");

		table.handleRequest(request);
		Assert.assertTrue("Incorrect expanded rows after handleRequest with no expansions", table.
				getExpandedRows()
				.isEmpty());

		resetContext();
		Assert.assertTrue("Incorrect default expanded rows after handleRequest with no expansions",
				table
				.getExpandedRows().isEmpty());
	}

	@Test
	public void testSingleHandleSelectionRequest() {
		WTable table = new WTable();
		table.setSelectMode(WTable.SelectMode.SINGLE);
		table.setTableModel(createModel(new String[100][1]));

		table.setLocked(true);
		setActiveContext(createUIContext());

		Set<Object> selected = new HashSet<Object>(Arrays.asList(5, 6, 7));

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".selected", new String[]{"5", "6", "7"});

		table.handleRequest(request);
		Assert.assertEquals("Should only have selected the first item after handleRequest", 1,
				table.getSelectedRows()
				.size());
		Assert.assertTrue("Should only have selected the first item after handleRequest",
				selected.containsAll(table.getSelectedRows()));

		resetContext();
		Assert.assertTrue("Incorrect default selection after handleRequest",
				table.getSelectedRows().isEmpty());
	}

	@Test
	public void testMultipleHandleSelectionRequest() {
		WTable table = new WTable();
		table.setSelectMode(WTable.SelectMode.MULTIPLE);
		table.setTableModel(createModel(new String[100][1]));

		Set<Object> selected = new HashSet<Object>(Arrays.asList(5, 6, 7));

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".selected", new String[]{"5", "6", "7"});

		table.handleRequest(request);
		Assert.assertEquals("Incorrect selection after handleRequest", 3, table.getSelectedRows().
				size());
		Assert
				.assertTrue("Incorrect selection rows after handleRequest", table.getSelectedRows().
						containsAll(selected));

		resetContext();
		Assert.assertTrue("Incorrect default selection after handleRequest",
				table.getSelectedRows().isEmpty());

		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");

		table.handleRequest(request);
		Assert.assertTrue("Incorrect selection after handleRequest with no selection set", table.
				getSelectedRows()
				.isEmpty());

		resetContext();
		Assert.assertTrue("Incorrect default selection after handleRequest with no selection set",
				table
				.getSelectedRows().isEmpty());
	}

	// TODO check sort test???
	@Test
	public void testHandleSortRequest() {

		SimpleTableModel model = new SimpleTableModel(new String[][]{{"1"}, {"3"}, {"2"}});
		model.setComparator(0, SimpleTableModel.COMPARABLE_COMPARATOR);

		WTable table = new WTable();
		table.setSelectMode(WTable.SelectMode.SINGLE);
		table.setTableModel(new AdapterBasicTableModel(model));
		table.addColumn(new WTableColumn("dummy", WText.class));
		table.setSortMode(SortMode.DYNAMIC);

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".sort", "0");

		table.handleRequest(request);
		Assert.assertTrue("Incorrect selection after handleRequest", table.isSorted());
		Assert.assertEquals("Incorrect sort column after handleRequest", 0, table.
				getSortColumnIndex());
		Assert.assertTrue("Incorrect sort direction after handleRequest", table.isSortAscending());

		List<RowIdWrapper> rowIndices = table.getRepeater().getBeanList();
		Assert.assertEquals("Incorrect sort", 3, rowIndices.size());
		Assert.assertEquals("Incorrect sort", Integer.valueOf(0), rowIndices.get(0).getRowIndex().
				get(0));
		Assert.assertEquals("Incorrect sort", Integer.valueOf(1), rowIndices.get(1).getRowIndex().
				get(0));
		Assert.assertEquals("Incorrect sort", Integer.valueOf(2), rowIndices.get(2).getRowIndex().
				get(0));

		resetContext();
		Assert.assertFalse("Incorrect default sort after handleRequest", table.isSorted());

		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".sort", "0");
		request.setParameter(table.getId() + ".sortDesc", "true");

		table.handleRequest(request);

		rowIndices = table.getRepeater().getBeanList();
		Assert.assertEquals("Incorrect sort", 3, rowIndices.size());
		Assert.assertEquals("Incorrect sort", Integer.valueOf(0), rowIndices.get(0).getRowIndex().
				get(0));
		Assert.assertEquals("Incorrect sort", Integer.valueOf(1), rowIndices.get(1).getRowIndex().
				get(0));
		Assert.assertEquals("Incorrect sort", Integer.valueOf(2), rowIndices.get(2).getRowIndex().
				get(0));
	}

	@Test
	public void testSortTable() {
		SimpleTableModel model = new SimpleTableModel(new String[][]{{"1"}, {"3"}, {"2"}});
		model.setComparator(0, SimpleTableModel.COMPARABLE_COMPARATOR);

		WTable table = new WTable();
		table.setSelectMode(WTable.SelectMode.SINGLE);
		table.setTableModel(new AdapterBasicTableModel(model));
		table.addColumn(new WTableColumn("dummy", WText.class));

		table.setLocked(true);
		setActiveContext(createUIContext());

		// Sort table manually
		table.sort(0, true);

		Assert.assertTrue("Incorrect selection after sort method", table.isSorted());
		Assert.
				assertEquals("Incorrect sort column after sort method", 0, table.
						getSortColumnIndex());
		Assert.assertTrue("Incorrect sort direction after sort method", table.isSortAscending());

		List<RowIdWrapper> rowIndices = table.getRepeater().getBeanList();
		Assert.assertEquals("Incorrect sort", 3, rowIndices.size());
		Assert.assertEquals("Incorrect sort", Integer.valueOf(0), rowIndices.get(0).getRowIndex().
				get(0));
		Assert.assertEquals("Incorrect sort", Integer.valueOf(2), rowIndices.get(1).getRowIndex().
				get(0));
		Assert.assertEquals("Incorrect sort", Integer.valueOf(1), rowIndices.get(2).getRowIndex().
				get(0));
	}

	/**
	 * Test setShowColumnHeaders.
	 */
	@Test
	public void testSetShowColumnHeaders() {
		final boolean showColumnHeaders = false;
		WTable table = new WTable();

		table.setShowColumnHeaders(showColumnHeaders);

		Assert.assertEquals("should return showColumnHeaders set", showColumnHeaders, table.
				isShowColumnHeaders());
	}

	@Test
	public void testColumnOrderAccessors() {
		WTable table = new WTable();
		table.addColumn(new WTableColumn("col1", WText.class));
		table.addColumn(new WTableColumn("col2", WText.class));

		int[] order1 = new int[]{0, 1};
		int[] order2 = new int[]{1};
		assertAccessorsCorrect(table, "columnOrder", null, order1, order2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetColumnOrderTooHigh() {
		WTable table = new WTable();
		table.addColumn(new WTableColumn("col1", WText.class));
		table.setColumnOrder(new int[]{1, 2});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnOrderInvalidColumnIndex() {
		WTable table = new WTable();
		table.addColumn(new WTableColumn("col1", WText.class));
		table.setColumnOrder(new int[]{1});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnOrderEmpty() {
		WTable table = new WTable();
		table.addColumn(new WTableColumn("col1", WText.class));
		table.setColumnOrder(new int[]{});
	}

	/**
	 * Test setRowsPerPage - required rowsPerPage too low.
	 */
	@Test
	public void testSetRowsPerPage() {
		final int rowsPerPage = 0;
		WTable table = new WTable();

		try {
			table.setRowsPerPage(rowsPerPage);
			Assert.fail("should throw IlegalArgumentException");
		} catch (Exception e) {
			String expectedMessage = SET_ROWS_PER_PAGE_ERROR_MESSAGE + rowsPerPage;
			Assert.assertEquals("should get messag expected", expectedMessage, e.getMessage());
		}
	}

	/**
	 * Test getCurrentPage - set back to maxPage.
	 */
	@Test
	public void testGetCurrentPage() {
		final int currentPage = 12;
		WTable table = new WTable();

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setCurrentPage(currentPage);
		Assert.assertEquals("Too high currentPage should be set back to maxPage", 0, table.
				getCurrentPage());

		resetContext();
		Assert.assertEquals("Incorrect default page", 0, table.getCurrentPage());
	}

	/**
	 * Test setCurrentPage - rejects negative values.
	 */
	@Test
	public void testSetCurrentPage() {
		final int currentPage = -1;
		WTable table = new WTable();

		try {
			table.setCurrentPage(currentPage);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("should get expected message", SET_CURRENT_PAGE_ERROR_MESSAGE, e.
					getMessage());
		}
	}

	/**
	 * Test setSortMode.
	 */
	@Test
	public void testSetSortMode() {
		WTable table = new WTable();
		table.setSortMode(WTable.SortMode.DYNAMIC);

		Assert.assertEquals("should return SortMode set", WTable.SortMode.DYNAMIC, table.
				getSortMode());
	}

	/**
	 * Test getCurrentPage - when set beyond maxPage set back to it.
	 */
	@Test
	public void testGetCurrentPageTooHigh() {
		final int currentPage = 42;

		WTable table = new WTable();
		table.setLocked(true);
		setActiveContext(createUIContext());

		Assert.assertEquals("current page should be 0", 0, table.getCurrentPage());
		table.setCurrentPage(currentPage);
		Assert.assertEquals("current page should still be 0", 0, table.getCurrentPage());
	}

	/**
	 * Test setSelectedRows - null input.
	 */
	@Test
	public void testSetSelectedRowsNull() {
		WTable table = new WTable();

		Set<Object> selectedRows = new HashSet<Object>(Arrays.asList(Arrays.asList(2), Arrays.
				asList(3)));

		table.setSelectedRows(null);

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSelectedRows(selectedRows);

		Assert.assertTrue("Incorrect selected rows", table.getSelectedRows().size() == 2);

		resetContext();
		Assert.assertTrue("Default selected rows should be empty",
				table.getSelectedRows().size() == 0);
	}

	/**
	 * Test setSort.
	 */
	@Test
	public void testSetSort() {
		final int index = 2;
		final boolean ascending = false;

		final int indexAlt = 4;
		final boolean ascendingAlt = true;

		WTable table = new WTable();
		table.setSort(index, ascending);
		table.setLocked(true);

		Assert.assertEquals("should return colIndex set", index, table.getSortColumnIndex());
		Assert.assertEquals("should return ascending set", ascending, table.isSortAscending());
		Assert.assertTrue("should be set as sorted", table.isSorted());

		UIContext uic1 = new UIContextImpl();
		UIContext uic2 = new UIContextImpl();

		setActiveContext(uic1);
		table.setSort(index, ascending);

		setActiveContext(uic2);
		table.setSort(indexAlt, ascendingAlt);

		resetContext();
		Assert.assertEquals("should return colIndex set", index, table.getSortColumnIndex());
		Assert.assertEquals("should return ascending set", ascending, table.isSortAscending());
		Assert.assertTrue("should be set as sorted", table.isSorted());

		setActiveContext(uic1);
		Assert.assertEquals("should return colIndex set", index, table.getSortColumnIndex());
		Assert.assertEquals("should return ascending set", ascending, table.isSortAscending());
		Assert.assertTrue("should be set as sorted", table.isSorted());

		setActiveContext(uic2);
		Assert.assertEquals("should return colIndexAlt set", indexAlt, table.getSortColumnIndex());
		Assert.assertEquals("should return ascendingAlt set", ascendingAlt, table.isSortAscending());
		Assert.assertTrue("should be set as sorted", table.isSorted());
	}

	/**
	 * Test addAction.
	 */
	@Test
	public void testAddAction() {
		WTable table = new WTable();
		WButton button = new WButton();
		table.addAction(button);

		table.setLocked(true);
		setActiveContext(createUIContext());
		List<WButton> actions = table.getActions();
		Assert.assertEquals("should get action added", button, actions.get(0));

		Assert.assertEquals("should be 1 item in list", 1, table.getActions().size());
		Assert.assertEquals("should get action added in list", button, table.getActions().get(0));

		// Add action to user context
		WButton button2 = new WButton();
		table.addAction(button2);

		Assert.assertEquals("should be 2 items in list with uic", 2, table.getActions().size());
		Assert.assertEquals("should get action added in list with uic", button, table.getActions().
				get(0));
		Assert.assertEquals("should get second action added in list with uic", button2, table.
				getActions().get(1));

		// Default context should be unaffected.
		resetContext();
		Assert.assertEquals("should be 1 item in list", 1, table.getActions().size());
		Assert.assertEquals("should get action added in list", button, table.getActions().get(0));
	}

	/**
	 * Test addActionContraint - Assert.fails when button not in table.
	 */
	@Test
	public void testAddActionConstraintFail() {
		WTable table = new WTable();
		WButton button = new WButton();
		WTable.ActionConstraint constraint = new WTable.ActionConstraint(2, 4, true, "mock");

		try {
			table.addActionConstraint(button, constraint);
			Assert.fail("should throw an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("should get expected message", ACTION_CONSTRAINT_ERROR_MESSAGE, e.
					getMessage());
		}
	}

	/**
	 * Test addActionContraint - uic, Assert.fails when button not in table.
	 */
	@Test
	public void testAddActionConstraintUicFail() {
		WTable table = new WTable();
		WButton button = new WButton();
		WTable.ActionConstraint constraint = new WTable.ActionConstraint(2, 4, true, "mock");

		try {
			table.addActionConstraint(button, constraint);
			Assert.fail("should throw an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("should get expected message", ACTION_CONSTRAINT_ERROR_MESSAGE, e.
					getMessage());
		}
	}

	/**
	 * Test addActionConstraint - succeeds.
	 */
	@Test
	public void testAddActionConstraintSucceed() {
		final int minRowSelected = 2;
		final int maxRowSelected = 4;
		final boolean error = false;
		final String message = "this is yet another test message";

		final int minRowSelectedAlt = 6;
		final int maxRowSelectedAlt = 8;
		final boolean errorAlt = true;
		final String messageAlt = "this is yet another test message but different";

		WTable table = new WTable();
		WButton button = new WButton();
		table.addAction(button);

		// add constraint to shared
		WTable.ActionConstraint constraint1 = new WTable.ActionConstraint(minRowSelected,
				maxRowSelected, error,
				message);
		table.addActionConstraint(button, constraint1);

		// add constraint to uic
		WTable.ActionConstraint constraint2 = new WTable.ActionConstraint(minRowSelectedAlt,
				maxRowSelectedAlt,
				errorAlt, messageAlt);

		setActiveContext(createUIContext());
		table.addActionConstraint(button, constraint2);

		List<WTable.ActionConstraint> constraints = table.getActionConstraints(button);
		Assert.assertNotNull("constraints should not be null", constraints);
		Assert.assertEquals("there should be two constraints", 2, constraints.size());
		Assert.assertTrue("constraint1 should be one of the constraints", constraints.contains(
				constraint1));
		Assert.assertTrue("constraint2 should be one of the constraints", constraints.contains(
				constraint2));
	}

	/**
	 * Test setters in WTreeTable.ActionConstraint class.
	 */
	@Test
	public void testActionConstraintSetters() {
		final int minSelectedRowCount = 2;
		final int maxSelectedRowCount = 7;
		final boolean error = false;
		final String message = "this is a test message";

		final int minSelectedRowCountAlt = 3;
		final int maxSelectedRowCountAlt = 8;
		final boolean errorAlt = true;
		final String messageAlt = "this is a different test message";

		WTable.ActionConstraint constraint = new WTable.ActionConstraint(minSelectedRowCount,
				maxSelectedRowCount,
				error, message);

		Assert.assertEquals("should return min count set in constructor", minSelectedRowCount,
				constraint.getMinSelectedRowCount());
		Assert.assertEquals("should return max count set in constructor", maxSelectedRowCount,
				constraint.getMaxSelectedRowCount());
		Assert.assertEquals("should return error set in constructor", error, constraint.isError());
		Assert.assertEquals("should return message set in constructor", message, constraint.
				getMessage());

		constraint.setMinSelectedRowCount(minSelectedRowCountAlt);
		constraint.setMaxSelectedRowCount(maxSelectedRowCountAlt);
		constraint.setError(errorAlt);
		constraint.setMessage(messageAlt);

		Assert.assertEquals("should return min count set", minSelectedRowCountAlt, constraint.
				getMinSelectedRowCount());
		Assert.assertEquals("should return max count set", maxSelectedRowCountAlt, constraint.
				getMaxSelectedRowCount());
		Assert.assertEquals("should return error set", errorAlt, constraint.isError());
		Assert.assertEquals("should return message set", messageAlt, constraint.getMessage());
	}

	@Test
	public void testTableDefaultIds() {
		WNamingContext context = new WNamingContext("TEST");

		WTable table = new WTable();
		table.setTableModel(new AdapterBasicTableModel(new SimpleTableModel(
				new String[][]{{"1"}, {"3"}, {"2"}})));
		WComponent repeated = new WBeanComponent();
		table.addColumn(new WTableColumn("dummy", repeated));

		context.add(table);

		context.setLocked(true);
		setActiveContext(createUIContext());

		String prefix = "TEST" + WComponent.ID_CONTEXT_SEPERATOR + WComponent.ID_FRAMEWORK_ASSIGNED_SEPERATOR;

		// Table ID
		Assert.assertEquals("Incorrect default id for table", prefix + "0", table.getId());
		String tableId = table.getId();

		// Repeater ID
		Assert.assertEquals("Incorrect default id for table repeater", tableId + "-row", table.
				getRepeater().getId());

		// Repeater repeat root ID
		Assert.assertEquals("Incorrect default id for table repeater root", tableId + "-row-r",
				table.getRepeater()
				.getRepeatRoot().getId());

		String rowPrefix = table.getRepeater().getRepeatRoot().getId();
		// Allow for WTableColumn between repeated component
		String rowSuffix = "_0a0";

		// Row IDs
		for (UIContext uic : table.getRepeater().getRowContexts()) {
			// Id has uic row render id in it
			int row = ((SubUIContext) uic).getContextId();
			String idPrefix = rowPrefix + row + WComponent.ID_CONTEXT_SEPERATOR + rowSuffix;
			try {
				UIContextHolder.pushContext(uic);
				Assert.assertEquals("Incorrect default id for repeated component", idPrefix,
						repeated.getId());
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	@Test
	public void testTableIdsWithIdnames() {
		WNamingContext context = new WNamingContext("TEST");

		WTable table = new WTable();
		table.setTableModel(new AdapterBasicTableModel(new SimpleTableModel(
				new String[][]{{"1"}, {"3"}, {"2"}})));
		WComponent repeated = new WBeanComponent();
		table.addColumn(new WTableColumn("dummy", repeated));

		context.add(table);

		table.setIdName("T");
		repeated.setIdName("X");

		context.setLocked(true);
		setActiveContext(new UIContextImpl());

		// Table ID
		Assert.assertEquals("Incorrect id for table with idname",
				"TEST" + WComponent.ID_CONTEXT_SEPERATOR + "T",
				table.getId());
		String tableId = table.getId();

		// Table repeater id
		Assert.assertEquals("Incorrect id for table repeater", tableId + "-row",
				table.getRepeater().getId());

		// Table repeater root id
		Assert.assertEquals("Incorrect id for table repeater root", tableId + "-row-r", table.
				getRepeater()
				.getRepeatRoot().getId());

		String rowPrefix = table.getRepeater().getRepeatRoot().getId();

		// Row IDs
		for (UIContext uic : table.getRepeater().getRowContexts()) {
			// Id has uic row render id in it
			int row = ((SubUIContext) uic).getContextId();
			String repeatedId = rowPrefix + row + WComponent.ID_CONTEXT_SEPERATOR + "X";
			try {
				UIContextHolder.pushContext(uic);
				Assert.assertEquals("Incorrect id for repeated component with idname", repeatedId,
						repeated.getId());
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	@Test
	public void testTableInternalIds() {
		WNamingContext context = new WNamingContext("TEST");

		WTable table = new WTable();
		table.setTableModel(new AdapterBasicTableModel(new SimpleTableModel(
				new String[][]{{"1"}, {"3"}, {"2"}})));
		WComponent repeated = new WBeanComponent();
		table.addColumn(new WTableColumn("dummy", repeated));

		context.add(table);

		context.setLocked(true);
		setActiveContext(new UIContextImpl());

		// Table ID
		Assert.assertEquals("Incorrect internal id for table", WComponent.DEFAULT_INTERNAL_ID + "0",
				table.getInternalId());
		String tableId = table.getInternalId();

		// Table Repeater ID
		Assert.assertEquals("Incorrect internal id for table repeater", tableId + "b", table.
				getRepeater()
				.getInternalId());

		// Table Repeater root ID
		Assert.assertEquals("Incorrect internal id for table repeater root", tableId + "br", table.
				getRepeater()
				.getRepeatRoot().getInternalId());

		String rowPrefix = table.getRepeater().getRepeatRoot().getInternalId();
		// Allow for WTableColumn between repeated component
		String rowSuffix = "a0a";

		// Row IDs
		for (UIContext uic : table.getRepeater().getRowContexts()) {
			// Id has uic row render id in it
			int row = ((SubUIContext) uic).getContextId();
			String repeatedId = rowPrefix + row + rowSuffix;
			try {
				UIContextHolder.pushContext(uic);
				Assert.assertEquals("Incorrect internal id for repeated component", repeatedId,
						repeated.getInternalId());
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	@Test
	public void testNamingContextAccessors() {
		assertAccessorsCorrect(new WTable(), "namingContext", false, true, false);
	}

	@Test
	public void testNamingContextIdAccessor() {
		String id = "test";
		NamingContextable naming = new WTable();
		naming.setIdName(id);
		Assert.assertEquals("Incorrect component id", id, naming.getId());
		Assert.assertEquals("Naming context should match component id", id, naming.
				getNamingContextId());
	}

	/**
	 * @param data the test data
	 * @return the table model
	 */
	private TableModel createModel(final Serializable[][] data) {
		SimpleTableModel model = new SimpleTableModel(data);
		return new AdapterBasicTableModel(model);
	}

}
