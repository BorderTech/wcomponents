package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPasswordField;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WPasswordField}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WPasswordFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WPasswordField.
	 *
	 * @param component the WPasswordField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WPasswordField field = (WPasswordField) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int cols = field.getColumns();
		int minLength = field.getMinLength();
		int maxLength = field.getMaxLength();
		WComponent submitControl = field.getDefaultSubmitButton();
		String submitControlId = submitControl == null ? null : submitControl.getId();

		xml.appendTagOpen("ui:passwordfield");
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
		xml.appendEnd();
	}
}
