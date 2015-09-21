package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WServlet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * AjaxOperation describes an AJAX operation, which can replace one or more components.
 * <p>
 * See {@link #setTargetContainerId(String)} for details on how to set up lazy-loading.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class AjaxOperation implements Serializable {

	/**
	 * The AJAX trigger id (present in the HTTP servlet request) that will trigger this operation.
	 */
	private final String triggerId;

	/**
	 * The list of target components to repaint when this trigger occurs. They do not necessarily need to be contained
	 * within one section of the UI, but if containerId is set, they should be.
	 */
	private final List<String> targetIds;

	/**
	 * Id of a container target.
	 */
	private String targetContainerId;

	/**
	 * Creates an AjaxOperation.
	 *
	 * @param triggerId the trigger id. {@link WServlet} uses this as a look-up to obtain the correct AjaxOperation.
	 * @param targetId the id of the target component.
	 */
	public AjaxOperation(final String triggerId, final String targetId) {
		if (triggerId == null) {
			throw new IllegalArgumentException("Trigger id cannot be null");
		}

		if (targetId == null) {
			throw new IllegalArgumentException("Target id cannot be null");
		}

		this.triggerId = triggerId;
		this.targetIds = new ArrayList<>(1);
		targetIds.add(targetId);
	}

	/**
	 * Creates an AjaxOperation.
	 *
	 * @param triggerId the trigger id. {@link WServlet} uses this as a look-up to obtain the correct AjaxOperation.
	 * @param targetIds the ids of the target components.
	 */
	public AjaxOperation(final String triggerId, final List<String> targetIds) {
		if (triggerId == null) {
			throw new IllegalArgumentException("Trigger id cannot be null");
		}

		if (targetIds == null || targetIds.isEmpty()) {
			throw new IllegalArgumentException("Target ids must be provided.");
		}

		this.triggerId = triggerId;
		this.targetIds = targetIds;
	}

	/**
	 * Sets the target container id. Allows you to replace a child component without affecting the parent. This is
	 * necessary for lazy loading components, where child content is not initally present, and can therefore not be
	 * replaced.
	 *
	 * @param targetContainerId the target container's id.
	 */
	public void setTargetContainerId(final String targetContainerId) {
		this.targetContainerId = targetContainerId;
	}

	/**
	 * @return the target container's id
	 */
	public String getTargetContainerId() {
		return targetContainerId;
	}

	/**
	 * @return Returns the trigger id.
	 */
	public String getTriggerId() {
		return triggerId;
	}

	/**
	 * @return Returns the target.
	 */
	public List<String> getTargets() {
		return targetIds == null ? null : Collections.unmodifiableList(targetIds);
	}
}
