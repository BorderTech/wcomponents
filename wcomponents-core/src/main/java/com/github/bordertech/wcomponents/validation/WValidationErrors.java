package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This component is used to render an error message box (commonly near the top of an application's UI) which contains
 * all supplied validation errors.
 * </p>
 * <p>
 * This provides (in conjuntion with {@link WFieldErrorIndicator} components) the ability to link error messages with
 * the input field that is in error.
 * </p>
 * <p>
 * NOTE: The collection of errors passed to this component must be implementations of {@link Diagnostic}.
 * </p>
 *
 * @see Diagnostic
 * @see com.github.bordertech.wcomponents.WMessages
 *
 * @author Adam Millard
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WValidationErrors extends AbstractWComponent {

	/**
	 * Clears the errors for a given user.
	 */
	public void clearErrors() {
		getOrCreateComponentModel().errors.clear();
	}

	/**
	 * Sets the errors for a given user.
	 *
	 * @param errors the errors to set.
	 */
	public void setErrors(final List<Diagnostic> errors) {
		if (errors != null) {
			ValidationErrorsModel model = getOrCreateComponentModel();

			for (Diagnostic error : errors) {
				if (error.getSeverity() == Diagnostic.ERROR) {
					model.errors.add(error);
				}
			}
		}
	}

	/**
	 * Retrieves the errors for a given user.
	 *
	 * @return the list of errors for the given user.
	 */
	public List<Diagnostic> getErrors() {
		return Collections.unmodifiableList(getComponentModel().errors);
	}

	/**
	 * Indicates whether there are any validation errors.
	 *
	 * @return true if there are validation errors, false otherwise.
	 */
	public boolean hasErrors() {
		ValidationErrorsModel model = getComponentModel();
		return !model.errors.isEmpty();
	}

	/**
	 * Groups the errors by their source field.
	 *
	 * @return a list of grouped errors.
	 */
	public List<GroupedDiagnositcs> getGroupedErrors() {
		List<GroupedDiagnositcs> grouped = new ArrayList<>();
		Diagnostic previousError = null;
		GroupedDiagnositcs group = null;

		for (Diagnostic theError : getErrors()) {
			boolean isNewField = ((previousError == null) || (previousError.getContext() != theError.
					getContext())
					|| (previousError.getComponent() != theError.getComponent()));

			if (group == null || isNewField) {
				group = new GroupedDiagnositcs();
				grouped.add(group);
			}

			group.addDiagnostic(theError);

			previousError = theError;
		}

		return Collections.unmodifiableList(grouped);
	}

	/**
	 * Groups a set of related diagnostics.
	 */
	public static class GroupedDiagnositcs {

		/**
		 * The grouped diagnostics.
		 */
		private final List<Diagnostic> diags = new ArrayList<>();

		/**
		 * Adds a diagnostic to this group.
		 *
		 * @param diag the diagnostic to add.
		 */
		public void addDiagnostic(final Diagnostic diag) {
			diags.add(diag);
		}

		/**
		 * @return the diagnostics contained in this group.
		 */
		public List<Diagnostic> getDiagnostics() {
			return Collections.unmodifiableList(diags);
		}
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + '(' + getErrors() + ')';
	}



	/**
	 * Sets the message box title.
	 *
	 * @param title the message box title to set, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setTitleText(final String title, final Serializable... args) {
		ValidationErrorsModel model = getOrCreateComponentModel();
		model.title = I18nUtilities.asMessage(title, args);
	}

	/**
	 * @return the message box title.
	 */
	public String getTitleText() {
		return I18nUtilities.format(null, getComponentModel().title);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Holds the extrinsic state information of a WValidationErrors.
	 */
	public static class ValidationErrorsModel extends ComponentModel {

		/**
		 * The validation errors.
		 */
		private final List<Diagnostic> errors = new ArrayList<>();

		/**
		 * The validation title text.
		 */
		private Serializable title;
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new ValidationErrorsModel.
	 */
	@Override
	protected ValidationErrorsModel newComponentModel() {
		return new ValidationErrorsModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected ValidationErrorsModel getComponentModel() {
		return (ValidationErrorsModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected ValidationErrorsModel getOrCreateComponentModel() {
		return (ValidationErrorsModel) super.getOrCreateComponentModel();
	}
}
