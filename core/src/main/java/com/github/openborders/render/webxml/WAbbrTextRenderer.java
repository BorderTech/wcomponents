package com.github.openborders.render.webxml;

import com.github.openborders.Renderer;
import com.github.openborders.WAbbrText;
import com.github.openborders.WComponent;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WAbbrText} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WAbbrTextRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WAbbrText.
     * 
     * @param component the WAbbrText to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WAbbrText abbrText = (WAbbrText) component;
        XmlStringBuilder xml = renderContext.getWriter();
        
        xml.appendTagOpen("ui:abbr");
        xml.appendOptionalAttribute("description", abbrText.getAbbrText());
        xml.appendClose();
        
        xml.appendEscaped(abbrText.getText());
        
        xml.appendEndTag("ui:abbr");
    }
}
