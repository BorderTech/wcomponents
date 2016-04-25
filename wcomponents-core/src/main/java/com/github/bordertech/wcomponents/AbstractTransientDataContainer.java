package com.github.bordertech.wcomponents;

/**
 * AbstractTransientContextContainer ensures that all child components have transient state - ie will have their UI
 * contexts reset after each request.
 *
 * The intended usage pattern is to set an attribute in this component's UIContext in the handleRequest(Request) method
 * or from within an action. The attribute can then be used in the {@link #setupData()} method to read in the data (e.g.
 * from the application cache) and pass it to the children.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractTransientDataContainer extends AbstractMutableContainer {

	/**
	 * After the component has been painted, the UIContexts for this component and all its children are cleared.
	 *
	 * @param renderContext the renderContext to send output to.
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);

		final int size = getChildCount();

		for (int i = 0; i < size; i++) {
			WComponent child = getChildAt(i);
			child.reset();
		}
	}

	/**
	 * Prepares the component for painting. The {@link #setupData()} method is called before preparePaint is called for
	 * the child components.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public final void preparePaintComponent(final Request request) {
		setupData();
		super.preparePaintComponent(request);
	}

	/**
	 * Subclasses must implement this method to ensure that all child components are set up correctly before they are
	 * painted.
	 */
	public abstract void setupData();
}
