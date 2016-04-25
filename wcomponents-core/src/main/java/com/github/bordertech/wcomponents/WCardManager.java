package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * <p>
 * A WCardManager is a wcomponent used to control the visibility of its child components. It can help to think of a
 * WCardManager as a deck of cards, where only the top most card is visible. The WCardManager enables you to add cards
 * to the deck (using the "add" method) and to select which of the cards to place on the top of the deck (using the
 * "makeVisible" method).
 * </p>
 * <p>
 * You would expect a WComponent that is using a WCardManager to support its functionality, to add all the cards it will
 * need in its constructor. Note that the first card added will be the default visible component. Implementations of the
 * Action interface are normally used to change which card is visible.
 * </p>
 *
 * @author James Gifford
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WCardManager extends AbstractMutableContainer {

	/**
	 * Cards are added to this container to exclude them from standard processing.
	 */
	private final WInvisibleContainer cardContainer = new WInvisibleContainer();

	/**
	 * Creates a WCardManager.
	 */
	public WCardManager() {
		super.add(cardContainer);
	}

	/**
	 * @return the card container
	 */
	protected WInvisibleContainer getCardContainer() {
		return cardContainer;
	}

	//================================
	// Attributes
	/**
	 * Returns the visible component (card) for the given context.
	 *
	 * @return the visible component.
	 */
	public WComponent getVisible() {
		CardManagerModel model = getComponentModel();
		return model.visibleComponent;
	}

	/**
	 * Sets the visible component (card) for the given context.
	 *
	 * @param component the visible component.
	 */
	public void makeVisible(final WComponent component) {
		if (component.getParent() != getCardContainer()) {
			throw new IllegalArgumentException(
					"Attempted to make a component visible which is not contained in this WCardManager");
		}

		CardManagerModel model = getOrCreateComponentModel();
		model.visibleComponent = component;
	}

	//================================
	// Structure
	/**
	 * Override add so that components are added to the (invisible) container.
	 *
	 * @param component the component to add.
	 */
	@Override
	public void add(final WComponent component) {
		getCardContainer().add(component);
		CardManagerModel model = getComponentModel();

		if (model.visibleComponent == null) {
			model.visibleComponent = component;
		}
	}

	/**
	 * Override remove so that components are removed from the (invisible) container.
	 *
	 * @param component the component to remove.
	 */
	@Override
	public void remove(final WComponent component) {
		getCardContainer().remove(component);
	}

	/**
	 * Override removeAll so that all components are removed from the (invisible) container.
	 */
	@Override
	public void removeAll() {
		getCardContainer().removeAll();
	}

	/**
	 * Since none of the children are visible to standard processing, handleRequest has been overridden so that the
	 * visible card is processed.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);

		WComponent visibleDialog = getVisible();

		if (visibleDialog != null) {
			visibleDialog.serviceRequest(request);
		}
	}

	/**
	 * Since none of the children are visible to standard processing, preparePaintComponent has been overridden so that
	 * the visible card is prepared.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		WComponent visibleDialog = getVisible();

		if (visibleDialog != null) {
			visibleDialog.preparePaint(request);
		}
	}

	/**
	 * Since none of the children are visible to standard processing, paintComponent has been overridden so that the
	 * visible card is painted.
	 *
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		super.paintComponent(renderContext);

		WComponent visibleDialog = getVisible();

		if (visibleDialog != null) {
			visibleDialog.paint(renderContext);
		}
	}

	/**
	 * Since none of the children are visible to standard processing, validateComponent has been overridden so that the
	 * visible card is processed.
	 *
	 * @param diags the list to add validation diagnostics to.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		WComponent visibleDialog = getVisible();

		if (visibleDialog != null) {
			visibleDialog.validate(diags);
		}
	}

	/**
	 * Override method to show Error indicators on the visible card.
	 *
	 * @param diags the list of diagnostics containing errors.
	 */
	@Override
	public void showErrorIndicators(final List<Diagnostic> diags) {
		WComponent visibleComponent = getVisible();
		visibleComponent.showErrorIndicators(diags);
	}

	/**
	 * Override method to show Warning indicators on the visible card.
	 *
	 * @param diags the list of diagnostics containing warnings.
	 */
	@Override
	public void showWarningIndicators(final List<Diagnostic> diags) {
		WComponent visibleComponent = getVisible();
		visibleComponent.showWarningIndicators(diags);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		int index = getCardContainer().getIndexOfChild(getVisible());
		List<WComponent> cards = getCardContainer().getComponentModel().getChildren();
		WComponent[] children = cards == null ? new WComponent[0] : cards.toArray(
				new WComponent[cards.size()]);

		return toString("active=" + index, -1, -1) + childrenToString(children);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new CardManagerModel.
	 */
	@Override
	protected CardManagerModel newComponentModel() {
		return new CardManagerModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected CardManagerModel getComponentModel() {
		return (CardManagerModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected CardManagerModel getOrCreateComponentModel() {
		return (CardManagerModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a CardManager.
	 */
	public static class CardManagerModel extends ComponentModel {

		/**
		 * The currently visible component.
		 */
		private WComponent visibleComponent;
	}
}
