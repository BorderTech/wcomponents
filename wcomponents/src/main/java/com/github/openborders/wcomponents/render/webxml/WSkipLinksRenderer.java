package com.github.openborders.wcomponents.render.webxml;

import com.github.openborders.wcomponents.Renderer;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WSkipLinks;
import com.github.openborders.wcomponents.XmlStringBuilder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;

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
