package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextDebugWrapper;
import com.github.bordertech.wcomponents.UIContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This interceptor removes component model objects that are no longer needed from the UIContext. This is not essential
 * other than to keep web server memory usage down.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class ContextCleanupInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ContextCleanupInterceptor.class);

	/**
	 * Override serviceRequest to clear out the targetable index from the last request. And clear out the scratch map
	 * after the request has been processed (and before painting occurs).
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void serviceRequest(final Request request) {
		LOG.debug("Before Service Request - Clearing targetable index.");
		UIContext uic = UIContextHolder.getCurrent();

		super.serviceRequest(request);
		// Clear phase scope scratch map
		LOG.debug("After Service Request - Clearing scratch map with phase scope.");
		uic.clearScratchMap();
	}

	/**
	 * Override paint to clear out the scratch map and component models which are no longer necessary.
	 *
	 * @param renderContext the renderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		super.paint(renderContext);
		UIContext uic = UIContextHolder.getCurrent();

		if (LOG.isDebugEnabled()) {
			UIContextDebugWrapper debugWrapper = new UIContextDebugWrapper(uic);
			LOG.debug("Session usage after paint:\n" + debugWrapper);
		}

		LOG.debug("Performing session tidy up of WComponents (any WComponents disconnected from the active top component will not be tidied up.");
		getUI().tidyUpUIContextForTree();

		LOG.debug("After paint - Clearing scratch maps.");
		uic.clearScratchMap();
		uic.clearRequestScratchMap();
	}
}
