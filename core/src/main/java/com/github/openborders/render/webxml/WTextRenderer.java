package com.github.openborders.render.webxml;

import com.github.openborders.WComponent;
import com.github.openborders.WText;
import com.github.openborders.WebUtilities;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * The Renderer for WText. 
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WTextRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WText.
     * 
     * @param component the WText to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WText text = (WText) component;
        XmlStringBuilder xml = renderContext.getWriter();
        
        String textString = text.getText();
        
        if (textString != null)
        {
            if (text.isEncodeText())
            {
                xml.print(WebUtilities.encode(textString));
            }
            else
            {
                xml.print(textString);
            }
        }        
    }
}
