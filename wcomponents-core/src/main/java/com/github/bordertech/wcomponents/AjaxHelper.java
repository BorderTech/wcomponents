package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AjaxHelper provides convenience methods to register components for use with the AJAX servlet.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class AjaxHelper {

	/**
	 * The trigger id of the component being serviced. This is necessary because the request is not available in the
	 * render phase.
	 */
	private static final ThreadLocal<AjaxOperation> THREAD_LOCAL_OPERATION = new ThreadLocal<>();

	/**
	 * Holds the trigger component and its context.
	 */
	private static final ThreadLocal<ComponentWithContext> THREAD_LOCAL_COMPONENT_WITH_CONTEXT = new ThreadLocal<>();

	/**
	 * The key we use to store the operations in the user's session. The ajax servlet will use this key to retrieve the
	 * UIC and process the request.
	 */
	private static final String AJAX_OPERATIONS_SESSION_KEY = "ajax.control.operations";

	/**
	 * Prevent instantiation of this class.
	 */
	private AjaxHelper() {
	}

	/**
	 * @param trigger the AJAX trigger to check
	 * @return true if this is the current AJAX trigger
	 */
	public static boolean isCurrentAjaxTrigger(final WComponent trigger) {
		if (trigger == null) {
			return false;
		}
		AjaxOperation operation = AjaxHelper.getCurrentOperation();
		return operation != null && operation.getTriggerId().equals(trigger.getId());
	}

	/**
	 * Sets the current AJAX operation details.
	 *
	 * @param operation the current AJAX operation.
	 * @param trigger the current AJAX operation trigger and its context.
	 */
	public static void setCurrentOperationDetails(final AjaxOperation operation, final ComponentWithContext trigger) {
		if (operation == null) {
			THREAD_LOCAL_OPERATION.remove();
		} else {
			THREAD_LOCAL_OPERATION.set(operation);
		}

		if (trigger == null) {
			THREAD_LOCAL_COMPONENT_WITH_CONTEXT.remove();
		} else {
			THREAD_LOCAL_COMPONENT_WITH_CONTEXT.set(trigger);
		}
	}

	/**
	 * @return the current Ajax operation (if any).
	 */
	public static AjaxOperation getCurrentOperation() {
		return THREAD_LOCAL_OPERATION.get();
	}

	/**
	 * @return the current AJAX trigger component and its context.
	 */
	public static ComponentWithContext getCurrentTriggerAndContext() {
		return THREAD_LOCAL_COMPONENT_WITH_CONTEXT.get();
	}

	/**
	 * Clear the details of the current AJAX operation on the thread.
	 */
	public static void clearCurrentOperationDetails() {
		THREAD_LOCAL_COMPONENT_WITH_CONTEXT.remove();
		THREAD_LOCAL_OPERATION.remove();
	}

	/**
	 * Registers one or more components as being AJAX capable.
	 *
	 * @param targetIds the components to register. Each component will be re-painted when the trigger occurs.
	 * @param triggerId the id of the trigger that will cause the components to be painted.
	 * @return the AjaxOperation control configuration object.
	 */
	public static AjaxOperation registerComponents(final List<String> targetIds, final String triggerId) {
		AjaxOperation operation = new AjaxOperation(triggerId, targetIds);
		registerAjaxOperation(operation);
		return operation;
	}

	/**
	 * Registers a single component as being AJAX capable.
	 *
	 * @param targetId the component to register. The component will be re-painted when the trigger occurs.
	 * @param triggerId the id of the trigger that will cause the component to be painted.
	 * @return the AjaxOperation control configuration object.
	 */
	public static AjaxOperation registerComponent(final String targetId, final String triggerId) {
		AjaxOperation operation = new AjaxOperation(triggerId, targetId);
		registerAjaxOperation(operation);
		return operation;
	}

	/**
	 * This internal method is used to register an arbitrary target container. It must only used by components which
	 * contain implicit AJAX capability.
	 *
	 * @param triggerId the id of the trigger that will cause the component to be painted.
	 * @param containerId the target container id. This is not necessarily a WComponent id.
	 * @param containerContentId the container content.
	 * @return the AjaxOperation control configuration object.
	 */
	static AjaxOperation registerContainer(final String triggerId, final String containerId,
			final String containerContentId) {
		AjaxOperation operation = new AjaxOperation(triggerId, containerContentId);
		operation.setTargetContainerId(containerId);
		operation.setAction(AjaxOperation.AjaxAction.REPLACE_CONTENT);
		registerAjaxOperation(operation);
		return operation;
	}

	/**
	 * This internal method is used to register an arbitrary target container. It must only used by components which
	 * contain implicit AJAX capability.
	 *
	 * @param triggerId the id of the trigger that will cause the component to be painted.
	 * @param containerId the target container id. This is not necessarily a WComponent id.
	 * @param containerContentIds the container content.
	 * @return the AjaxOperation control configuration object.
	 */
	static AjaxOperation registerContainer(final String triggerId, final String containerId,
			final List<String> containerContentIds) {
		AjaxOperation operation = new AjaxOperation(triggerId, containerContentIds);
		operation.setTargetContainerId(containerId);
		operation.setAction(AjaxOperation.AjaxAction.REPLACE_CONTENT);
		registerAjaxOperation(operation);
		return operation;
	}

	/**
	 * Retrieves the AjaxOperation that has been registered for the given trigger. This method will return null if there
	 * is no corresponding operation registered.
	 *
	 * @param triggerId the trigger id.
	 * @return the AjaxOperation corresponding to the trigger id.
	 */
	public static AjaxOperation getAjaxOperation(final String triggerId) {
		Map<String, AjaxOperation> operations = getRegisteredOperations();
		return operations == null ? null : operations.get(triggerId);
	}

	/**
	 * Clear the registered AJAX operations for this user context.
	 */
	public static void clearAllRegisteredOperations() {
		UIContext uic = UIContextHolder.getCurrentPrimaryUIContext();
		if (uic != null) {
			uic.setFwkAttribute(AJAX_OPERATIONS_SESSION_KEY, null);
		}
	}

	/**
	 *
	 * @return the registered AJAX operations for this user context or null
	 */
	public static Map<String, AjaxOperation> getRegisteredOperations() {
		UIContext uic = UIContextHolder.getCurrentPrimaryUIContext();
		return uic == null ? null : (Map<String, AjaxOperation>) uic.getFwkAttribute(AJAX_OPERATIONS_SESSION_KEY);
	}

	/**
	 * The Ajax servlet needs access to the AjaxOperation Store the operation in the user context using the trigger Id,
	 * as this will be present in the Servlet HttpRequest. agreed key. The ajax id is passed in the url to the servlet
	 * so it can then access the context.
	 *
	 * @param operation the operation to register.
	 */
	private static void registerAjaxOperation(final AjaxOperation operation) {
		UIContext uic = UIContextHolder.getCurrentPrimaryUIContext();
		if (uic == null) {
			throw new SystemException("No User Context Available to Register AJAX Operations.");
		}
		Map<String, AjaxOperation> operations = (Map<String, AjaxOperation>) uic.getFwkAttribute(AJAX_OPERATIONS_SESSION_KEY);
		if (operations == null) {
			operations = new HashMap<>();
			uic.setFwkAttribute(AJAX_OPERATIONS_SESSION_KEY, operations);
		}
		operations.put(operation.getTriggerId(), operation);
	}
}
