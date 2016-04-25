package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.WComponent;
import java.util.List;

/**
 * <p>
 * This component is used to render errors relating to an individual field. Generally, this is placed next to the input
 * field to help highlight the individual fields that are in error. An instance of the indicator is automatically
 * provided when adding a field to a WFieldLayout.</p>
 *
 * @author Adam Millard
 */
public class WFieldErrorIndicator extends AbstractWFieldIndicator {

	/**
	 * Creates an WFieldErrorIndicator which is associated with a field.
	 *
	 * @param relatedField The related field.
	 */
	public WFieldErrorIndicator(final WComponent relatedField) {
		super(relatedField);
	}

	/**
	 * Calls {@link #showIndicatorsForComponent(List, int)} with a {@link Diagnostic#ERROR}.
	 *
	 * @param diags A List of Diagnostic objects.
	 */
	@Override
	protected void showErrorIndicatorsForComponent(final List<Diagnostic> diags) {
		showIndicatorsForComponent(diags, Diagnostic.ERROR);
	}

	/**
	 * Return the field indicator type of error.
	 *
	 * @return the field indicator type
	 */
	@Override
	public FieldIndicatorType getFieldIndicatorType() {
		return FieldIndicatorType.ERROR;
	}
}
