package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Option;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Examples of {@link WCheckBoxSelect} usage.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WCheckBoxSelectExample extends WContainer {

	/**
	 * No selection text.
	 */
	private static final String NO_SELECTION = "No selection";

		/**
	 * Creates a WCheckBoxSelectExample.
	 */
	public WCheckBoxSelectExample() {
		addInteractiveExamples();
		// disabled state
		// NOTE: you would normally use WSubordinateControl to set a disabled state
		addDisabledExamples();
		// read only state
		addReadOnlyExamples();
		addAntiPatternExamples();
	}

	/**
	 * Simple interactive-state WCheckBoxSelect examples.
	 */
	private void addInteractiveExamples() {
		add(new WHeading(HeadingLevel.H2, "Simple WCheckBoxSelect examples"));
		addExampleUsingLookupTable();
		addExampleUsingArrayList();
		addExampleUsingStringArray();
		addInsideAFieldLayoutExamples();
		add(new WHeading(HeadingLevel.H2, "Examples showing LAYOUT properties"));
		addFlatSelectExample();
		addColumnSelectExample();
		addSingleColumnSelectExample();
		add(new WHeading(HeadingLevel.H2, "WCheckBoxSelect showing the frameless state"));
		add(new WHeading(HeadingLevel.H3, "Normal (with frame)"));
		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setToolTip("Make a selection");
		add(new WHeading(HeadingLevel.H3, "Without frame"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setFrameless(true);
		select.setToolTip("Make a selection (no frame)");
	}

	/**
	 * This example creates the WCheckBoxSelect using an a look up table. All other optional properties are in their default state.
	 * <p>Note for Framework devs: the unit tests for this Example are used to test the Selenium WebElement extension and this example is expected
	 * to be: the first WCheckBoxSelect in the example; and to be interactive; and to have the 9 options of the "australian_state" lookup table.
	 */
	private void addExampleUsingLookupTable() {
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect created using a lookup table"));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		final WTextField text = new WTextField();
		text.setReadOnly(true);
		text.setText(NO_SELECTION);

		WButton update = new WButton("Select");
		update.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String output = select.getSelected().isEmpty() ? NO_SELECTION : "The selected states are: "
						+ select.getSelected();
				text.setText(output);
			}
		});
		select.setDefaultSubmitButton(update);
		add(new WLabel("Select a state or territory", select));
		add(select);
		add(update);
		add(text);
		add(new WAjaxControl(update, text));
	}

	/**
	 * This example creates the WCheckBoxSelect from an array of Strings.
	 */
	private void addExampleUsingStringArray() {
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect created using a String array"));
		String[] options = new String[]{"Dog", "Cat", "Bird", "Turtle"};
		final WCheckBoxSelect select = new WCheckBoxSelect(options);
		select.setToolTip("Animals");
		select.setMandatory(true);
		final WTextField text = new WTextField();
		text.setReadOnly(true);
		text.setText(NO_SELECTION);

		WButton update = new WButton("Select Animals");
		update.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String output = select.getSelected().isEmpty() ? NO_SELECTION : "The selected animals are: "
						+ select.getSelected();
				text.setText(output);
			}
		});
		select.setDefaultSubmitButton(update);
		WLabel animalLabel = new WLabel("A selection is required", select);
		animalLabel.setHint("mandatory");
		add(animalLabel);
		add(select);
		add(update);
		add(text);
		add(new WAjaxControl(update, text));
	}

	/**
	 * This example creates the WCheckBoxSelect from a List of CarOptions.
	 */
	private void addExampleUsingArrayList() {
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect created using an array list of options"));
		List<CarOption> options = new ArrayList<>();
		options.add(new CarOption("1", "Ferrari", "F-360"));
		options.add(new CarOption("2", "Mercedez Benz", "amg"));
		options.add(new CarOption("3", "Nissan", "Skyline"));
		options.add(new CarOption("5", "Toyota", "Prius"));

		final WCheckBoxSelect select = new WCheckBoxSelect(options);
		select.setToolTip("Cars");
		final WTextField text = new WTextField();
		text.setReadOnly(true);
		text.setText(NO_SELECTION);

		WButton update = new WButton("Select Cars");
		update.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String output = select.getSelected().isEmpty() ? NO_SELECTION : "The selected cars are: "
						+ select.getSelected();
				text.setText(output);
			}
		});
		select.setDefaultSubmitButton(update);

		add(select);
		add(update);
		add(text);
		add(new WAjaxControl(update, text));
	}

	/**
	 * When a WCheckBoxSelect is added to a WFieldLayout the legend is moved. The first CheckBoxSelect has a frame, the
	 * second doesn't
	 */
	private void addInsideAFieldLayoutExamples() {
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect inside a WFieldLayout"));
		add(new ExplanatoryText("When a WCheckBoxSelect is inside a WField its label is exposed in a way which appears and behaves like a regular "
				+ "HTML label. This allows WCheckBoxSelects to be used in a layout with simple form controls (such as WTextField) and produce a "
				+ "consistent and predicatable interface. The third example in this set uses a null label and a toolTip to hide the labelling "
				+ "element. This can lead to user confusion and is not recommended."));
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		String[] options = new String[]{"Dog", "Cat", "Bird", "Turtle"};
		WCheckBoxSelect select = new WCheckBoxSelect(options);
		layout.addField("Select some animals", select);
		String[] options2 = new String[]{"Parrot", "Galah", "Cockatoo", "Lyre"};

		select = new WCheckBoxSelect(options2);
		layout.addField("Select some birds", select);
		select.setFrameless(true);

		// a tooltip can be used as a label stand-in even in a WField
		String[] options3 = new String[]{"Carrot", "Beet", "Brocolli", "Bacon - the perfect vegetable"};
		select = new WCheckBoxSelect(options3);
		layout.addField((WLabel) null, select);
		select.setToolTip("Veggies");

		select = new WCheckBoxSelect("australian_state");
		layout.addField("Select a state", select).getLabel().setHint("This is an ajax trigger");
		add(new WAjaxControl(select, layout));
	}

	/**
	 * WCheckBoxSelect layout options These examples show the various ways to lay out the options in a WCheckBoxSelect
	 * NOTE: the default (if no buttonLayout is set) is LAYOUT_STACKED. adds a WCheckBoxSelect with LAYOUT_FLAT
	 */
	private void addFlatSelectExample() {
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect with flat layout"));
		add(new ExplanatoryText("Setting the layout to FLAT will make thecheck boxes be rendered in a horizontal line. They will wrap when they reach"
				+ " the edge of the parent container."));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
		add(select);
	}

	/**
	 * adds a WCheckBoxSelect with LAYOUT_COLUMN in 2 columns.
	 */
	private void addColumnSelectExample() {
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect laid out in columns"));
		add(new ExplanatoryText("Setting the layout to COLUMN will make the check boxes be rendered in 'n' columns. The number of columns is"
				+ " determined by the layoutColumnCount property."));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(2);
		add(select);

		add(new WHeading(HeadingLevel.H3, "Options equal to columns"));
		String[] options = new String[]{"Dog", "Cat", "Bird"};
		final WCheckBoxSelect select2 = new WCheckBoxSelect(options);
		select2.setToolTip("Animals");
		select2.setButtonColumns(3);
		final WTextField text = new WTextField();
		text.setReadOnly(true);
		text.setText(NO_SELECTION);

		WButton update = new WButton("Select Animals");
		update.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String output = select2.getSelected().isEmpty() ? NO_SELECTION : "The selected animals are: "
						+ select2.getSelected();
				text.setText(output);
			}
		});
		select2.setDefaultSubmitButton(update);

		add(select2);
		add(update);
		add(text);
		add(new WAjaxControl(update, text));
	}

	/**
	 * adds a WCheckBoxSelect with LAYOUT_COLUMN in 1 column simply by not setting the number of columns. This is
	 * superfluous as you should use LAYOUT_STACKED (the default) instead.
	 */
	private void addSingleColumnSelectExample() {
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect laid out in a single column"));
		add(new ExplanatoryText("When layout is COLUMN, setting the layoutColumnCount property to one, or forgetting to set it at all (default is "
				+ "one) is a little bit pointless."));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		add(select);
	}

	/**
	 * Examples of readonly states.
	 */
	private void addReadOnlyExamples() {
		add(new WHeading(HeadingLevel.H3, "Read-only WCheckBoxSelect examples"));
		add(new ExplanatoryText("These examples all use the same list of options: the states and territories list from the editable examples above."
				+ " When the readOnly state is specified only those options which are selected are output."));

		// NOTE: when there are 0 or 1 selections the frame is not rendered.
		add(new WHeading(HeadingLevel.H4, "Read only with no selection"));
		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with no selection");
		add(new WText("end of unselected read only example"));

		add(new WHeading(HeadingLevel.H4, "Read only with one selection"));
		select = new SelectWithSingleSelected("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with one selection");

		add(new WHeading(HeadingLevel.H4, "Read only with many selections and no frame"));
		select = new SelectWithSingleSelected("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with many selections");
		select.setFrameless(true);

		add(new WHeading(HeadingLevel.H4, "Read only with many selections and COLUMN layout"));
		select = new SelectWithSingleSelected("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with many selections");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);

		// read only in a WFieldLayout
		add(new WHeading(HeadingLevel.H4, "Read only in a WFieldLayout"));
		add(new ExplanatoryText("Each read only example is preceded by an editable example with the same options and selection. This is to ensure the"
				+ " CSS works properly."));
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);

		// no selections
		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		layout.addField("No selections were made", select);

		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		select.setReadOnly(true);
		layout.addField("No selections were made (read only)", select);

		// one selection
		select = new SelectWithSingleSelected("australian_state");
		select.setFrameless(true);
		layout.addField("One selection was made", select);

		select = new SelectWithSingleSelected("australian_state");
		select.setFrameless(true);
		select.setReadOnly(true);
		layout.addField("One selection was made (read only)", select);

		// many selections
		select = new SelectWithManySelected("australian_state");
		layout.addField("Many selections with frame", select);

		select = new SelectWithManySelected("australian_state");
		select.setReadOnly(true);
		layout.addField("Many selections with frame (read only)", select);

		// columns with selections
		select = new SelectWithSingleSelected("australian_state");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		select.setFrameless(true);
		layout.addField("many selections, frameless, COLUMN layout (3 columns)", select);

		select = new SelectWithManySelected("australian_state");
		select.setReadOnly(true);
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		select.setFrameless(true);
		layout.addField("many selections, frameless, COLUMN layout (3 columns) (read only)", select);

		// flat with selections
		select = new SelectWithManySelected("australian_state");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
		select.setFrameless(true);
		layout.addField("Many selections, frameless, FLAT layout", select);

		select = new SelectWithManySelected("australian_state");
		select.setReadOnly(true);
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
		select.setFrameless(true);
		layout.addField("Many selections, frameless, FLAT layout (read only)", select);
	}

	/**
	 * Examples of disabled state. You should use {@link WSubordinateControl} to set and manage the disabled state
	 * unless there is no facility for the user to enable a control.
	 */
	private void addDisabledExamples() {
		add(new WHeading(HeadingLevel.H2, "Disabled WCheckBoxSelect examples"));
		WFieldLayout layout = new WFieldLayout();
		add(layout);

		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setDisabled(true);
		layout.addField("Disabled with no default selection", select);

		add(new WHeading(HeadingLevel.H3, "Disabled with no selection and no frame"));
		select = new WCheckBoxSelect("australian_state");
		select.setDisabled(true);
		select.setFrameless(true);
		layout.addField("Disabled with no selection and no frame", select);
		select = new SelectWithSingleSelected("australian_state");
		select.setDisabled(true);
		layout.addField("Disabled with one selection", select);

		select = new SelectWithManySelected("australian_state");
		select.setDisabled(true);
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		layout.addField("Disabled with many selections and COLUMN layout", select);
	}

	/**
	 * Examples of what not to do when using WCheckBoxSelect.
	 */
	private void addAntiPatternExamples() {
		add(new WHeading(HeadingLevel.H2, "WCheckBoxSelect anti-pattern examples"));
		add(new WMessageBox(
				WMessageBox.WARN,
				"These examples are purposely bad and should not be used as samples of how to use WComponents but samples of how NOT to use them."));

		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect with submitOnChange"));
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setSubmitOnChange(true);
		layout.addField("Select a state or territory with auto save", select);

		select = new WCheckBoxSelect("australian_state");
		select.setSubmitOnChange(true);
		layout.addField("Select a state or territory with auto save and hint", select).getLabel().setHint("This is a hint");

		// Even compound controls need a label
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect with no labelling component"));
		add(new ExplanatoryText("All input controls, even those which are complex and do not output labellable HTML elements, must be associated with"
				+ " a WLabel or have a toolTip."));
		add(new WCheckBoxSelect("australian_state"));

		// Too many options anti-pattern
		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect with too many options"));
		add(new ExplanatoryText("Don't use a WCheckBoxSelect if you have more than a handful of options. A good rule of thumb is fewer than 10."));
		select = new WCheckBoxSelect(new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
			"q", "r", "s", "t", "u", "v", "w", "x", "y", "z"});
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(6);
		select.setFrameless(true);
		add(new WLabel("Select your country of birth", select));
		add(select);

		add(new WHeading(HeadingLevel.H3, "WCheckBoxSelect with no options."));
		add(new ExplanatoryText("An interactive WCheckBoxSelect with no options is rather pointless."));
		select = new WCheckBoxSelect();
		add(new WLabel("WCheckBoxSelect with no options", select));
		add(select);
	}

	/**
	 * Applications can wrap options inside lists in order to provide custom text and values for the CheckBoxes.
	 */
	private static final class CarOption implements Option, Serializable {

		/**
		 * The car bean for this option.
		 */
		private final Car car;

		/**
		 * The Car option constructor.
		 *
		 * @param id the id for the car
		 * @param make the make of the car
		 * @param model the model of the car
		 */
		private CarOption(final String id, final String make, final String model) {
			this.car = new Car(id, make, model);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getCode() {
			return car.getId();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDesc() {
			return car.getMake() + ", " + car.getModel();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "{ " + getDesc() + " }";
		}
	}

	/**
	 * simple javabean representing a car object.
	 *
	 * @author exisux
	 */
	private static final class Car implements Serializable {

		/**
		 * the id for the car.
		 */
		private final String id;
		/**
		 * the make of the car.
		 */
		private final String make;
		/**
		 * the model of the car.
		 */
		private final String model;

		/**
		 * standard constructor.
		 *
		 * @param id the id for the car
		 * @param make the make of the car
		 * @param model the model of the car
		 */
		private Car(final String id, final String make, final String model) {
			this.id = id;
			this.make = make;
			this.model = model;
		}

		/**
		 * get the id of the car.
		 *
		 * @return the car id.
		 */
		public String getId() {
			return id;
		}

		/**
		 * get the make of the car.
		 *
		 * @return the make of the car
		 */
		public String getMake() {
			return make;
		}

		/**
		 * get the model of the car.
		 *
		 * @return the model of the car.
		 */
		public String getModel() {
			return model;
		}

	}

	/**
	 * Simple override to select one item on first load.
	 */
	private class SelectWithSingleSelected extends WCheckBoxSelect {

		/**
		 * Create a WCheckBoxSelect with one option, from a table of options, selected on first load.
		 * @param table the lookup table to use.
		 */
		public SelectWithSingleSelected(final Object table) {
			super(table);
		}

		@Override
		protected void preparePaintComponent(final Request request) {
			if (!isInitialised()) {
				List<?> options = getOptions();
				if (options != null && !options.isEmpty()) {
					List<Option> selectedOptions = new ArrayList<>();
					selectedOptions.add((Option) options.get(0));
					setSelected(selectedOptions);
				}
				setInitialised(true);
			}
			super.preparePaintComponent(request);
		}
	}


	/**
	 * Simple override to select all items on first load.
	 */
	private class SelectWithManySelected extends WCheckBoxSelect {

		/**
		 * Create a WCheckBoxSelect with many options, from a table of options, selected on first load.
		 * @param table the lookup table to use.
		 */
		public SelectWithManySelected(final Object table) {
			super(table);
		}

		@Override
		protected void preparePaintComponent(final Request request) {
			if (!isInitialised()) {
				setSelected(getOptions());
				setInitialised(true);
			}
			super.preparePaintComponent(request);
		}


	}
}
