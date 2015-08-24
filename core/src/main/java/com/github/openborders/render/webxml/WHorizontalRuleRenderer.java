package com.github.openborders.render.webxml;

import com.github.openborders.Renderer;
import com.github.openborders.WComponent;
import com.github.openborders.WHorizontalRule;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;

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
