package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.layout.CellAlignment;

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
	 * An enumeration of possible values for horizontal alignment of column content.
	 *
	 * @deprecated Use {@link com.github.bordertech.wcomponents.layout.CellAlignment}
	 *             instead of {@link com.github.bordertech.wcomponents.WTableColumn.Alignment}.
	 */
	public enum Alignment {
		/**
		 * Indicates that content should be left-aligned. This is the default alignment.
		 */
		LEFT(CellAlignment.LEFT),
		/**
		 * Indicates that content should be horizontally centered in the column.
		 */
		CENTER(CellAlignment.CENTER),
		/**
		 * Indicates that content should be right-aligned.
		 */
		RIGHT(CellAlignment.RIGHT);

		/**
		 * Alignment constructor.
		 *
		 * @param cellAlignment the corresponding {@link CellAlignment}
		 */
		Alignment(final CellAlignment cellAlignment) {
			this.cellAlignment = cellAlignment;
		}

		/**
		 * The {@link CellAlignment} that corresponds to this {@link Alignment}.
		 */
		private final CellAlignment cellAlignment;

		/**
		 * Converts a {@link CellAlignment} to an {@link Alignment}.
		 *
		 * @param cellAlignment the {@link CellAlignment} to convert from.
		 * @return alignment the converted {@link Alignment} value.
		 */
		private static Alignment fromCellAlignment(final CellAlignment cellAlignment) {

			if (cellAlignment == null) {
				return null;
			}

			switch (cellAlignment) {
				case LEFT: return Alignment.LEFT;
				case CENTER: return Alignment.CENTER;
				case RIGHT: return Alignment.RIGHT;
				default: return null;
			}
		}

		/**
		 * Converts this {@link Alignment} to a {@link CellAlignment}.
		 * @return the converted {@link CellAlignment} value.
		 */
		private CellAlignment toCellAlignment() {
			return this.cellAlignment;
		}

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
	public CellAlignment getCellAlignment() {
		return getComponentModel().align;
	}

	/**
	 * Sets the column alignment.
	 *
	 * @param align the column alignment.
	 */
	public void setCellAlignment(final CellAlignment align) {
		getOrCreateComponentModel().align = align;
	}

	/**
	 * Retrieves the column alignment.
	 *
	 * @return the column alignment
	 *
	 * @deprecated Use {@link WTableColumn#getCellAlignment() } instead.
	 */
	public Alignment getAlign() {
		return Alignment.fromCellAlignment(getCellAlignment());
	}

	/**
	 * Sets the column alignment.
	 *
	 * @param align the column alignment.
	 *
	 * @deprecated Use {@link WTableColumn#setCellAlignment(CellAlignment) } instead.
	 */
	public void setAlign(final Alignment align) {
		setCellAlignment(align == null ? null : align.toCellAlignment());
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
		private CellAlignment align;
	}
}
