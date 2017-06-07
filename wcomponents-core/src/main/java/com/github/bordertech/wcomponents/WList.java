package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SpaceUtil;

/**
 * <p>
 * This component is an extension of a {@link WRepeater} that is used to render a collection of items as a list.
 * </p>
 * <p>
 * The operation of this component is essentially the same as a WRepeater, but it allows for some basic control over how
 * the repeated components are arranged.
 * </p>
 *
 * @author Adam Millard
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WList extends WRepeater implements Marginable {

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
	 * The space between the components in the list.
	 */
	private final Size space;

	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int gap;

	/**
	 * For temporary backwards compatibility only.
	 * @param type the list type
	 * @param space the real space between items
	 * @param gap the requested space between items
	 */
	@Deprecated
	private WList(final Type type, final Size space, final int gap) {
		getComponentModel().type = type;
		this.space = space;
		this.gap = gap;
	}

	/**
	 * Creates a WList of the given type with the specified space between items.
	 *
	 * @param type the list type.
	 * @param space the space between the list items
	 */
	public WList(final Type type, final Size space) {
		getComponentModel().type = type;
		this.space = space;
		this.gap = -1;
	}

	/**
	 * Creates a WList of the given type.
	 *
	 * @param type the list type.
	 */
	public WList(final Type type) {
		this(type, null);
	}

	/**
	 * Creates a WList of the given type with the specified space between items.
	 *
	 * @param type the list type.
	 * @param gap the space between the list items
	 * @deprecated use {@link #WList(Type, Size)}
	 */
	@Deprecated
	public WList(final Type type, final int gap) {
		this(type, SpaceUtil.intToSize(gap), gap);
	}

	/**
	 * Creates a WList of the given type.
	 *
	 * @param type the list type.
	 * @param hgap the horizontal space between the list items, used only if type is Type.FLAT
	 * @param vgap the vertical space between the list items,  used only if type is not Type.FLAT
	 * @deprecated use {@link #WList(Type, Size)}
	 */
	@Deprecated
	public WList(final Type type, final int hgap, final int vgap) {
		this(type, type == Type.FLAT ? hgap : vgap);
	}

	/**
	 * Sets the list type.
	 *
	 * @param type the list type.
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type;
	}

	/**
	 * @return the list type.
	 */
	public Type getType() {
		return getComponentModel().type;
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
	 * Sets the separator used to separate items in the list.
	 *
	 * @param separator the separator to set.
	 */
	public void setSeparator(final Separator separator) {
		getOrCreateComponentModel().separator = separator;
	}

	/**
	 * @return the separator used to separate items in the list.
	 */
	public Separator getSeparator() {
		return getComponentModel().separator;
	}

	/**
	 * Sets whether a border should be rendered around the list.
	 *
	 * @param renderBorder true to render a border, false otherwise.
	 */
	public void setRenderBorder(final boolean renderBorder) {
		getOrCreateComponentModel().renderBorder = renderBorder;
	}

	/**
	 * Indicates whether a border should be rendered around the list.
	 *
	 * @return renderBorder true if a border should be rendered, false otherwise.
	 */
	public boolean isRenderBorder() {
		return getComponentModel().renderBorder;
	}

	/**
	 * @return Returns the horizontal space between the cells.
	 * @deprecated use {@link #getGap()}
	 */
	@Deprecated
	public int getHgap() {
		if (getType() == Type.FLAT) {
			return gap;
		}
		return 0;
	}

	/**
	 * @return Returns the vertical space between the cells.
	 * @deprecated use {@link #getGap()}
	 */
	@Deprecated
	public int getVgap() {
		if (getType() == Type.FLAT) {
			return 0;
		}
		return gap;
	}

	/**
	 * @return the space between items in the List.
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
	 * Creates a new ListModel.
	 *
	 * @return a new ListModel
	 */
	@Override
	protected ListModel newComponentModel() {
		return new ListModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ListModel getComponentModel() {
		return (ListModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ListModel getOrCreateComponentModel() {
		return (ListModel) super.getOrCreateComponentModel();
	}

	/**
	 * ListModel holds Extrinsic state management of the field.
	 */
	public static class ListModel extends RepeaterModel {

		/**
		 * Indicates whether the border should be shown.
		 */
		private boolean renderBorder;

		/**
		 * The separator used to separate items in the list.
		 */
		private Separator separator;

		/**
		 * The list type controls the arrangement of items in the list.
		 */
		private Type type;

		/**
		 * The margins to be used on the section.
		 */
		private Margin margin;
	}
}
