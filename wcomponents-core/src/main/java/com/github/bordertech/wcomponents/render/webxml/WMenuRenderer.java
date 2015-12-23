package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for the {@link WMenu} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WMenuRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WMenu.
	 *
	 * @param component the WMenu to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMenu menu = (WMenu) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int rows = menu.getRows();

		xml.appendTagOpen("ui:menu");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");

		switch (menu.getType()) {
			case BAR:
				xml.appendAttribute("type", "bar");
				break;

			case FLYOUT:
				xml.appendAttribute("type", "flyout");
				break;

			case TREE:
				xml.appendAttribute("type", "tree");
				break;

			case COLUMN:
				xml.appendAttribute("type", "column");
				break;

			default:
				throw new IllegalStateException("Invalid menu type: " + menu.getType());
		}

		xml.appendOptionalAttribute("disabled", menu.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", menu.isHidden(), "true");
		xml.appendOptionalAttribute("rows", rows > 0, rows);

		switch (menu.getSelectionMode()) {
			case NONE:
				break;

			case SINGLE:
				xml.appendAttribute("selectMode", "single");
				break;

			case MULTIPLE:
				xml.appendAttribute("selectMode", "multiple");
				break;

			default:
				throw new IllegalStateException("Invalid select mode: " + menu.getSelectMode());
		}

		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(menu, renderContext);

		paintChildren(menu, renderContext);

		xml.appendEndTag("ui:menu");
	}
}
