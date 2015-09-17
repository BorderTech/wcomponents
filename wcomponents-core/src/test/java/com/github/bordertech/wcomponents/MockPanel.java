package com.github.bordertech.wcomponents;

/**
 * A WPanel which stores the count of action/render phases for each session.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class MockPanel extends WPanel {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PanelModel newComponentModel() {
		return new CountModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);

		((MockPanel.CountModel) getOrCreateComponentModel()).handleRequestCount++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		super.paintComponent(renderContext);

		((MockPanel.CountModel) getOrCreateComponentModel()).paintCount++;
	}

	/**
	 * Retrieves the number of invocations of the handleRequest method for the given UIContext.
	 *
	 * @return the number of invocations.
	 */
	public int getHandleRequestCount() {
		return ((MockPanel.CountModel) getComponentModel()).handleRequestCount;
	}

	/**
	 * Retrieves the number of invocations of the paint method for the given UIContext.
	 *
	 * @return the number of invocations.
	 */
	public int getPaintCount() {
		return ((MockPanel.CountModel) getComponentModel()).paintCount;
	}

	/**
	 * An extension of the panel data model to record request/paint invocation counts.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class CountModel extends WPanel.PanelModel {

		private int handleRequestCount = 0;
		private int paintCount = 0;
	}
}
