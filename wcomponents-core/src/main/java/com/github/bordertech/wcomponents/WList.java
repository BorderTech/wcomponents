package com.github.bordertech.wcomponents;

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
	 * The gap between the components in the list.
	 */
	private final int gap;

	/**
	 * Creates a WList of the given type.
	 *
	 * @param type the list type.
	 */
	public WList(final Type type) {
		this(type, 0);
	}

	/**
	 * Creates a WList of the given type.
	 *
	 * @param type the list type.
	 * @param hgap the horizontal gap between the list items, used only if type is Type.FLAT
	 * @param vgap the vertical gap between the list items,  used only if type is not Type.FLAT
	 * @deprecated use {@link #WList(Type, int)}
	 */
	public WList(final Type type, final int hgap, final int vgap) {
		this(type, type == Type.FLAT ? hgap : vgap);
	}

	/**
	 * Creates a WList of the given type with the specified gap between items.
	 *
	 * @param type the list type.
	 * @param gap the gap between the list items
	 */
	public WList(final Type type, final int gap) {
		getComponentModel().type = type;
		this.gap = gap;
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
	 * @return Returns the horizontal gap between the cells.
	 * @deprecated use {@link #getGap()}
	 */
	public int getHgap() {
		if (getType() == Type.FLAT) {
			return gap;
		}
		return 0;
	}

	/**
	 * @return Returns the vertical gap between the cells.
	 * @deprecated use {@link #getGap()}
	 */
	public int getVgap() {
		if (getType() == Type.FLAT) {
			return 0;
		}
		return gap;
	}

	/**
	 * @return the gap between items in the List.
	 */
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
