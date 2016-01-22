package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTemplate;
import static com.github.bordertech.wcomponents.render.webxml.VelocityRenderer.LIST_SUFFIX;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.template.TemplateRenderer;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		String templateName = template.getTemplateName();
		String engine = template.getTemplateEngineClassName();
		if (Util.empty(engine)) {
			engine = TemplateRendererFactory.DEFAULT_FACTORY;
		}
		TemplateRenderer templateRenderer = TemplateRendererFactory.newInstance(engine);

		// Render the template
		templateRenderer.render(templateName, context, componentsByKey, renderContext.getWriter(), true);
	}

	/**
	 * Fills the given velocity context with data from the component which is being rendered. A map of components is
	 * also built up, in order to support deferred rendering.
	 *
	 * @param component the current component being rendered.
	 * @param context the velocity context to modify.
	 * @param componentsByKey a map to store components for deferred rendering.
	 */
	private void fillContext(final WTemplate component,
			final Map<String, Object> context, final Map<String, WComponent> componentsByKey) {

		// Also make the component available under the "this" key.
		context.put("this", component);

		// Make the UIContext available under the "uicontext" key.
		UIContext uic = UIContextHolder.getCurrent();
		context.put("uicontext", uic);
		context.put("uic", uic);

		// Load any extra parameters
		context.putAll(component.getParameters());

		// As well as going into their own named slots, visible children are also
		// placed into a list called children
		ArrayList<String> children = new ArrayList<>();

		Map<WComponent, String> tags = component.getTaggedComponents();

		for (WComponent child : component.getChildren()) {
			String tag = tags.get(child);

			// The key needs to be something which would never be output by a Velocity template.
			String key = "<TemplateLayout" + child.getId() + "/>";
			componentsByKey.put(key, child);

			addToContext(context, tag, key);

			if (child.isVisible()) {
				children.add(key);
			}
		}
		context.put("children", children);

		// Put the context in the context
		context.put("context", context);
	}

	/**
	 * Adds a name/value pair to the Velocity context. If the name parameter ends with {@link #LIST_SUFFIX}
	 *
	 * @param context the context to add to.
	 * @param name the name
	 * @param value the value
	 */
	private void addToContext(final Map<String, Object> context, final String name, final Object value) {
		if (name.endsWith(LIST_SUFFIX)) {
			// We want to use lists
			Object already = context.get(name);
			if (already != null && !(already instanceof List)) {
				throw new SystemException(
						"VelocityContext contained " + already + " instead of List under " + name);
			}

			List list = (List) context.get(name);

			if (list == null) {
				list = new ArrayList();
				context.put(name, list);
			}

			list.add(value);
		} else {
			context.put(name, value);
		}
	}

}
