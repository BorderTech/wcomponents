package com.github.dibp.wcomponents.render.webxml; 

import com.github.dibp.wcomponents.WCollapsibleToggle;
import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;

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
