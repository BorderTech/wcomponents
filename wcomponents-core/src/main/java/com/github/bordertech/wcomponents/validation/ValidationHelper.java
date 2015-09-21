package com.github.bordertech.wcomponents.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods to assist with validation.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public final class ValidationHelper {

	/**
	 * Prevent instantiation of this class.
	 */
	private ValidationHelper() {
	}

	/**
	 * Extract diagnostics with a given severity from a list of Diagnostic objects. This is useful for picking errors
	 * out from a list of diagnostics, for example.
	 *
	 * @param diagnostics the list of diagnostics to look through.
	 * @param severity the severity of diagnostics to extract.
	 * @return a list of diagnostics with the given severity, may be empty.
	 */
	public static List<Diagnostic> extractDiagnostics(final List<Diagnostic> diagnostics,
			final int severity) {
		ArrayList<Diagnostic> extract = new ArrayList<>();

		for (Diagnostic diagnostic : diagnostics) {
			if (diagnostic.getSeverity() == severity) {
				extract.add(diagnostic);
			}
		}

		return extract;
	}
}
