package com.github.bordertech.wcomponents.layout;

/**
 * ListLayout renders out its items in a list.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ListLayout implements LayoutManager {

	/**
	 * An enumeration of possible values for horizontal alignment of column content.
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
	 * An enumeration of possible values for the list type.
	 */
	public enum Type {
		/**
		 * Indicates that content should be arranged horizontally.
		 */
		FLAT,
		/**
		 * Indicates that content should be arranged vertically.
		 */
		STACKED,
		/**
		 * Indicates that content should be arranged vertically, with highlighting on alternate rows (zebra striping).
		 */
		STRIPED
	}

	/**
	 * An enumeration of possible values for the item separator.
	 */
	public enum Separator {
		/**
		 * Indicates that no separator should be used.
		 */
		NONE,
		/**
		 * Indicates that the separator should be a bar (vertical line).
		 */
		BAR,
		/**
		 * Indicates that the separator should be a dot.
		 */
		DOT
	}

	/**
	 * The list alignment.
	 */
	private final Alignment alignment;

	/**
	 * The list type.
	 */
	private final Type type;

	/**
	 * The list separator.
	 */
	private final Separator separator;

	/**
	 * The list alignment.
	 */
	private final boolean ordered;

	/**
	 * The horizontal gap between the list items, measured in pixels.
	 */
	private final int hgap;

	/**
	 * The vertical gap between the list items, measured in pixels.
	 */
	private final int vgap;

	/**
	 * Creates a ListLayout with the specified attributes.
	 *
	 * @param type the list type.
	 * @param alignment the item alignment.
	 * @param separator the separator to display between items.
	 * @param ordered whether the list is an ordered list.
	 */
	public ListLayout(final Type type, final Alignment alignment, final Separator separator,
			final boolean ordered) {
		this(type, alignment, separator, ordered, 0, 0);
	}

	/**
	 * Creates a ListLayout with the specified attributes.
	 *
	 * @param type the list type.
	 * @param alignment the item alignment.
	 * @param separator the separator to display between items.
	 * @param ordered whether the list is an ordered list.
	 * @param hgap the horizontal gap between the list items, measured in pixels.
	 * @param vgap the vertical gap between the list items, measured in pixels.
	 */
	public ListLayout(final Type type, final Alignment alignment, final Separator separator,
			final boolean ordered, final int hgap, final int vgap) {
		this.type = type;
		this.alignment = alignment;
		this.separator = separator;
		this.ordered = ordered;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * @return the list alignment.
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @return the list type.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the list separator.
	 */
	public Separator getSeparator() {
		return separator;
	}

	/**
	 * @return true for an ordered list, false for unordered.
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * @return the horizontal gap between the list items, measured in pixels.
	 */
	public int getHgap() {
		return hgap;
	}

	/**
	 * @return the vertical gap between the list items, measured in pixels.
	 */
	public int getVgap() {
		return vgap;
	}
}
