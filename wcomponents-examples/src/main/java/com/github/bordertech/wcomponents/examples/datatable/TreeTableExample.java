package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.AbstractTreeTableDataModel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.TableTreeNode;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.ExpandMode;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.GridLayout;
import com.github.bordertech.wcomponents.util.AbstractComparator;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.LogFactory;

/**
 * This example shows the use of a {@link WDataTable} with hierarchical data.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TreeTableExample extends WPanel {

	/**
	 * Example table.
	 */
	private final WDataTable table = createTable();

	/**
	 * Creates a TreeTableExample.
	 */
	public TreeTableExample() {
		add(table);
		add(new WButton("Submit"));
	}

	/**
	 * Creates and configures the table to be used by the example. The table is configured with global rather than user
	 * data. Although this is not a realistic scenario, it will suffice for this example.
	 *
	 * @return a new configured table.
	 */
	private WDataTable createTable() {
		WDataTable tbl = new WDataTable();
		tbl.addColumn(new WTableColumn("First name", new WTextField()));
		tbl.addColumn(new WTableColumn("Last name", new WTextField()));
		tbl.addColumn(new WTableColumn("DOB", new WDateField()));
		tbl.setExpandMode(ExpandMode.CLIENT);

		TableTreeNode root = createTree();
		tbl.setDataModel(new ExampleTreeTableModel(root));

		return tbl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			TableTreeNode root = createTree();
			table.setDataModel(new ExampleTreeTableModel(root));
			setInitialised(true);
		}
	}

	/**
	 * @return a tree containing the data for this example.
	 */
	private TableTreeNode createTree() {
		TableTreeNode root = new TableTreeNode(null);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");

		try {
			TableTreeNode joe = new TableTreeNode(new PersonBean("Joe", "Bloggs", sdf.parse(
					"01/02/1973")));
			TableTreeNode jane = new TableTreeNode(new PersonBean("Jane", "Bloggs", sdf.parse(
					"04/05/1976")));
			TableTreeNode kid = new TableTreeNode(new PersonBean("Kid", "Bloggs", sdf.parse(
					"31/12/1999")));
			TableTreeNode doc1 = new TravelDocNode(new TravelDoc("12345", "Canada", "Ottawa", sdf.
					parse("01/01/1990"), sdf.parse("01/01/1983")));
			TableTreeNode doc2 = new TravelDocNode(new TravelDoc("23456", "New Zealand",
					"Wellington", sdf.parse("01/01/1999"), sdf.parse("01/01/2009")));
			TableTreeNode doc3 = new TravelDocNode(new TravelDoc("78901", "New Zealand",
					"Wellington", sdf.parse("01/01/2005"), sdf.parse("01/01/2015")));

			root.add(joe);
			root.add(jane);
			root.add(kid);

			joe.add(doc1);
			joe.add(doc2);

			jane.add(doc3);
		} catch (ParseException e) {
			LogFactory.getLog(getClass()).error("Failed to create test data", e);
		}

		return root;
	}

	/**
	 * An example tree table data model implementation.
	 *
	 * @author Yiannis Paschalidis
	 * @since 1.0.0
	 */
	private static final class ExampleTreeTableModel extends AbstractTreeTableDataModel {

		/**
		 * The first name column id.
		 */
		private static final int FIRST_NAME = 0;

		/**
		 * The last name column id.
		 */
		private static final int LAST_NAME = 1;

		/**
		 * The date of birth column id.
		 */
		private static final int DOB = 2;

		/**
		 * Creates an ExampleTreeTableModel.
		 *
		 * @param root the root of the tree for this model.
		 */
		private ExampleTreeTableModel(final TableTreeNode root) {
			super(root);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final TableTreeNode node, final int col) {
			PersonBean personBean = (PersonBean) node.getData();

			switch (col) {
				case FIRST_NAME:
					return personBean.getFirstName();

				case LAST_NAME:
					return personBean.getLastName();

				case DOB: {
					if (personBean.getDateOfBirth() == null) {
						return null;
					}

					return personBean.getDateOfBirth();
				}

				default:
					return null;
			}
		}

		/**
		 * Indicates whether the model supports sorting by the given column.
		 *
		 * @param col the column index.
		 * @return true, this model supports sorting on all columns.
		 */
		@Override
		public boolean isSortable(final int col) {
			return true;
		}

		/**
		 * Sorts the data by the given column. Any previous sorting should be disregarded.
		 *
		 * @param col the column to sort on
		 * @param ascending true for an ascending sort, false for descending.
		 * @return null, there is no mapping for this tree model
		 */
		@Override
		public int[] sort(final int col, final boolean ascending) {
			// Obtains the list of top level nodes, sorts them & re-add them in order
			TableTreeNode root = getRootNode();
			List<TableTreeNode> topLevelNodes = new ArrayList<>(root.getChildCount());

			for (int i = 0; i < root.getChildCount(); i++) {
				topLevelNodes.add((TableTreeNode) root.getChildAt(i));
			}

			Comparator comparator = new AbstractComparator() {
				@Override
				protected Comparable getComparable(final Object obj) {
					TableTreeNode node = (TableTreeNode) obj;
					PersonBean personBean = (PersonBean) node.getData();

					switch (col) {
						case FIRST_NAME:
							return personBean.getFirstName();

						case LAST_NAME:
							return personBean.getLastName();

						case DOB:
							return personBean.getDateOfBirth();

						default:
							return null;
					}
				}
			};

			Collections.sort(topLevelNodes, comparator);

			if (!ascending) {
				Collections.reverse(topLevelNodes);
			}

			root.removeAll();

			for (TableTreeNode node : topLevelNodes) {
				root.add(node);
			}

			return null;
		}
	}

	/**
	 * An example convenience node extension, to set a custom renderer.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class TravelDocNode extends TableTreeNode {

		/**
		 * Creates a travel doc node containing the given travel document bean.
		 *
		 * @param doc the travel document data.
		 */
		private TravelDocNode(final TravelDoc doc) {
			super(doc, TravelDocPanel.class, false);
		}
	}

	/**
	 * Example travel document bean.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class TravelDoc implements Serializable {

		/**
		 * The travel document number.
		 */
		private String documentNumber;
		/**
		 * The country which issued the travel document.
		 */
		private String countryOfIssue;
		/**
		 * The place where the travel document was issued.
		 */
		private String placeOfIssue;
		/**
		 * The date when the travel document was issued.
		 */
		private Date issueDate;
		/**
		 * The date when the travel document expires.
		 */
		private Date expiryDate;

		/**
		 * Creates a TravelDoc.
		 *
		 * @param documentNumber the document number
		 * @param countryOfIssue the country of issue
		 * @param placeOfIssue the place of issue
		 * @param issueDate the date of issue
		 * @param expiryDate the expiry date
		 */
		public TravelDoc(final String documentNumber, final String countryOfIssue,
				final String placeOfIssue,
				final Date issueDate, final Date expiryDate) {
			this.documentNumber = documentNumber;
			this.countryOfIssue = countryOfIssue;
			this.placeOfIssue = placeOfIssue;
			this.issueDate = issueDate;
			this.expiryDate = expiryDate;
		}

		/**
		 * @return Returns the documentNumber.
		 */
		public String getDocumentNumber() {
			return documentNumber;
		}

		/**
		 * @param documentNumber The documentNumber to set.
		 */
		public void setDocumentNumber(final String documentNumber) {
			this.documentNumber = documentNumber;
		}

		/**
		 * @return Returns the countryOfIssue.
		 */
		public String getCountryOfIssue() {
			return countryOfIssue;
		}

		/**
		 * @param countryOfIssue The countryOfIssue to set.
		 */
		public void setCountryOfIssue(final String countryOfIssue) {
			this.countryOfIssue = countryOfIssue;
		}

		/**
		 * @return Returns the placeOfIssue.
		 */
		public String getPlaceOfIssue() {
			return placeOfIssue;
		}

		/**
		 * @param placeOfIssue The placeOfIssue to set.
		 */
		public void setPlaceOfIssue(final String placeOfIssue) {
			this.placeOfIssue = placeOfIssue;
		}

		/**
		 * @return Returns the issueDate.
		 */
		public Date getIssueDate() {
			return issueDate;
		}

		/**
		 * @param issueDate The issueDate to set.
		 */
		public void setIssueDate(final Date issueDate) {
			this.issueDate = issueDate;
		}

		/**
		 * @return Returns the expiryDate.
		 */
		public Date getExpiryDate() {
			return expiryDate;
		}

		/**
		 * @param expiryDate The expiryDate to set.
		 */
		public void setExpiryDate(final Date expiryDate) {
			this.expiryDate = expiryDate;
		}
	}

	/**
	 * An example component to display travel document details. Expects that the supplied bean is a {@link TravelDoc}.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class TravelDocPanel extends WBeanContainer {

		/**
		 * Creates a TravelDocPanel.
		 */
		public TravelDocPanel() {
			WPanel panel = new WPanel();
			panel.setLayout(new GridLayout(5, 2));
			add(panel);

			WText documentNumber = new WText();
			WText countryOfIssue = new WText();
			WText placeOfIssue = new WText();
			WDateField issueDate = new WDateField();
			WDateField expiryDate = new WDateField();

			issueDate.setReadOnly(true);
			expiryDate.setReadOnly(true);

			documentNumber.setBeanProperty("documentNumber");
			panel.add(new WLabel("Document number"));
			panel.add(documentNumber);

			countryOfIssue.setBeanProperty("countryOfIssue");
			panel.add(new WLabel("Country of issue"));
			panel.add(countryOfIssue);

			placeOfIssue.setBeanProperty("placeOfIssue");
			panel.add(new WLabel("Place Of Issue"));
			panel.add(placeOfIssue);

			issueDate.setBeanProperty("issueDate");
			panel.add(new WLabel("Issue Date"));
			panel.add(issueDate);

			expiryDate.setBeanProperty("expiryDate");
			panel.add(new WLabel("Expiry Date"));
			panel.add(expiryDate);
		}
	}
}
