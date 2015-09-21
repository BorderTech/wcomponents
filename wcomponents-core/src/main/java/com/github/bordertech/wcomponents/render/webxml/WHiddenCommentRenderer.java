package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WHiddenComment;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;

/**
 * {@link Renderer} for the {@link WHiddenComment} component.
 *
 * @author Darian Bridge
 * @since 1.0.0
 */
final class WHiddenCommentRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WHiddenComment.
	 *
	 * @param component the WHiddenComment to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WHiddenComment hiddenComponent = (WHiddenComment) component;
		XmlStringBuilder xml = renderContext.getWriter();

		String hiddenText = hiddenComponent.getText();

		if (!Util.empty(hiddenText)) {
			xml.appendTag("ui:comment");
			xml.appendEscaped(hiddenText);
			xml.appendEndTag("ui:comment");
		}
	}
}
