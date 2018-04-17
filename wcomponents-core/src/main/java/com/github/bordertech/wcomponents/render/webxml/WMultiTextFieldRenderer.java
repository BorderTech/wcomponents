package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMultiTextField;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The Renderer for {@link WMultiTextField}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
class WMultiTextFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WMultiTextField.
	 *
	 * @param component the WMultiTextField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMultiTextField textField = (WMultiTextField) component;
		XmlStringBuilder xml = renderContext.getWriter();

		boolean readOnly = textField.isReadOnly();

		String[] values = textField.getTextInputs();

		xml.appendTagOpen("ui:multitextfield");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", textField.isHidden(), "true");

		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			int cols = textField.getColumns();
			int minLength = textField.getMinLength();
			int maxLength = textField.getMaxLength();
			int maxInputs = textField.getMaxInputs();
			String pattern = textField.getPattern();
			xml.appendOptionalAttribute("disabled", textField.isDisabled(), "true");
			xml.appendOptionalAttribute("required", textField.isMandatory(), "true");
			xml.appendOptionalAttribute("toolTip", textField.getToolTip());
			xml.appendOptionalAttribute("accessibleText", textField.getAccessibleText());
			xml.appendOptionalAttribute("size", cols > 0, cols);
			xml.appendOptionalAttribute("minLength", minLength > 0, minLength);
			xml.appendOptionalAttribute("maxLength", maxLength > 0, maxLength);
			xml.appendOptionalAttribute("max", maxInputs > 0, maxInputs);
			xml.appendOptionalAttribute("pattern", !Util.empty(pattern), pattern);
			// NOTE: do not use HtmlRenderUtil.getEffectivePlaceholder for placeholder - we do not want to echo "required" in every field.
			String placeholder = textField.getPlaceholder();
			xml.appendOptionalAttribute("placeholder", !Util.empty(placeholder), placeholder);
			xml.appendOptionalAttribute("title", I18nUtilities.format(null, InternalMessages.DEFAULT_MULTITEXTFIELD_TIP));
		}
		xml.appendClose();

		if (values != null) {
			for (String value : values) {
				xml.appendTag("ui:value");
				xml.appendEscaped(value);
				xml.appendEndTag("ui:value");
			}
		}

		if (!readOnly) {
			DiagnosticRenderUtil.renderDiagnostics(textField, renderContext);
		}
		xml.appendEndTag("ui:multitextfield");
	}
}
