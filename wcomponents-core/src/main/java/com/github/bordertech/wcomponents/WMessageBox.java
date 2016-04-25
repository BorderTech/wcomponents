package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Duplet;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * A <code>WMessageBox</code> is a component that renders a collection of informational messages. Most commonly placed
 * near the top of an application's UI.
 * </p>
 * <p>
 * The different message types catered for here are:
 * </p>
 * <ul>
 * <li>Error - For reporting system and application errors.</li>
 * <li>Warning - For warning users about the possible consequences of an action. The process may continue.</li>
 * <li>Information - For providing general information.</li>
 * <li>Success - For reporting on the success of an action or operation.</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Validation errors should be provided via the component
 * {@link com.github.bordertech.wcomponents.validation.WValidationErrors}.
 * </p>
 *
 * @see com.github.bordertech.wcomponents.validation.WValidationErrors
 * @see com.github.bordertech.wcomponents.WMessages
 * @author Ming Gao
 * @author Adam Millard
 * @author Jonathan Austin
 * @author Mark Reeves
 */
public class WMessageBox extends AbstractWComponent implements AjaxTarget, SubordinateTarget {

	/**
	 * The message box type used to display "success" messages.
	 */
	public static final Type SUCCESS = Type.SUCCESS;

	/**
	 * The message box type used to display informational messages.
	 */
	public static final Type INFO = Type.INFO;

	/**
	 * The message box type used to display warning messages.
	 */
	public static final Type WARN = Type.WARN;

	/**
	 * The message box type used to display error messages.
	 */
	public static final Type ERROR = Type.ERROR;

	/**
	 * An enumeration of message box types.
	 */
	public enum Type {
		/**
		 * The message box type used to display "success" messages.
		 */
		SUCCESS,
		/**
		 * The message box type used to display informational messages.
		 */
		INFO,
		/**
		 * The message box type used to display warning messages.
		 */
		WARN,
		/**
		 * The message box type used to display error messages.
		 */
		ERROR
	}

	/**
	 * Holds the extrinsic state information of a WMessageBox.
	 *
	 * @author Ming Gao
	 * @author Adam Millard
	 * @author Mark Reeves
	 */
	public static class MessageModel extends ComponentModel {

		/**
		 * Message box type.
		 */
		private Type type;
		/**
		 * Message with encoding flag.
		 */
		private final List<Duplet<Serializable, Boolean>> messages = new ArrayList<>();

		/**
		 * The message box title text.
		 */
		private Serializable title;
	}

	/**
	 * Creates a WMessageBox of the given type.
	 *
	 * @param type the messageBox type, one of {@link #SUCCESS}, {@link #INFO}, {@link #WARN} or {@link #ERROR}.
	 */
	public WMessageBox(final Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Message type cannot be null");
		}
		setType(type);
	}

	/**
	 * Creates a WMessageBox of the given type with an initial message.
	 *
	 * @param type the messageBox type, one of {@link #SUCCESS}, {@link #INFO}, {@link #WARN} or {@link #ERROR}.
	 * @param msg the initial message to display, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public WMessageBox(final Type type, final String msg, final Serializable... args) {
		this(type, true, msg, args);
	}

	/**
	 * Creates a WMessageBox of the given type with an initial message.
	 *
	 * @param type the messageBox type, one of {@link #SUCCESS}, {@link #INFO}, {@link #WARN} or {@link #ERROR}.
	 * @param encode true to encode the message text, false to leave it unencoded.
	 * @param msg the initial message to display, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public WMessageBox(final Type type, final boolean encode, final String msg,
			final Serializable... args) {
		this(type);
		addMessage(encode, msg, args);
	}

	/**
	 * Sets the message box type.
	 *
	 * @param type the messageBox type, one of {@link #SUCCESS}, {@link #INFO}, {@link #WARN} or {@link #ERROR}.
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type;
	}

	/**
	 * @return the message box type for the given context.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * Sets the message box title.
	 *
	 * @param title the message box title to set, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setTitleText(final String title, final Serializable... args) {
		MessageModel model = getOrCreateComponentModel();
		model.title = I18nUtilities.asMessage(title, args);
	}

	/**
	 * @return the message box title.
	 */
	public String getTitleText() {
		return I18nUtilities.format(null, getComponentModel().title);
	}

	/**
	 * Adds a message to the message box.
	 *
	 * @param msg the text of the message to add, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void addMessage(final String msg, final Serializable... args) {
		addMessage(true, msg, args);
	}

	/**
	 * Adds a message to the message box.
	 *
	 * @param encode true to encode the message text, false to leave it unencoded.
	 * @param msg the text of the message to add, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void addMessage(final boolean encode, final String msg, final Serializable... args) {
		MessageModel model = getOrCreateComponentModel();
		model.messages.add(new Duplet<>(I18nUtilities.asMessage(msg, args), encode));
	}

	/**
	 * Removes a message from the message box.
	 *
	 * @param index the index of the message to remove.
	 */
	public void removeMessage(final int index) {
		getOrCreateComponentModel().messages.remove(index);
	}

	/**
	 * Removes a message from the message box.
	 *
	 * @param index the index of the mssage to remove.
	 */
	public void removeMessages(final int index) {
		MessageModel model = getOrCreateComponentModel();

		if (model.messages != null) {
			model.messages.remove(index);
		}
	}

	/**
	 * Removes all messages from the message box.
	 */
	public void clearMessages() {
		MessageModel model = getOrCreateComponentModel();

		if (model.messages != null) {
			model.messages.clear();
		}
	}

	/**
	 * Retrieves the list of messages.
	 *
	 * @return the messages for the current context.
	 */
	public List<String> getMessages() {
		MessageModel model = getComponentModel();

		List<String> messages = new ArrayList<>(model.messages.size());

		for (Duplet<Serializable, Boolean> message : model.messages) {
			String text = I18nUtilities.format(null, message.getFirst());
			messages.add(message.getSecond() ? WebUtilities.encode(text) : text);
		}

		return Collections.unmodifiableList(messages);
	}

	/**
	 * Indicates whether the message box contains messages in the given context.
	 *
	 * @return true if the message box contains at least one message.
	 */
	public boolean hasMessages() {
		List<String> msgs = getMessages();
		return !msgs.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String details = String.valueOf(getType()) + ':' + getMessages();
		return toString(details);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new MessageModel.
	 */
	@Override
	protected ComponentModel newComponentModel() {
		return new MessageModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MessageModel getComponentModel() {
		return (MessageModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MessageModel getOrCreateComponentModel() {
		return (MessageModel) super.getOrCreateComponentModel();
	}
}
