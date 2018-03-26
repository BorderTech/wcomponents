package com.github.bordertech.wcomponents;

/**
 * <p>
 * WProgressBar is a component for displaying progress bars. The number of steps in the progress bar is configurable,
 * and the progress bar's value can either be set manually, or sourced from an Integer bean.</p>
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WProgressBar extends WBeanComponent implements AjaxTarget, Labelable {

	/**
	 * Message when a max argument is not a valid value.
	 */
	private static final String ILLEGAL_MAX = "Max may not be negative";
	/**
	 * Message when a progress bar type argument is not a valid value.
	 */
	private static final String ILLEGAL_TYPE = "Type must not be null";

	/**
	 * Message when a value argument is not a valid value.
	 */
	private static final String ILLEGAL_VALUE = "Value may not be negative";

	/**
	 * Progress bar types.
	 */
	public enum ProgressBarType {
		/**
		 * A normal progress bar.
		 */
		NORMAL,
		/**
		 * A smaller progress bar.
		 */
		SMALL
	}

	/**
	 * The default type for a WProgressBar.
	 */
	public static final ProgressBarType DEFAULT_TYPE = ProgressBarType.NORMAL;

	/**
	 * Display unit types.
	 * @deprecated - not implemented in HTML spec - do not use
	 */
	@Deprecated
	public enum UnitType {
		/**
		 * Units will be displayed as a fraction, e.g. <code>33/100</code>.
		 */
		FRACTION,
		/**
		 * Units will be displayed as a percentage, e.g. <code>33%</code>.
		 */
		PERCENTAGE
	}

	/**
	 * Creates a normal progress bar.
	 */
	public WProgressBar() {
	}

	/**
	 * Creates a progress bar with the given maximum value.
	 *
	 * @param max the maximum value
	 */
	public WProgressBar(final int max) {
		if (max < 0) {
			throw new IllegalArgumentException(ILLEGAL_MAX);
		}
		setMax(max);
	}

	/**
	 * Creates a progress bar with the given bar type.
	 *
	 * @param type the progress bar type.
	 * @param unitType the display unit type.
	 * @deprecated use {@link #WProgressBar(com.github.bordertech.wcomponents.WProgressBar.ProgressBarType)}
	 */
	@Deprecated
	public WProgressBar(final ProgressBarType type, final UnitType unitType) {
		this(type);
	}

	/**
	 * Creates a progress bar with the given bar type.
	 *
	 * @param type the progress bar type.
	 */
	public WProgressBar(final ProgressBarType type) {
		if (type == null) {
			throw new IllegalArgumentException(ILLEGAL_TYPE);
		}
		setProgressBarType(type);
	}

	/**
	 * Creates a progress bar with the given bar type, unit type and max value.
	 *
	 * @param type the progress bar type.
	 * @param unitType the display unit type.
	 * @param max the maximum value
	 * @deprecated use {@link #WProgressBar(com.github.bordertech.wcomponents.WProgressBar.ProgressBarType, int)}
	 */
	@Deprecated
	public WProgressBar(final ProgressBarType type, final UnitType unitType, final int max) {
		this(type, max);
	}

	/**
	 * Creates a progress bar with the given bar type and max value.
	 *
	 * @param type the progress bar type.
	 * @param max the maximum value
	 */
	public WProgressBar(final ProgressBarType type, final int max) {
		if (type == null) {
			throw new IllegalArgumentException(ILLEGAL_TYPE);
		}
		if (max < 0) {
			throw new IllegalArgumentException(ILLEGAL_MAX);
		}
		setProgressBarType(type);
		setMax(max);
	}

	/**
	 * @return the maximum value of the progress bar.
	 */
	public int getMax() {
		return getComponentModel().max;
	}

	/**
	 * Sets the maximum value of the progress bar.
	 *
	 * @param max the maximum allowable value.
	 */
	public final void setMax(final int max) {
		if (max < 0) {
			throw new IllegalArgumentException(ILLEGAL_MAX);
		}
		if (max != getMax()) {
			getOrCreateComponentModel().max = max;
		}
	}

	/**
	 * Sets the value of the progress bar for the given context.
	 *
	 * @param value the progress bar value.
	 */
	public void setValue(final int value) {
		if (value < 0) {
			throw new IllegalArgumentException(ILLEGAL_VALUE);
		}
		setData(value == 0 ? null : value);
	}

	/**
	 * Retrieves the value of the progress bar.
	 *
	 * @return the progress bar's value for the context.
	 */
	public int getValue() {
		int max = getMax();
		Integer data = (Integer) getData();
		return data == null ? 0 : Math.max(0, Math.min(max, data));
	}

	/**
	 * @return the progress bar type.
	 */
	public ProgressBarType getProgressBarType() {
		return getComponentModel().barType;
	}

	/**
	 * Sets the progress bar type.
	 *
	 * @param type the progress bar type.
	 */
	public final void setProgressBarType(final ProgressBarType type) {
		ProgressBarType currentType = getProgressBarType();
		ProgressBarType typeToSet = type == null ? DEFAULT_TYPE : type;
		if (typeToSet != currentType) {
			getOrCreateComponentModel().barType = typeToSet;
		}
	}

	/**
	 * Retrieves the display unit type.
	 *
	 * @return the unit type.
	 * @deprecated unitType not supported in HTML spec
	 */
	@Deprecated
	public final UnitType getUnitType() {
		return null;
	}

	/**
	 * Sets the display unit type.
	 *
	 * @param unitType the unit type.
	 * @deprecated unitType not supported in HTML spec - now a no-op
	 */
	@Deprecated
	public void setUnitType(final UnitType unitType) {
		// no-op
	}

	/**
	 * @return the progress bar text.
	 * @deprecated progress elements contain no text use {@link #getToolTip()} instead
	 */
	@Deprecated
	public String getText() {
		return null;
	}

	/**
	 * Sets the progress bar text.
	 *
	 * @param text the text to set.
	 * @deprecated progress bars contain no text use {@link #setToolTip(java.lang.String, java.io.Serializable...)} instead
	 */
	@Deprecated
	public void setText(final String text) {
		// no-op
	}

	@Override
	public String toString() {
		return toString(String.valueOf(getValue()) + '/' + getMax());
	}

	/**
	 * Creates a new Component model appropriate for this component.
	 *
	 * @return a new WProgressBarModel.
	 */
	@Override
	protected WProgressBarModel newComponentModel() {
		return new WProgressBarModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected WProgressBarModel getOrCreateComponentModel() {
		return (WProgressBarModel) super.getOrCreateComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected WProgressBarModel getComponentModel() {
		return (WProgressBarModel) super.getComponentModel();
	}

	/**
	 * The component model that holds progress bar's state.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class WProgressBarModel extends BeanAndProviderBoundComponentModel {

		private ProgressBarType barType = DEFAULT_TYPE;
		private int max;
	}
}
