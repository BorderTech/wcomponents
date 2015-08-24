package com.github.openborders.render.webxml;

import com.github.openborders.RadioButtonGroup;
import com.github.openborders.Renderer;
import com.github.openborders.WComponent;
import com.github.openborders.WRadioButton;
import com.github.openborders.WebUtilities;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * The {@link Renderer} for {@link WRadioButton}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WRadioButtonRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WRadioButton.
     * 
     * @param component the WRadioButton to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WRadioButton button = (WRadioButton) component;
        RadioButtonGroup group = button.getGroup();
        
        XmlStringBuilder xml = renderContext.getWriter();
        
        String value = button.getValue();
        // Check for null option (ie null or empty). Match isEmpty() logic.
        boolean isNull = value == null ? true : (value.toString().length() == 0);

        xml.appendTagOpen("ui:radioButton");
        xml.appendAttribute("id", component.getId());
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
