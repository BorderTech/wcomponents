package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Diagnosable;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * Utility to render inline diagnostic messages.
 * @author Mark Reeves
 * @since 1.4.12
 */
public final class DiagnosticRenderUtil {

	/**
	 * The xml element name used for diagnostic output.
	 */
	private static final String TAG_NAME = "ui:fieldindicator";
	/**
	 * The xml element name used for each message in the diagnostic output.
	 */
	private static final String MESSAGE_TAG_NAME = "ui:message";

	/**
	 * Used to create an error diagnostic for a component. Value is unimportant so long as it is different from the other extensions.
	 */
	private static final String ERR_EXTENSION = "_err";

	/**
	 * Used to create a warning diagnostic for a component. Value is unimportant so long as it is different from the other extensions.
	 */
	private static final String WARN_EXTENSION = "_wrn";

	/**
	 * Used to create an info diagnostic for a component. Value is unimportant so long as it is different from the other extensions.
	 */
	private static final String INFO_EXTENSION = "_nfo";

	/**
	 * Used to create a success diagnostic for a component. Value is unimportant so long as it is different from the other extensions.
	 */
	private static final String SUCCESS_EXTENSION = "_scc";

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
	 * @param diags the list of Diagnostic objects
	 * @param severity the severity we are rendering
	 */
	private static void renderHelper(final WebXmlRenderContext renderContext,
			final Diagnosable component,
			final List<Diagnostic> diags,
			final int severity) {
		if (diags.isEmpty()) {
			return;
		}

		XmlStringBuilder xml = renderContext.getWriter();

		String id = component.getId();
		String indicatorId = id;
		switch (severity) {
			case Diagnostic.ERROR:
				indicatorId = indicatorId.concat(ERR_EXTENSION);
				break;
			case Diagnostic.WARNING:
				indicatorId = indicatorId.concat(WARN_EXTENSION);
				break;
			case Diagnostic.INFO:
				indicatorId = indicatorId.concat(INFO_EXTENSION);
				break;
			case Diagnostic.SUCCESS:
				indicatorId = indicatorId.concat(SUCCESS_EXTENSION);
				break;
			default:
				throw new SystemException("Unexpected diagnostic severity");
		}
		xml.appendTagOpen(TAG_NAME);
		xml.appendAttribute("id", indicatorId);
		xml.appendAttribute("type", getLevel(severity));
		xml.appendAttribute("for", id);
		xml.appendClose();

		for (Diagnostic diagnostic : diags) {
			xml.appendTag(MESSAGE_TAG_NAME);
			xml.appendEscaped(diagnostic.getDescription());
			xml.appendEndTag(MESSAGE_TAG_NAME);
		}
		xml.appendEndTag(TAG_NAME);
	}

	/**
	 * Render diagnostics for the component.
	 * @param component the component being rendered
	 * @param renderContext the RenderContext to paint to.
	 */
	public static void renderDiagnostics(final Diagnosable component, final WebXmlRenderContext renderContext) {
		List<Diagnostic> diags = component.getDiagnostics(Diagnostic.WARNING);
		if (diags != null) {
			renderHelper(renderContext, component, diags, Diagnostic.WARNING);
		}
		diags = component.getDiagnostics(Diagnostic.ERROR);
		if (diags != null) {
			renderHelper(renderContext, component, diags, Diagnostic.ERROR);
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
