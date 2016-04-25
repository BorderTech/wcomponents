package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.EmptyIterator;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validator.FieldValidator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Superclass for components that correspond to an input field.
 *
 * @author James Gifford, Martin Shevchenko, Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractInput extends WBeanComponent implements Input {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractInput.class);

	// ================================
	// Validation
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addValidator(final FieldValidator validator) {
		InputModel model = getOrCreateComponentModel();

		if (model.validators == null) {
			model.validators = new ArrayList<>();
		}

		validator.setInputField(this);
		model.validators.add(validator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<FieldValidator> getValidators() {
		List<FieldValidator> validators = getComponentModel().validators;

		if (validators != null) {
			return validators.iterator();
		}

		return new EmptyIterator<>();
	}

	/**
	 * Override WComponent's validatorComponent in order to use the validators which have been added to this input
	 * field. Subclasses may still override this method, but it is suggested to call super.validateComponent to ensure
	 * that the validators are still used.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		// Mandatory validation
		if (isMandatory() && isEmpty()) {
			diags.add(createMandatoryDiagnostic());
		}

		// Other validations
		List<FieldValidator> validators = getComponentModel().validators;

		if (validators != null) {
			for (FieldValidator validator : validators) {
				validator.validate(diags);
			}
		}
	}

	/**
	 * <p>
	 * This method is called by validateComponent to create the mandatory diagnostic error message if the mandatory
	 * validation check does not pass.
	 * </p>
	 * <p>
	 * Subclasses may override this method to customise the message, however in most cases it is easier to supply a
	 * custom error message pattern to the setMandatory method.
	 * </p>
	 *
	 * @return a new diagnostic for when mandatory validation fails.
	 */
	protected Diagnostic createMandatoryDiagnostic() {
		String errorMessage = getComponentModel().errorMessage;
		String msg = errorMessage == null ? InternalMessages.DEFAULT_VALIDATION_ERROR_MANDATORY : errorMessage;
		return createErrorDiagnostic(msg, this);
	}

	// ================================
	// Action on change
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActionOnChange(final Action actionOnChange) {
		getOrCreateComponentModel().actionOnChange = actionOnChange;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Action getActionOnChange() {
		return getComponentModel().actionOnChange;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getActionObject() {
		InputModel model = getComponentModel();
		return model.actionObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActionObject(final Object data) {
		InputModel model = getOrCreateComponentModel();
		model.actionObject = data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getActionCommand() {
		return getValueAsString();
	}

	// ================================
	// Default submit button
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefaultSubmitButton(final WButton defaultSubmitButton) {
		getOrCreateComponentModel().defaultSubmitButton = defaultSubmitButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WButton getDefaultSubmitButton() {
		return getComponentModel().defaultSubmitButton;
	}

	// ================================
	// Mandatory flag
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMandatory(final boolean mandatory, final String message) {
		setFlag(ComponentModel.MANDATORY_FLAG, mandatory);

		// Developer wants to customise the MandatoryFieldValidator's message.
		getOrCreateComponentModel().errorMessage = message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMandatory(final boolean mandatory) {
		setFlag(ComponentModel.MANDATORY_FLAG, mandatory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMandatory() {
		return isFlagSet(ComponentModel.MANDATORY_FLAG);
	}

	// ================================
	// ReadOnly Flag
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnly() {
		return isFlagSet(ComponentModel.READONLY_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(final boolean readOnly) {
		setFlag(ComponentModel.READONLY_FLAG, readOnly);
	}

	// ================================
	// Disabled Flag
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	// ================================
	// Handle Request
	/**
	 * Override handleRequest in order to perform consistent processing for input components.
	 * <p>
	 * To protect against client-side tampering of the request, disabled/readonly input fields will not have their
	 * handle request processing performed.
	 * </p>
	 * <p>
	 * This method will call {@link #doHandleRequest(Request)} for input components to process the request. If the input
	 * component has changed, then this method will call {@link #doHandleChanged()} and set the
	 * {@link #isChangedInLastRequest()} flag.
	 * </p>
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public final void handleRequest(final Request request) {
		if (!beforeHandleRequest(request)) {
			return;
		}

		setChangedInLastRequest(false);

		if (isDisabled() || isReadOnly()) {
			// Protect against client-side tampering of disabled/read-only fields.
			return;
		}

		if (isPresent(request)) {

			// Only process on a POST
			if (!"POST".equals(request.getMethod())) {
				LOG.warn("Input component on a request that is not a POST. Will be ignored.");
				return;
			}

			boolean changed = doHandleRequest(request);

			if (changed) {
				setChangedInLastRequest(true);
				doHandleChanged();
			}
		}
	}

	/**
	 * Handle before handle request processing.
	 *
	 * @param request the request being responded to.
	 * @return true to continue
	 */
	protected boolean beforeHandleRequest(final Request request) {
		return true;
	}

	/**
	 * Specific handle request processing for an input component is provided here.
	 * <p>
	 * Input components are required to determine if the component has changed in the request, set the component data to
	 * the new value (if changed) and return the changed flag.
	 * </p>
	 *
	 * @param request the request being responded to.
	 * @return true if the input component has changed, otherwise return false
	 */
	protected abstract boolean doHandleRequest(final Request request);

	/**
	 * Perform change logic for this component.
	 */
	protected void doHandleChanged() {
		// If there is an associated action, execute it
		if (getActionOnChange() != null) {
			final ActionEvent event = new ActionEvent(this, getActionCommand(), getActionObject());
			Runnable later = new Runnable() {
				@Override
				public void run() {
					getActionOnChange().execute(event);

					if (isSubmitOnChange() && UIContextHolder.getCurrent().getFocussed() == null) {
						setFocussed();
					}
				}
			};

			invokeLater(later);
		} else if (isSubmitOnChange() && UIContextHolder.getCurrent().getFocussed() == null) {
			setFocussed();
		}
	}

	/**
	 * Determine if this component is on the Request.
	 *
	 * @param request the request being responded to.
	 * @return true if this component is on the Request, otherwise return false.
	 */
	protected boolean isPresent(final Request request) {
		return request.getParameter(getId()) != null;
	}

	// ================================
	// Input Value
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue() {
		return getData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValueAsString() {
		Object data = getValue();
		return data == null ? null : data.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		String value = getValueAsString();
		return value == null || value.length() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isChangedInLastRequest() {
		return getComponentModel().changedInLastRequest;
	}

	/**
	 * Set the changed flag to indicate if the component changed in the last request.
	 *
	 * @param changed true if the value changed in the request
	 */
	protected void setChangedInLastRequest(final boolean changed) {
		if (isChangedInLastRequest() != changed) {
			InputModel model = getOrCreateComponentModel();
			model.changedInLastRequest = changed;
		}
	}

	// ================================
	// Submit On Change
	/**
	 * Setting this flag to true will cause this component to post the form to the server when it changes.
	 *
	 * @param flag if true, the form is submitted when the component changes.
	 */
	void setSubmitOnChange(final boolean flag) {
		setFlag(ComponentModel.SUBMIT_ON_CHANGE_FLAG, flag);
	}

	/**
	 * Indicates whether the form should submit to server when the component's value changes.
	 *
	 * @return true if the form is submitted when the value changes.
	 */
	boolean isSubmitOnChange() {
		return isFlagSet(ComponentModel.SUBMIT_ON_CHANGE_FLAG);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getValueAsString();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected InputModel newComponentModel() {
		return new InputModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected InputModel getComponentModel() {
		return (InputModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected InputModel getOrCreateComponentModel() {
		return (InputModel) super.getOrCreateComponentModel();
	}

	/**
	 * InputModel holds Extrinsic state management of the field.
	 */
	public static class InputModel extends BeanAndProviderBoundComponentModel {

		/**
		 * A flag if the input changed in the last request.
		 */
		private boolean changedInLastRequest;

		/**
		 * The action object to use on the change action event.
		 */
		private Object actionObject;

		/**
		 * The list of validators which have been added to this input.
		 */
		private List<FieldValidator> validators;

		/**
		 * The error message to display when the input fails the mandatory validation check.
		 */
		private String errorMessage;

		/**
		 * The submit button associated with this input field.
		 */
		private WButton defaultSubmitButton;

		/**
		 * The action that will be executed when the input field's value is changed by the user.
		 */
		private Action actionOnChange;

	}
}
