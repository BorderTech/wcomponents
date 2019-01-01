package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLocalDateField;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The Renderer for {@link WLocalDateField}.
 *
 * @author John McGuinness
 * @since 1.5.15
 */
final class WLocalDateFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * This date format is used internally to exchange dates between the client and server.
	 */
	private static final String INTERNAL_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Paints the given WLocalDateField.
	 *
	 * @param component the WLocalDateField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WLocalDateField dateField = (WLocalDateField) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean readOnly = dateField.isReadOnly();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INTERNAL_DATE_FORMAT);
                
		LocalDate date = dateField.getLocalDate();

		xml.appendTagOpen("ui:datefield");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", dateField.isHidden(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			xml.appendOptionalAttribute("disabled", dateField.isDisabled(), "true");
			xml.appendOptionalAttribute("required", dateField.isMandatory(), "true");
			xml.appendOptionalAttribute("toolTip", dateField.getToolTip());
			xml.appendOptionalAttribute("accessibleText", dateField.getAccessibleText());

			WComponent submitControl = dateField.getDefaultSubmitButton();
			String submitControlId = submitControl == null ? null : submitControl.getId();
			xml.appendOptionalAttribute("buttonId", submitControlId);

			LocalDate minDate = dateField.getMinDate();
			LocalDate maxDate = dateField.getMaxDate();

			if (minDate != null) {
				xml.appendAttribute("min", minDate.format(formatter));
			}
			if (maxDate != null) {
				xml.appendAttribute("max", maxDate.format(formatter));
			}

			String autocomplete = dateField.getAutocomplete();
			xml.appendOptionalAttribute("autocomplete", !Util.empty(autocomplete), autocomplete);
		}

		if (date != null) {
			xml.appendAttribute("date", date.format(formatter));
		}

		xml.appendClose();

		if (date == null) {
			xml.appendEscaped(dateField.getText());
		}

		if (!readOnly) {
			DiagnosticRenderUtil.renderDiagnostics(dateField, renderContext);
		}

		xml.appendEndTag("ui:datefield");
	}
}
