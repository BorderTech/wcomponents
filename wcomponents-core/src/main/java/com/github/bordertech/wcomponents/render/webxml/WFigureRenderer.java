package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WFigure;
import com.github.bordertech.wcomponents.WFigure.FigureMode;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * The Renderer for {@link WFigure}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WFigureRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WFigure.
	 *
	 * @param component the WFigure to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WFigure figure = (WFigure) component;
		XmlStringBuilder xml = renderContext.getWriter();

		boolean renderChildren = isRenderContent(figure);

		xml.appendTagOpen("ui:figure");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		if (FigureMode.LAZY.equals(figure.getMode())) {
			xml.appendOptionalAttribute("hidden", !renderChildren, "true");
		} else {
			xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		}

		FigureMode mode = figure.getMode();
		if (mode != null) {
			switch (mode) {
				case LAZY:
					xml.appendAttribute("mode", "lazy");
					break;
				case EAGER:
					xml.appendAttribute("mode", "eager");
					break;
				default:
					throw new SystemException("Unknown figure mode: " + figure.getMode());
			}
		}

		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(figure, renderContext);

		if (renderChildren) {
			// Label
			figure.getDecoratedLabel().paint(renderContext);

			// Content
			xml.appendTagOpen("ui:content");
			xml.appendAttribute("id", component.getId() + "-content");
			xml.appendClose();
			figure.getContent().paint(renderContext);
			xml.appendEndTag("ui:content");
		}

		xml.appendEndTag("ui:figure");
	}

	/**
	 * @param figure the figure to paint.
	 * @return true if the figure content needs to be rendered
	 */
	private boolean isRenderContent(final WFigure figure) {
		FigureMode mode = figure.getMode();

		// EAGER figures only render content if the figure is the current AJAX trigger
		if (FigureMode.EAGER.equals(mode)) {
			return AjaxHelper.isCurrentAjaxTrigger(figure);
		} else if (FigureMode.LAZY.equals(mode)) {
			// LAZY figures render content if the figure is not hidden or it is the current AJAX Trigger (ie content has
			// been requested)
			return (!figure.isHidden() || AjaxHelper.isCurrentAjaxTrigger(figure));
		}

		return true;
	}

}
