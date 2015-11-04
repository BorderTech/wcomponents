package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.WFieldErrorIndicator;
import com.github.bordertech.wcomponents.validation.WFieldWarningIndicator;
import com.github.bordertech.wcomponents.validator.FieldValidator;

/**
 * <p>
 * This component is used to aggregate the common elements that are used to display an editable field (ie, the label,
 * the field itself and an error indicator). It can only be used by adding instances to a {@link WFieldLayout}
 * component. If input field is not present then it renders as read only field.
 * </p>
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class WField extends AbstractContainer implements AjaxTarget, SubordinateTarget {

	/**
	 * The label for the field.
	 */
	private final WLabel label;
	/**
	 * The component for the field.
	 */
	private final WComponent field;
	/**
	 * The error indicator for the field.
	 */
	private final WFieldErrorIndicator errorIndicator;
	/**
	 * The warning indicator for the field.
	 */
	private final WFieldWarningIndicator warningIndicator;

	/**
	 * Creates a WField with the specified label text and field.
	 *
	 * @param labelText contains the textual label to be displayed next to the input field.
	 * @param field is the component to be layed out (normally in the right hand column). In order to support
	 * validation, the given field must be a WInput or a component that contains one WInput as a descendant.
	 */
	WField(final String labelText, final WComponent field) {
		this(new WLabel(labelText), field);
	}

	/**
	 * Creates a WField with the specified label and field.
	 *
	 * @param label the field label
	 * @param field the field
	 */
	WField(final WLabel label, final WComponent field) {
		this.label = label;
		this.field = field;

		if (label != null) {
			add(label);
		}

		add(field);

		// Find the first component in the field that can be labelled (if any)
		WComponent labelField = findComponentForLabel(field);

		// Check if we need to associate the label to the labelField
		if (label != null && label.getForComponent() == null && labelField != null) {
			// Save the original label (in case the field is in a nested WField)
			WLabel origLabel = labelField.getLabel();
			label.setForComponent(labelField);

			// If the labelField is in a nested WField, restore the original label (if set)
			if (labelField instanceof AbstractWComponent && origLabel != null && WebUtilities.
					getAncestorOfClass(WField.class, labelField) != this) {
				((AbstractWComponent) labelField).setLabel(origLabel);
			}
		}

		// If there is a label field, and it is not nested in another WField, then set the error indicators
		if (labelField != null && WebUtilities.getAncestorOfClass(WField.class, labelField) == this) {
			errorIndicator = new WFieldErrorIndicator(labelField);
			add(errorIndicator, "errorIndicator");
			warningIndicator = new WFieldWarningIndicator(labelField);
			add(warningIndicator, "warningIndicator");
		} else {
			errorIndicator = null;
			warningIndicator = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		if (getIdName() == null && field != null && field.getIdName() != null) {
			return field.getId() + ID_FRAMEWORK_ASSIGNED_SEPERATOR + "fld";
		}
		return super.getId();
	}

	/**
	 * Finds the first component that can be labeled (if any).
	 *
	 * @param component the root of the component hierarchy to check.
	 * @return the first component that can be labelled, otherwise null.
	 */
	private WComponent findComponentForLabel(final WComponent component) {
		// Input (but not a RadioButtonGroup)
		if (component instanceof Input && !(component instanceof RadioButtonGroup)) {
			return component;
		}

		// Fieldset or RadioButton
		if (component instanceof WFieldSet || component instanceof WRadioButton) {
			return component;
		}

		if (component instanceof Container) {
			Container container = (Container) component;
			final int childCount = container.getChildCount();

			for (int i = 0; i < childCount; i++) {
				WComponent theField = findComponentForLabel(container.getChildAt(i));

				if (theField != null) {
					return theField;
				}
			}
		}

		return null;
	}

	/**
	 * @return the field label
	 */
	@Override
	public WLabel getLabel() {
		return label;
	}

	/**
	 * @return the field
	 */
	public WComponent getField() {
		return field;
	}

	/**
	 * @return the field's error indicator
	 */
	public WFieldErrorIndicator getErrorIndicator() {
		return errorIndicator;
	}

	/**
	 * @return the field's warning indicator
	 */
	public WFieldWarningIndicator getWarningIndicator() {
		return warningIndicator;
	}

	/**
	 * Sets whether this field is mandatory.
	 * <p>
	 * The mandatory flag will only be set if the field is a {@link Input} component or {@link WFieldSet}.
	 * </p>
	 *
	 * @param mandatory true for mandatory, false for optional
	 * @deprecated set mandatory directly on the required component.
	 */
	@Deprecated
	public void setMandatory(final boolean mandatory) {
		setMandatory(mandatory, null);
	}

	/**
	 * Sets whether this field is mandatory.
	 * <p>
	 * The mandatory flag will only be set if the field is a {@link Input} component or {@link WFieldSet}.
	 * </p>
	 *
	 * @param mandatory true for mandatory, false for optional
	 * @param errorMessage the error message to display on validation error.
	 * @deprecated set mandatory directly on the required component.
	 */
	@Deprecated
	public void setMandatory(final boolean mandatory, final String errorMessage) {
		WComponent labelField = findComponentForLabel(field);

		if (labelField instanceof Mandatable) {
			((Input) labelField).setMandatory(mandatory, errorMessage);
		} else if (labelField instanceof WFieldSet) {
			((WFieldSet) labelField).setMandatory(mandatory, errorMessage);
		}
	}

	/**
	 * Adds a validator to the input field.
	 * <p>
	 * The validator will only be added if the field is a {@link Input} component.
	 * </p>
	 *
	 * @param validator the validator to add.
	 */
	public void addValidator(final FieldValidator validator) {
		WComponent labelField = findComponentForLabel(field);

		if (labelField instanceof Input) {
			((Input) labelField).addValidator(validator);
		}
	}

	/**
	 * Updates the label text (if label exists).
	 *
	 * @param text the new label text
	 */
	public void setLabelText(final String text) {
		if (label != null) {
			label.setText(text);
		}
	}

	/**
	 * Gets the label text (if label exists) else returns <code>null</code>.
	 *
	 * @return the text of the label component of this field.
	 */
	public String getLabelText() {
		String text = null;

		if (label != null) {
			text = label.getText();
		}

		return text;
	}

	/**
	 * Indicates the desired width of the input field, as a percentage of the available space.
	 *
	 * @return the percentage width, or 0 to use the default field width.
	 */
	public int getInputWidth() {
		return getComponentModel().inputWidth;
	}

	/**
	 * Sets the desired width of the input field, as a percentage of the available space.
	 *
	 * @param inputWidth the percentage width, or &lt;= 0 to use the default field width.
	 */
	public void setInputWidth(final int inputWidth) {
		if (inputWidth > 100) {
			throw new IllegalArgumentException(
					"inputWidth (" + inputWidth + ") cannot be greater than 100 percent.");
		}
		getOrCreateComponentModel().inputWidth = Math.max(0, inputWidth);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(getClass().getSimpleName());
		String text = getLabelText();

		if (text == null) {
			buf.append("(null)");
		} else {
			buf.append("(\"").append(text).append("\")");
		}

		buf.append(childrenToString(field));

		return buf.toString();
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new FieldModel.
	 */
	@Override    // For type safety only
	protected FieldModel newComponentModel() {
		return new FieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override    // For type safety only
	protected FieldModel getComponentModel() {
		return (FieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override    // For type safety only
	protected FieldModel getOrCreateComponentModel() {
		return (FieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class FieldModel extends ComponentModel {

		/**
		 * Indicates the desired width of the input field, as a percentage of the available space.
		 */
		private int inputWidth;
	}
}
