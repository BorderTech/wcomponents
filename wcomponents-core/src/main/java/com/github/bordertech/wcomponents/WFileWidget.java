package com.github.bordertech.wcomponents;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;

import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.portlet.context.WFileWidgetCleanup;
import com.github.bordertech.wcomponents.util.Util;

/**
 * <p>
 * The WFileWidget represents a "File Chooser" form widget. The {@link #getBytes() "bytes"} property is updated with the
 * binary data from the uploaded file. If the user submits a form with no file chosen, the bytes array will be set to
 * null.
 * </p>
 * <p>
 * The current implementation creates a {@link FileItem} which will be written temporarily to disk if the size of the
 * file reaches a threshold. A reaper thread is started to clean up those temp files no longer being used. When using
 * this component, developers should include the {@link WFileWidgetCleanup} context listener to their application to
 * kill this thread when the application is stopped. i.e the web.xml should include:
 * </p>
 *
 * <pre>
 *    &lt;listener&gt;
 *    &lt;listener-class&gt;com.github.bordertech.wcomponents.portlet.context.WFileWidgetCleanup&lt;/listener-class&gt;
 *    &lt;/listener&gt;
 * </pre>
 * <p>
 * The WFileWidget allows developers to limit the maximum file size and types of files which can be uploaded.
 * </p>
 *
 * @author James Gifford
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 *
 * @deprecated Use {@link WMultiFileWidget} instead.
 */
@Deprecated
public class WFileWidget extends AbstractInput implements AjaxTarget, SubordinateTarget {

	/**
	 * Returns a list of strings that determine the allowable file mime types accepted by the file input. If no types
	 * have been added an empty list is returned. An empty list indicates that all file types are accepted.
	 *
	 * @return The mime types accepted by this file input e.g. "text/plain", "text/html", "application/pdf".
	 */
	public List<String> getFileTypes() {
		List<String> fileTypes = getComponentModel().fileTypes;

		if (fileTypes == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(fileTypes);
	}

	/**
	 * Set each file type as a valid file mime type to be accepted by the WMultiFileWidget.
	 *
	 * @param types The mime types that will be accepted by the file input.
	 */
	public void setFileTypes(final String[] types) {
		if (types == null) {
			setFileTypes((List<String>) null);
		} else {
			setFileTypes(Arrays.asList(types));
		}
	}

	/**
	 * Set each file type as a valid file mime type to be accepted by the WMultiFileWidget.
	 *
	 * @param types The mime types that will be accepted by the file input.
	 */
	public void setFileTypes(final List<String> types) {
		getOrCreateComponentModel().fileTypes = types;
	}

	/**
	 * Set the maximum file size (in bytes) that will be accepted by the file input. If the user selects a file larger
	 * than this value the client script will tell the user it cannot be uploaded.
	 *
	 * @param bytes The maximum size (in bytes) that can be uploaded by this input.
	 */
	public void setMaxFileSize(final long bytes) {
		getOrCreateComponentModel().maxFileSize = bytes;
	}

	/**
	 * Return the maximum file size (in bytes) that can be accepted by this file input.
	 *
	 * @return The maximum size (in bytes) that can be uploaded by this component.
	 */
	public long getMaxFileSize() {
		return getComponentModel().maxFileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		FileItemWrap value = getRequestValue(request);
		FileItemWrap current = getValue();

		boolean changed = value != null || current != null;

		if (changed) {
			setData(value);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getActionCommand() {
		return getFileName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileItemWrap getRequestValue(final Request request) {
		if (isPresent(request)) {
			FileItem value = request.getFileItem(getId());

			// No file selected
			if (Util.empty(value.getName()) && value.getSize() == 0) {
				value = getFileItemFromBase64(request);
				
				if (value == null) {
					return null;
				}
			}

			FileItemWrap wrapper = new FileItemWrap(value);
			return wrapper;
		} else {
			return getValue();
		}
	}
	
	/**
	 * Transform Base64 to FileItem, assumes Base64 string is found on the same {@link #getId()} property
	 * 
	 * @param request
	 * @return fileItem
	 */
	private FileItem getFileItemFromBase64(final Request request) {
		String valueStr = request.getParameter(getId());
		
		if (valueStr !=  null) {
			String delims="[,]";
			String[] parts = valueStr.split(delims);
			//String contentType = Pattern.compile("[a-zA-Z0-9]+/[a-zA-Z0-9-.+]+").matcher(parts[0]).group(1);
		    
			byte[] decodedBytes = Base64.decodeBase64(parts[1].getBytes());
			try {
				InputStream inputStream = new ByteArrayInputStream(decodedBytes);
				int availableBytes = inputStream.available();
				
				// Write the inputStream to a FileItem
				
				// temp file, store here in order to avoid storing it in memory
				File tempFile = File.createTempFile("temp-file-name","");
				// link FileItem to temp file 
				FileItem fileItem = new DiskFileItem(getId(), null, false, tempFile.getName(), availableBytes, tempFile);
				// get FileItem's output stream, and 
				OutputStream outputStream = fileItem.getOutputStream();  
				// write inputStream in it
		        int read = 0;
		        byte[] bytes = new byte[1024];
		        while ((read = inputStream.read(bytes)) != -1) {
		            outputStream.write(bytes, 0, read);
		        }
		        // release all resources
		        inputStream.close();
		        outputStream.flush();
		        outputStream.close();
		        tempFile.delete();
				
		        return fileItem;
				 
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * * {@inheritDoc}
	 */
	@Override
	protected boolean isPresent(final Request request) {
		return request.getFileItem(getId()) != null;
	}

	/**
	 * Retrieves the contents of the uploaded file.
	 *
	 * @return the file contents, or null if there was no file uploaded.
	 */
	public byte[] getBytes() {
		FileItemWrap wrapper = getValue();

		if (wrapper != null) {
			return wrapper.getBytes();
		}

		return null;
	}

	/**
	 * Retrieves an input stream of the uploaded file's contents.
	 *
	 * @return an input stream of the file's contents, or null if there was no file uploaded
	 * @throws IOException if there is an error obtaining the input stream from the uploaded file.
	 */
	public InputStream getInputStream() throws IOException {
		FileItemWrap wrapper = getValue();

		if (wrapper != null) {
			return wrapper.getInputStream();
		}

		return null;
	}

	/**
	 * @return the size of the uploaded file, or zero if there was no file uploaded
	 */
	public long getSize() {
		FileItemWrap wrapper = getValue();

		if (wrapper != null) {
			return wrapper.getSize();
		}

		return 0;
	}

	/**
	 * @return the file name of the uploaded file, or null if there was no file uploaded
	 */
	public String getFileName() {
		FileItemWrap wrapper = getValue();

		if (wrapper != null) {
			return wrapper.getName();
		}

		return null;
	}

	/**
	 * Retrieves the File item that has been uploaded.
	 *
	 * @return the File item that has been uploaded by the client.
	 */
	public FileItemWrap getFile() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileItemWrap getValue() {
		return (FileItemWrap) getData();
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Holds the extrinsic state information of a WFileWidget.
	 */
	public static class FileWidgetModel extends InputModel {

		/**
		 * The mime types accepted by the file input.
		 */
		private List<String> fileTypes;

		/**
		 * The maximum size of files uploaded by this component.
		 */
		private long maxFileSize;
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new FileWidgetModel.
	 */
	@Override
	protected FileWidgetModel newComponentModel() {
		return new FileWidgetModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected FileWidgetModel getComponentModel() {
		return (FileWidgetModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected FileWidgetModel getOrCreateComponentModel() {
		return (FileWidgetModel) super.getOrCreateComponentModel();
	}
}
