package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.container.SubordinateControlInterceptor;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.HashSet;
import java.util.Set;
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
	private static final String SUBORDINATE_CONTROL_SESSION_KEY = "subordinate.control.active";

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
	 */
	public static void registerSubordinateControl(final String controlId) {
		UIContext uic = UIContextHolder.getCurrentPrimaryUIContext();
		if (uic == null) {
			throw new SystemException("No User Context available to register Subordinate Control.");
		}
		Set<String> controls = (Set<String>) uic.getFwkAttribute(SUBORDINATE_CONTROL_SESSION_KEY);
		if (controls == null) {
			controls = new HashSet<>();
			uic.setFwkAttribute(SUBORDINATE_CONTROL_SESSION_KEY, controls);
		}
		controls.add(controlId);
	}

	/**
	 * @return the registered subordinate controls or null
	 */
	public static Set<String> getRegisteredSubordinateControls() {
		UIContext uic = UIContextHolder.getCurrentPrimaryUIContext();
		return uic == null ? null : (Set<String>) uic.getFwkAttribute(SUBORDINATE_CONTROL_SESSION_KEY);
	}

	/**
	 * Apply the registered Subordinate Controls.
	 *
	 * @param request the request being processed.
	 * @param useRequestValues the flag to indicate the controls should use values from the request.
	 */
	public static void applyRegisteredControls(final Request request, final boolean useRequestValues) {

		Set<String> controls = getRegisteredSubordinateControls();
		if (controls == null) {
			return;
		}

		// Process Controls
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

			WSubordinateControl control = (WSubordinateControl) controlWithContext.getComponent();
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

	/**
	 * Clear all registered Subordinate Controls on the session.
	 */
	public static void clearAllRegisteredControls() {
		UIContext uic = UIContextHolder.getCurrentPrimaryUIContext();
		if (uic != null) {
			uic.setFwkAttribute(SUBORDINATE_CONTROL_SESSION_KEY, null);
		}
	}

}
