package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WBeanComponent;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WFileWidget;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WMultiDropdown;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.WMultiTextField;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPartialDateField;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WShuffler;
import com.github.bordertech.wcomponents.WSingleSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Mandatory;
import com.github.bordertech.wcomponents.subordinate.Optional;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Demonstrate WInput components that are bound to a bean and have change actions.
 * <p>
 * The example displays the current value of the component as well as the current value of the bean.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class InputBeanBindingExample extends WBeanContainer {

	/**
	 * Example options.
	 */
	private final List<String> options = Arrays.asList(new String[]{"A", "B", "C"});

	/**
	 * Example WCheckBox.
	 */
	private final WCheckBoxSelect checkBoxSelect = new WCheckBoxSelect(options);
	/**
	 * Example WMultiDropdown.
	 */
	private final WMultiDropdown multiDropdown = new WMultiDropdown(options);
	/**
	 * Example WMultiSelect.
	 */
	private final WMultiSelect multiSelect = new WMultiSelect(options);
	/**
	 * Example WMultiSelectPair.
	 */
	private final WMultiSelectPair multiSelectPair = new WMultiSelectPair(options);
	/**
	 * Example WDropdown.
	 */
	private final WDropdown dropdown = new WDropdown(options);
	/**
	 * Example WRadioButtonSelect.
	 */
	private final WRadioButtonSelect radioButtonSelect = new WRadioButtonSelect(options);
	/**
	 * Example WSingleSelect.
	 */
	private final WSingleSelect singleSelect = new WSingleSelect(options);
	/**
	 * Example WCheckBox.
	 */
	private final WCheckBox checkBox = new WCheckBox();
	/**
	 * Example WDateField.
	 */
	private final WDateField dateField = new WDateField();
	/**
	 * Example WEmailField.
	 */
	private final WEmailField emailField = new WEmailField();
	/**
	 * Example WFileWidget.
	 */
	private final WFileWidget fileWidget = new WFileWidget();
	/**
	 * Example WMultiFileWidget.
	 */
	private final WMultiFileWidget multiFileWidget = new WMultiFileWidget();
	/**
	 * Example WMultiTextField.
	 */
	private final WMultiTextField multiTextField = new WMultiTextField();
	/**
	 * Example WNumberField.
	 */
	private final WNumberField numberField = new WNumberField();
	/**
	 * Example WPartialDateField.
	 */
	private final WPartialDateField partialDateField = new WPartialDateField();
	/**
	 * Example WPhoneNumberField.
	 */
	private final WPhoneNumberField phoneNumberField = new WPhoneNumberField();
	/**
	 * Example RadioButtonGroup.
	 */
	private final RadioButtonGroup radioButtonGroup = new RadioButtonGroup();
	/**
	 * Example WRadioButton.
	 */
	private final WRadioButton radioButton = radioButtonGroup.addRadioButton(1);
	/**
	 * Example WShuffler.
	 */
	private final WShuffler shuffler = new WShuffler();
	/**
	 * Example WTextField.
	 */
	private final WTextField textField = new WTextField();
	/**
	 * Example WTextArea.
	 */
	private final WTextArea textArea = new WTextArea();

	/**
	 * Example RadioButtonGroup - WRadioButton1.
	 */
	private final RadioButtonGroup radioButtonGroup2 = new RadioButtonGroup();
	/**
	 * Example RadioButtonGroup - WRadioButton1.
	 */
	private final WRadioButton radioButton1 = radioButtonGroup2.addRadioButton("rb1");
	/**
	 * Example RadioButtonGroup - WRadioButton2.
	 */
	private final WRadioButton radioButton2 = radioButtonGroup2.addRadioButton("rb2");
	/**
	 * Example RadioButtonGroup - WRadioButton3.
	 */
	private final WRadioButton radioButton3 = radioButtonGroup2.addRadioButton("rb3");
	/**
	 * Example RadioButtonGroup - Value.
	 */
	private final WText radioButtonGroupValue = new WText();

	/**
	 * Display messages.
	 */
	private final WMessages messages = new WMessages();
	/**
	 * Layout for the example input components.
	 */
	private final WFieldLayout layout = new WFieldLayout();

	/**
	 * Check box used to indicate if the input components should be mandatory.
	 */
	private final WCheckBox mandatory = new WCheckBox();
	/**
	 * Check box used to indicate if the input components should be disabled.
	 */
	private final WCheckBox disabled = new WCheckBox();
	/**
	 * Check box used to indicate if the input components should be readOnly.
	 */
	private final WCheckBox readOnly = new WCheckBox();

	/**
	 * Construct the example.
	 */
	public InputBeanBindingExample() {
		// Setup radio button
		WContainer radioPanel = new WPanel();
		radioPanel.add(radioButton);
		radioPanel.add(new MyPanel(radioButtonGroup));

		add(messages);
		addButtons();
		layout.setLabelWidth(30);
		add(layout);
		addButtons();

		readOnly.setSubmitOnChange(true);
		layout.addField("Fields Mandatory", mandatory);
		layout.addField("Fields Disabled", disabled);
		layout.addField("Fields ReadOnly", readOnly);

		// Bean Properties
		checkBoxSelect.setBeanProperty("checkBoxSelect");
		multiDropdown.setBeanProperty("multiDropdown");
		multiSelect.setBeanProperty("multiSelect");
		multiSelectPair.setBeanProperty("multiSelectPair");
		dropdown.setBeanProperty("dropdown");
		radioButtonSelect.setBeanProperty("radioButtonSelect");
		singleSelect.setBeanProperty("singleSelect");
		checkBox.setBeanProperty("checkBox");
		dateField.setBeanProperty("dateField");
		emailField.setBeanProperty("emailField");
		fileWidget.setBeanProperty("fileWidget");
		multiFileWidget.setBeanProperty("multiFileWidget");
		multiTextField.setBeanProperty("multiTextField");
		numberField.setBeanProperty("numberField");
		partialDateField.setBeanProperty("paritalDateField");
		phoneNumberField.setBeanProperty("phoneNumberField");
		radioButtonGroup.setBeanProperty("radioButton");
		shuffler.setBeanProperty("shuffler");
		textField.setBeanProperty("textField");
		textArea.setBeanProperty("textArea");

		// Add to Field Layout
		layout.addField("WCheckBoxSelect", new MyPanel(checkBoxSelect));
		layout.addField("WMultiDropDown", new MyPanel(multiDropdown));
		layout.addField("WMultiSelect", new MyPanel(multiSelect));
		layout.addField("WMultiSelectPair", new MyPanel(multiSelectPair));
		layout.addField("WDropdown", new MyPanel(dropdown));
		layout.addField("WRadioButtonSelect", new MyPanel(radioButtonSelect));
		layout.addField("WSingleSelect", new MyPanel(singleSelect));
		layout.addField("WCheckBox", new MyPanel(checkBox));
		layout.addField("WDateField", new MyPanel(dateField));
		layout.addField("WEmailField", new MyPanel(emailField));
		layout.addField("WFileWidget", new MyPanel(fileWidget));
		layout.addField("WMultiFileWidget", new MyPanel(multiFileWidget));
		layout.addField("WMultiTextField", new MyPanel(multiTextField));
		layout.addField("WNumberField", new MyPanel(numberField));
		layout.addField("WPartialDateField", new MyPanel(partialDateField));
		layout.addField("WPhoneNumberField", new MyPanel(phoneNumberField));
		layout.addField("WRadioButton", radioPanel);
		layout.addField("WShuffler", new MyPanel(shuffler));
		layout.addField("WTextField", new MyPanel(textField));
		layout.addField("WTextArea", new MyPanel(textArea));

		// Setup RadioButtonGroup
		WFieldLayout groupLayout = new WFieldLayout();
		groupLayout.addField("rb1", radioButton1);
		groupLayout.addField("rb2", radioButton2);
		groupLayout.addField("rb3", radioButton3);
		WFieldSet groupPanel = new WFieldSet("Test");
		groupPanel.add(groupLayout);
		groupPanel.add(radioButtonGroupValue);
		groupPanel.add(radioButtonGroup2);
		WField groupField = layout.addField("rb1", groupPanel);

		// AJAX Control for RadioButtonGroup (not a subordinate target)
		add(new WAjaxControl(mandatory, groupField));
		add(new WAjaxControl(disabled, groupField));

		// Change Actions
		checkBoxSelect.setActionOnChange(new MyChangedAction(messages, "WCheckBoxSelect"));
		multiDropdown.setActionOnChange(new MyChangedAction(messages, "WMultiDropdown"));
		multiSelect.setActionOnChange(new MyChangedAction(messages, "WMultiSelect"));
		multiSelectPair.setActionOnChange(new MyChangedAction(messages, "WMultiSelectPair"));
		dropdown.setActionOnChange(new MyChangedAction(messages, "WDropdown"));
		radioButtonSelect.setActionOnChange(new MyChangedAction(messages, "WRadioButtonSelect"));
		singleSelect.setActionOnChange(new MyChangedAction(messages, "WSingleSelect"));
		checkBox.setActionOnChange(new MyChangedAction(messages, "WCheckBox"));
		dateField.setActionOnChange(new MyChangedAction(messages, "WDateField"));
		emailField.setActionOnChange(new MyChangedAction(messages, "WEmailField"));
		fileWidget.setActionOnChange(new MyChangedAction(messages, "WFileWidget"));
		numberField.setActionOnChange(new MyChangedAction(messages, "WNumberField"));
		partialDateField.setActionOnChange(new MyChangedAction(messages, "WPartialDateField"));
		phoneNumberField.setActionOnChange(new MyChangedAction(messages, "WPhoneNumberField"));
		textField.setActionOnChange(new MyChangedAction(messages, "WTextField"));
		textArea.setActionOnChange(new MyChangedAction(messages, "WTextArea"));
		multiFileWidget.setActionOnChange(new MyChangedAction(messages, "WMultiFileWidget"));
		multiTextField.setActionOnChange(new MyChangedAction(messages, "WMultiTextField"));
		radioButtonGroup.setActionOnChange(new MyChangedAction(messages, "RadioButton"));
		shuffler.setActionOnChange(new MyChangedAction(messages, "WShuffler"));
		radioButtonGroup2.setActionOnChange(new MyChangedAction(messages, "RadioButtonGroup"));

		addSubordinate();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			setInitialised(true);
			setBean(new MyDemoBean());
		}

		// Handle readOnly state
		checkBoxSelect.setReadOnly(readOnly.isSelected());
		multiDropdown.setReadOnly(readOnly.isSelected());
		multiSelect.setReadOnly(readOnly.isSelected());
		multiSelectPair.setReadOnly(readOnly.isSelected());
		dropdown.setReadOnly(readOnly.isSelected());
		radioButtonSelect.setReadOnly(readOnly.isSelected());
		singleSelect.setReadOnly(readOnly.isSelected());
		checkBox.setReadOnly(readOnly.isSelected());
		dateField.setReadOnly(readOnly.isSelected());
		emailField.setReadOnly(readOnly.isSelected());
		fileWidget.setReadOnly(readOnly.isSelected());
		multiFileWidget.setReadOnly(readOnly.isSelected());
		multiTextField.setReadOnly(readOnly.isSelected());
		numberField.setReadOnly(readOnly.isSelected());
		partialDateField.setReadOnly(readOnly.isSelected());
		phoneNumberField.setReadOnly(readOnly.isSelected());
		radioButton.setReadOnly(readOnly.isSelected());
		shuffler.setReadOnly(readOnly.isSelected());
		textField.setReadOnly(readOnly.isSelected());
		textArea.setReadOnly(readOnly.isSelected());
		radioButton1.setReadOnly(readOnly.isSelected());
		radioButton2.setReadOnly(readOnly.isSelected());
		radioButton3.setReadOnly(readOnly.isSelected());

		radioButtonGroupValue.setText("Value: " + radioButtonGroup2.getValueAsString());

		// Handle RadioButtonGroup (not a subordinate target)
		radioButtonGroup2.setMandatory(mandatory.isSelected());
		radioButtonGroup2.setDisabled(disabled.isSelected());
	}

	/**
	 * Setup the subordinate control.
	 */
	private void addSubordinate() {
		// Set up subordinate (to make mandatory/optional)
		WComponentGroup<SubordinateTarget> inputs = new WComponentGroup<>();
		add(inputs);
		inputs.addToGroup(checkBoxSelect);
		inputs.addToGroup(multiDropdown);
		inputs.addToGroup(multiSelect);
		inputs.addToGroup(multiSelectPair);
		inputs.addToGroup(dropdown);
		inputs.addToGroup(radioButtonSelect);
		inputs.addToGroup(singleSelect);
		inputs.addToGroup(checkBox);
		inputs.addToGroup(dateField);
		inputs.addToGroup(emailField);
		inputs.addToGroup(fileWidget);
		inputs.addToGroup(multiFileWidget);
		inputs.addToGroup(multiTextField);
		inputs.addToGroup(numberField);
		inputs.addToGroup(partialDateField);
		inputs.addToGroup(phoneNumberField);
		inputs.addToGroup(radioButton);
		inputs.addToGroup(shuffler);
		inputs.addToGroup(textField);
		inputs.addToGroup(textArea);
		inputs.addToGroup(radioButton1);
		inputs.addToGroup(radioButton2);
		inputs.addToGroup(radioButton3);

		WSubordinateControl control = new WSubordinateControl();
		add(control);

		// Mandatory
		Rule rule = new Rule();
		rule.setCondition(new Equal(mandatory, "true"));
		rule.addActionOnTrue(new Mandatory(inputs));
		rule.addActionOnFalse(new Optional(inputs));
		control.addRule(rule);

		// Disabled
		rule = new Rule();
		rule.setCondition(new Equal(disabled, "true"));
		rule.addActionOnTrue(new Disable(inputs));
		rule.addActionOnFalse(new Enable(inputs));
		control.addRule(rule);
	}

	/**
	 * Setup the action buttons.
	 */
	private void addButtons() {
		// Validation Button
		WButton buttonValidate = new WButton("Validate and Update Bean");
		add(buttonValidate);
		buttonValidate.setAction(new ValidatingAction(messages.getValidationErrors(), layout) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				WebUtilities.updateBeanValue(layout);
				messages.success("OK");
			}
		});

		// Update Bean
		WButton buttonUpdate = new WButton("Update Bean");
		add(buttonUpdate);
		buttonUpdate.setAction(new Action() {

			@Override
			public void execute(final ActionEvent event) {
				WebUtilities.updateBeanValue(layout);
			}
		});

		// Reset Inputs
		WButton buttonReset = new WButton("Reset Inputs");
		add(buttonReset);
		buttonReset.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				layout.reset();
			}
		});

		// Reset Bean
		WButton buttonBean = new WButton("Reset Bean");
		add(buttonBean);
		buttonBean.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				InputBeanBindingExample.this.setBean(new MyDemoBean());
			}
		});

		add(new WButton("submit"));
	}

	/**
	 * Class used to display the WINput component as well as its current value and its corresponding value on the bean.
	 */
	public static class MyPanel extends WContainer {

		/**
		 * Display the current string value of the component.
		 */
		private final WText stringValue = new WText();
		/**
		 * Display the current data value of the component.
		 */
		private final WText dataValue = new WText();
		/**
		 * Display the current value on the bean.
		 */
		private final WText beanValue = new WText();
		/**
		 * The input component being displayed.
		 */
		private final Input input;

		/**
		 * Construct the panel.
		 *
		 * @param input the input component being displayed.
		 */
		public MyPanel(final Input input) {
			this.input = input;

			add(input);

			WPanel details = new WPanel();
			details.setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));
			add(details);
			details.add(stringValue);
			details.add(dataValue);
			details.add(beanValue);
			add(new WHorizontalRule());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);
			stringValue.setText("Value as String: " + input.getValueAsString());
			dataValue.setText("Data: " + formatValue(input.getData()));
			Object value = null;
			if (input instanceof WBeanComponent) {
				value = ((WBeanComponent) input).getBeanValue();
			}
			beanValue.setText("Bean: " + value);
		}

		/**
		 * @param data the data to be displayed.
		 * @return the formatted data value.
		 */
		private String formatValue(final Object data) {
			if (data == null) {
				return "null";
			} else if (data instanceof Object[]) {
				StringBuffer values = new StringBuffer();
				values.append("Array - ");
				Object[] array = (Object[]) data;
				for (int i = 0; i < array.length; i++) {
					if (i > 0) {
						values.append(", ");
					}
					values.append(array[i]);
				}
				return values.toString();
			} else {
				return data.toString();
			}
		}

	}

	/**
	 * Action class to display a message that the input component has changed.
	 */
	public static class MyChangedAction implements Action {

		/**
		 * The message component to add a message to.
		 */
		private final WMessages messages;
		/**
		 * The description to include in the message.
		 */
		private final String desc;

		/**
		 * Construct the action.
		 *
		 * @param messages the message component to add a message to
		 * @param desc the description to include in the message
		 */
		public MyChangedAction(final WMessages messages, final String desc) {
			this.messages = messages;
			this.desc = desc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute(final ActionEvent event) {
			messages.info(desc + " changed.");
		}
	}

	/**
	 * Example bean to bind to the input components.
	 */
	public static class MyDemoBean {

		/**
		 * Value bound to a checkBoxSelect.
		 */
		private List<String> checkBoxSelect;
		/**
		 * Value bound to a multiDropdown.
		 */
		private List<String> multiDropdown;
		/**
		 * Value bound to a multiSelect.
		 */
		private List<String> multiSelect;
		/**
		 * Value bound to a multiSelectPair.
		 */
		private List<String> multiSelectPair;
		/**
		 * Value bound to a dropdown.
		 */
		private String dropdown;
		/**
		 * Value bound to a WRadioButtonSelect.
		 */
		private String radioButtonSelect;
		/**
		 * Value bound to a singleSelect.
		 */
		private String singleSelect;
		/**
		 * Value bound to a checkBox.
		 */
		private boolean checkBox;
		/**
		 * Value bound to a dateField.
		 */
		private Date dateField;
		/**
		 * Value bound to a emailField.
		 */
		private String emailField;
		/**
		 * Value bound to a fileWidget.
		 */
		private FileItemWrap fileWidget;
		/**
		 * Value bound to a multiFileWidget.
		 */
		private String multiFileWidget;
		/**
		 * Value bound to a multiTextField.
		 */
		private String[] multiTextField;
		/**
		 * Value bound to a numberField.
		 */
		private BigDecimal numberField;
		/**
		 * Value bound to a paritalDateField.
		 */
		private String paritalDateField;
		/**
		 * Value bound to a phoneNumberField.
		 */
		private String phoneNumberField;
		/**
		 * Value bound to a radioButton.
		 */
		private Integer radioButton;
		/**
		 * Value bound to a shuffler.
		 */
		private List<String> shuffler = Arrays.asList(new String[]{"A", "B", "C"});
		/**
		 * Value bound to a textField.
		 */
		private String textField;
		/**
		 * Value bound to a textArea.
		 */
		private String textArea;

		/**
		 * @return the checkBoxSelect
		 */
		public List<String> getCheckBoxSelect() {
			return checkBoxSelect;
		}

		/**
		 * @param checkBoxSelect the checkBoxSelect to set
		 */
		public void setCheckBoxSelect(final List<String> checkBoxSelect) {
			this.checkBoxSelect = checkBoxSelect;
		}

		/**
		 * @return the multiDropdown
		 */
		public List<String> getMultiDropdown() {
			return multiDropdown;
		}

		/**
		 * @param multiDropdown the multiDropdown to set
		 */
		public void setMultiDropdown(final List<String> multiDropdown) {
			this.multiDropdown = multiDropdown;
		}

		/**
		 * @return the multiSelect
		 */
		public List<String> getMultiSelect() {
			return multiSelect;
		}

		/**
		 * @param multiSelect the multiSelect to set
		 */
		public void setMultiSelect(final List<String> multiSelect) {
			this.multiSelect = multiSelect;
		}

		/**
		 * @return the multiSelectPair
		 */
		public List<String> getMultiSelectPair() {
			return multiSelectPair;
		}

		/**
		 * @param multiSelectPair the multiSelectPair to set
		 */
		public void setMultiSelectPair(final List<String> multiSelectPair) {
			this.multiSelectPair = multiSelectPair;
		}

		/**
		 * @return the dropdown
		 */
		public String getDropdown() {
			return dropdown;
		}

		/**
		 * @param dropdown the dropdown to set
		 */
		public void setDropdown(final String dropdown) {
			this.dropdown = dropdown;
		}

		/**
		 * @return the radioButtonSelect
		 */
		public String getRadioButtonSelect() {
			return radioButtonSelect;
		}

		/**
		 * @param radioButtonSelect the radioButtonSelect to set
		 */
		public void setRadioButtonSelect(final String radioButtonSelect) {
			this.radioButtonSelect = radioButtonSelect;
		}

		/**
		 * @return the singleSelect
		 */
		public String getSingleSelect() {
			return singleSelect;
		}

		/**
		 * @param singleSelect the singleSelect to set
		 */
		public void setSingleSelect(final String singleSelect) {
			this.singleSelect = singleSelect;
		}

		/**
		 * @return the checkBox
		 */
		public boolean isCheckBox() {
			return checkBox;
		}

		/**
		 * @param checkBox the checkBox to set
		 */
		public void setCheckBox(final boolean checkBox) {
			this.checkBox = checkBox;
		}

		/**
		 * @return the dateField
		 */
		public Date getDateField() {
			return dateField;
		}

		/**
		 * @param dateField the dateField to set
		 */
		public void setDateField(final Date dateField) {
			this.dateField = dateField;
		}

		/**
		 * @return the emailField
		 */
		public String getEmailField() {
			return emailField;
		}

		/**
		 * @param emailField the emailField to set
		 */
		public void setEmailField(final String emailField) {
			this.emailField = emailField;
		}

		/**
		 * @return the fileWidget
		 */
		public FileItemWrap getFileWidget() {
			return fileWidget;
		}

		/**
		 * @param fileWidget the fileWidget to set
		 */
		public void setFileWidget(final FileItemWrap fileWidget) {
			this.fileWidget = fileWidget;
		}

		/**
		 * @return the multiFileWidget
		 */
		public String getMultiFileWidget() {
			return multiFileWidget;
		}

		/**
		 * @param multiFileWidget the multiFileWidget to set
		 */
		public void setMultiFileWidget(final String multiFileWidget) {
			this.multiFileWidget = multiFileWidget;
		}

		/**
		 * @return the multiTextField
		 */
		public String[] getMultiTextField() {
			return multiTextField;
		}

		/**
		 * @param multiTextField the multiTextField to set
		 */
		public void setMultiTextField(final String[] multiTextField) {
			this.multiTextField = multiTextField;
		}

		/**
		 * @return the numberField
		 */
		public BigDecimal getNumberField() {
			return numberField;
		}

		/**
		 * @param numberField the numberField to set
		 */
		public void setNumberField(final BigDecimal numberField) {
			this.numberField = numberField;
		}

		/**
		 * @return the paritalDateField
		 */
		public String getParitalDateField() {
			return paritalDateField;
		}

		/**
		 * @param paritalDateField the paritalDateField to set
		 */
		public void setParitalDateField(final String paritalDateField) {
			this.paritalDateField = paritalDateField;
		}

		/**
		 * @return the phoneNumberField
		 */
		public String getPhoneNumberField() {
			return phoneNumberField;
		}

		/**
		 * @param phoneNumberField the phoneNumberField to set
		 */
		public void setPhoneNumberField(final String phoneNumberField) {
			this.phoneNumberField = phoneNumberField;
		}

		/**
		 * @return the radioButton
		 */
		public Integer getRadioButton() {
			return radioButton;
		}

		/**
		 * @param radioButton the radioButton to set
		 */
		public void setRadioButton(final Integer radioButton) {
			this.radioButton = radioButton;
		}

		/**
		 * @return the shuffler
		 */
		public List<String> getShuffler() {
			return shuffler;
		}

		/**
		 * @param shuffler the shuffler to set
		 */
		public void setShuffler(final List<String> shuffler) {
			this.shuffler = shuffler;
		}

		/**
		 * @return the textField
		 */
		public String getTextField() {
			return textField;
		}

		/**
		 * @param textField the textField to set
		 */
		public void setTextField(final String textField) {
			this.textField = textField;
		}

		/**
		 * @return the textArea
		 */
		public String getTextArea() {
			return textArea;
		}

		/**
		 * @param textArea the textArea to set
		 */
		public void setTextArea(final String textArea) {
			this.textArea = textArea;
		}

	}

}
