package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Option;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
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
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 * @since 1.0.0
 * @since 1.0.0 using defaul buttons and ajax controls.
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
		add(new WHeading(WHeading.MAJOR, "Simple WCheckBoxSelect examples"));
		addAustralianStatesExample();

		add(new WHorizontalRule());
		addCarsExample();

		add(new WHorizontalRule());
		addAnimalExample();

		add(new WHorizontalRule());
		addCheckBoxSelectWithLabelExample();

		add(new WHorizontalRule());
		addInsideAFieldLayoutExample();

		add(new WHeading(WHeading.MAJOR, "Examples showing LAYOUT properties"));
		addFlatSelectExample();
		addColumnSelectExample();
		addSingleColumnSelectExample();

		add(new WHeading(WHeading.MAJOR, "Examples showing other properties"));
		addFrameExamples();
		add(new WHorizontalRule());

		// disabled state
		// NOTE: you would normally use WSubordinateControl to set a disabled state
		addDisabledExamples();
		add(new WHorizontalRule());

		// read only state
		addReadOnlyExamples();

		add(new WHorizontalRule());
		addAntiPatternExamples();
	}

	/**
	 * This example creates the WCheckBoxSelect using an a look up table. This checkBoxSelect has a frame.
	 */
	private void addAustralianStatesExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect created using a lookup table"));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setToolTip("Make a selection");
		// select.setFrameless(true);
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

		add(select);
		add(update);
		add(text);
		add(new WAjaxControl(update, text));
	}

	/**
	 * This example creates the WCheckBoxSelect from an array of Strings. This checkBoxSelect has no frame and uses a
	 * toolTip to set its legend
	 */
	private void addAnimalExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect created using a String array"));
		String[] options = new String[]{"Dog", "Cat", "Bird", "Turtle"};
		final WCheckBoxSelect select = new WCheckBoxSelect(options);
		select.setToolTip("Animals");
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

		add(select);
		add(update);
		add(text);
		add(new WAjaxControl(update, text));
	}

	/**
	 * This example creates the WCheckBoxSelect from a List of CarOptions. This WCheckBoxSelect has no frame and uses a
	 * toolTip to set its labelling element
	 */
	private void addCarsExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect created using an array list of options"));
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
	private void addInsideAFieldLayoutExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect inside a WFieldLayout"));
		add(new ExplanatoryText(
				"When a WCheckBoxSelect is inside a WField its label is exposed in a way which appears and behaves like a regular HTML label."
				+ "This allows WCheckBoxSelects to be used in a layout with simple form controls (such as WTextField) and produce a consistent and predicatable interface."
				+ "The third example in this set uses a null label and a toolTip to hide the labelling element. This can lead to user confusion and is not recommended."));
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		String[] options = new String[]{"Dog", "Cat", "Bird", "Turtle"};
		final WCheckBoxSelect select = new WCheckBoxSelect(options);
		layout.addField("Select some animals", select);
		String[] options2 = new String[]{"Parrot", "Galah", "Cockatoo", "Lyre"};
		final WCheckBoxSelect select2 = new WCheckBoxSelect(options2);
		layout.addField("Select some birds", select2);
		select2.setFrameless(true);

		// a tooltip can be used as a label stand-in even in a WField
		String[] options3 = new String[]{"Carrot", "Beet", "Brocolli", "Bacon - the perfect vegetable"};
		final WCheckBoxSelect select3 = new WCheckBoxSelect(options3);
		layout.addField((WLabel) null, select3);
		select3.setToolTip("Veggies");
	}

	/**
	 * WCheckBoxSelect layout options These examples show the various ways to lay out the options in a WCheckBoxSelect
	 * NOTE: the default (if no buttonLayout is set) is LAYOUT_STACKED. adds a WCheckBoxSelect with LAYOUT_FLAT
	 */
	private void addFlatSelectExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect with flat layout"));
		add(new ExplanatoryText(
				"Setting the layout to FLAT will make thecheck boxes be rendered in a horizontal line. They will wrap when they reach the edge of the parent container."));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
		add(select);
	}

	/**
	 * adds a WCheckBoxSelect with LAYOUT_COLUMN in 2 columns.
	 */
	private void addColumnSelectExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect laid out in two columns"));
		add(new ExplanatoryText(
				"Setting the layout to COLUMN will make the check boxes be rendered in 'n' columns. The number of columns is determined by the layoutColumnCount property."));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(2);
		add(select);

		final WCheckBoxSelect select2 = new WCheckBoxSelect(new String[]{"Dog", "Cat", "Bird", "Turtle"});
		select2.setButtonColumns(4);
		select2.setToolTip("Animals");
		add(select2);

		final WCheckBoxSelect select3 = new WCheckBoxSelect(new String[]{"Dog", "Cat", "Bird", "Turtle"});
		select3.setButtonColumns(4);
		select3.setToolTip("Animals");
		select3.setSelected(select3.getOptions());
		select3.setReadOnly(true);
		add(select3);
	}

	/**
	 * adds a WCheckBoxSelect with LAYOUT_COLUMN in 1 column simply by not setting the number of columns. This is
	 * superfluous as you should use LAYOUT_STACKED (the default) instead.
	 */
	private void addSingleColumnSelectExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect laid out in a single column"));
		add(new ExplanatoryText(
				"When layout is COLUMN, setting the layoutColumnCount property to one, or forgetting to set it at all (default is one) is a little bit pointless."));
		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		add(select);
	}

	/**
	 * Add check box with label.example.
	 */
	private void addCheckBoxSelectWithLabelExample() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect with a WLabel"));
		add(new ExplanatoryText(
				"When a WLabel is associated with a WCheckBoxSelect (not read only) the label is output in-situ and as part of the WCheckBoxSelect.\n"
				+ "It does not matter where in the UI the label is placed: the WCheckBoxSelect will hunt it out. The label becomes the legend of the control's fieldset.\n"
				+ "You must be aware though that unless the label is part of a WField it will be present in the legend AND wherever it is placed. You can alleviate this by using setHidden(true) "
				+ "on the WLabel."));

		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);

		WLabel label = new WLabel("Make a selection", select);
		add(label);
		add(select);
	}

	/**
	 * Examples of readonly states.
	 */
	private void addReadOnlyExamples() {
		add(new WHeading(WHeading.SECTION, "Read-only WCheckBoxSelect examples"));
		add(new ExplanatoryText(
				"These examples all use the same list of options: the states and territories list from the editable examples above. When the readOnly state is specified only those options which are selected are output."));

		// NOTE: when there are 0 or 1 selections the frame is not rendered.
		add(new WHeading(WHeading.MINOR, "Read only with no selection"));
		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with no selection");
		add(new WText("end of unselected read only example"));

		add(new WHeading(WHeading.MINOR, "Read only with one selection"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with one selection");
		List<?> options = select.getOptions();
		List<Option> selectedOptions;
		if (!options.isEmpty()) {
			selectedOptions = new ArrayList<>();
			selectedOptions.add((Option) options.get(0));
			select.setSelected(selectedOptions);
		}

		add(new WHeading(WHeading.MINOR, "Read only with many selections and no frame"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with many selections");
		select.setFrameless(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}
		add(new WHeading(WHeading.MINOR, "Read only with many selections and COLUMN layout"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with many selections");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}

		// read only in a WFieldLayout
		add(new WHeading(WHeading.MINOR, "Read only in a WFieldLayout"));
		add(new ExplanatoryText(
				"Each read only example is preceded by an editable example with the same options and selection. This is to ensure the CSS works properly."));
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
		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		options = select.getOptions();
		if (!options.isEmpty()) {
			selectedOptions = new ArrayList<>();
			selectedOptions.add((Option) options.get(0));
			select.setSelected(selectedOptions);
		}
		layout.addField("One selection was made", select);

		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		select.setReadOnly(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			selectedOptions = new ArrayList<>();
			selectedOptions.add((Option) options.get(0));
			select.setSelected(selectedOptions);
		}
		layout.addField("One selection was made (read only)", select);

		// many selections
		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}
		layout.addField("Many selections with frame", select);

		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		select.setReadOnly(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}
		layout.addField("Many selections with frame (read only)", select);

		// columns with selections
		select = new WCheckBoxSelect("australian_state");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		select.setFrameless(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}
		layout.addField("many selections, frameless, COLUMN layout (3 columns)", select);
		select = new WCheckBoxSelect("australian_state");
		select.setReadOnly(true);
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		select.setFrameless(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}
		layout.addField("many selections, frameless, COLUMN layout (3 columns) (read only)", select);

		// flat with selections
		select = new WCheckBoxSelect("australian_state");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
		select.setFrameless(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}
		layout.addField("Many selections, frameless, FLAT layout", select);
		select = new WCheckBoxSelect("australian_state");
		select.setReadOnly(true);
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
		select.setFrameless(true);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}
		layout.addField("Many selections, frameless, FLAT layout (read only)", select);
	}

	/**
	 * Examples of disabled state. You should use {@link WSubordinateControl} to set and manage the disabled state
	 * unless there is no facility for the user to enable a control.
	 */
	private void addDisabledExamples() {
		add(new WHeading(WHeading.SECTION, "Disabled WCheckBoxSelect examples"));
		add(new WHeading(WHeading.MINOR, "Disabled with no selection"));
		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setDisabled(true);
		select.setToolTip("Make a selection");
		add(new WHeading(WHeading.MINOR, "Disabled with no selection and no frame"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setDisabled(true);
		select.setFrameless(true);
		select.setToolTip("Make a selection (no frame)");

		add(new WHeading(WHeading.MINOR, "Disabled with one selection"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setDisabled(true);
		select.setToolTip("Make a selection");
		List<?> options = select.getOptions();

		if (!options.isEmpty()) {
			List<Option> selectedOptions = new ArrayList<>();
			selectedOptions.add((Option) options.get(0));
			select.setSelected(selectedOptions);
		}

		add(new WHeading(WHeading.MINOR, "Disabled with many selections and COLUMN layout"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setDisabled(true);
		select.setToolTip("Make a selection");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected(options);
		}

	}

	/**
	 * Examples of setFrameless.
	 */
	private void addFrameExamples() {
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect showing the frameless state"));
		add(new WHeading(WHeading.MINOR, "Normal (with frame)"));
		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setToolTip("Make a selection");
		add(new WHeading(WHeading.MINOR, "Without frame"));
		select = new WCheckBoxSelect("australian_state");
		add(select);
		select.setFrameless(true);
		select.setToolTip("Make a selection (no frame)");
	}

	/**
	 * Examples of what not to do when using WCheckBoxSelect.
	 */
	private void addAntiPatternExamples() {
		add(new WHeading(WHeading.MAJOR, "WCheckBoxSelect anti-pattern examples"));
		add(new WMessageBox(
				WMessageBox.WARN,
				"These examples are purposely bad and should not be used as samples of how to use WComponents but samples of how NOT to use them."));

		// Even compound controls need a label
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect with no labelling component"));
		add(new ExplanatoryText(
				"All input controls, even those which are complex and do not output labellable HTML elements, must be associated with a WLabel or have a toolTip."));
		add(new WCheckBoxSelect("australian_state"));

		add(new WHorizontalRule());

		// Too many options anti-pattern
		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect with too many options"));
		add(new ExplanatoryText(
				"Don't use a WCheckBoxSelect if you have more than a handful of options. A good rule of thumb is fewer than 10."));
		// use the country code list at your peril!!
		WCheckBoxSelect hugeSelect = new WCheckBoxSelect("icao");
		hugeSelect.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		hugeSelect.setButtonColumns(5);
		hugeSelect.setFrameless(true);
		hugeSelect.setToolTip("Select your country of birth");
		add(hugeSelect);

		add(new WHorizontalRule());

		add(new WHeading(WHeading.SECTION, "WCheckBoxSelect with a WLabel in an unexpected place."));
		add(new ExplanatoryText("The WLabel must precede the WCheckBoxSelect."));

		final WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);

		WLabel label = new WLabel("Make a selection (label not in the expected place)", select);
		add(select);
		add(label);

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
}
