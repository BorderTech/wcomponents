package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Option;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.util.List;

/**
 * Example of {@link WRadioButtonSelect} usage.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 *
 */
public class WRadioButtonSelectExample extends WPanel {

	/**
	 * No selection text.
	 */
	private static final String NO_SELECTION = "No selection";

	/**
	 * Creates a WRadioButtonSelectExample.
	 */
	public WRadioButtonSelectExample() {

		add(new WHeading(WHeading.MAJOR, "WRadioButtonSelect examples"));
		add(new ExplanatoryText(
				"WRadioButtonSelect represents a 0-1 of n selection tool. It does not allow for selection to be made null once a selection is made."
				+ " As a common data input control it is prepresented in the user interface as if it was a simple labellable input."
				+ " This means that it must be associated with a labelling element, most commonly a WLabel."));

		makeSimpleExample();

		makeFramelessExample();

		addInsideAFieldLayoutExample();

		add(new WHeading(WHeading.MAJOR, "WRadioButtonSelect examples showing the layout properties"));
		addFlatSelectExample();
		addColumnSelectExample();
		addSingleColumnSelectExample();

		add(new WHeading(WHeading.MAJOR, "WRadioButtonSelect examples showing other properties"));
		addFMandatorySelectExample();
		addDisabledExamples();
		addReadOnlyExamples();

		add(new WHorizontalRule());
		addRadioButtonSelectWithLabelExample();

		add(new WHorizontalRule());
		addAntiPatternExamples();
	}

