package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WLink;
import com.github.bordertech.wcomponents.WLink.ImagePosition;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The {@link Renderer} for the {@link WLink} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WLinkRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given {@link WLink}.
	 *
	 * @param component the WLink to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WLink link = (WLink) component;
		XmlStringBuilder xml = renderContext.getWriter();
		String text = link.getText();
		String targetWindowName = link.getOpenNewWindow() ? link.getTargetWindowName() : null;
		String imageUrl = link.getImageUrl();

		xml.appendTagOpen("ui:link");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("disabled", link.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		xml.appendOptionalAttribute("toolTip", link.getToolTip());
		xml.appendOptionalAttribute("accessibleText", link.getAccessibleText());
		xml.appendUrlAttribute("url", link.getUrl());
		xml.appendOptionalAttribute("rel", link.getRel());

		AccessKeyRendererUtil.appendOptionalAccessKeyXMLAttribute(link, renderContext);

		if (imageUrl != null) {
			xml.appendUrlAttribute("imageUrl", imageUrl);
			ImagePosition imagePosition = link.getImagePosition();

			if (imagePosition != null) {
				switch (imagePosition) {
					case NORTH:
						xml.appendAttribute("imagePosition", "n");
						break;
					case EAST:
						xml.appendAttribute("imagePosition", "e");
						break;
					case SOUTH:
						xml.appendAttribute("imagePosition", "s");
						break;
					case WEST:
						xml.appendAttribute("imagePosition", "w");
						break;
					default:
						throw new SystemException("Unknown image position: " + imagePosition);
				}
			}
			// we have an image. We must have a text equivalent
			if (Util.empty(text) && Util.empty(link.getToolTip()) && Util.empty(link.getAccessibleText())) {
				// If the link has an umageUrl but no text equivalent get the text equivalent off of the image
				WImage linkImage = link.getImageHolder();
				if (null != linkImage) {
					xml.appendOptionalAttribute("toolTip", linkImage.getAlternativeText());
				}
			}
		}

		if (link.isRenderAsButton()) {
			xml.appendAttribute("type", "button");
		}

		xml.appendClose();

		if (targetWindowName != null) {
			xml.appendTagOpen("ui:windowAttributes");
			xml.appendAttribute("name", targetWindowName);

			WLink.WindowAttributes attributes = link.getWindowAttrs();

			if (attributes != null) {
				xml.appendOptionalAttribute("top", attributes.getTop() >= 0, attributes.getTop());
				xml.appendOptionalAttribute("left", attributes.getLeft() >= 0, attributes.getLeft());
				xml.appendOptionalAttribute("width", attributes.getWidth() > 0, attributes.
						getWidth());
				xml.appendOptionalAttribute("height", attributes.getHeight() > 0, attributes.
						getHeight());
				xml.appendOptionalAttribute("resizable", attributes.isResizable(), "true");
				xml.appendOptionalAttribute("showMenubar", attributes.isMenubar(), "true");
				xml.appendOptionalAttribute("showToolbar", attributes.isToolbars(), "true");
				xml.appendOptionalAttribute("showLocation", attributes.isLocation(), "true");
				xml.appendOptionalAttribute("showStatus", attributes.isStatus(), "true");
				xml.appendOptionalAttribute("showScrollbars", attributes.isScrollbars(), "true");
				xml.appendOptionalAttribute("showDirectories", attributes.isDirectories(), "true");
			}

			xml.appendEnd();
		}

		if (text != null) {
			xml.appendEscaped(text);
		}

		xml.appendEndTag("ui:link");

		// Paint the AJAX trigger if the link has an action
		if (link.getAction() != null) {
			paintAjaxTrigger(link, xml);
		}

	}

	/**
	 * Paint the AJAX trigger if the link has an action.
	 *
	 * @param link the link component being rendered
	 * @param xml the XmlStringBuilder to paint to.
	 */
	private void paintAjaxTrigger(final WLink link, final XmlStringBuilder xml) {
		AjaxTarget[] actionTargets = link.getActionTargets();

		// Start tag
		xml.appendTagOpen(WAjaxControlRenderer.WC_AJAXTRIGGER);
		xml.appendAttribute("triggerId", link.getId());
		xml.appendClose();

		if (actionTargets != null && actionTargets.length > 0) {
			// Targets
			for (AjaxTarget target : actionTargets) {
				xml.appendTagOpen(WAjaxControlRenderer.WC_AJAXTARGETID);
				xml.appendAttribute("targetId", target.getId());
				xml.appendClose();
				xml.appendEndTag(WAjaxControlRenderer.WC_AJAXTARGETID);
			}
		} else {
			// Target itself
			xml.appendTagOpen(WAjaxControlRenderer.WC_AJAXTARGETID);
			xml.appendAttribute("targetId", link.getId());
			xml.appendClose();
			xml.appendEndTag(WAjaxControlRenderer.WC_AJAXTARGETID);
		}

		// End tag
		xml.appendEndTag(WAjaxControlRenderer.WC_AJAXTRIGGER);
	}
}
