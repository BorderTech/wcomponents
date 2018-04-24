package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.portlet.context.WFileWidgetCleanup;
import com.github.bordertech.wcomponents.util.FileUtil;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.fileupload.FileItem;

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
 * @author Aswin Kandula
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
	 * @see #setFileTypes(java.util.List) 
	 * @see #setFileTypes(java.lang.String[]) 
	 * @return {@code true} if one or more file type is supplied.
	 */
	public boolean hasFileTypes() {
		FileWidgetModel fileWidgetModel = getComponentModel();
		return fileWidgetModel.fileTypes != null && fileWidgetModel.fileTypes.size() > 0;
	}

	/**
	 * Set the maximum file size (in bytes) that will be accepted by the file input. If the user selects a file larger
	 * than this value the client script will tell the user it cannot be uploaded.
	 *
	 * @param bytes The maximum size (in bytes) that can be uploaded by this input.
	 */
	public void setMaxFileSize(final long bytes) {
		if (bytes > 0) {
			getOrCreateComponentModel().maxFileSize = bytes;
		} else {
			getOrCreateComponentModel().maxFileSize = 0;
		}
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
	 * @see #setMaxFileSize(long) 
	 * @return {@code true} if max file size is supplied.
	 */
	public boolean hasMaxFileSize() {
		return getComponentModel().maxFileSize >  0;
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
			// Reset validation fields
			resetValidationState();
			// if User Model exists it will be returned, othewise Shared Model is returned
			final FileWidgetModel sharedModel = getComponentModel();
			// if fileType is supplied then validate it
			if (hasFileTypes()) {
				boolean validFileType = FileUtil.validateFileType(value, getFileTypes());
				// If invalid only then update 
				if (sharedModel.validFileType != validFileType) {
					// if User Model exists it will be returned, othewise it will be created
					final FileWidgetModel userModel = getOrCreateComponentModel();
					userModel.validFileType = validFileType;
				}
			}
			
			// if fileSize is supplied then validate it
			if (hasMaxFileSize()) {
				boolean validFileSize = FileUtil.validateFileSize(value, getMaxFileSize());
				// If invalid only then update 
				if (sharedModel.validFileSize != validFileSize) {
					// if User Model exists it will be returned, othewise it will be created
					final FileWidgetModel userModel = getOrCreateComponentModel();
					userModel.validFileSize = validFileSize;
				}
			}
			
			// if file is valid, the update data
			if (isFileSizeValid() && isFileTypeValid()) {
				setData(value);
			} else if (current == null) {
				// otherwise no change
				changed = false;
			} else {
				changed = true;
				setData(null);
			}
		} 

		return changed;
	}

	/**
	 * Reset validation state.
	 */
	private void resetValidationState() {
		// if User Model exists it will be returned, othewise Shared Model is returned
		final FileWidgetModel componentModel = getComponentModel();
		// If Shared Model is returned then both fileType and fileSize are always valid
		// If User Model is returned check if any if any is false
		if (!componentModel.validFileSize || !componentModel.validFileType) {
		    final FileWidgetModel userModel = getOrCreateComponentModel();
		    userModel.validFileType = true;
		    userModel.validFileSize = true;

		}
	}

	/**
	 * Indicates whether the uploaded file is valid. <br>
	 * If {@link #getFileTypes()} is set then it is validated, otherwise {@link #getFile()} is considered valid.
	 *
	 * @return true if file type valid, false file type invalid.
	 */
	public boolean isFileTypeValid() {
		return getComponentModel().validFileType;
	}

	/**
	 * Indicates whether the uploaded file is valid.
	 * If {@link #getMaxFileSize()} is set then it is validated, otherwise {@link #getFile()} is considered valid.
	 *
	 * @return true if file size valid, false file size invalid, otherwise null.
	 */
	public boolean isFileSizeValid() {
		return getComponentModel().validFileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		if (!isFileTypeValid()) {
			// Add invalid file type validation message.
			String invalidMessage = FileUtil.getInvalidFileTypeMessage(getFileTypes());
			Diagnostic diag = new DiagnosticImpl(Diagnostic.ERROR, this, invalidMessage);
			diags.add(diag);
		}

		if (!isFileSizeValid()) {
			// Adds invalid file size validation message.
			String invalidMessage = FileUtil.getInvalidFileSizeMessage(getMaxFileSize());
			Diagnostic diag = new DiagnosticImpl(Diagnostic.ERROR, this, invalidMessage);
			diags.add(diag);
		}
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
				return null;
			}

			FileItemWrap wrapper = new FileItemWrap(value);
			return wrapper;
		} else {
			return getValue();
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

		/**
		 * Flag to indicate if the selected file is a valid fileType.
		 */
		private boolean validFileType = true;

		/**
		 * Flag to indicate if the selected file is a valid fileSize.
		 */
		private boolean validFileSize = true;
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


