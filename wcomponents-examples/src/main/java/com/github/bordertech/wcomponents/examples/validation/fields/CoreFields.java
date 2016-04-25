package com.github.bordertech.wcomponents.examples.validation.fields;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCancelButton;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WConfirmationButton;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPrintButton;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.validator.DateFieldPivotValidator;
import com.github.bordertech.wcomponents.validator.RegExFieldValidator;
import java.util.Date;

/**
 * <p>
 * This <code>ValidatableComponent</code> just contains examples of validation of all the core <code>WComponent</code>s.
 * </p>
 * <p>
 * <b>NOTE:</b> This example makes use of the <code>WFieldLayout</code> and <code>WField</code> components to provide
 * alot of the ground work to do with validation of this component. The <code>BasicFields</code> example from the
 * <code>.../examples/validation/basic</code> package shows a similar setup but without using the
 * <code>WFieldLayout</code> and <code>WField</code> components.
 * </p>
 * <p>
 * The "sourceField" passed to the WFieldLayout (and the showErrorIndicators() method) must be consistent with the type
 * of validation being performed so that the error messages can be successfully linked back to the appropriate input
 * fields. (Another option would be to pass validation to a Business Object instance).
 * </p>
 *
 * @author Adam Millard
 */
public class CoreFields extends WPanel {

	/**
	 * the cancel button.
	 */
	private final WCancelButton cancelBtn;

	/**
	 * the print button.
	 */
	private final WPrintButton printBtn;

	/**
	 * the submit button.
	 */
	private final WButton submitBtn;

	/**
	 * the reset button.
	 */
	private final WConfirmationButton resetBtn;

	/**
	 * the Text field.
	 */
	private final WTextField textField;

	/**
	 * the text area.
	 */
	private final WTextArea textArea;

	/**
	 * the field set of the radio button group.
	 */
	private final WFieldSet radioButtonGroupFieldSet;

	/**
	 * the radio button group itself.
	 */
	private final RadioButtonGroup rbgroup;

