package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.AjaxInternalTrigger;
import com.github.bordertech.wcomponents.AjaxOperation;
import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.Map;

/**
 * This interceptor setups the AJAX operation details.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AjaxSetupInterceptor extends InterceptorComponent {

	/**
	 * Setup the AJAX operation details.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {

		// Get trigger id
		String triggerId = request.getParameter(WServlet.AJAX_TRIGGER_PARAM_NAME);
		if (triggerId == null) {
			throw new SystemException("No AJAX trigger id to on request");
		}

		// Find the Component for this trigger
		ComponentWithContext trigger = WebUtilities.getComponentById(triggerId,
				true);
		if (trigger == null) {
			throw new SystemException("No component found for AJAX trigger " + triggerId + ".");
		}
		WComponent triggerComponent = trigger.getComponent();

		// Check for an internal AJAX request (if client has flagged it as internal)
		boolean internal = request.getParameter(WServlet.AJAX_TRIGGER_INTERNAL_PARAM_NAME) != null;

		AjaxOperation ajaxOperation = null;
		if (internal) {
			if (!(triggerComponent instanceof AjaxInternalTrigger)) {
				throw new SystemException("AJAX trigger [" + triggerId + "] does not support internal actions.");
			}
			// Create internal operation
			ajaxOperation = new AjaxOperation(triggerId);
		} else {
			// Check for a registered AJAX operation
			Map<String, AjaxOperation> operations = (Map<String, AjaxOperation>) request
					.getSessionAttribute(AjaxHelper.AJAX_OPERATIONS_SESSION_KEY);
			if (operations != null) {
				ajaxOperation = operations.get(triggerId);
			}

			// Override registered operation if it is a GET and trigger supports Internal AJAX
			// TODO This is only required until all components start using the Internal param flag
			if (ajaxOperation != null && "GET".equals(request.getMethod()) && triggerComponent instanceof AjaxInternalTrigger) {
				// Create internal operation
				ajaxOperation = new AjaxOperation(triggerId);
			}
		}

		// If no operation registered, check if the trigger supports internal AJAX then assume it is an Internal Action
		if (ajaxOperation == null && trigger.getComponent() instanceof AjaxInternalTrigger) {
			// Create internal operation
			ajaxOperation = new AjaxOperation(triggerId);
		}

		// No Valid operation
		if (ajaxOperation == null) {
			throw new SystemException("No AJAX operation has been registered for trigger " + triggerId + ".");
		}

		// Set current operation
		AjaxHelper.setCurrentOperationDetails(ajaxOperation, trigger);

		// Process Service Request
		super.serviceRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		try {
			super.paint(renderContext);
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}
	}
}
