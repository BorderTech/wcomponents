package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;

/**
 * <p>
 * This component is used to group together a collection of <code>WField</code> components to provide a consistent
 * layout template.
 * </p>
 * <p>
 * It also provides some helpful infrastructure around WComponent validation by supplying helper methods to validate and
 * set error indicators on each of its contained <code>WField</code> components. It is up the the parent
 * <code>ValidatableComponent</code> or a <code>ValidatingAction</code> to call the validate() and showErrorIndicator()
 * methods on this component.
 * </p>
 *
 * @author Adam Millard
 */
public class WFieldLayout extends AbstractNamingContextContainer implements AjaxTarget,
		SubordinateTarget, Marginable {

	/**
	 * The default label width.
	 *
	 * @deprecated Will be defined by the theme. Set {@link #setLabelWidth(int) labelWidth} &lt;=0 for the default.
	 */
	@Deprecated
	public static final int DEFAULT_LABEL_WIDTH = 0;

	/**
	 * Layout type of flat.
	 */
	public static final String LAYOUT_FLAT = "flat";
	/**
	 * Layout type of stacked.
	 */
	public static final String LAYOUT_STACKED = "stacked";

	/**
	 * Creates a WFieldLayout with the default layout type of {@link #LAYOUT_FLAT}.
	 */
	public WFieldLayout() {
		this(LAYOUT_FLAT);
	}

	/**
	 * Creates a WFieldLayout with the given layout type.
	 *
	 * @param layout one of {@link #LAYOUT_FLAT} or {@link #LAYOUT_STACKED}.
	 */
	public WFieldLayout(final String layout) {
		if ((layout == null) || (layout.length() == 0)) {
			throw new IllegalArgumentException("A layout must be supplied.");
		}

		if (!(layout.equals(LAYOUT_FLAT) || layout.equals(LAYOUT_STACKED))) {
			throw new IllegalArgumentException("Unknown layout: " + layout);
		}

		getComponentModel().layoutType = layout;
	}

	/**
	 * Set the heading for the field layout.
	 *
	 * @param heading the text for the heading
	 * @deprecated use {@link #setTitle(String)} instead
	 */
	@Deprecated
	public void setHeading(final String heading) {
		setTitle(heading);
	}

	/**
	 * Set the title for the field layout.
	 *
	 * @param title the text for the title
	 */
	public void setTitle(final String title) {
		getOrCreateComponentModel().title = title;
	}

	/**
	 * Get the title for this field layout.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return I18nUtilities.format(null, getComponentModel().title);
	}

	/**
	 * Get the type of layout for this field layout.
	 *
	 * @return the layout type
	 */
	public String getLayoutType() {
		return getComponentModel().layoutType;
	}

	/**
	 * @return Returns the labelWidth.
	 */
	public int getLabelWidth() {
		return getComponentModel().labelWidth;
	}

	/**
	 * Sets the label width.
	 *
	 * @param labelWidth the percentage width, or &lt;= 0 to use the default field width.
	 */
	public void setLabelWidth(final int labelWidth) {
		if (labelWidth > 100) {
			throw new IllegalArgumentException(
					"labelWidth (" + labelWidth + ") cannot be greater than 100 percent.");
		}
		getOrCreateComponentModel().labelWidth = Math.max(0, labelWidth);
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
	 * @return true if ordered layout
	 */
	public boolean isOrdered() {
		return getComponentModel().ordered;
	}

	/**
	 * Flag a layout to be an ordered list. Used with {@link #setOrderedOffset(int)}.
	 *
	 * @param ordered true if ordered layout
	 */
	public void setOrdered(final boolean ordered) {
		getOrCreateComponentModel().ordered = ordered;
	}

	/**
	 * Allows layouts to have its ordered numbering start from an offset. Used with {@link #setOrdered(boolean)}.
	 *
	 * @return the ordered start offset.
	 */
	public int getOrderedOffset() {
		return getComponentModel().orderedOffset;
	}

	/**
	 * Set the starting offset for an ordered layout. Used with {@link #setOrdered(boolean)}.
	 *
	 * @param orderedOffset the ordered start offset. Must be 1 or greater.
	 */
	public void setOrderedOffset(final int orderedOffset) {
		if (orderedOffset <= 0) {
			throw new IllegalArgumentException(
					"Ordered start offset (" + orderedOffset + ") must be greater than zero.");
		}
		getOrCreateComponentModel().orderedOffset = orderedOffset;
	}

	/**
	 * Add a field using the label and components passed in.
	 *
	 * @param label the label to use for the field
	 * @param field the component to use for the field
	 * @return the field which was added to the layout.
	 */
	public WField addField(final String label, final WComponent field) {
		return addField(new WLabel(label), field);
	}

	/**
	 * Add a field using the label and components passed in.
	 *
	 * @param label the label to use for the field
	 * @param field the component to use for the field
	 * @return the field which was added to the layout.
	 */
	public WField addField(final WLabel label, final WComponent field) {
		WField wField = new WField(label, field);
		add(wField);

		return wField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public void remove(final WComponent child) {
		super.remove(child);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		return toString(getLayoutType());
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new FieldLayoutModel.
	 */
	@Override // For type safety only
	protected FieldLayoutModel newComponentModel() {
		return new FieldLayoutModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FieldLayoutModel getComponentModel() {
		return (FieldLayoutModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FieldLayoutModel getOrCreateComponentModel() {
		return (FieldLayoutModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class FieldLayoutModel extends ComponentModel {

		/**
		 * Optional title for the field layout.
		 */
		private String title;

		/**
		 * Width of the labels.
		 */
		private int labelWidth;

		/**
		 * The type of layout to use for the field layout.
		 */
		private String layoutType;

		/**
		 * The margins to be used on the section.
		 */
		private Margin margin;

		/**
		 * Ordered layout.
		 */
		private boolean ordered;

		/**
		 * Ordered layout starting offset.
		 */
		private int orderedOffset = 1;

	}
}
