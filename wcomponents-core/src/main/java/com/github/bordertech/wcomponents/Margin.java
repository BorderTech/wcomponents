package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.GapSizeUtil;
import com.github.bordertech.wcomponents.util.GapSizeUtil.Size;
import java.io.Serializable;

/**
 * The margins to be used on a component.
 * <p>
 * A default margin size can be set for "all" sides of the panel, or the specific margin sizes can be set for each side
 * of the panel.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Margin implements Serializable {

	/**
	 * The size of the margins on all sides of the panel.
	 */
	private final Size all;
	/**
	 * The size of the north margin.
	 */
	private final Size top;
	/**
	 * The size of the east margin.
	 */
	private final Size right;
	/**
	 * The size of the south margin.
	 */
	private final Size bottom;
	/**
	 * The size of the west margin.
	 */
	private final Size left;

	/**
	 * A margin equal on all sizes.
	 * @param all the size of the margin to be used on all sides of the component.
	 */
	public Margin(final Size all) {
		this.all = all;
		this.top = null;
		this.right = null;
		this.bottom = null;
		this.left = null;
	}

	/**
	 * @param all the size of the margin to be used on all sides of the component.
	 * @deprecated 1.4.0 use {@link #Margin(Size)}
	 */
	public Margin(final int all) {
		this(GapSizeUtil.intToSize(all));
	}

	/**
	 * The margin sizes to be used on each side of the component.
	 *
	 * @param north the size of the north margin.
	 * @param east the size of the east margin.
	 * @param south the size of the south margin.
	 * @param west the size of the west margin.
	 */
	public Margin(final Size north, final Size east, final Size south, final Size west) {
		this.all = null;
		this.top = north;
		this.right = east;
		this.bottom = south;
		this.left = west;
	}

	/**
	 * The margin sizes to be used on each side of the panel.
	 *
	 * @param north the size of the north margin.
	 * @param east the size of the east margin.
	 * @param south the size of the south margin.
	 * @param west the size of the west margin.
	 * @deprecated 1.4.0 use {@link #Margin(Size, Size, Size, Size)}
	 */
	public Margin(final int north, final int east, final int south, final int west) {
		this(GapSizeUtil.intToSize(north), GapSizeUtil.intToSize(east), GapSizeUtil.intToSize(south), GapSizeUtil.intToSize(west));
	}

	/**
	 * @return the size of the margin to be used on all sides of the panel, or -1 if it has not been set.
	 * @deprecated 1.4.0 do not use.
	 */
	public int getAll() {
		return GapSizeUtil.sizeToInt(all);
	}

	/**
	 * @return the size of the north margin, or -1 if it has not been set.
	 * @deprecated 1.4.0 do not use.
	 */
	public int getNorth() {
		return GapSizeUtil.sizeToInt(top);
	}

	/**
	 * @return the size of the east margin, or -1 if it has not been set.
	 * @deprecated 1.4.0 do not use.
	 */
	public int getEast() {
		return GapSizeUtil.sizeToInt(right);
	}

	/**
	 * @return the size of the south margin, or -1 if it has not been set.
	 * @deprecated 1.4.0 do not use.
	 */
	public int getSouth() {
		return GapSizeUtil.sizeToInt(bottom);
	}

	/**
	 * @return the size of the west margin, or -1 if it has not been set.
	 * @deprecated 1.4.0 do not use.
	 */
	public int getWest() {
		return GapSizeUtil.sizeToInt(left);
	}

	/**
	 * @return the margin on all sides of the container.
	 */
	public Size getMargin() {
		return all;
	}

	/**
	 * @return the margin on the top of the container.
	 */
	public Size getTop() {
		return top;
	}

	/**
	 * @return the margin on the east side of the container.
	 */
	public Size getRight() {
		return right;
	}

	/**
	 * @return the margin on the south side of the container.
	 */
	public Size getBottom() {
		return bottom;
	}

	/**
	 * @return the margin on the west side of the container.
	 */
	public Size getLeft() {
		return left;
	}
}
