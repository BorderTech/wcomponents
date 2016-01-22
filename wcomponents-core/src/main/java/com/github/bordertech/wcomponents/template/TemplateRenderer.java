package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.WComponent;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author jonathan
 */
public interface TemplateRenderer {

	void render(final String templateName, final Map<String, Object> context, final Map<String, WComponent> componentsByKey, final PrintWriter writer, final boolean debug);

}
