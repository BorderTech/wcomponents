package com.github.openborders.wcomponents.render.webxml; 

import com.github.openborders.wcomponents.Renderer;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WTabGroup;
import com.github.openborders.wcomponents.XmlStringBuilder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WTabGroup} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WTabGroupRenderer extends AbstractWebXmlRenderer
{
   /**
    * Paints the given WTabGroup.
    * 
    * @param component the WTabGroup to paint.
    * @param renderContext the RenderContext to paint to.
    */
   @Override
   public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
   {
       WTabGroup group = (WTabGroup) component;
       XmlStringBuilder xml = renderContext.getWriter();
       
       xml.appendTagOpen("ui:tabGroup");
       xml.appendAttribute("id", component.getId());
       xml.appendOptionalAttribute("track", component.isTracking(), "true");
       xml.appendOptionalAttribute("disabled", group.isDisabled(), "true");
       xml.appendClose();

       paintChildren(group, renderContext);
       
       xml.appendEndTag("ui:tabGroup");
   }
}
