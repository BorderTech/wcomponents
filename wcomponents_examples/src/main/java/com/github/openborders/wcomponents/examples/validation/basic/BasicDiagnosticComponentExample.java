package com.github.openborders.wcomponents.examples.validation.basic;

import java.util.List;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.UIContextHolder;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WTextField;
import com.github.openborders.wcomponents.validation.Diagnostic;
import com.github.openborders.wcomponents.validation.DiagnosticImpl;
import com.github.openborders.wcomponents.validation.WValidationErrors;

import com.github.openborders.wcomponents.examples.validation.ValidationContainer;

/**
 * <p>An example of the {@link WDiagnosticImpl} class. This type of diagnostic
 * provides a {@link WComponent} that represents the message. The
 * {@link WValidationErrors} component renderes the {@link WComponent} in place
 * of the description (old behaviour).</p>
 *
 * <p>The example diagnostic in this example ({@link LinkDiagnosticImpl}) creates
 * an error message that contains a link that clears the input feilds of the
 * form.</p>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class BasicDiagnosticComponentExample extends ValidationContainer
{
    /** Creates a BasicDiagnosticComponentExample. */
    public BasicDiagnosticComponentExample()
    {
        super(new WDiagnosticComponent());
    }

    /**
     * The component that will be validated by this example.
     * @author Christina Harris
     */
    public static class WDiagnosticComponent extends WContainer
    {
        private final WTextField inputText1 = new WTextField();
        private final WTextField inputText2 = new WTextField();

        /** Creates a WDiagnosticComponent. */
        public WDiagnosticComponent()
        {
            WFieldLayout layout = new WFieldLayout();
            layout.setLabelWidth(30);
            layout.setMargin(new com.github.openborders.wcomponents.Margin(0,0,12,0));
            //mandatory fields
            inputText1.setMandatory(true);
            layout.addField("Unique Input 1", inputText1).getLabel().setHint("required");
            inputText2.setMandatory(true);
            layout.addField("Unique Input 2", inputText2).getLabel().setHint("required and must be different from unique input 1");
            add(layout);
        }

		/**
		 * Validates this component. If the text inputs are the same then validation fails.
         *
         * @param diags the list of Diagnostics to add validation errors to.
		 */
        @Override
        protected void validateComponent(final List<Diagnostic> diags)
        {
            String text1 = inputText1.getText();
            String text2 = inputText2.getText();

            if (text1 != null && text1.length() > 0 && text1.equals(text2))
            {
                // The inputs are the same so create an error.
                LinkDiagnosticImpl message = new LinkDiagnosticImpl(inputText2, null,
                                                                    "Inputs 1 and 2 cannot be the same.",
                                                                    Diagnostic.ERROR);

                diags.add(message);
            }
        }
    }

	/**
	 * A diagnostic error message component that contains a description
	 * of the error and a link. Use {@link #setLinkAction(Action)} to set the
	 * action for the link to perfrom.
     *
     * @author Christina Harris
	 */
	private static class LinkDiagnosticImpl extends DiagnosticImpl
    {

        /**
         * Creates a LinkDiagnosticImpl.
         *
         * @param sourceField
         *            the field that is the source of the diagnostic,
         *            or null if there is no appropriate field
         * @param code
         *            the code of the diagnostic
         * @param description
         *            the description (message) of the diagnostic
         * @param severity
         *            one of Diagnostic.INFO, Diagnostic.WARNING or
         *            Diagnostic.ERROR
         */
        public LinkDiagnosticImpl(final WComponent sourceField,
                                  final String code, final String description, final int severity)
        {
            super(severity, UIContextHolder.getCurrent(), sourceField, description);
        }


    }
}
