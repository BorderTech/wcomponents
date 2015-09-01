package com.github.dibp.wcomponents.render.webxml;

import com.github.dibp.wcomponents.Renderer;
import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WSkipLinks;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;

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
