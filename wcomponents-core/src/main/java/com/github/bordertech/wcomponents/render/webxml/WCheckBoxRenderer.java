package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * The {@link Renderer} for {@link WCheckBox}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WCheckBoxRenderer extends AbstractWebXmlRenderer {

	/**
	 * XML element name.
	 */
	private static final String TAG_NAME = "ui:checkbox";
	/**
	 * Paints the given WCheckBox.
	 *
	 * @param component the WCheckBox to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WCheckBox checkBox = (WCheckBox) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean readOnly = checkBox.isReadOnly();

		xml.appendTagOpen(TAG_NAME);
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", checkBox.isHidden(), "true");
		xml.appendOptionalAttribute("selected", checkBox.isSelected(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
			xml.appendEnd();
			return;
		}

		WComponent submitControl = checkBox.getDefaultSubmitButton();
		String submitControlId = submitControl == null ? null : submitControl.getId();
		WComponentGroup<WCheckBox> group = checkBox.getGroup();
		String groupName = group == null ? null : group.getId();
		xml.appendOptionalAttribute("groupName", groupName);
		xml.appendOptionalAttribute("disabled", checkBox.isDisabled(), "true");
		xml.appendOptionalAttribute("required", checkBox.isMandatory(), "true");
		xml.appendOptionalAttribute("submitOnChange", checkBox.isSubmitOnChange(), "true");
		xml.appendOptionalAttribute("toolTip", checkBox.getToolTip());
		xml.appendOptionalAttribute("accessibleText", checkBox.getAccessibleText());
		xml.appendOptionalAttribute("buttonId", submitControlId);

		List<Diagnostic> diags = checkBox.getDiagnostics(Diagnostic.ERROR);
		if (diags == null || diags.isEmpty()) {
			xml.appendEnd();
			return;
		}
		xml.appendClose();
		DiagnosticRenderUtil.renderDiagnostics(checkBox, renderContext);
		xml.appendEndTag(TAG_NAME);
	}

}
