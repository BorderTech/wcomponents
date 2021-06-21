package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.template.TemplateRenderer;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

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
		if (StringUtils.isBlank(engine)) {
			engine = ConfigurationProperties.getDefaultRenderingEngine();
		}
		TemplateRenderer templateRenderer = TemplateRendererFactory.newInstance(engine);

		// Render
		if (StringUtils.isNotBlank(template.getTemplateName())) {
			// Render the template
			templateRenderer.renderTemplate(template.getTemplateName(), context, template.getTaggedComponents(), renderContext.getWriter(),
					template.getEngineOptions());
		} else if (StringUtils.isNotBlank(template.getInlineTemplate())) {
			// Render inline
			templateRenderer.renderInline(template.getInlineTemplate(), context, template.getTaggedComponents(), renderContext.getWriter(),
					template.getEngineOptions());
		}
	}

}
