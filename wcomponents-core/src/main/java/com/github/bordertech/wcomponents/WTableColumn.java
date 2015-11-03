package com.github.bordertech.wcomponents;

/**
 * WTableColumn represents a column in a {@link WDataTable}. It only holds configuration and state information relating
 * to the UI, and does not know about the data model.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class WTableColumn extends AbstractContainer {

	/**
	 * The renderer class that will be used to render row data for this column.
	 */
	private final Class<? extends WComponent> rendererClass;

	/**
	 * The renderer that will be used to render row data for this column.
	 */
	private final WComponent renderer;

	/**
	 * The table column heading, which may contain complex content.
	 */
	private final WDecoratedLabel label;

	/**
	 * An enumeration of possible values for horizontal alignment of table column content.
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
	 * Creates a WTableColumn.
	 *
	 * @param heading the column heading text.
	 * @param rendererClass the renderer class for rendering row data.
	 */
	public WTableColumn(final String heading, final Class<? extends WComponent> rendererClass) {
		this(new WDecoratedLabel(heading), rendererClass);
	}

	/**
	 * Creates a WTableColumn.
	 *
	 * @param label the column heading.
	 * @param rendererClass the renderer class for rendering row data.
	 */
	public WTableColumn(final WDecoratedLabel label, final Class<? extends WComponent> rendererClass) {
		add(label);

		this.label = label;
		this.rendererClass = rendererClass;
		this.renderer = null;
	}

	/**
	 * Creates a WTableColumn.
	 *
	 * @param heading the column heading text.
	 * @param renderer the component for rendering row data.
	 */
	public WTableColumn(final String heading, final WComponent renderer) {
		this(new WDecoratedLabel(heading), renderer);
	}

	/**
	 * Creates a WTableColumn.
	 *
	 * @param label the column heading.
	 * @param renderer the component for rendering row data.
	 */
	public WTableColumn(final WDecoratedLabel label, final WComponent renderer) {
		add(label);

		this.label = label;
		this.renderer = renderer;
		this.rendererClass = renderer.getClass();
	}

	/**
	 * @return the renderer class that will be used to render row data for this column.
	 */
	public Class<? extends WComponent> getRendererClass() {
		return rendererClass;
	}

	/**
	 * @return the renderer that will be used to render row data for this column.
	 */
	public WComponent getRenderer() {
		return renderer;
	}

	/**
	 * Returns the heading text for this column, in the case that the heading does not contain complex content.
	 *
	 * @return the heading text, if available, otherwise null.
	 * @see WDecoratedLabel#getText()
	 */
	public String getHeadingText() {
		return label.getText();
	}

	/**
	 * Retrieves the column width, if set.
	 *
	 * @return the column width
	 */
	public int getWidth() {
		return getComponentModel().width;
	}

	/**
	 * Sets the column width.
	 *
	 * @param width the column width as a percentage, or &lt;= 0 for default width.
	 */
	public void setWidth(final int width) {
		if (width > 100) {
			throw new IllegalArgumentException(
					"Width (" + width + ") cannot be greater than 100 percent");
		}
		getOrCreateComponentModel().width = Math.max(0, width);
	}

	/**
	 * Retrieves the column alignment.
	 *
	 * @return the column alignment
	 */
	public Alignment getAlign() {
		return getComponentModel().align;
	}

	/**
	 * Sets the column alignment.
	 *
	 * @param align the column alignment.
	 */
	public void setAlign(final Alignment align) {
		getOrCreateComponentModel().align = align;
	}

	/**
	 * @return the label for this column
	 */
	public WDecoratedLabel getColumnLabel() {
		return label;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = label.getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, -1, -1);
	}

	/**
	 * Creates a new Component model appropriate for this component.
	 *
	 * @return a new WTableColumnModel.
	 */
	@Override
	protected WTableColumnModel newComponentModel() {
		return new WTableColumnModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected WTableColumnModel getOrCreateComponentModel() {
		return (WTableColumnModel) super.getOrCreateComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected WTableColumnModel getComponentModel() {
		return (WTableColumnModel) super.getComponentModel();
	}

	/**
	 * The component model that holds the column's state.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class WTableColumnModel extends ComponentModel {

		private int width;
		private Alignment align;
	}
}
