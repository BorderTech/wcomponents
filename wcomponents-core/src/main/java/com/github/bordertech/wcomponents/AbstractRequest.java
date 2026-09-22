package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Enumerator;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;

/**
 * This abstract class is intended to support all the various request implementations.
 *
 * @author Martin Shevchenko
 */
public abstract class AbstractRequest implements Request {

	private boolean logout;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParameter(final String key) {
		String[] value = getParameters().get(key);
		return value == null || value.length == 0 ? null : value[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParameterValues(final String key) {
		return getParameters().get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getFileContents(final String key) {
		FileItem file = getFileItem(key);
		return file == null ? null : file.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileItem[] getFileItems(final String key) {
		FileItem[] result = getFiles().get(key);
		/* The commented code below would allow us to transparently handle serialized file uploads encoded as Base64
		if (result == null) {
			String[] params = getParameterValues(key);
			if (params != null && params.length > 0) {
				List<FileItem> deserialized = new ArrayList<>(params.length);
				for (String param : params) {
					FileItem fileItem = getFileItemFromBase64(param);
					if (fileItem != null) {
						deserialized.add(fileItem);
					}
				}
				result = deserialized.toArray(new FileItem[]{});
			}
		}
		 */
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileItem getFileItem(final String key) {
		FileItem[] value = getFileItems(key);
		return value == null || value.length == 0 ? null : value[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration getParameterNames() {
		return new Enumerator(getParameters().keySet().iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsSameData(final Request other) {
		if (!(other instanceof AbstractRequest)) {
			return false;
		}

		Map ours = getParameters();
		Map theirs = ((AbstractRequest) other).getParameters();
		return Util.equals(ours, theirs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAppPreferenceParameter(final String key) {
		return Config.getInstance().getString(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout() {
		logout = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLogout() {
		return logout;
	}

	/**
	 * This method contains no logic. Subclasses which need to perform event handling logic (eg.
	 * <code>WPortletRequest</code>) should override this method.
	 *
	 * @param actionName the name of the action that is invoking the event
	 * @param parameter the key of the parameter to store in the eventMap
	 * @param value the value of the parameter to store in the eventMap
	 * @since 1.0.0
	 * @deprecated portal specific
	 */
	@Override
	@Deprecated
	public void setEvent(final String actionName, final String parameter, final Serializable value) {
		// default behaviour is to do nothing
	}

	/**
	 * This method contains no logic. Subclasses which need to perform event handling logic (eg.
	 * <code>WPortletRequest</code>) should override this method.
	 *
	 * @param action name of the publishing event to trigger
	 * @param eventMap the key/value pair for the event payload
	 * @since 1.0.0
	 * @deprecated portal specific
	 */
	@Override
	@Deprecated
	public void setEvent(final String action, final HashMap<String, Serializable> eventMap) {
		// default behaviour is to do nothing
	}
}
