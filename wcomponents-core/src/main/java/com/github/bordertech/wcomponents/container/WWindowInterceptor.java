package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextDelegate;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UserAgentInfo;
import com.github.bordertech.wcomponents.WWindow;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This interceptor temporarily replaces the primary UIContext's {@link Environment} with an Environment suitable for
 * processing a content request for a WWindow.
 * </p>
 * <p>
 * The replacement of the environment is not thread-safe, but the interceptor methods are only ever called from code in
 * {@link AbstractContainerHelper} which synchronizes on the primary context. This prevents any concurrency issues.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WWindowInterceptor extends InterceptorComponent {

	/**
	 * The ID of the component being targeted by the content helper servlet.
	 */
	private String windowId;

	/**
	 * Flag if window should be attached to chain.
	 */
	private final boolean attachWindow;

	/**
	 * @param attachWindow true if attach window to interceptor chain
	 */
	public WWindowInterceptor(final boolean attachWindow) {
		this.attachWindow = attachWindow;
	}

	/**
	 * Temporarily replaces the environment while the request is being handled.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void serviceRequest(final Request request) {
		// Get window id off the request
		windowId = request.getParameter(WWindow.WWINDOW_REQUEST_PARAM_KEY);

		if (windowId == null) {
			super.serviceRequest(request);
		} else {
			// Get the window component
			ComponentWithContext target = WebUtilities.getComponentById(windowId, true);
			if (target == null) {
				throw new SystemException("No window component for id " + windowId);
			}

			// Setup the Environment on the context
			UIContext uic = UIContextDelegate.getPrimaryUIContext(UIContextHolder.getCurrent());
			Environment originalEnvironment = uic.getEnvironment();
			uic.setEnvironment(new EnvironmentDelegate(originalEnvironment, windowId, target));

			if (attachWindow) {
				attachUI(target.getComponent());
			}
			UIContextHolder.pushContext(target.getContext());
			try {
				super.serviceRequest(request);
			} finally {
				uic.setEnvironment(originalEnvironment);
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Temporarily replaces the environment while the UI prepares to render.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaint(final Request request) {
		if (windowId == null) {
			super.preparePaint(request);
		} else {
			// Get the window component
			ComponentWithContext target = WebUtilities.getComponentById(windowId, true);
			if (target == null) {
				throw new SystemException("No window component for id " + windowId);
			}

			UIContext uic = UIContextDelegate.getPrimaryUIContext(UIContextHolder.getCurrent());
			Environment originalEnvironment = uic.getEnvironment();
			uic.setEnvironment(new EnvironmentDelegate(originalEnvironment, windowId, target));

			UIContextHolder.pushContext(target.getContext());
			try {
				super.preparePaint(request);
			} finally {
				uic.setEnvironment(originalEnvironment);
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Temporarily replaces the environment while the UI is being rendered.
	 *
	 * @param renderContext the context to render to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		if (windowId == null) {
			super.paint(renderContext);
		} else {
			// Get the window component
			ComponentWithContext target = WebUtilities.getComponentById(windowId, true);
			if (target == null) {
				throw new SystemException("No window component for id " + windowId);
			}

			UIContext uic = UIContextDelegate.getPrimaryUIContext(UIContextHolder.getCurrent());
			Environment originalEnvironment = uic.getEnvironment();
			uic.setEnvironment(new EnvironmentDelegate(originalEnvironment, windowId, target));

			UIContextHolder.pushContext(target.getContext());
			try {
				super.paint(renderContext);
			} finally {
				uic.setEnvironment(originalEnvironment);
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * This Environment implementation delegates all methods to a backing Environment instance, except for methods
	 * relating to the step counter.
	 */
	private static final class EnvironmentDelegate implements Environment {

		/**
		 * The backing environment instance.
		 */
		private final Environment backing;

		/**
		 * The ID of the component being targeted by the content helper servlet.
		 */
		private final String windowId;

		/**
		 * WWindow with the context.
		 */
		private final ComponentWithContext window;

		/**
		 * Creates an EnvironmentDelegate.
		 *
		 * @param backing the backing environment.
		 * @param windowId the ID of the component being targeted.
		 * @param window the window with its context
		 */
		private EnvironmentDelegate(final Environment backing, final String windowId,
				final ComponentWithContext window) {
			this.backing = backing;
			this.windowId = windowId;
			this.window = window;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSessionToken() {
			return backing.getSessionToken();
		}

		/**
		 * {@inheritDoc}
		 *
		 * @deprecated portal specific
		 */
		@Deprecated
		@Override
		public int getActionStep() {
			return backing.getActionStep();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getAppHostPath() {
			return backing.getAppHostPath();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getAppId() {
			return backing.getAppId();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getBaseUrl() {
			return backing.getBaseUrl();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getFormEncType() {
			return backing.getFormEncType();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getHostFreeBaseUrl() {
			return backing.getHostFreeBaseUrl();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPostPath() {
			if (windowId == null) {
				return backing.getPostPath();
			} else {
				Map<String, String> parameters = new HashMap<>();
				parameters.put(WWindow.WWINDOW_REQUEST_PARAM_KEY, windowId);
				String url = getWServletPath();
				return WebUtilities.getPath(url, parameters, true);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getWServletPath() {
			return backing.getWServletPath();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getThemePath() {
			return backing.getThemePath();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UserAgentInfo getUserAgentInfo() {
			return backing.getUserAgentInfo();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setSessionToken(final String token) {
			backing.setSessionToken(token);
		}

		/**
		 * {@inheritDoc}
		 *
		 * @deprecated portal specific
		 */
		@Deprecated
		@Override
		public void setActionStep(final int actionStep) {
			backing.setActionStep(actionStep);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setAppId(final String appId) {
			backing.setAppId(appId);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setFormEncType(final String enctype) {
			backing.setFormEncType(enctype);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPostPath(final String postPath) {
			backing.setPostPath(postPath);
		}

		/**
		 * The {@link #windowId} will need to be carried through to subsequent requests so that the correct component
		 * will continue being targeted. The step counter should also be retrieved from a WWindow if one is present.
		 *
		 * @return the hidden parameters.
		 */
		@Override
		public Map<String, String> getHiddenParameters() {
			Map<String, String> map = backing.getHiddenParameters();

			if (windowId != null) {
				map.put(STEP_VARIABLE, String.valueOf(getStep()));
				map.put(WWindow.WWINDOW_REQUEST_PARAM_KEY, windowId);
			}

			return map;
		}

		/**
		 * Override getStep to retrieve the step from a WWindow if the targeted component is a WWindow or a descendant
		 * of one.
		 *
		 * @return the step count.
		 */
		@Override
		public int getStep() {
			// WWindows require a special case, as they keep track of their own separate step.
			// We must check if the request is an AJAX or content target inside a WWindow,
			// and if so return that WWindow's step. Otherwise, we return the step on the
			// environment.
			UIContextHolder.pushContext(window.getContext());

			try {
				WWindow targetWindow = (WWindow) window.getComponent();
				return targetWindow.getStep();
			} finally {
				UIContextHolder.popContext();
			}
		}

		/**
		 * Override setStep to store the step on a WWindow if the targeted component is a WWindow or a descendant of
		 * one.
		 *
		 * @param step the step count to set.
		 */
		@Override
		public void setStep(final int step) {
			// WWindows require a special case, as they keep track of their own separate step.
			// We must check if the request is an AJAX or content target inside a WWindow,
			// and if so set that WWindow's step. Otherwise, we set the step on the
			// environment.
			UIContextHolder.pushContext(window.getContext());

			try {
				WWindow targetWindow = (WWindow) window.getComponent();
				targetWindow.setStep(step);
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

}
