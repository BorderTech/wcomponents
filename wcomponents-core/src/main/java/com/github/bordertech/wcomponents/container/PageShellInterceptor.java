package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Headers;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Factory;
import java.io.PrintWriter;

/**
 * HtmlComponent adds in some HTTP headers and elements commonly used in HTML-based web apps. This interceptor is used
 * when running in a servlet environment, without any theme and skin.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class PageShellInterceptor extends InterceptorComponent {

	/**
	 * Override preparePaint in order to prepare the headers.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaint(final Request request) {
		Headers headers = this.getUI().getHeaders();
		headers.reset();

		headers.setContentType(WebUtilities.CONTENT_TYPE_XML);

		super.preparePaint(request);
	}

	/**
	 * Produce the html output.
	 *
	 * @param renderContext the renderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
		PrintWriter writer = webRenderContext.getWriter();

		beforePaint(writer);
		getBackingComponent().paint(renderContext);
		afterPaint(writer);
	}

	/**
	 * Renders the content before the backing component.
	 *
	 * @param writer the writer to write to.
	 */
	protected void beforePaint(final PrintWriter writer) {
		PageShell pageShell = Factory.newInstance(PageShell.class);

		pageShell.openDoc(writer);
		pageShell.writeHeader(writer);
	}

	/**
	 * Renders the content after the backing component.
	 *
	 * @param writer the writer to write to.
	 */
	protected void afterPaint(final PrintWriter writer) {
		PageShell pageShell = Factory.newInstance(PageShell.class);

		pageShell.writeFooter(writer);
		pageShell.closeDoc(writer);
	}
}
