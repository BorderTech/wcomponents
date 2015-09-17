package com.github.bordertech.wcomponents;

/**
 * A {@link WLabel} which stores the count of action/render phases for each session.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class MockLabel extends WLabel {

	/**
	 * Constructs a MockLabel.
	 *
	 * @param text the label text.
	 */
	public MockLabel(final String text) {
		super(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LabelModel newComponentModel() {
		return new CountModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);

		((MockLabel.CountModel) getOrCreateComponentModel()).handleRequestCount++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		super.paintComponent(renderContext);

		((MockLabel.CountModel) getOrCreateComponentModel()).paintCount++;
	}

	/**
	 * Retrieves the number of invocations of the handleRequest method for the given UIContext.
	 *
	 * @return the number of invocations.
	 */
	public int getHandleRequestCount() {
		return ((MockLabel.CountModel) getComponentModel()).handleRequestCount;
	}

	/**
	 * Retrieves the number of invocations of the paint method for the given UIContext.
	 *
	 * @return the number of invocations.
	 */
	public int getPaintCount() {
		return ((MockLabel.CountModel) getComponentModel()).paintCount;
	}

	/**
	 * An extension of the label data model to record request/paint invocation counts.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class CountModel extends WLabel.LabelModel {

		private int handleRequestCount = 0;
		private int paintCount = 0;
	}
}
