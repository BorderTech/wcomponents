package com.github.dibp.wcomponents.container;

import com.github.dibp.wcomponents.AjaxHelper;
import com.github.dibp.wcomponents.AjaxOperation;
import com.github.dibp.wcomponents.ComponentWithContext;
import com.github.dibp.wcomponents.RenderContext;
import com.github.dibp.wcomponents.WebUtilities;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;
import com.github.dibp.wcomponents.util.DebugUtil;

/**
 * <p>An Interceptor used to output structural debugging information for AJAX requests.</p>
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AjaxDebugStructureInterceptor extends DebugStructureInterceptor
{
    /**
     * Override paint to only output the debugging info for the current targets.
     * @param renderContext the renderContext to send the output to.
     */
    @Override
    public void paint(final RenderContext renderContext)
    {
        if (!DebugUtil.isDebugStructureEnabled() || !(renderContext instanceof WebXmlRenderContext))
        {
            getBackingComponent().paint(renderContext);
            return;
        }

        AjaxOperation operation = AjaxHelper.getCurrentOperation();
        
        if (operation == null)
        {
            getBackingComponent().paint(renderContext);
            return;
        }
        
        getBackingComponent().paint(renderContext);
        
        XmlStringBuilder xml = ((WebXmlRenderContext) renderContext).getWriter();
        xml.appendTag("ui:debug");
        
        for (String targetId : operation.getTargets())
        {
            ComponentWithContext target = WebUtilities.getComponentById(targetId, true);
            if (target != null)
            {
                writeDebugInfo(target.getComponent(), xml);
            }
        }
        
        xml.appendEndTag("ui:debug");
    }
}
