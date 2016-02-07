package com.github.bordertech.wcomponents.examples.repeater;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WNamingContext;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <p>An example demonstrating a WRepeater with editable fields with static IDs. There are a few aspects of the use of
 * static IDs with repeated components we are highlighting here:</p>
 * <ul>
 * <li> Setting a naming context's ID on the WRepeater;</li>
 * <li> Setting a custom row identifier on the WRepeater;</li>
 * <li> Setting a custom ID on the repeated component - this is important to show the effect of repetition on a static
 * ID.</li>
 * </ul>
 * <p>NOTE: We also find the parent naming context for the examples content tab and prevent this from being a naming
 * context. This to prevent the naming context ID from being pre-pended to all of the IDs we are <em>really</em>
 * interested in. This is both dangerous and unnecessary and should <strong>NEVER</strong> be done in a real
 * application.</p>
 *
 * @author Martin Shevchenko
 * @author Mark Reeves
 * @since 1.0.0
 */
public class RepeaterExampleWithStaticIDs extends WContainer {

	/**
	 * The ID to set on the contact list.
	 */
	private static final String ID_LIST = "Contacts_List";
	/**
	 * The static ID of the roles WCheckBoxSelect.
	 */
	private static final String ID_ROLE = "roles";
	/**
	 * The static ID of the phone number WPhoneNumberField.
	 */
	private static final String ID_PHONE = "phone_number";

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
	private final WTextArea printOutput;

	/**
	 * Creates a RepeaterExampleWithEditableRows.
	 */
	public RepeaterExampleWithStaticIDs() {
		repeater = new WRepeater() {
		@Override
			protected String getRowIdName(final Object rowBean, final Object contact) {
				ContactDetails bean = (ContactDetails) contact;
				return "Contact_id_" + String.valueOf(bean.getUid());
			};
		};
		repeater.setIdName(ID_LIST);
		repeater.setRepeatedComponent(new ContactContainer());
		newNameField = new WTextField();
		printOutput = new WTextArea();
		printOutput.setColumns(60);
		printOutput.setRows(5);
		createUI();
	}

	/**
	 * Creates the example UI.
	 */
	private void createUI() {
		add(new WHeading(HeadingLevel.H2, "Contacts"));
		add(repeater);
		createButtonBar();
		createAddContactSubForm();
		createPrintContactsSubForm();
	}

