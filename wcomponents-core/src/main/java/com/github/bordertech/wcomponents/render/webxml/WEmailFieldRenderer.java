package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WSuggestions;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WEmailField}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
class WEmailFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WEmailField.
	 *
	 * @param component the WEmailField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WEmailField field = (WEmailField) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean readOnly = field.isReadOnly();

		xml.appendTagOpen("ui:emailfield");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", component.isHidden(), "true");

		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			int cols = field.getColumns();
			int maxLength = field.getMaxLength();

			WSuggestions suggestions = field.getSuggestions();
			String suggestionsId = suggestions == null ? null : suggestions.getId();

			WComponent submitControl = field.getDefaultSubmitButton();
			String submitControlId = submitControl == null ? null : submitControl.getId();

			xml.appendOptionalAttribute("disabled", field.isDisabled(), "true");
			xml.appendOptionalAttribute("required", field.isMandatory(), "true");
			xml.appendOptionalAttribute("maxLength", maxLength > 0, maxLength);
			xml.appendOptionalAttribute("tabIndex", field.hasTabIndex(), field.getTabIndex());
			xml.appendOptionalAttribute("toolTip", field.getToolTip());
			xml.appendOptionalAttribute("accessibleText", field.getAccessibleText());
			xml.appendOptionalAttribute("size", cols > 0, cols);
			xml.appendOptionalAttribute("buttonId", submitControlId);
			xml.appendOptionalAttribute("list", suggestionsId);
			xml.appendOptionalAttribute("placeholder", field.getPlaceholder());
		}
		xml.appendClose();

		xml.appendEscaped(field.getText());

		xml.appendEndTag("ui:emailfield");
	}
}
