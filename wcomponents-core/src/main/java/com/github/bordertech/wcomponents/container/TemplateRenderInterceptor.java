package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.template.HandlebarsRendererImpl;
import com.github.bordertech.wcomponents.template.TemplateRenderer;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Render templates on the server (i.e. Handlebars).
 */
public class TemplateRenderInterceptor extends InterceptorComponent {

	/**
	 * Template engine.
	 */
	private static final TemplateRenderer TEMPLATE_RENDERER = TemplateRendererFactory.newInstance(TemplateRendererFactory.TemplateEngine.HANDLEBARS);

	/**
	 * No CACHE option.
	 */
	private static final Map<String, Object> OPTIONS;

	static {
		HashMap<String, Object> map = new HashMap<>();
		map.put(HandlebarsRendererImpl.USE_CACHE, "false");
		OPTIONS = Collections.unmodifiableMap(map);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final RenderContext renderContext) {
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
		TEMPLATE_RENDERER.renderInline(outputBuffer.toString(), Collections.EMPTY_MAP, Collections.EMPTY_MAP, writer, OPTIONS);
	}
}
