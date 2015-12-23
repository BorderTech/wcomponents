package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WSubMenu.MenuMode;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The Renderer for {@link WSubMenu}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WSubMenuRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WSubMenu.
	 *
	 * @param component the WSubMenu to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WSubMenu menu = (WSubMenu) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:submenu");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("open", menu.isOpen(), "true");
		xml.appendOptionalAttribute("disabled", menu.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", menu.isHidden(), "true");

		Boolean selectable = menu.isSelectable();

		if (selectable != null) {
			xml.appendAttribute("selectable", selectable.toString());
		}

		xml.appendOptionalAttribute("selected", menu.isSelected(), "true");
		xml.appendOptionalAttribute("accessKey", Util.upperCase(menu.getAccessKeyAsString()));

		switch (menu.getMode()) {
			case CLIENT:
				xml.appendAttribute("mode", "client");
				break;
			case LAZY:
				xml.appendAttribute("mode", "lazy");
				break;
			case EAGER:
				xml.appendAttribute("mode", "eager");
				break;
			case DYNAMIC:
				xml.appendAttribute("mode", "dynamic");
				break;
			case SERVER:
				xml.appendAttribute("mode", "server");
				break;
			default:
				throw new SystemException("Unknown menu mode: " + menu.getMode());
		}

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

		// Paint label
		menu.getDecoratedLabel().paint(renderContext);

		MenuMode mode = menu.getMode();

		// Paint submenu items
		xml.appendTagOpen("ui:content");
		xml.appendAttribute("id", component.getId() + "-content");
		xml.appendClose();

		// Render content if not EAGER Mode or is EAGER and is the current AJAX request
		if (mode != MenuMode.EAGER || AjaxHelper.isCurrentAjaxTrigger(menu)) {
			// Visibility of content set in prepare paint
			menu.paintMenuItems(renderContext);
		}

		xml.appendEndTag("ui:content");

		xml.appendEndTag("ui:submenu");
	}

}
