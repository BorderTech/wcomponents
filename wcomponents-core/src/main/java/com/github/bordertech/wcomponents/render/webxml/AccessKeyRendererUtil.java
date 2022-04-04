package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AccessKeyable;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for rendering access key details.
 */
public final class AccessKeyRendererUtil {

	/**
	 * Prevent instantiation of utility class.
	 */
	private AccessKeyRendererUtil() {
		// Do nothing
	}

	/**
	 * Add the optional XML attribute for access key.
	 *
	 * @param component the component with an access key
	 * @param renderContext the RenderContext to paint to
	 */
	public static void appendOptionalAccessKeyXMLAttribute(final AccessKeyable component, final WebXmlRenderContext renderContext) {
		XmlStringBuilder xml = renderContext.getWriter();
		xml.appendOptionalAttribute("accessKey", StringUtils.upperCase(component.getAccessKeyAsString()));
	}

	/**
	 * Render the HTML label for access key.
	 *
	 * @param component the component with an access key
	 * @param renderContext the RenderContext to paint to
	 */
	public static void renderAccessKeyHtmlLabel(final AccessKeyable component, final WebXmlRenderContext renderContext) {

		// Check if key provided
		String key = StringUtils.upperCase(component.getAccessKeyAsString());
		if (key == null) {
			return;
		}

		XmlStringBuilder xml = renderContext.getWriter();

		// Span with Access key label and standard tooltip id suffix
		xml.appendTagOpen("span");
		xml.appendAttribute("id", component.getId() + "_wctt");
		xml.appendAttribute("role", "tooltip");
		xml.appendAttribute("hidden", "hidden");
		xml.appendAttribute("aria-hidden", "true");
		xml.appendClose();
		xml.append(key);
		xml.appendEndTag("span");

	}

}
