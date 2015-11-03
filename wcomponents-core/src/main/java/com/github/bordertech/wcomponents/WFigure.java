package com.github.bordertech.wcomponents;

/**
 * <p>
 * WFigure represents a container that is used to associate a large graphical element with its text description. For
 * example, a graph and its description.
 * <p>
 * Various {@link FigureMode modes of operation} are supported, which allow developers to tune performance by only
 * loading content when it is needed.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFigure extends AbstractNamingContextContainer implements AjaxTarget, SubordinateTarget,
		Marginable {

	/**
	 * The available types of AJAX mode.
	 */
	public enum FigureMode {
		/**
		 * A lazy figure will load its content via AJAX when it is made visible.
		 */
		LAZY,
		/**
		 * An eager figure will load its content via AJAX immediately after the page is loaded.
		 */
		EAGER
	};

	/**
	 * The label for the figure.
	 */
	private final WDecoratedLabel label;

	/**
	 * The content for the figure.
	 */
	private final WComponent content;

	/**
	 * Creates a WFigure with the given content and heading.
	 *
	 * @param content the content for the figure
	 * @param heading the figure's heading.
	 */
	public WFigure(final WComponent content, final String heading) {
		this(content, new WDecoratedLabel(heading));
	}

	/**
	 * Creates a WFigure with the given content and heading.
	 *
	 * @param content the content for the figure.
	 * @param label the figure's heading.
	 */
	public WFigure(final WComponent content, final WDecoratedLabel label) {
		if (content == null) {
			throw new IllegalArgumentException("The content of a WFigure cannot be null");
		}

		if (label == null) {
			throw new IllegalArgumentException("The label of a WFigure cannot be null");
		}

		this.content = content;
		this.label = label;
		add(label);
		add(content);
	}

	/**
	 * @return this WFigure's mode of operation
	 */
	public FigureMode getMode() {
		return getComponentModel().mode;
	}

	/**
	 * Sets this WFigure's mode of operation.
	 *
	 * @param mode the mode of operation.
	 */
	public void setMode(final FigureMode mode) {
		getOrCreateComponentModel().mode = mode;
	}

	/**
	 * @return the content of this figure
	 */
	public WComponent getContent() {
		return content;
	}

	/**
	 * @return the decorated label that is used to render the figure heading.
	 */
	public WDecoratedLabel getDecoratedLabel() {
		return label;
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
	 * Override preparePaintComponent in order to toggle the visibility of the content, or to register the appropriate
	 * ajax operation.
	 *
	 * @param request the request being responded to
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		UIContext uic = UIContextHolder.getCurrent();

		// Register figure for AJAX
		if (uic.getUI() != null && getMode() != null) {
			AjaxHelper.registerComponentTargetItself(getId(), request);
		}
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = label == null ? null : label.getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, 1, 1);
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new FigureModel.
	 */
	@Override
	protected FigureModel newComponentModel() {
		return new FigureModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected FigureModel getComponentModel() {
		return (FigureModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected FigureModel getOrCreateComponentModel() {
		return (FigureModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WFigure.
	 */
	public static class FigureModel extends ComponentModel {

		/**
		 * Indicates how the WFigure should operate.
		 */
		private FigureMode mode;

		/**
		 * The margins to be used on the WFigure.
		 */
		private Margin margin;
	}
}
