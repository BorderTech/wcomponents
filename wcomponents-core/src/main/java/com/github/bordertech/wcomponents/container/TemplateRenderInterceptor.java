package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.servlet.ServletRequest;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.template.TemplateRenderer;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Render templates on the server (i.e. Handlebars).
 */
public class TemplateRenderInterceptor extends InterceptorComponent {

	/**
	 * Template engine.
	 */
	private static final TemplateRenderer TEMPLATE_RENDERER = TemplateRendererFactory.newInstance(TemplateRendererFactory.TemplateEngine.HANDLEBARS);

	private static final Map<String, Object> CONTEXT;

	private boolean doRender = false;

	static {
		Map<String, Object> tmpContext = new HashMap<>();
		tmpContext.put("rendered", "server");
		CONTEXT = Collections.unmodifiableMap(tmpContext);
	}

	/**
	 * Override preparePaint in order to perform processing specific to this interceptor.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaint(final Request request) {
		// TODO this should check the the response is actually HTML / Handlebars (not XML)
		String mode = ConfigurationProperties.getTemplateRenderingMode();
		doRender = "on".equals(mode);
		if (!doRender && "sniff".equals(mode) && request instanceof ServletRequest) {
			HttpServletRequest httpServletRequest = ((ServletRequest) request).getBackingRequest();
			String userAgentString = httpServletRequest.getHeader("User-Agent");
			if (userAgentString.indexOf("MSIE 8.0") > 0 || userAgentString.indexOf("MSIE 9.0;") > 0) {
				doRender = true;
			}
		}
		super.preparePaint(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final RenderContext renderContext) {

		if (!doRender) {
			super.paint(renderContext);
			return;
		}

		UIContext uic = UIContextHolder.getCurrent();

		// Set up a render CONTEXT to buffer the Payload.
		StringWriter outputBuffer = new StringWriter();
		PrintWriter outputWriter = new PrintWriter(outputBuffer);
		WebXmlRenderContext outputContext = new WebXmlRenderContext(outputWriter, uic.getLocale());
		super.paint(outputContext);

		// Get a the PrintWriter to pass to TEMPLATE_RENDERER
		WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
		PrintWriter writer = webRenderContext.getWriter();

		// Transform handlebars
		TEMPLATE_RENDERER.renderInline(outputBuffer.toString(), CONTEXT, Collections.EMPTY_MAP, writer, Collections.EMPTY_MAP);
	}
}
