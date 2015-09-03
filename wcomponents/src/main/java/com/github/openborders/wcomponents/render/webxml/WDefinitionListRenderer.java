package com.github.openborders.wcomponents.render.webxml;

import java.util.List;

import com.github.openborders.wcomponents.Renderer;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WDefinitionList;
import com.github.openborders.wcomponents.XmlStringBuilder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;
import com.github.openborders.wcomponents.util.Duplet;
import com.github.openborders.wcomponents.util.I18nUtilities;
import com.github.openborders.wcomponents.util.SystemException;

/**
 * The {@link Renderer} for {@link WDefinitionList}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
class WDefinitionListRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given definition list.
     * 
     * @param component the list to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WDefinitionList list = (WDefinitionList) component;
        XmlStringBuilder xml = renderContext.getWriter();
        
        xml.appendTagOpen("ui:definitionList");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        
        switch (list.getType())
        {
            case FLAT:
                xml.appendAttribute("type", "flat");
                break;
            
            case COLUMN:
                xml.appendAttribute("type", "column");
                break;
            
            case STACKED:
                xml.appendAttribute("type", "stacked");
                break;
            
            case NORMAL:
                break;
                
            default:
                throw new SystemException("Unknown layout type: " + list.getType());
        }
        
        xml.appendClose();
        
        // Render margin
        MarginRendererUtil.renderMargin(list, renderContext);

        for (Duplet<String, List<WComponent>> term : list.getTerms())
        {
            xml.appendTagOpen("ui:term");            
            xml.appendAttribute("text", I18nUtilities.format(null, term.getFirst()));
            xml.appendClose();
            
            for (WComponent data : term.getSecond())
            {
                xml.appendTag("ui:data");
                data.paint(renderContext);
                xml.appendEndTag("ui:data");
            }
            
            xml.appendEndTag("ui:term");
        }        
        
        xml.appendEndTag("ui:definitionList");
    }
}
