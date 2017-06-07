package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SpaceUtil;

/**
 * This is a layout component, to which you add {@link WColumn} components. The widths of all the columns added to the
 * row should total 100.
 *
 * @author Ming Gao
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 */
public class WRow extends AbstractNamingContextContainer implements AjaxTarget, SubordinateTarget,
		Marginable {

	/**
	 * The horizontal space between the columns in the row, measured in pixels.
	 */
	private final Size space;

	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int gap;

	/**
	 * For temporary backwards compatibility only.
	 * @param space the real space between items
	 * @param gap the requested space between items
	 */
	@Deprecated
	private WRow(final Size space, final int gap) {
		this.space = space;
		this.gap = gap;
	}

	/**
	 * Creates a WRow.
	 */
	public WRow() {
		this(null);
	}

	/**
	 * Create a WRow with the specified space between columns.
	 * @param space the space between columns in the row
	 */
	public WRow(final Size space) {
		this.space = space;
		this.gap = -1;
	}

	/**
	 * Creates a WRow with the specified attributes.
	 *
	 * @param hgap the space between the columns in the row
	 * @deprecated use {@link #WRow(com.github.bordertech.wcomponents.Size)}
	 */
	@Deprecated
	public WRow(final int hgap) {
		this(SpaceUtil.intToSize(hgap), hgap);
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
	 * @return the horizontal space between the columns in the row, measured in pixels
	 * @deprecated use {@link #getGap()}
	 */
	@Deprecated
	public int getHgap() {
		return gap;
	}

	/**
	 * @return the horizontal space between the columns in the row
	 */
	public Size getSpace() {
		return space;
	}

	/**
	 * @return the space between the components added to the layout.
	 */
	@Deprecated
	public int getGap() {
		return gap;
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
