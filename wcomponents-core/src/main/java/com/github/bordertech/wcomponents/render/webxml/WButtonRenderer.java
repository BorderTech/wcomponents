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

		if (Util.empty(text) && imageUrl == null && Util.empty(accessibleText) && Util.empty(toolTip)) {
			throw new SystemException("WButton text or imageUrl must be specified");
		}

		xml.appendTagOpen(getTagName(button));

		String buttonId = button.getId();
		ImagePosition pos = button.getImagePosition();
		if (Util.empty(text) && Util.empty(toolTip) && Util.empty(accessibleText)) {
			// If the button has an imageUrl but no text equivalent get the text equivalent off of the image
			WImage imgHolder = button.getImageHolder();
			if (null != imgHolder) {
				toolTip = imgHolder.getAlternativeText();
			}
		}

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
		xml.appendOptionalAttribute("accesskey", Util.upperCase(button.getAccessKeyAsString()));
		xml.appendOptionalAttribute("data-wc-btnmsg", button.getMessage());

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

		if (imageUrl != null) {
			xml.appendTagOpen("span");
			String imageHolderClass =  "wc_nti";
			if (pos != null) {
				StringBuffer imageHolderClassBuffer = new StringBuffer("wc_btn_img wc_btn_img");
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
				imageHolderClass = imageHolderClassBuffer.toString();
			}
			xml.appendAttribute("class", imageHolderClass);
			xml.appendClose();

			if (pos != null && text != null) {
				xml.appendTag("span");
				xml.appendEscaped(text);
				xml.appendEndTag("span");
			}

			xml.appendTagOpen("img");
			xml.appendUrlAttribute("src", imageUrl);
			String alternateText = pos == null ? text : "";
			xml.appendAttribute("alt", alternateText);
			xml.appendEnd();
			xml.appendEndTag("span");
		} else if (text != null) {
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
		return "button";
	}

	/**
	 * @param button the WButton being painted.
	 * @return the HTML class attribute value for this button
	 */
	protected String geHtmlClassName(final WButton button) {
		StringBuffer htmlClassName = new StringBuffer("wc-button");

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
}
