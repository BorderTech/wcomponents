package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import com.github.bordertech.wcomponents.validation.WValidationErrors.GroupedDiagnositcs;

/**
 * The Renderer for the {@link WValidationErrors} component.
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
final class WValidationErrorsRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given {@link WValidationErrors} component.
	 *
	 * @param component The {@link WValidationErrors} component to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WValidationErrors errors = (WValidationErrors) component;
		XmlStringBuilder xml = renderContext.getWriter();

		if (errors.hasErrors()) {
			xml.appendTagOpen("ui:validationerrors");
			xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
			xml.appendOptionalAttribute("track", component.isTracking(), "true");
			xml.appendOptionalAttribute("title", errors.getTitleText());
			xml.appendClose();

			for (GroupedDiagnositcs nextGroup : errors.getGroupedErrors()) {
				// Render each diagnostic message in this group.
				for (Diagnostic nextMessage : nextGroup.getDiagnostics()) {
					xml.appendTagOpen("ui:error");
					WComponent forComponent = nextMessage.getComponent();

					if (forComponent != null) {
						UIContextHolder.pushContext(nextMessage.getContext());

						try {
							xml.appendAttribute("for", forComponent.getId());
						} finally {
							UIContextHolder.popContext();
						}
					}

					xml.appendClose();

					// DiagnosticImpl has been extended to support rendering
					// of a WComponent as the message.
					if (nextMessage instanceof DiagnosticImpl) {
						WComponent messageComponent = ((DiagnosticImpl) nextMessage)
								.createDiagnosticErrorComponent();

						// We add the component to a throw-away container so that it renders with the correct ID.
						WContainer container = new WContainer() {
							@Override
							public String getId() {
								return component.getId();
							}
						};

						container.add(messageComponent);
						messageComponent.paint(renderContext);
						container.remove(messageComponent);
						container.reset();
					} else {
						xml.append(nextMessage.getDescription());
					}

					xml.appendEndTag("ui:error");
				}
			}

			xml.appendEndTag("ui:validationerrors");
		}
	}
}
