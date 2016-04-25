package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabSetType;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WTabSet} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WTabSetRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WTabSet.
	 *
	 * @param component the WTabSet to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WTabSet tabSet = (WTabSet) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:tabset");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendAttribute("type", getTypeAsString(tabSet.getType()));
		xml.appendOptionalAttribute("disabled", tabSet.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", tabSet.isHidden(), "true");
		xml.appendOptionalAttribute("contentHeight", tabSet.getContentHeight());
		xml.appendOptionalAttribute("showHeadOnly", tabSet.isShowHeadOnly(), "true");
		xml.appendOptionalAttribute("single",
				TabSetType.ACCORDION.equals(tabSet.getType()) && tabSet.isSingle(),
				"true");
		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(tabSet, renderContext);

		paintChildren(tabSet, renderContext);

		xml.appendEndTag("ui:tabset");
	}

	/**
	 * Determines the theme tabset type name for the given TabSetType.
	 *
	 * @param type the TabSetType
	 * @return the theme tabset type name for the given TabSetType.
	 */
	public static String getTypeAsString(final WTabSet.TabSetType type) {
		switch (type) {
			case TOP:
				return "top";
			case LEFT:
				return "left";
			case RIGHT:
				return "right";
			case ACCORDION:
				return "accordion";
			case APPLICATION:
				return "application";
			default:
				throw new IllegalStateException("Invalid tab set type: " + type);
		}
	}
}
