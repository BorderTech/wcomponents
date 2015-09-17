package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Headers;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * Adds headline entries to a page. Headline entries are intended to only be added once to a page, but in a portal
 * environment this proves difficult. This interceptor is currently used on a per-portlet basis which is not really what
 * we want. It seems to work for now.
 *
 * @author Martin Shevchenko
 */
public class HeadLineInterceptor extends InterceptorComponent {

	/**
	 * Override preparePaint in order to perform processing specific to this interceptor. Any old headers are cleared
	 * out before preparePaint is called on the main UI.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaint(final Request request) {
		// Headers
		// The WHeaders comes from the root WComponent and is a
		// mechanism for WComponents to add their own headers
		// (eg more JavaScript references).

		Headers headers = this.getUI().getHeaders();
		headers.reset();

		super.preparePaint(request);
	}

	/**
	 * Override paint in order to perform processing specific to this interceptor. This implementation is responsible
	 * for rendering the headlines for the UI.
	 *
	 * @param renderContext the renderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		if (renderContext instanceof WebXmlRenderContext) {
			PageContentHelper.addAllHeadlines(((WebXmlRenderContext) renderContext).getWriter(),
					getUI().getHeaders());
		}

		getBackingComponent().paint(renderContext);
	}
}
