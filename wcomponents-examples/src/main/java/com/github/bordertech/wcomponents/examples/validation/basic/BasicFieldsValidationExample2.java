package com.github.bordertech.wcomponents.examples.validation.basic;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.validation.ValidationContainer;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.util.List;

/**
 * Same as BasicFieldsValidationExample except that the ValidatingAction has been extended to add an extra validation.
 *
 * @author Martin Shevchenko
 * @since 18/3/2008
 */
public class BasicFieldsValidationExample2 extends ValidationContainer {

	/**
	 * Creates a BasicFieldsValidationExample2.
	 */
	private final BasicFields fields;

	/**
	 * Construct example.
	 */
	public BasicFieldsValidationExample2() {
		super(new BasicFields());
		fields = (BasicFields) getComponentToValidate();

		fields.getField2().getLabel().setHint(
				fields.getField2().getLabel().getHint() + " and do not enter 'qwerty'");

		// Get access to the BasicFields object created above.
		final BasicFields basicFields = (BasicFields) getComponentToValidate();

		// Customise the validating action to include one additional test for Field 2.
		setValidatingAction(new ValidatingAction(getErrorsBox(), basicFields) {
			@Override
			public void validate(final List<Diagnostic> diags) {
				super.validate(diags);

				String text2 = basicFields.getField2().getText();

				if ("qwerty".equals(text2)) {
					// Note that this error will hyperlink to Field 2.
					diags.add(createErrorDiagnostic(basicFields.getField2(),
							"Field 2 must not contain the text \"qwerty\"."));
				}
			}

			@Override
			public void executeOnValid(final ActionEvent event) {
				showSuccessDialog();
			}
		});

		// Make the user aware of the additional validation.
		WText overview = basicFields.getOverview();
		overview.setText(overview.getText()
				+ "\n<ul>"
				+ " <li>Field 2 must not contain the text \"qwerty\".</li>"
				+ "</ul>");
	}
}
