package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.AbstractTableDataModel;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.ExpandMode;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This example shows the use of an action from within a {@link WDataTable} table cell.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TableCellWithActionExample extends WPanel {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TableCellWithActionExample.class);

	/**
	 * Used to display the output from the example actions.
	 */
	private final WText actionText = new WText();

	/**
	 * Creates a DataTableActionExample.
	 */
	public TableCellWithActionExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		WDataTable table = createTable();

		add(table);
		add(actionText);
	}

	/**
	 * @return the WText used to display an effect of the action
	 */
	public WText getActionText() {
		return actionText;
	}

	/**
	 * Creates and configures the table to be used by the example. The table is configured with global rather than user
	 * data. Although this is not a realistic scenario, it will suffice for this example.
	 *
	 * @return a new configured table.
	 */
	private WDataTable createTable() {
		WDataTable table = new WDataTable();
		table.addColumn(new WTableColumn("First name", new WTextField()));
		table.addColumn(new WTableColumn("Last name", new WTextField()));
		table.addColumn(new WTableColumn("DOB", new WDateField()));
		table.addColumn(new WTableColumn("Action", new ExampleButton()));
		table.setExpandMode(ExpandMode.CLIENT);

		table.setDataModel(createTableModel());

		return table;
	}

	/**
	 * Parses a date string.
	 *
	 * @param dateString the date string to parse
	 * @return a date corresponding to the given dateString, or null on error.
	 */
	private static Date parse(final String dateString) {
		try {
			return new SimpleDateFormat("dd/mm/yyyy").parse(dateString);
		} catch (ParseException e) {
			LOG.error("Error parsing date: " + dateString, e);
			return null;
		}
	}

	/**
	 * Creates a table data model containing some dummy person data.
	 *
	 * @return a new data model.
	 */
	private TableDataModel createTableModel() {
		return new AbstractTableDataModel() {
			/**
			 * Column id for the first name column.
			 */
			private static final int FIRST_NAME = 0;

			/**
			 * Column id for the last name column.
			 */
			private static final int LAST_NAME = 1;

			/**
			 * Column id for the date of birth column.
			 */
			private static final int DOB = 2;

			/**
			 * Column id for the action column.
			 */
			private static final int BUTTON = 3;

			private final List<Person> data
					= Arrays.asList(new Person[]{new Person(123, "Joe", "Bloggs",
				parse("01/02/1973")),
				new Person(456, "Jane", "Bloggs", parse("04/05/1976")),
				new Person(789, "Kid", "Bloggs", parse("31/12/1999"))});

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object getValueAt(final int row, final int col) {
				Person person = data.get(row);

				switch (col) {
					case FIRST_NAME:
						return person.getFirstName();

					case LAST_NAME:
						return person.getLastName();

					case DOB: {
						if (person.getDateOfBirth() == null) {
							return null;
						}

						return person.getDateOfBirth();
					}

					case BUTTON: {
						return person;
					}

					default:
						return null;
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int getRowCount() {
				return data.size();
			}
		};
	}

	/**
	 * Example Person bean.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class Person implements Serializable {

		private int id;
		private String firstName;
		private String lastName;
		private Date dateOfBirth;

		/**
		 * Creates a Person with the given attributes.
		 *
		 * @param id the person id.
		 * @param firstName the first name.
		 * @param lastName the last name.
		 * @param dateOfBirth the date of birth.
		 */
		public Person(final int id, final String firstName, final String lastName,
				final Date dateOfBirth) {
			this.id = id;
			this.firstName = firstName;
			this.lastName = lastName;
			this.dateOfBirth = dateOfBirth;
		}

		/**
		 * @return Returns the firstName.
		 */
		public String getFirstName() {
			return firstName;
		}

		/**
		 * @param firstName The firstName to set.
		 */
		public void setFirstName(final String firstName) {
			this.firstName = firstName;
		}

		/**
		 * @return Returns the lastName.
		 */
		public String getLastName() {
			return lastName;
		}

		/**
		 * @param lastName The lastName to set.
		 */
		public void setLastName(final String lastName) {
			this.lastName = lastName;
		}

		/**
		 * @return Returns the dateOfBirth.
		 */
		public Date getDateOfBirth() {
			return dateOfBirth;
		}

		/**
		 * @param dateOfBirth The dateOfBirth to set.
		 */
		public void setDateOfBirth(final Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}

		/**
		 * @return the person id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param id the id to set.
		 */
		public void setId(final int id) {
			this.id = id;
		}
	}

	/**
	 * An example button showing how to embed an action in cell within a table.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class ExampleButton extends WButton {

		/**
		 * Creates an ExampleButton.
		 */
		public ExampleButton() {
			setBeanProperty("id");
			setText("Action");

			setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					Person person = (Person) getBean();

					TableCellWithActionExample example = WebUtilities.getAncestorOfClass(
							TableCellWithActionExample.class, ExampleButton.this);
					example.getActionText().setText(
							"Action for: " + person.getFirstName() + ' ' + person.getLastName());
				}
			});
		}
	}
}
