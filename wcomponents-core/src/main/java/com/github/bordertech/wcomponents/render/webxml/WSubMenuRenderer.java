package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WSubMenu.MenuMode;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * The Renderer for {@link WSubMenu}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
final class WSubMenuRenderer extends AbstractWebXmlRenderer {

	/**
	 * Only SubMenus in a TREE are allowed to be open.
	 * @param submenu the WSubMenu to test
	 * @return the open state of submenus inside a tree or false for all other submenus
	 */
	private boolean isOpen(final WSubMenu submenu) {
		if (!submenu.isOpen()) {
			return false;
		}
		WMenu menu = WebUtilities.getAncestorOfClass(WMenu.class, submenu);
		return menu != null && WMenu.MenuType.TREE == menu.getType();
	}

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
		xml.appendOptionalAttribute("open", isOpen(menu), "true");
		xml.appendOptionalAttribute("disabled", menu.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", menu.isHidden(), "true");
		if (menu.isTopLevelMenu()) {
			xml.appendOptionalAttribute("accessKey", menu.getAccessKeyAsString());
		}

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
				// mode server mapped to mode dynamic as per https://github.com/BorderTech/wcomponents/issues/687
				xml.appendAttribute("mode", "dynamic");
				break;
			default:
				throw new SystemException("Unknown menu mode: " + menu.getMode());
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
