package com.github.openborders.render.webxml;

import com.github.openborders.WComponent;
import com.github.openborders.WInternalLink;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;
import com.github.openborders.util.Util;

/**
 * The Renderer for {@link WInternalLink}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WInternalLinkRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given {@link WInternalLink}.
     * 
     * @param component the WInternalLink to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WInternalLink link = (WInternalLink) component;
        XmlStringBuilder xml = renderContext.getWriter();

        if (Util.empty(link.getText()))
        {
            return;
        }

        xml.appendTagOpen("ui:link");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendOptionalAttribute("toolTip", link.getToolTip());
        xml.appendOptionalAttribute("accessibleText", link.getAccessibleText());
        xml.appendAttribute("url", "#" + link.getReference().getId());
        xml.appendClose();
        xml.appendEscaped(link.getText());
        xml.appendEndTag("ui:link");

    }
}
