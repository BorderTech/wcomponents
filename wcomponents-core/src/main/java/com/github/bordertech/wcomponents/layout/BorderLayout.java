package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.util.GapSizeUtil;

/**
 * BorderLayout is a {@link LayoutManager} that emulates {@link java.awt.BorderLayout}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 * @deprecated WComponents 1.1.4. Use {@link com.github.bordertech.wcomponents.WRow} and
 * {@link com.github.bordertech.wcomponents.WColumn} or {@link com.github.bordertech.wcomponents.layout.ColumnLayout} instead.
 * It is preferred that an application use {@link com.github.bordertech.wcomponents.WTemplate} for layout as this
 * provides for lighter payloads and more responsive UIs.
 */
public class BorderLayout implements LayoutManager {

	/**
	 * The north layout constraint (top of container).
	 */
	public static final BorderLayoutConstraint NORTH = BorderLayoutConstraint.NORTH;

	/**
	 * The south layout constraint (bottom of container).
	 */
	public static final BorderLayoutConstraint SOUTH = BorderLayoutConstraint.SOUTH;

	/**
	 * The east layout constraint (right side of container).
	 */
	public static final BorderLayoutConstraint EAST = BorderLayoutConstraint.EAST;

	/**
	 * The west layout constraint (left side of container).
	 */
	public static final BorderLayoutConstraint WEST = BorderLayoutConstraint.WEST;

	/**
	 * The center layout constraint (middle of container).
	 */
	public static final BorderLayoutConstraint CENTER = BorderLayoutConstraint.CENTER;

	/**
	 * An enumeration of the possible locations for content within a border layout.
	 */
	public enum BorderLayoutConstraint {
		/**
		 * North (top) positioning.
		 */
		NORTH,
		/**
		 * South (bottom) positioning.
		 */
		SOUTH,
		/**
		 * East (right) positioning.
		 */
		EAST,
		/**
		 * West (left) positioning.
		 */
		WEST,
		/**
		 * Center positioning.
		 */
		CENTER
	};

	/**
	 * The horizontal gap between the west, center and east cells, measured in pixels.
	 */
	private final GapSizeUtil.Size hgap;

	/**
	 * The vertical gap between the north cell, middle row and south cell, measured in pixels.
	 */
	private final GapSizeUtil.Size vgap;

	/**
	 * Creates a border layout.
	 */
	public BorderLayout() {
		this(null, null);
	}

	/**
	 * Creates a border layout with the gap between component areas.
	 * <p>
	 * The horizontal and vertical gaps are set to the specified values. Horizontal gaps are placed at the left and
	 * right edges, and between each of the columns. Vertical gaps are placed at the top and bottom edges, and between
	 * each of the rows.
	 *
	 * @param hgap the horizontal gap between the west, center and east cells, measured in pixels.
	 * @param vgap the vertical gap between the north cell, middle row and south cell, measured in pixels
	 * @deprecated use {@link #BorderLayout(GapSizeUtil.Size, GapSizeUtil.Size)} instead.
	 */
	@Deprecated
	public BorderLayout(final int hgap, final int vgap) {
		this (GapSizeUtil.intToSize(hgap), GapSizeUtil.intToSize(vgap));
	}
	/**
	 * Creates a border layout with the gap between component areas.
	 * <p>
	 * The horizontal and vertical gaps are set to the specified values. Horizontal gaps are placed at the left and
	 * right edges, and between each of the columns. Vertical gaps are placed at the top and bottom edges, and between
	 * each of the rows.
	 * <p>
	 * All <code>BorderLayout</code> constructors defer to this one.
	 *
	 * @param hgap the horizontal gap between the west, center and east cells
	 * @param vgap the vertical gap between the north cell, middle row and south cell
	 */
	public BorderLayout(final GapSizeUtil.Size hgap, final GapSizeUtil.Size vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * @return Returns the horizontal gap between the cells, measured in pixels.
	 */
	public GapSizeUtil.Size getHgap() {
		return hgap;
	}

	/**
	 * @return Returns the vertical gap between the cells, measured in pixels.
	 */
	public GapSizeUtil.Size getVgap() {
		return vgap;
	}
}
