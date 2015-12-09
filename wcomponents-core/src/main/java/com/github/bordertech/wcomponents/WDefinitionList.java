package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Duplet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * WDefinitionList is used to render pair lists of terms/data.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDefinitionList extends AbstractNamingContextContainer implements AjaxTarget,
		SubordinateTarget, Marginable {

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
		for (WComponent component : data) {
			if (component != null) {
				content.add(component, term);
			}
		}

		// If the term doesn't exist, we may need to add a dummy component
		if (getComponentsForTerm(term).isEmpty()) {
			content.add(new DefaultWComponent(), term);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final WComponent child) {
		content.remove(child);
	}

	/**
	 * Removes a term from the definition list. All data (components) for the term is removed.
	 *
	 * @param term the term to remove.
	 */
	public void removeTerm(final String term) {
		for (WComponent child : getComponentsForTerm(term)) {
			content.remove(child);
		}
	}

	/**
	 * Groups a definition list's child components by their term for rendering.
	 *
	 * @return a list of this definition list's children grouped by their terms.
	 */
	public List<Duplet<String, List<WComponent>>> getTerms() {
		Map<String, Duplet<String, List<WComponent>>> componentsByTerm = new HashMap<>();
		List<Duplet<String, List<WComponent>>> result = new ArrayList<>();

		List<WComponent> childList = content.getComponentModel().getChildren();

		if (childList != null) {
			for (int i = 0; i < childList.size(); i++) {
				WComponent child = childList.get(i);
				String term = child.getTag();

				Duplet<String, List<WComponent>> termComponents = componentsByTerm.get(term);

				if (termComponents == null) {
					termComponents = new Duplet<String, List<WComponent>>(term,
							new ArrayList<WComponent>());
					componentsByTerm.put(term, termComponents);
					result.add(termComponents);
				}

				termComponents.getSecond().add(child);
			}
		}

		return result;
	}

	/**
	 * Retrieves the components for the given term.
	 *
	 * @param term the term of the children to be retrieved.
	 * @return the child components for the given term, may be empty.
	 */
	private List<WComponent> getComponentsForTerm(final String term) {
		List<WComponent> childList = content.getComponentModel().getChildren();
		List<WComponent> result = new ArrayList<>();

		if (childList != null) {
			for (int i = 0; i < childList.size(); i++) {
				WComponent child = childList.get(i);

				if (term.equals(child.getTag())) {
					result.add(child);
				}
			}
		}

		return result;
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
