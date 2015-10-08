package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * WFieldSet is used to logically group together input fields. It can be used to optionally render a border and title
 * around the fields.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldSet extends AbstractMutableContainer implements AjaxTarget, SubordinateTarget,
		Mandatable, Marginable, DropZone {

	/**
	 * Describes how the field set's frame is rendered.
	 */
	public enum FrameType {
		/**
		 * The field set frame will be rendered with a lined border and the title.
		 */
		NORMAL,
		/**
		 * The field set frame will be rendered with the title only.
		 */
		NO_BORDER,
		/**
		 * The field set frame will be rendered with a lined border only.
		 */
		NO_TEXT,
		/**
		 * The field set frame will not be rendered.
		 */
		NONE
	}

	/**
	 * The frameset title.
	 */
	private final WDecoratedLabel title;

	/**
	 * Creates a WFieldSet.
	 *
	 * @param title the fieldset title.
	 */
	public WFieldSet(final String title) {
		this.title = title == null ? new WDecoratedLabel() : new WDecoratedLabel(title);

		add(this.title);
	}

	/**
	 * Creates a WFieldSet.
	 *
	 * @param title the fieldset title.
	 */
	public WFieldSet(final WComponent title) {
		this.title = title == null ? new WDecoratedLabel() : new WDecoratedLabel(title);
		add(this.title);
	}

	/**
	 * @return Returns the frameType.
	 */
	public FrameType getFrameType() {
		return getComponentModel().frameType;
	}

	/**
	 * @param frameType The frameType to set.
	 */
	public void setFrameType(final FrameType frameType) {
		getOrCreateComponentModel().frameType = frameType;
	}

	/**
	 * @return Returns the title.
	 */
	public WDecoratedLabel getTitle() {
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(final String title) {
		this.title.setText(title);
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(final WComponent title) {
		this.title.setBody(title);
	}

	/**
	 * Set whether or not this field set is mandatory, and customise the error message that will be displayed.
	 *
	 * @param mandatory true for mandatory, false for optional.
	 * @param message the message to display to the user on mandatory validation failure.
	 */
	public void setMandatory(final boolean mandatory, final String message) {
		setFlag(ComponentModel.MANDATORY_FLAG, mandatory);
		getOrCreateComponentModel().errorMessage = message;
	}

	/**
	 * Set whether or not this field set is mandatory.
	 *
	 * @param mandatory if true, the fieldset is mandatory.
	 */
	@Override
	public void setMandatory(final boolean mandatory) {
		setFlag(ComponentModel.MANDATORY_FLAG, mandatory);
	}

	/**
	 * Indicates whether this fieldset is mandatory in the given context.
	 *
	 * @return true if this fieldset is mandatory in the given context.
	 */
	@Override
	public boolean isMandatory() {
		return isFlagSet(ComponentModel.MANDATORY_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		if (isMandatory() && !hasInputWithValue(this)) {
			diags.add(createMandatoryDiagnostic());
		}
	}

	/**
	 * Checks at least one input component has a value.
	 *
	 * @param component the root of the component hierarchy to check.
	 * @return true if the component has a value, otherwise false.
	 */
	private boolean hasInputWithValue(final WComponent component) {
		if (component instanceof Input) {
			return !((Input) component).isEmpty();
		}

		if (component instanceof Container) {
			Container container = (Container) component;
			int childCount = container.getChildCount();

			for (int i = 0; i < childCount; i++) {
				boolean hasValue = hasInputWithValue(container.getChildAt(i));

				if (hasValue) {
					return true;
				}
			}
		}

		return false;
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
		return createErrorDiagnostic(msg, getTitle().getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getTitle().getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, 1, getChildCount() - 1);
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new FieldSetModel.
	 */
	@Override // For type safety only
	protected FieldSetModel newComponentModel() {
		return new FieldSetModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FieldSetModel getComponentModel() {
		return (FieldSetModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FieldSetModel getOrCreateComponentModel() {
		return (FieldSetModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class FieldSetModel extends ComponentModel {

		/**
		 * Controls whether the fieldset should be rendered without a frame.
		 */
		private FrameType frameType = FrameType.NORMAL;

		/**
		 * The error message to display when the input fails the mandatory validation check.
		 */
		private String errorMessage;

		/**
		 * The margins to be used on the fieldset.
		 */
		private Margin margin;
	}
}