	/**
	 * Create the UI artefacts for the update and reset buttons.
	 */
	private void createButtonBar() {
		// Update and reset controls for the repeater.
		WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
		buttonPanel.setMargin(new Margin(6, 0 , 12, 0));
		buttonPanel.setLayout(new BorderLayout());

		WButton updateButton = new WButton("Update");
		updateButton.setImage("/image/document-save-5.png");
		updateButton.setImagePosition(WButton.ImagePosition.EAST);
		buttonPanel.add(updateButton, BorderLayout.EAST);

		WButton resetButton = new WButton("Reset");
		resetButton.setImage("/image/edit-undo-8.png");
		resetButton.setImagePosition(WButton.ImagePosition.WEST);
		resetButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				repeater.setData(fetchDataList());
			}
		});
		buttonPanel.add(resetButton, BorderLayout.WEST);
		add(buttonPanel);
		add(new WAjaxControl(updateButton, repeater));
		add(new WAjaxControl(resetButton, repeater));
	}

	/**
	 * Create the UI artefacts for the "Add contact" sub form.
	 */
	private void createAddContactSubForm() {
		add(new WHeading(HeadingLevel.H3, "Add a new contact"));

		WButton addBtn = new WButton("Add");
		addBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				addNewContact();
			}
		});
		addBtn.setImage("/image/address-book-new.png");
		newNameField.setDefaultSubmitButton(addBtn);

		WContainer container = new WContainer();
		container.add(newNameField);
		container.add(addBtn);

		WFieldLayout layout = new WFieldLayout();
		add(layout);
		layout.addField("New contact name", container);
		add(new WAjaxControl(addBtn, new AjaxTarget[]{repeater, newNameField}));
	}

	/**
	 * Create the UI artefacts for the "Print contacts" sub form.
	 */
	private void createPrintContactsSubForm() {
		add(new WHeading(HeadingLevel.H3, "Print to CSV"));

		WButton printBtn = new WButton("Print");
		printBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				printDetails();
			}
		});
		printBtn.setImage("/image/document-print.png");
		printBtn.setImagePosition(WButton.ImagePosition.EAST);

		WFieldLayout layout = new WFieldLayout();
		add(layout);
		layout.setMargin(new Margin(12, 0, 0, 0));
		layout.addField("Print output", printOutput);
		layout.addField((WLabel) null, printBtn);
		add(new WAjaxControl(printBtn, printOutput));
	}

	/**
	 * Adds a new contact to the list of contacts. The contact's name is obtained from {@link newNameField}.
	 */
	private void addNewContact() {
		String name = newNameField.getText();
		if (name != null && !"".equals(name)) {
			List list = new ArrayList(repeater.getBeanList());
			list.add(new ContactDetails(name, null, new String[0]));
			repeater.setBeanList(list);
		}
		newNameField.setText("");
	}

	/**
	 * Write the list of contacts into the WTextArea printOutput.
	 */
	private void printDetails() {
		StringBuilder builder = new StringBuilder("\"Name\",\"Phone\",\"Roles\",\"Identifier\"\n");
		for (Object contact : repeater.getBeanList()) {
			builder.append(contact).append('\n');
		}
		printOutput.setText(builder.toString());
	}

	/**
	 * Override to initialise some data the first time through.
	 *
	 * @param request The request being responded to.
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		if (!this.isInitialised()) {
			// Give the repeater the list of data to display.
			repeater.setData(fetchDataList());






			// THIS IS THE BIT YOUSHOULD NEVER EVER DO SO LOOK AWAY NOW...
			WNamingContext nc = (WNamingContext) WebUtilities.getAncestorOfClass(WNamingContext.class, this);
			if (null != nc) {
				nc.setNamingContext(false);
			}
			// OK, WE ARE SAFE AGAIN

			// Remember that we've done the initialisation.
			this.setInitialised(true);
		}
	}

	/**
	 * The component to be used to render one row. Note that this component remembers the given data object and keeps it
	 * up to date. The component static IDs are purely for demonstration purposes.
	 *
	 * @author Martin Shevchenko
	 * @author Mark Reeves
	 */
	public class ContactContainer extends WDataRenderer {

		/**
		 * Used to display/modify the contact's phone number.
		 */
		private final WPhoneNumberField phoneNumField = new WPhoneNumberField();

		/**
		 * Used to display/modify the contact's roles.
		 */
		private final WCheckBoxSelect roleSelect = new WCheckBoxSelect(new String[]{"a", "b", "c"});

		/**
		 * A button to delete a contact if we don't like them any more.
		 */
		private final WButton deleteButton = new WButton("Delete");

		/**
		 * Creates a ContactContainer.
		 */
		public ContactContainer() {
			roleSelect.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
			roleSelect.setFrameless(true);
			roleSelect.setIdName(ID_ROLE);
			phoneNumField.setIdName(ID_PHONE);
			deleteButton.setRenderAsLink(true);
			deleteButton.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					removeRow();
				}
			});
			deleteButton.setMessage("Are you really sure?");
			deleteButton.setImage("/image/remove.png");
			createUI();
		}

		/**
		 * Creates the UI artefacts for the ContactContainer.
		 */
		private void createUI() {
			WText headingText = new WText();
			WHeading contactName = new WHeading(HeadingLevel.H3, new WDecoratedLabel(null, headingText, deleteButton));
			headingText.setBeanProperty("name");
			add(contactName);

			WFieldLayout fieldLayout = new WFieldLayout();
			fieldLayout.addField("Phone", phoneNumField);
			fieldLayout.addField("Roles", roleSelect);
			add(fieldLayout);
			add(new WAjaxControl(deleteButton, repeater));
		}

		/**
		 * Remove a contact from the list.
		 */
		private void removeRow() {
			UIContext uic = UIContextHolder.getCurrent();
			if (!(uic instanceof WRepeater.SubUIContext)) {
				return;
			}

			List list = new ArrayList(repeater.getBeanList());
			list.remove((ContactDetails) repeater.getRowBeanForSubcontext((WRepeater.SubUIContext) uic));
			repeater.setData(list);
		}

		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);
			if (!isInitialised()) {
				setInitialised(true);
				UIContext uic = UIContextHolder.getCurrent();
				if (!(uic instanceof WRepeater.SubUIContext)) {
					return;
				}
				WRepeater.SubUIContext subUic = (WRepeater.SubUIContext) uic;
			}
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
		 * The contact unique id of the contact.
		 */
		private final int uid;

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
			// This would normally be something unique and sensible in your data source like a record identifier,
			// database unique key etc.
			// The mucking about with sign is just because I am using UUID because I am LAZY.
			int i = UUID.randomUUID().hashCode();
			i = i > 0 ? i : i * -1;
			this.uid = i;
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
		 * @return the contact's unique id.
		 */
		public int getUid() {
			return uid;
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
			StringBuilder builder = new StringBuilder("\"");
			builder.append(name);
			builder.append("\",\"");
			builder.append(phoneNumber == null ? "" : phoneNumber);
			builder.append("\",\"");
			for (int i = 0; i < roles.size(); i++) {
				if (i > 0) {
					builder.append(',');
				}
				builder.append(roles.get(i));
			}
			builder.append("\",\"");
			builder.append(String.valueOf(uid));
			builder.append("\"");

			return builder.toString();
		}
	}
}
