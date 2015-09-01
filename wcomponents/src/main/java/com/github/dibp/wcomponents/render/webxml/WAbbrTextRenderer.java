package com.github.dibp.wcomponents.render.webxml;

import com.github.dibp.wcomponents.Renderer;
import com.github.dibp.wcomponents.WAbbrText;
import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;

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
