package com.github.bordertech.wcomponents;

/**
 * This is a layout component to be used in conjunction with WRow.
 *
 * @author Ming Gao
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
	 * Creates a WColumn with 1% width.
	 *
	 * @deprecated Use {{@link #WColumn(int)} instead as width is required. Will default width to 1%.
	 */
	@Deprecated
	public WColumn() {
		setWidth(1);
	}

	/**
	 * Creates a WColumn with the width specified as a percentage of the total available width.
	 *
	 * @param widthPercentage the percentage width.
	 */
	public WColumn(final int widthPercentage) {
		setWidth(widthPercentage);
	}

	/**
	 * Sets the relative column width, measured as a percentage of the total available width.
	 *
	 * @param widthPercent the column width.
	 */
	public void setWidth(final int widthPercent) {
		if (widthPercent < 1 || widthPercent > 100) {
			throw new IllegalArgumentException(
					"Width (" + widthPercent + ") must be between 1 and 100 percent");
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
		private int width;

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
