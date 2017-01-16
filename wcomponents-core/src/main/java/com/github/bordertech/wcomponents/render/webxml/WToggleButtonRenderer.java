package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WToggleButton;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The {@link Renderer} for {@link WCheckBox}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WToggleButtonRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WCheckBox.
	 *
	 * @param component the WCheckBox to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WToggleButton toggle = (WToggleButton) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean readOnly = toggle.isReadOnly();


		xml.appendTagOpen("ui:togglebutton");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", toggle.isHidden(), "true");
		xml.appendOptionalAttribute("selected", toggle.isSelected(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			WComponentGroup<WCheckBox> group = toggle.getGroup();
			String groupName = group == null ? null : group.getId();
			xml.appendOptionalAttribute("groupName", groupName);
			xml.appendOptionalAttribute("disabled", toggle.isDisabled(), "true");
			xml.appendOptionalAttribute("submitOnChange", toggle.isSubmitOnChange(), "true");
			xml.appendOptionalAttribute("toolTip", toggle.getToolTip());
			xml.appendOptionalAttribute("accessibleText", toggle.getAccessibleText());
		}
		xml.appendClose();
		String text = toggle.getText();
		if (text != null) {
			xml.appendEscaped(text);
		}
		xml.appendEndTag("ui:togglebutton");
	}

}
