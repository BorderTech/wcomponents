package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTemplate;
import java.io.Writer;
import java.util.Map;

/**
 * Template renderer.
 * <p>
 * A template can be passed inline or loaded from a resource file.
 * </p>
 * <p>
 * Variables are passed to the template via the context which is a map of String keys and data objects.
 * </p>
 * <p>
 * The template engines used to render the template can have configuration passed in via the engine options map.
 * </p>
 *
 * @see TemplateRendererFactory
 * @see WTemplate
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public interface TemplateRenderer {

	/**
	 * Render a template loaded from a resource.
	 * <p>
	 * Depending on how the Template Engine loads the template resource (ie by ClassLoader or Class) will determine the
	 * format of the template resource.
	 * </p>
	 * <p>
	 * ClassLoader.getResource() and Class.getResource() work differently.
	 * </p>
	 * <p>
	 * The methods in ClassLoader use the given String as the name of the resource without applying any
	 * absolute/relative transformation. The name should not have a leading “/”.
	 * </p>
	 * <p>
	 * The methods in Class can use a relative (“path/resource.xml”) or absolute path (“/path/resource.xml”). Relative
	 * means, relative to the location in the classpath, where the method was called. The path will be appended if
	 * needed (ie my/packaage/path/resopurce.xml). Absolute will be used as is, only first / will be removed before the
	 * search (ie path/resource.xml).
	 * </p>
	 *
	 * @param templateName the template name and its path
	 * @param context the context for the template
	 * @param taggedComponents the tagged components
	 * @param writer the writer
	 * @param options the engine options
	 */
	void renderTemplate(final String templateName, final Map<String, Object> context, final Map<String, WComponent> taggedComponents, final Writer writer, final Map<String, Object> options);

	/**
	 * Render an inline template.
	 *
	 * @param templateInline the inline template
	 * @param context the context for the template
	 * @param taggedComponents the tagged components
	 * @param writer the writer
	 * @param options the engine options
	 */
	void renderInline(final String templateInline, final Map<String, Object> context, final Map<String, WComponent> taggedComponents, final Writer writer, final Map<String, Object> options);

}
