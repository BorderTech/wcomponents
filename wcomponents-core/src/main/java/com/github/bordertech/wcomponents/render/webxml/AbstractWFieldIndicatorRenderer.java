package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Diagnosable;
import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.AbstractWFieldIndicator;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * {@link Renderer} for the {@link AbstractWFieldIndicator} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
abstract class AbstractWFieldIndicatorRenderer extends AbstractWebXmlRenderer {

	private static final String TAG_NAME = "span";
	/**
	 * The xml element name used for each message.
	 */
	private static final String MESSAGE_TAG_NAME = "div";


	/**
	 * Render the diagnostics.
	 * @param renderContext the current renderContext
	 * @param id the component being rendered
	 * @param diags the list of Diagnostic objects
	 * @param type the severity we are rendering (error, warn, info, success)
	 * @param forId the ID of the component this diagnostic message refers to.
	 * @param isTracking if true the optional "tracking" attribute will be "true"
	 */
	static void renderHelper(final WebXmlRenderContext renderContext,
			final String id,
			final List<Diagnostic> diags,
			final String type,
			final String forId,
			final boolean isTracking) {
		if (diags.isEmpty()) {
			return;
		}

		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen(TAG_NAME);
		xml.appendAttribute("id", id);
		xml.appendOptionalAttribute("track", isTracking, "true");
		xml.appendAttribute("data-wc-type", type);
		xml.appendAttribute("data-wc-dfor", forId);
		xml.appendAttribute("is", "wc-fieldindicator");
		xml.appendClose();
		for (Diagnostic diagnostic : diags) {
			xml.appendTagOpen(MESSAGE_TAG_NAME);
			xml.appendAttribute("is", "wc-message");
			xml.appendClose();
			xml.appendEscaped(diagnostic.getDescription());
			xml.appendEndTag(MESSAGE_TAG_NAME);
		}
		xml.appendEndTag(TAG_NAME);
	}

	/**
	 * Paints the given AbstractWFieldIndicator.
	 *
	 * @param component the WFieldErrorIndicator to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		AbstractWFieldIndicator fieldIndicator = (AbstractWFieldIndicator) component;
		WComponent validationTarget = fieldIndicator.getTargetComponent();

		// no need to render an indicator for nothing.
		// Diagnosables takes care of their own  messaging.
		if (validationTarget == null || (validationTarget instanceof Diagnosable && !(validationTarget instanceof Input))) {
			return;
		}

		if (validationTarget instanceof Input && !((Input) validationTarget).isReadOnly()) {
			return;
		}

		List<Diagnostic> diags = fieldIndicator.getDiagnostics();

		if (diags != null && !diags.isEmpty()) {
			String id = component.getId();
			String forId = fieldIndicator.getRelatedFieldId();
			String type = getLevel(fieldIndicator.getFieldIndicatorType());
			renderHelper(renderContext, id, diags, type, forId, component.isTracking());
		}
	}

	/**
	 * @param severity the field indicator severity.
	 * @return a string representation of the severity level.
	 */
	private static String getLevel(final AbstractWFieldIndicator.FieldIndicatorType severity) {
		switch (severity) {
			case ERROR:
				return "error";
			case WARN:
				return "warn";
			case INFO:
				return "info";
			case SUCCESS:
				return "success";
			default:
				throw new SystemException("Cannot paint field indicator due to an invalid field indicator type: " + severity);
		}
	}
}
