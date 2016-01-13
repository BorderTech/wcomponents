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
	 * The column widths.
	 */
	private final int[] columnWidths;

	/**
	 * The column alignments.
	 */
	private final Alignment[] columnAlignments;

	/**
	 * The horizontal gap between the columns, measured in pixels.
	 */
	private final int hgap;

	/**
	 * The vertical gap between the rows, measured in pixels.
	 */
	private final int vgap;


	/**
	 * Creates a ColumnLayout with the specified percentage column widths. Using a column width of 0 will make the width
	 * undefined in the UI. This may then be used for application level styling with CSS for responsive design.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined.
	 */
	public ColumnLayout(final int[] columnWidths) {
		this(columnWidths, null, 0, 0);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths and column alignments.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined.
	 * @param columnAlignments the column alignments
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments) {
		this(columnWidths, columnAlignments, 0, 0);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined.
	 * @param hgap the horizontal gap between the columns, measured in pixels.
	 * @param vgap the vertical gap between the rows, measured in pixels.
	 */
	public ColumnLayout(final int[] columnWidths, final int hgap, final int vgap) {
		this(columnWidths, null, hgap, vgap);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined.
	 * @param columnAlignments the column alignments
	 * @param hgap the horizontal gap between the columns, measured in pixels.
	 * @param vgap the vertical gap between the rows, measured in pixels.
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments, final int hgap,
			final int vgap) {
		if (columnWidths == null || columnWidths.length == 0) {
			throw new IllegalArgumentException("ColumnWidths must be provided");
		}

		// Column Definitions
		for (int col = 0; col < columnWidths.length; col++) {
			if (columnWidths[col] < 0 || columnWidths[col] > 100) {
				throw new IllegalArgumentException(
						"ColumnWidth (" + columnWidths[col] + ") must be between 0 and 100 percent");
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
		this.columnAlignments = new Alignment[columnWidths.length];

		if (columnAlignments == null) {
			Arrays.fill(this.columnAlignments, Alignment.LEFT);
		} else {
			for (int i = 0; i < columnAlignments.length; i++) {
				this.columnAlignments[i] = columnAlignments[i] == null ? Alignment.LEFT : columnAlignments[i];
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
	 */
	public void setAlignment(final int col, final Alignment alignment) {
		columnAlignments[col] = alignment == null ? Alignment.LEFT : alignment;
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
	public Alignment getColumnAlignment(final int columnIndex) {
		return columnAlignments[columnIndex];
	}

	/**
	 * @return the number of columns in this layout.
	 */
	public int getColumnCount() {
		return columnWidths.length;
	}
}
