package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;
import java.util.Arrays;

/**
 * ColumnLayout renders its items in columns.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @author John McGuinness
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
		 * Converts an array of {@link Alignment}s to an array of {@link CellAlignment}s.
		 * @param alignments the array of {@link Alignment}s to convert.
		 * @return cellAlignments an array of {@link CellAlignment}s corresponding
		 *         to the passed in {@link Alignment} values
		 */
		private static CellAlignment[] toCellAlignments(final Alignment[] alignments) {

			final CellAlignment[] cellAlignemnts = alignments == null ? null : new CellAlignment[alignments.length];

			if (cellAlignemnts != null) {
				for (int i = 0; i < alignments.length; i++) {
					cellAlignemnts[i] = alignments[i].toCellAlignment();
				}
			}

			return cellAlignemnts;
		}

		/**
		 * Converts a {@link CellAlignment} to an {@link Alignment}.
		 *
		 * @param cellAlignment the {@link CellAlignment} to convert from.
		 * @return alignment the converted {@link Alignment} value.
		 */
		private static Alignment fromCellAlignment(final CellAlignment cellAlignment) {
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
	 * The column widths.
	 */
	private final int[] columnWidths;

	/**
	 * The column alignments.
	 */
	private final CellAlignment[] columnAlignments;

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
		this(columnWidths, Alignment.toCellAlignments(columnAlignments), hSpace, vSpace, hgap, vgap);
	}

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
	private ColumnLayout(final int[] columnWidths, final CellAlignment[] columnAlignments, final Size hSpace, final Size vSpace,
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
		this.columnAlignments = new CellAlignment[columnWidths.length];

		if (columnAlignments == null) {
			Arrays.fill(this.columnAlignments, CellAlignment.LEFT);
		} else {
			for (int i = 0; i < columnAlignments.length; i++) {
				this.columnAlignments[i] = columnAlignments[i] == null ? CellAlignment.LEFT : columnAlignments[i];
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
		this(columnWidths, (Alignment[]) null, null, null);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths and column alignments.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param columnAlignments the column alignments
	 * 
	 * @deprecated use {@link #ColumnLayout(int[], CellAlignment[])}
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments) {
		this(columnWidths, columnAlignments, null, null);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths and column alignments.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param columnAlignments the column alignments
	 */
	public ColumnLayout(final int[] columnWidths, final CellAlignment[] columnAlignments) {
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
		this(columnWidths, (CellAlignment[]) null, SpaceUtil.intToSize(hgap), SpaceUtil.intToSize(vgap), hgap, vgap);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param hSpace the space between the columns
	 * @param vSpace the space between the rows
	 */
	public ColumnLayout(final int[] columnWidths, final Size hSpace, final Size vSpace) {
		this(columnWidths, (CellAlignment[]) null, hSpace, vSpace);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined
	 * @param columnAlignments the column alignments
	 * @param hgap the horizontal gap between the columns
	 * @param vgap the vertical gap between the rows
	 * @deprecated use {@link #ColumnLayout(int[], CellAlignment[], Size, Size)}
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
	 * @deprecated use {@link #ColumnLayout(int[], CellAlignment[], Size, Size)}
	 */
	public ColumnLayout(final int[] columnWidths, final Alignment[] columnAlignments, final Size hSpace, final Size vSpace) {
		this(columnWidths, columnAlignments, hSpace, vSpace, -1, -1);
	}

	/**
	 * Creates a ColumnLayout with the specified percentage column widths.
	 *
	 * @param columnWidths the column widths, in percent units, 0 for undefined.
	 * @param columnAlignments the column alignments
	 * @param hSpace the space between the columns
	 * @param vSpace the space between the rows
	 */
	public ColumnLayout(final int[] columnWidths, final CellAlignment[] columnAlignments, final Size hSpace, final Size vSpace) {
		this(columnWidths, columnAlignments, hSpace, vSpace, -1, -1);
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
		setCellAlignment(col, alignment == null ? null : alignment.toCellAlignment());
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
