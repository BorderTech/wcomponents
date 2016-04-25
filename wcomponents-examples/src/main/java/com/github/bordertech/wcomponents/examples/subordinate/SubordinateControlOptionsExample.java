package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Disableable;
import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WDropdown.DropdownType;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPartialDateField;
import com.github.bordertech.wcomponents.WPasswordField;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WRow;
import com.github.bordertech.wcomponents.WSingleSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExampleLookupTable.TableWithNullOption;
import com.github.bordertech.wcomponents.subordinate.AbstractCompare.CompareType;
import com.github.bordertech.wcomponents.subordinate.Condition;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.DisableInGroup;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.EnableInGroup;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.GreaterThan;
import com.github.bordertech.wcomponents.subordinate.GreaterThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.HideInGroup;
import com.github.bordertech.wcomponents.subordinate.LessThan;
import com.github.bordertech.wcomponents.subordinate.LessThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.Mandatory;
import com.github.bordertech.wcomponents.subordinate.Match;
import com.github.bordertech.wcomponents.subordinate.Not;
import com.github.bordertech.wcomponents.subordinate.NotEqual;
import com.github.bordertech.wcomponents.subordinate.Optional;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.ShowInGroup;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates the different configuration options for a {@link WSubordinateControl}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SubordinateControlOptionsExample extends WContainer {

	/**
	 * Trigger for the subordinate control.
	 */
	private SubordinateTrigger trigger;

	/**
	 * Target textField.
	 */
	private final WTextField targetTextField = new WTextField();

	/**
	 * Target textFieldSet.
	 */
	private final WFieldSet targetFieldSet = new WFieldSet("Targetable WFieldSet");

	/**
	 * Target textFieldLayout.
	 */
	private final WFieldLayout targetFieldLayout = new WFieldLayout();

	/**
	 * Target WField1.
	 */
	private final WField targetField1 = targetFieldLayout.addField("Targetable WField",
			targetTextField);
	/**
	 * Target WField2.
	 */
	private final WField targetField2 = targetFieldLayout.addField("TextField 2", new WTextField());
	/**
	 * Target WField3.
	 */
	private final WField targetField3 = targetFieldLayout.addField("TextField 3", new WTextField());

	/**
	 * Target WComponentGroup.
	 */
	private final WComponentGroup<SubordinateTarget> targetGroup = new WComponentGroup<>();

	/**
	 * Target WPanel.
	 */
	private final WPanel targetPanel = new WPanel();

	/**
	 * Target WCollapsible.
	 */
	private final WCollapsible targetCollapsible = new WCollapsible(targetPanel,
			"Targetable WCollapsible");

	/**
	 * Panel that contains the configured subordinate control.
	 */
	private final WPanel buildControlPanel = new WPanel();

	/**
	 * Panel that contains the subordinate target.
	 */
	private final WPanel buildTargetPanel = new WPanel();

	/**
	 * CheckBox to indicate a Not condition should be used.
	 */
	private final WCheckBox cbNot = new WCheckBox();

	/**
	 * Trigger type to use in the subordinate control.
	 */
	private final WDropdown drpTriggerType = new WDropdown(TriggerType.values());

	/**
	 * String compare value for the condition.
	 */
	private final WDropdown comboCompareValue = new WDropdown();

	/**
	 * Date compare value for the condition (for WDateField triggers).
	 */
	private final WDateField dateCompareValue = new WDateField();

	/**
	 * Number compare value for the condition (for WNumbderField triggers).
	 */
	private final WNumberField numberCompareValue = new WNumberField();

	/**
	 * Compare type for the condition.
	 */
	private final WDropdown drpCompareType = new WDropdown(CompareType.values()) {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDesc(final Object option, final int index) {
			CompareType type = (CompareType) option;
			switch (type) {
				case EQUAL:
					return "=";
				case NOT_EQUAL:
					return "!=";
				case LESS_THAN:
					return "<";
				case LESS_THAN_OR_EQUAL:
					return "<=";
				case GREATER_THAN:
					return ">";
				case GREATER_THAN_OR_EQUAL:
					return ">=";
				case MATCH:
					return "matches";
				default:
					return super.getDesc(option, index);
			}
		}
	};

	/**
	 * Action Type for the subordinate control.
	 */
	private final WDropdown drpActionType = new WDropdown(ControlActionType.values());

	/**
	 * Target Type for the subordinate control.
	 */
	private final WDropdown drpTargetType = new WDropdown(TargetType.values());

	/**
	 * Field holding the String value.
	 */
	private final WField comboField;

	/**
	 * Field holding the Date value.
	 */
	private final WField dateField;

	/**
	 * Field holding the Number value.
	 */
	private final WField numberField;

	/**
	 * Test lookup table for list options.
	 */
	private static final String LOOKUP_TABLE_NAME = "australian_state";

	/**
	 * Disabled trigger.
	 */
	private final WCheckBox cbDisableTrigger = new WCheckBox();

	/**
	 * Readonly trigger.
	 */
	private final WCheckBox cbReadOnlyTrigger = new WCheckBox();

	/**
	 * Trigger components.
	 */
	private enum TriggerType {
		/**
		 * WCheckBox trigger.
		 */
		CheckBox,
		/**
		 * WCheckBoxSelect trigger.
		 */
		CheckBoxSelect,
		/**
		 * WDateField trigger.
		 */
		DateField,
		/**
		 * WDropdown trigger.
		 */
		Dropdown,
		/**
		 * WEmailField trigger.
		 */
		EmailField,
		/**
		 * WMultiSelect trigger.
		 */
		MultiSelect,
		/**
		 * WMultiSelectPair trigger.
		 */
		MultiSelectPair,
		/**
		 * NumberField trigger.
		 */
		NumberField,
		/**
		 * WPartialDateField trigger.
		 */
		PartialDateField,
		/**
		 * WPasswordField trigger.
		 */
		PasswordField,
		/**
		 * WPhoneNumberField trigger.
		 */
		PhoneNumberField,
		/**
		 * RadioButtonGroup trigger.
		 */
		RadioButtonGroup,
		/**
		 * WRadioButtonSelect trigger.
		 */
		RadioButtonSelect,
		/**
		 * WSingleSelect trigger.
		 */
		SingleSelect,
		/**
		 * WTextField trigger.
		 */
		TextField,
		/**
		 * WTextArea trigger.
		 */
		TextArea
	}

	/**
	 * Label width.
	 */
	private static final int LABEL_WIDTH = 40;

	/**
	 * Subordinate actions.
	 */
	private enum ControlActionType {
		/**
		 * SHOW on true, HIDE on false.
		 */
		SHOW_HIDE("Show | Hide"),
		/**
		 * ENABLE on true, DISABLE on false.
		 */
		ENABLE_DISABLE("Enable | Disable"),
		/**
		 * MANDATORY on true, OPTIONAL on false.
		 */
		MAN_OPT("Mandatory | Optional"),
		/**
		 * SHOWIN on true, HIDEIN on false.
		 */
		SHOWIN_HIDEIN("ShowIn | HideIn"),
		/**
		 * ENABLEIN on true, DISABLEIN on false.
		 */
		ENABLEIN_DISABLEIN("EnableIn | DisableIn");

		/**
		 * Description of action.
		 */
		private final String desc;

		/**
		 * @param desc the description of the action.
		 */
		ControlActionType(final String desc) {
			this.desc = desc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return desc;
		}
	}

	/**
	 * Target Types.
	 */
	private enum TargetType {
		/**
		 * WTextField target.
		 */
		WTEXTFIELD("WTextField"),
		/**
		 * WCollapsible target.
		 */
		WCOLLAPSIBLE("WCollapsible"),
		/**
		 * WFieldLayout target.
		 */
		WFIELDLAYOUT("WFieldLayout"),
		/**
		 * WField target.
		 */
		WFIELD("WField"),
		/**
		 * WFieldSet target.
		 */
		WFIELDSET("WFieldSet");

		/**
		 * Description of action.
		 */
		private String desc;

		/**
		 * @param desc the description of the target.
		 */
		TargetType(final String desc) {
			this.desc = desc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return desc;
		}
	}

	/**
	 * Construct the example.
	 */
	public SubordinateControlOptionsExample() {
		WFieldSet configFieldSet = new WFieldSet("Config Options");
		add(configFieldSet);

		WRow row = new WRow(12);
		configFieldSet.add(row);

		// Col1 - Not
		WColumn col = new WColumn(10);
		row.add(col);
		WFieldLayout configLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		col.add(configLayout);
		WField field = configLayout.addField("Not Condition", cbNot);
		field.setInputWidth(100);

		// Col2 - Trigger Type
		col = new WColumn(15);
		row.add(col);
		configLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		col.add(configLayout);
		field = configLayout.addField("Trigger Type", drpTriggerType);
		field.setInputWidth(100);

		// Col3 - Compare Type
		col = new WColumn(10);
		row.add(col);
		configLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		col.add(configLayout);
		field = configLayout.addField("Compare Type", drpCompareType);
		field.setInputWidth(100);

		// Col4 - Compare Value
		col = new WColumn(20);
		row.add(col);
		configLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		col.add(configLayout);
		comboField = configLayout.addField("Compare Value (String)", comboCompareValue);
		dateField = configLayout.addField("Compare Value (Date)", dateCompareValue);
		numberField = configLayout.addField("Compare Value (Number)", numberCompareValue);
		comboField.setInputWidth(100);
		dateField.setInputWidth(100);
		numberField.setInputWidth(100);

		comboCompareValue.setType(DropdownType.COMBO);

		WAjaxControl ajax = new WAjaxControl(drpTriggerType, configLayout);
		add(ajax);

		// Col5 - Action
		col = new WColumn(20);
		row.add(col);
		configLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		col.add(configLayout);
		field = configLayout.addField("Action", drpActionType);
		field.setInputWidth(100);

		drpActionType.setToolTip("on true action | on false action");

		// Col6 - Target
		col = new WColumn(20);
		row.add(col);
		configLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		col.add(configLayout);
		field = configLayout.addField("Target", drpTargetType);
		field.setInputWidth(100);

		ajax = new WAjaxControl(drpActionType, configLayout);
		add(ajax);

		WButton apply = new WButton("Apply");
		configFieldSet.add(apply);

		add(new WHorizontalRule());

		// Trigger Settings
		final WFieldSet triggerConfigSet = new WFieldSet("Trigger Settings");
		triggerConfigSet.setVisible(false);
		add(triggerConfigSet);

		WFieldLayout triggerConfigLayout = new WFieldLayout();
		triggerConfigSet.add(triggerConfigLayout);
		triggerConfigLayout.addField("Disable Trigger", cbDisableTrigger);
		triggerConfigLayout.addField("ReadOnly Trigger", cbReadOnlyTrigger);
		triggerConfigLayout.setLabelWidth(LABEL_WIDTH);

		// Build Panel for Control/Target
		final WPanel buildPanel = new WPanel();
		add(buildPanel);

		buildPanel.setVisible(false);

		// Control
		buildPanel.add(new WHeading(WHeading.SECTION, "Control"));
		buildPanel.add(buildControlPanel);

		buildPanel.add(new WHorizontalRule());

		// Target
		buildPanel.add(new WHeading(WHeading.SECTION, "Target"));
		buildPanel.add(buildTargetPanel);
		buildPanel.add(new WHorizontalRule());
		buildPanel.add(new WButton("submit"));

		// Actions
		drpTriggerType.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				setPresets();
			}
		});

		drpCompareType.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				setPresets();
			}
		});

		drpActionType.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				setPresets();
			}
		});

		apply.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				buildControl();
				buildPanel.setVisible(true);
				triggerConfigSet.setVisible(true);
			}
		});

		ajax = new WAjaxControl(cbDisableTrigger, buildPanel);
		triggerConfigSet.add(ajax);

		ajax = new WAjaxControl(cbReadOnlyTrigger, buildPanel);
		triggerConfigSet.add(ajax);

		// Setup target components
		buildTargetPanel.add(targetCollapsible);
		targetCollapsible.setCollapsed(false);

		targetPanel.add(targetFieldSet);

		targetFieldSet.add(targetFieldLayout);
		targetFieldLayout.setLabelWidth(LABEL_WIDTH);
		targetFieldLayout.setTitle("Targetable WFieldLayout");

		targetGroup.addToGroup(targetField1);
		targetGroup.addToGroup(targetField2);
		targetGroup.addToGroup(targetField3);
		targetPanel.add(targetGroup);

		// Set default options in String combo
		List<Object> options = new ArrayList<>();
		options.add(null);
		options.add("VIC");
		options.add("NSW");
		options.add("ACT");
		options.add("A");
		options.add("B");
		options.add("true");
		options.add("false");
		options.add("[abc]");
		options.add("[^abc]");
		options.add("\\d");
		options.add("foo[1-5]");
		options.add("Z{3}");
		options.add("(?i)foo");

		comboCompareValue.setOptions(options);
	}

	/**
	 * Build the subordinate control.
	 */
	private void buildControl() {
		buildControlPanel.reset();
		buildTargetPanel.reset();

		// Setup Trigger
		setupTrigger();

		// Create target
		SubordinateTarget target = setupTarget();

		// Create Actions
		com.github.bordertech.wcomponents.subordinate.Action trueAction;
		com.github.bordertech.wcomponents.subordinate.Action falseAction;

		switch ((ControlActionType) drpActionType.getSelected()) {
			case ENABLE_DISABLE:
				trueAction = new Enable(target);
				falseAction = new Disable(target);
				break;

			case SHOW_HIDE:
				trueAction = new Show(target);
				falseAction = new Hide(target);
				break;

			case MAN_OPT:
				trueAction = new Mandatory(target);
				falseAction = new Optional(target);
				break;

			case SHOWIN_HIDEIN:
				trueAction = new ShowInGroup(target, targetGroup);
				falseAction = new HideInGroup(target, targetGroup);
				break;

			case ENABLEIN_DISABLEIN:
				trueAction = new EnableInGroup(target, targetGroup);
				falseAction = new DisableInGroup(target, targetGroup);
				break;

			default:
				throw new SystemException("ControlAction type not valid");
		}

		// Create Condition
		Condition condition = createCondition();

		if (cbNot.isSelected()) {
			condition = new Not(condition);
		}

		// Create Rule
		Rule rule = new Rule(condition, trueAction, falseAction);

		// Create Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		buildControlPanel.add(control);

		if (trigger.getLabel() != null) {
			trigger.getLabel().setHint(control.toString());
		}

	}

	/**
	 * Setup the trigger for the subordinate control.
	 */
	private void setupTrigger() {
		String label = drpTriggerType.getSelected() + " Trigger";

		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(LABEL_WIDTH);

		buildControlPanel.add(layout);

		switch ((TriggerType) drpTriggerType.getSelected()) {
			case RadioButtonGroup:

				trigger = new RadioButtonGroup();

				WFieldSet rbSet = new WFieldSet("Select an option");
				RadioButtonGroup group = (RadioButtonGroup) trigger;
				WRadioButton rb1 = group.addRadioButton("A");
				WRadioButton rb2 = group.addRadioButton("B");
				WRadioButton rb3 = group.addRadioButton("C");
				rbSet.add(group);
				rbSet.add(rb1);
				rbSet.add(new WLabel("A", rb1));
				rbSet.add(new WText("\u00a0"));
				rbSet.add(rb2);
				rbSet.add(new WLabel("B", rb2));
				rbSet.add(new WText("\u00a0"));
				rbSet.add(rb3);
				rbSet.add(new WLabel("C", rb3));
				layout.addField(label, rbSet);
				return;

			case CheckBox:
				trigger = new WCheckBox();
				break;

			case CheckBoxSelect:
				trigger = new WCheckBoxSelect(LOOKUP_TABLE_NAME);
				break;

			case DateField:
				trigger = new WDateField();
				break;

			case Dropdown:
				trigger = new WDropdown(new TableWithNullOption(LOOKUP_TABLE_NAME));
				break;

			case EmailField:
				trigger = new WEmailField();
				break;

			case MultiSelect:
				trigger = new WMultiSelect(LOOKUP_TABLE_NAME);
				break;

			case MultiSelectPair:
				trigger = new WMultiSelectPair(LOOKUP_TABLE_NAME);
				break;

			case NumberField:
				trigger = new WNumberField();
				break;

			case PartialDateField:
				trigger = new WPartialDateField();
				break;

			case PasswordField:
				trigger = new WPasswordField();
				break;

			case PhoneNumberField:
				trigger = new WPhoneNumberField();
				break;

			case RadioButtonSelect:
				trigger = new WRadioButtonSelect(LOOKUP_TABLE_NAME);
				break;

			case SingleSelect:
				trigger = new WSingleSelect(LOOKUP_TABLE_NAME);
				break;

			case TextArea:
				trigger = new WTextArea();
				break;

			case TextField:
				trigger = new WTextField();
				break;

			default:
				throw new SystemException("Trigger type not valid");
		}

		layout.addField(label, trigger);
	}

	/**
	 * @return the target for the subordinate control.
	 */
	private SubordinateTarget setupTarget() {
		SubordinateTarget target;
		switch ((TargetType) drpTargetType.getSelected()) {
			case WCOLLAPSIBLE:
				target = targetCollapsible;
				break;

			case WFIELD:
				target = targetField1;
				break;

			case WFIELDLAYOUT:
				target = targetFieldLayout;
				break;

			case WFIELDSET:
				target = targetFieldSet;
				break;

			case WTEXTFIELD:
				target = targetTextField;
				break;

			default:
				throw new SystemException("Target type not valid");
		}
		return target;
	}

	/**
	 * @return the condition for the subordinate control.
	 */
	private Condition createCondition() {
		// Compare value
		Object value;
		switch ((TriggerType) drpTriggerType.getSelected()) {
			case DateField:
				value = dateCompareValue.getValue();
				break;

			case NumberField:
				value = numberCompareValue.getValue();
				break;

			default:
				value = comboCompareValue.getValue();
				break;
		}

		// Create condition
		Condition condition;
		switch ((CompareType) drpCompareType.getSelected()) {
			case EQUAL:
				condition = new Equal(trigger, value);
				break;

			case NOT_EQUAL:
				condition = new NotEqual(trigger, value);
				break;

			case LESS_THAN:
				condition = new LessThan(trigger, value);
				break;

			case LESS_THAN_OR_EQUAL:
				condition = new LessThanOrEqual(trigger, value);
				break;

			case GREATER_THAN:
				condition = new GreaterThan(trigger, value);
				break;

			case GREATER_THAN_OR_EQUAL:
				condition = new GreaterThanOrEqual(trigger, value);
				break;

			case MATCH:
				condition = new Match(trigger, (String) value);
				break;

			default:
				throw new SystemException("Compare type not valid");
		}

		return condition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			setPresets();
			setInitialised(true);
		}

		if (trigger instanceof Disableable) {
			((Disableable) trigger).setDisabled(cbDisableTrigger.isSelected());
		}

		if (trigger instanceof Input) {
			((Input) trigger).setReadOnly(cbReadOnlyTrigger.isSelected());
		}
	}

	/**
	 * Setup the default values for the configuration options.
	 */
	private void setPresets() {
		TriggerType selectedTrigger = (TriggerType) drpTriggerType.getSelected();

		comboField.setVisible(
				selectedTrigger != TriggerType.DateField && selectedTrigger != TriggerType.NumberField);
		dateField.setVisible(selectedTrigger == TriggerType.DateField);
		numberField.setVisible(selectedTrigger == TriggerType.NumberField);

		// If a group action selected, then target can only be the field (as it is defined in a group)
		switch ((ControlActionType) drpActionType.getSelected()) {
			case ENABLEIN_DISABLEIN:
			case SHOWIN_HIDEIN:
				drpTargetType.setSelected(TargetType.WFIELD);
				drpTargetType.setDisabled(true);
				break;
			default:
				drpTargetType.setDisabled(false);
		}
	}

}
