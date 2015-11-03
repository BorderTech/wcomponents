package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A component to display a warning/error (or other) message that can be associated with an input field.
 *
 * @author Darian Bridge
 * @version 9/10/2009
 * @since 1.0.0
 */
public abstract class AbstractWFieldIndicator extends AbstractWComponent {

	/**
	 * The field that this indicator is related to.
	 */
	private final WComponent relatedField;

	/**
	 * Remember the dynamic list of indicator diagnostics.
	 */
	public static class FieldIndicatorModel extends ComponentModel {

		/**
		 * A List of Diagnostic objects.
		 */
		private final List<Diagnostic> diagnostics = new ArrayList<>();
	}

	/**
	 * Constructor. Set the field that the indicator is related to.
	 *
	 * @param relatedField The related field.
	 */
	public AbstractWFieldIndicator(final WComponent relatedField) {
		this.relatedField = relatedField;
	}

	// ================================
	// Validation
	/**
	 * Iterates over the {@link Diagnostic}s and finds the diagnostics that related to {@link #relatedField}.
	 *
	 * @param diags A List of Diagnostic objects.
	 * @param severity A Diagnostic severity code. e.g. {@link Diagnostic#ERROR}
	 */
	protected void showIndicatorsForComponent(final List<Diagnostic> diags, final int severity) {
		FieldIndicatorModel model = getOrCreateComponentModel();
		model.diagnostics.clear();
		UIContext uic = UIContextHolder.getCurrent();

		for (int i = 0; i < diags.size(); i++) {
			Diagnostic diagnostic = diags.get(i);

			if (diagnostic.getSeverity() == severity && uic == diagnostic.getContext()
					// To support repeated components.
					&& relatedField == diagnostic.getComponent()) {
				model.diagnostics.add(diagnostic);
			}
		}
	}

	/**
	 * Provide subclasses with access to the related field.
	 *
	 * @return The Related Field.
	 */
	protected WComponent getRelatedField() {
		return this.relatedField;
	}

	/**
	 * Returns the id of the related field ({@link #relatedField}).
	 *
	 * @return The id of the field that is related to this indicator.
	 */
	public String getRelatedFieldId() {
		return relatedField.getId();
	}

	/**
	 * Return the diagnostics for this indicator.
	 *
	 * @return A list of diagnostics (may be empty) for this indicator.
	 */
	public List<Diagnostic> getDiagnostics() {
		FieldIndicatorModel model = getComponentModel();
		return Collections.unmodifiableList(model.diagnostics);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * @return The FieldIndicatorModel.
	 */
	@Override
	protected FieldIndicatorModel getComponentModel() {
		return (FieldIndicatorModel) super.getComponentModel();
	}

	/**
	 * @return The FieldIndicatorModel.
	 */
	@Override
	protected FieldIndicatorModel getOrCreateComponentModel() {
		return (FieldIndicatorModel) super.getOrCreateComponentModel();
	}

	/**
	 * @return a new Component Model.
	 */
	@Override
	protected ComponentModel newComponentModel() {
		return new FieldIndicatorModel();
	}

	/**
	 * Used to determine the type of field indicator.
	 */
	public enum FieldIndicatorType {
		/**
		 * Information Type.
		 */
		INFO,
		/**
		 * Warning Type.
		 */
		WARN,
		/**
		 * Error Type.
		 */
		ERROR
	};

	/**
	 * Return the type of field indicator.
	 *
	 * @return the field indicator type.
	 */
	public abstract FieldIndicatorType getFieldIndicatorType();

}
