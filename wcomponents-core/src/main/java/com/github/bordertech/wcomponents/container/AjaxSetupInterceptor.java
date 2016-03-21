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

		// Get AJAX operation (if registered)
		AjaxOperation ajaxOperation = null;
		Map<String, AjaxOperation> operations = (Map<String, AjaxOperation>) request
				.getSessionAttribute(AjaxHelper.AJAX_OPERATIONS_SESSION_KEY);
		if (operations != null) {
			ajaxOperation = operations.get(triggerId);
		}

		// Override registered operation if is a GET and trigger supports Internal AJAX
		if (ajaxOperation != null && "GET".equals(request.getMethod()) && triggerComponent instanceof AjaxInternalTrigger) {
			// Create internal operation
			ajaxOperation = new AjaxOperation(triggerId);
		}

		// If no operation found, check if the trigger supports internal AJAX
		if (ajaxOperation == null && trigger.getComponent() instanceof AjaxInternalTrigger) {
			// Create internal operation
			ajaxOperation = new AjaxOperation(triggerId);
		}

		// No Valid operation
		if (ajaxOperation == null) {
			throw new SystemException(
					"No AJAX operation has been registered for trigger " + triggerId + ".");
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
