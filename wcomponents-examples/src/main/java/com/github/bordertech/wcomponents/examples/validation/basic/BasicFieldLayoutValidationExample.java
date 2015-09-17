package com.github.bordertech.wcomponents.examples.validation.basic;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.validation.ValidationContainer;
import com.github.bordertech.wcomponents.validator.DateFieldPivotValidator;
import com.github.bordertech.wcomponents.validator.RegExFieldValidator;

/**
 * This validation example uses the WFieldLayout class to simplify layout and validation.
 *
 * @author Martin Shevchenko
 */
public class BasicFieldLayoutValidationExample extends ValidationContainer {

	/**
	 * Creates a BasicFieldLayoutValidationExample.
	 */
	public BasicFieldLayoutValidationExample() {
		super(new BasicFieldsUsingFieldLayout());
	}

	/**
	 * The component that will be validated by this example.
	 *
	 * @author Martin Shevchenko
	 */
	public static class BasicFieldsUsingFieldLayout extends WContainer {

		/**
		 * Creates a BasicFieldsUsingFieldLayout.
		 */
		public BasicFieldsUsingFieldLayout() {
			WText overview = new WText(
					"<p>This list describes the validation rules added to this component and its fields.</p>"
					+ "<ul>"
					+ " <li>Field 1 is mandatory, has a minimum length of 2 and a maximum length of 5.</li>"
					+ " <li>Field 2 has a minimum length of 2 and must only contain alphabetic characters.</li>"
					+ " <li>Date Field 1 must be a valid date (lenient) and must be after today.</li>"
					+ " <li>Date Field 2 must be a valid date (lenient) and must be after or equal to Date Field 1.</li>"
					+ "</ul>");
			overview.setEncodeText(false);
			add(overview);

			WFieldLayout layout = new WFieldLayout();
			add(layout);

			WTextField textField1 = new WTextField();
			textField1.setMinLength(2);
			textField1.setMaxLength(5);

			layout.addField("Field 1", textField1).getLabel().setHint("required");
			textField1.setMandatory(true);

			WTextField textField2 = new WTextField();
			textField2.setMinLength(2);

			WField field2 = layout.addField("Field 2", textField2);
			field2.addValidator(new RegExFieldValidator("^[a-zA-Z]*$",
					"{0} must only contain alphabetic characters."));
			field2.getLabel().setHint("may only contain alphabetic characters");

			WDateField df1 = new WDateField(true);
			WField dateField = layout.addField("Date Field 1", df1);
			dateField.addValidator(new DateFieldPivotValidator(DateFieldPivotValidator.AFTER));
			dateField.getLabel().setHint("after today");

			WField dateField2 = layout.addField("Date Field 2", new WDateField(true));
			dateField2.addValidator(new DateFieldPivotValidator(
					DateFieldPivotValidator.AFTER_OR_EQUAL, df1));
			dateField2.getLabel().setHint("on or after the date entered in date field 1");

			WTextField textField = new WTextField();
			textField.setMandatory(true);
			layout.addField("Mandatory flag set to input field instead", textField).getLabel().
					setHint("required");
		}
	}
}
