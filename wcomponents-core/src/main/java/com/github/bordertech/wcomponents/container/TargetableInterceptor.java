package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Targetable;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * This interceptor component checks to see if a parameter named {@link Environment#TARGET_ID} exists on the request. If
 * the parameter is found then this interceptor replaces the top level ui component with the {@link Targetable}
 * component with the same id as the parameter value.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class TargetableInterceptor extends InterceptorComponent {

	/**
	 * The id of the targeted component.
	 */
	private String targetId = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serviceRequest(final Request request) {
		// Check if this is a targeted request
		targetId = request.getParameter(Environment.TARGET_ID);
		if (targetId == null) {
			throw new SystemException("No target id request parameter");
		}

		ComponentWithContext target = WebUtilities.getComponentById(targetId, true);
		if (target == null) {
			throw new SystemException("No target component found for id " + targetId);
		}

		// go straight to the target component.
		attachUI(target.getComponent());

		UIContextHolder.pushContext(target.getContext());
		try {
			super.serviceRequest(request);
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preparePaint(final Request request) {
		ComponentWithContext target = WebUtilities.getComponentById(targetId, true);
		if (target == null) {
			throw new SystemException("No target component found for id " + targetId);
		}

		UIContextHolder.pushContext(target.getContext());
		try {
			super.preparePaint(request);
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		ComponentWithContext target = WebUtilities.getComponentById(targetId, true);
		if (target == null) {
			throw new SystemException("No target component found for id " + targetId);
		}

		UIContextHolder.pushContext(target.getContext());
		try {
			super.paint(renderContext);
		} finally {
			UIContextHolder.popContext();
		}
	}

}
