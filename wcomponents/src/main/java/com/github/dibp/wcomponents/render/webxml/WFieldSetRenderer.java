package com.github.dibp.wcomponents.render.webxml;

import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WDecoratedLabel;
import com.github.dibp.wcomponents.WFieldSet;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WFieldSet}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WFieldSetRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WFieldSet.
     * 
     * @param component the WFieldSet to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WFieldSet fieldSet = (WFieldSet) component;
        XmlStringBuilder xml = renderContext.getWriter();

        xml.appendTagOpen("ui:fieldSet");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendOptionalAttribute("hidden", fieldSet.isHidden(), "true");
        
        switch (fieldSet.getFrameType())
        {
            case NO_BORDER:
                xml.appendOptionalAttribute("frame", "noborder");
                break;
                
            case NO_TEXT:
                xml.appendOptionalAttribute("frame", "notext");
                break;
                
            case NONE:
                xml.appendOptionalAttribute("frame", "none");
                break;

            case NORMAL:
            default:
                break;
        }
        
        xml.appendOptionalAttribute("required", fieldSet.isMandatory(), "true");
        xml.appendClose();
        
        // Render margin
        MarginRendererUtil.renderMargin(fieldSet, renderContext);

        // Label
        WDecoratedLabel label = fieldSet.getTitle();
        label.paint(renderContext);

        // Children
        xml.appendTag("ui:content");
        int size = fieldSet.getChildCount();

        for (int i = 0; i < size; i++)
        {
            WComponent child = fieldSet.getChildAt(i);

            // Skip label, as it has already been painted
            if (child != label)
            {
                child.paint(renderContext);
            }
        }
        
        xml.appendEndTag("ui:content");
        xml.appendEndTag("ui:fieldSet");
    }
}
