package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPartialDateField;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WPartialDateField}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WPartialDateFieldRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WPartialDateField.
	 *
	 * @param component the WPartialDateField to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WPartialDateField dateField = (WPartialDateField) component;
		XmlStringBuilder xml = renderContext.getWriter();

		WComponent submitControl = dateField.getDefaultSubmitButton();
		String submitControlId = submitControl == null ? null : submitControl.getId();
		String date = formatDate(dateField);

		xml.appendTagOpen("ui:datefield");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendAttribute("allowPartial", "true");
		xml.appendOptionalAttribute("disabled", dateField.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", dateField.isHidden(), "true");
		xml.appendOptionalAttribute("required", dateField.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", dateField.isReadOnly(), "true");
		xml.appendOptionalAttribute("tabIndex", dateField.hasTabIndex(), dateField.getTabIndex());
		xml.appendOptionalAttribute("toolTip", dateField.getToolTip());
		xml.appendOptionalAttribute("accessibleText", dateField.getAccessibleText());
		xml.appendOptionalAttribute("buttonId", submitControlId);
		xml.appendOptionalAttribute("date", date);

		xml.appendClose();

		if (date == null) {
			xml.appendEscaped(dateField.getText());
		}

		xml.appendEndTag("ui:datefield");
	}

	/**
	 * Formats a partial date to the format required by the schema.
	 *
	 * @param dateField the date field containing the date to format.
	 * @return the formatted date.
	 */
	private String formatDate(final WPartialDateField dateField) {
		Integer day = dateField.getDay();
		Integer month = dateField.getMonth();
		Integer year = dateField.getYear();

		if (day != null || month != null || year != null) {
			StringBuffer buf = new StringBuffer(10);

			append(buf, year, 4);
			buf.append('-');
			append(buf, month, 2);
			buf.append('-');
			append(buf, day, 2);

			return buf.toString();
		}

		return null;
	}

	/**
	 * Appends a single date component to the given StringBuffer. Nulls are replaced with question marks, and numbers
	 * are padded with zeros.
	 *
	 * @param buf the buffer to append to.
	 * @param num the number to append, may be null.
	 * @param digits the minimum number of digits to append.
	 */
	private void append(final StringBuffer buf, final Integer num, final int digits) {
		if (num == null) {
			for (int i = 0; i < digits; i++) {
				buf.append('?');
			}
		} else {
			for (int digit = 1, test = 10; digit < digits; digit++, test *= 10) {
				if (num < test) {
					buf.append('0');
				}
			}

			buf.append(num);
		}
	}
}
