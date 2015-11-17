package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPanel.PanelMode;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.layout.LayoutManager;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The {@link Renderer} for the {@link WPanel} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WPanelRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given container.
	 *
	 * @param component the container to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WPanel panel = (WPanel) component;
		XmlStringBuilder xml = renderContext.getWriter();
		WButton submitButton = panel.getDefaultSubmitButton();
		String submitId = submitButton == null ? null : submitButton.getId();
		String titleText = panel.getTitleText();

		boolean renderChildren = isRenderContent(panel);

		xml.appendTagOpen("ui:panel");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		if (PanelMode.LAZY.equals(panel.getMode())) {
			xml.appendOptionalAttribute("hidden", !renderChildren, "true");
		} else {
			xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		}
		xml.appendOptionalAttribute("buttonId", submitId);
		xml.appendOptionalAttribute("title", titleText);
		xml.appendOptionalAttribute("accessKey", Util.upperCase(panel.getAccessKeyAsString()));
		xml.appendOptionalAttribute("type", getPanelType(panel));
		xml.appendOptionalAttribute("mode", getPanelMode(panel));

		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(panel, renderContext);

		if (renderChildren) {
			renderChildren(panel, renderContext);
		} else {
			// Content will be loaded via AJAX
			xml.append("<ui:content/>");
		}

		xml.appendEndTag("ui:panel");
	}

	/**
	 * @param panel the panel to paint.
	 * @return true if the panel content needs to be rendered
	 */
	private boolean isRenderContent(final WPanel panel) {
		PanelMode panelMode = panel.getMode();

		// EAGER panels only render content if the panel is the current AJAX trigger
		if (PanelMode.EAGER.equals(panelMode)) {
			return AjaxHelper.isCurrentAjaxTrigger(panel);
		} else if (PanelMode.LAZY.equals(panelMode)) {
			// LAZY panels render content if the panel is not hidden or it is the current AJAX Trigger (ie content has been requested)
			return (!panel.isHidden() || AjaxHelper.isCurrentAjaxTrigger(panel));
		}

		return true;
	}

	/**
	 * @param panel the panel to paint.
	 * @return the panel mode to be included in the xml
	 */
	private String getPanelMode(final WPanel panel) {
		PanelMode panelMode = panel.getMode();

		if (PanelMode.LAZY.equals(panelMode)) {
			return "lazy";
		} else if (PanelMode.EAGER.equals(panelMode)) {
			return "eager";
		}

		return null;
	}

	/**
	 * @param panel the panel to paint.
	 * @return the panel type to be included in the xml
	 */
	private String getPanelType(final WPanel panel) {
		String type = null;

		switch (panel.getType()) {
			case ACTION:
				type = "action";
				break;
			case BANNER:
				type = "banner";
				break;
			case BLOCK:
				type = "block";
				break;
			case BOX:
				type = "box";
				break;
			case CHROME:
				type = "chrome";
				break;
			case FEATURE:
				type = "feature";
				break;
			case FOOTER:
				type = "footer";
				break;
			case HEADER:
				type = "header";
				break;
			case PLAIN:
				break;
			default:
				// None
				break;
		}

		return type;
	}

	/**
	 * Paints the children contained within the panel. This defers rendering to a layout renderer (if available).
	 *
	 * @param panel the panel to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	private void renderChildren(final WPanel panel, final WebXmlRenderContext renderContext) {
		LayoutManager layout = panel.getLayout();
		Renderer layoutRenderer = null;

		if (layout != null) {
			layoutRenderer = new RendererFactoryImpl().getRenderer(layout.getClass());
		}

		if (layoutRenderer == null) {
			renderContext.getWriter().appendTag("ui:content");
			paintChildren(panel, renderContext);
			renderContext.getWriter().appendEndTag("ui:content");
		} else {
			layoutRenderer.render(panel, renderContext);
		}
	}
}
