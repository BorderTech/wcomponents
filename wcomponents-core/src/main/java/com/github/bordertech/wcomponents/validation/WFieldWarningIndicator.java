package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.WComponent;
import java.util.List;

/**
 * <p>
 * This component is used to render warnings relating to an individual field. Generally, this is placed next to the
 * input field to help highlight the individual fields that have warnings. An instance of the indicator is automatically
 * provided when adding a field to a WFieldLayout.</p>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WFieldWarningIndicator extends AbstractWFieldIndicator {

	/**
	 * Just calls the super classes constructor.
	 *
	 * @param relatedField The related field.
	 */
	public WFieldWarningIndicator(final WComponent relatedField) {
		super(relatedField);
	}

	/**
	 * Calls {@link #showIndicatorsForComponent(List, int)} with a {@link Diagnostic#WARNING}.
	 *
	 * @param diags A List of Diagnostic objects.
	 */
	@Override
	protected void showWarningIndicatorsForComponent(final List<Diagnostic> diags) {
		showIndicatorsForComponent(diags, Diagnostic.WARNING);
	}

	/**
	 * Return the field indicator type of warn.
	 *
	 * @return the field indicator type
	 */
	@Override
	public FieldIndicatorType getFieldIndicatorType() {
		return FieldIndicatorType.WARN;
	}

}
