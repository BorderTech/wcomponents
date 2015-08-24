package com.github.openborders.render.webxml;

import com.github.openborders.Renderer;
import com.github.openborders.WComponent;
import com.github.openborders.WSkipLinks;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WSkipLinks} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WSkipLinksRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WSkipLinks.
     * 
     * @param component the WSkipLinks to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        XmlStringBuilder xml = renderContext.getWriter();
        xml.appendTagOpen("ui:skipLinks");
        xml.appendEnd();
    }
}
