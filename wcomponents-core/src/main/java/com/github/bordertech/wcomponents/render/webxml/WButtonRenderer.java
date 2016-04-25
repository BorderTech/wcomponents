package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WButton.ImagePosition;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.ValidatingAction;

/**
 * {@link Renderer} for the {@link WButton} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
class WButtonRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WButton.
	 *
	 * @param component the WButton to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		XmlStringBuilder xml = renderContext.getWriter();
		WButton button = (WButton) component;
		String text = button.getText();
		String imageUrl = button.getImageUrl();

		if (Util.empty(text) && imageUrl == null) {
			throw new SystemException("WButton text or imageUrl must be specified");
		}

		xml.appendTagOpen(getTagName(button));
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("disabled", button.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", button.isHidden(), "true");
		xml.appendOptionalAttribute("tabIndex", button.hasTabIndex(), button.getTabIndex());
		xml.appendOptionalAttribute("toolTip", button.getToolTip());
		xml.appendOptionalAttribute("accessibleText", button.getAccessibleText());
		xml.appendOptionalAttribute("popup", button.isPopupTrigger(), "true");
		xml.appendOptionalAttribute("accessKey", Util.upperCase(button.getAccessKeyAsString()));
		xml.appendOptionalAttribute("cancel", button.isCancel(), "true");
		xml.appendOptionalAttribute("unsavedChanges", button.isUnsavedChanges(), "true");
		xml.appendOptionalAttribute("msg", button.getMessage());

		if (imageUrl != null) {
			xml.appendAttribute("imageUrl", imageUrl);
			ImagePosition imagePosition = button.getImagePosition();

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
		}

		if (button.isRenderAsLink()) {
			xml.appendAttribute("type", "link");
		}

		Action action = button.getAction();

		if (action instanceof ValidatingAction) {
			WComponent validationTarget = ((ValidatingAction) action).getComponentToValidate();
			xml.appendAttribute("validates", validationTarget.getId());
		}

		xml.appendClose();

		if (text != null) {
			xml.appendEscaped(text);
		}

		xml.appendEndTag(getTagName(button));

		if (button.isAjax()) {
			paintAjax(button, xml);
		}
	}

	/**
	 * Paints the AJAX information for the given WButton.
	 *
	 * @param button the WButton to paint.
	 * @param xml the XmlStringBuilder to paint to.
	 */
	private void paintAjax(final WButton button, final XmlStringBuilder xml) {
		// Start tag
		xml.appendTagOpen("ui:ajaxtrigger");
		xml.appendAttribute("triggerId", button.getId());
		xml.appendClose();

		// Target
		xml.appendTagOpen("ui:ajaxtargetid");
		xml.appendAttribute("targetId", button.getAjaxTarget().getId());
		xml.appendEnd();

		// End tag
		xml.appendEndTag("ui:ajaxtrigger");
	}

	/**
	 * Subclasses may override to change the main tag.
	 *
	 * @param button the WButton being painted.
	 * @return the main tag name
	 */
	protected String getTagName(final WButton button) {
		return "ui:button";
	}
}
