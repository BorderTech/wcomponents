package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WSection;
import com.github.bordertech.wcomponents.WSection.SectionMode;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * The Renderer for {@link WSection}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WSectionRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WSection.
	 *
	 * @param component the WSection to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WSection section = (WSection) component;
		XmlStringBuilder xml = renderContext.getWriter();

		boolean renderChildren = isRenderContent(section);

		xml.appendTagOpen("ui:section");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		if (SectionMode.LAZY.equals(section.getMode())) {
			xml.appendOptionalAttribute("hidden", !renderChildren, "true");
		} else {
			xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		}

		SectionMode mode = section.getMode();
		if (mode != null) {
			switch (mode) {
				case LAZY:
					xml.appendAttribute("mode", "lazy");
					break;
				case EAGER:
					xml.appendAttribute("mode", "eager");
					break;
				default:
					throw new SystemException("Unknown section mode: " + section.getMode());
			}
		}

		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(section, renderContext);

		if (renderChildren) {
			// Label
			section.getDecoratedLabel().paint(renderContext);
			// Content
			section.getContent().paint(renderContext);
		}

		xml.appendEndTag("ui:section");
	}

	/**
	 * @param section the section to paint.
	 * @return true if the section content needs to be rendered
	 */
	private boolean isRenderContent(final WSection section) {
		SectionMode mode = section.getMode();

		// EAGER sections only render content if the section is the current AJAX trigger
		if (SectionMode.EAGER.equals(mode)) {
			return AjaxHelper.isCurrentAjaxTrigger(section);
		} else if (SectionMode.LAZY.equals(mode)) {
			// LAZY sections render content if the section is not hidden or it is the current AJAX Trigger (ie content has
			// been requested)
			return (!section.isHidden() || AjaxHelper.isCurrentAjaxTrigger(section));
		}

		return true;
	}

}
