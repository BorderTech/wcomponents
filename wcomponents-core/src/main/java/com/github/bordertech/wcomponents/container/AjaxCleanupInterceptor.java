package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.Request;

/**
 * This interceptor cleans up any registered AJAX operations.
 * <p>
 * Make sure the only registered AJAX operations are for the current page.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public class AjaxCleanupInterceptor extends InterceptorComponent {

	/**
	 * Clear all the registered AJAX operations.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {
		// Clear all the registered AJAX operations
		AjaxHelper.clearAllRegisteredOperations(request);
		// Process Service Request
		super.serviceRequest(request);
	}
}
