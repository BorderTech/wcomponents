package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.AbstractTableModel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.ExpandMode;
import com.github.bordertech.wcomponents.WTable.PaginationMode;
import com.github.bordertech.wcomponents.WTable.SortMode;
import com.github.bordertech.wcomponents.WTable.TableModel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.table.PersonBean.TravelDoc;
import com.github.bordertech.wcomponents.util.AbstractComparator;
import com.github.bordertech.wcomponents.util.TableUtil;
import java.util.Comparator;
import java.util.List;

/**
 * This class demonstrates an implementation of {@link TableModel} that allows for expandable rows.
 * <p>
 * It is not heirarchic as the expanded content has its own "renderer".
 * </p>
 * <p>
 * {@link SimpleBeanBoundTableModel} can be used to achieve the same result and is the preferred implementation.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TableExpandableContentModelExample extends WPanel {

	/**
	 * Example table.
	 */
	private final WTable table = new WTable();

	/**
	 * Construct the example.
	 */
	public TableExpandableContentModelExample() {
		add(table);

		// Columns
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WDateField()));

		// Expand mode
		table.setExpandMode(ExpandMode.DYNAMIC);

		table.setExpandAll(true);

		// Pagination
		table.setPaginationMode(PaginationMode.DYNAMIC);
		table.setRowsPerPage(5);

		// Sorting
		table.setSortMode(SortMode.DYNAMIC);
	}

	/**
	 * Override preparePaintComponent in order to set up the example data the first time that the example is accessed by
	 * each user.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			// This model holds the data so would be included on the user session.
			ExampleExpandableModel data = new ExampleExpandableModel(ExampleDataUtil.
					createExampleData(),
					TravelDocPanel.class);
			table.setTableModel(data);
			setInitialised(true);
		}
	}

	/**
	 * This class is an example of a table model that will support one expandable level.
	 * <p>
	 * {@link SimpleBeanBoundTableModel} can be used to achieve the same result and is the preferred implementation.
	 * </p>
	 */
	public static class ExampleExpandableModel extends AbstractTableModel {

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
		private final List<PersonBean> data;

		/**
		 * Expanded content render.
		 */
		private final Class<? extends WComponent> renderer;

		/**
		 * @param data the sample data
		 * @param renderer the expanded content renderer
		 */
		public ExampleExpandableModel(final List<PersonBean> data,
				final Class<? extends WComponent> renderer) {
			this.data = data;
			this.renderer = renderer;
		}

		/**
		 * @return the sample data.
		 */
		public List<PersonBean> getData() {
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final List<Integer> row, final int col) {
			int rootIdx = row.get(0);
			// Top Level
			PersonBean bean = getData().get(rootIdx);
			if (row.size() == 1) {
				switch (col) {
					case COL1:
						return bean.getFirstName();
					case COL2:
						return bean.getLastName();
					case COL3:
						return bean.getDateOfBirth();
					default:
						return null;
				}
			} else if (row.size() == 2) {  // Expandable Level
				int docIdx = row.get(1);
				TravelDoc doc = bean.getDocuments().get(docIdx);
				return doc;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int[] sort(final int col, final boolean ascending) {
			Comparator<Object> comp = new AbstractComparator() {
				@Override
				protected Comparable getComparable(final Object obj) {
					return (String) obj;
				}
			};
			return sort(comp, col, ascending);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSortable(final int col) {
			return (col != COL3);
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
		public int getChildCount(final List<Integer> row) {
			// Top Level - check if level has children (ie has documents)
			if (row.size() == 1) {
				PersonBean bean = data.get(row.get(0));
				return bean.getDocuments() == null ? 0 : bean.getDocuments().size();
			}

			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Class<? extends WComponent> getRendererClass(final List<Integer> row) {
			// Expandable Level - Renderer for expanded content
			if (row.size() == 2) {
				return renderer;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isExpandable(final List<Integer> row) {
			return true;
		}
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
			WDateField issueDate = new WDateField();
			WDateField expiryDate = new WDateField();

			issueDate.setReadOnly(true);
			expiryDate.setReadOnly(true);

			documentNumber.setBeanProperty("documentNumber");
			countryOfIssue.setBeanProperty("countryOfIssue");
			placeOfIssue.setBeanProperty("placeOfIssue");
			issueDate.setBeanProperty("issueDate");
			expiryDate.setBeanProperty("expiryDate");

			WDefinitionList list = new WDefinitionList(WDefinitionList.Type.COLUMN);
			add(list);

			list.addTerm("Document number", documentNumber);
			list.addTerm("Country of issue", countryOfIssue);
			list.addTerm("Place Of issue", placeOfIssue);
			list.addTerm("Issue date", issueDate);
			list.addTerm("Expiry date", expiryDate);

		}
	}

}
