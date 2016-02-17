package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for WTextArea.
 *
 * @author Yiannis Paschalidis
 * @author Rick Brown
 * @since 1.0.0
 */
final class WTextAreaRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WTextArea.
	 *
	 * @param component the WTextArea to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WTextArea textArea = (WTextArea) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int cols = textArea.getColumns();
		int rows = textArea.getRows();
		int minLength = textArea.getMinLength();
		int maxLength = textArea.getMaxLength();
		WComponent submitControl = textArea.getDefaultSubmitButton();
		String submitControlId = submitControl == null ? null : submitControl.getId();

		xml.appendTagOpen("ui:textarea");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("disabled", textArea.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", textArea.isHidden(), "true");
		xml.appendOptionalAttribute("required", textArea.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", textArea.isReadOnly(), "true");
		xml.appendOptionalAttribute("minLength", minLength > 0, minLength);
		xml.appendOptionalAttribute("maxLength", maxLength > 0, maxLength);
		xml.appendOptionalAttribute("tabIndex", textArea.hasTabIndex(), textArea.getTabIndex());
		xml.appendOptionalAttribute("toolTip", textArea.getToolTip());
		xml.appendOptionalAttribute("accessibleText", textArea.getAccessibleText());
		xml.appendOptionalAttribute("rows", rows > 0, rows);
		xml.appendOptionalAttribute("cols", cols > 0, cols);
		xml.appendOptionalAttribute("buttonId", submitControlId);
		xml.appendClose();

		// TODO Pattern is not supported on the client for TextArea, and will not be rendered. Consider making WTextArea
		// no longer extend WTextField.
		if (textArea.isRichTextArea()) {
			/*
			 * This is a nested element instead of an attribute to cater for future enhancements
			 * such as turning rich text features on or off, or specifying JSON config either as
			 * a URL attribute or a nested CDATA section.
			 */
			xml.append("<ui:rtf />");
		}

		xml.appendEscaped(textArea.getText());

		xml.appendEndTag("ui:textarea");
	}
}
