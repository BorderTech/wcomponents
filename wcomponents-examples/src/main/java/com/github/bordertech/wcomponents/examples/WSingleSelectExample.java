package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Option;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WSingleSelect;
import com.github.bordertech.wcomponents.examples.common.ExampleLookupTable.TableWithNullOption;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This example demonstrates usage of the {@link WSingleSelect} component.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WSingleSelectExample extends WContainer {

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
	 * Creates a WSingleSelectExample.
	 */
	public WSingleSelectExample() {
		List<Person> people = new ArrayList<>();
		people.add(new Person("123", "Joe", "Bloggs"));
		people.add(new Person("456", "Jane", "Doe"));
		people.add(new Person("789", "Fred", "Nerk"));

		List<PersonOption> peopleOptions = new ArrayList<>();
		for (Person person : people) {
			peopleOptions.add(new PersonOption(person));
		}

		WSingleSelect sSelect = new WSingleSelect(OPTIONS_ARRAY);
		sSelect.setRows(3);
		addFieldToLayout(sSelect, "Simple Select list", null);

		sSelect = new WSingleSelect(OPTIONS_ARRAY);
		sSelect.setSelected(OPTIONS_ARRAY[1]);
		sSelect.setRows(3);
		addFieldToLayout(sSelect, "Simple Select list with default selection", null);

		sSelect = new WSingleSelect(OPTIONS_NULL_ARRAY);
		addFieldToLayout(sSelect, "Simple Select list, with \"null\" option", null);

		sSelect = new WSingleSelect(OPTIONS_NULL_ARRAY) {
			@Override
			public String getDesc(final Object option, final int index) {
				if (option == null) {
					return "Select one";
				} else {
					return super.getDesc(option, index);
				}
			}
		};
		addFieldToLayout(sSelect, "Simple Select list, with custom \"null\" selection text", null);

		sSelect = new WSingleSelect(OPTIONS_ARRAY);
		sSelect.setSelected(OPTIONS_ARRAY[1]);
		sSelect.setReadOnly(true);
		addFieldToLayout(sSelect, "Simple Select list, read-only", null);

		sSelect = new WSingleSelect(OPTIONS_ARRAY);
		sSelect.setSelected(OPTIONS_ARRAY[1]);
		sSelect.setDisabled(true);
		addFieldToLayout(sSelect, "Simple Select list, disabled", null);

		sSelect = new WSingleSelect(people);
		sSelect.setRows(3);
		addFieldToLayout(sSelect, "Select list with a list of beans",
			"This is an example sSelect with data from a non-textual source.");

		sSelect = new WSingleSelect(peopleOptions);
		sSelect.setRows(3);
		addFieldToLayout(sSelect, "Select list with a list of options", null);

		OptionGroup stringGroup = new OptionGroup("Strings", OPTIONS_LIST);
		OptionGroup peopleGroup = new OptionGroup("People", people);
		sSelect = new WSingleSelect(new Object[]{stringGroup, peopleGroup, "Ungrouped option"});
		sSelect.setRows(5);
		addFieldToLayout(sSelect, "Select list with option groups", null);

		sSelect = new WSingleSelect("icao");
		sSelect.setRows(10);
		addFieldToLayout(sSelect, "Select list with cached data from a look up table",
			"see ExampleLookupTable for data");

		sSelect = new WSingleSelect(new TableWithNullOption("icao"));
		sSelect.setRows(10);
		addFieldToLayout(sSelect,
			"Select list with cached data from a look up table with Null Option",
			"see ExampleLookupTable for data");

		sSelect = new WSingleSelect(new TableWithNullOption("icao", "Select one please"));
		sSelect.setRows(10);
		addFieldToLayout(sSelect, "Select list with cached data with a custom null option",
			"see ExampleLookupTable for data");

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
	 * Applications can wrap options inside lists in order to provide custom
	 * text and values for the drop down.
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
