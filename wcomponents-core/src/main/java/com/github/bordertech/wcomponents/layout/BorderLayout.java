package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;

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
@Deprecated
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
	private final Size hSpace;

	/**
	 * The vertical gap between the north cell, middle row and south cell, measured in pixels.
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
	 * @param hSpace the real horizontal space between cells
	 * @param vSpace the real vertical space between rows of cells
	 * @param hgap the requested horizontal space between cells
	 * @param vgap the requested vertical space between rows of cells
	 */
	@Deprecated
	private BorderLayout(final Size hSpace, final Size vSpace, final int hgap, final int vgap) {
		this.hSpace = hSpace;
		this.vSpace = vSpace;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * Creates a border layout.
	 */
	public BorderLayout() {
		this(null, null);
	}

	/**
	 * Creates a border layout with the gap between component areas.
	 *
	 * @param hgap the horizontal space between the west, center and east cells
	 * @param vgap the vertical space between the north cell, middle row and south cell
	 * @deprecated use {@link #BorderLayout(Size, Size)} instead.
	 */
	@Deprecated
	public BorderLayout(final int hgap, final int vgap) {
		this (SpaceUtil.intToSize(hgap), SpaceUtil.intToSize(vgap), hgap, vgap);
	}
	/**
	 * Creates a border layout with the gap between component areas.
	 *
	 * @param hSpace the horizontal space between the west, center and east cells
	 * @param vSpace the vertical space between the north cell, middle row and south cell
	 */
	public BorderLayout(final Size hSpace, final Size vSpace) {
		this.hSpace = hSpace;
		this.vSpace = vSpace;
		this.hgap = -1;
		this.vgap = -1;
	}

	/**
	 * @return the horizontal space between the cells
	 */
	public Size getHorizontalGap() {
		return hSpace;
	}

	/**
	 * @return the vertical space between the cells
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
	 * @return the vertical gap between the cells measured in pixels.
	 * @deprecated use {@link #getVerticalGap() }
	 */
	@Deprecated
	public int getVgap() {
		return vgap;
	}
}
