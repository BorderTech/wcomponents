package com.github.bordertech.wcomponents;

/**
 * This is a layout component to be used in conjunction with WRow.
 *
 * @author Ming Gao
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WColumn extends AbstractMutableContainer implements AjaxTarget {

	/**
	 * Describes how content within a column should be aligned.
	 */
	public enum Alignment {
		/**
		 * Indicates that content should be left-aligned. This is the default alignment.
		 */
		LEFT,
		/**
		 * Indicates that content should be horizontally centered in the column.
		 */
		CENTER,
		/**
		 * Indicates that content should be right-aligned.
		 */
		RIGHT
	}

	/**
	 * Creates a WColumn with undefined width.
	 */
	public WColumn() {
	}

	/**
	 * Creates a WColumn with the width specified as a percentage of the total available width.
	 *
	 * @param widthPercentage the percentage width, 0 for undefined.
	 */
	public WColumn(final int widthPercentage) {
		setWidth(widthPercentage);
	}

	/**
	 * Sets the relative column width, measured as a percentage of the total available width.
	 *
	 * @param widthPercent the column width, 0 for undefined.
	 */
	public void setWidth(final int widthPercent) {
		if (widthPercent < 0 || widthPercent > 100) {
			throw new IllegalArgumentException(
					"Width (" + widthPercent + ") must be between 0 and 100 percent");
		}
		getOrCreateComponentModel().width = widthPercent;
	}

	/**
	 * @return the column width as a percentage of the total available width, or zero if it has not been specified.
	 */
	public int getWidth() {
		return getComponentModel().width;
	}

	/**
	 * @return Returns the alignment.
	 */
	public Alignment getAlignment() {
		return getComponentModel().alignment;
	}

	/**
	 * @param alignment The alignment to set.
	 */
	public void setAlignment(final Alignment alignment) {
		getOrCreateComponentModel().alignment = alignment == null ? Alignment.LEFT : alignment;
	}

	/**
	 * Holds the extrinsic state information of a WColumn.
	 */
	public static class ColumnModel extends ComponentModel {

		/**
		 * The column width.
		 */
		private int width = 0;

		/**
		 * The alignment of content within the column.
		 */
		private Alignment alignment = Alignment.LEFT;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		return toString(String.valueOf(getWidth()) + '%');
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new ColumnModel.
	 */
	@Override
	protected ColumnModel newComponentModel() {
		return new ColumnModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ColumnModel getComponentModel() {
		return (ColumnModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ColumnModel getOrCreateComponentModel() {
		return (ColumnModel) super.getOrCreateComponentModel();
	}
}
