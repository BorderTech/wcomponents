package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.TreeUtil;
import com.github.bordertech.wcomponents.util.WComponentTreeVisitor;
import com.github.bordertech.wcomponents.util.visitor.AbstractVisitorWithResult;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.List;

/**
 * WFieldSet is used to logically group together input fields. It can be used to optionally render a border and title
 * around the fields.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldSet extends AbstractMutableContainer implements AjaxTarget, SubordinateTarget, Mandatable, Marginable, DropZone,
		MultiInputComponent, Diagnosable {

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

		if (isMandatory() && !hasInputWithValue()) {
			diags.add(createMandatoryDiagnostic());
		}
	}

	/**
	 * Checks at least one input component has a value.
	 *
	 * @return true if the field set contains an input with a value
	 */
	private boolean hasInputWithValue() {

		// Visit all children of the fieldset of type Input
		AbstractVisitorWithResult<Boolean> visitor = new AbstractVisitorWithResult<Boolean>() {
			@Override
			public WComponentTreeVisitor.VisitorResult visit(final WComponent comp) {
				// Check if the component is an Input and has a value
				if (comp instanceof Input && !((Input) comp).isEmpty()) {
					setResult(true);
					return VisitorResult.ABORT;
				}
				return VisitorResult.CONTINUE;
			}
		};
		visitor.setResult(false);

		TreeUtil.traverseVisible(this, visitor);

		return visitor.getResult();
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
	 * Set the diagnostics for a given severity.
	 * @param diags the list of Diagnostics
	 * @param severity the message severity
	 */
	private void showIndicatorsForComponent(final List<Diagnostic> diags, final int severity) {
		FieldSetModel model = getOrCreateComponentModel();
		if (severity == Diagnostic.ERROR) {
			model.errorDiagnostics.clear();
		} else {
			model.warningDiagnostics.clear();
		}
		UIContext uic = UIContextHolder.getCurrent();

		for (int i = 0; i < diags.size(); i++) {
			Diagnostic diagnostic = diags.get(i);
			// NOTE: double equals because they must be the same instance.
			if (diagnostic.getSeverity() == severity && uic == diagnostic.getContext() && this == diagnostic.getComponent()) {
				if (severity == Diagnostic.ERROR) {
					model.errorDiagnostics.add(diagnostic);
				} else {
					model.warningDiagnostics.add(diagnostic);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void showErrorIndicatorsForComponent(final List<Diagnostic> diags) {
		showIndicatorsForComponent(diags, Diagnostic.ERROR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void showWarningIndicatorsForComponent(final List<Diagnostic> diags) {
		showIndicatorsForComponent(diags, Diagnostic.WARNING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Diagnostic> getDiagnostics(final int severity) {
		FieldSetModel model = getComponentModel();
		switch (severity) {
			case Diagnostic.ERROR:
				return model.errorDiagnostics;
			case Diagnostic.WARNING:
				return model.warningDiagnostics;
			case Diagnostic.INFO:
				return model.infoDiagnostics;
			case Diagnostic.SUCCESS:
				return model.successDiagnostics;
			default:
				return null;
		}
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

		/**
		 * A List of error level Diagnostic objects.
		 */
		private final List<Diagnostic> errorDiagnostics = new ArrayList<>();

		/**
		 * A List of warning level Diagnostic objects.
		 */
		private final List<Diagnostic> warningDiagnostics = new ArrayList<>();

		/**
		 * A List of info level Diagnostic objects.
		 */
		private final List<Diagnostic> infoDiagnostics = new ArrayList<>();

		/**
		 * A List of success level Diagnostic objects.
		 */
		private final List<Diagnostic> successDiagnostics = new ArrayList<>();
	}
}
