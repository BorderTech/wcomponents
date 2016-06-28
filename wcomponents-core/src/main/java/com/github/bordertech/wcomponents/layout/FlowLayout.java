package com.github.bordertech.wcomponents.layout;
import com.github.bordertech.wcomponents.WPanel;

/**
 * FlowLayout is a {@link LayoutManager} that allows components to be added to a {@link WPanel} in an arrangement
 * that allows for vertical or horizontal flows irrespective of the content type of the added component. For
 * horizontal flows the components may be arranged so that they are aligned to the left, center or right of the
 * containing {@link WPanel} and so their content is arranged relative to each other at the top, middle or bottom of
 * the containing {@link WPanel} or so their baselines align.
 *
 * @author Yiannis Paschalidis, Mark Reeves
 * @since 1.0.0
 */
public class FlowLayout implements LayoutManager {

	/**
	 * This is used to control the alignment of the components relative to the containing {@link WPanel}.
	 */
	public enum Alignment {
		/**
		 * The components are placed in a row and left-aligned relative to the containing {@link WPanel}.
		 */
		LEFT,
		/**
		 * The components are placed in a row and centered relative to the containing {@link WPanel}.
		 */
		CENTER,
		/**
		 * The components are placed in a row and tight-aligned relative to the containing {@link WPanel}.
		 */
		RIGHT,
		/**
		 * Each component should be placed on a new row.
		 */
		VERTICAL
	};

	/**
	 * This is used to control the relative vertical alignment of the content in each cell. It is irrelevant (and
	 * therefore not used) when the FlowLayout's Alignment is Alignment.VERTICAL.
	 */
	public enum ContentAlignment {
		/**
		 * The content should align to the top.
		 */
		TOP,
		/**
		 * The content should align to the middle.
		 */
		MIDDLE,
		/**
		 * The content should align to the baseline.
		 */
		BASELINE,
		/**
		 * The content should align to the bottom.
		 */
		BOTTOM
	};

	/**
	 * This value indicates that the components are placed in a row and left-aligned relative to the containing
	 * {@link WPanel}.
	 */
	public static final Alignment LEFT = Alignment.LEFT;

	/**
	 * This value indicates that the components are placed in a row and centered relative to the containing
	 * {@link WPanel}.
	 */
	public static final Alignment CENTER = Alignment.CENTER;

	/**
	 * This value indicates that the components are placed in a row and right-aligned relative to the containing
	 * {@link WPanel}.
	 */
	public static final Alignment RIGHT = Alignment.RIGHT;

	/**
	 * This value indicates that each component should be placed on a new row.
	 */
	public static final Alignment VERTICAL = Alignment.VERTICAL;

	/**
	 * The alignment of components added to the FlowLayout.
	 */
	private final Alignment alignment;

	/**
	 * The space between components added to the FlowLayout. The direction of the space is determined by the
	 * FlowLayout's Alignment.
	 */
	private final int gap;

	/**
	 * The relative vertical alignment of content in each cell.
	 */
	private final ContentAlignment contentAlignment;

	/**
	 * Constructs a new <code>FlowLayout</code> with a centered alignment.
	 */
	public FlowLayout() {
		this(Alignment.CENTER, 0, null);
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified alignment and no horizontal or vertical gap.
	 *
	 * @param alignment the alignment of the components
	 */
	public FlowLayout(final Alignment alignment) {
		this(alignment, 0, null);
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified alignment (no horizontal or vertical gap) and content
	 * alignment.
	 *
	 * @param alignment the alignment of the components
	 * @param contentAlignment the alignment of the content in each cell
	 */
	public FlowLayout(final Alignment alignment, final ContentAlignment contentAlignment) {
		this(alignment, 0, contentAlignment);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment and horizontal and vertical gaps. The horizontal
	 * gap is only used if alignment is not VERTICAl, the vertical gap is used only if alignment is VERTICAL.
	 *
	 * @param alignment the alignment of the components
	 * @param hgap The horizontal gap between the cells, measured in pixels. Not used if alignment is VERTICAL.
	 * @param vgap The vertical gap between the cells, measured in pixels. Used only if alignment is VERTICAL.
	 *
	 * @deprecated use {@link #FlowLayout(Alignment, int)}
	 */
	public FlowLayout(final Alignment alignment, final int hgap, final int vgap) {
		this(alignment, Alignment.VERTICAL.equals(alignment) ? vgap : hgap, null);
	}

	/**
	 * Creates a FlowLayout with the indicated alignment and a gap between the components in the FlowLayout. The gap is
	 * applied as a vertical gap if the alignment is VERTICAL otherwise it is applied as a horizontal gap.
	 *
	 * @param alignment the required alignment
	 * @param gap the required gap between components in the layout
	 */
	public FlowLayout(final Alignment alignment, final int gap) {
		this(alignment, Alignment.VERTICAL.equals(alignment) ? 0 : gap, Alignment.VERTICAL.equals(alignment) ? gap : 0, null);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment, horizontal and vertical gaps and content
	 * alignment. The horizontal gap is only used if alignment is not VERTICAl, the vertical gap is used only if
	 * alignment is VERTICAL, content alignment is only relevant if alignment is not VERTICAL.
	 *
	 * @param alignment the alignment of the components
	 * @param hgap The horizontal gap between the cells, measured in pixels. Not used if alignment is VERTICAL.
	 * @param vgap The vertical gap between the cells, measured in pixels. Used only if alignment is VERTICAL.
	 * @param contentAlignment The relative vertical alignment of the content in each cell. Not used if alignment is
	 * VERTICAL.
	 *
	 * @deprecated use {@link #FlowLayout(Alignment, int, ContentAlignment)}
	 */
	public FlowLayout(final Alignment alignment, final int hgap, final int vgap,
			final ContentAlignment contentAlignment) {
		this(alignment, Alignment.VERTICAL.equals(alignment) ? vgap : hgap, Alignment.VERTICAL.equals(alignment) ? null : contentAlignment);
	}

	/**
	 * Creates a FlowLayout with the indicated alignment and a gap between the components in the FlowLayout and
	 * alignment of content in the cells. The gap is applied as a vertical gap if the alignment is VERTICAL otherwise it
	 * is applied as a horizontal gap. The content alignment is applied only if alignment is <em>not</em> VERTICAL.
	 *
	 * @param alignment the required alignment
	 * @param gap the required gap between components in the layout
	 * @param contentAlignment the alignment of the content in each cell when alignment is not vertical
	 */
	public FlowLayout(final Alignment alignment, final int gap, final ContentAlignment contentAlignment) {
		if (alignment == null) {
			throw new IllegalArgumentException("Alignment must be provided.");
		}
		this.alignment = alignment;
		this.gap = gap;
		this.contentAlignment = Alignment.VERTICAL.equals(alignment) ? null : contentAlignment;
	}

	/**
	 * @return the component alignment.
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @return the horizontal gap between the cells, measured in pixels.
	 * @deprecated use {@link #getGap()}
	 */
	public int getHgap() {
		return Alignment.VERTICAL.equals(alignment) ? 0 : gap;
	}

	/**
	 * @return the vertical gap between the cells, measured in pixels.
	 * @deprecated use {@link #getGap()}
	 */
	public int getVgap() {
		return Alignment.VERTICAL.equals(alignment) ? gap : 0;
	}


	/**
	 * @return the gap between the components added to the FlowLayout.
	 */
	public int getGap() {
		return gap;
	}

	/**
	 * @return the alignment of the content in each cell.
	 */
	public ContentAlignment getContentAlignment() {
		return contentAlignment;
	}
}
