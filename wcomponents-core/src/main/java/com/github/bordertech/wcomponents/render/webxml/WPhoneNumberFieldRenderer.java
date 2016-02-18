package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WSuggestions;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The Renderer for {@link WPhoneNumberField}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WPhoneNumberFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WPhoneNumberField.
	 *
	 * @param component the WPhoneNumberField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WPhoneNumberField field = (WPhoneNumberField) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int cols = field.getColumns();
		int minLength = field.getMinLength();
		int maxLength = field.getMaxLength();
		String pattern = field.getPattern();

		WSuggestions suggestions = field.getSuggestions();
		String suggestionsId = suggestions == null ? null : suggestions.getId();

		WComponent submitControl = field.getDefaultSubmitButton();
		String submitControlId = submitControl == null ? null : submitControl.getId();

		xml.appendTagOpen("ui:phonenumberfield");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("disabled", field.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		xml.appendOptionalAttribute("required", field.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", field.isReadOnly(), "true");
		xml.appendOptionalAttribute("minLength", minLength > 0, minLength);
		xml.appendOptionalAttribute("maxLength", maxLength > 0, maxLength);
		xml.appendOptionalAttribute("tabIndex", field.hasTabIndex(), field.getTabIndex());
		xml.appendOptionalAttribute("toolTip", field.getToolTip());
		xml.appendOptionalAttribute("accessibleText", field.getAccessibleText());
		xml.appendOptionalAttribute("size", cols > 0, cols);
		xml.appendOptionalAttribute("buttonId", submitControlId);
		xml.appendOptionalAttribute("pattern", !Util.empty(pattern), pattern);
		xml.appendOptionalAttribute("list", suggestionsId);
		xml.appendClose();

		xml.appendEscaped(field.getText());

		xml.appendEndTag("ui:phonenumberfield");
	}
}
