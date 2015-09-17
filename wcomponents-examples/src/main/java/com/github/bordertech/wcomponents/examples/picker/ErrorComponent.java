package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Displays an error message to the developer.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ErrorComponent extends WContainer {

	/**
	 * The error message to display.
	 */
	private final String message;

	/**
	 * The throwable which caused the error (if applicable).
	 */
	private final Throwable error;

	/**
	 * Creates an ErrorComponent with the given message.
	 *
	 * @param message the error message.
	 *
	 */
	public ErrorComponent(final String message) {
		this.message = message;
		this.error = null;
	}

	/**
	 * Creates an ErrorComponent with the given message and cause.
	 *
	 * @param message the error message.
	 * @param error the throwable which caused the error.
	 */
	public ErrorComponent(final String message, final Throwable error) {
		this.message = message;
		this.error = error;
	}

	/**
	 * Override in order to paint the component. Real applications should not emit HTML directly.
	 *
	 * @param renderContext the renderContext to send output to.
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		if (renderContext instanceof WebXmlRenderContext) {
			PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();
			writer.println(WebUtilities.encode(message));

			if (error != null) {
				writer.println("\n<br/>\n<pre>\n");

				StringWriter buf = new StringWriter();
				error.printStackTrace(new PrintWriter(buf));
				writer.println(WebUtilities.encode(buf.toString()));

				writer.println("\n</pre>");
			}
		}
	}
}
