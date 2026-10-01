package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WButton.ImagePosition;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WImage;
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
		String accessibleText = button.getAccessibleText();
		String toolTip = button.getToolTip();
		String iconClass = button.getImageIconClass();
		String buttonId = button.getId();
		ImagePosition pos = button.getImagePosition();

		// If no text provided at least an imageUrl must be provided
		if (Util.empty(text) && Util.empty(accessibleText) && Util.empty(toolTip)) {
			if (imageUrl == null) {
				throw new SystemException("WButton text or tooltip or accessibleText or imageUrl must be specified");
			}
			// Set the tooltip from the image holder
			WImage imgHolder = button.getImageHolder();
			if (null != imgHolder) {
				toolTip = imgHolder.getAlternativeText();
			}
		}

		// If using an Icon class with no position make sure at least the tooltip has a value as the text is not rendered
		if (iconClass != null && pos == null && Util.empty(toolTip)) {
			toolTip = Util.empty(text) ? accessibleText : text;
		}

		xml.appendTagOpen(getTagName(button));
		xml.appendAttribute("id", buttonId);
		xml.appendAttribute("name", buttonId);
		xml.appendAttribute("value", "x");
		xml.appendAttribute("type", getButtonType(button));
		xml.appendAttribute("class", geHtmlClassName(button));
		xml.appendOptionalAttribute("disabled", button.isDisabled(), "disabled");
		xml.appendOptionalAttribute("hidden", button.isHidden(), "hidden");
		xml.appendOptionalAttribute("title", toolTip);
		xml.appendOptionalAttribute("aria-label", accessibleText);
		xml.appendOptionalAttribute("aria-haspopup", button.isPopupTrigger(), "true");
		xml.appendOptionalAttribute("data-wc-btnmsg", button.getMessage());

		AccessKeyRendererUtil.appendOptionalAccessKeyXMLAttribute(button, renderContext);

		if (button.isCancel()) {
			xml.appendAttribute("formnovalidate", "formnovalidate");
		} else {
			Action action = button.getAction();
			if (action instanceof ValidatingAction) {
				WComponent validationTarget = ((ValidatingAction) action).getComponentToValidate();
				xml.appendAttribute("data-wc-validate", validationTarget.getId());
			}
		}

		xml.appendClose();

		if (imageUrl != null || iconClass != null) {
			String imageHolderClass;
			if (pos == null) {
				// Images or Icons with no postion will not render the button text
				// Images will use the text as the image alt and Icons will rely on the toolTip
				imageHolderClass = "wc_nti";
			} else {
				imageHolderClass = getImageHolderPositionClass(pos);
			}
			// Holder span
			xml.appendTagOpen("span");
			xml.appendAttribute("class", imageHolderClass);
			xml.appendClose();
			// Text span
			if (pos != null && text != null) {
				paintTextSpan(text, xml);
			}
			// Image or Icon
			if (imageUrl != null) {
				xml.appendTagOpen("img");
				xml.appendUrlAttribute("src", imageUrl);
				String alternateText = pos == null ? text : "";
				xml.appendAttribute("alt", alternateText);
				xml.appendEnd();
			} else {
				xml.appendTagOpen("i");
				xml.appendAttribute("class", iconClass);
				xml.appendAttribute("aria-hidden", "true");
				xml.appendClose();
				xml.appendEndTag("i");
			}
			// Close holder span
			xml.appendEndTag("span");
		} else if (text != null) {
			paintTextSpan(text, xml);
		}

		// Optional Access Key Label
		AccessKeyRendererUtil.renderAccessKeyHtmlLabel(button, renderContext);

		xml.appendEndTag(getTagName(button));

		if (button.isAjax()) {
			paintAjax(button, xml);
		}
	}

	/**
	 * Paints the button text span.
	 *
	 * @param text the button text
	 * @param xml the XmlStringBuilder to paint to.
	 */
	protected void paintTextSpan(final String text, final XmlStringBuilder xml) {
		xml.appendTagOpen("span");
		xml.appendAttribute("class", "wc_btn_text");
		xml.appendClose();
		xml.appendEscaped(text);
		xml.appendEndTag("span");
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
		return "button";
	}

	/**
	 * @param button the WButton being painted.
	 * @return the HTML class attribute value for this button
	 */
	protected String geHtmlClassName(final WButton button) {
		StringBuilder htmlClassName = new StringBuilder("wc-button");

		if (button.isRenderAsLink()) {
			htmlClassName.append(" wc-linkbutton");
		}
		if (button.isUnsavedChanges()) {
			htmlClassName.append(" wc_unsaved");
		}
		if (button.isCancel()) {
			htmlClassName.append(" wc_btn_cancel");
		}
		String customButtonClassNames = button.getHtmlClass();
		if (customButtonClassNames != null) {
			htmlClassName.append(" ");
			htmlClassName.append(customButtonClassNames);
		}
		return htmlClassName.toString();
	}

	/**
	 * @param button the WButton being painted.
	 * @return the HTML type attribute value for this button.
	 */
	protected String getButtonType(final WButton button) {
		return button.isClientCommandOnly() ? "button" : "submit";
	}

	/**
	 * @param pos the image position to translate to CSS
	 * @return the CSS class for image holder position
	 */
	protected String getImageHolderPositionClass(final ImagePosition pos) {
		StringBuilder imageHolderClassBuffer = new StringBuilder("wc_btn_img wc_btn_img");
		switch (pos) {
			case NORTH:
				imageHolderClassBuffer.append("n");
				break;
			case EAST:
				imageHolderClassBuffer.append("e");
				break;
			case SOUTH:
				imageHolderClassBuffer.append("s");
				break;
			case WEST:
				imageHolderClassBuffer.append("w");
				break;
			default:
				throw new SystemException("Unknown image position: " + pos);
		}
		return imageHolderClassBuffer.toString();
	}

}
