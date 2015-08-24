package com.github.openborders.render.webxml;

import com.github.openborders.Margin;
import com.github.openborders.Marginable;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * Utility methods for rendering margin element.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class MarginRendererUtil
{
    /** Prevent instantiation of utility class. */
    private MarginRendererUtil()
    {
        // Do nothing
    }

    /**
     * @param component the marginable component to paint a margin
     * @param renderContext the RenderContext to paint to.
     */
    public static void renderMargin(final Marginable component, final WebXmlRenderContext renderContext)
    {
        Margin margin = component.getMargin();
        if (margin == null)
        {
            return;
        }

        XmlStringBuilder xml = renderContext.getWriter();

        if (margin != null)
        {
            if (margin.getAll() > 0)
            {
                xml.appendTagOpen("ui:margin");
                xml.appendAttribute("all", String.valueOf(margin.getAll()));
                xml.appendEnd();
            }
            else if (margin.getNorth() > 0 || margin.getEast() > 0 || margin.getSouth() > 0 || margin.getWest() > 0)
            {
                xml.appendTagOpen("ui:margin");
                xml.appendAttribute("north", String.valueOf(margin.getNorth()));
                xml.appendAttribute("east", String.valueOf(margin.getEast()));
                xml.appendAttribute("south", String.valueOf(margin.getSouth()));
                xml.appendAttribute("west", String.valueOf(margin.getWest()));
                xml.appendEnd();
            }
        }
    }

}
