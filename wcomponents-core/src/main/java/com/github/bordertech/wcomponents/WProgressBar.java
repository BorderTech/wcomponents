package com.github.bordertech.wcomponents;

/**
 * <p>
 * WProgressBar is a component for displaying progress bars. The number of steps in the progress bar is configurable,
 * and the progress bar's value can either be set manually, or sourced from an Integer bean.</p>
 *
 * <p>
 * Methods are available to customise the progress bar's appearance, such as setting its size or whether to display the
 * progress as a fraction or percentage.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WProgressBar extends WBeanComponent implements AjaxTarget {

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
	 * Display unit types.
	 */
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
	 * Creates a progress bar with the type of "normal" and a fraction unit type.
	 */
	public WProgressBar() {
		this(ProgressBarType.NORMAL, UnitType.FRACTION);
	}

	/**
	 * Creates a progress bar with the type of "normal", a fraction unit type and the given maximum value.
	 *
	 * @param max the maximum value
	 */
	public WProgressBar(final int max) {
		this(ProgressBarType.NORMAL, UnitType.FRACTION, max);
	}

	/**
	 * Creates a progress bar with the given bar and unit types.
	 *
	 * @param type the progress bar type.
	 * @param unitType the display unit type.
	 */
	public WProgressBar(final ProgressBarType type, final UnitType unitType) {
		setProgressBarType(type);
		setUnitType(unitType);
	}

	/**
	 * Creates a progress bar with the given bar type, unit type and max value.
	 *
	 * @param type the progress bar type.
	 * @param unitType the display unit type.
	 * @param max the maximum value
	 */
	public WProgressBar(final ProgressBarType type, final UnitType unitType, final int max) {
		setProgressBarType(type);
		setUnitType(unitType);
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
	public void setMax(final int max) {
		getOrCreateComponentModel().max = max;
	}

	/**
	 * Sets the value of the progress bar for the given context.
	 *
	 * @param value the progress bar value.
	 */
	public void setValue(final int value) {
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
	public void setProgressBarType(final ProgressBarType type) {
		getOrCreateComponentModel().barType = type;
	}

	/**
	 * Retrieves the display unit type.
	 *
	 * @return the unit type.
	 */
	public UnitType getUnitType() {
		return getComponentModel().unitType;
	}

	/**
	 * Sets the display unit type.
	 *
	 * @param unitType the unit type.
	 */
	public void setUnitType(final UnitType unitType) {
		getOrCreateComponentModel().unitType = unitType;
	}

	/**
	 * @return the progress bar text.
	 */
	public String getText() {
		return getComponentModel().text;
	}

	/**
	 * Sets the progress bar text.
	 *
	 * @param text the text to set.
	 */
	public void setText(final String text) {
		getOrCreateComponentModel().text = text;
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

		private ProgressBarType barType;
		private UnitType unitType;
		private int max;
		private String text;
	}
}
