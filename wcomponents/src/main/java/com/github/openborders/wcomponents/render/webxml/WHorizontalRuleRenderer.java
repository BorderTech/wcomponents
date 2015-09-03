package com.github.openborders.wcomponents.render.webxml;

import com.github.openborders.wcomponents.Renderer;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WHorizontalRule;
import com.github.openborders.wcomponents.XmlStringBuilder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WHorizontalRule} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WHorizontalRuleRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WHorizontalRule.
     * 
     * @param component the WHorizontalRule to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        XmlStringBuilder xml = renderContext.getWriter();
        xml.appendTagOpen("ui:hr");
        xml.appendEnd();
    }
}
