package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;

/**
 * GridLayout is a {@link LayoutManager} that emulates {@link java.awt.GridLayout}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class GridLayout implements LayoutManager {

	/**
	 * The number of rows, or 0 for a dynamic number of rows based on the number of components and columns.
	 */
	private final int rows;

	/**
	 * The number of columns, or 0 for a dynamic number of columns based on the number of components and columns.
	 */
	private final int cols;

	/**
	 * The horizontal space between the columns.
	 */
	private final Size hSpace;

	/**
	 * The vertical space between the rows.
	 */
	private final Size vSpace;

	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int hgap;

	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int vgap;

	/**
	 * For temporary backwards compatibility only.
	 *
	 * @param rows the rows, with the value zero meaning any number of rows
	 * @param cols the columns, with the value zero meaning any number of columns
	 * @param hSpace the real space between columns in the grid
	 * @param vSpace the real space between rows in the gid
	 * @param hgap the requested gap between the columns
	 * @param vgap the requested gap between the rows
	 */
	@Deprecated
	private GridLayout(final int rows, final int cols, final Size hSpace, final Size vSpace, final int hgap, final int vgap) {
		if (rows < 0) {
			throw new IllegalArgumentException("Rows must be greater than or equal to zero");
		}

		if (cols < 0) {
			throw new IllegalArgumentException("Cols must be greater than or equal to zero");
		}

		if (rows == 0 && cols == 0) {
			throw new IllegalArgumentException("One of rows or cols must be greater than zero");
		}

		this.rows = rows;
		this.cols = cols;
		this.hSpace = hSpace;
		this.vSpace = vSpace;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * Creates a grid layout with the specified number of rows and columns.
	 * <p>
	 * One, but not both, of <code>rows</code> and <code>cols</code> can be zero, which means that any number of objects
	 * can be placed in a row or in a column.
	 *
	 * @param rows the rows, with the value zero meaning any number of rows.
	 * @param cols the columns, with the value zero meaning any number of columns.
	 */
	public GridLayout(final int rows, final int cols) {
		this(rows, cols, null, null);
	}

	/**
	 * Creates a grid layout with the specified number of rows and columns and spacing.
	 *
	 * @param rows the rows, with the value zero meaning any number of rows
	 * @param cols the columns, with the value zero meaning any number of columns
	 * @param hgap the space between the columns
	 * @param vgap the space between the rows
	 *
	 * @deprecated use {@link #GridLayout(int, int, Size, Size)}
	 */
	@Deprecated
	public GridLayout(final int rows, final int cols, final int hgap, final int vgap) {
		this(rows, cols, SpaceUtil.intToSize(hgap), SpaceUtil.intToSize(vgap), hgap, vgap);
	}
	/**
	 * Creates a grid layout with the specified number of rows and columns.
	 * <p>
	 * In addition, the horizontal and vertical spaces are set to the specified values. Horizontal spaces are placed between each of the columns.
	 * Vertical spaces are placed between each of the rows.
	 * <p>
	 * One, but not both, of <code>rows</code> and <code>cols</code> can be zero, which means that any number of objects can be placed in a row or in
	 * a column.
	 * <p>
	 * All <code>GridLayout</code> constructors defer to this one.
	 *
	 * @param rows the rows, with the value zero meaning any number of rows
	 * @param cols the columns, with the value zero meaning any number of columns
	 * @param hSpace the space between the columns
	 * @param vSpace the space between the rows
	 */
	public GridLayout(final int rows, final int cols, final Size hSpace, final Size vSpace) {
		if (rows < 0) {
			throw new IllegalArgumentException("Rows must be greater than or equal to zero");
		}

		if (cols < 0) {
			throw new IllegalArgumentException("Cols must be greater than or equal to zero");
		}

		if (rows == 0 && cols == 0) {
			throw new IllegalArgumentException("One of rows or cols must be greater than zero");
		}

		this.rows = rows;
		this.cols = cols;
		this.hSpace = hSpace;
		this.vSpace = vSpace;
		this.hgap = -1;
		this.vgap = -1;
	}

	/**
	 * @return the horizontal gap between the cells
	 */
	public Size getHorizontalGap() {
		return hSpace;
	}

	/**
	 * @return the vertical gap between the cells
	 */
	public Size getVerticalGap() {
		return vSpace;
	}

	/**
	 * @return the horizontal gap between the cells measured in pixels
	 * @deprecated use {@link #getHorizontalGap() }
	 */
	@Deprecated
	public int getHgap() {
		return hgap;
	}

	/**
	 * @return the vertical gap between the cells measured in pixels
	 * @deprecated use {@link #getVerticalGap() }
	 */
	@Deprecated
	public int getVgap() {
		return vgap;
	}

	/**
	 * @return the number of rows, or 0 for a dynamic number of rows based on the number of components and columns.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the number of columns, or 0 for a dynamic number of column based on the number of components and rows.
	 */
	public int getCols() {
		return cols;
	}
}
