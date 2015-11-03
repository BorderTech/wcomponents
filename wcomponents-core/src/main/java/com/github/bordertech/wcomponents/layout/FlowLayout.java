package com.github.bordertech.wcomponents.layout;

/**
 * FlowLayout is a {@link LayoutManager} that emulates {@link java.awt.FlowLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class FlowLayout implements LayoutManager {

	/**
	 * This is used to control the alignment of the components.
	 */
	public enum Alignment {
		/**
		 * Each row of components should be left-justified.
		 */
		LEFT,
		/**
		 * Each row of components should be centered.
		 */
		CENTER,
		/**
		 * Each row of components should be right-justified.
		 */
		RIGHT,
		/**
		 * Each component should be placed on a new row.
		 */
		VERTICAL
	};

	/**
	 * This is used to control the alignment of the content in each cell.
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
	 * This value indicates that each row of components should be left-justified.
	 */
	public static final Alignment LEFT = Alignment.LEFT;

	/**
	 * This value indicates that each row of components should be centered.
	 */
	public static final Alignment CENTER = Alignment.CENTER;

	/**
	 * This value indicates that each row of components should be right-justified.
	 */
	public static final Alignment RIGHT = Alignment.RIGHT;

	/**
	 * This value indicates that each component should be placed on a new row.
	 */
	public static final Alignment VERTICAL = Alignment.VERTICAL;

	/**
	 * The component alignment.
	 */
	private final Alignment alignment;

	/**
	 * The horizontal gap between the cells, measured in pixels.
	 */
	private final int hgap;

	/**
	 * The vertical gap between the cells, measured in pixels.
	 */
	private final int vgap;

	/**
	 * The content alignment in each cell.
	 */
	private final ContentAlignment contentAlignment;

	/**
	 * Constructs a new <code>FlowLayout</code> with a centered alignment and no horizontal or vertical gap.
	 */
	public FlowLayout() {
		this(Alignment.CENTER, 0, 0, null);
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified alignment and no horizontal or vertical gap.
	 *
	 * @param alignment the alignment of the components
	 */
	public FlowLayout(final Alignment alignment) {
		this(alignment, 0, 0, null);
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified alignment (no horizontal or vertical gap) and content
	 * alignment.
	 *
	 * @param alignment the alignment of the components
	 * @param contentAlignment the alignment of the content in each cell
	 */
	public FlowLayout(final Alignment alignment, final ContentAlignment contentAlignment) {
		this(alignment, 0, 0, contentAlignment);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment and horizontal and vertical gaps.
	 *
	 * @param alignment the alignment of the components
	 * @param hgap the horizontal gap between the cells, measured in pixels.
	 * @param vgap the vertical gap between the cells, measured in pixels.
	 */
	public FlowLayout(final Alignment alignment, final int hgap, final int vgap) {
		this(alignment, hgap, vgap, null);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment, horizontal and vertical gaps and content
	 * alignment.
	 *
	 * @param alignment the alignment of the components
	 * @param hgap the horizontal gap between the cells, measured in pixels.
	 * @param vgap the vertical gap between the cells, measured in pixels.
	 * @param contentAlignment the alignment of the content in each cell
	 */
	public FlowLayout(final Alignment alignment, final int hgap, final int vgap,
			final ContentAlignment contentAlignment) {
		if (alignment == null) {
			throw new IllegalArgumentException("Alignment must be provided.");
		}
		this.hgap = hgap;
		this.vgap = vgap;
		this.alignment = alignment;
		this.contentAlignment = contentAlignment;
	}

	/**
	 * @return the component alignment.
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @return the horizontal gap between the cells, measured in pixels.
	 */
	public int getHgap() {
		return hgap;
	}

	/**
	 * @return the vertical gap between the cells, measured in pixels.
	 */
	public int getVgap() {
		return vgap;
	}

	/**
	 * @return the alignment of the content in each cell.
	 */
	public ContentAlignment getContentAlignment() {
		return contentAlignment;
	}

}
