package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.util.List;

/**
 * Example of {@link WRadioButtonSelect} usage.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 *
 */
public final class WRadioButtonSelectExample extends WPanel {

	/**
	 * No selection text.
	 */
	private static final String NO_SELECTION = "No selection";

	/**
	 * Creates a WRadioButtonSelectExample.
	 */
	public WRadioButtonSelectExample() {
		add(new WHeading(HeadingLevel.H2, "WRadioButtonSelect examples"));
		add(new ExplanatoryText(
				"WRadioButtonSelect represents a 0-1 of n selection tool. It does not allow for selection to be made null once a selection is made."
				+ " As a common data input control it is prepresented in the user interface as if it was a simple labellable input."
				+ " This means that it must be associated with a labelling element, most commonly a WLabel."));

		makeSimpleExample();

		makeFramelessExample();

		addInsideAFieldLayoutExample();

		add(new WHeading(HeadingLevel.H2, "WRadioButtonSelect examples showing the layout properties"));
		addFlatSelectExample();
		addColumnSelectExample();
		addSingleColumnSelectExample();

		add(new WHeading(HeadingLevel.H2, "WRadioButtonSelect examples showing other properties"));
		addMandatorySelectExample();
		addDisabledExamples();
		addReadOnlyExamples();
		addAntiPatternExamples();
	}

