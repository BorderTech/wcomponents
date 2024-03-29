package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Diagnosable;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;
import java.util.UUID;

/**
 * Utility to render inline diagnostic messages.
 * @author Mark Reeves
 * @since 1.4.12
 */
public final class DiagnosticRenderUtil {

	/**
	 * Prevent instantiation.
	 */
	private DiagnosticRenderUtil() {
	}

	/**
	 * @param severity the diagnostic severity.
	 * @return a string representation of the diagnostic severity level.
	 */
	private static String getLevel(final int severity) {
		switch (severity) {
			case Diagnostic.ERROR:
				return "error";
			case Diagnostic.WARNING:
				return "warn";
			case Diagnostic.INFO:
				return "info";
			case Diagnostic.SUCCESS:
				return "success";
			default:
				throw new SystemException("Unexpected diagnostic severity");
		}
	}

	/**
	 * Render the diagnostics.
	 * @param renderContext the current renderContext
	 * @param component the component being rendered
	 * @param diags the list of Diagnostic objects
	 * @param severity the severity we are rendering
	 */
	private static void renderHelper(final WebXmlRenderContext renderContext,
			final Diagnosable component,
			final List<Diagnostic> diags,
			final int severity) {

		String id = "_wc_".concat(UUID.randomUUID().toString());
		String forId = component.getId();
		String type = getLevel(severity);

		AbstractWFieldIndicatorRenderer.renderHelper(renderContext, id, diags, type, forId, false);
	}

	/**
	 * Render diagnostics for the component.
	 * @param component the component being rendered
	 * @param renderContext the RenderContext to paint to.
	 */
	public static void renderDiagnostics(final Diagnosable component, final WebXmlRenderContext renderContext) {
		List<Diagnostic> diags = component.getDiagnostics(Diagnostic.ERROR);
		if (diags != null) {
			renderHelper(renderContext, component, diags, Diagnostic.ERROR);
		}
		diags = component.getDiagnostics(Diagnostic.WARNING);
		if (diags != null) {
			renderHelper(renderContext, component, diags, Diagnostic.WARNING);
		}
		diags = component.getDiagnostics(Diagnostic.INFO);
		if (diags != null) {
			renderHelper(renderContext, component, diags, Diagnostic.INFO);
		}
		diags = component.getDiagnostics(Diagnostic.SUCCESS);
		if (diags != null) {
			renderHelper(renderContext, component, diags, Diagnostic.SUCCESS);
		}
	}
}
