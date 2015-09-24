package com.github.bordertech.wcomponents.validator;

import com.github.bordertech.wcomponents.util.InternalMessages;
import java.util.regex.Pattern;

/**
 * <p>
 * A <code>FieldValidator</code> implementation used to match an input fields value with the supplied regular
 * expression. An error is added if the value does not match the supplied pattern.
 * </p>
 * <p>
 * NOTE: When using this validator it is probably a good idea to always provide an error message.
 * </p>
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class RegExFieldValidator extends AbstractFieldValidator {

	/**
	 * The regular expression to validate against.
	 */
	private final Pattern pattern;

	/**
	 * Creates a RegExFieldValidator.
	 *
	 * @param pattern the regular expression to validate against.
	 */
	public RegExFieldValidator(final String pattern) {
		this(pattern, InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID);
	}

	/**
	 * Creates a RegExFieldValidator.
	 *
	 * @param pattern the regular expression to validate against.
	 * @param errorMessage the error message to display when validation fails.
	 */
	public RegExFieldValidator(final String pattern, final String errorMessage) {
		super(errorMessage);
		this.pattern = Pattern.compile(pattern);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isValid() {
		String value = getInputAsString();
		return (value == null) || (value.length() == 0) || (pattern.matcher(value).matches());
	}
}
