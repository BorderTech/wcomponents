package com.github.dibp.wcomponents.render.webxml;

import com.github.dibp.wcomponents.Renderer;
import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WHeading;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WHeading} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WHeadingRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WHeading.
     * 
     * @param component the WHeading to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WHeading heading = (WHeading) component;
        XmlStringBuilder xml = renderContext.getWriter();
        
        xml.appendTagOpen("ui:heading");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendAttribute("level", heading.getHeadingLevel().getLevel());
        xml.appendOptionalAttribute("accessibleText", heading.getAccessibleText());
        xml.appendClose();
        
        // Render margin
        MarginRendererUtil.renderMargin(heading, renderContext);
        
        if (heading.getDecoratedLabel() == null)
        {
            // Constructed with a String
            xml.append(heading.getText(), heading.isEncodeText());
        }
        else
        {
            heading.getDecoratedLabel().paint(renderContext);
        }
        
        xml.appendEndTag("ui:heading");
    }
}
