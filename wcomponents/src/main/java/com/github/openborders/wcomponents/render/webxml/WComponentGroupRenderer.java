package com.github.openborders.wcomponents.render.webxml;

import java.util.List;

import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WComponentGroup;
import com.github.openborders.wcomponents.XmlStringBuilder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WComponentGroup}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WComponentGroupRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WComponentGroup.
     * 
     * @param component the WComponentGroup to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WComponentGroup group = (WComponentGroup) component;
        XmlStringBuilder xml = renderContext.getWriter();
        List<WComponent> components = group.getComponents();

        if (components != null && !components.isEmpty())
        {
            xml.appendTagOpen("ui:componentGroup");
            xml.appendAttribute("id", component.getId());
            xml.appendOptionalAttribute("track", component.isTracking(), "true");
            xml.appendClose();
            
            for (WComponent comp : components)
            {
                xml.appendTagOpen("ui:component");
                xml.appendAttribute("id", comp.getId());
                xml.appendEnd();
            }
            
            xml.appendEndTag("ui:componentGroup");
        }
    }
}
