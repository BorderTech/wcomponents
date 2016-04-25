package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Message - encapsulates a simple message that is to be displayed to the user.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class Message implements Serializable {

	/**
	 * A message type that indicates that a message is an "informational message".
	 */
	public static final int INFO_MESSAGE = 0;

	/**
	 * A message type that indicates that a message is a "warning message".
	 */
	public static final int WARNING_MESSAGE = 1;

	/**
	 * A message type that indicates that a message is an "error message".
	 */
	public static final int ERROR_MESSAGE = 2;

	/**
	 * A message type that indicates that a message is a "success message".
	 */
	public static final int SUCCESS_MESSAGE = 3;

	/**
	 * The content of this message.
	 */
	private final String message;

	/**
	 * The message arguments, if applicable.
	 */
	private Serializable[] args;

	/**
	 * The type of this message. One of ({@link #SUCCESS_MESSAGE}, {@link #INFO_MESSAGE}, {@link #WARNING_MESSAGE} or
	 * {@link #ERROR_MESSAGE}).
	 */
	private int type;

	/**
	 * Creates an informational message.
	 *
	 * @param message the message text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public Message(final String message, final Serializable... args) {
		this(INFO_MESSAGE, message, args);
	}

	/**
	 * Creates a message.
	 *
	 * @param type the type of message.
	 * @param message the message text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public Message(final int type, final String message, final Serializable... args) {
		setType(type);
		this.message = message;
		this.args = args == null || args.length == 0 ? null : args;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the message format arguments.
	 *
	 * @param args the message arguments.
	 */
	public void setArgs(final Serializable... args) {
		this.args = args == null || args.length == 0 ? null : args;
	}

	/**
	 * @return Returns the message format arguments.
	 */
	public Serializable[] getArgs() {
		return args;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param type The type to set.
	 */
	public final void setType(final int type) {
		if (type == INFO_MESSAGE || type == WARNING_MESSAGE || type == ERROR_MESSAGE || type == SUCCESS_MESSAGE) {
			this.type = type;
		} else {
			throw new IllegalArgumentException("Invalid message type: " + type);
		}
	}

	/**
	 * Returns a string representation of this Message.
	 *
	 * @return the message text.
	 */
	@Override
	public String toString() {
		return I18nUtilities.format(null, this);
	}

	/**
	 * Indicates whether this Message is equal to the given object.
	 *
	 * @param obj the object to compare against.
	 * @return true if the supplied object is a Message and is equal to this message.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Message)) {
			return false;
		}

		Message other = (Message) obj;

		return type == other.type
				&& Util.equals(message, other.message)
				&& Arrays.equals(args, other.args);
	}

	/**
	 * @return the message's hash code.
	 */
	@Override
	public int hashCode() {
		return String.valueOf(message).hashCode();
	}
}
