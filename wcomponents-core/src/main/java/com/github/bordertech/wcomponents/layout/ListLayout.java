package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;

/**
 * ListLayout renders out its items in a list.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class ListLayout implements LayoutManager {

	/**
	 * An enumeration of possible values for horizontal alignment of the components added to the layout relative to the
	 * containing WPanel.
	 */
	public enum Alignment {
		/**
		 * Indicates that content should be left-aligned. This is the default alignment.
		 */
		LEFT,
		/**
		 * Indicates that content should be horizontally centered in the WPanel.
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
	 * An enumeration of possible values for the item separator. When the ListLayout is ordered the separator is the
	 * default ordering separator unless specifically set to Separator.NONE.
	 */
	public enum Separator {
		/**
		 * Indicates that no separator should be used.
		 */
		NONE,
		/**
		 * Indicates that the separator should be a bar (vertical line). Not used when the ListLayout is ordered.
		 */
		BAR,
		/**
		 * Indicates that the separator should be a dot. Not used when the ListLayout is ordered.
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
	 * The space between the components added to the layout.
	 */
	private final Size space;

	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int gap;

	/**
	 * For temporary backwards compatibility only.
	 *
	 * @param type the list type
	 * @param alignment the item alignment
	 * @param separator the separator to display between items
	 * @param ordered whether the list is an ordered list
	 * @param space the real space between the components added to the layouts
	 * @param gap the requested gap between the components added to the layouts
	 */
	@Deprecated
	private ListLayout(final Type type, final Alignment alignment, final Separator separator, final boolean ordered, final Size space,
			final int gap) {
		if (type == null) {
			throw new IllegalArgumentException("Type must be provided.");
		}
		this.type = type;
		this.alignment = alignment;
		this.separator = separator;
		this.ordered = ordered;
		this.space = space;
		this.gap = gap;
	}

	/**
	 * Default constructor creates an unordered, LEFT aligned, DOT separated, STACKED list - i.e. a pretty much default
	 * HTML list.
	 */
	public ListLayout() {
		this(Type.STACKED, Alignment.LEFT, Separator.DOT, false, null);
	}

	/**
	 * Creates a ListLayout with a specified type. The ListLayout will be unordered, leftAligned, and DOT separated.
	 * @param type The type of list to create.
	 */
	public ListLayout(final Type type) {
		this(type, Alignment.LEFT, Separator.DOT, false, null);
	}

	/**
	 * Creates a stacked ListLayout with a specified ordered setting. The ListLayout will be stacked, left aligned, and
	 * DOT separated (if not ordered) or numbered (if ordered).
	 * @param ordered true to create an ordered list.
	 */
	public ListLayout(final boolean ordered) {
		this(Type.STACKED, Alignment.LEFT, Separator.DOT, ordered, null);
	}

	/**
	 * Creates a ListLayout with a specified type. The ListLayout will be unordered, leftAligned, and DOT separated.
	 * @param type he type of list to create
	 * @param alignment the alignment of the list relative to its containing WPanel.
	 */
	public ListLayout(final Type type, final Alignment alignment) {
		this(type, alignment, Separator.DOT, false, null);
	}

	/**
	 * Creates a ListLayout with the specified attributes.
	 *
	 * @param type the list type.
	 * @param alignment the item alignment.
	 * @param separator the separator to display between items.
	 * @param ordered whether the list is an ordered list.
	 */
	public ListLayout(final Type type, final Alignment alignment, final Separator separator, final boolean ordered) {
		this(type, alignment, separator, ordered, null);
	}

	/**
	 * Creates a ListLayout with the specified attributes.
	 *
	 * @param type the list type.
	 * @param alignment the item alignment.
	 * @param separator the separator to display between items.
	 * @param ordered whether the list is an ordered list.
	 * @param hgap The horizontal gap between the list items. Used only when type is Type.FLAT.
	 * @param vgap The vertical gap between the list items. Used only when type is not Type.FLAT.
	 *
	 * @deprecated use {@link #ListLayout(Type, Alignment, Separator, boolean, Size)}
	 */
	@Deprecated
	public ListLayout(final Type type, final Alignment alignment, final Separator separator, final boolean ordered, final int hgap, final int vgap) {
		this(type, alignment, separator, ordered,
				type == Type.FLAT ? SpaceUtil.intToSize(hgap) : SpaceUtil.intToSize(vgap), type == Type.FLAT ? hgap : vgap);
	}

	/**
	 * Creates a ListLayout with the specified attributes.
	 *
	 * @param type the list type
	 * @param alignment the item alignment
	 * @param separator the separator to display between items
	 * @param ordered whether the list is an ordered list
	 * @param gap the gap between the components added to the layouts
	 * @deprecated use {@link #ListLayout(Type, Alignment, Separator, boolean, Size)}
	 */
	@Deprecated
	public ListLayout(final Type type, final Alignment alignment, final Separator separator, final boolean ordered, final int gap) {
		this(type, alignment, separator, ordered, SpaceUtil.intToSize(gap), gap);
	}

	/**
	 * Creates a ListLayout with the specified attributes.
	 *
	 * @param type the list type
	 * @param alignment the item alignment
	 * @param separator the separator to display between items
	 * @param ordered whether the list is an ordered list
	 * @param space the space between the components added to the layouts
	 */
	public ListLayout(final Type type, final Alignment alignment, final Separator separator, final boolean ordered, final Size space) {
		if (type == null) {
			throw new IllegalArgumentException("Type must be provided.");
		}
		this.type = type;
		this.alignment = alignment;
		this.separator = separator;
		this.ordered = ordered;
		this.space = space;
		this.gap = -1;
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
	 * @return the horizontal gap between the list items measured in pixels.
	 * @deprecated use {@link #getSpace() }
	 */
	@Deprecated
	public int getHgap() {
		if (this.type == Type.FLAT) {
			return gap;
		}
		return 0;
	}

	/**
	 * @return the vertical gap between the list items measured in pixels.
	 * @deprecated use {@link #getSpace() }
	 */
	@Deprecated
	public int getVgap() {
		if (this.type == Type.FLAT) {
			return 0;
		}
		return gap;
	}

	/**
	 * @return the space between the components added to the layout
	 */
	public Size getSpace() {
		return space;
	}

	/**
	 * @return the space between the components added to the layout
	 * @deprecated use {@link #getSpace() }
	 */
	@Deprecated
	public int getGap() {
		return gap;
	}
}
