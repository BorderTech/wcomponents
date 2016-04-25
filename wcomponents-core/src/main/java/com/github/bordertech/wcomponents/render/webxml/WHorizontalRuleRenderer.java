package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WHorizontalRule} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WHorizontalRuleRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WHorizontalRule.
	 *
	 * @param component the WHorizontalRule to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		XmlStringBuilder xml = renderContext.getWriter();
		xml.appendTagOpen("hr");
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendEnd();
	}
}
