package com.github.openborders.wcomponents.render.webxml;

import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WFilterControl;
import com.github.openborders.wcomponents.XmlStringBuilder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;
import com.github.openborders.wcomponents.util.SystemException;

/**
 * The Renderer for {@link WFilterControl}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WFilterControlRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WFilterControl.
     * 
     * @param component the WFilterControl to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WFilterControl filter = (WFilterControl) component;
        XmlStringBuilder xml = renderContext.getWriter();
        String value = filter.getValue();

        if (filter.getTarget() == null)
        {
            throw new SystemException("The filter control cannot be painted as it has no target component.");
        }

        xml.appendTagOpen("ui:filterControl");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendAttribute("for", filter.getTarget().getId());
        xml.appendAttribute("value", value == null ? "" : value);
        xml.appendOptionalAttribute("active", filter.isActive(), "true");
        xml.appendOptionalAttribute("hidden", filter.isHidden(), "true");
        xml.appendClose();
        
        filter.getFilterLabel().paint(renderContext);

        xml.appendEndTag("ui:filterControl");
    }
}
