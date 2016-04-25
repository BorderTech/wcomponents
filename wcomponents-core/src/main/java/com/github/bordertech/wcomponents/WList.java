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
	 * The horizontal gap between the list items, measured in pixels.
	 */
	private final int hgap;
	/**
	 * The vertical gap between the list items, measured in pixels.
	 */
	private final int vgap;

	/**
	 * Creates a WList of the given type.
	 *
	 * @param type the list type.
	 */
	public WList(final Type type) {
		this(type, 0, 0);
	}

	/**
	 * Creates a WList of the given type.
	 *
	 * @param type the list type.
	 * @param hgap the horizontal gap between the list items, measured in pixels.
	 * @param vgap the vertical gap between the list items, measured in pixels.
	 */
	public WList(final Type type, final int hgap, final int vgap) {
		getComponentModel().type = type;
		this.hgap = hgap;
		this.vgap = vgap;
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
	 * @return Returns the horizontal gap between the cells, measured in pixels.
	 */
	public int getHgap() {
		return hgap;
	}

	/**
	 * @return Returns the vertical gap between the cells, measured in pixels.
	 */
	public int getVgap() {
		return vgap;
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
		 * The seperator used to separate items in the list.
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
