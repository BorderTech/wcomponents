package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.error.SystemFailureMapper;
import java.io.PrintWriter;

/**
 * A component used to display a "Fatal" error page, when an unhandled error occurs.
 *
 * @author Martin Shevchenko
 */
public class FatalErrorPage extends AbstractWComponent implements ErrorPage {

	/**
	 * Indicates whether additional developer details should be included on the error page.
	 */
	private final boolean developerFriendly;

	/**
	 * The unhandled error which caused this component to display.
	 */
	private final Throwable error;

	/**
	 * Creates a FatalErrorPage.
	 *
	 * @param developerFriendly if true, additional technical details will be included.
	 * @param error the unhandled error.
	 */
	public FatalErrorPage(final boolean developerFriendly, final Throwable error) {
		this.developerFriendly = developerFriendly;
		this.error = error;
	}

	/**
	 * @return a mapped version of the {@link #error}'s message.
	 */
	protected String getMessage() {
		SystemFailureMapper mapper = Factory.newInstance(SystemFailureMapper.class);
		String desc = I18nUtilities.format(null, mapper.toMessage(error));
		return desc;
	}

	/**
	 * Renders this FatalErrorPage.
	 *
	 * @param renderContext the RenderContext to send the rendered output to.
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		if (renderContext instanceof WebXmlRenderContext) {
			PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();
			writer.println(getMessage());

			if (developerFriendly) {
				// TODO: This should not emit mark-up
				writer.println("<pre style=\"background: lightgrey\">");
				writer.println("Additional details for the developer:");
				writer.println();
				error.printStackTrace(writer);
				writer.println("</pre>");
			}
		}
	}
}
