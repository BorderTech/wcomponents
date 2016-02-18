package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.AjaxOperation;
import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This {@link InterceptorComponent} looks for an ajax 'trigger' parameter in the request. The trigger parameter
 * identifies the {@link WAjaxControl} to be handled by the request. In the paint phase the interceptor will only paint
 * the targeted components to the response.
 *
 * @see WAjaxControl
 * @author Christina Harris
 * @since 1.0.0
 */
public class AjaxInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AjaxInterceptor.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serviceRequest(final Request request) {
		String triggerId = request.getParameter(WServlet.AJAX_TRIGGER_PARAM_NAME);

		AjaxOperation ajaxOperation = AjaxHelper.getCurrentOperation();
		if (ajaxOperation == null) {
			throw new IllegalStateException(
					"No AJAX operation available for trigger " + triggerId + ".");
		}

		ComponentWithContext triggerWithContext = AjaxHelper.getCurrentTriggerAndContext();
		if (triggerWithContext == null) {
			throw new IllegalStateException(
					"No component/context available for AJAX trigger " + triggerId + ".");
		}

		UIContext uic = UIContextHolder.getCurrent();

		// Reset the focus for this new request.
		uic.setFocussed(null, null);

		// We've hit the action phase, so we do want focus on this app.
		uic.setFocusRequired(true);

		// Process trigger only
		if (isProcessTriggerOnly(triggerWithContext, ajaxOperation)) {
			// Get user context
			UIContext tuic = triggerWithContext.getContext();
			UIContextHolder.pushContext(tuic);
			try {
				WComponent trigger = triggerWithContext.getComponent();
				trigger.serviceRequest(request);
				// Manually invoke laters as the InvokeLaters in the service request is not run due to the trigger
				// having a "parent"
				tuic.doInvokeLaters();
			} finally {
				UIContextHolder.popContext();
			}
		} else if ("GET".equals(request.getMethod())) { // GET only supports the above scenarios
			throw new IllegalStateException(
					"GET is not supported for the AJAX trigger " + triggerId + ".");
		} else {
			// service the request
			super.serviceRequest(request);
		}
	}

	/**
	 * Paints the targeted ajax regions. The format of the response is an agreement between the server and the client
	 * side JavaScript handling our ajax response.
	 *
	 * @param renderContext the renderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		AjaxOperation operation = AjaxHelper.getCurrentOperation();
		if (operation == null) {
			// the request attribute that we place in the ui context in the action phase can't be null
			throw new SystemException(
					"Can't paint AJAX response. Couldn't find the expected reference to the AjaxOperation.");
		}

		if (operation.getTargetContainerId() != null) {
			paintContainerResponse(renderContext, operation);
		} else {
			paintResponse(renderContext, operation);
		}
	}

	/**
	 * Paint the ajax container response.
	 *
	 * @param renderContext the render context
	 * @param operation the ajax operation
	 */
	private void paintContainerResponse(final RenderContext renderContext,
			final AjaxOperation operation) {
		WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
		XmlStringBuilder xml = webRenderContext.getWriter();

		// Get trigger's context
		ComponentWithContext trigger = AjaxHelper.getCurrentTriggerAndContext();
		if (trigger == null) {
			throw new SystemException("No context available for trigger " + operation.getTriggerId());
		}

		xml.appendTagOpen("ui:ajaxtarget");
		xml.appendAttribute("id", operation.getTargetContainerId());
		xml.appendAttribute("action", "replaceContent");
		xml.appendClose();

		// Paint targets - Assume targets are in the same context as the trigger
		UIContextHolder.pushContext(trigger.getContext());
		try {
			for (String targetId : operation.getTargets()) {
				ComponentWithContext target;
				if (targetId.equals(operation.getTriggerId())) {
					target = trigger;
				} else {
					target = WebUtilities.getComponentById(targetId, true);
					if (target == null) {
						LOG.warn("Could not find ajax target to render [" + targetId + "]");
						continue;
					}
				}
				target.getComponent().paint(renderContext);
			}
		} finally {
			UIContextHolder.popContext();
		}

		xml.appendEndTag("ui:ajaxtarget");
	}

	/**
	 * Paint the ajax response.
	 *
	 * @param renderContext the render context
	 * @param operation the ajax operation
	 */
	private void paintResponse(final RenderContext renderContext, final AjaxOperation operation) {
		WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
		XmlStringBuilder xml = webRenderContext.getWriter();

		// Get trigger's context
		ComponentWithContext trigger = AjaxHelper.getCurrentTriggerAndContext();
		if (trigger == null) {
			throw new SystemException("No context available for trigger " + operation.getTriggerId());
		}

		for (String targetId : operation.getTargets()) {
			ComponentWithContext target;
			if (targetId.equals(operation.getTriggerId())) {
				target = trigger;
			} else {
				target = WebUtilities.getComponentById(targetId, true);
				if (target == null) {
					LOG.warn("Could not find ajax target to render [" + targetId + "]");
					continue;
				}
			}

			UIContextHolder.pushContext(target.getContext());
			try {
				xml.appendTagOpen("ui:ajaxtarget");
				xml.appendAttribute("id", targetId);
				xml.appendAttribute("action", "replace");
				xml.appendClose();

				target.getComponent().paint(renderContext);

				xml.appendEndTag("ui:ajaxtarget");
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Check if process this trigger only.
	 *
	 * @param triggerWithContext the trigger with its context
	 * @param operation current ajax operation
	 * @return true if process this trigger only
	 */
	private boolean isProcessTriggerOnly(final ComponentWithContext triggerWithContext,
			final AjaxOperation operation) {
		// Target container implies only process the trigger
		if (operation.getTargetContainerId() != null) {
			return true;
		}

		WComponent trigger = triggerWithContext.getComponent();

		// Check if trigger is a polling AJAX control
		if (trigger instanceof WAjaxControl) {
			// Get user context
			UIContext uic = triggerWithContext.getContext();
			UIContextHolder.pushContext(uic);
			try {
				WAjaxControl ajax = (WAjaxControl) trigger;
				// Is a polling region so only process trigger
				if (ajax.getDelay() > 0) {
					return true;
				}
			} finally {
				UIContextHolder.popContext();
			}
		}

		// Check if the operation only has one target and it is the same as the trigger
		List<String> targets = operation.getTargets();
		if (targets == null || targets.isEmpty() || targets.size() > 1) {
			return false;
		}
		return operation.getTriggerId().equals(targets.get(0));
	}

}
