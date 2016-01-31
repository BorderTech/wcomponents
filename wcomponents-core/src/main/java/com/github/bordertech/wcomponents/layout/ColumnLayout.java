package com.github.bordertech.wcomponents.layout;

import java.util.Arrays;

/**
 * ColumnLayout renders its items in columns.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ColumnLayout implements LayoutManager {

	/**
	 * An enumeration of possible values for horizontal alignment of column content.
	 *
	 * @deprecated Use {@link com.github.bordertech.wcomponents.layout.CellAlignment}
	 *             instead of {@link com.github.bordertech.wcomponents.layout.ColumnLayout.Alignment}.
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

		Alignment(final CellAlignment cellAlignment) {
			this.cellAlignment = cellAlignment;
		}

		private final CellAlignment cellAlignment;

		private static CellAlignment[] toCellAlignments(final Alignment[] alignments) {

			final CellAlignment[] cellAlignemnts = new CellAlignment[alignments.length];

			for(int i = 0; i < alignments.length; i++) {
				cellAlignemnts[i] = alignments[i].toCellAlignment();
			}

			return cellAlignemnts;
		}

		private static Alignment fromCellAlignment(final CellAlignment cellAlignment) {
			switch(cellAlignment) {
				case LEFT: return Alignment.LEFT;
				case CENTER: return Alignment.CENTER;
				case RIGHT: return Alignment.RIGHT;
			}

			return null;
		}

		private CellAlignment toCellAlignment()
		{
			return this.cellAlignment;
		}
	}

	/** The column widths.
	 */
	private final int[] columnWidths;

	/**
	 * The column alignments.
	 */
	private final CellAlignment[] columnAlignments;

	/**
	 * The horizontal gap between the columns, measured in pixels.
	 */
	private final int hgap;

	/**
	 * The vertical gap between the rows, measured in pixels.
	 */
	private final int vgap;

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units.
	 */
	public ColumnLayout(final int[] columnWidths) {
		this(columnWidths, (CellAlignment[])null, 0, 0);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths and column alignments.
	 *
	 * @param columnWidths the column widths, in percent units.
	 * @param columnAlignments the column alignments
	 */
	public ColumnLayout(final int[] columnWidths, final CellAlignment[] columnAlignments) {
		this(columnWidths, columnAlignments, 0, 0);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths and column alignments.
	 *
	 * @param columnWidths the column widths, in percent units.
	 * @param columnAlignments the column alignments
	 *
	 * @deprecated Use {@link ColumnLayout(int[], CellAlignment[])} instead.
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments) {
		this(columnWidths, Alignment.toCellAlignments(columnAlignments), 0, 0);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units.
	 * @param hgap the horizontal gap between the columns, measured in pixels.
	 * @param vgap the vertical gap between the rows, measured in pixels.
	 */
	public ColumnLayout(final int[] columnWidths, final int hgap, final int vgap) {
		this(columnWidths, (CellAlignment[])null, hgap, vgap);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units.
	 * @param columnAlignments the column alignments
	 * @param hgap the horizontal gap between the columns, measured in pixels.
	 * @param vgap the vertical gap between the rows, measured in pixels.
	 *
	 * @deprecated Use {@link com.github.bordertech.wcomponents.layout.CellAlignment}
	 *             instead of {@link com.github.bordertech.wcomponents.layout.ColumnLayout.Alignment}.
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments, final int hgap,
			final int vgap) {
		this(columnWidths, Alignment.toCellAlignments(columnAlignments), hgap, vgap);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units.
	 * @param columnAlignments the column alignments
	 * @param hgap the horizontal gap between the columns, measured in pixels.
	 * @param vgap the vertical gap between the rows, measured in pixels.
	 */
	public ColumnLayout(final int[] columnWidths, final CellAlignment[] columnAlignments, final int hgap,
			final int vgap) {
		if (columnWidths == null || columnWidths.length == 0) {
			throw new IllegalArgumentException("ColumnWidths must be provided");
		}

		// Column Definitions
		for (int col = 0; col < columnWidths.length; col++) {
			if (columnWidths[col] < 1 || columnWidths[col] > 100) {
				throw new IllegalArgumentException(
						"ColumnWidth (" + columnWidths[col] + ") must be between 1 and 100 percent");
			}
		}

		if (columnAlignments != null && columnAlignments.length != columnWidths.length) {
			throw new IllegalArgumentException(
					"A columnAlignment must be provided for each columnWidth");
		}

		if (hgap < 0) {
			throw new IllegalArgumentException("Hgap must be greater than or equal to zero");
		}

		if (vgap < 0) {
			throw new IllegalArgumentException("Vgap must be greater than or equal to zero");
		}

		this.columnWidths = columnWidths;
		this.columnAlignments = new CellAlignment[columnWidths.length];

		if (columnAlignments == null) {
			Arrays.fill(this.columnAlignments, CellAlignment.LEFT);
		} else {
			for (int i = 0; i < columnAlignments.length; i++) {
				this.columnAlignments[i] = columnAlignments[i] == null ? CellAlignment.LEFT : columnAlignments[i];
			}
		}

		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * Sets the alignment of the given column. An IndexOutOfBoundsException will be thrown if col is out of bounds.
	 *
	 * @param col the index of the column to set the alignment of.
	 * @param alignment the alignment to set.
	 *
	 * @deprecated Use {@link ColumnLayout#setCellAlignment(int, CellAlignment)} instead.
	 */
	public void setAlignment(final int col, final Alignment alignment) {
		setCellAlignment(col, alignment.toCellAlignment());
	}

	/**
	 * Sets the alignment of the given column. An IndexOutOfBoundsException will be thrown if col is out of bounds.
	 *
	 * @param col the index of the column to set the alignment of.
	 * @param alignment the alignment to set.
	 */
	public void setCellAlignment(final int col, final CellAlignment alignment) {
		columnAlignments[col] = alignment == null ? CellAlignment.LEFT : alignment;
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
	 * @param columnIndex the index of the column to retrieve the width for.
	 * @return the width of the given column.
	 */
	public int getColumnWidth(final int columnIndex) {
		return columnWidths[columnIndex];
	}

	/**
	 * @param columnIndex the index of the column to retrieve the alignment for.
	 * @return the alignment of the given column.
	 */
	public CellAlignment getColumnCellAlignment(final int columnIndex) {
		return columnAlignments[columnIndex];
	}

	/**
	 * @param columnIndex the index of the column to retrieve the alignment for.
	 * @return the alignment of the given column.
	 *
	 * @deprecated Use {@link ColumnLayout#getColumnCellAlignment(int)} instead.
	 */
	public Alignment getColumnAlignment(final int columnIndex) {
		return Alignment.fromCellAlignment(getColumnCellAlignment(columnIndex));
	}

	/**
	 * @return the number of columns in this layout.
	 */
	public int getColumnCount() {
		return columnWidths.length;
	}
}
