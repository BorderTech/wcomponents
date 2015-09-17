package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WText;

/**
 * Demonstrates application behaviour when errors occur at certain parts of the WComponent workflow.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class ErrorGenerator extends WContainer {

	/**
	 * A button to generate an exception in the action phase.
	 */
	private final WButton actionErrorBtn = new WButton("Generate exception in Action");
	/**
	 * A button to generate an exception in the request handling phase.
	 */
	private final WButton handleRequestErrorBtn = new WButton("Generate exception in handleRequest");
	/**
	 * A button to generate an exception in the prepare-paint phase.
	 */
	private final WButton preparePaintComponentErrorBtn = new WButton(
			"Generate exception in preparePaintComponent");
	/**
	 * A button to generate an exception in the render phase.
	 */
	private final WButton paintComponentErrorBtn = new WButton(
			"Generate exception in paintComponent");

	/**
	 * Creates an ErrorGenerator.
	 */
	public ErrorGenerator() {
		actionErrorBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				throw new MyRuntimeException("A deliberate runtime exception thrown in Action.");
			}
		});

		add(new TextDuplicator());

		// TODO: This is bad - use a layout instead
		WText lineBreak = new WText("<br />");
		lineBreak.setEncodeText(false);
		add(lineBreak);

		add(actionErrorBtn);
		add(handleRequestErrorBtn);
		add(preparePaintComponentErrorBtn);
		add(paintComponentErrorBtn);
	}

	/**
	 * Override handleRequest to throw an exception if handleRequestErrorBtn is pressed.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);

		if (handleRequestErrorBtn.isPressed()) {
			throw new MyRuntimeException("A deliberate runtime exception thrown in handleRequest.");
		}
	}

	/**
	 * Override preparePaintComponent to throw an exception if preparePaintComponentErrorBtn is pressed.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (preparePaintComponentErrorBtn.isPressed()) {
			throw new MyRuntimeException(
					"A deliberate runtime exception thrown in preparePaintComponent.");
		}

		super.preparePaintComponent(request);
	}

	/**
	 * Override paintComponent to throw an exception if paintComponentErrorBtn is pressed.
	 *
	 * @param renderContext the renderContext to write send the output to.
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		if (paintComponentErrorBtn.isPressed()) {
			throw new MyRuntimeException("A deliberate runtime exception thrown in paintComponent.");
		}

		super.paintComponent(renderContext);
	}

	/**
	 * A simple run-time exception.
	 *
	 * @author Martin Shevchenko
	 */
	private static final class MyRuntimeException extends RuntimeException {

		/**
		 * Creates a MyRuntimeException.
		 *
		 * @param msg the message.
		 */
		private MyRuntimeException(final String msg) {
			super(msg);
		}
	}
}
