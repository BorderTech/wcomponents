package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextDelegate;
import com.github.bordertech.wcomponents.UIContextHolder;

/**
 * LookupTableHelper provides convenience methods to register lookup tables for use with the data list servlet.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class LookupTableHelper {

	/**
	 * The key we use to store the UIContext in the user's session. The data list servlet will use this key to retrieve
	 * the UIContext and process the request.
	 */
	private static final String DATA_LIST_UIC_SESSION_KEY = "dataList.uic";

	/**
	 * Prevent instantiation of this class.
	 */
	private LookupTableHelper() {
	}

	/**
	 * Registers a data list with the servlet.
	 *
	 * @param key the list key.
	 * @param request the current request being responded to.
	 */
	public static void registerList(final String key, final Request request) {
		UIContext uic = UIContextHolder.getCurrent();
		request.setSessionAttribute(DATA_LIST_UIC_SESSION_KEY, UIContextDelegate.
				getPrimaryUIContext(uic));
	}

	/**
	 * Retrieves the UIContext for the given data list.
	 *
	 * @param key the list key.
	 * @param request the current request being responded to.
	 *
	 * @return the UIContext for the given key.
	 */
	public static UIContext getContext(final String key, final Request request) {
		return (UIContext) request.getSessionAttribute(DATA_LIST_UIC_SESSION_KEY);
	}
}
