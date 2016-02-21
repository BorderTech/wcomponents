package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.template.TemplateRenderer;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.util.Util;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Renderer} for the {@link WTemplate} component.
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
final class WTemplateRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WTemplate.
	 *
	 * @param component the WTemplate to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WTemplate template = (WTemplate) component;

		// Setup the context
		Map<String, Object> context = new HashMap<>();
		// Make the component available under the "wc" key.
		context.put("wc", template);
		// Load the parameters
		context.putAll(template.getParameters());

		// Get template renderer for the engine
		String engine = template.getEngineName();
		if (Util.empty(engine)) {
			engine = TemplateRendererFactory.DEFAULT_ENGINE_NAME;
		}
		TemplateRenderer templateRenderer = TemplateRendererFactory.newInstance(engine);

		// Render
		if (!Util.empty(template.getTemplateName())) {
			// Render the template
			templateRenderer.renderTemplate(template.getTemplateName(), context, template.getTaggedComponents(), renderContext.getWriter(), template.getEngineOptions());
		} else if (!Util.empty(template.getInlineTemplate())) {
			// Render inline
			templateRenderer.renderInline(template.getInlineTemplate(), context, template.getTaggedComponents(), renderContext.getWriter(), template.getEngineOptions());
		}
	}

}
