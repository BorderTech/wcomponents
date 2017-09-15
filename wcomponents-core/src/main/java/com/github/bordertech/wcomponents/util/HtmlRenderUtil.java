package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.Mandatable;
import com.github.bordertech.wcomponents.Placeholderable;

/**
 * This utility class provides common helpers needed when rendering components.
 * @author Rick Brown
 * @since 1.4
 */
public final class HtmlRenderUtil {

	/**
	 * Gets the placeholder text to use on a "Placeholderable" component and handles cases where no explicit placeholder has been set
	 * but a meaningful placeholder message can be inferred.
	 * @param wcomponent The component which will take the placeholder text.
	 * @return Either the explicitly set placeholder or a meaningful fallback, if possible.
	 */
	public static String getEffectivePlaceholder(final Placeholderable wcomponent) {
		String placeholder = wcomponent.getPlaceholder();
		if (placeholder == null && wcomponent instanceof Mandatable) {
			Mandatable field = (Mandatable) wcomponent;
			if (field.isMandatory()) {
				placeholder = I18nUtilities.format(null, InternalMessages.DEFAULT_REQUIRED_FIELD_PLACEHOLDER);
			}
		}
		return placeholder;
	}

	/**
	 * Prevent instantiation.
	 */
	private HtmlRenderUtil() {
	}
}
