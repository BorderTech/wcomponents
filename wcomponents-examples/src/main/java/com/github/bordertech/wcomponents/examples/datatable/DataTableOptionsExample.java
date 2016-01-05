package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.AbstractTableDataModel;
import com.github.bordertech.wcomponents.AbstractTreeTableDataModel;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.TableTreeNode;
import com.github.bordertech.wcomponents.TreeTableDataModel;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.util.AbstractComparator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.beanutils.BeanComparator;

/**
 * This class demonstrates the options available on a {@link WDataTable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class DataTableOptionsExample extends WContainer {

	/**
	 * Example of a basic table.
	 */
	private final WDataTable table1;
	/**
	 * Example of a table with expandable context.
	 */
	private final WDataTable table2;
	/**
	 * Example of a hierarchic table.
	 */
	private final WDataTable table3;

	/**
	 * Default rows per page on the example tables.
	 */
	private static final int DEFAULT_ROWS_PER_PAGE = 3;

	/**
	 * Display selected items for table1.
	 */
	private final WText selected1 = new WText();
	/**
	 * Display selected items for table2.
	 */
	private final WText selected2 = new WText();
	/**
	 * Display selected items for table3.
	 */
	private final WText selected3 = new WText();

	/**
	 * Select Options.
	 */
	private final EnumerationRadioButtonSelect<WDataTable.SelectMode> rbsSelect;
	/**
	 * Select All Options.
	 */
	private final EnumerationRadioButtonSelect<WDataTable.SelectAllType> rbsSelectAll;
	/**
	 * Expand Options.
	 */
	private final EnumerationRadioButtonSelect<WDataTable.ExpandMode> rbsExpand;
	/**
	 * Paging Options.
	 */
	private final EnumerationRadioButtonSelect<WDataTable.PaginationMode> rbsPaging;
	/**
	 * Striping Options.
	 */
	private final EnumerationRadioButtonSelect<WDataTable.StripingType> rbsStriping;
	/**
	 * Separator Options.
	 */
	private final EnumerationRadioButtonSelect<WDataTable.SeparatorType> rbsSeparator;
	/**
	 * Sorting Options.
	 */
	private final EnumerationRadioButtonSelect<WDataTable.SortMode> rbsSorting;
	/**
	 * Row header toggle.
	 */
	private final WCheckBox showRowHeaders = new WCheckBox();
	/**
	 * Column header toggle.
	 */
	private final WCheckBox showColHeaders = new WCheckBox();
	/**
	 * Expand all toggle.
	 */
	private final WCheckBox expandAll = new WCheckBox();
	/**
	 * Disabled toggle.
	 */
	private final WCheckBox cbDisable = new WCheckBox();

	/**
	 * Construct the example.
	 */
	public DataTableOptionsExample() {
		// Create Options
		rbsSelect = createRadioButtonGroup(WDataTable.SelectMode.values());
		rbsSelectAll = createRadioButtonGroup(WDataTable.SelectAllType.values());
		rbsExpand = createRadioButtonGroup(WDataTable.ExpandMode.values());
		rbsPaging = createRadioButtonGroup(WDataTable.PaginationMode.values());
		rbsStriping = createRadioButtonGroup(WDataTable.StripingType.values());
		rbsSeparator = createRadioButtonGroup(WDataTable.SeparatorType.values());
		rbsSorting = createRadioButtonGroup(WDataTable.SortMode.values());

		// Options Layout
		WFieldSet fieldSet = new WFieldSet("Table configuration");
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(30);

		layout.addField("Select Mode", rbsSelect);
		layout.addField("Select All Type", rbsSelectAll);
		layout.addField("Expand Mode", rbsExpand);
		layout.addField("Paging Mode", rbsPaging);
		layout.addField("Striping Type", rbsStriping);
		layout.addField("Separator Type", rbsSeparator);
		layout.addField("Sort Mode", rbsSorting);
		layout.addField("Show row headers", showRowHeaders);
		layout.addField("Show col headers", showColHeaders);
		layout.addField("Expand all", expandAll);
		layout.addField("Disable", cbDisable);

		// Apply Button
		WButton apply = new WButton("Apply");
		apply.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				applySettings();
			}
		});

		fieldSet.add(layout);
		fieldSet.add(apply);
		add(fieldSet);

		add(new WHorizontalRule());

		// Create Tables
		table1 = createBasicDataTable();
		table2 = createExpandedDataTable();
		table3 = createHierarchicDataTable();

		add(new WHeading(WHeading.SECTION, "Table"));
		add(table1);
		add(selected1);

		add(new WHorizontalRule());

		add(new WHeading(WHeading.SECTION, "Expanded Content Table"));
		add(table2);
		add(selected2);

		add(new WHorizontalRule());

		add(new WHeading(WHeading.SECTION, "Hierarchic Table"));
		add(table3);
		add(selected3);
	}

	/**
	 * Create a radio button select containing the options.
	 *
	 * @param <T> the enumeration type.
	 * @param options the list of options
	 * @return a radioButtonSelect with the options
	 */
	private <T extends Enum<T>> EnumerationRadioButtonSelect<T> createRadioButtonGroup(
			final T[] options) {
		EnumerationRadioButtonSelect<T> rbSelect = new EnumerationRadioButtonSelect<>(options);
		rbSelect.setButtonLayout(EnumerationRadioButtonSelect.Layout.FLAT);
		rbSelect.setFrameless(true);
		return rbSelect;
	}

	/**
	 * @return a basic table example.
	 */
	private WDataTable createBasicDataTable() {
		WDataTable table = new WDataTable();
		table.setType(WDataTable.Type.TABLE);
		table.addColumn(new WTableColumn("Column1", WText.class));
		table.addColumn(new WTableColumn("Column2", WText.class));
		table.addColumn(new WTableColumn("Column3", WText.class));
		table.setRowsPerPage(DEFAULT_ROWS_PER_PAGE);
		table.setSummary("Basic table summary");
		table.setCaption("Basic table caption");

		ExampleDataModel data = new ExampleDataModel();
		data.setData(createData());
		table.setDataModel(data);

		return table;
	}

	/**
	 * @return a list of sample data
	 */
	private List<ExampleBean> createData() {
		List<ExampleBean> list = new ArrayList<>();
		list.add(new ExampleBean("A1", "H2", "G3"));
		list.add(new ExampleBean("B1", "G2", "E3"));
		list.add(new ExampleBean("C1", "F2", "C3"));
		list.add(new ExampleBean("D1", "E2", "A3"));
		list.add(new ExampleBean("E1", "D2", "B3"));
		list.add(new ExampleBean("F1", "C2", "D3"));
		list.add(new ExampleBean("G1", "B2", "F3"));
		list.add(new ExampleBean("H1", "A2", "H3"));
		return list;
	}

	/**
	 * @return a table that has expanded content.
	 */
	private WDataTable createExpandedDataTable() {
		WDataTable table = new WDataTable();
		table.setType(WDataTable.Type.TABLE);
		table.addColumn(new WTableColumn("Column1", WText.class));
		table.addColumn(new WTableColumn("Column2", WText.class));
		table.addColumn(new WTableColumn("Column3", WText.class));
		table.setRowsPerPage(DEFAULT_ROWS_PER_PAGE);
		table.setSummary("Expanded content table summary");
		table.setCaption("Expanded content table caption");

		TableTreeNode root = createTree();
		table.setDataModel(new ExampleTreeTableModel(root));

		return table;
	}

	/**
	 * @return a TableTreeNode containing sample data.
	 */
	private TableTreeNode createTree() {
		TableTreeNode root = new TableTreeNode(null);
		TableTreeNode row1 = new TableTreeNode(new ExampleBean("A1", "H2", "G3"));
		TableTreeNode row2 = new TableTreeNode(new ExampleBean("B1", "G2", "E3"));
		TableTreeNode row3 = new TableTreeNode(new ExampleBean("C1", "F2", "C3"));
		TableTreeNode row4 = new TableTreeNode(new ExampleBean("D1", "E2", "A3"));
		TableTreeNode row5 = new TableTreeNode(new ExampleBean("E1", "D2", "B3"));
		TableTreeNode row6 = new TableTreeNode(new ExampleBean("F1", "C2", "D3"));
		TableTreeNode row7 = new TableTreeNode(new ExampleBean("G1", "B2", "F3"));
		TableTreeNode row8 = new TableTreeNode(new ExampleBean("H1", "A2", "H3"));
		TableTreeNode extra1 = new ExtraDetailsNode(new ExtraBean("X1-A", "X1-B", "X1-C"));
		TableTreeNode extra2 = new ExtraDetailsNode(new ExtraBean("Y1-A", "Y1-B", "Y1-C"));
		TableTreeNode extra3 = new ExtraDetailsNode(new ExtraBean("Y2-A", "Y2-B", "Y2-C"));
		TableTreeNode extra4 = new ExtraDetailsNode(new ExtraBean("Z1-A", "Z1-B", "Z1-C"));

		root.add(row1);
		root.add(row2);
		root.add(row3);
		root.add(row4);
		root.add(row5);
		root.add(row6);
		root.add(row7);
		root.add(row8);

		row1.add(extra1);
		row3.add(extra2);
		row3.add(extra3);
		row7.add(extra4);

		return root;
	}

	/**
	 * @return a hierarchic table
	 */
	private WDataTable createHierarchicDataTable() {
		WDataTable table = new WDataTable();
		table.setType(WDataTable.Type.HIERARCHIC);
		table.addColumn(new WTableColumn("Column1", WText.class));
		table.addColumn(new WTableColumn("Column2", WText.class));
		table.addColumn(new WTableColumn("Column3", WText.class));
		table.setRowsPerPage(DEFAULT_ROWS_PER_PAGE);
		table.setSummary("Hierarchic table summary");
		table.setCaption("Hierarchic table caption");

		TableTreeNode root = createHierarchicTree();
		table.setDataModel(new ExampleTreeTableModel(root));

		return table;
	}

	/**
	 * @return a tableTreeNode to be used by a hierachic table
	 */
	private TableTreeNode createHierarchicTree() {
		TableTreeNode root = new TableTreeNode(null);
		TableTreeNode row1 = new TableTreeNode(new ExampleBean("A1", "H2", "G3"));
		TableTreeNode row2 = new TableTreeNode(new ExampleBean("B1", "G2", "E3"));
		TableTreeNode row3 = new TableTreeNode(new ExampleBean("C1", "F2", "C3"));
		TableTreeNode row4 = new TableTreeNode(new ExampleBean("D1", "E2", "A3"));
		TableTreeNode row5 = new TableTreeNode(new ExampleBean("E1", "D2", "B3"));
		TableTreeNode row6 = new TableTreeNode(new ExampleBean("F1", "C2", "D3"));
		TableTreeNode row7 = new TableTreeNode(new ExampleBean("G1", "B2", "F3"));
		TableTreeNode row8 = new TableTreeNode(new ExampleBean("H1", "A2", "H3"));
		TableTreeNode child1 = new TableTreeNode(new ExampleBean("A1-A", "A1-B", "A1-C"));
		TableTreeNode child2 = new TableTreeNode(new ExampleBean("C1-A", "C1-B", "C1-C"));
		TableTreeNode child3 = new TableTreeNode(new ExampleBean("C2-A", "C2-B", "C2-C"));
		TableTreeNode child4 = new TableTreeNode(new ExampleBean("G1-A", "G1-B", "G1-C"));

		root.add(row1);
		root.add(row2);
		root.add(row3);
		root.add(row4);
		root.add(row5);
		root.add(row6);
		root.add(row7);
		root.add(row8);

		row1.add(child1);
		row3.add(child2);
		row3.add(child3);
		row7.add(child4);

		return root;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			// Defaults
			rbsSelect.setSelected(WDataTable.SelectMode.NONE);
			rbsSelectAll.setSelected(WDataTable.SelectAllType.NONE);
			rbsExpand.setSelected(WDataTable.ExpandMode.NONE);
			rbsPaging.setSelected(WDataTable.PaginationMode.DYNAMIC);
			rbsStriping.setSelected(WDataTable.StripingType.NONE);
			rbsSeparator.setSelected(WDataTable.SeparatorType.NONE);
			rbsSorting.setSelected(WDataTable.SortMode.SERVER);
			showColHeaders.setSelected(true);
			applySettings();
			setInitialised(true);
		}

		displaySelected();

	}

	/**
	 * Display the rows that have been selected.
	 */
	private void displaySelected() {
		copySelection(table1, selected1);
		copySelection(table2, selected2);
		copySelection(table3, selected3);
	}

	/**
	 *
	 * @param table the table with selected items
	 * @param selected the text field that will contain a copy of the selected item details
	 */
	private void copySelection(final WDataTable table, final WText selected) {
		List<Integer> rows = table.getSelectedRows();

		if (rows.isEmpty()) {
			selected.setText("No Rows Selected");
		} else {
			StringBuffer out = new StringBuffer("Selected: ");
			TableDataModel data = table.getDataModel();
			boolean firstRow = true;
			for (Integer rowIdx : rows) {
				if (firstRow) {
					firstRow = false;
				} else {
					out.append(", ");
				}
				ExampleBean bean;
				if (data instanceof TreeTableDataModel) {
					TableTreeNode node = ((TreeTableDataModel) data).
							getNodeAtLine(rowIdx.intValue());
					bean = (ExampleBean) node.getData();
				} else {
					ExampleDataModel node = (ExampleDataModel) data;
					bean = node.getData().get(rowIdx.intValue());
				}
				out.append(bean.getColumn1());
			}
			selected.setText(out.toString());
		}
	}

	/**
	 * Apply the table options selected by the user.
	 */
	private void applySettings() {
		applyTableSettings(table1);
		applyTableSettings(table2);
		applyTableSettings(table3);
	}

	/**
	 * Apply the settings to a particular table.
	 *
	 * @param table the table to apply settings to
	 */
	private void applyTableSettings(final WDataTable table) {
		table.setSelectMode(rbsSelect.getSelected());
		table.setSelectAllMode(rbsSelectAll.getSelected());
		table.setExpandMode(rbsExpand.getSelected());
		table.setPaginationMode(rbsPaging.getSelected());
		table.setSortMode(rbsSorting.getSelected());
		table.setStripingType(rbsStriping.getSelected());
		table.setSeparatorType(rbsSeparator.getSelected());
		table.setShowRowHeaders(showRowHeaders.isSelected());
		table.setShowColumnHeaders(showColHeaders.isSelected());
		table.setExpandAll(expandAll.isSelected());
		table.setDisabled(cbDisable.isSelected());
	}

	/**
	 *
	 * This class is an example of a basic data model.
	 */
	public static class ExampleDataModel extends AbstractTableDataModel {

		/**
		 * Column1 index.
		 */
		private static final int COL1 = 0;
		/**
		 * Column2 index.
		 */
		private static final int COL2 = 1;
		/**
		 * Column3 index.
		 */
		private static final int COL3 = 2;

		/**
		 * List that holds the sample data.
		 */
		private List<ExampleBean> data;

		/**
		 * @return the sample data.
		 */
		public List<ExampleBean> getData() {
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			return getData().size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final int row, final int col) {
			ExampleBean bean = getData().get(row);
			switch (col) {
				case COL1:
					return bean.getColumn1();
				case COL2:
					return bean.getColumn2();
				case COL3:
					return bean.getColumn3();
				default:
					return null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getRowHeader(final int row) {
			return "Row " + row;
		}

		/**
		 * @param data the list of sample data.
		 */
		public void setData(final List<ExampleBean> data) {
			this.data = data;
		}

		/**
		 * Sort the sample data.
		 *
		 * @param col the index of the column to sort by.
		 * @param ascending true if sort ascending.
		 * @return null, there is no mapping for a tree model.
		 */
		@Override
		public int[] sort(final int col, final boolean ascending) {
			String name = null;

			switch (col) {
				case COL1:
					name = "column1";
					break;
				case COL2:
					name = "column2";
					break;
				default:
					return null;
			}

			Comparator<ExampleBean> comp = new BeanComparator(name);

			if (ascending) {
				Collections.sort(data, comp);
			} else {
				Collections.sort(data, Collections.reverseOrder(comp));
			}

			return null;
		}

		/**
		 * Indicates whether the given column is sortable.
		 *
		 * @param col the column index
		 *
		 * @return true if this column is sortable.
		 */
		@Override
		public boolean isSortable(final int col) {
			return (col != COL3);
		}

	}

	/**
	 * A basic bean example.
	 *
	 * @since 1.0.0
	 */
	public static class ExampleBean implements Serializable {

		/**
		 * Column1 data.
		 */
		private String column1;
		/**
		 * Column2 data.
		 */
		private String column2;
		/**
		 * Column3 data.
		 */
		private String column3;

		/**
		 * Construct the example bean.
		 *
		 * @param column1 the data for column1
		 * @param column2 the data for column2
		 * @param column3 the data for column3
		 */
		public ExampleBean(final String column1, final String column2, final String column3) {
			this.column1 = column1;
			this.column2 = column2;
			this.column3 = column3;
		}

		/**
		 * @return the data for column1
		 */
		public String getColumn1() {
			return column1;
		}

		/**
		 * @param column1 the data for column1
		 */
		public void setColumn1(final String column1) {
			this.column1 = column1;
		}

		/**
		 * @return the data for column2
		 */
		public String getColumn2() {
			return column2;
		}

		/**
		 * @param column2 the data for column2
		 */
		public void setColumn2(final String column2) {
			this.column2 = column2;
		}

		/**
		 * @return the data for column3
		 */
		public String getColumn3() {
			return column3;
		}

		/**
		 * @param column3 the data for column3
		 */
		public void setColumn3(final String column3) {
			this.column3 = column3;
		}

	}

	/**
	 * This class is an example of a Tree Data Model.
	 *
	 * @since 1.0.0
	 */
	private static final class ExampleTreeTableModel extends AbstractTreeTableDataModel {

		/**
		 * Column1 index.
		 */
		private static final int COL1 = 0;
		/**
		 * Column2 index.
		 */
		private static final int COL2 = 1;
		/**
		 * Column3 index.
		 */
		private static final int COL3 = 2;

		/**
		 * Construct the model.
		 *
		 * @param root the sample data for the table.
		 */
		private ExampleTreeTableModel(final TableTreeNode root) {
			super(root);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final TableTreeNode node, final int col) {
			ExampleBean bean = (ExampleBean) node.getData();
			switch (col) {
				case COL1:
					return bean.getColumn1();
				case COL2:
					return bean.getColumn2();
				case COL3:
					return bean.getColumn3();
				default:
					return null;
			}
		}

		/**
		 * Sort the sample data.
		 *
		 * @param col the column to sort by
		 * @param ascending true if sort ascending
		 * @return null, there is no mapping for a tree model.
		 */
		@Override
		public int[] sort(final int col, final boolean ascending) {

			// Obtains the list of top level nodes, sorts them & re-add them in
			// order
			TableTreeNode root = getRootNode();
			List<TableTreeNode> topLevelNodes = new ArrayList<>(root.getChildCount());

			for (int i = 0; i < root.getChildCount(); i++) {
				topLevelNodes.add((TableTreeNode) root.getChildAt(i));
			}

			Comparator<TableTreeNode> comp = new AbstractComparator() {
				@Override
				protected Comparable getComparable(final Object obj) {
					TableTreeNode node = (TableTreeNode) obj;
					ExampleBean bean = (ExampleBean) node.getData();

					switch (col) {
						case COL1:
							return bean.getColumn1();
						case COL2:
							return bean.getColumn2();
						default:
							return null;
					}
				}
			};

			if (ascending) {
				Collections.sort(topLevelNodes, comp);
			} else {
				Collections.sort(topLevelNodes, Collections.reverseOrder(comp));
			}

			root.removeAll();

			for (TableTreeNode node : topLevelNodes) {
				root.add(node);
			}

			return null;
		}

		/**
		 * Indicates whether the given column is sortable.
		 *
		 * @param col the column index.
		 *
		 * @return true if this column is sortable.
		 */
		@Override
		public boolean isSortable(final int col) {
			return (col != COL3);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getRowHeader(final int row) {
			return "Row " + row;
		}
	}

	/**
	 *
	 * This class is an example of node used for expanded content.
	 *
	 * @since 1.0.0
	 */
	public static final class ExtraDetailsNode extends TableTreeNode {

		/**
		 * Construct the node.
		 *
		 * @param extra the bean for the expanded content.
		 */
		public ExtraDetailsNode(final ExtraBean extra) {
			super(extra, ExtraDetailsPanel.class, false);
		}
	}

	/**
	 * A basic bean used for expanded content.
	 *
	 * @since 1.0.0
	 */
	public static final class ExtraBean implements Serializable {

		/**
		 * ColumnA data.
		 */
		private String columnA;
		/**
		 * ColumnB data.
		 */
		private String columnB;
		/**
		 * ColumnC data.
		 */
		private String columnC;

		/**
		 * Construct the extra bean.
		 *
		 * @param columnA the data for columnA
		 * @param columnB the data for columnB
		 * @param columnC the data for columnC
		 */
		public ExtraBean(final String columnA, final String columnB, final String columnC) {
			this.columnA = columnA;
			this.columnB = columnB;
			this.columnC = columnC;
		}

		/**
		 * @return the data for columnA
		 */
		public String getColumnA() {
			return columnA;
		}

		/**
		 * @param columnA the data for columnA
		 */
		public void setColumnA(final String columnA) {
			this.columnA = columnA;
		}

		/**
		 * @return the data for columnB
		 */
		public String getColumnB() {
			return columnB;
		}

		/**
		 * @param columnB the data for columnB
		 */
		public void setColumnB(final String columnB) {
			this.columnB = columnB;
		}

		/**
		 * @return the data for columnC
		 */
		public String getColumnC() {
			return columnC;
		}

		/**
		 * @param columnC the data for columnC
		 */
		public void setColumnC(final String columnC) {
			this.columnC = columnC;
		}

	}

	/**
	 * A class used to render the expanded content.
	 *
	 * @since 1.0.0
	 */
	public static final class ExtraDetailsPanel extends WBeanContainer {

		/**
		 * Construct the component.
		 */
		public ExtraDetailsPanel() {
			WPanel panel = new WPanel();
			panel.setLayout(new FlowLayout(Alignment.LEFT, 5, 0));
			add(panel);

			WText colA = new WText();
			colA.setBeanProperty("columnA");
			panel.add(new WLabel("ColumnA"));
			panel.add(colA);

			WText colB = new WText();
			colB.setBeanProperty("columnB");
			panel.add(new WLabel("ColumnB"));
			panel.add(colB);

			WText colC = new WText();
			colC.setBeanProperty("columnC");
			panel.add(new WLabel("ColumnC"));
			panel.add(colC);
		}
	}

	/**
	 * A simple extension of WRadioButtonSelect to enhance type safety and provide a more intelligent description of the
	 * enumerated values.
	 *
	 * @param <T> the enumeration type.
	 */
	private static final class EnumerationRadioButtonSelect<T extends Enum<T>> extends WRadioButtonSelect {

		/**
		 * Creates an EnumerationRadioButtonSelect.
		 *
		 * @param options the options
		 */
		private EnumerationRadioButtonSelect(final T[] options) {
			super(options);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override // for type-safety only
		public T getSelected() {
			return (T) super.getSelected();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDesc(final Object option, final int index) {
			String desc = super.getDesc(option, index);
			return desc.charAt(0) + desc.substring(1).replace('_', ' ').toLowerCase();
		}
	}
}
