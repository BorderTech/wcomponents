package com.github.openborders.render.webxml; 

import com.github.openborders.WComponent;
import com.github.openborders.WMenuItemGroup;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/** 
 * The Renderer for {@link WMenuItemGroup}.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
final class WMenuItemGroupRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WMenuItemGroup.
     * 
     * @param component the WMenuItemGroup to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WMenuItemGroup group = (WMenuItemGroup) component;
        XmlStringBuilder xml = renderContext.getWriter();
        
        xml.appendTagOpen("ui:menuGroup");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendClose();

        paintChildren(group, renderContext);
        
        xml.appendEndTag("ui:menuGroup");        
    }
}
