package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * Marks a component as being able to store diagnostic information.
 * @author Mark Reeves
 */
public interface Diagnosable extends WComponent {

	/**
	 * @param severity the diagnostic level we are interested in
	 * @return the list of diagnostics for the component.
	 */
	List<Diagnostic> getDiagnostics(final int severity);
}
