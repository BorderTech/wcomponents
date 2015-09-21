package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class calls a components validation logic prior to executing its underlying action logic. If there are no
 * validation errors then the <code>executeOnValid()</code> method is called. This allows for the validation logic to be
 * seperated from the action logic.
 * <p>
 * Users should provide a subclass of this as the action to perform on any <code>WButton</code>s that should trigger
 * validation. The code to run when validation has passed is to be provided in the <code>executeOnValid()</code> method.
 * </p>
 *
 * @author Adam Millard
 */
public abstract class ValidatingAction implements Action {

	/**
	 * The validation errors box which will display the errors.
	 */
	private WValidationErrors errorsBox;

	/**
	 * The component to be validated for this action.
	 */
	private WComponent componentToValidate;

	/**
	 * Creates a ValidatingAction. Note that any visible components within the componentToValidate will also be
	 * validated.
	 *
	 * @param errorsBox the validation errors box which will display any validation errors.
	 * @param componentToValidate the component to be validated for this action.
	 */
	public ValidatingAction(final WValidationErrors errorsBox, final WComponent componentToValidate) {
		setErrorsBox(errorsBox);
		setComponentToValidate(componentToValidate);
	}

	/**
	 * Sets the errors box.
	 *
	 * @param errorsBox the validation errors box which will display any validation errors.
	 */
	public void setErrorsBox(final WValidationErrors errorsBox) {
		this.errorsBox = errorsBox;
	}

	/**
	 * Sets the component to validate. Note that any visible components within the component will also be validated.
	 *
	 * @param componentToValidate the component to be validated for this action.
	 */
	public void setComponentToValidate(final WComponent componentToValidate) {
		this.componentToValidate = componentToValidate;
	}

	/**
	 * @return the component to be validated for this action.
	 */
	public WComponent getComponentToValidate() {
		return componentToValidate;
	}

	/**
	 * This method can be overridden to perform additional validations, but don't forget to call super.validate if you
	 * still want to perform default validation.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	public void validate(final List<Diagnostic> diags) {
		componentToValidate.validate(diags);
	}

	/**
	 * This method is called if no errors are found from validation.
	 *
	 * @param event the action event which triggered this action.
	 */
	public abstract void executeOnValid(final ActionEvent event);

	/**
	 * <p>
	 * This method is called if errors are found from validation.</p>
	 *
	 * <p>
	 * This method can be overriden, but don't forget to call super.executeOnError if you still want to see error
	 * messages.</p>
	 *
	 * @param event the action event which triggered this action.
	 * @param diags the list into which any validation diagnostics were added.
	 */
	public void executeOnError(final ActionEvent event,
			final List<Diagnostic> diags) {
		errorsBox.setErrors(diags);
		errorsBox.setFocussed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void execute(final ActionEvent event) {
		errorsBox.clearErrors();

		List<Diagnostic> diags = new ArrayList<>();

		validate(diags);

		componentToValidate.showWarningIndicators(diags);
		componentToValidate.showErrorIndicators(diags);

		if (containsError(diags)) {
			executeOnError(event, diags);
		} else {
			executeOnValid(event);
		}
	}

	/**
	 * Indicates whether the given list of diagnostics contains any errors.
	 *
	 * @param diags the list into which any validation diagnostics were added.
	 * @return true if any of the diagnostics in the list are errors, false otherwise.
	 */
	private static boolean containsError(final List<Diagnostic> diags) {
		if (diags == null || diags.isEmpty()) {
			return false;
		}

		for (Diagnostic diag : diags) {
			if (diag.getSeverity() == Diagnostic.ERROR) {
				return true;
			}
		}

		return false;
	}
}
