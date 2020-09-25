package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Duplet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * WDefinitionList is used to render pair lists of terms/data.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDefinitionList extends AbstractNamingContextContainer implements AjaxTarget,
		SubordinateTarget, Marginable {

	private static final String TERM_ATTRIBUTE = "WDefinitionList.term";

	/**
	 * The layout options.
	 */
	public enum Type {
		/**
		 * A layout where elements are placed on the same line.
		 */
		FLAT,
		/**
		 * A layout where elements are placed vertically.
		 */
		STACKED,
		/**
		 * A layout where elements are placed in columns.
		 */
		COLUMN,
		/**
		 * A layout where elements are placed ???.
		 */
		NORMAL
	}

	/**
	 * Content is added to this hidden container, so nothing can be added to the definition list directly.
	 */
	private final WContainer content = new WContainer();

	/**
	 * Creates an empty WDefinitionList with a {@link Type#NORMAL} layout.
	 */
	public WDefinitionList() {
		this(Type.NORMAL);
	}

	/**
	 * Creates an empty WDefinitionList with the given layout type.
	 *
	 * @param type the layout type.
	 */
	public WDefinitionList(final Type type) {
		add(content);
		getComponentModel().type = type;
	}

	/**
	 * @return the layout type.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * Sets the layout type.
	 *
	 * @param layout The layout to set.
	 */
	public void setType(final Type layout) {
		getOrCreateComponentModel().type = layout;
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
	 * Adds a term to this definition list. If there is an existing term, the component is added to the list of data for
	 * the term.
	 *
	 * @param term the term to add.
	 * @param data the term data.
	 */
	public void addTerm(final String term, final WComponent... data) {
		getOrAddTermContainer(term)
			.ifPresent(container -> {
				for (WComponent datum : data) {
					datum.setTag(term);
					container.add(datum);
				}
			});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final WComponent child) {
		getTermContainers()
			.stream()
			.forEach(container -> {
				if (container.getChildren().contains(child)) {
					container.remove(child);
				}
			});
	}

	/**
	 * Removes a term from the definition list. All data (components) for the term is removed.
	 *
	 * @param term the term to remove.
	 */
	public void removeTerm(final String term) {
		getTermContainer(term).ifPresent(container -> content.remove(container));
	}

	/**
	 * Groups a definition list's child components by their term for rendering.
	 *
	 * @return a list of this definition list's children grouped by their terms.
	 */
	public List<Duplet<String, ArrayList<WComponent>>> getTerms() {
		return getTermContainers()
				.stream()
				.map(container ->
					new Duplet<>((String) container.getAttribute(TERM_ATTRIBUTE),
						new ArrayList<>(container.getChildren())))
				.collect(Collectors.toList());
	}

	private List<WContainer> getTermContainers() {
		return content
			.getChildren()
			.stream()
			.filter(child -> (child instanceof WContainer) && ((WContainer) child).getAttribute(TERM_ATTRIBUTE) != null)
			.map(child -> (WContainer) child)
			.collect(Collectors.toList());
	}

	private Optional<WContainer> getOrAddTermContainer(final String term) {

		final Optional<WContainer> container = getTermContainer(term);

		if (term != null && !container.isPresent()) {
			WContainer newContainer = new WContainer();
			newContainer.setAttribute(TERM_ATTRIBUTE, term);
			content.add(newContainer);
			return Optional.of(newContainer);
		}

		return container;
	}

	private Optional<WContainer> getTermContainer(final String term) {
		return getTermContainers()
				.stream()
				.filter(container -> term != null && term.equals(container.getAttribute(TERM_ATTRIBUTE)))
				.findFirst();
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new DefinitionListModel.
	 */
	@Override // For type safety only
	protected DefinitionListModel newComponentModel() {
		return new DefinitionListModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected DefinitionListModel getComponentModel() {
		return (DefinitionListModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected DefinitionListModel getOrCreateComponentModel() {
		return (DefinitionListModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class DefinitionListModel extends ComponentModel {

		/**
		 * The layout.
		 */
		private Type type = Type.NORMAL;

		/**
		 * The margins to be used on the defintion list.
		 */
		private Margin margin;
	}
}
