package com.github.openborders.render.webxml;

import com.github.openborders.Renderer;
import com.github.openborders.WComponent;
import com.github.openborders.WRow;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WRow} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WRowRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WButton.
     * 
     * @param component the WRow to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WRow row = (WRow) component;
        XmlStringBuilder xml = renderContext.getWriter();
        int cols = row.getChildCount();
        int hgap = row.getHgap();
        
        if (cols > 0)
        {
            xml.appendTagOpen("ui:row");
            xml.appendAttribute("id", component.getId());
            xml.appendOptionalAttribute("track", component.isTracking(), "true");
            xml.appendOptionalAttribute("hgap", hgap > 0, hgap);
            xml.appendClose();

            // Render margin
            MarginRendererUtil.renderMargin(row, renderContext);
            
            paintChildren(row, renderContext);
            
            xml.appendEndTag("ui:row");
        }
    }
}
