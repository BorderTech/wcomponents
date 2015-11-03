package com.github.bordertech.wcomponents;

/**
 * <p>
 * The WSection component defines a major discrete section of a screen which is associated with, and described by, a
 * heading.
 * </p>
 * <p>
 * Various {@link SectionMode modes of operation} are supported, which allow developers to tune performance by only
 * loading content when it is needed.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSection extends AbstractNamingContextContainer implements AjaxTarget,
		SubordinateTarget, Marginable, DropZone {

	/**
	 * The available types of section mode.
	 */
	public enum SectionMode {
		/**
		 * A lazy section will load its content via AJAX when it is made visible.
		 */
		LAZY,
		/**
		 * An eager section will load its content via AJAX immediately after the page is loaded.
		 */
		EAGER
	};

	/**
	 * The label for the section.
	 */
	private final WDecoratedLabel label;

	/**
	 * The content for the section.
	 */
	private final WPanel content;

	/**
	 * Creates a WSection with the given heading.
	 *
	 * @param heading the section's heading.
	 */
	public WSection(final String heading) {
		this(new WPanel(), new WDecoratedLabel(heading));
	}

	/**
	 * Creates a WSection with the given content and heading.
	 *
	 * @param content the content for the section
	 * @param heading the section's heading.
	 */
	public WSection(final WPanel content, final String heading) {
		this(content, new WDecoratedLabel(heading));
	}

	/**
	 * Creates a WSection with the given content and heading.
	 *
	 * @param content the content for the section.
	 * @param label the section's heading.
	 */
	public WSection(final WPanel content, final WDecoratedLabel label) {
		if (content == null) {
			throw new IllegalArgumentException("The content of a WSection cannot be null");
		}

		if (label == null) {
			throw new IllegalArgumentException("The label of a WSection cannot be null");
		}

		this.content = content;
		this.label = label;
		add(label);
		add(content);
	}

	/**
	 * @return this WSection's mode of operation
	 */
	public SectionMode getMode() {
		return getComponentModel().mode;
	}

	/**
	 * Sets this WSection's mode of operation.
	 *
	 * @param mode the mode of operation.
	 */
	public void setMode(final SectionMode mode) {
		getOrCreateComponentModel().mode = mode;
	}

	/**
	 * @return the content of this section
	 */
	public WPanel getContent() {
		return content;
	}

	/**
	 * @return the decorated label that is used to render the section heading.
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

		// Register section for AJAX
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
	 * @return a new SectionModel.
	 */
	@Override
	protected SectionModel newComponentModel() {
		return new SectionModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected SectionModel getComponentModel() {
		return (SectionModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected SectionModel getOrCreateComponentModel() {
		return (SectionModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WSection.
	 */
	public static class SectionModel extends ComponentModel {

		/**
		 * Indicates how the section should operate.
		 */
		private SectionMode mode;

		/**
		 * The margins to be used on the section.
		 */
		private Margin margin;
	}
}
