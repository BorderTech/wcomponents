package com.github.bordertech.wcomponents;

/**
 * This is a layout component, to which you add {@link WColumn} components. The widths of all the columns added to the
 * row should total 100.
 *
 * @author Ming Gao
 * @author Yiannis Paschalidis
 */
public class WRow extends AbstractNamingContextContainer implements AjaxTarget, SubordinateTarget,
		Marginable {

	/**
	 * The horizontal gap between the columns in the row, measured in pixels.
	 */
	private final int hgap;

	/**
	 * Creates a WRow.
	 */
	public WRow() {
		this(0);
	}

	/**
	 * Creates a WRow with the specified attributes.
	 *
	 * @param hgap the horizontal gap between the columns in the row, measured in pixels
	 */
	public WRow(final int hgap) {
		this.hgap = hgap;
	}

	/**
	 * Adds the given column as a child of this component.
	 *
	 * @param column the column to add.
	 */
	public void add(final WColumn column) {
		super.add(column);
	}

	/**
	 * Removes the given column from this components list of children.
	 *
	 * @param column the column to remove
	 */
	public void remove(final WColumn column) {
		super.remove(column);
	}

	/**
	 * @return the horizontal gap between the columns in the row, measured in pixels
	 */
	public int getHgap() {
		return hgap;
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
	 * Creates a new Component model.
	 *
	 * @return a new RowModel.
	 */
	@Override
	// For type safety only
	protected RowModel newComponentModel() {
		return new RowModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected RowModel getComponentModel() {
		return (RowModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected RowModel getOrCreateComponentModel() {
		return (RowModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 */
	public static class RowModel extends ComponentModel {

		/**
		 * The margins to be used on the section.
		 */
		private Margin margin;
	}

}
