package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.container.SubordinateControlInterceptor;
import java.util.HashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SubordinateControlHelper provides convenience methods to register Subordinate Controls for use with the
 * {@link SubordinateControlInterceptor}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class SubordinateControlHelper {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SubordinateControlHelper.class);

	/**
	 * The key we use to store the subordinate controls that are currently active on the client.
	 */
	public static final String SUBORDINATE_CONTROL_SESSION_KEY = "subordinate.control.active";

	/**
	 * Prevent instantiation of this class.
	 */
	private SubordinateControlHelper() {
		// Do Nothing
	}

	/**
	 * Register the Subordinate Control so that it can be applied by the {@link SubordinateControlInterceptor}.
	 *
	 * @param controlId the subordinate id
	 * @param request the request to store the operation under.
	 */
	public static void registerSubordinateControl(final String controlId, final Request request) {
		HashSet<String> controls = (HashSet<String>) request.getSessionAttribute(
				SUBORDINATE_CONTROL_SESSION_KEY);
		if (controls == null) {
			controls = new HashSet<>();
			request.setSessionAttribute(SUBORDINATE_CONTROL_SESSION_KEY, controls);
		}
		controls.add(controlId);
	}

	/**
	 * Apply the registered Subordinate Controls.
	 *
	 * @param request the request being processed.
	 * @param useRequestValues the flag to indicate the controls should use values from the request.
	 */
	public static void applyRegisteredControls(final Request request, final boolean useRequestValues) {
		HashSet<String> controls = (HashSet<String>) request.getSessionAttribute(
				SUBORDINATE_CONTROL_SESSION_KEY);

		if (controls != null) {
			for (String controlId : controls) {
				// Find the Component for this ID
				ComponentWithContext controlWithContext = WebUtilities.getComponentById(controlId,
						true);
				if (controlWithContext == null) {
					LOG.warn(
							"Subordinate control for id " + controlId + " is no longer in the tree.");
					continue;
				}

				if (!(controlWithContext.getComponent() instanceof WSubordinateControl)) {
					LOG.warn("Component for id " + controlId + " is not a subordinate control.");
					continue;
				}

				WSubordinateControl control = (WSubordinateControl) controlWithContext.
						getComponent();
				UIContext uic = controlWithContext.getContext();

				UIContextHolder.pushContext(uic);

				try {
					if (useRequestValues) {
						control.applyTheControls(request);
					} else {
						control.applyTheControls();
					}
				} finally {
					UIContextHolder.popContext();
				}
			}
		}
	}

	/**
	 * Clear all registered Subordinate Controls on the session.
	 *
	 * @param request the request being processed.
	 */
	public static void clearAllRegisteredControls(final Request request) {
		request.setSessionAttribute(SUBORDINATE_CONTROL_SESSION_KEY, null);
	}
}
