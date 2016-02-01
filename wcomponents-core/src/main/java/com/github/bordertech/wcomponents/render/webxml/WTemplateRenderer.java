package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
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
 * @author Yiannis Paschalidis
 * @since 1.0.0
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
		Map<String, WComponent> componentsByKey = new HashMap<>();
		fillContext(template, context, componentsByKey);

		// Get template renderer
		String engine = template.getEngineName();
		if (Util.empty(engine)) {
			engine = TemplateRendererFactory.DEFAULT_ENGINE_NAME;
		}
		TemplateRenderer templateRenderer = TemplateRendererFactory.newInstance(engine);

		// Render
		if (!Util.empty(template.getTemplateName())) {
			// Render the template
			templateRenderer.renderTemplate(template.getTemplateName(), context, componentsByKey, renderContext.getWriter(), template.getEngineOptions());
		} else if (!Util.empty(template.getInlineTemplate())) {
			// Render inline
			templateRenderer.renderInline(template.getInlineTemplate(), context, componentsByKey, renderContext.getWriter(), template.getEngineOptions());
		}
	}

	/**
	 * Fills the given context with data from the component which is being rendered. A map of components is also built
	 * up, in order to support deferred rendering.
	 *
	 * @param component the current component being rendered.
	 * @param context the context to modify.
	 * @param componentsByKey a map to store components for deferred rendering.
	 */
	protected void fillContext(final WTemplate component,
			final Map<String, Object> context, final Map<String, WComponent> componentsByKey) {

		// Make the component available under the "this" key.
		context.put("wc", component);

		// Make the UIContext available under the "uic" key.
		UIContext uic = UIContextHolder.getCurrent();
		context.put("uic", uic);

		// Load any extra parameters
		context.putAll(component.getParameters());

		Map<WComponent, String> tags = component.getTaggedComponents();

		// Replace each component tag with the key so it can be used in the replace writer
		for (WComponent child : component.getChildren()) {
			String tag = tags.get(child);

			// The key needs to be something which would never be output by a Template.
			String key = "<TemplateLayout" + child.getId() + "/>";
			componentsByKey.put(key, child);
			context.put(tag, key);
		}

		context.put("context", context);
	}
}
