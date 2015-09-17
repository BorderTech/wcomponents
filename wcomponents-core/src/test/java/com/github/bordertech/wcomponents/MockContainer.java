package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * This component doesn't do anything important, but does note which methods were called.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockContainer extends AbstractMutableContainer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CountModel newComponentModel() {
		return new CountModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);
		((CountModel) getOrCreateComponentModel()).handleRequestCount++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		((CountModel) getOrCreateComponentModel()).preparePaintCount++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		super.paintComponent(renderContext);
		((CountModel) getOrCreateComponentModel()).paintCount++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(final List<Diagnostic> diags) {
		super.validate(diags);
		((CountModel) getOrCreateComponentModel()).validateCount++;
	}

	/**
	 * @return the number of times handleRequest has been called in the current context.
	 */
	public int getHandleRequestCount() {
		return ((CountModel) getComponentModel()).handleRequestCount;
	}

	/**
	 * @return the number of times paintComponent has been called in the current context.
	 */
	public int getPaintCount() {
		return ((CountModel) getComponentModel()).paintCount;
	}

	/**
	 * @return the number of times preparePaintComponent has been called in the current context.
	 */
	public int getPreparePaintCount() {
		return ((CountModel) getComponentModel()).paintCount;
	}

	/**
	 * @return the number of times validate has been called in the current context.
	 */
	public int getValidateCount() {
		return ((CountModel) getComponentModel()).validateCount;
	}

	/**
	 * An component model extension which records certain method invocation counts.
	 */
	public static class CountModel extends ComponentModel {

		/**
		 * The number of times that the handleRequest method has been invoked.
		 */
		private int handleRequestCount = 0;
		/**
		 * The number of times that the preparePaintComponent method has been invoked.
		 */
		private int preparePaintCount = 0;
		/**
		 * The number of times that the paintComponent method has been invoked.
		 */
		private int paintCount = 0;
		/**
		 * The number of times that the validate method has been invoked.
		 */
		private int validateCount = 0;
	}
}
