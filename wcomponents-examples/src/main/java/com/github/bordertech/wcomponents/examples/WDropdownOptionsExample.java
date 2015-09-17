package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WDropdown.DropdownType;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WStyledText.WhitespaceMode;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.ShowInGroup;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * This example provides the configuration options for the dropdown box, including the various different action types.
 * </p>
 * <p>
 * Please note that for the subordinate controls its not actually the text being copied from the drop down to the panel
 * rather a series of panels with the appropriate text being shown and hidden. This can lead with both the Action on
 * Change populating the text of the drop down and the subordinate showing the panel representing the dropdown value at
 * the same time.
 * </p>
 *
 * @author Steve Harney
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WDropdownOptionsExample extends WContainer {

	/**
	 * None option.
	 */
	private static final String NONE = "none";
	/**
	 * No white space option.
	 */
	private static final String NO_SPACE = "NoSpace";
	/**
	 * Leading space option.
	 */
	private static final String LEADING_SPACE = " LeadingSpace";
	/**
	 * Trailing_space option.
	 */
	private static final String TRAILING_SPACE = "TrailingSpace ";
	/**
	 * Double space option.
	 */
	private static final String DOUBLE_SPACE = "Double  Space";

	/**
	 * Some array data for the drop downs.
	 */
	private static final String[] OPTIONS_ARRAY = {NO_SPACE, LEADING_SPACE, TRAILING_SPACE, DOUBLE_SPACE, ">", "<",
		"&", "\"", "<br/>"};
	/**
	 * Checkbox to enable or disable the null option.
	 */
	private final WCheckBox cbNullOption = new WCheckBox(false);

	/**
	 * Radio button select for selecting the default option on the dropdown.
	 */
	private final WRadioButtonSelect rgDefaultOption = new WRadioButtonSelect(
			new String[]{NONE, NO_SPACE,
				LEADING_SPACE,
				TRAILING_SPACE, DOUBLE_SPACE,
				">", "<", "&", "\"", "<br/>"});

	/**
	 * radio button select for selecting the dropdown type.
	 */
	private final WRadioButtonSelect rbsDDType = new WRadioButtonSelect(WDropdown.DropdownType.
			values());

	/**
	 * width control.
	 */
	private final WNumberField nfWidth = new WNumberField();
	/**
	 * text field to add a tool tip.
	 */
	private final WTextField tfToolTip = new WTextField();
	/**
	 * action on change control.
	 */
	private final WCheckBox cbSubmitOnChange = new WCheckBox();
	/**
	 * visibility control.
	 */
	private final WCheckBox cbVisible = new WCheckBox(true);
	/**
	 * disabled control.
	 */
	private final WCheckBox cbDisabled = new WCheckBox();
	/**
	 * action on change on/off checkbox.
	 */
	private final WCheckBox cbActionOnChange = new WCheckBox();
	/**
	 * ajax on/off checkbox.
	 */
	private final WCheckBox cbAjax = new WCheckBox();
	/**
	 * subordinate on/off checkbox.
	 */
	private final WCheckBox cbSubordinate = new WCheckBox();
	/**
	 * container for holding the dropdown.
	 */
	private final WPanel container = new WPanel();
	/**
	 * the info panel to be the target of the various control mechanisms.
	 */
	private final WPanel infoPanel = new WPanel();

	/**
	 * Constructor to put the example together.
	 */
	public WDropdownOptionsExample() {
		WFieldSet fieldSet = getDropDownControls();
		add(fieldSet);
		add(new WHorizontalRule());
		container.setLayout(new FlowLayout(Alignment.VERTICAL, 0, 6));
		add(container);
		add(new WHorizontalRule());
		add(infoPanel);
	}

	/**
	 * build the drop down controls.
	 *
	 * @return a field set containing the dropdown controls.
	 */
	private WFieldSet getDropDownControls() {
		WFieldSet fieldSet = new WFieldSet("Drop down configuration");
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		fieldSet.add(layout);

		rbsDDType.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		rbsDDType.setSelected(WDropdown.DropdownType.NATIVE);
		rbsDDType.setFrameless(true);
		layout.addField("Dropdown Type", rbsDDType);

		nfWidth.setMinValue(0);
		nfWidth.setDecimalPlaces(0);
		layout.addField("Width", nfWidth);
		layout.addField("ToolTip", tfToolTip);
		layout.addField("Include null option", cbNullOption);

		rgDefaultOption.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		rgDefaultOption.setButtonColumns(2);
		rgDefaultOption.setSelected(NONE);
		rgDefaultOption.setFrameless(true);
		layout.addField("Default Option", rgDefaultOption);

		layout.addField("Action on change", cbActionOnChange);
		layout.addField("Ajax", cbAjax);
		WField subField = layout.addField("Subordinate", cbSubordinate);
		//.getLabel().setHint("Does not work with Dropdown Type COMBO");

		layout.addField("Submit on change", cbSubmitOnChange);
		layout.addField("Visible", cbVisible);
		layout.addField("Disabled", cbDisabled);

		// Apply Button
		WButton apply = new WButton("Apply");
		fieldSet.add(apply);

		WSubordinateControl subSubControl = new WSubordinateControl();
		Rule rule = new Rule();
		subSubControl.addRule(rule);
		rule.setCondition(new Equal(rbsDDType, WDropdown.DropdownType.COMBO));
		rule.addActionOnTrue(new Disable(subField));
		rule.addActionOnFalse(new Enable(subField));
		fieldSet.add(subSubControl);

		apply.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				applySettings();
			}
		});

		return fieldSet;
	}

	/**
	 * Override the prepare paint to initialise the initial instance of the dropdown.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			applySettings();
			setInitialised(true);
		}

	}

	/**
	 * Apply the settings from the control table to the drop down.
	 */
	private void applySettings() {
		container.reset();
		infoPanel.reset();

		// create the list of options.
		List<String> options = new ArrayList<>(Arrays.asList(OPTIONS_ARRAY));

		if (cbNullOption.isSelected()) {
			options.add(0, "");
		}

		// create the dropdown.
		final WDropdown dropdown = new WDropdown(options);

		// set the dropdown type.
		dropdown.setType((DropdownType) rbsDDType.getSelected());

		// set the selected option if applicable.
		String selected = (String) rgDefaultOption.getSelected();
		if (selected != null && !NONE.equals(selected)) {
			dropdown.setSelected(selected);
		}

		// set the width.
		if (nfWidth.getValue() != null) {
			dropdown.setOptionWidth(nfWidth.getValue().intValue());
		}

		// set the tool tip.
		if (tfToolTip.getText() != null && tfToolTip.getText().length() > 0) {
			dropdown.setToolTip(tfToolTip.getText());
		}

		// set misc options.
		dropdown.setVisible(cbVisible.isSelected());
		dropdown.setDisabled(cbDisabled.isSelected());

		// add the action for action on change, ajax and subordinate.
		if (cbActionOnChange.isSelected() || cbAjax.isSelected() || cbSubmitOnChange.isSelected()) {
			final WStyledText info = new WStyledText();
			info.setWhitespaceMode(WhitespaceMode.PRESERVE);
			infoPanel.add(info);

			dropdown.setActionOnChange(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					String selectedOption = (String) dropdown.getSelected();
					info.setText(selectedOption);
				}
			});
		}

		// this has to be below the set action on change so it is
		// not over written.
		dropdown.setSubmitOnChange(cbSubmitOnChange.isSelected());

		// add the ajax target.
		if (cbAjax.isSelected()) {
			WAjaxControl update = new WAjaxControl(dropdown);
			update.addTarget(infoPanel);
			container.add(update);
		}

		// add the subordinate stuff.
		if (rbsDDType.getValue() == WDropdown.DropdownType.COMBO) {
			//This is to work around a WComponent Subordinate logic flaw.
			cbSubordinate.setSelected(false);
		}
		if (cbSubordinate.isSelected()) {
			WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
			container.add(group);
			WSubordinateControl control = new WSubordinateControl();
			container.add(control);

			for (String option : OPTIONS_ARRAY) {
				buildSubordinatePanel(dropdown, option, group, control);
			}

			// add a rule for none selected.
			Rule rule = new Rule();
			control.addRule(rule);
			rule.setCondition(new Equal(dropdown, ""));
			rule.addActionOnTrue(new Hide(group));
		}

		WFieldLayout flay = new WFieldLayout();
		flay.setLabelWidth(25);
		container.add(flay);
		flay.addField("Configured dropdown", dropdown);
		flay.addField((WLabel) null, new WButton("Submit"));
	}

	/**
	 * Builds a panel for the subordinate control, including the rule for that particular option.
	 *
	 * @param dropdown the subordinate trigger.
	 * @param value the dropdown option to be added
	 * @param group the group
	 * @param control the subordinate control
	 */
	private void buildSubordinatePanel(final WDropdown dropdown, final String value,
			final WComponentGroup<SubordinateTarget> group,
			final WSubordinateControl control) {
		// create the panel.
		WPanel panel = new WPanel();
		WStyledText subordinateInfo = new WStyledText();
		subordinateInfo.setWhitespaceMode(WhitespaceMode.PRESERVE);
		subordinateInfo.setText(value + " - Subordinate");
		panel.add(subordinateInfo);

		// add the panel to the screen and group.
		infoPanel.add(panel);
		group.addToGroup(panel);

		// create the rule
		Rule rule = new Rule();
		control.addRule(rule);
		rule.setCondition(new Equal(dropdown, value));
		rule.addActionOnTrue(new ShowInGroup(panel, group));
	}

}
