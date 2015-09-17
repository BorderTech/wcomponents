package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.subordinate.SubordinateControlHelper;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * This {@link InterceptorComponent} will process any subordinate controls that were registered as being active on the
 * client (i.e. rendered).
 * <p>
 * It applies the subordinate controls before the service request phase, to apply any state changes that have occurred
 * on the client, and after the prepare paint phase to make sure the subordinate logic has been applied and the
 * components are in the correct state before being rendered to the client.
 * </p>
 *
 * @see WSubordinateControl
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SubordinateControlInterceptor extends InterceptorComponent {

	/**
	 * Before servicing the request, apply the registered subordinate controls to make sure any state changes that have
	 * occurred on the client are applied.
	 *
	 * @param request the request being serviced
	 */
	@Override
	public void serviceRequest(final Request request) {
		// Only apply for POST
		if ("POST".equals(request.getMethod())) {
			// Apply Controls (Use values on request)
			SubordinateControlHelper.applyRegisteredControls(request, true);
		}

		// Service Request
		super.serviceRequest(request);
	}

	/**
	 * After the prepare paint phase, apply the registered subordinate controls to make sure all the components are in
	 * the correct state before being rendered to the client.
	 *
	 * @param request the request being serviced
	 */
	@Override
	public void preparePaint(final Request request) {
		// Clear all registered controls on Session
		SubordinateControlHelper.clearAllRegisteredControls(request);

		super.preparePaint(request);

		// Apply Controls (Use values from the component models)
		SubordinateControlHelper.applyRegisteredControls(request, false);
	}
}
