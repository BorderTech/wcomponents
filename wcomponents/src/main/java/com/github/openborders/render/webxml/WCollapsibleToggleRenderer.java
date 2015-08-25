package com.github.openborders.render.webxml; 

import com.github.openborders.WCollapsibleToggle;
import com.github.openborders.WComponent;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WCollapsibleToggle}. 
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
class WCollapsibleToggleRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WCollapsibleToggle.
     * 
     * @param component the WCollapsibleToggle to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WCollapsibleToggle toggle = (WCollapsibleToggle) component;
        XmlStringBuilder xml = renderContext.getWriter();
        
        xml.appendTagOpen("ui:expandCollapseAll");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendAttribute("groupName", toggle.getGroupName());
        xml.appendOptionalAttribute("roundTrip", !toggle.isClientSideToggleable(), "true");
        xml.appendEnd();
    }
}
