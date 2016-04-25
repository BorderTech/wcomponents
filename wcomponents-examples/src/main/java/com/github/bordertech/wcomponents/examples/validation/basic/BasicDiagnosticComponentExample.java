package com.github.bordertech.wcomponents.examples.validation.basic;

import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.validation.ValidationContainer;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.util.List;

/**
 * <p>
 * An example of the {@link DiagnosticImpl} class. This type of diagnostic provides a {@link WComponent} that represents
 * the message. The {@link WValidationErrors} component renderes the {@link WComponent} in place of the description (old
 * behaviour).</p>
 *
 * <p>
 * The example diagnostic in this example ({@link LinkDiagnosticImpl}) creates an error message that contains a link
 * that clears the input feilds of the form.</p>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class BasicDiagnosticComponentExample extends ValidationContainer {

	/**
	 * Creates a BasicDiagnosticComponentExample.
	 */
	public BasicDiagnosticComponentExample() {
		super(new WDiagnosticComponent());
	}

	/**
	 * The component that will be validated by this example.
	 *
	 * @author Christina Harris
	 */
	public static class WDiagnosticComponent extends WContainer {

		private final WTextField inputText1 = new WTextField();
		private final WTextField inputText2 = new WTextField();

		/**
		 * Creates a WDiagnosticComponent.
		 */
		public WDiagnosticComponent() {
			WFieldLayout layout = new WFieldLayout();
			layout.setLabelWidth(30);
			layout.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
			//mandatory fields
			inputText1.setMandatory(true);
			layout.addField("Unique Input 1", inputText1).getLabel().setHint("required");
			inputText2.setMandatory(true);
			layout.addField("Unique Input 2", inputText2).getLabel().setHint(
					"required and must be different from unique input 1");
			add(layout);
		}

		/**
		 * Validates this component. If the text inputs are the same then validation fails.
		 *
		 * @param diags the list of Diagnostics to add validation errors to.
		 */
		@Override
		protected void validateComponent(final List<Diagnostic> diags) {
			String text1 = inputText1.getText();
			String text2 = inputText2.getText();

			if (text1 != null && text1.length() > 0 && text1.equals(text2)) {
				// The inputs are the same so create an error.
				LinkDiagnosticImpl message = new LinkDiagnosticImpl(inputText2,
						"Inputs 1 and 2 cannot be the same.",
						Diagnostic.ERROR);

				diags.add(message);
			}
		}
	}

	/**
	 * A diagnostic error message component that contains a description of the error and a link. Use
	 * setLinkAction(Action) to set the action for the link to perform.
	 *
	 * @author Christina Harris
	 */
	private static final class LinkDiagnosticImpl extends DiagnosticImpl {

		/**
		 * Creates a LinkDiagnosticImpl.
		 *
		 * @param sourceField the field that is the source of the diagnostic, or null if there is no appropriate field
		 * @param description the description (message) of the diagnostic
		 * @param severity one of Diagnostic.INFO, Diagnostic.WARNING or Diagnostic.ERROR
		 */
		private LinkDiagnosticImpl(final WComponent sourceField,
				final String description, final int severity) {
			super(severity, UIContextHolder.getCurrent(), sourceField, description);
		}

	}
}
