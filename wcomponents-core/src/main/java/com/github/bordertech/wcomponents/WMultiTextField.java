package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The WMultiTextField component allows multiple text input elements to be generated dynamically on the client without
 * requiring the page to be reloaded. This component takes its appearance and attributes from a regular text input but
 * allows for one or more items of text to be entered via the add link adjacent to the right of the text input.
 * </p>
 * <p>
 * This component is useful in instances where the user needs to enter one or more text items into the interfaces for a
 * particular field. For instance, the user may have one or more aliases that need to be entered into the system.
 * </p>
 * <p>
 * The following attributes can be set on WMultiTextField:
 * </p>
 * <ul>
 * <li>MaxInputs: The maximum number of text inputs the user can add to the component. Client-side functionality will
 * stop users adding more than the allowable number of inputs via the UI. This class chops off any excess inputs if an
 * attempt is made to add them programmatically.</li>
 * <li>MaxLength: The maximum length of the text fields rendered to allow user text entry.</li>
 * <li>Columns: The size of the text fields rendered to allow user text entry.</li>
 * </ul>
 *
 * @author Christina Harris
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiTextField extends AbstractInput implements AjaxTrigger, AjaxTarget,
		SubordinateTarget {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WMultiTextField.class);

	/**
	 * Creates an empty WMultiTextField.
	 */
	public WMultiTextField() {
		this(null);
	}

	/**
	 * Creates a WMultiTextField with the default input values.
	 *
	 * @param inputs the default set of values.
	 */
	public WMultiTextField(final String[] inputs) {
		getOrCreateComponentModel().setData(inputs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getValue() {
		Object data = getData();
		if (data == null) {
			return null;
		}

		String[] array = null;

		// Array data
		if (data instanceof String[]) {
			array = (String[]) data;
		} else if (data instanceof List) { // List data
			List<?> list = (List<?>) data;
			array = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Object item = list.get(i);
				array[i] = item == null ? "" : item.toString();
			}
		} else { // Object
			array = new String[]{data.toString()};
		}

		return removeEmptyStrings(array);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final Object data) {
		String[] inputs = (String[]) data;
		String[] parsed = removeEmptyStrings(inputs);
		int maxInputs = getMaxInputs();

		if (maxInputs > 0 && parsed != null && parsed.length > maxInputs) {
			LOG.warn("Attempt made to set [" + parsed.length
					+ "] inputs on WMultiTextField but the maximum allowable is [" + maxInputs + "]. Only the first ["
					+ maxInputs + "] inputs will be included.");

			String[] choppedInputs = new String[maxInputs];
			System.arraycopy(parsed, 0, choppedInputs, 0, maxInputs);
			super.setData(choppedInputs);
		} else {
			super.setData(parsed);
		}
	}

	/**
	 * @return The text inputs of this component.
	 */
	public String[] getTextInputs() {
		return getValue();
	}

	/**
	 * Set the text inputs of this component.
	 *
	 * @param inputs The text inputs to set.
	 */
	public void setTextInputs(final String[] inputs) {
		setData(inputs);
	}

	/**
	 * @return The maximum number of strings that can be associated to this component.
	 */
	public int getMaxInputs() {
		return getComponentModel().maxInputs;
	}

	/**
	 * Set the maximum number of strings/inputs that can be associated to this component.
	 *
	 * @param max The maximum number of text inputs.
	 */
	public void setMaxInputs(final int max) {
		getOrCreateComponentModel().maxInputs = max;
	}

	/**
	 * @return the width of the input field in characters.
	 */
	public int getColumns() {
		return getComponentModel().columns;
	}

	/**
	 * Sets the width of the input field in characters.
	 *
	 * @param columns the number of characters to display.
	 */
	public void setColumns(final int columns) {
		getOrCreateComponentModel().columns = columns;
	}

	/**
	 * @return the minimum number of characters that the user can enter into the multi text field.
	 */
	public int getMinLength() {
		return getComponentModel().minLength;
	}

	/**
	 * Set the minimum number of characters that the user can enter into the multi text field.
	 * <p>
	 * Setting the minimum number of characters will not make a field mandatory as the validation is only applied once
	 * the user has entered some text. Use {@link #setMandatory(boolean)} to make a field mandatory, which can be used
	 * in combination with setMinLength.
	 * </p>
	 *
	 * @param minLength the minimum number of characters to allow.
	 */
	public final void setMinLength(final int minLength) {
		getOrCreateComponentModel().minLength = minLength;
	}

	/**
	 * @return the maximum number of characters that the user can enter into the multi text field.
	 */
	public int getMaxLength() {
		return getComponentModel().maxLength;
	}

	/**
	 * Set the maximum number of characters that the user can enter into the multi text field.
	 *
	 * @param maxLength the maximum number of characters to allow.
	 */
	public final void setMaxLength(final int maxLength) {
		getOrCreateComponentModel().maxLength = maxLength;
	}

	/**
	 * The pattern to validate against.
	 * <p>
	 * The pattern must be supported natively by your target user agent (e.g. browser).
	 * </p>
	 * <p>
	 * It is expected the {@link WLabel} for this component describes the required format of the component.
	 * </p>
	 *
	 * @param pattern the pattern to validate against.
	 */
	public final void setPattern(final String pattern) {
		getOrCreateComponentModel().pattern = pattern == null ? null : Pattern.compile(pattern);
	}

	/**
	 * @return the pattern to validate against.
	 */
	public String getPattern() {
		Pattern pattern = getComponentModel().pattern;
		return pattern == null ? null : pattern.toString();
	}

	/**
	 * The string is a comma sperated list of the string inputs.
	 *
	 * @return A string concatenation of the string inputs.
	 */
	@Override
	public String getValueAsString() {
		String result = null;

		String[] inputs = getValue();

		if (inputs != null && inputs.length > 0) {
			StringBuffer stringValues = new StringBuffer();

			for (int i = 0; i < inputs.length; i++) {
				if (i > 0) {
					stringValues.append(", ");
				}

				stringValues.append(inputs[i]);
			}

			result = stringValues.toString();

		}

		return result;
	}

	/**
	 * Set the inputs based on the incoming request. The text input values are set as an array of strings on the
	 * parameter with this name {@link #getName()}. Any empty strings will be ignored.
	 *
	 * @param request the current request.
	 * @return true if the inputs have changed, otherwise return false
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		String[] values = getRequestValue(request);
		String[] current = getValue();

		boolean changed = !Arrays.equals(values, current);

		if (changed) {
			setData(values);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getRequestValue(final Request request) {
		if (isPresent(request)) {
			String[] paramValues = request.getParameterValues(getId());
			return removeEmptyStrings(paramValues);
		} else {
			return getValue();
		}
	}

	/**
	 * Helper that removes empty/null string from the <code>original</code> string array.
	 * <p>
	 * Will treat an empty array the same as null.
	 * </p>
	 *
	 * @param originals The string array from which the null/empty strings should be removed from.
	 * @return Array of non empty strings from the <code>original</code> string array.
	 */
	private String[] removeEmptyStrings(final String[] originals) {
		if (originals == null) {
			return null;
		} else {
			List<String> parsed = new ArrayList<>();

			for (String original : originals) {
				if (original != null && original.length() > 0) {
					parsed.add(original);
				}
			}
			// Treat empty the same as null
			return parsed.isEmpty() ? null : parsed.toArray(new String[parsed.size()]);
		}
	}

	/**
	 * Override validateComponent to perform further validation.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		String[] values = getValue();

		if (values != null) {
			int max = getMaxLength();
			int min = getMinLength();
			Pattern pattern = getComponentModel().pattern;

			for (String value : values) {
				boolean invalid = false;

				if (Util.empty(value)) {
					continue;
				}

				// Minimum Length
				if (min > 0 && value.length() < min) {
					diags.add(createErrorDiagnostic(
							InternalMessages.DEFAULT_VALIDATION_ERROR_MIN_LENGTH, this,
							String.valueOf(min)));
					invalid = true;
				}

				// Maximum Length
				if (max > 0 && value.length() > max) {
					diags.add(createErrorDiagnostic(
							InternalMessages.DEFAULT_VALIDATION_ERROR_MAX_LENGTH, this,
							String.valueOf(max)));
					invalid = true;
				}

				// Pattern
				if (pattern != null) {
					Matcher matcher = pattern.matcher(value);
					if (!matcher.matches()) {
						diags.add(createErrorDiagnostic(
								InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID_PATTERN, this));
						invalid = true;
					}
				}

				// Only report the first invalid item
				if (invalid) {
					break;
				}
			}
		}
	}

	/**
	 * Creates a new MultiTextFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new MultiTextFieldModel
	 */
	@Override
	protected MultiTextFieldModel newComponentModel() {
		return new MultiTextFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MultiTextFieldModel getComponentModel() {
		return (MultiTextFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MultiTextFieldModel getOrCreateComponentModel() {
		return (MultiTextFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * MultiTextFieldModel holds Extrinsic state management of the field.
	 */
	public static class MultiTextFieldModel extends InputModel {

		/**
		 * The maximum text length to allow.
		 */
		private int maxLength;

		/**
		 * The minimum text length to allow.
		 */
		private int minLength;

		/**
		 * The number of columns to display for the field.
		 */
		private int columns;

		/**
		 * The maximum number of text inputs the user can add to the component.
		 */
		private int maxInputs;

		/**
		 * The pattern to allow.
		 */
		private Pattern pattern;
	}
}
