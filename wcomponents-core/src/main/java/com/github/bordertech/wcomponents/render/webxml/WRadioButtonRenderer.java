package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The {@link Renderer} for {@link WRadioButton}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WRadioButtonRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WRadioButton.
	 *
	 * @param component the WRadioButton to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WRadioButton button = (WRadioButton) component;

		XmlStringBuilder xml = renderContext.getWriter();

		String value = button.getValue();
		// Check for null option (ie null or empty). Match isEmpty() logic.
		boolean isNull = value == null ? true : (value.length() == 0);

		xml.appendTagOpen("ui:radiobutton");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendAttribute("groupName", button.getGroupName());
		xml.appendAttribute("value", WebUtilities.encode(value));
		xml.appendOptionalAttribute("disabled", button.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", button.isHidden(), "true");
		xml.appendOptionalAttribute("required", button.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", button.isReadOnly(), "true");
		xml.appendOptionalAttribute("selected", button.isSelected(), "true");
		xml.appendOptionalAttribute("submitOnChange", button.isSubmitOnChange(), "true");
		xml.appendOptionalAttribute("tabIndex", component.hasTabIndex(), component.getTabIndex());
		xml.appendOptionalAttribute("toolTip", button.getToolTip());
		xml.appendOptionalAttribute("accessibleText", button.getAccessibleText());
		xml.appendOptionalAttribute("isNull", isNull, "true");
		xml.appendEnd();
	}

}
