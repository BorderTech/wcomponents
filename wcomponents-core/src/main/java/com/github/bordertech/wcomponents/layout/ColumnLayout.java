package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;
import java.util.Arrays;

/**
 * ColumnLayout renders its items in columns.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
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
	 * The horizontal gap between the columns.
	 */
	private final Size hSpace;

	/**
	 * The vertical gap between the rows.
	 */
	private final Size vSpace;

	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int hgap;

	/**
	 * For temporary backwards compatibility only.
	 */
	@Deprecated
	private final int vgap;

	/**
	 * For temporary backwards compatibility only.
	 * @param columnWidths the column widths
	 * @param columnAlignments the column alignments
	 * @param hSpace the real horizontal space between columns
	 * @param vSpace the real vertical space between rows
	 * @param hgap the requested horizontal space between columns
	 * @param vgap  the requested vertical space between rows
	 */
	@Deprecated
	private ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments, final Size hSpace, final Size vSpace,
			final int hgap, final int vgap) {
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

		this.columnWidths = columnWidths;
		this.columnAlignments = new Alignment[columnWidths.length];

		if (columnAlignments == null) {
			Arrays.fill(this.columnAlignments, Alignment.LEFT);
		} else {
			for (int i = 0; i < columnAlignments.length; i++) {
				this.columnAlignments[i] = columnAlignments[i] == null ? Alignment.LEFT : columnAlignments[i];
			}
		}

		this.hSpace = hSpace;
		this.vSpace = vSpace;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths. Using a column width of 0 will make the width
	 * undefined in the UI. This may then be used for application level styling with CSS for responsive design.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined.
	 */
	public ColumnLayout(final int[] columnWidths) {
		this(columnWidths, null, null, null);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths and column alignments.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param columnAlignments the column alignments
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments) {
		this(columnWidths, columnAlignments, null, null);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param hgap the horizontal gap between the columns
	 * @param vgap the vertical gap between the rows
	 * @deprecated use {@link #ColumnLayout(int[], Size, Size)}
	 */
	@Deprecated
	public ColumnLayout(final int[] columnWidths, final int hgap, final int vgap) {
		this(columnWidths, null, SpaceUtil.intToSize(hgap), SpaceUtil.intToSize(vgap), hgap, vgap);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param hSpace the space between the columns
	 * @param vSpace the space between the rows
	 */
	public ColumnLayout(final int[] columnWidths, final Size hSpace, final Size vSpace) {
		this(columnWidths, null, hSpace, vSpace);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param columnAlignments the column alignments
	 * @param hgap the horizontal gap between the columns
	 * @param vgap the vertical gap between the rows
	 * @deprecated use {@link #ColumnLayout(int[], Alignment[], Size, Size)}
	 */
	@Deprecated
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments, final int hgap, final int vgap) {
		this(columnWidths, columnAlignments, SpaceUtil.intToSize(hgap), SpaceUtil.intToSize(vgap), hgap, vgap);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined.
	 * @param columnAlignments the column alignments
	 * @param hSpace the space between the columns
	 * @param vSpace the space between the rows
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments, final Size hSpace, final Size vSpace) {
		this(columnWidths, columnAlignments, hSpace, vSpace, -1, -1);
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
	 * @return the horizontal space between the cells
	 */
	public Size getHorizontalGap() {
		return hSpace;
	}

	/**
	 * @return the vertical space between the cells
	 */
	public Size getVerticalGap() {
		return vSpace;
	}

	/**
	 * @return the horizontal gap between the cells measured in pixels
	 * @deprecated use {@link #getHorizontalGap() }
	 */
	@Deprecated
	public int getHgap() {
		return hgap;
	}

	/**
	 * @return the vertical gap between the cells, measured in pixels
	 * @deprecated use {@link #getVerticalGap() }
	 */
	@Deprecated
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
