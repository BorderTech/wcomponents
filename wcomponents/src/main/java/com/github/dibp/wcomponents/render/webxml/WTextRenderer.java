package com.github.dibp.wcomponents.render.webxml;

import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WText;
import com.github.dibp.wcomponents.WebUtilities;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;

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
