package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.WComponent;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for template renderers.
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public final class TemplateUtil {

	/**
	 * Prevent instantiation.
	 */
	private TemplateUtil() {
	}

	/**
	 * Replace each component tag with the key so it can be used in the replace writer.
	 *
	 * @param context the context to modify.
	 * @param taggedComponents the tagged components
	 *
	 * @return the keyed components
	 */
	public static Map<String, WComponent> mapTaggedComponents(final Map<String, Object> context, final Map<String, WComponent> taggedComponents) {

		Map<String, WComponent> componentsByKey = new HashMap<>();

		// Replace each component tag with the key so it can be used in the replace writer
		for (Map.Entry<String, WComponent> tagged : taggedComponents.entrySet()) {
			String tag = tagged.getKey();
			WComponent comp = tagged.getValue();

			// The key needs to be something which would never be output by a Template.
			String key = "[WC-TemplateLayout-" + tag + "]";
			componentsByKey.put(key, comp);

			// Map the tag to the key in the context
			context.put(tag, key);
		}

		return componentsByKey;
	}
}
