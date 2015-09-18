package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WMessagesProxy - A proxy used by {@link WMessages} when a component does not have an ancestor that implements
 * MessageContainer when WMessages.getInstance(WComponent) is called.
 *
 * The proxy will search for a MessageContainer each time a method is called.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
class WMessagesProxy extends WMessages {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WMessagesProxy.class);

	/**
	 * The component which this proxy is for.
	 */
	private final WComponent component;

	/**
	 * A proxy for the WValidationErrors contained in WMessages.
	 */
	private final WValidationErrorsProxy validationErrorsProxy;

	/**
	 * Creates a proxy using the given component.
	 *
	 * @param component the component that will be used to search for a MessageContainer
	 */
	protected WMessagesProxy(final WComponent component) {
		this.component = component;
		validationErrorsProxy = new WValidationErrorsProxy();
	}

	/**
	 * Utility method that searches for the WMessages instance for the given component. If not found, a warning will be
	 * logged and null returned.
	 *
	 * @return the WMessages instance for the given component, or null if not found.
	 */
	private WMessages getWMessageInstance() {
		MessageContainer container = getMessageContainer(component);
		WMessages result = null;

		if (container == null) {
			LOG.warn("No MessageContainer as ancestor of " + component
					+ ". Messages will not be added");
		} else {
			result = container.getMessages();

			if (result == null) {
				LOG.warn("No messages in container of " + component
						+ ". Messages will not be added");
			}
		}

		return result;
	}

	/**
	 * Adds an error message.
	 *
	 * @param code the message code.
	 */
	@Override
	public void error(final String code) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.error(code);
		}
	}

	/**
	 * Adds an error message.
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 */
	@Override
	public void error(final String code, final String field) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.error(code, field);
		}
	}

	/**
	 * @return all error messages for this Messages instance.
	 */
	@Override
	public List<String> getErrorMessages() {
		WMessages instance = getWMessageInstance();

		if (instance == null) {
			return new ArrayList<>(0);
		} else {
			return instance.getErrorMessages();
		}
	}

	/**
	 * @return all informational messages for this Messages instance.
	 */
	@Override
	public List<String> getInfoMessages() {
		WMessages instance = getWMessageInstance();

		if (instance == null) {
			return new ArrayList<>(0);
		} else {
			return instance.getInfoMessages();
		}
	}

	/**
	 * @return all success messages for this Messages instance.
	 */
	@Override
	public List<String> getSuccessMessages() {
		WMessages instance = getWMessageInstance();

		if (instance == null) {
			return new ArrayList<>(0);
		} else {
			return instance.getSuccessMessages();
		}
	}

	/**
	 * @return the validation errors component, for use in e.g. ValidatingActions.
	 */
	@Override
	public WValidationErrors getValidationErrors() {
		return validationErrorsProxy;
	}

	/**
	 * @return all warning messages for this Messages instance.
	 */
	@Override
	public List<String> getWarningMessages() {
		WMessages instance = getWMessageInstance();

		if (instance == null) {
			return new ArrayList<>(0);
		} else {
			return instance.getWarningMessages();
		}
	}

	/**
	 * Adds an informational message.
	 *
	 * @param code the message code.
	 */
	@Override
	public void info(final String code) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.info(code);
		}
	}

	/**
	 * Adds an informational message.
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 */
	@Override
	public void info(final String code, final String field) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.info(code, field);
		}
	}

	/**
	 * Adds a success message.
	 *
	 * @param code the message code.
	 */
	@Override
	public void success(final String code) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.success(code);
		}
	}

	/**
	 * Adds a success message.
	 *
	 * @param code the message code.
	 * @param field the field for the success message.
	 */
	@Override
	public void success(final String code, final String field) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.success(code, field);
		}
	}

	/**
	 * Adds a warning message.
	 *
	 * @param code the message code.
	 */
	@Override
	public void warn(final String code) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.warn(code);
		}
	}

	/**
	 * Adds a warning message.
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 */
	@Override
	public void warn(final String code, final String field) {
		WMessages instance = getWMessageInstance();

		if (instance != null) {
			instance.warn(code, field);
		}
	}

	/**
	 * Provides a proxy for the WValidationErrors exposed by {@link WMessages#getValidationErrors()}. All methods in
	 * this proxy will delegate to the WValidationErrors instance for the given component, or will have no effect if
	 * there is no instance found.
	 *
	 * @author Yiannis Paschalidis
	 */
	private final class WValidationErrorsProxy extends WValidationErrors {

		/**
		 * @return the WValidationErrors instance for the given component, or null if not found.
		 */
		private WValidationErrors getWValidationErrors() {
			WMessages messages = getWMessageInstance();
			return messages == null ? null : messages.getValidationErrors();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clearErrors() {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				instance.clearErrors();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<Diagnostic> getErrors() {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				return instance.getErrors();
			}

			return new ArrayList<>(0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GroupedDiagnositcs> getGroupedErrors() {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				return instance.getGroupedErrors();
			}

			return new ArrayList<>(0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ValidationErrorsModel getOrCreateComponentModel() {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				// This ugly cast is required because WValidationErrors is in a different package
				return (ValidationErrorsModel) ((AbstractWComponent) instance).
						getOrCreateComponentModel();
			}

			return new ValidationErrorsModel();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ValidationErrorsModel getComponentModel() {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				// This ugly cast is required because WValidationErrors is in a different package
				return (ValidationErrorsModel) ((AbstractWComponent) instance).getComponentModel();
			}

			return new ValidationErrorsModel();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasErrors() {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				return instance.hasErrors();
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setErrors(final List<Diagnostic> errors) {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				instance.setErrors(errors);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setFocussed() {
			WValidationErrors instance = getWValidationErrors();

			if (instance != null) {
				instance.setFocussed();
			}
		}
	}
}
