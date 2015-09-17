package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Enumerator;
import com.github.bordertech.wcomponents.util.RequestUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This abstract class is intended to support all the various request implementations.
 *
 * @author Martin Shevchenko
 */
public abstract class AbstractRequest implements Request {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractRequest.class);

	private boolean logout;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParameter(final String key) {
		Object value = getParameters().get(key);

		if (value == null) {
			return null;
		}

		if (value.getClass().isArray()) {
			String[] values = (String[]) value;

			if (values.length == 0) {
				return null;
			}

			return values[0];
		}

		return (String) value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParameterValues(final String key) {
		Object value = getParameters().get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return new String[]{(String) value};
		}

		return (String[]) value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getFileContents(final String key) {
		FileItem file = (FileItem) getFiles().get(key);
		return file.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileItem[] getFileItems(final String key) {
		Object file = getFiles().get(key);

		if (file == null) {
			return null;
		}

		if (file instanceof FileItem) {
			return new FileItem[]{(FileItem) file};
		}
		return (FileItem[]) file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileItem getFileItem(final String key) {
		Object file = getFiles().get(key);
		if (file instanceof FileItem[]) {
			// For backwards compatibility we ignore multiple files and simply return one of them.
			return ((FileItem[]) file)[0];
		}
		return (FileItem) file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration getParameterNames() {
		return new Enumerator(getParameters().keySet().iterator());
	}

	/**
	 * @return the complete list of parameters contained in this request. If the request contains no parameters, the
	 * method returns an empty <code>Map</code>.
	 */
	@Override
	public abstract Map getParameters();

	/**
	 * Retrieves the files which were uploaded in this request.
	 *
	 * @return the uploaded files.
	 */
	public abstract Map getFiles();

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
	 * <p>
	 * {@link FileItem} classes (if attachements) will be kept as part of the request. The default behaviour of the file
	 * item is to store the upload in memory until it reaches a certain size, after which the content is streamed to a
	 * temp file.</p>
	 *
	 * <p>
	 * If, in the future, performance of uploads becomes a focus we can instead look into using the Jakarta Commons
	 * Streaming API. In this case, the content of the upload isn't stored anywhere. It will be up to the user to
	 * read/store the content of the stream.</p>
	 *
	 * @param fileItems a list of {@link FileItem}s corresponding to POSTed form data.
	 * @param parameters the map to store non-file request parameters in.
	 * @param files the map to store the uploaded file parameters in.
	 */
	protected static void uploadFileItems(final List fileItems, final Map parameters,
			final Map files) {
		for (int i = 0; i < fileItems.size(); i++) {
			FileItem item = (FileItem) fileItems.get(i);
			String name = item.getFieldName();
			boolean formField = item.isFormField();

			if (LOG.isDebugEnabled()) {
				LOG.debug(
						"Uploading form " + (formField ? "field" : "attachment") + " \"" + name + "\"");
			}

			if (formField) {
				String value;
				try {
					// Without specifying UTF-8, apache commons DiskFileItem defaults to ISO-8859-1.
					value = item.getString("UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new SystemException("Encoding error on formField item", e);
				}
				RequestUtil.addParameter(parameters, name, value);
			} else {
				// Form attachment
				RequestUtil.addFileItem(files, name, item);
				String value = item.getName();
				RequestUtil.addParameter(parameters, name, value);
			}
		}
	}

	/**
	 * Returns a byte array containing all the information contained in the given input stream.
	 *
	 * @param stream the input stream to read from.
	 * @return the stream contents as a byte array.
	 * @throws IOException if there is an error reading from the stream.
	 */
	protected static byte[] readBytes(final InputStream stream) throws IOException {
		// Load stuff into a byte array
		ByteArrayOutputStream buffOut = new ByteArrayOutputStream();

		while (stream.available() > 0) {
			byte[] buff = new byte[stream.available()];
			int size = stream.read(buff);

			if (size != -1) {
				buffOut.write(buff, 0, size);
			}
		}

		stream.close();
		byte[] buff = buffOut.toByteArray();
		return buff;
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
	public void setEvent(final String action, final HashMap<String, Serializable> eventMap) {
		// default behaviour is to do nothing
	}
}