	/**
	 * Make a simple editable example.
	 */
	private void makeSimpleExample() {
		add(new WHeading(WHeading.SECTION, "Simple WRadioButtonSelect"));
		WPanel examplePanel = new WPanel();
		examplePanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, 0, 6));
		add(examplePanel);

		/**
		 * The radio button select.
		 */
		final WRadioButtonSelect rbSelect = new WRadioButtonSelect("australian_state");
		final WTextField text = new WTextField();

		text.setReadOnly(true);
		text.setText(NO_SELECTION);

		WButton update = new WButton("Update");
		update.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				text.setText("The selected item is: "
						+ rbSelect.getSelected());
			}
		});
		//setting the default submit button improves usability. It can be set on a WPanel or the WRadioButtonSelect directly
		examplePanel.setDefaultSubmitButton(update);

		examplePanel.add(new WLabel("Select a state or territory", rbSelect));
		examplePanel.add(rbSelect);
		examplePanel.add(text);
		examplePanel.add(update);
		add(new WAjaxControl(update, text));
	}

	/**
	 * Make a simple editable example without a frame.
	 */
	private void makeFramelessExample() {
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect without its frame"));
		add(new ExplanatoryText(
				"When a WRadioButtonSelect is frameless it loses some of its coherence, especially when its WLabel is hidden or replaced by a toolTip."
				+ " Using a frameless WRadioButtonSelect is useful within an existing WFieldLayout as it can provide a more consistent user interface but only if it has a relatively small number of options."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setToolTip("Select a State or territory");
		select.setFrameless(true);
		add(select);
	}

	/**
	 * When a WRadioButtonSelect is added to a WFieldLayout the legend is moved. The first CheckBoxSelect has a frame,
	 * the second doesn't
	 *
	 */
	private void addInsideAFieldLayoutExample() {
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect inside a WFieldLayout"));
		add(new ExplanatoryText(
				"When a WRadioButtonSelect is inside a WField its label is exposed in a way which appears and behaves like a regular HTML label."
				+ " This allows WRadioButtonSelects to be used in a layout with simple form controls (such as WTextField) and produce a consistent and predicatable interface.\n"
				+ "The third example in this set uses a null label and a toolTip to hide the labelling element. This can lead to user confusion and is not recommended."));
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		String[] options = new String[]{"Dog", "Cat", "Bird", "Turtle"};
		final WRadioButtonSelect select = new WRadioButtonSelect(options);
		layout.addField("Select some animals", select);
		String[] options2 = new String[]{"Parrot", "Galah", "Cockatoo", "Lyre"};
		final WRadioButtonSelect select2 = new WRadioButtonSelect(options2);
		layout.addField("Select some birds", select2);
		select2.setFrameless(true);

		//a tooltip can be used as a label stand-in even in a WField
		String[] options3 = new String[]{"Carrot", "Beet", "Brocolli", "Bacon - the perfect vegetable"};
		final WRadioButtonSelect select3 = new WRadioButtonSelect(options3);
		//if you absolutely do not want a WLabel in a WField then it has to be added using null cast to a WLabel.
		layout.addField((WLabel) null, select3);
		select3.setToolTip("Veggies");
	}

	/*
	 * WRadioButtonSelect layout options
	 * These examples show the various ways to lay out the options in a WRadioButtonSelect
	 * NOTE: the default (if no buttonLayout is set) is LAYOUT_STACKED
	 */
	/**
	 * adds a WRadioButtonSelect with LAYOUT_FLAT.
	 */
	private void addFlatSelectExample() {
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect with flat layout"));
		add(new ExplanatoryText(
				"Setting the layout to FLAT will make the radio buttons be rendered in a horizontal line. They will wrap when they reach the edge of the parent container."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		add(select);
	}

	/**
	 * adds a WRadioButtonSelect with LAYOUT_COLUMN in 3 columns.
	 */
	private void addColumnSelectExample() {
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect laid out in three columns"));
		add(new ExplanatoryText(
				"Setting the layout to COLUMN will make the radio buttons be rendered in 'n' columns. The number of columns is determined by the layoutColumnCount property."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		add(select);
	}

	/**
	 * adds a WRadioButtonSelect with LAYOUT_COLUMN in 1 column simply by not setting the number of columns. This is
	 * superfluous as you should use LAYOUT_STACKED (the default) instead.
	 */
	private void addSingleColumnSelectExample() {
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect laid out in a single column"));
		add(new ExplanatoryText(
				"When layout is COLUMN, setting the layoutColumnCount property to one, or forgetting to set it at all (default is one) is a little bit pointless."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		add(select);
	}

	/**
	 * adds a WRadioButtonSelect with setMandatory(true).
	 */
	private void addFMandatorySelectExample() {
		add(new WHeading(WHeading.SECTION, "Mandatory WRadioButtonSelect"));

		add(new ExplanatoryText(
				"When a WRadioButtonSelect is mandatory it needs a visible labelling element, otherwise many users may not know that the component requires an answer."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setToolTip("Make a selection");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		select.setMandatory(true);
		add(select);

		add(new WHeading(WHeading.SECTION, "Mandatory WRadioButtonSelect in a WFieldLayout"));
		WRadioButtonSelect select2 = new WRadioButtonSelect("australian_state");
		select2.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		select2.setMandatory(true);
		final WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		layout.addField("Make a selection", select2).getLabel().setHint("Required");
		select2 = new WRadioButtonSelect("australian_state");
		select2.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		select2.setMandatory(true);
		select2.setToolTip("Select a state");
		layout.addField((WLabel) null, select2);
	}

	/**
	 * Add radio button select with a label.
	 */
	private void addRadioButtonSelectWithLabelExample() {
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect with a WLabel"));
		add(new ExplanatoryText(
				"When a WLabel is associated with a WRadioButtonSelect (not read only) the label is output in-situ and as part of the WRadioButtonSelect.\n"
				+ "It does not matter where in the UI the label is placed: the WRadioButtonSelect will hunt it out. The label becomes the legend of the control's fieldset.\n"
				+ "You must be aware though that unless the label is part of a WField it will be present in the legend AND wherever it is placed. You can alleviate this by using setHidden(true) "
				+ "on the WLabel."));

		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		add(select);
		//normally you should add a WLabel BEFORE its control (except single WCheckBox or WRadioButton components)
		//so I am going to add it after.
		WLabel label = new WLabel("Make a selection (label not in the expected place)", select);
		label.setHidden(true);
		add(label);
	}

	/**
	 * Examples of readonly states. When in a read only state only the selected option is output. Since a
	 * WRadioButtonSeelct can only have 0 or 1 selected option the LAYOUT and FRAME are ignored.
	 */
	private void addReadOnlyExamples() {
		add(new WHeading(WHeading.SECTION, "Read-only WRadioButtonSelect examples"));
		add(new ExplanatoryText(
				"These examples all use the same list of options: the states and territories list from the editable examples above. When the readOnly state is specified only that option which is selected is output.\n"
				+ "Since no more than one option is able to be selected the layout and frame settings are ignored in the read only state."));

		//NOTE: when there are 0 or 1 selections the frame is not rendered.
		add(new WHeading(WHeading.MINOR, "Read only with no selection"));
		WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with no selection");
		add(new WText(
				"End of unselected read only example (note that the component has presence in the UI but does not have any content)."));

		add(new WHeading(WHeading.MINOR, "Read only with one selection"));
		select = new WRadioButtonSelect("australian_state");
		add(select);
		select.setReadOnly(true);
		select.setToolTip("Read only with one selection");
		List<?> options = select.getOptions();
		if (!options.isEmpty()) {
			select.setSelected((Option) options.get(0));
		}

		//read only in a WFieldLayout
		add(new WHeading(WHeading.MINOR, "Read only in a WFieldLayout"));
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		//no selections
		select = new WRadioButtonSelect("australian_state");
		select.setReadOnly(true);
		layout.addField("No selection was made", select);
		//one selection
		select = new WRadioButtonSelect("australian_state");
		select.setReadOnly(true);

		options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected((Option) options.get(0));
		}
		layout.addField("This selection was made", select);
	}

	/**
	 * Examples of disabled state. You should use {@link WSubordinateControl} to set and manage the disabled state
	 * unless there is no facility for the user to enable a control.
	 *
	 * If you want to prevent the user enabling and interacting with a WRadioButtonSeelct then you should consider using
	 * the readOnly state instead of the disabled state.
	 */
	private void addDisabledExamples() {
		add(new WHeading(WHeading.MAJOR, "Disabled WRadioButtonSelect examples"));
		add(new WHeading(WHeading.SECTION, "Disabled with no selection"));
		WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setToolTip("Make a selection");
		add(select);
		select.setDisabled(true);

		add(new WHeading(WHeading.SECTION, "Disabled with no selection and no frame"));
		select = new WRadioButtonSelect("australian_state");
		add(select);
		select.setDisabled(true);
		select.setFrameless(true);
		select.setToolTip("Make a selection (no frame)");

		add(new WHeading(WHeading.SECTION, "Disabled with one selection"));
		select = new WRadioButtonSelect("australian_state");
		select.setToolTip("Make a selection");
		add(select);
		select.setDisabled(true);
		List<?> options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected((Option) options.get(0));
		}
	}

	/**
	 * Examples of what not to do when using WRadioButtonSelect.
	 */
	private void addAntiPatternExamples() {
		add(new WHeading(WHeading.MAJOR, "WRadioButtonSelect anti-pattern examples"));
		add(new WMessageBox(WMessageBox.WARN,
				"These examples are purposely bad and should not be used as samples of how to use WComponents but samples of how NOT to use them."));

		//Even compound controls need a label
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect with no labelling component"));
		add(new ExplanatoryText(
				"All input controls, even those which are complex and do not output labellable HTML elements, must be associated with a WLabel or have a toolTip."));
		add(new WRadioButtonSelect("australian_state"));

		//submitOnChange is a WRadioButtonSelect no no!!
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect with submitOnChange"));
		add(new ExplanatoryText(
				"SubmitOnChange is bad in most cases but terrible with radio buttons because there is no way to change the selection between non-contiguous options using the keyboard without having multiple page submits.\n"
				+ "In the following example try to change the selection from 'Outside Australia' to 'Queensland' using only your keyboard. To make this easier the WRadioButtonSelect has an access key of 'M'"));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		List<?> options = select.getOptions();

		if (!options.isEmpty()) {
			select.setSelected((Option) options.get(0));
		}

		final WTextField selected = new WTextField();
		selected.setReadOnly(true);

		select.setActionOnChange(new Action() {

			@Override
			public void execute(final ActionEvent event) {
				//does not matter what this is
				selected.setText(select.getValueAsString());
			}
		});
		select.setSubmitOnChange(true);
		//now put them all into the UI
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		WField selectField = layout.addField("Make a selection to update the page", select);
		selectField.getLabel().setAccessKey('M');
		layout.addField("Selected option", selected);

		add(new WHorizontalRule());

		//Too many options anti-pattern
		add(new WHeading(WHeading.SECTION, "WRadioButtonSelect with too many options"));
		add(new ExplanatoryText(
				"Don't use a WRadioButtonSelect if you have more than a handful of options. A good rule of thumb is fewer than 10."));
		//use the country code list at your peril!!
		WRadioButtonSelect hugeSelect = new WRadioButtonSelect("icao");
		hugeSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		hugeSelect.setButtonColumns(5);
		hugeSelect.setFrameless(true);
		hugeSelect.setToolTip("Select your country of birth");
		add(hugeSelect);

		add(new WHorizontalRule());

		//Don't use a radioButtonSelect if the user can make no selection unless you provide a null option
		add(new WHeading(WHeading.SECTION, "Optional WRadioButtonSelect with no null option"));
		add(new ExplanatoryText(
				"Once a radio button group has a selection it cannot be removed. If a WRadioButtonSelect is not mandatory it should include a 'none of these' type null option.\n"
				+ "What happens if you make a selection in the following but then change your mind (even ugly chairs are not your scene). To concentrate the mind I have made a selection for you."));
		WRadioButtonSelect noneOfTheAboveSelect = new WRadioButtonSelect(
				new String[]{"spike", "broken glass", "ugly chair", "wet paint"});
		noneOfTheAboveSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		options = noneOfTheAboveSelect.getOptions();
		noneOfTheAboveSelect.setFrameless(true);

		if (!options.isEmpty()) {
			noneOfTheAboveSelect.setSelected((String) options.get(0));
		}

		layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		layout.addField("Where would you like to sit?", noneOfTheAboveSelect);

		add(new WHorizontalRule());
		//don't use a yes/no group of radio buttons for something which should be a checkbox
		add(new WHeading(WHeading.SECTION, "Yes/No options"));
		add(new ExplanatoryText(
				"If the only answers to your question is one of yes or no then you do not have a group of radio buttons, you have a check box.\n"
				+ "In the following example the WRadioButtonSelect should be a WCheckBox and the label be 'I agree to the terms and conditions'"));

		layout = new WFieldLayout();
		add(layout);
		WRadioButtonSelect yesNoSelect = new WRadioButtonSelect(new String[]{"yes", "no"});
		yesNoSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		yesNoSelect.setFrameless(true);
		layout.addField("Do you agree to the terms and conditions?", yesNoSelect);

	}
}
