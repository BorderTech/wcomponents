package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * WMessages provides a convenient wrapper for the WMessageBox and WValidationErrors components, and lets the developer
 * use a "singleton" WMessages instance per application.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMessages extends WPanel {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WMessages.class);

	/**
	 * The message box used to display "success" messages.
	 */
	private final WMessageBox successMessages = new WMessageBox(WMessageBox.SUCCESS);

	/**
	 * The message box used to display "information" messages.
	 */
	private final WMessageBox infoMessages = new WMessageBox(WMessageBox.INFO);

	/**
	 * The message box used to display "warning" messages.
	 */
	private final WMessageBox warningMessages = new WMessageBox(WMessageBox.WARN);

	/**
	 * The message box used to display "error" messages.
	 */
	private final WMessageBox errorMessages = new WMessageBox(WMessageBox.ERROR);

	/**
	 * The message box used to display validation errors.
	 */
	private final WValidationErrors validationErrors = new WValidationErrors();

	/**
	 * Creates a new WMessages, where only validation messages are persisted between requests.
	 */
	public WMessages() {
		this(false);
	}

	/**
	 * Creates a new WMessages.
	 *
	 * @param persistent if true, all messages are persisted between requests. If false, only validation messages are
	 * persisted between requests.
	 */
	public WMessages(final boolean persistent) {
		add(validationErrors);

		if (!persistent) {
			// We don't want the message display components to retain any state information
			MutableContainer container = new DefaultTransientDataContainer();
			add(container);

			container.add(errorMessages);
			container.add(warningMessages);
			container.add(infoMessages);
			container.add(successMessages);
		} else {
			add(errorMessages);
			add(warningMessages);
			add(infoMessages);
			add(successMessages);
		}
	}

	/**
	 * Retrieves the WMessages instance for the given component. The component tree is searched for an ancestor that
	 * implements the MessageContainer interface. If not found, a proxy is returned that searches for the
	 * MessageContainer each time a WMessages method is called. This allows developers to obtain a valid "instance"
	 * during e.g. constructors, where the component will not have been added to the tree yet.
	 *
	 * @param component the component to retrieve the WMessages instance for
	 * @return the WMessages instance for the given component.
	 */
	public static WMessages getInstance(final WComponent component) {
		MessageContainer container = getMessageContainer(component);
		WMessages messages = container != null ? container.getMessages() : null;

		if (messages == null) {
			messages = new WMessagesProxy(component);
		}

		return messages;
	}

	/**
	 * @return true if there are messages to display
	 */
	public boolean hasMessages() {
		return successMessages.hasMessages() || infoMessages.hasMessages() || warningMessages.
				hasMessages()
				|| errorMessages.hasMessages() || validationErrors.hasErrors();
	}

	/**
	 * Adds a message.
	 *
	 * @param message the message to add
	 */
	public void addMessage(final Message message) {
		addMessage(message, true);
	}

	/**
	 * Adds a message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param message the message to add
	 * @param encodeText true to encode the message, false to leave it unencoded.
	 */
	public void addMessage(final Message message, final boolean encodeText) {
		switch (message.getType()) {
			case Message.SUCCESS_MESSAGE:
				addMessage(successMessages, message, encodeText);
				break;

			case Message.INFO_MESSAGE:
				addMessage(infoMessages, message, encodeText);
				break;

			case Message.WARNING_MESSAGE:
				addMessage(warningMessages, message, encodeText);
				break;

			case Message.ERROR_MESSAGE:
				addMessage(errorMessages, message, encodeText);
				break;

			default:
				LOG.warn("Unknown message type: " + message.getType());
		}
	}

	/**
	 * Adds a success message.
	 *
	 * @param code the message code
	 */
	public void success(final String code) {
		success(code, true);
	}

	/**
	 * Adds a success message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void success(final String code, final boolean encodeText) {
		Message message = new Message(Message.SUCCESS_MESSAGE, code);
		addMessage(successMessages, message, encodeText);
	}

	/**
	 * Adds a success message.
	 *
	 * @param code the message code.
	 * @param field the field for the success message.
	 */
	public void success(final String code, final String field) {
		success(code, field, true);
	}

	/**
	 * Adds a success message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code.
	 * @param field the field for the success message.
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void success(final String code, final String field, final boolean encodeText) {
		Message message = new Message(Message.SUCCESS_MESSAGE, code, field);
		addMessage(successMessages, message, encodeText);
	}

	/**
	 * Adds an informational message.
	 *
	 * @param code the message code.
	 */
	public void info(final String code) {
		info(code, true);
	}

	/**
	 * Adds an informational message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code.
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void info(final String code, final boolean encodeText) {
		Message message = new Message(Message.INFO_MESSAGE, code);
		addMessage(infoMessages, message, encodeText);
	}

	/**
	 * Adds an informational message.
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 */
	public void info(final String code, final String field) {
		info(code, field, true);
	}

	/**
	 * Adds an informational message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void info(final String code, final String field, final boolean encodeText) {
		Message message = new Message(Message.INFO_MESSAGE, code, field);
		addMessage(infoMessages, message, encodeText);
	}

	/**
	 * Adds a warning message.
	 *
	 * @param code the message code.
	 */
	public void warn(final String code) {
		warn(code, true);
	}

	/**
	 * Adds a warning message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code.
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void warn(final String code, final boolean encodeText) {
		Message message = new Message(Message.WARNING_MESSAGE, code);
		addMessage(warningMessages, message, encodeText);
	}

	/**
	 * Adds a warning message.
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 */
	public void warn(final String code, final String field) {
		warn(code, field, true);
	}

	/**
	 * Adds a warning message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void warn(final String code, final String field, final boolean encodeText) {
		Message message = new Message(Message.WARNING_MESSAGE, code, field);
		addMessage(warningMessages, message, encodeText);
	}

	/**
	 * Adds an error message.
	 *
	 * @param code the message code.
	 */
	public void error(final String code) {
		error(code, true);
	}

	/**
	 * Adds an error message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code.
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void error(final String code, final boolean encodeText) {
		Message message = new Message(Message.ERROR_MESSAGE, code);
		addMessage(errorMessages, message, encodeText);
	}

	/**
	 * Adds an error message.
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 */
	public void error(final String code, final String field) {
		error(code, field, true);
	}

	/**
	 * Adds an error message.
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.
	 * </p>
	 * <p>
	 * <b>WARNING:</b> If you are using WMessageBox to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.
	 * </p>
	 *
	 * @param code the message code.
	 * @param field the field for the information message.
	 * @param encodeText true to encode the message, false otherwise.
	 */
	public void error(final String code, final String field, final boolean encodeText) {
		Message message = new Message(Message.ERROR_MESSAGE, code, field);
		addMessage(errorMessages, message, encodeText);
	}

	/**
	 * Convenience method to add a message to a message box.
	 *
	 * @param box the message box to add the message to
	 * @param message the message to add
	 * @param encode true to encode the message, false otherwise.
	 */
	private void addMessage(final WMessageBox box, final Message message, final boolean encode) {
		String code = message.getMessage();

		if (!Util.empty(code)) {
			box.addMessage(encode, code, message.getArgs());
		}
	}

	/**
	 * @return all informational messages for this Messages instance.
	 */
	public List<String> getInfoMessages() {
		return getMessages(Message.INFO_MESSAGE);
	}

	/**
	 * @return all warning messages for this Messages instance.
	 */
	public List<String> getWarningMessages() {
		return getMessages(Message.WARNING_MESSAGE);
	}

	/**
	 * @return all error messages for this Messages instance.
	 */
	public List<String> getErrorMessages() {
		return getMessages(Message.ERROR_MESSAGE);
	}

	/**
	 * @return all success messages for this Messages instance.
	 */
	public List<String> getSuccessMessages() {
		return getMessages(Message.SUCCESS_MESSAGE);
	}

	/**
	 * @param type the message type (@see Message)
	 * @return all messages of the given type for this Messages instance.
	 */
	public List<String> getMessages(final int type) {
		switch (type) {
			case Message.SUCCESS_MESSAGE:
				return successMessages.getMessages();
			case Message.INFO_MESSAGE:
				return infoMessages.getMessages();
			case Message.WARNING_MESSAGE:
				return warningMessages.getMessages();
			case Message.ERROR_MESSAGE:
				return errorMessages.getMessages();
			default:
				return null;
		}
	}

	/**
	 * @return the validation errors component, for use in e.g. ValidatingActions.
	 */
	public WValidationErrors getValidationErrors() {
		return validationErrors;
	}

	/**
	 * @return the success message box.
	 */
	public WMessageBox getSuccessMessageBox() {
		return successMessages;
	}

	/**
	 * @return the warning message box.
	 */
	public WMessageBox getWarningMessageBox() {
		return warningMessages;
	}

	/**
	 * @return the error message box.
	 */
	public WMessageBox getErrorMessageBox() {
		return errorMessages;
	}

	/**
	 * @return the info message box.
	 */
	public WMessageBox getInfoMessageBox() {
		return infoMessages;
	}

	/**
	 * Searches the WComponent tree of the given component for an ancestor that implements the MessageContainer
	 * interface.
	 *
	 * @param component the component to return the Container for
	 * @return the nearest MessageContainer if found, null otherwise
	 */
	protected static MessageContainer getMessageContainer(final WComponent component) {
		for (WComponent c = component; c != null; c = c.getParent()) {
			if (c instanceof MessageContainer) {
				return (MessageContainer) c;
			}
		}

		return null;
	}
}
