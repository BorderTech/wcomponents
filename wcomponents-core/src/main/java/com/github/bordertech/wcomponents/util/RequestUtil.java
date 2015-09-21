package com.github.bordertech.wcomponents.util;

import java.util.Map;
import org.apache.commons.fileupload.FileItem;

/**
 * This class provides helpers for processing Requests.
 *
 * @author Rick Brown
 * @since 1.0.0
 */
public final class RequestUtil {

	/**
	 * Hide the constructor as there are no instance methods.
	 */
	private RequestUtil() {
	}

	/**
	 * Add the file data to the files collection. If a file already exists with the given name then the value for this
	 * name will be an array of all registered files.
	 *
	 * @param files the map in which to store file data.
	 * @param name the name of the file, i.e. the key to store the file against.
	 * @param item the file data.
	 */
	public static void addFileItem(final Map files, final String name, final FileItem item) {
		if (files.containsKey(name)) {
			// This field contains multiple values, append the new value to the existing values.
			Object oldValue = files.get(name);

			if (oldValue instanceof FileItem) {
				files.put(name, new FileItem[]{(FileItem) oldValue, item});
			} else if (oldValue instanceof FileItem[]) {
				FileItem[] oldValues = (FileItem[]) oldValue;
				FileItem[] newValues = new FileItem[oldValues.length + 1];
				System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
				newValues[newValues.length - 1] = item;
				files.put(name, newValues);
			} else {
				throw new SystemException("Unknown parameter type: " + item.getClass());
			}
		} else {
			files.put(name, item);
		}
	}

	/**
	 * Add the request parameter to the parameters collection. If a parameter already exists with the given name then
	 * the Map will contain an array of all registered values.
	 *
	 * @param parameters the map to store non-file request parameters in.
	 * @param name the name of the parameter.
	 * @param value the parameter value.
	 */
	public static void addParameter(final Map parameters, final String name, final String value) {
		if (parameters.containsKey(name)) {
			// This field contains multiple values, append the new value to the existing values.
			Object oldValue = parameters.get(name);

			if (oldValue instanceof String) {
				parameters.put(name, new String[]{(String) oldValue, value});
			} else if (oldValue instanceof String[]) {
				String[] oldValues = (String[]) oldValue;
				String[] newValues = new String[oldValues.length + 1];
				System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
				newValues[newValues.length - 1] = value;
				parameters.put(name, newValues);
			} else {
				throw new SystemException("Unknown parameter type: " + value.getClass());
			}
		} else {
			parameters.put(name, value);
		}
	}

}
