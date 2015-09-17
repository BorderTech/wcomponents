package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * <p>
 * This class is used by the Layout classes to assist with common but primitive html/xml string construction.
 * </p>
 * <p>
 * It extends PrintWriter to allow the same instance to be re-used by the old layout classes which use PrintWriters.
 * </p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public final class XmlStringBuilder extends PrintWriter {

	/**
	 * If true, format XML content by indenting nested tags.
	 */
	private boolean indentingOn = true;

	/**
	 * The current indent level.
	 */
	private int indentLevel = -1;

	/**
	 * Indicates whether an indent has been performed on the current line.
	 */
	private boolean newIndentDone = false;

	/**
	 * The translator used to translate messages.
	 */
	private final Locale locale;

	/**
	 * Creates a XmlStringBuilder with the default capacity and no explicit locale.
	 *
	 * @param writer the backing writer.
	 */
	public XmlStringBuilder(final Writer writer) {
		this(writer, null);
	}

	/**
	 * Creates a XmlStringBuilder with the default capacity and the given Locale.
	 *
	 * @param writer the backing writer.
	 * @param locale the locale to use for translating messages.
	 */
	public XmlStringBuilder(final Writer writer, final Locale locale) {
		super(writer);
		this.locale = locale;
	}

	/**
	 * <p>
	 * Adds a starting xml tag to the end of this XmlStringBuilder.
	 * </p>
	 * <p>
	 * Eg. &lt;name&gt;
	 * </p>
	 *
	 * @param name the name of the tag to be added.
	 */
	public void appendTag(final String name) {
		incrementIndent();
		indent();
		write('<');
		write(name);
		write('>');
	}

	/**
	 * <p>
	 * Adds an ending xml tag to the end of this XmlStringBuilder.
	 * </p>
	 * <p>
	 * Eg. &lt;/name&gt;
	 * </p>
	 *
	 * @param name the name of the ending tag to be added.
	 */
	public void appendEndTag(final String name) {
		indent();
		write('<');
		write('/');
		write(name);
		write('>');

		decrementIndent();
	}

	/**
	 * <p>
	 * Adds a starting xml tag (but without the closing angle bracket) to the end of this XmlStringBuilder.
	 * </p>
	 * <p>
	 * Eg. &lt;name
	 * </p>
	 *
	 * @param name the name of the tag to be added.
	 */
	public void appendTagOpen(final String name) {
		incrementIndent();
		indent();
		write('<');
		write(name);
	}

	/**
	 * <p>
	 * Adds a closing angle bracket to the end of this XmlStringBuilder.
	 * </p>
	 * <p>
	 * Eg. &gt;
	 * </p>
	 */
	public void appendClose() {
		write('>');
	}

	/**
	 * <p>
	 * Adds a tag end character plus a closing angle bracket to the end of this XmlStringBuilder.
	 * </p>
	 * <p>
	 * Eg. /&gt;
	 * </p>
	 */
	public void appendEnd() {
		write(" />");
		decrementIndent();
	}

	// === start formatting routines ===
	/**
	 * Turns indenting off for further content written to this XmlStringBuilder.
	 */
	public void turnIndentingOff() {
		indentingOn = false;
	}

	/**
	 * Turns indenting on for further content written to this XmlStringBuilder.
	 */
	public void turnIndentingOn() {
		indentingOn = true;
	}

	/**
	 * Increments the indenting level if indenting is active.
	 */
	private void incrementIndent() {
		if (indentingOn) {
			indentLevel++;
			newIndentDone = false;
		}
	}

	/**
	 * Decrements the indenting level if indenting is active.
	 */
	private void decrementIndent() {
		if (indentingOn) {
			newIndentDone = false;

			if (indentLevel > 0) {
				indentLevel--;
			}
		}
	}

	/**
	 * If indenting, outputs an indent for the current indenting level.
	 */
	private void indent() {
		if (indentingOn && !newIndentDone) {
			println();
			for (int i = 0; i < indentLevel; i++) {
				write('\t');
			}
			newIndentDone = true;
		}
	}

	// === end formatting routines ===
	/**
	 * <p>
	 * Adds an xml attribute name+value pair to the end of this XmlStringBuilder. All attribute values are escaped to
	 * prevent malformed XML and XSS attacks.
	 * </p>
	 * <p>
	 * If the value is null an empty string "" is output.
	 * </p>
	 * <p>
	 * Eg. name="value"
	 * </p>
	 *
	 * @param name the name of the attribute to be added.
	 * @param value the value of the attribute to be added.
	 */
	public void appendAttribute(final String name, final Object value) {
		write(' ');
		write(name);
		write("=\"");

		if (value instanceof Message) {
			appendEscaped(translate(value));
		} else if (value != null) {
			appendEscaped(value.toString());
		}

		write('"');
	}

	/**
	 * <p>
	 * Adds an xml attribute name+value pair to the end of this XmlStringBuilder.
	 * </p>
	 * <p>
	 * Eg. name="1"
	 * </p>
	 *
	 * @param name the name of the attribute to be added.
	 * @param value the value of the attribute to be added.
	 */
	public void appendAttribute(final String name, final int value) {
		write(' ');
		write(name);
		write("=\"");
		write(String.valueOf(value));
		write('"');
	}

	/**
	 * <p>
	 * If the value is not null, add an xml attribute name+value pair to the end of this XmlStringBuilder
	 * <p>
	 * Eg. name="value"
	 * </p>
	 *
	 * @param name the name of the attribute to be added.
	 * @param value the value of the attribute.
	 */
	public void appendOptionalAttribute(final String name, final Object value) {
		if (value != null) {
			appendAttribute(name, value);
		}
	}

	/**
	 * <p>
	 * If the flag is true, add an xml attribute name+value pair to the end of this XmlStringBuilder
	 * <p>
	 * Eg. name="1"
	 * </p>
	 *
	 * @param name the name of the boolean attribute to be added.
	 * @param flag should this attribute be set.
	 * @param value the value of the attribute.
	 */
	public void appendOptionalAttribute(final String name, final boolean flag, final int value) {
		if (flag) {
			appendAttribute(name, value);
		}
	}

	/**
	 * <p>
	 * If the flag is true, add an xml attribute name+value pair to the end of this XmlStringBuilder
	 * <p>
	 * Eg. name="value"
	 * </p>
	 *
	 * @param name the name of the boolean attribute to be added.
	 * @param flag should this attribute be set.
	 * @param value the value of the attribute.
	 */
	public void appendOptionalAttribute(final String name, final boolean flag, final Object value) {
		if (flag) {
			appendAttribute(name, value);
		}
	}

	/**
	 * If the value in not null, add it to the end of this XmlStringBuilder.
	 *
	 * @param value the value
	 */
	public void appendOptional(final String value) {
		if (value != null) {
			write(value);
		}
	}

	/**
	 * Appends the string to this XmlStringBuilder. XML values are not escaped.
	 *
	 * @param string the String to append.
	 */
	public void append(final String string) {
		append(string, false);
	}

	/**
	 * Appends the given text to this XmlStringBuilder. XML values are escaped.
	 *
	 * @param text the message to append.
	 */
	public void append(final Object text) {
		if (text instanceof Message) {
			append(translate(text), true);
		} else if (text != null) {
			append(text.toString(), true);
		}
	}

	/**
	 * Appends the string to this XmlStringBuilder. XML values are not escaped.
	 *
	 * @param string the String to append.
	 * @param encode true to encode the string before output, false to output as is
	 */
	public void append(final String string, final boolean encode) {
		if (encode) {
			appendOptional(WebUtilities.encode(string));
		} else {
			write(string);
		}
	}

	/**
	 * Appends the integer value to this XmlStringBuilder.
	 *
	 * @param value the value to append.
	 */
	public void append(final int value) {
		super.write(String.valueOf(value));
	}

	/**
	 * Appends the given character to this XmlStringBuilder. XML values are not escaped.
	 *
	 * @param chr the character to append.
	 * @return this XmlStringBuilder
	 */
	@Override
	public PrintWriter append(final char chr) {
		super.write(chr);
		return this;
	}

	/**
	 * Appends an XML-escaped copy of the string to this XmlStringBuffer. If the string is null then no text will be
	 * appended.
	 *
	 * @param string the String to append.
	 */
	public void appendEscaped(final String string) {
		append(string, true);
	}

	/**
	 * Translates the given message into the appropriate user text. This takes the current locale into consideration, if
	 * set.
	 *
	 * @param messageObject the message to translate.
	 * @return the translated message.
	 */
	private String translate(final Object messageObject) {
		if (messageObject instanceof Message) {
			Message message = (Message) messageObject;
			return I18nUtilities.format(locale, message.getMessage(), (Object[]) message.getArgs());
		} else if (messageObject != null) {
			return I18nUtilities.format(locale, messageObject.toString());
		}

		return null;
	}
}
