package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.ServletUtil;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Enumerator;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;

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
		if (result == null) {
			// If the file is not present in the "files" collection it may possibly be serialized as a regular form parameter
			String[] params = getParameterValues(key);
			if (params != null && params.length > 0) {
				List<FileItem> deserialized = new ArrayList<>(params.length);
				for (String param : params) {
					FileItem fileItem = getFileItemFromBase64(param, key);
					if (fileItem != null) {
						deserialized.add(fileItem);
					}
				}
				result = deserialized.toArray(new FileItem[]{});
			}
		}
		return result;
	}

	/**
	 * Transform Base64 to FileItem, assumes Base64 string is found on the same {@link #getId()} property.
	 *
	 * @param valueStr A request which may contain an uploaded file.
	 * @param id An uploaded file, if found.
	 * @return FileItem or null
	 */
	private static FileItem getFileItemFromBase64(final String valueStr, final String id) {

		if (valueStr != null && valueStr.length() > 0) {
			String delims = "[,]";
			String[] parts = valueStr.split(delims);
			if (parts.length < 2) {
				return null;
			}
			byte[] decodedBytes = Base64.decodeBase64(parts[1].getBytes());
			try {
				File tempFile;
				FileItem fileItem;
				OutputStream outputStream;
				try (InputStream inputStream = new ByteArrayInputStream(decodedBytes)) {
					int availableBytes = inputStream.available();
					// Write the inputStream to a FileItem
					// temp file, store here in order to avoid storing it in memory
					tempFile = File.createTempFile("temp-file-name", "");
					// link FileItem to temp file
					fileItem = new DiskFileItem(id, null, false, tempFile.getName(), availableBytes, tempFile);
					// get FileItem's output stream, and
					outputStream = fileItem.getOutputStream();
					// write inputStream in it
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = inputStream.read(bytes)) != -1) {
						outputStream.write(bytes, 0, read);
					}
					// release all resources
				}
				outputStream.flush();
				outputStream.close();
				if (!tempFile.delete()) {
					//LOG.warn("Could not delete " + tempFile.getCanonicalPath());
				}

				return fileItem;

			} catch (IOException e) {
				//LOG.error("Error decoding base64 parameter", e);
				return null;
			}
		} else {
			return null;
		}
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
	 * @deprecated Use {@link ServletUtil#uploadFileItems(java.util.List, java.util.Map, java.util.Map)} instead.
	 */
	@Deprecated
	protected static void uploadFileItems(final List fileItems, final Map<String, String[]> parameters,
			final Map<String, FileItem[]> files) {
		ServletUtil.uploadFileItems(fileItems, parameters, files);
	}

	/**
	 * Returns a byte array containing all the information contained in the given input stream.
	 *
	 * @param stream the input stream to read from.
	 * @return the stream contents as a byte array.
	 * @throws IOException if there is an error reading from the stream.
	 * @deprecated Use {@link StreamUtil#getBytes(java.io.InputStream)} instead.
	 */
	@Deprecated
	protected static byte[] readBytes(final InputStream stream) throws IOException {
		return StreamUtil.getBytes(stream);
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
