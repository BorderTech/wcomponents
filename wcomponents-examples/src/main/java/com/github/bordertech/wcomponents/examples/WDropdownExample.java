package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Option;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.examples.common.ExampleLookupTable.TableWithNullOption;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This example demonstrates usage of the {@link WDropdown} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDropdownExample extends WContainer {

	/**
	 * Some array data for the drop downs.
	 */
	private static final String[] OPTIONS_ARRAY = {"option 1", "option 2", "option 3", "Adoption 1", "Adoption 2", "adoption 3", "the", "quick", "brown", "fox", "jumped", "over", "the", "lazy", "dogs"};

	/**
	 * Some array data for the drop downs.
	 */
	private static final String[] OPTIONS_NULL_ARRAY = {null, "option 1", "option 2", "option 3", "Adoption 1", "Adoption 2", "adoption 3", "the", "quick", "brown", "fox", "jumped", "over", "the", "lazy", "dogs"};

	/**
	 * Some list data for the drop downs.
	 */
	private static final List<String> OPTIONS_LIST = Arrays.asList(OPTIONS_ARRAY);

	/**
	 * the screen layout.
	 */
	private final WFieldLayout layout = new WFieldLayout();

	/**
	 * Creates a WDropdownExample.
	 */
	public WDropdownExample() {
		List<Person> people = new ArrayList<>();
		people.add(new Person("123", "Joe", "Bloggs"));
		people.add(new Person("456", "Jane", "Doe"));
		people.add(new Person("789", "Fred", "Nerk"));

		List<PersonOption> peopleOptions = new ArrayList<>();
		for (Person person : people) {
			peopleOptions.add(new PersonOption(person));
		}

		WDropdown dropdown = new WDropdown(OPTIONS_ARRAY);
		addFieldToLayout(dropdown, "Simple drop-down", null);

		dropdown = new WDropdown(OPTIONS_ARRAY);
		dropdown.setSelected(OPTIONS_ARRAY[1]);
		addFieldToLayout(dropdown, "Simple drop-down with default selection", null);

		dropdown = new WDropdown(OPTIONS_NULL_ARRAY);
		addFieldToLayout(dropdown, "Simple drop-down, with \"null\" option", null);

		dropdown = new WDropdown(OPTIONS_NULL_ARRAY) {
			@Override
			public String getDesc(final Object option, final int index) {
				if (option == null) {
					return "Select one";
				} else {
					return super.getDesc(option, index);
				}
			}
		};
		addFieldToLayout(dropdown, "Simple drop-down, with custom \"null\" selection text", null);

		dropdown = new WDropdown(people);
		addFieldToLayout(dropdown, "Drop-down with a list of beans",
				"This is an example dropdown with data from a non-textual source.");

		dropdown = new WDropdown(peopleOptions);
		addFieldToLayout(dropdown, "Drop-down with a list of options", null);

		OptionGroup stringGroup = new OptionGroup("Strings", OPTIONS_LIST);
		OptionGroup peopleGroup = new OptionGroup("People", people);
		dropdown = new WDropdown(new Object[]{stringGroup, peopleGroup, "Ungrouped option"});
		addFieldToLayout(dropdown, "Drop-down with option groups", null);

		dropdown = new WDropdown(OPTIONS_ARRAY);
		dropdown.setType(WDropdown.DropdownType.COMBO);
		addFieldToLayout(dropdown, "Combo drop-down",
				"This is an example dropdown which allows user input");

		dropdown = new WDropdown("icao");
		addFieldToLayout(dropdown, "Drop-down with cached data from a look up table",
				"see ExampleLookupTable for data");

		dropdown = new WDropdown(new TableWithNullOption("icao"));
		addFieldToLayout(dropdown,
				"Drop-down with cached data from a look up table with Null Option",
				"see ExampleLookupTable for data");

		dropdown = new WDropdown(new TableWithNullOption("icao", "Select one please"));
		addFieldToLayout(dropdown, "Drop-down with cached data with a custom null option",
				"see ExampleLookupTable for data");

		dropdown = new WDropdown("icao");
		dropdown.setType(WDropdown.DropdownType.COMBO);
		addFieldToLayout(dropdown, "Combo with cached data from a look up table",
				"see ExampleLookupTable for data");

		dropdown = new WDropdown(new TableWithNullOption("icao"));
		dropdown.setType(WDropdown.DropdownType.COMBO);
		addFieldToLayout(dropdown, "Combo with cached data from a look up table with Null Option",
				"Null options make no sense in COMBOs");

		dropdown = new WDropdown(new TableWithNullOption("icao", "Select one please"));
		dropdown.setType(WDropdown.DropdownType.COMBO);
		addFieldToLayout(dropdown, "Combo with cached data with a custom null option",
				"You probably shouldn't do this since it defeats the purpose of a combo.");

		dropdown = new WDropdown(OPTIONS_ARRAY);
		dropdown.setDisabled(true);
		addFieldToLayout(dropdown, "Disabled drop-down", null);

		dropdown = new WDropdown(OPTIONS_ARRAY);
		dropdown.setSelected(OPTIONS_ARRAY[1]);
		dropdown.setDisabled(true);
		addFieldToLayout(dropdown, "Disabled drop-down with default selection", null);

		dropdown = new WDropdown(OPTIONS_ARRAY);
		dropdown.setType(WDropdown.DropdownType.COMBO);
		dropdown.setDisabled(true);
		addFieldToLayout(dropdown, "Disabled combo ", null);

		dropdown = new WDropdown("icao");
		dropdown.setDisabled(true);
		addFieldToLayout(dropdown, "Disabled drop-down with cached data from a look up table", null);

		dropdown = new WDropdown("icao");
		dropdown.setType(WDropdown.DropdownType.COMBO);
		dropdown.setDisabled(true);
		addFieldToLayout(dropdown, "Disabled combo with cached data from a look up table", null);

		add(layout);
		add(new WButton("submit"));
	}

	/**
	 * A simple "person" bean used by the example.
	 */
	private static final class Person implements Serializable {

		/**
		 * The person's first name.
		 */
		private String firstName;

		/**
		 * The person's last name.
		 */
		private String lastName;

		/**
		 * The person's id.
		 */
		private String id;

		/**
		 * Creates a Person bean.
		 *
		 * @param id the person id.
		 * @param firstName the first name.
		 * @param lastName the last name.
		 */
		private Person(final String id, final String firstName, final String lastName) {
			this.id = id;
			this.firstName = firstName;
			this.lastName = lastName;
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
		 * @return Returns the id.
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id The id to set.
		 */
		public void setId(final String id) {
			this.id = id;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return firstName + ' ' + lastName;
		}
	}

	/**
	 * Applications can wrap options inside lists in order to provide custom text and values for the drop down.
	 */
	private static final class PersonOption implements Option, Serializable {

		/**
		 * The person bean for this option.
		 */
		private final Person person;

		/**
		 * Creates a PersonOption.
		 *
		 * @param person the person to wrap.
		 */
		private PersonOption(final Person person) {
			this.person = person;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getCode() {
			return person.getId();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDesc() {
			return person.getLastName() + ", " + person.getFirstName();
		}
	}

	/**
	 * Adds a field to the example's layout.
	 *
	 * @param input the input field to add.
	 * @param labelText the label text for the field.
	 * @param labelHint the optional label hint for the field.
	 */
	private void addFieldToLayout(final WComponent input, final String labelText,
			final String labelHint) {
		WField field = layout.addField(labelText, input);

		if (labelHint != null) {
			field.getLabel().setHint(labelHint);
		}
	}
}
