package com.github.openborders.render.webxml;

import com.github.openborders.Renderer;
import com.github.openborders.WComponent;
import com.github.openborders.WSeparator;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WSeparator} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WSeparatorRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WSeparator.
     * 
     * @param component the WSeparator to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        XmlStringBuilder xml = renderContext.getWriter();
        xml.appendTagOpen("ui:separator");
        xml.appendEnd();
    }
}
