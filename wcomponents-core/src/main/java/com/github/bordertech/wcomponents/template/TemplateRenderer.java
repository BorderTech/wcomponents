package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.WComponent;
import java.io.Writer;
import java.util.Map;

/**
 *
 * @author jonathan
 */
public interface TemplateRenderer {

	/**
	 * Depending on how the Template Engine loads the template resource (ie by ClassLoader or Class) will determine the
	 * format of the template resource.
	 * <p>
	 * ClassLoader.getResource() and Class.getResource() work differently.
	 * </p>
	 * <p>
	 * The methods in ClassLoader use the given String as the name of the resource without applying any
	 * absolute/relative transformation. The name should not have a leading “/”.
	 * </p>
	 * <p>
	 * The methods in Class can use a relative (“path/resource.xml”) or absolute path (“/path/resource.xml”). Relative
	 * means, relative to the location in the classapth, where the method was called. The path will be appended if
	 * needed (ie my/packaage/path/resopurce.xml).
	 * </p>
	 * <p>
	 ** Absolute will be used as is, only first / will be removed before the search (ie path/resource.xml).
	 * </p>
	 *
	 * @param templateName the template name
	 * @param context the context for the template
	 * @param componentsByKey the components listed by key
	 * @param writer the writer
	 * @param options the engine options
	 */
	void renderTemplate(final String templateName, final Map<String, Object> context, final Map<String, WComponent> componentsByKey, final Writer writer, final Map<String, Object> options);

	/**
	 * @param templateInline the inline template
	 * @param context the context for the template
	 * @param componentsByKey the components listed by key
	 * @param writer the writer
	 * @param options the engine options
	 */
	void renderInline(final String templateInline, final Map<String, Object> context, final Map<String, WComponent> componentsByKey, final Writer writer, final Map<String, Object> options);

}
