package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;
import java.math.BigDecimal;

/**
 * The Renderer for {@link WNumberField}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
final class WNumberFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WNumberField.
	 *
	 * @param component the WNumberField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WNumberField field = (WNumberField) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean readOnly = field.isReadOnly();

		BigDecimal value = field.getValue();
		String userText = field.getText();

		xml.appendTagOpen("ui:numberfield");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			WComponent submitControl = field.getDefaultSubmitButton();
			String submitControlId = submitControl == null ? null : submitControl.getId();
			BigDecimal min = field.getMinValue();
			BigDecimal max = field.getMaxValue();
			BigDecimal step = field.getStep();
			int decimals = field.getDecimalPlaces();
			xml.appendOptionalAttribute("disabled", field.isDisabled(), "true");
			xml.appendOptionalAttribute("required", field.isMandatory(), "true");
			xml.appendOptionalAttribute("toolTip", field.getToolTip());
			xml.appendOptionalAttribute("accessibleText", field.getAccessibleText());
			xml.appendOptionalAttribute("min", min != null, String.valueOf(min));
			xml.appendOptionalAttribute("max", max != null, String.valueOf(max));
			xml.appendOptionalAttribute("step", step != null, String.valueOf(step));
			xml.appendOptionalAttribute("decimals", decimals > 0, decimals);
			xml.appendOptionalAttribute("buttonId", submitControlId);

			String autocomplete = field.getAutocomplete();
			xml.appendOptionalAttribute("autocomplete", !Util.empty(autocomplete), autocomplete);
		}

		xml.appendClose();

		xml.appendEscaped(value == null ? userText : value.toString());
		if (!readOnly) {
			DiagnosticRenderUtil.renderDiagnostics(field, renderContext);
		}
		xml.appendEndTag("ui:numberfield");
	}
}
