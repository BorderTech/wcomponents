package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SpaceUtil;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int oldAll;
	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int north;
	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int east;
	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int south;
	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int west;
	/**
	 * For temporary backwards compatibility only.
	 * @param all the real size of the margin
	 * @param oldAll the requested size of the margin
	 */
	@Deprecated
	private Margin(final Size all, final int oldAll) {
		this.all = all;
		this.top = null;
		this.right = null;
		this.bottom = null;
		this.left = null;
		this.oldAll = oldAll;
		this.north = -1;
		this.east = -1;
		this.south = -1;
		this.west = -1;
	}

	/**
	 * For temporary backwards compatibility only.
	 * @param top the real top margin
	 * @param right the real right margin
	 * @param bottom the real bottom margin
	 * @param left the real left margin
	 * @param north the requested north margin
	 * @param east the requested east margin
	 * @param south the requested south margin
	 * @param west the requested west margin
	 * @deprecated
	 */
	@Deprecated
	private Margin(final Size top, final Size right, final Size bottom, final Size left, final int north,
			final int east, final int south, final int west) {
		this.all = null;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		this.oldAll = -1;
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
	}
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
		this.oldAll = -1;
		this.north = -1;
		this.east = -1;
		this.south = -1;
		this.west = -1;
	}

	/**
	 * @param all the size of the margin to be used on all sides of the component.
	 * @deprecated use {@link #Margin(Size)}
	 */
	@Deprecated
	public Margin(final int all) {
		this(SpaceUtil.intToSize(all), all);
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
		this.oldAll = -1;
		this.north = -1;
		this.east = -1;
		this.south = -1;
		this.west = -1;
	}

	/**
	 * The margin sizes to be used on each side of the panel.
	 *
	 * @param north the size of the north margin.
	 * @param east the size of the east margin.
	 * @param south the size of the south margin.
	 * @param west the size of the west margin.
	 * @deprecated use {@link #Margin(Size, Size, Size, Size)}
	 */
	@Deprecated
	public Margin(final int north, final int east, final int south, final int west) {
		this(SpaceUtil.intToSize(north), SpaceUtil.intToSize(east), SpaceUtil.intToSize(south), SpaceUtil.intToSize(west), north, east, south, west);
	}

	/**
	 * @return the size of the margin to be used on all sides of the panel, or -1 if it has not been set.
	 * @deprecated use {@link #getMargin()}
	 */
	@Deprecated
	public int getAll() {
		return oldAll;
	}

	/**
	 * @return the size of the north margin, or -1 if it has not been set.
	 * @deprecated use {@link #getTop()}
	 */
	@Deprecated
	public int getNorth() {
		return north;
	}

	/**
	 * @return the size of the east margin, or -1 if it has not been set.
	 * @deprecated use {@link #getRight()}
	 */
	@Deprecated
	public int getEast() {
		return east;
	}

	/**
	 * @return the size of the south margin, or -1 if it has not been set.
	 * @deprecated use {@link #getBottom()}
	 */
	@Deprecated
	public int getSouth() {
		return south;
	}

	/**
	 * @return the size of the west margin, or -1 if it has not been set.
	 * @deprecated use {@link #getLeft()}
	 */
	@Deprecated
	public int getWest() {
		return west;
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

	/**
	 * Get a set of HTML class attribute values in the same form as {@link AbstractWComponent#getHtmlClasses}.
	 *
	 * @return the Margin expressed as a set of HTML class attribute values.
	 */
	public final Set<String> getAsHTMLClassSet() {
		Set<String> htmlClasses = new HashSet<>();
		final String htmlClassPrefix = "wc-margin-";
		final String topClassModifier = "n-";
		final String rightClassModifier = "e-";
		final String bottomClassModifier = "s-";
		final String leftClassModifier = "w-";

		Size current = getTop();
		if (current != null) {
			htmlClasses.add(htmlClassPrefix.concat(topClassModifier).concat(current.toString()));
		}
		current = getRight();
		if (current != null) {
			htmlClasses.add(htmlClassPrefix.concat(rightClassModifier).concat(current.toString()));
		}
		current = getBottom();
		if (current != null) {
			htmlClasses.add(htmlClassPrefix.concat(bottomClassModifier).concat(current.toString()));
		}
		current = getLeft();
		if (current != null) {
			htmlClasses.add(htmlClassPrefix.concat(leftClassModifier).concat(current.toString()));
		}

		if (htmlClasses.isEmpty()) {
			return null;
		}
		return htmlClasses;
	}

	/**
	 * @return the margin expressed as a set of HTML class attribute values.
	 */
	public final String getAsHtmlClassValue() {
		Set<String> htmlClasses = getAsHTMLClassSet();
		final String separator = " ";

		if (htmlClasses == null) {
			return null;
		}
		// NOTE: htmlClasses cannot be empty - see `getAsHTMLClassSet` above.
		StringBuilder builder = new StringBuilder(htmlClasses.size());
		for (String current : htmlClasses) {
			builder.append(current);
			builder.append(separator);
		}
		return builder.toString().trim();
	}
}
