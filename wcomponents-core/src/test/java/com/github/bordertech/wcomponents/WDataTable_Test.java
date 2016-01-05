package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WDataTable.PaginationMode;
import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Junit test case for {@link WDataTable}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class WDataTable_Test extends AbstractWComponentTestCase {

	/**
	 * error message expected for testAddActionConstraint - private in class being tested.
	 */
	private static final String ACTION_CONSTRAINT_ERROR_MESSAGE = "Can only add a constraint to a button which is in this table's actions";

	/**
	 * error message expected for testSetColumnOrderTooHigh - private in class being tested.
	 */
	private static final String SET_COLUMN_ORDER_ERROR_MESSAGE = "Number of column order indices must match the number of table columns";

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
		WDataTable table = new WDataTable();
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
	public void testSetDisabled() {
		WDataTable table = new WDataTable();

		table.setLocked(true);
		setActiveContext(createUIContext());

		table.setDisabled(true);
		Assert.assertTrue("Should be disabled for modified session", table.isDisabled());

		resetContext();
		Assert.assertFalse("Table should not be disabled by default", table.isDisabled());
	}

	@Test
	public void testDataModelAccessors() {
		WDataTable table = new WDataTable();
		TableDataModel model1 = new SimpleTableDataModel(new String[0][0]);
		TableDataModel model2 = new SimpleTableDataModel(new String[0][0]);

		table.setDataModel(model1);
		Assert.assertSame("Incorrect default data model", model1, table.getDataModel());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setDataModel(model2);
		Assert.assertSame("Incorrect data model for modified session", model2, table.getDataModel());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect data model for other sessions", model1, table.getDataModel());
	}

	@Test
	public void testSeparatorTypeAccessors() {
		WDataTable table = new WDataTable();
		WDataTable.SeparatorType type1 = WDataTable.SeparatorType.HORIZONTAL;
		WDataTable.SeparatorType type2 = WDataTable.SeparatorType.VERTICAL;

		Assert.assertEquals("Table should not have a separator by default",
				WDataTable.SeparatorType.NONE, table.getSeparatorType());

		table.setSeparatorType(type1);
		Assert.assertSame("Incorrect default separator", type1, table.getSeparatorType());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSeparatorType(type2);
		Assert.assertSame("Incorrect separator for modified session", type2, table.
				getSeparatorType());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect separator for other sessions", type1, table.getSeparatorType());
	}

	@Test
	public void testStripingTypeAccessors() {
		WDataTable table = new WDataTable();
		table.setLocked(true);
		WDataTable.StripingType type1 = WDataTable.StripingType.ROWS;
		WDataTable.StripingType type2 = WDataTable.StripingType.COLUMNS;

		Assert.assertEquals("Table should not have striping by default",
				WDataTable.StripingType.NONE, table.getStripingType());

		table.setStripingType(type1);
		Assert.assertSame("Incorrect default striping type", type1, table.getStripingType());

		setActiveContext(createUIContext());
		table.setStripingType(type2);
		Assert.assertSame("Incorrect striping type for modified session", type2, table.
				getStripingType());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect striping type for other sessions", type1, table.
				getStripingType());
	}

	@Test
	public void testExpandAllTypeAccessors() {
		WDataTable table = new WDataTable();

		Assert.assertEquals("Incorrect default expand all", false, table.isExpandAll());

		table.setExpandAll(true);
		Assert.assertSame("Incorrect default expand all", true, table.isExpandAll());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setExpandAll(false);
		Assert.assertSame("Incorrect expand all for modified session", false, table.isExpandAll());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect expand all for other sessions", true, table.isExpandAll());
	}

	@Test
	public void testExpandModeAccessors() {
		WDataTable table = new WDataTable();
		WDataTable.ExpandMode type1 = WDataTable.ExpandMode.SERVER;
		WDataTable.ExpandMode type2 = WDataTable.ExpandMode.DYNAMIC;

		Assert.assertEquals("Incorrect default select mode", WDataTable.ExpandMode.NONE, table.
				getExpandMode());

		table.setExpandMode(type1);
		Assert.assertSame("Incorrect default select mode", type1, table.getExpandMode());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setExpandMode(type2);
		Assert.
				assertSame("Incorrect select mode for modified session", type2, table.
						getExpandMode());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect select mode for other sessions", type1, table.getExpandMode());
	}

	@Test
	public void testPaginationModeAccessors() {
		WDataTable table = new WDataTable();
		WDataTable.PaginationMode mode1 = WDataTable.PaginationMode.CLIENT;
		WDataTable.PaginationMode mode2 = WDataTable.PaginationMode.DYNAMIC;

		Assert.assertEquals("Incorrect default pagination mode", WDataTable.PaginationMode.NONE,
				table.getPaginationMode());

		table.setPaginationMode(mode1);
		Assert.assertSame("Incorrect default pagination mode", mode1, table.getPaginationMode());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setPaginationMode(mode2);
		Assert.assertSame("Incorrect pagination mode for modified session", mode2, table.
				getPaginationMode());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect pagination mode for other sessions", mode1, table.
				getPaginationMode());
	}

	@Test
	public void testSortModeAccessors() {
		WDataTable table = new WDataTable();
		WDataTable.SortMode type1 = WDataTable.SortMode.SERVER;
		WDataTable.SortMode type2 = WDataTable.SortMode.DYNAMIC;

		Assert.assertEquals("Incorrect default select mode", type1, table.getSortMode());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSortMode(type2);
		Assert.assertSame("Incorrect select mode for modified session", type2, table.getSortMode());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect select mode for other sessions", type1, table.getSortMode());
	}

	@Test
	public void testSelectModeAccessors() {
		WDataTable table = new WDataTable();
		WDataTable.SelectMode type1 = WDataTable.SelectMode.SINGLE;
		WDataTable.SelectMode type2 = WDataTable.SelectMode.MULTIPLE;

		Assert.assertEquals("Incorrect default select mode", WDataTable.SelectMode.NONE, table.
				getSelectMode());

		table.setSelectMode(type1);
		Assert.assertSame("Incorrect default select  mode", type1, table.getSelectMode());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSelectMode(type2);
		Assert.
				assertSame("Incorrect select mode for modified session", type2, table.
						getSelectMode());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect select mode for other sessions", type1, table.getSelectMode());
	}

	@Test
	public void testSelectAllTypeAccessors() {
		WDataTable table = new WDataTable();
		WDataTable.SelectAllType type1 = WDataTable.SelectAllType.TEXT;
		WDataTable.SelectAllType type2 = WDataTable.SelectAllType.CONTROL;

		Assert.assertEquals("Incorrect default select all mode", WDataTable.SelectAllType.TEXT,
				table.getSelectAllMode());

		table.setSelectAllMode(type1);
		Assert.assertSame("Incorrect default select all mode", type1, table.getSelectAllMode());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSelectAllMode(type2);
		Assert.assertSame("Incorrect select all mode for modified session", type2, table.
				getSelectAllMode());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect select all mode for other sessions", type1, table.
				getSelectAllMode());
	}

	@Test
	public void testShowRowHeadersAccessors() {
		WDataTable table = new WDataTable();

		Assert.assertFalse("Row headers should not be visible by default", table.isShowRowHeaders());

		table.setShowRowHeaders(true);
		Assert.assertTrue("Incorrect default show row headers", table.isShowRowHeaders());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setShowRowHeaders(false);
		Assert.assertFalse("Incorrect show row headers for modified session", table.
				isShowRowHeaders());

		setActiveContext(createUIContext());
		Assert.assertTrue("Incorrect show row headers for other sessions", table.isShowRowHeaders());
	}

	@Test
	public void testShowRowIndicesAccessors() {
		WDataTable table = new WDataTable();

		Assert.assertFalse("Row indices should not be visible by default", table.isShowRowIndices());

		table.setShowRowIndices(true);
		Assert.assertTrue("Incorrect default show row indices", table.isShowRowIndices());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setShowRowIndices(false);
		Assert.assertFalse("Incorrect show row indices for modified session", table.
				isShowRowIndices());

		setActiveContext(createUIContext());
		Assert.assertTrue("Incorrect show row indices for other sessions", table.isShowRowIndices());
	}

	@Test
	public void testSubmitOnRowSelectAccessors() {
		WDataTable table = new WDataTable();

		Assert.
				assertFalse("Should not submit on row select by default", table.
						isSubmitOnRowSelect());

		table.setSubmitOnRowSelect(true);
		Assert.assertTrue("Incorrect default submit on row select", table.isSubmitOnRowSelect());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSubmitOnRowSelect(false);
		Assert.assertFalse("Incorrect submit on row select for modified session", table.
				isSubmitOnRowSelect());

		setActiveContext(createUIContext());
		Assert.assertTrue("Incorrect submit on row select for other sessions", table.
				isSubmitOnRowSelect());
	}

	@Test
	public void testSummaryAccessors() {
		WDataTable table = new WDataTable();
		String summary1 = "testSummaryAccessors.summary1";
		String summary2 = "testSummaryAccessors.summary2";

		table.setSummary(summary1);
		Assert.assertSame("Incorrect default summary", summary1, table.getSummary());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSummary(summary2);
		Assert.assertSame("Incorrect summary for modified session", summary2, table.getSummary());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect summary for other sessions", summary1, table.getSummary());
	}

	@Test
	public void testCaptionAccessors() {
		WDataTable table = new WDataTable();
		String caption1 = "testCaptionAccessors.caption1";
		String caption2 = "testCaptionAccessors.caption2";

		table.setCaption(caption1);
		Assert.assertSame("Incorrect default caption", caption1, table.getCaption());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setCaption(caption2);
		Assert.assertSame("Incorrect caption for modified session", caption2, table.getCaption());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect caption for other sessions", caption1, table.getCaption());
	}

	@Test
	public void testFilterableAccessors() {
		WDataTable table = new WDataTable();

		Assert.assertFalse("Should not be filterable by default", table.isFilterable());

		table.setFilterable(true);
		Assert.assertTrue("Incorrect default filterable", table.isFilterable());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setFilterable(false);
		Assert.assertFalse("Incorrect filterable for modified session", table.isFilterable());

		setActiveContext(createUIContext());
		Assert.assertTrue("Incorrect filterable for other sessions", table.isFilterable());
	}

	@Test
	public void testNoDataMessageAccessors() {
		WDataTable table = new WDataTable();
		String noDataMessage1 = "testNoDataMessageAccessors.noDataMessage1";
		String noDataMessage2 = "testNoDataMessageAccessors.noDataMessage2";

		table.setNoDataMessage(noDataMessage1);
		Assert.assertSame("Incorrect default 'no data' message", noDataMessage1, table.
				getNoDataMessage());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setNoDataMessage(noDataMessage2);
		Assert.assertSame("Incorrect 'no data' message for modified session", noDataMessage2, table.
				getNoDataMessage());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect 'no data' message for other sessions", noDataMessage1, table.
				getNoDataMessage());
	}

	@Test
	public void testColumnOrderAccessors() {
		WDataTable table = new WDataTable();
		table.addColumn(new WTableColumn("col1", WText.class));
		table.addColumn(new WTableColumn("col2", WText.class));

		int[] order1 = new int[]{0, 1};
		int[] order2 = new int[]{1, 0};

		Assert.
				assertNull("Should not have a custom column order by default", table.
						getColumnOrder());

		table.setColumnOrder(order1);
		Assert.assertSame("Incorrect default column order", order1, table.getColumnOrder());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setColumnOrder(order2);
		Assert.assertSame("Incorrect column order for modified session", order2, table.
				getColumnOrder());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect column order for other sessions", order1, table.
				getColumnOrder());
	}

	@Test
	public void testSelectionChangeActionAccessors() {
		WDataTable table = new WDataTable();
		table.addColumn(new WTableColumn("col1", WText.class));
		table.addColumn(new WTableColumn("col2", WText.class));

		Action action1 = new TestAction();
		Action action2 = new TestAction();

		Assert.assertNull("Should not have a custom action by default", table.
				getSelectionChangeAction());

		table.setSelectionChangeAction(action1);
		Assert.assertSame("Incorrect default action", action1, table.getSelectionChangeAction());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSelectionChangeAction(action2);
		Assert.assertSame("Incorrect action for modified session", action2, table.
				getSelectionChangeAction());

		setActiveContext(createUIContext());
		Assert.assertSame("Incorrect action for other sessions", action1, table.
				getSelectionChangeAction());
	}

	@Test
	public void testRowsPerPageAccessors() {
		WDataTable table = new WDataTable();
		int numRows1 = 5;
		int numRows2 = 7;

		table.setRowsPerPage(numRows1);
		Assert.assertSame("Incorrect default rows per page", numRows1, table.getRowsPerPage());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setRowsPerPage(numRows2);
		Assert.assertEquals("Incorrect rows per page for modified session", numRows2, table.
				getRowsPerPage());

		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect rows per page for other sessions", numRows1, table.
				getRowsPerPage());

		// try an invalid # of rows per page
		try {
			table.setRowsPerPage(-123);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should have a message", expected.getMessage());
			Assert.assertEquals("Rows per page should not have been modified", numRows1, table.
					getRowsPerPage());
		}
	}

	@Test
	public void testCurrentPageAccessors() {
		WDataTable table = new WDataTable();
		table.setDataModel(new SimpleTableDataModel(new String[100][1]));
		table.setRowsPerPage(10);
		int page1 = 5;
		int page2 = 7;

		table.setCurrentPage(page1);
		Assert.assertSame("Incorrect default rows per page", page1, table.getCurrentPage());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setCurrentPage(page2);
		Assert.assertEquals("Incorrect rows per page for modified session", page2, table.
				getCurrentPage());

		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect rows per page for other sessions", page1, table.
				getCurrentPage());

		// try an invalid page
		try {
			table.setCurrentPage(-123);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should have a message", expected.getMessage());
			Assert.assertEquals("Current page should not have been modified", page1, table.
					getCurrentPage());
		}

		// try an OOB page - should be max page
		table.setCurrentPage(123);
		Assert.assertEquals("Incorrect rows per page for invalid page number", 9, table.
				getCurrentPage());
	}

	@Test
	public void testSelectedRowsAccessors() {
		WDataTable table = new WDataTable();
		table.setDataModel(new SimpleTableDataModel(new String[10][1]));

		List<Integer> rows1 = Arrays.asList(new Integer[]{0, 1, 2});
		List<Integer> rows2 = Arrays.asList(new Integer[]{3, 4, 5});

		Assert.assertTrue("Should not have selected rows by default", table.getSelectedRows().
				isEmpty());

		table.setSelectedRows(rows1);
		Assert.assertEquals("Incorrect default selected rows", rows1, table.getSelectedRows());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSelectedRows(rows2);
		Assert.assertEquals("Incorrect selected rows for modified session", rows2, table.
				getSelectedRows());

		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect selected rows for other sessions", rows1, table.
				getSelectedRows());
	}

	@Test
	public void testExpandedRowsAccessors() {
		WDataTable table = new WDataTable();
		table.setDataModel(new SimpleTableDataModel(new String[10][1]));

		List<Integer> rows1 = Arrays.asList(new Integer[]{0, 1, 2});
		List<Integer> rows2 = Arrays.asList(new Integer[]{3, 4, 5});

		Assert.assertTrue("Should not have expanded rows by default", table.getExpandedRows().
				isEmpty());

		table.setExpandedRows(rows1);
		Assert.assertEquals("Incorrect default expanded rows", rows1, table.getExpandedRows());

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setExpandedRows(rows2);
		Assert.assertEquals("Incorrect expanded rows for modified session", rows2, table.
				getExpandedRows());

		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect expanded rows for other sessions", rows1, table.
				getExpandedRows());
	}

	@Test
	public void testHandlePaginationRequest() {
		WDataTable table = new WDataTable();
		table.setPaginationMode(WDataTable.PaginationMode.DYNAMIC);
		table.setDataModel(new SimpleTableDataModel(new String[100][1]));
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
	public void testHandleExpansionRequest() {
		WDataTable table = new WDataTable();
		table.setExpandMode(WDataTable.ExpandMode.DYNAMIC);
		table.setDataModel(new SimpleTableDataModel(new String[100][1]));

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".expanded", new String[]{"5", "6", "7"});

		table.handleRequest(request);
		Assert.assertEquals("Incorrect expanded rows after handleRequest", Arrays.asList(
				new Integer[]{5, 6, 7}), table.getExpandedRows());

		resetContext();
		Assert.assertTrue("Incorrect default expanded rows after handleRequest", table.
				getExpandedRows().isEmpty());

		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");

		table.handleRequest(request);
		Assert.assertTrue("Incorrect expanded rows after handleRequest with no expansions", table.
				getExpandedRows().isEmpty());

		resetContext();
		Assert.assertTrue("Incorrect default expanded rows after handleRequest with no expansions",
				table.getExpandedRows().isEmpty());
	}

	@Test
	public void testSingleHandleSelectionRequest() {
		WDataTable table = new WDataTable();
		table.setSelectMode(WDataTable.SelectMode.SINGLE);
		table.setDataModel(new SimpleTableDataModel(new String[100][1]));

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".selected", new String[]{"5", "6", "7"});

		table.handleRequest(request);
		Assert.assertEquals("Should only have selected the first item after handleRequest", Arrays.
				asList(new Integer[]{5}), table.getSelectedRows());

		resetContext();
		Assert.assertTrue("Incorrect default selection after handleRequest",
				table.getSelectedRows().isEmpty());
	}

	@Test
	public void testMultipleHandleSelectionRequest() {
		WDataTable table = new WDataTable();
		table.setSelectMode(WDataTable.SelectMode.MULTIPLE);
		table.setDataModel(new SimpleTableDataModel(new String[100][1]));

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".selected", new String[]{"5", "6", "7"});

		table.handleRequest(request);
		Assert.assertEquals("Incorrect selection after handleRequest", Arrays.asList(
				new Integer[]{5, 6, 7}), table.getSelectedRows());

		resetContext();
		Assert.assertTrue("Incorrect default selection after handleRequest",
				table.getSelectedRows().isEmpty());

		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");

		table.handleRequest(request);
		Assert.assertTrue("Incorrect selection after handleRequest with no selection set", table.
				getSelectedRows().isEmpty());

		resetContext();
		Assert.assertTrue("Incorrect default selection after handleRequest with no selection set",
				table.getSelectedRows().isEmpty());
	}

	@Test
	public void testMultipleHandleSelectionSortedDataRequest() {
		WDataTable table = new WDataTable();
		table.setSelectMode(WDataTable.SelectMode.MULTIPLE);
		table.setPaginationMode(PaginationMode.DYNAMIC);
		table.setRowsPerPage(3);

		SimpleBeanBoundTableDataModel model = new SimpleBeanBoundTableDataModel(new String[]{"."});
		model.setComparator(0, SimpleBeanBoundTableDataModel.COMPARABLE_COMPARATOR);

		table.setDataModel(model);

		table.setLocked(true);
		setActiveContext(createUIContext());

		// Set Bean
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			list.add("A" + i);
		}
		table.setBean(list);

		// Select 2nd item
		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".selected", new String[]{"1"});

		table.handleRequest(request);
		Assert.assertEquals("Incorrect selection after handleRequest", Arrays.asList(
				new Integer[]{1}), table.getSelectedRows());

		// Sort table
		table.sort(0, false);

		// Select new 2nd item (after sort)
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".selected", new String[]{"8"});

		table.handleRequest(request);
		Assert.assertEquals("Incorrect selection after handleRequest", Arrays.asList(
				new Integer[]{1, 8}), table.getSelectedRows());

		// Select 1st item (after sort)
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".selected", new String[]{"9"});

		table.handleRequest(request);
		Assert.assertEquals("Incorrect selection after handleRequest", Arrays.asList(
				new Integer[]{1, 9}), table.getSelectedRows());

		resetContext();
		Assert.assertTrue("Incorrect default selection after handleRequest with no selection set",
				table.getSelectedRows().isEmpty());
	}

	@Test
	public void testHandleFilterRequest() {
		String[] filters = new String[]{"filterA", "filterB", "filterC"};
		WDataTable table = new WDataTable();
		table.setFilterable(true);
		table.setDataModel(new SimpleTableDataModel(new String[100][1]));

		table.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".filters", filters);

		table.handleRequest(request);
		Assert.assertEquals("Incorrect selection after handleRequest", Arrays.asList(filters),
				table.getActiveFilters());

		resetContext();
		Assert.assertTrue("Incorrect default filters after handleRequest", table.getActiveFilters().
				isEmpty());

		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");

		table.handleRequest(request);
		Assert.assertTrue("Incorrect filters after handleRequest with no filters set", table.
				getActiveFilters().isEmpty());

		resetContext();
		Assert.assertTrue("Incorrect default filters after handleRequest with no filters set",
				table.getActiveFilters().isEmpty());
	}

	@Test
	public void testHandleSortRequest() {
		SimpleTableDataModel model = new SimpleTableDataModel(new String[][]{{"1"}, {"3"}, {"2"}});
		model.setComparator(0, SimpleTableDataModel.COMPARABLE_COMPARATOR);

		WDataTable table = new WDataTable();
		table.setSelectMode(WDataTable.SelectMode.SINGLE);
		table.setDataModel(model);
		table.addColumn(new WTableColumn("dummy", WText.class));

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

		List rowIndices = table.getRepeater().getBeanList();
		Assert.assertEquals("Incorrect sort", Arrays.asList(new Integer[]{0, 2, 1}), rowIndices);

		resetContext();
		Assert.assertFalse("Incorrect default sort after handleRequest", table.isSorted());

		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(table.getId() + "-h", "x");
		request.setParameter(table.getId() + ".sort", "0");
		request.setParameter(table.getId() + ".sortDesc", "true");

		table.handleRequest(request);

		rowIndices = table.getRepeater().getBeanList();
		Assert.assertEquals("Incorrect sort", Arrays.asList(new Integer[]{1, 2, 0}), rowIndices);
	}

	@Test
	public void testSortTable() {
		SimpleTableDataModel model = new SimpleTableDataModel(new String[][]{{"1"}, {"3"}, {"2"}});
		model.setComparator(0, SimpleTableDataModel.COMPARABLE_COMPARATOR);

		WDataTable table = new WDataTable();
		table.setSelectMode(WDataTable.SelectMode.SINGLE);
		table.setDataModel(model);
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

		List<?> rowIndices = table.getRepeater().getBeanList();
		Assert.assertEquals("Incorrect sort", Arrays.asList(new Integer[]{0, 2, 1}), rowIndices);
	}

	/**
	 * Test setDisabledAll - shared, different uics.
	 */
	@Test
	public void testSetDisabledAll() {
		WDataTable table = new WDataTable();

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setDisabled(true);

		Assert.assertTrue("Should be disabled", table.isDisabled());

		resetContext();
		Assert.assertFalse("Should not be disabled by default", table.isDisabled());
	}

	/**
	 * Test setShowColumnHeaders.
	 */
	@Test
	public void testSetShowColumnHeaders() {
		final boolean showColumnHeaders = false;
		WDataTable table = new WDataTable();

		table.setShowColumnHeaders(showColumnHeaders);

		Assert.assertEquals("should return showColumnHeaders set", showColumnHeaders, table.
				isShowColumnHeaders());
	}

	/**
	 * Test setActiveFilters.
	 */
	@Test
	public void testSetActiveFilters() {
		WDataTable table = new WDataTable();
		List<String> activeFilters = Arrays.asList(new String[]{"flinstone", "rubble", "slate"});

		table.setActiveFilters(activeFilters);

		List<String> resultActiveFilters = table.getActiveFilters();
		Assert.assertNotNull("list of filters should not be null", resultActiveFilters);
		Assert.assertEquals("result same size as input", activeFilters.size(), resultActiveFilters.
				size());
		Assert.assertTrue("result contains all input elements", resultActiveFilters.containsAll(
				activeFilters));
	}

	/**
	 * Test setColumnOrder - too many.
	 */
	@Test
	public void testSetColumnOrderTooHigh() {
		WDataTable table = new WDataTable();
		int[] columnOrder = {1, 2, 3, 4, 5};

		try {
			table.setColumnOrder(columnOrder);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("should get expected message", SET_COLUMN_ORDER_ERROR_MESSAGE, e.
					getMessage());
		}
	}

	/**
	 * Test setRowsPerPage - required rowsPerPage too low.
	 */
	@Test
	public void testSetRowsPerPage() {
		final int rowsPerPage = 0;
		WDataTable table = new WDataTable();

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
		WDataTable table = new WDataTable();

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
		WDataTable table = new WDataTable();

		try {
			table.setCurrentPage(currentPage);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("should get expected message", SET_CURRENT_PAGE_ERROR_MESSAGE, e.
					getMessage());
		}
	}

	/**
	 * Test setSelectGroup.
	 */
	@Test
	public void testSetSelectGroup() {
		final String selectGroup = "group42";

		WDataTable table = new WDataTable();

		table.setLocked(true);
		setActiveContext(createUIContext());
		table.setSelectGroup(selectGroup);

		Assert.assertEquals("Incorrect selectGroup", selectGroup, table.getSelectGroup());

		resetContext();
		Assert.assertNull("Default selectGroup should be null", table.getSelectGroup());
	}

	/**
	 * Test setSortMode.
	 */
	@Test
	public void testSetSortMode() {
		WDataTable table = new WDataTable();
		table.setSortMode(WDataTable.SortMode.SERVER);

		Assert.assertEquals("should return SortMode set", WDataTable.SortMode.SERVER, table.
				getSortMode());
	}

	/**
	 * Test setColumnOrder - with too many columns.
	 */
	@Test
	public void testSetColumnOrderTooMany() {
		WDataTable table = new WDataTable();
		int[] columnOrder = {1, 2, 3};

		try {
			table.setColumnOrder(columnOrder);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("should get expected message", SET_COLUMN_ORDER_ERROR_MESSAGE, e.
					getMessage());
		}
	}

	/**
	 * Test getCurrentPage - when set beyond maxPage set back to it.
	 */
	@Test
	public void testGetCurrentPageTooHigh() {
		final int currentPage = 42;

		WDataTable table = new WDataTable();
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
		WDataTable table = new WDataTable();

		List<Integer> selectedRows = Arrays.asList(new Integer[]{Integer.valueOf(2), Integer.
			valueOf(3)});

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

		WDataTable table = new WDataTable();
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
		WDataTable table = new WDataTable();
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
		WDataTable table = new WDataTable();
		WButton button = new WButton();
		WDataTable.ActionConstraint constraint = new WDataTable.ActionConstraint(2, 4, true, "mock");

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
		WDataTable table = new WDataTable();
		WButton button = new WButton();
		WDataTable.ActionConstraint constraint = new WDataTable.ActionConstraint(2, 4, true, "mock");

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

		WDataTable table = new WDataTable();
		WButton button = new WButton();
		table.addAction(button);

		// add constraint to shared
		WDataTable.ActionConstraint constraint1 = new WDataTable.ActionConstraint(minRowSelected,
				maxRowSelected,
				error, message);
		table.addActionConstraint(button, constraint1);

		// add constraint to uic
		WDataTable.ActionConstraint constraint2 = new WDataTable.ActionConstraint(minRowSelectedAlt,
				maxRowSelectedAlt,
				errorAlt, messageAlt);

		setActiveContext(createUIContext());
		table.addActionConstraint(button, constraint2);

		List<WDataTable.ActionConstraint> constraints = table.getActionConstraints(button);
		Assert.assertNotNull("constraints should not be null", constraints);
		Assert.assertEquals("there should be two constraints", 2, constraints.size());
		Assert.assertTrue("constraint1 should be one of the constraints", constraints.contains(
				constraint1));
		Assert.assertTrue("constraint2 should be one of the constraints", constraints.contains(
				constraint2));
	}

	/**
	 * Test setters in WDataTable.ActionConstraint class.
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

		WDataTable.ActionConstraint constraint = new WDataTable.ActionConstraint(minSelectedRowCount,
				maxSelectedRowCount, error, message);

		Assert.assertEquals("should return min count set in constructor", minSelectedRowCount,
				constraint
				.getMinSelectedRowCount());
		Assert.assertEquals("should return max count set in constructor", maxSelectedRowCount,
				constraint
				.getMaxSelectedRowCount());
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

		WDataTable table = new WDataTable();
		table.setDataModel(new SimpleTableDataModel(new String[][]{{"1"}, {"3"}, {"2"}}));
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
		String rowSuffix = "_0b0";

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

		WDataTable table = new WDataTable();
		table.setDataModel(new SimpleTableDataModel(new String[][]{{"1"}, {"3"}, {"2"}}));
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

		WDataTable table = new WDataTable();
		table.setDataModel(new SimpleTableDataModel(new String[][]{{"1"}, {"3"}, {"2"}}));
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
		Assert.assertEquals("Incorrect internal id for table repeater", tableId + "c", table.
				getRepeater()
				.getInternalId());

		// Table Repeater root ID
		Assert.assertEquals("Incorrect internal id for table repeater root", tableId + "cr", table.
				getRepeater()
				.getRepeatRoot().getInternalId());

		String rowPrefix = table.getRepeater().getRepeatRoot().getInternalId();
		// Allow for WTableColumn between repeated component
		String rowSuffix = "a1a";

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
		assertAccessorsCorrect(new WDataTable(), "namingContext", false, true, false);
	}

	@Test
	public void testNamingContextIdAccessor() {
		String id = "test";
		NamingContextable naming = new WDataTable();
		naming.setIdName(id);
		Assert.assertEquals("Incorrect component id", id, naming.getId());
		Assert.assertEquals("Naming context should match component id", id, naming.
				getNamingContextId());
	}

}