	/**
	 * Creates a CoreFields example.
	 */
	public CoreFields() {
		// set up layouts.
		WFieldLayout fields = new WFieldLayout();
		fields.setLabelWidth(30);
		add(fields);

		// text field.
		textField = new WTextField();
		textField.setMinLength(2);
		WField field = fields.addField("WTextField", textField);
		field.addValidator(new RegExFieldValidator("^[a-zA-Z]*$",
				"{0} must only contain alphabetic characters."));
		field.getLabel().setHint("Must contain only alphabetic characters.");

		// text area.
		textArea = new WTextArea();
		textArea.setRows(5);
		textArea.setColumns(50);
		textArea.setMaxLength(300);
		textArea.setMandatory(true);
		WField textAreaField = fields.addField("WTextArea", textArea);
		textAreaField.getLabel().setHint("required with a maximum of 300 characters");

		// date field.
		WDateField df = new WDateField();
		WField dateField = fields.addField("WDateField", df);
		dateField.getLabel().setHint("before today");
		df.setMaxDate(DateUtilities.roundToDay(new Date()));
		dateField.addValidator(new DateFieldPivotValidator(DateFieldPivotValidator.BEFORE));
		df.setToolTip("Set a date before today");

		// date field - with min and max set.
		WDateField df2 = new WDateField();
		df2.setMinDate(DateUtilities.createDate(01, 01, 2012));
		df2.setMaxDate(DateUtilities.createDate(31, 01, 2012));
		dateField = fields.addField("WDateField with Min and Max", df2);
		dateField.getLabel().setHint("must be between 01 JAN 2012 and 31 JAN 2012");
		df2.setToolTip("Set a date between 01 JAN 2012 and 31 JAN 2012");

		// radio button group
		// note the validation is actually attached to the
		// CoreFields container.
		radioButtonGroupFieldSet = new WFieldSet("RadioButtonGroup");
		radioButtonGroupFieldSet.setFrameType(WFieldSet.FrameType.NO_TEXT);
		rbgroup = new RadioButtonGroup();
		WRadioButton rb1 = rbgroup.addRadioButton("Yes");
		WRadioButton rb2 = rbgroup.addRadioButton("No");
		WRadioButton rb3 = rbgroup.addRadioButton("Uncertain");

		radioButtonGroupFieldSet.add(rb1);
		radioButtonGroupFieldSet.add(new WLabel("Yes", rb1));
		radioButtonGroupFieldSet.add(new WText("\u00a0"));
		radioButtonGroupFieldSet.add(rb2);
		radioButtonGroupFieldSet.add(new WLabel("No", rb2));
		radioButtonGroupFieldSet.add(new WText("\u00a0"));
		radioButtonGroupFieldSet.add(rb3);
		radioButtonGroupFieldSet.add(new WLabel("Uncertain", rb3));
		fields.addField("Radio Button Group", radioButtonGroupFieldSet).getLabel().setHint(
				"required");
		radioButtonGroupFieldSet.setMandatory(true);
		radioButtonGroupFieldSet.add(rbgroup);

		// Radio button select
		WRadioButtonSelect wrbSelect = new WRadioButtonSelect(new String[]{"Yes", "No", "Maybe"});
		wrbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		wrbSelect.setMandatory(true);
		fields.addField("WRadioButtonSelect", wrbSelect).getLabel().setHint("required");

		// Drop down.
		WDropdown dropdown = new WDropdown(new String[]{null, "Cat", "Dog", "Elephant", "Mouse"});
		fields.addField("WDropdown", dropdown).getLabel().setHint("required");
		dropdown.setMandatory(true);

		// WMultiSelect
		WMultiSelect multi = new WMultiSelect(new String[]{"Circle", "Oval",
			"Rectangle", "Square",
			"Triangle"});

		multi.setMinSelect(2);
		multi.setMaxSelect(3);

		multi.setMandatory(true);
		fields.addField("WMultiSelect Min 2 Max 3", multi).getLabel().setHint(
				"must select two or three options");

		// multi select field.
		WField multiSelectPairField = fields.addField("WMultiSelectPair",
				new WMultiSelectPair(new String[]{"Circle", "Oval",
			"Rectangle", "Square",
			"Triangle"}));
		((WMultiSelectPair) multiSelectPairField.getField()).setMandatory(true);
		multiSelectPairField.getLabel().setHint("required");

		// wcheckbox group.
		WField checkboxSelectField = fields.addField("WCheckBoxSelect", new WCheckBoxSelect("sex"));
		((WCheckBoxSelect) checkboxSelectField.getField()).setMandatory(true);
		checkboxSelectField.getLabel().setHint("required");

		// create the buttons at the bottom.
		WPanel buttons = new WPanel(WPanel.Type.FEATURE);
		buttons.setMargin(new com.github.bordertech.wcomponents.Margin(12, 0, 0, 0));
		buttons.setLayout(new BorderLayout());
		add(buttons);

		printBtn = new WPrintButton();
		buttons.add(printBtn, BorderLayout.EAST);

		submitBtn = new WButton("Submit", 'S');
		buttons.add(submitBtn, BorderLayout.EAST);
		setDefaultSubmitButton(submitBtn);

		cancelBtn = new WCancelButton();
		buttons.add(cancelBtn, BorderLayout.WEST);

		// Same as cancel. Just testing a different component.
		resetBtn = new WConfirmationButton("Reset");
		resetBtn.setMessage("Are you sure you want to reset all fields?");
		buttons.add(resetBtn, BorderLayout.WEST);
	}

	/**
	 * Sets the cancel button's action.
	 *
	 * @param action the cancel action.
	 */
	public void setCancelAction(final Action action) {
		cancelBtn.setAction(action);
	}

	/**
	 * Sets the submit button's action.
	 *
	 * @param action the submit action.
	 */
	public void setSubmitAction(final Action action) {
		submitBtn.setAction(action);
	}

	/**
	 * Sets the reset button's action.
	 *
	 * @param action the reset action.
	 */
	public void setResetAction(final Action action) {
		resetBtn.setAction(action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final Request request) {
		// A dodgy example of using the unsaved changes flag on the
		// cancel button.
		String t1 = textField.getText();
		String t2 = textArea.getText();

		boolean hasChanges = (t1 != null && t1.length() > 0) || (t2 != null && t2.length() > 0);

		this.cancelBtn.setUnsavedChanges(hasChanges);
	}

	/**
	 * Override preparePaintComponent to perform initialisation the first time through.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			textArea.setFocussed();

			setInitialised(true);
		}
	}

	/**
	 * used to ensure that the fieldset holding the radio button group is highlighted rather than the individual
	 * buttons.
	 *
	 * @param diags the diagnosits list.
	 */
//    @Override
//    protected void validateComponent(final List<Diagnostic> diags)
//    {
//        super.validateComponent(diags);
//
//        if (rbgroup.getSelectedValue() == null)
//        {
//            diags.add(createErrorDiagnostic(radioButtonGroupFieldSet, "Please enter RadioButtonGroup"));
//        }
//    }
}
