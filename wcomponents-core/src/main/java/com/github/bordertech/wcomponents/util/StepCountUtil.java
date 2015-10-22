package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WAudio;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WVideo;
import com.github.bordertech.wcomponents.WebUtilities;

/**
 * Static utility methods related to working with the step count.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class StepCountUtil {

	/**
	 * The parameter key for the URL users are redirected to when a step error occurs.
	 */
	public static final String STEP_ERROR_URL_PARAMETER_KEY = "bordertech.wcomponents.wrongStep.redirect.url";

	/**
	 * Prevent instantiation of utility class.
	 */
	private StepCountUtil() {
	}

	/**
	 * @return the url users are redirected to when a step error occurs
	 */
	public static String getErrorUrl() {
		String url = Config.getInstance().getString(STEP_ERROR_URL_PARAMETER_KEY);
		return url;
	}

	/**
	 * @return true if users are to be redirected when a step error occurs
	 */
	public static boolean isErrorRedirect() {
		return !Util.empty(getErrorUrl());
	}

	/**
	 * Increments the step that is recorded in session.
	 *
	 * @param uic the current user's session
	 */
	public static void incrementSessionStep(final UIContext uic) {
		int step = uic.getEnvironment().getStep();
		uic.getEnvironment().setStep(step + 1);
	}

	/**
	 * Retrieves the value of the step variable from the given request.
	 *
	 * @param request the request being responded to.
	 * @return the request step present in the request, or -1 on error.
	 */
	public static int getRequestStep(final Request request) {
		String val = request.getParameter(Environment.STEP_VARIABLE);

		if (val == null) {
			return 0;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException ex) {
			return -1;
		}
	}

	/**
	 * Checks if the step count is on the request.
	 *
	 * @param request the request being responded to.
	 * @return true if the step count is on the request
	 */
	public static boolean isStepOnRequest(final Request request) {
		String val = request.getParameter(Environment.STEP_VARIABLE);
		return val != null;
	}

	/**
	 * Check if the request is for cached content.
	 *
	 * @param request the request being processed
	 * @return true if content is cached, otherwise false
	 */
	public static boolean isCachedContentRequest(final Request request) {

		// Get target id on request
		String targetId = request.getParameter(Environment.TARGET_ID);
		if (targetId == null) {
			return false;
		}

		// Get target
		ComponentWithContext targetWithContext = WebUtilities.getComponentById(targetId, true);
		if (targetWithContext == null) {
			return false;
		}

		// Check for caching key
		WComponent target = targetWithContext.getComponent();
		UIContextHolder.pushContext(targetWithContext.getContext());

		try {
			// TODO Look at implementing CacheableTarget interface
			String key = null;
			if (target instanceof WContent) {
				key = ((WContent) target).getCacheKey();
			} else if (target instanceof WImage) {
				key = ((WImage) target).getCacheKey();
			} else if (target instanceof WVideo) {
				key = ((WVideo) target).getCacheKey();
			} else if (target instanceof WAudio) {
				key = ((WAudio) target).getCacheKey();
			}
			return !Util.empty(key);
		} finally {
			UIContextHolder.popContext();
		}
	}

}
