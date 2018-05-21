package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPasswordField;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * The Renderer for {@link WPasswordField}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
class WPasswordFieldRenderer extends AbstractWebXmlRenderer {
	/**
	 * XML element name.
	 */
	private static final String TAG_NAME = "ui:passwordfield";

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

		boolean readOnly = field.isReadOnly();

		xml.appendTagOpen(TAG_NAME);
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", component.isHidden(), "true");

		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
			xml.appendEnd();
			return;
		}
		int cols = field.getColumns();
		int minLength = field.getMinLength();
		int maxLength = field.getMaxLength();
		WComponent submitControl = field.getDefaultSubmitButton();
		String submitControlId = submitControl == null ? null : submitControl.getId();

		xml.appendOptionalAttribute("disabled", field.isDisabled(), "true");
		xml.appendOptionalAttribute("required", field.isMandatory(), "true");
		xml.appendOptionalAttribute("minLength", minLength > 0, minLength);
		xml.appendOptionalAttribute("maxLength", maxLength > 0, maxLength);
		xml.appendOptionalAttribute("toolTip", field.getToolTip());
		xml.appendOptionalAttribute("accessibleText", field.getAccessibleText());
		xml.appendOptionalAttribute("size", cols > 0, cols);
		xml.appendOptionalAttribute("buttonId", submitControlId);
		String placeholder = field.getPlaceholder();
		xml.appendOptionalAttribute("placeholder", !Util.empty(placeholder), placeholder);
		String autocomplete = field.getAutocomplete();
		xml.appendOptionalAttribute("autocomplete", !Util.empty(autocomplete), autocomplete);

		List<Diagnostic> diags = field.getDiagnostics(Diagnostic.ERROR);
		if (diags == null || diags.isEmpty()) {
			xml.appendEnd();
			return;
		}
		xml.appendClose();
		DiagnosticRenderUtil.renderDiagnostics(field, renderContext);
		xml.appendEndTag(TAG_NAME);
	}
}
