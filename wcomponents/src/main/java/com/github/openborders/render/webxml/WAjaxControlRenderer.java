package com.github.openborders.render.webxml;

import com.github.openborders.AjaxTarget;
import com.github.openborders.Renderer;
import com.github.openborders.WAjaxControl;
import com.github.openborders.WComponent;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

/**
 * The {@link Renderer} for {@link WAjaxControl}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WAjaxControlRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given AjaxControl.
     * 
     * @param component the AjaxControl to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WAjaxControl ajaxControl = (WAjaxControl) component;
        XmlStringBuilder xml = renderContext.getWriter();
        WComponent trigger = ajaxControl.getTrigger() == null ? ajaxControl : ajaxControl.getTrigger();
        int loadCount = ajaxControl.getLoadCount();
        int delay = ajaxControl.getDelay();

        if (ajaxControl.getTargets() == null || ajaxControl.getTargets().isEmpty())
        {
            return;
        }

        // Start tag
        xml.appendTagOpen("ui:ajaxTrigger");
        xml.appendAttribute("triggerId", trigger.getId());
        xml.appendOptionalAttribute("allowedUses", loadCount > 0, loadCount);
        xml.appendOptionalAttribute("delay", delay > 0, delay);
        xml.appendClose();

        // Targets
        for (AjaxTarget target : ajaxControl.getTargets())
        {
            xml.appendTagOpen("ui:ajaxTargetId");
            xml.appendAttribute("targetId", target.getId());
            xml.appendEnd();
        }

        // End tag
        xml.appendEndTag("ui:ajaxTrigger");
    }

}
