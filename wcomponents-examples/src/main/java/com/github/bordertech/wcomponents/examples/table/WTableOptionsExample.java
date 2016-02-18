package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel.LevelDetails;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.PaginationMode;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.table.PersonBean.TravelDoc;
import com.github.bordertech.wcomponents.subordinate.And;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.NotEqual;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.util.TableUtil;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * This class demonstrates the options available on a {@link WTable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WTableOptionsExample extends WBeanContainer {

	/**
	 * Messages.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * Example of a basic table.
	 */
	private final WTable table1;
	/**
	 * Example of a table with expandable context.
	 */
	private final WTable table2;
	/**
	 * Example of a hierarchic table.
	 */
	private final WTable table3;

	/**
	 * Default rows per page on the example tables.
	 */
	private static final int DEFAULT_ROWS_PER_PAGE = 3;

	/**
	 * Default rows per page options.
	 */
	private static final List<Integer> DEFAULT_ROWS_OPTIONS = Arrays.asList(0, 3, 5);

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
	private final EnumerationRadioButtonSelect<WTable.SelectMode> rbsSelect;
	/**
	 * Select All Options.
	 */
	private final EnumerationRadioButtonSelect<WTable.SelectAllType> rbsSelectAll;
	/**
	 * Expand Options.
	 */
	private final EnumerationRadioButtonSelect<WTable.ExpandMode> rbsExpand;
	/**
	 * Paging Options.
	 */
	private final EnumerationRadioButtonSelect<WTable.PaginationMode> rbsPaging;
	/**
	 * number of rows per page selection.
	 */
	private final WNumberField numRowsPerPage = new WNumberField();
	/**
	 * Location of pagination controls.
	 */
	private final EnumerationRadioButtonSelect<WTable.PaginationLocation> paginationControlsLocation;
	/**
	 * Striping Options.
	 */
	private final EnumerationRadioButtonSelect<WTable.StripingType> rbsStriping;
	/**
	 * Separator Options.
	 */
	private final EnumerationRadioButtonSelect<WTable.SeparatorType> rbsSeparator;
	/**
	 * Sorting Options.
	 */
	private final EnumerationRadioButtonSelect<WTable.SortMode> rbsSorting;
	/**
	 * Column header toggle.
	 */
	private final WCheckBox showColHeaders = new WCheckBox();
	/**
	 * Expand all toggle.
	 */
	private final WCheckBox expandAll = new WCheckBox();
	/**
	 * Editable.
	 */
	private final WCheckBox chbEditable = new WCheckBox();
	/**
	 * Column order.
	 */
	private final WMultiSelectPair columnOrder = new WMultiSelectPair(Arrays.asList(COLUMN.values()));

	/**
	 * Rows per page options.
	 */
	private final WCheckBox chbRowsPerPageOptions = new WCheckBox();

	/**
	 * Caption text.
	 */
	private final WTextField tfCaption = new WTextField();

	/**
	 * Sub row select toggle.
	 */
	private final WCheckBox cbToggleSubRowSelection = new WCheckBox();

	/**
	 * Use row headers.
	 */
	private final WCheckBox cbHasRowHeaders = new WCheckBox();

	/**
	 * Construct the example.
	 */
	public WTableOptionsExample() {
		// Create Options
		rbsSelect = createRadioButtonGroup(WTable.SelectMode.values());
		rbsSelectAll = createRadioButtonGroup(WTable.SelectAllType.values());
		rbsExpand = createRadioButtonGroup(WTable.ExpandMode.values());
		rbsPaging = createRadioButtonGroup(WTable.PaginationMode.values());
		rbsStriping = createRadioButtonGroup(WTable.StripingType.values());
		rbsSeparator = createRadioButtonGroup(WTable.SeparatorType.values());
		rbsSorting = createRadioButtonGroup(WTable.SortMode.values());
		numRowsPerPage.setNumber(DEFAULT_ROWS_PER_PAGE);
		numRowsPerPage.setMinValue(1);
		paginationControlsLocation = createRadioButtonGroup(WTable.PaginationLocation.values());

		columnOrder.setSelected(columnOrder.getOptions());
		columnOrder.setMinSelect(1);
		columnOrder.setShuffle(true);
		columnOrder.setMandatory(true);

		add(messages);

		// Options Layout
		WFieldSet fieldSet = new WFieldSet("Table configuration");
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(30);

		layout.addField("Select Mode", rbsSelect);
		WField fieldSelectAll = layout.addField("Select All Type", rbsSelectAll);
		/* show and hide the row selection sub-options */
		WSubordinateControl subShowSelectOptions = new WSubordinateControl();
		Rule rule = new Rule();
		rule.setCondition(new Equal(rbsSelect, WTable.SelectMode.MULTIPLE));
		rule.addActionOnTrue(new Show(fieldSelectAll));
		rule.addActionOnFalse(new Hide(fieldSelectAll));
		subShowSelectOptions.addRule(rule);
		add(subShowSelectOptions);

		layout.addField("Expand Mode", rbsExpand);
		layout.addField("Paging Mode", rbsPaging);
		layout.addField("Striping Type", rbsStriping);
		layout.addField("Separator Type", rbsSeparator);
		layout.addField("Sort Mode", rbsSorting);
		layout.addField("Show col headers", showColHeaders);
		WField fieldExpandAll = layout.addField("Expand all", expandAll);
		/* show and hide the row expansion sub-options */
		WSubordinateControl subShowExpandOptions = new WSubordinateControl();
		rule = new Rule();
		rule.setCondition(new Equal(rbsExpand, WTable.ExpandMode.NONE));
		rule.addActionOnTrue(new Hide(fieldExpandAll));
		rule.addActionOnFalse(new Show(fieldExpandAll));
		subShowExpandOptions.addRule(rule);
		add(subShowExpandOptions);

		WField fieldToggleSubRowSelection = layout.addField("Parent row selection controls sub row selection", cbToggleSubRowSelection);
		/* show/hide sub-row selection options */
		WSubordinateControl subToggler = new WSubordinateControl();
		rule = new Rule();
		rule.setCondition(new And(new Equal(rbsSelect, WTable.SelectMode.MULTIPLE), new NotEqual(rbsExpand, WTable.ExpandMode.NONE)));
		rule.addActionOnTrue(new Show(fieldToggleSubRowSelection));
		rule.addActionOnFalse(new Hide(fieldToggleSubRowSelection));
		subToggler.addRule(rule);
		add(subToggler);

		layout.addField("Editable", chbEditable);
		layout.addField("Column order", columnOrder);
		WField fieldRows = layout.addField("Rows per page", numRowsPerPage);
		WField fieldRowsOptions = layout.addField("Rows per page options", chbRowsPerPageOptions);
		WField fieldPaginationLocation = layout.addField("Location of pagination controls", paginationControlsLocation);
		/* show/hide pagination options */
		WSubordinateControl pagRowsPerPage = new WSubordinateControl();
		rule = new Rule();
		rule.setCondition(new Equal(rbsPaging, WTable.PaginationMode.NONE));
		rule.addActionOnTrue(new Hide(fieldRows));
		rule.addActionOnTrue(new Hide(fieldRowsOptions));
		rule.addActionOnTrue(new Hide(fieldPaginationLocation));
		rule.addActionOnFalse(new Show(fieldRows));
		rule.addActionOnFalse(new Show(fieldRowsOptions));
		rule.addActionOnFalse(new Show(fieldPaginationLocation));
		pagRowsPerPage.addRule(rule);
		add(pagRowsPerPage);

		layout.addField("Caption", tfCaption);
		layout.addField("Use row headers", cbHasRowHeaders);

		// Apply Button
		WButton apply = new WButton("Apply");
		apply.setAction(new ValidatingAction(messages.getValidationErrors(), fieldSet) {
			@Override
			public void executeOnValid(final ActionEvent event) {
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

		add(new WHeading(WHeading.SECTION, "Basic Table"));
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

		add(new WHorizontalRule());

		add(new WButton("Refresh"));
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
	private WTable createBasicDataTable() {
		WTable table = new WTable();
		addColumns(table);
		table.setType(WTable.Type.TABLE);
		table.setRowsPerPage(DEFAULT_ROWS_PER_PAGE);
		table.setSummary("Basic table summary");
		table.setCaption("Basic table caption");
		table.setBeanProperty(".");

		// Setup model with column properties
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth"});
		model.setSelectable(true);
		model.setEditable(true);
		model.setComparator(0, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);
		model.setComparator(1, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);

		table.setTableModel(model);

		return table;
	}

	/**
	 * @return a table that has expanded content.
	 */
	private WTable createExpandedDataTable() {
		WTable table = new WTable();
		addColumns(table);
		table.setType(WTable.Type.TABLE);
		table.setRowsPerPage(DEFAULT_ROWS_PER_PAGE);
		table.setSummary("Expanded content table summary");
		table.setCaption("Expanded content table caption");
		table.setBeanProperty(".");

		// Define the expandable level. The row will expand if the bean has "extra" details
		LevelDetails level1 = new LevelDetails("documents", TravelDocPanel.class);

		// Setup model with column properties and the "expandable" level
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth"}, level1);

		model.setSelectable(true);
		model.setEditable(true);
		model.setComparator(0, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);
		model.setComparator(1, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);

		table.setTableModel(model);

		return table;
	}

	/**
	 * @return a hierarchic table
	 */
	private WTable createHierarchicDataTable() {
		WTable table = new WTable();
		addColumns(table);
		table.setType(WTable.Type.HIERARCHIC);
		table.setRowsPerPage(DEFAULT_ROWS_PER_PAGE);
		table.setSummary("Hierarchic table summary");
		table.setCaption("Hierarchic table caption");
		table.setBeanProperty(".");

		// Setup model with column properties and the "level" to iterate on (ie more details)
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth"}, "more");

		model.setIterateFirstLevel(true);
		model.setSelectable(true);
		model.setEditable(true);
		model.setComparator(0, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);
		model.setComparator(1, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);

		table.setTableModel(model);

		return table;
	}

	/**
	 * @param table the table to add columns
	 */
	private void addColumns(final WTable table) {
		// Column - First name
		WTextField textField = new WTextField();
		textField.setToolTip("First name");
		table.addColumn(new WTableColumn("First name", textField));

		// Column - Last name
		textField = new WTextField();
		textField.setToolTip("Last name");
		table.addColumn(new WTableColumn("Last name", textField));

		// Column - Date field
		WDateField dateField = new WDateField();
		dateField.setToolTip("Date of birth");
		table.addColumn(new WTableColumn("Date of birth", dateField));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			// Defaults
			rbsSelect.setSelected(WTable.SelectMode.NONE);
			rbsSelectAll.setSelected(WTable.SelectAllType.NONE);
			rbsExpand.setSelected(WTable.ExpandMode.NONE);
			rbsPaging.setSelected(WTable.PaginationMode.NONE);
			rbsStriping.setSelected(WTable.StripingType.NONE);
			rbsSeparator.setSelected(WTable.SeparatorType.NONE);
			rbsSorting.setSelected(WTable.SortMode.NONE);
			showColHeaders.setSelected(true);
			paginationControlsLocation.setSelected(WTable.PaginationLocation.AUTO);
			applySettings();

			// Set the data used by the tables
			setBean(ExampleDataUtil.createExampleData());

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
	 * @param table the table with selected items
	 * @param selected the text field that will contain a copy of the selected item details
	 */
	private void copySelection(final WTable table, final WText selected) {
		Set<?> rows = table.getSelectedRows();

		if (rows.isEmpty()) {
			selected.setText("No Rows Selected");
		} else {
			StringBuffer out = new StringBuffer("Selected: ");
			boolean firstRow = true;
			for (Object row : rows) {
				if (firstRow) {
					firstRow = false;
				} else {
					out.append(", ");
				}
				out.append(row);
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
	private void applyTableSettings(final WTable table) {
		table.setSelectMode(rbsSelect.getSelected());
		table.setSelectAllMode(rbsSelectAll.getSelected());
		table.setExpandMode(rbsExpand.getSelected());
		table.setSortMode(rbsSorting.getSelected());
		table.setStripingType(rbsStriping.getSelected());
		table.setSeparatorType(rbsSeparator.getSelected());
		table.setShowColumnHeaders(showColHeaders.isSelected());
		table.setExpandAll(expandAll.isSelected());
		table.setEditable(chbEditable.isSelected());
		table.setToggleSubRowSelection(table.getType() == WTable.Type.HIERARCHIC
				&& cbToggleSubRowSelection.isSelected()
				&& rbsExpand.getSelected() != WTable.ExpandMode.NONE
				&& rbsSelect.getSelected() == WTable.SelectMode.MULTIPLE);
		// row headers
		table.setRowHeaders(cbHasRowHeaders.isSelected());
		// Caption
		if (null == tfCaption.getText() || "".equals(tfCaption.getText())) {
			table.setCaption(null);
		} else {
			table.setCaption(tfCaption.getText());
		}

		// Pagination
		table.setPaginationMode(rbsPaging.getSelected());
		if (rbsPaging.getSelected() == PaginationMode.NONE) {
			table.setRowsPerPage(DEFAULT_ROWS_PER_PAGE);
			table.setRowsPerPageOptions(null);
			table.setPaginationLocation(WTable.PaginationLocation.AUTO);
		} else {
			// Options
			table.setRowsPerPageOptions(
					chbRowsPerPageOptions.isSelected() ? DEFAULT_ROWS_OPTIONS : null);
			// Rows
			int rows;
			if (numRowsPerPage.isEmpty() || numRowsPerPage.getNumber().intValue() < 1) {
				rows = DEFAULT_ROWS_PER_PAGE;
			} else {
				rows = numRowsPerPage.getNumber().intValue();
				if (chbRowsPerPageOptions.isSelected() && !DEFAULT_ROWS_OPTIONS.contains(rows)) {
					rows = DEFAULT_ROWS_PER_PAGE;
					numRowsPerPage.setNumber(rows);
				}
			}
			table.setRowsPerPage(rows);
			table.setPaginationLocation(paginationControlsLocation.getSelected());
		}

		List<COLUMN> cols = (List<COLUMN>) columnOrder.getSelected();
		int[] order = new int[cols.size()];
		int i = 0;
		for (COLUMN col : cols) {
			order[i++] = col.getCol();
		}
		table.setColumnOrder(order);
	}

	/**
	 * An example component to display travel document details. Expects that the supplied bean is a {@link TravelDoc}.
	 */
	public static final class TravelDocPanel extends WBeanContainer {

		/**
		 * Creates a TravelDocPanel.
		 */
		public TravelDocPanel() {
			WHorizontalRule rule = new WHorizontalRule() {
				@Override
				public boolean isVisible() {
					List<Integer> index = TableUtil.getCurrentRowIndex(TravelDocPanel.this);
					// On expanded row, so check the index of the expanded level
					return index.get(1) > 0;
				}

			};
			add(rule);

			WText documentNumber = new WText();
			WText countryOfIssue = new WText();
			WText placeOfIssue = new WText();

			documentNumber.setBeanProperty("documentNumber");
			countryOfIssue.setBeanProperty("countryOfIssue");
			placeOfIssue.setBeanProperty("placeOfIssue");

			WDefinitionList list = new WDefinitionList(WDefinitionList.Type.COLUMN);
			add(list);

			list.addTerm("Document number", documentNumber);
			list.addTerm("Country of issue", countryOfIssue);
			list.addTerm("Place Of issue", placeOfIssue);
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
		@Override
		// for type-safety only
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

	/**
	 * Columns used on the table.
	 * <p>
	 * This enum is used as the options in the WShuffler to demonstrate how column orders can be changed.
	 * </p>
	 */
	private enum COLUMN {
		/**
		 * First name.
		 */
		FIRST_NAME(0, "First name"),
		/**
		 * Last name.
		 */
		LAST_NAME(1, "Last name"),
		/**
		 * Date of birth.
		 */
		DATE_OF_BIRTH(2, "Date of birth");

		/**
		 * Column index.
		 */
		private final int col;
		/**
		 * Column description.
		 */
		private final String desc;

		/**
		 * @param col the column index
		 * @param desc the column description
		 */
		COLUMN(final int col, final String desc) {
			this.col = col;
			this.desc = desc;
		}

		/**
		 * @return the col index
		 */
		public int getCol() {
			return col;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return desc;
		}

	}

}
