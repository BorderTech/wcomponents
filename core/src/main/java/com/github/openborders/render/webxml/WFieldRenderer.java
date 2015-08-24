package com.github.openborders.render.webxml;

import com.github.openborders.WComponent;
import com.github.openborders.WField;
import com.github.openborders.WLabel;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WField}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WFieldRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WField.
     * 
     * @param component the WField to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WField field = (WField) component;
        XmlStringBuilder xml = renderContext.getWriter();
        int inputWidth = field.getInputWidth();

        xml.appendTagOpen("ui:field");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendOptionalAttribute("hidden", field.isHidden(), "true");
        xml.appendOptionalAttribute("inputWidth", inputWidth > 0, inputWidth);
        xml.appendClose();

        // Label
        WLabel label = field.getLabel();
        
        if (label != null)
        {
            label.paint(renderContext);
        }

        // Field
        if (field.getField() != null)
        {
            xml.appendTag("ui:input");
            field.getField().paint(renderContext);
            
            if (field.getErrorIndicator() != null)
            {
                field.getErrorIndicator().paint(renderContext);
            }
            
            if (field.getWarningIndicator() != null)
            {
                field.getWarningIndicator().paint(renderContext);
            }
            
            xml.appendEndTag("ui:input");
        }
        
        xml.appendEndTag("ui:field");
    }
}