	/**
	 * Make a simple editable example. The label for this example is used to get the example for use in the unit tests.
	 */
	private void makeSimpleExample() {
		add(new WHeading(HeadingLevel.H3, "Simple WRadioButtonSelect"));
		WPanel examplePanel = new WPanel();
		examplePanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, Size.MEDIUM));
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
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect without its frame"));
		add(new ExplanatoryText("When a WRadioButtonSelect is frameless it loses some of its coherence, especially when its WLabel is hidden or "
				+ "replaced by a toolTip. Using a frameless WRadioButtonSelect is useful within an existing WFieldLayout as it can provide a more "
				+ "consistent user interface but only if it has a relatively small number of options."));
		final WRadioButtonSelect select = new SelectWithSelection("australian_state");
		select.setFrameless(true);
		add(new WLabel("Frameless with default selection", select));
		add(select);
	}

	/**
	 * When a WRadioButtonSelect is added to a WFieldLayout the legend is moved. The first CheckBoxSelect has a frame,
	 * the second doesn't
	 *
	 */
	private void addInsideAFieldLayoutExample() {
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect inside a WFieldLayout"));
		add(new ExplanatoryText(
				"When a WRadioButtonSelect is inside a WField its label is exposed in a way which appears and behaves like a regular HTML label."
				+ " This allows WRadioButtonSelects to be used in a layout with simple form controls (such as WTextField) and produce a consistent"
				+ " and predicatable interface.\n"
				+ "The third example in this set uses a null label and a toolTip to hide the labelling element. This can lead to user confusion and"
				+ " is not recommended."));
		// Note: the wrapper WPanel here is to work around a bug in validation. See https://github.com/BorderTech/wcomponents/issues/1370
		final WPanel wrapper = new WPanel();
		add(wrapper);
		final WMessages messages = new WMessages();
		wrapper.add(messages);
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		wrapper.add(layout);
		WButton resetThisBit = new WButton("Reset this bit");
		resetThisBit.setCancel(true);
		resetThisBit.setAjaxTarget(wrapper);
		resetThisBit.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				wrapper.reset();
			}
		});
		layout.addField(resetThisBit);
		String[] options = new String[]{"Dog", "Cat", "Bird", "Turtle"};
		WRadioButtonSelect select = new WRadioButtonSelect(options);
		layout.addField("Select an animal", select);
		String[] options2 = new String[]{"Parrot", "Galah", "Cockatoo", "Lyre"};
		select = new WRadioButtonSelect(options2);
		select.setMandatory(true);
		layout.addField("You must select a bird", select);
		select.setFrameless(true);

		//a tooltip can be used as a label stand-in even in a WField
		String[] options3 = new String[]{"Carrot", "Beet", "Brocolli", "Bacon - the perfect vegetable"};
		select = new WRadioButtonSelect(options3);
		//if you absolutely do not want a WLabel in a WField then it has to be added using null cast to a WLabel.
		layout.addField((WLabel) null, select);
		select.setToolTip("Veggies");
		WButton btnValidate = new WButton("validate");
		btnValidate.setAction(new ValidatingAction(messages.getValidationErrors(), layout) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				// do nothing
			}
		});
		layout.addField(btnValidate);
		wrapper.add(new WAjaxControl(btnValidate, wrapper));
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
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect with flat layout"));
		add(new ExplanatoryText("Setting the layout to FLAT will make the radio buttons be rendered in a horizontal line. They will wrap when they"
				+ " reach the edge of the parent container."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		add(new WLabel("Flat selection", select));
		add(select);
	}

	/**
	 * adds a WRadioButtonSelect with LAYOUT_COLUMN in 3 columns.
	 */
	private void addColumnSelectExample() {
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect laid out in three columns"));
		add(new ExplanatoryText("Setting the layout to COLUMN will make the radio buttons be rendered in 'n' columns. The number of columns is"
				+ " determined by the layoutColumnCount property."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		select.setButtonColumns(3);
		add(new WLabel("Three column selection", select));
		add(select);

		add(new WHeading(HeadingLevel.H3, "Options equal to columns"));
		String[] options = new String[]{"Dog", "Cat", "Bird"};
		final WRadioButtonSelect select2 = new WRadioButtonSelect(options);
		select2.setButtonColumns(3);
		final WTextField text = new WTextField();
		text.setReadOnly(true);
		text.setText(NO_SELECTION);

		WButton update = new WButton("Select Animals");
		update.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String output = select2.getSelected() == null ? NO_SELECTION : "The selected animal is: "
						+ select2.getSelected();
				text.setText(output);
			}
		});
		select2.setDefaultSubmitButton(update);

		add(new WLabel("Three columns and three options", select2));
		add(select2);
		add(update);
		add(text);
		add(new WAjaxControl(update, text));
	}

	/**
	 * adds a WRadioButtonSelect with LAYOUT_COLUMN in 1 column simply by not setting the number of columns. This is
	 * superfluous as you should use LAYOUT_STACKED (the default) instead.
	 */
	private void addSingleColumnSelectExample() {
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect laid out in a single column"));
		add(new ExplanatoryText("When layout is COLUMN, setting the layoutColumnCount property to one, or forgetting to set it at all (default is "
				+ "one) is a little bit pointless."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		add(new WLabel("One column", select));
		add(select);
	}

	/**
	 * adds a WRadioButtonSelect with setMandatory(true).
	 */
	private void addMandatorySelectExample() {
		add(new WHeading(HeadingLevel.H3, "Mandatory WRadioButtonSelect"));

		add(new ExplanatoryText("When a WRadioButtonSelect is mandatory it needs a visible labelling element, otherwise many users may not know that "
				+ "the component requires an answer."));
		final WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		select.setMandatory(true);
		add(new WLabel("Mandatory selection", select));
		add(select);

		add(new WHeading(HeadingLevel.H3, "Mandatory WRadioButtonSelect in a WFieldLayout"));
		WRadioButtonSelect select2 = new WRadioButtonSelect("australian_state");
		select2.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		select2.setMandatory(true);
		final WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		layout.addField("Required selection", select2).getLabel().setHint("Required");
		select2 = new WRadioButtonSelect("australian_state");
		select2.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		select2.setMandatory(true);
		select2.setToolTip("Select a state");
		layout.addField((WLabel) null, select2);
	}

	/**
	 * Examples of readonly states. When in a read only state only the selected option is output. Since a
	 * WRadioButtonSeelct can only have 0 or 1 selected option the LAYOUT and FRAME are ignored.
	 */
	private void addReadOnlyExamples() {
		add(new WHeading(HeadingLevel.H3, "Read-only WRadioButtonSelect examples"));
		add(new ExplanatoryText("These examples all use the same list of options: the states and territories list from the editable examples above. "
				+ "When the readOnly state is specified only that option which is selected is output.\n"
				+ "Since no more than one option is able to be selected the layout and frame settings are ignored in the read only state."));

		WFieldLayout layout = new WFieldLayout();
		add(layout);

		WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setReadOnly(true);
		layout.addField("Read only with no selection", select);

		select = new SelectWithSelection("australian_state");
		select.setReadOnly(true);
		layout.addField("Read only with selection", select);
	}

	/**
	 * Examples of disabled state. You should use {@link WSubordinateControl} to set and manage the disabled state
	 * unless there is no facility for the user to enable a control.
	 *
	 * If you want to prevent the user enabling and interacting with a WRadioButtonSeelct then you should consider using
	 * the readOnly state instead of the disabled state.
	 */
	private void addDisabledExamples() {
		add(new WHeading(HeadingLevel.H2, "Disabled WRadioButtonSelect examples"));
		WFieldLayout layout = new WFieldLayout();
		add(layout);
		WRadioButtonSelect select = new WRadioButtonSelect("australian_state");
		select.setDisabled(true);
		layout.addField("Disabled with no selection", select);

		select = new WRadioButtonSelect("australian_state");
		select.setDisabled(true);
		select.setFrameless(true);
		layout.addField("Disabled with no selection no frame", select);

		select = new SelectWithSelection("australian_state");
		select.setDisabled(true);
		layout.addField("Disabled with selection", select);
	}

	/**
	 * Examples of what not to do when using WRadioButtonSelect.
	 */
	private void addAntiPatternExamples() {
		add(new WHeading(HeadingLevel.H2, "WRadioButtonSelect anti-pattern examples"));
		add(new WMessageBox(WMessageBox.WARN,
				"These examples are purposely bad and should not be used as samples of how to use WComponents but samples of how NOT to use them."));

		//Even compound controls need a label
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect with no labelling component"));
		add(new ExplanatoryText("All input controls, even those which are complex and do not output labellable HTML elements, must be associated with"
				+ " a WLabel or have a toolTip."));
		add(new WRadioButtonSelect("australian_state"));

		//submitOnChange is a WRadioButtonSelect no no!!
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect with submitOnChange"));
		add(new ExplanatoryText("SubmitOnChange is bad in most cases but terrible with radio buttons because there is no way to change the selection"
				+ " between non-contiguous options using the keyboard without having multiple page submits.\nIn the following example try to change "
				+ "the selection from 'Outside Australia' to 'Queensland' using only your keyboard. To make this easier the WRadioButtonSelect has"
				+ " an access key of 'M'"));
		final WRadioButtonSelect select = new SelectWithSelection("australian_state");
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
		add(layout);
		WField selectField = layout.addField("Make a selection to update the page", select);
		selectField.getLabel().setAccessKey('M');
		layout.addField("Selected option", selected);

		//Too many options anti-pattern
		add(new WHeading(HeadingLevel.H3, "WRadioButtonSelect with too many options"));
		add(new ExplanatoryText(
				"Don't use a WRadioButtonSelect if you have more than a handful of options. A good rule of thumb is fewer than 10."));
		//use the country code list at your peril!!
		WRadioButtonSelect rbsTooBig = new WRadioButtonSelect(new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
			"p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"});
		rbsTooBig.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		rbsTooBig.setButtonColumns(6);
		rbsTooBig.setFrameless(true);
		rbsTooBig.setToolTip("Select your country of birth");
		add(rbsTooBig);

		//Don't use a radioButtonSelect if the user can make no selection unless you provide a null option
		add(new WHeading(HeadingLevel.H3, "Optional WRadioButtonSelect with no null option"));
		add(new ExplanatoryText("Once a radio button group has a selection it cannot be removed. If a WRadioButtonSelect is not mandatory it should"
				+ " include a 'none of these' type null option.\nWhat happens if you make a selection in the following but then change your mind"
				+ " (even ugly chairs are not your scene). To concentrate the mind I have made a selection for you."));
		WRadioButtonSelect noneOfTheAboveSelect = new SelectWithSelection(
				new String[]{"spike", "broken glass", "ugly chair", "wet paint"});
		noneOfTheAboveSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		noneOfTheAboveSelect.setFrameless(true);
		layout = new WFieldLayout();
		add(layout);
		layout.addField("Where would you like to sit?", noneOfTheAboveSelect);

		//don't use a yes/no group of radio buttons for something which should be a checkbox
		add(new WHeading(HeadingLevel.H3, "Yes/No options"));
		add(new ExplanatoryText(
				"If the only answers to your question is one of yes or no then you do not have a group of radio buttons, you have a check box.\n"
				+ "In the following example the WRadioButtonSelect should be a WCheckBox and the label be 'I agree to the terms and conditions'"));

		layout = new WFieldLayout();
		add(layout);
		WRadioButtonSelect yesNoSelect = new WRadioButtonSelect(new String[]{"yes", "no"});
		yesNoSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		yesNoSelect.setFrameless(true);
		layout.addField("Do you agree to the terms and conditions?", yesNoSelect);

		add(new WHeading(HeadingLevel.H3, "No options"));
		add(new ExplanatoryText("An interactive WRadioButtonSelect with no options is rather pointless."));
		layout = new WFieldLayout();
		add(layout);
		layout.addField("Select from no options", new WRadioButtonSelect());
	}

	/**
	 * Simple override to select one item on first load.
	 */
	private class SelectWithSelection extends WRadioButtonSelect {

		/**
		 * Create a WRadioButtonSelect with one option selected from a table of options.
		 * @param table the lookup table to use
		 */
		SelectWithSelection(final Object table) {
			super(table);
		}

		/**
		 * Create a WRadioButtonSelect with one option selected from an array of options.
		 * @param options the options to use
		 */
		SelectWithSelection(final Object[] options) {
			super(options);
		}

		@Override
		protected void preparePaintComponent(final Request request) {
			if (!isInitialised()) {
				List<?> options = getOptions();
				if (options != null && !options.isEmpty()) {
					setSelected(options.get(0));
				}
				setInitialised(true);
			}
			super.preparePaintComponent(request);
		}
	}
}
