package com.github.bordertech.wcomponents.examples.repeater;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An example demonstrating a WRepeater with editable fields.
 *
 * @author Martin Shevchenko
 * @author Mark Reeves
 * @since 1.0.0
 */
public class RepeaterExampleWithEditableRows extends WContainer {

	/**
	 * The repeater which is used to display the data.
	 */
	private final WRepeater repeater;

	/**
	 * A text field used to enter in a new contact name.
	 */
	private final WTextField newNameField;

	/**
	 * Used to display a text-only view of the data when the "print" button is pressed.
	 */
	private final WTextArea console;

	/**
	 * Creates a RepeaterExampleWithEditableRows.
	 */
	public RepeaterExampleWithEditableRows() {
		repeater = new WRepeater();
		repeater.setRepeatedComponent(new PhoneNumberEditPanel());
		newNameField = new WTextField();
		console = new WTextArea();
		console.setColumns(60);
		console.setRows(5);
		createExampleUi();
	}

	/**
	 * Add all the required UI artefacts for this example.
	 */
	private void createExampleUi() {
		add(new WHeading(HeadingLevel.H2, "Contacts"));

		add(repeater);
		WButton addBtn = new WButton("Add");
		addBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				addNewContact();
			}
		});
		newNameField.setDefaultSubmitButton(addBtn);

		WButton printBtn = new WButton("Print");
		printBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				printEditedDetails();
			}
		});

		WFieldLayout layout = new WFieldLayout();
		add(layout);
		layout.addField("New contact name", newNameField);
		layout.addField((WLabel) null, addBtn);
		layout.addField("Print output", console);
		layout.addField((WLabel) null, printBtn);

		// Ajax controls to make things zippier
		add(new WAjaxControl(addBtn, new AjaxTarget[]{repeater, newNameField}));
		add(new WAjaxControl(printBtn, console));
	}

	/**
	 * Adds a new contact to the list of contacts. The contact's name is obtained from {@link newNameField}.
	 */
	private void addNewContact() {
		List list = new ArrayList(repeater.getBeanList());
		list.add(new ContactDetails(newNameField.getText(), null, new String[0]));
		newNameField.setText("");
		repeater.setBeanList(list);
	}

	/**
	 * Write the list of contacts into the textarea console. Any modified phone numbers should be printed out.
	 */
	private void printEditedDetails() {
		StringBuilder buf = new StringBuilder();

		for (Object contact : repeater.getBeanList()) {
			buf.append(contact).append('\n');
		}

		console.setText(buf.toString());
	}

	/**
	 * Override to initialise some data the first time through.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		if (!this.isInitialised()) {
			// Give the repeater the list of data to display.
			repeater.setData(fetchDataList());

			// Remember that we've done the initialisation.
			this.setInitialised(true);
		}
	}

	/**
	 * The component to be used to render one row. Note that this component remembers the given data object and keeps it
	 * up to date.
	 *
	 * @author Martin Shevchenko
	 * @author Mark Reeves
	 */
	public static class PhoneNumberEditPanel extends WDataRenderer {

		/**
		 * Used to display/modify the contact's phone number.
		 */
		private final WTextField phoneNumField = new WTextField();

		/**
		 * Used to display/modify the contact's roles.
		 */
		private final WCheckBoxSelect roleSelect = new WCheckBoxSelect(new String[]{"a", "b", "c"});

		/**
		 * Creates a PhoneNumberEditPanel.
		 */
		public PhoneNumberEditPanel() {
			roleSelect.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
			roleSelect.setFrameless(true);
			createUI();
		}

		/**
		 * Adds the UI artefacts for this PhoneNumberEditPanel.
		 */
		private void createUI() {
			WHeading contactName = new WHeading(HeadingLevel.H3, "");
			contactName.setBeanProperty("name");
			add(contactName);

			WFieldLayout fieldLayout = new WFieldLayout();
			fieldLayout.addField("Phone", phoneNumField);
			fieldLayout.addField("Roles", roleSelect);
			add(fieldLayout);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void updateData(final Object data) {
			ContactDetails details = (ContactDetails) data;
			details.setPhoneNumber(phoneNumField.getText());
			details.setRoles((List<String>) roleSelect.getSelected());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void updateComponent(final Object data) {
			ContactDetails details = (ContactDetails) data;
			phoneNumField.setText(details.getPhoneNumber());
			roleSelect.setSelected(details.getRoles());
		}
	}

	/**
	 * Retrieves dummy data used by this example.
	 *
	 * @return the list of data for this example.
	 */
	private static List<ContactDetails> fetchDataList() {
		List<ContactDetails> list = new ArrayList<>();
		list.add(new ContactDetails("David", "1234", new String[]{"a", "b"}));
		list.add(new ContactDetails("Jun", "1111", new String[]{"c"}));
		list.add(new ContactDetails("Martin", null, new String[]{"b"}));

		return list;
	}

	/**
	 * A simple data object.
	 *
	 * @author Martin Shevchenko
	 * @author Mark Reeves
	 */
	public static class ContactDetails implements Serializable {

		/**
		 * The contact name.
		 */
		private String name;
		/**
		 * The contact phone number.
		 */
		private String phoneNumber;
		/**
		 * The contact roles.
		 */
		private List<String> roles;

		/**
		 * Creates a ContactDetails.
		 *
		 * @param name the contact name.
		 * @param phoneNumber the contact phone number.
		 * @param roles the contact roles.
		 */
		public ContactDetails(final String name, final String phoneNumber, final String[] roles) {
			this.name = name;
			this.phoneNumber = phoneNumber;
			this.roles = new ArrayList<>(Arrays.asList(roles));
		}

		/**
		 * @return the contact name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the contact name.
		 *
		 * @param name the contact name.
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * @return the contact phone number.
		 */
		public String getPhoneNumber() {
			return phoneNumber;
		}

		/**
		 * Sets the contact phone number.
		 *
		 * @param phoneNumber the contact phone number.
		 */
		public void setPhoneNumber(final String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		/**
		 * @return the contact roles.
		 */
		public List<String> getRoles() {
			return roles;
		}

		/**
		 * Sets the contact roles.
		 *
		 * @param roles the contact roles.
		 */
		public void setRoles(final List<String> roles) {
			this.roles = roles;
		}

		/**
		 * @return a textual representation of this Contact.
		 */
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder(name);
			buf.append(": ");
			buf.append(phoneNumber == null ? "" : phoneNumber);
			buf.append(": ");

			for (int i = 0; i < roles.size(); i++) {
				if (i > 0) {
					buf.append(',');
				}

				buf.append(roles.get(i));
			}

			return buf.toString();
		}
	}
}
