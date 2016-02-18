package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WSuggestions;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The Renderer for WTextField.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WTextFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WTextField.
	 *
	 * @param component the WTextField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WTextField textField = (WTextField) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int cols = textField.getColumns();
		int minLength = textField.getMinLength();
		int maxLength = textField.getMaxLength();
		String pattern = textField.getPattern();

		WSuggestions suggestions = textField.getSuggestions();
		String suggestionsId = suggestions == null ? null : suggestions.getId();

		WComponent submitControl = textField.getDefaultSubmitButton();
		String submitControlId = submitControl == null ? null : submitControl.getId();

		xml.appendTagOpen("ui:textfield");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("disabled", textField.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		xml.appendOptionalAttribute("required", textField.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", textField.isReadOnly(), "true");
		xml.appendOptionalAttribute("minLength", minLength > 0, minLength);
		xml.appendOptionalAttribute("maxLength", maxLength > 0, maxLength);
		xml.appendOptionalAttribute("tabIndex", textField.hasTabIndex(), textField.getTabIndex());
		xml.appendOptionalAttribute("toolTip", textField.getToolTip());
		xml.appendOptionalAttribute("accessibleText", textField.getAccessibleText());
		xml.appendOptionalAttribute("size", cols > 0, cols);
		xml.appendOptionalAttribute("buttonId", submitControlId);
		xml.appendOptionalAttribute("pattern", !Util.empty(pattern), pattern);
		xml.appendOptionalAttribute("list", suggestionsId);
		xml.appendClose();

		xml.appendEscaped(textField.getText());

		xml.appendEndTag("ui:textfield");
	}
}
