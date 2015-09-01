package com.github.dibp.wcomponents.render.webxml;

import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WMultiTextField;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;
import com.github.dibp.wcomponents.util.Util;

/**
 * The Renderer for {@link WMultiTextField}.
 * 
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WMultiTextFieldRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WMultiTextField.
     * 
     * @param component the WMultiTextField to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WMultiTextField textField = (WMultiTextField) component;
        XmlStringBuilder xml = renderContext.getWriter();
        int cols = textField.getColumns();
        int minLength = textField.getMinLength();
        int maxLength = textField.getMaxLength();
        int maxInputs = textField.getMaxInputs();
        String pattern = textField.getPattern();
        String[] values = textField.getTextInputs();

        xml.appendTagOpen("ui:multiTextField");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendOptionalAttribute("disabled", textField.isDisabled(), "true");
        xml.appendOptionalAttribute("hidden", textField.isHidden(), "true");
        xml.appendOptionalAttribute("required", textField.isMandatory(), "true");
        xml.appendOptionalAttribute("readOnly", textField.isReadOnly(), "true");
        xml.appendOptionalAttribute("tabIndex", textField.hasTabIndex(), textField.getTabIndex());
        xml.appendOptionalAttribute("toolTip", textField.getToolTip());
        xml.appendOptionalAttribute("accessibleText", textField.getAccessibleText());
        xml.appendOptionalAttribute("size", cols > 0, cols);
        xml.appendOptionalAttribute("minLength", minLength > 0, minLength);        
        xml.appendOptionalAttribute("maxLength", maxLength > 0, maxLength);        
        xml.appendOptionalAttribute("max", maxInputs > 0, maxInputs);        
        xml.appendOptionalAttribute("pattern", !Util.empty(pattern), pattern);
        xml.appendClose();
        
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                xml.appendTag("ui:value");
                xml.appendEscaped(values[i]);
                xml.appendEndTag("ui:value");
            }
        }
        
        xml.appendEndTag("ui:multiTextField");
    }
}
