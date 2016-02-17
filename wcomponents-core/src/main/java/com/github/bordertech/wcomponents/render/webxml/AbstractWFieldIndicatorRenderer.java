package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.AbstractWFieldIndicator;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * {@link Renderer} for the {@link AbstractWFieldIndicator} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
abstract class AbstractWFieldIndicatorRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given AbstractWFieldIndicator.
	 *
	 * @param component the WFieldErrorIndicator to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		AbstractWFieldIndicator fieldIndicator = (AbstractWFieldIndicator) component;
		XmlStringBuilder xml = renderContext.getWriter();

		List<Diagnostic> diags = fieldIndicator.getDiagnostics();

		if (diags != null && !diags.isEmpty()) {
			xml.appendTagOpen("ui:fieldindicator");
			xml.appendAttribute("id", component.getId());
			xml.appendOptionalAttribute("track", component.isTracking(), "true");

			switch (fieldIndicator.getFieldIndicatorType()) {
				case INFO:
					xml.appendAttribute("type", "info");
					break;

				case WARN:
					xml.appendAttribute("type", "warn");
					break;

				case ERROR:
					xml.appendAttribute("type", "error");
					break;

				default:
					throw new SystemException(
							"Cannot paint field indicator due to an invalid field indicator type: " + fieldIndicator.
							getFieldIndicatorType());
			}

			xml.appendAttribute("for", fieldIndicator.getRelatedFieldId());
			xml.appendClose();

			for (Diagnostic diag : diags) {
				xml.appendTag("ui:message");
				xml.appendEscaped(diag.getDescription());
				xml.appendEndTag("ui:message");
			}

			xml.appendEndTag("ui:fieldindicator");
		}
	}
}
