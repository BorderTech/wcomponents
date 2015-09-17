package com.github.bordertech.wcomponents.examples.validation.basic;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.validator.DateFieldPivotValidator;
import com.github.bordertech.wcomponents.validator.FieldValidator;
import com.github.bordertech.wcomponents.validator.RegExFieldValidator;
import java.util.List;

/**
 * This component contains three simple fields and a couple of field validations.
 *
 * @author Adam Millard
 */
public class BasicFields extends WContainer {

	private final WTextField field1;
	private final WTextField field2;
	private final WDateField dateField;
	private final WTextField field3;

	private final WText overview;

	/**
	 * Creates a BasicFields example.
	 */
	public BasicFields() {
		overview = new WText(
				"This list describes the validation rules added to this component and its fields.<br />"
				+ "<ul>"
				+ " <li>Field 1 is mandatory, has a minimum length of 2 and a maximum length of 5.</li>"
				+ " <li>Field 2 has a minimum length of 2 and must only contain alphabetic characters.</li>"
				+ " <li>Fields 1 and 2 cannot contain the same value. (This is a cross field example)</li>"
				+ " <li>The total length of Field 1 plus Field 2 can exceed 20 characters. (This is another cross field example)</li>"
				+ " <li>Field 3 has a warning message if it is blank.</li>"
				+ " <li>Date Field must be a valid date and must be after today (inclusive).</li>"
				+ "</ul>");

		overview.setEncodeText(false);
		add(overview);

		WFieldLayout layout = new WFieldLayout();

		// FIELD 1
		field1 = new WTextField();
		field1.setMandatory(true);
		field1.setMinLength(2);
		field1.setMaxLength(5);

		// FIELD 2
		field2 = new WTextField();
		field2.setMinLength(2);
		FieldValidator field2RegEx = new RegExFieldValidator("^[a-zA-Z]*$");
		field2RegEx.setErrorMessage("{0} must only contain alphabetic characters.");
		field2.addValidator(field2RegEx);

		WButton field2ToggleMandatoryBtn = new WButton("Toggle Field 2 Mandatory");
		field2ToggleMandatoryBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				field2.setMandatory(!field2.isMandatory());
				field2.getLabel().setHint(
						"mandatory and must be at least two characters and may only contain alphabetic characters and must be different from field 1");
			}
		});

		// FIELD 3
		field3 = new WTextField();

		// DATE FIELD
		dateField = new WDateField();
		dateField.addValidator(new DateFieldPivotValidator(DateFieldPivotValidator.AFTER_OR_EQUAL));

		WField field = layout.addField("Field 1", field1);
		field.getLabel().setHint("must contain between 2 and 5 characters");

		field = layout.addField("Field 2", field2);
		field.getLabel().setHint(
				"may only contain alphabetic characters, should be a minimum of two characters and must be different from field 1");
		layout.addField((WLabel) null, field2ToggleMandatoryBtn);
		field = layout.addField("Field 3", field3);
		field.getLabel().setHint("preferably not blank");
		field = layout.addField("Date Field", dateField);
		field.getLabel().setHint("must be a valid date on or after today");
		add(layout);
	}

	/**
	 * An example of cross field validation.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		String text1 = field1.getText();
		String text2 = field2.getText();
		String text3 = field3.getText();

		if (text1 != null && text1.length() > 0 && text1.equals(text2)) {
			// Note that this error will hyperlink to Field 2.
			diags.add(createErrorDiagnostic(field2, "Fields 1 and 2 cannot be the same."));
		}

		int len = 0;
		if (text1 != null) {
			len += text1.length();
		}
		if (text2 != null) {
			len += text2.length();
		}
		if (len > 20) {
			// Note that this error does not link to a specific field.
			diags.add(createErrorDiagnostic(
					"The total length of Field 1 plus Field 2 can exceed 20 characters."));
		}

		// Sample Warning Message
		if (Util.empty(text3)) {
			diags.add(new DiagnosticImpl(Diagnostic.WARNING, UIContextHolder.getCurrent(), field3,
					"Warning that this should not be blank"));
		}
	}

	/**
	 * @return the date field
	 */
	public WDateField getDateField() {
		return dateField;
	}

	/**
	 * @return the text field 1
	 */
	public WTextField getField1() {
		return field1;
	}

	/**
	 * @return the text field 2
	 */
	public WTextField getField2() {
		return field2;
	}

	/**
	 * @return the overview
	 */
	public WText getOverview() {
		return overview;
	}
}
