package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WLink.ImagePosition;
import com.github.bordertech.wcomponents.file.File;
import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.render.webxml.FileWidgetRendererUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.util.XMLUtil;
import com.github.bordertech.wcomponents.util.thumbnail.ThumbnailUtil;
import java.awt.Dimension;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The WMultiFileWidget component allows multiple file input elements to be uploaded, without requiring an entire page
 * reload for each item. After a file is uploaded to the server the client displays the file information with a checkbox
 * adjacent to it. The file information is a link that pops up the file content. Use {@link #getFiles()} to retrieve all
 * files uploaded by the client, use {@link #getSelectedFiles()} to retrieve only the selected file items.
 * </p>
 * <p>
 * The maximum number of allowed files, maximum individual file size, and allowed file types can be configured.
 * </p>
 *
 * @author Christina Harris
 * @author Jonathan Austin
 * @author Rick Brown
 * @since 1.0.0
 */
public class WMultiFileWidget extends AbstractInput implements Targetable, AjaxTrigger, AjaxTarget,
		SubordinateTarget {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WMultiFileWidget.class);

	/**
	 * File upload index content request.
	 */
	public static final String FILE_UPLOAD_ID_KEY = "wc_fileid";

	/**
	 * File upload index thumb nail content request.
	 */
	public static final String FILE_UPLOAD_THUMB_NAIL_KEY = "wc_filetn";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValueAsString() {
		List<FileWidgetUpload> files = getValue();

		if ((files == null) || files.isEmpty()) {
			return null;
		}

		StringBuffer stringValues = new StringBuffer();

		boolean append = false;
		for (FileWidgetUpload file : files) {
			if (append) {
				stringValues.append(", ");
			}
			stringValues.append(file.getFile().toString());
			append = true;
		}

		return stringValues.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FileWidgetUpload> getValue() {
		List<FileWidgetUpload> files = (List<FileWidgetUpload>) getData();

		if (files == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(files);
		}
	}

	/**
	 * Gets all the File items that have been uploaded.
	 *
	 * @return A list of all File items uploaded by the client.
	 */
	public List<FileWidgetUpload> getFiles() {
		return getValue();
	}

	/**
	 * Add a file item to this widget.
	 *
	 * @param file the file item
	 */
	public void addFile(final FileWidgetUpload file) {
		List<FileWidgetUpload> files = (List<FileWidgetUpload>) getData();
		if (files == null) {
			files = new ArrayList<>();
			setData(files);
		}
		files.add(file);
	}

	/**
	 * Remove the file.
	 *
	 * @param file the file to remove
	 */
	public void removeFile(final FileWidgetUpload file) {
		List<FileWidgetUpload> files = (List<FileWidgetUpload>) getData();
		if (files != null) {
			files.remove(file);
			if (files.isEmpty()) {
				setData(null);
			}
		}
	}

	/**
	 * Retrieves the File at the given index. Will throw an {@link IndexOutOfBoundsException} if <code>index</code> is
	 * not in the range of the list of items.
	 *
	 * @param fileId the file id
	 * @return The FileWidgetUpload for the file id or null
	 */
	public FileWidgetUpload getFile(final String fileId) {
		for (FileWidgetUpload file : getFiles()) {
			if (Util.equals(file.getFileId(), fileId)) {
				return file;
			}
		}
		return null;
	}

	/**
	 * Returns only the selected file items. The file is selected if the checkbox adjacent to the uploaded file name is
	 * checked by the user.
	 *
	 * @return The uploaded file items that have been selected by the user. If no files have been selected then an empty
	 * list is returned.
	 * @deprecated no longer required as unselected files are now removed. So all files are "selected".
	 */
	@Deprecated
	public List<FileWidgetUpload> getSelectedFiles() {
		return getValue();
	}

	/**
	 * Indicates whether the given upload has been selected by the user.
	 *
	 * @param upload the uploaded file
	 * @return true if the upload has been selected, false otherwise.
	 * @deprecated no longer required as unselected files are now removed. So all files are "selected".
	 */
	@Deprecated
	public boolean isSelected(final File upload) {
		return true;
	}

	/**
	 * Clear uploaded files from this component.
	 */
	public void clearFiles() {
		resetData();
	}

	/**
	 * Set each file type to be accepted by the WMultiFileWidget.
	 *
	 * @see #setFileTypes(java.util.Collection) for the file types
	 *
	 * @param types The file types that will be accepted by the file input.
	 */
	public void setFileTypes(final String[] types) {
		if (types == null) {
			setFileTypes((List<String>) null);
		} else {
			setFileTypes(Arrays.asList(types));
		}
	}

	/**
	 * Determines the file types accepted by this widget. Note that duplicates are not allowed and these are not case
	 * sensitive. A file type may be one of the following:
	 * <ul>
	 * <li>The string audio/* (Indicates that sound files are accepted.)</li>
	 * <li>The string video/* (Indicates that video files are accepted.)</li>
	 * <li>The string image/* (Indicates that image files are accepted.)</li>
	 * <li>A valid MIME type with no parameters (Indicates that files of the specified type are accepted.)</li>
	 * <li>A string whose first character is a "." (U+002E) character (Indicates that files with the specified file
	 * extension are accepted).</li>
	 * </ul>
	 *
	 * @param types The file types that will be accepted by the file input. Note that this is not additive, it will
	 * overwrite any previously set fileTypes. Pass null or and empty collection to clear all file types.
	 */
	public void setFileTypes(final Collection<String> types) {
		Set newFileTypes = null;
		MultiFileWidgetModel model = getOrCreateComponentModel();
		if (types != null) {
			newFileTypes = new HashSet<>();
			for (String fileType : types) {
				if (fileType != null) {
					String uniqueFileType = fileType.toLowerCase();  // Lowercase to avoid duplication
					if (isValid(uniqueFileType)) {
						newFileTypes.add(uniqueFileType);
					} else {
						throw new IllegalArgumentException("Not a valid filetype: " + fileType);
					}
				}
			}
		}
		model.fileTypes = newFileTypes;
	}

	/**
	 * Check that the file type SEEMS to be legit.
	 *
	 * @param fileType The file type to check
	 * @return true if the file type is valid.
	 */
	private boolean isValid(final String fileType) {
		boolean result = false;
		if (fileType != null && fileType.length() > 1) { // the shortest I can think of would be something like ".h"
			if (fileType.startsWith(".")) { // assume it's a file extension
				result = true;
			} else if (fileType.length() > 2 && fileType.indexOf('/') > 0) { // some imaginary mimetype like "a/*" would be at least 3 characters
				result = true;
			}
		}
		return result;
	}

	/**
	 * Returns a list of file types accepted by the file input.
	 *
	 * @see #setFileTypes(Collection) for a description of what constitutes an allowable file types
	 *
	 * If no types have been added an empty list is returned. An empty list indicates that all file types are accepted.
	 *
	 * @return The file types accepted by this file input e.g. "image/*", ".vis", "text/plain", "text/html",
	 * "application/pdf".
	 */
	public List<String> getFileTypes() {
		Set<String> fileTypes = getComponentModel().fileTypes;
		List<String> result;
		if (fileTypes == null || fileTypes.isEmpty()) {
			return Collections.emptyList();
		}
		result = new ArrayList<>(fileTypes);
		return result;
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
	 * Return the maximum number of files that can be accepted by this file input.
	 *
	 * @return The maximum number of files that can be uploaded by this component.
	 */
	public int getMaxFiles() {
		return getComponentModel().maxFiles;
	}

	/**
	 * Set the maximum number of files that will be accepted by the file input.
	 *
	 * @param maxFiles The maximum number of files that can be uploaded by this input.
	 */
	public void setMaxFiles(final int maxFiles) {
		getOrCreateComponentModel().maxFiles = maxFiles;
	}

	/**
	 * Register a component to receive drag and dropped files on behalf of this input. It is recommended that you
	 * register the WApplication as the dropzone. This allows users to drop files anywhere on the page and eliminates
	 * the risk of them dropping files outside of the dropzone (which causes the browser to attempt to render the
	 * dropped files).
	 *
	 * @param dropzone The dropzone.
	 */
	public void setDropzone(final DropZone dropzone) {
		getOrCreateComponentModel().dropzone = dropzone;
	}

	/**
	 * Return the dropzone associated with this file input.
	 *
	 * @return The dropzone or null if not set.
	 */
	public DropZone getDropzone() {
		return getComponentModel().dropzone;
	}

	/**
	 * Registers an image editor with this file upload widget so that the user will be prompted to edit (crop, rotate etc).
	 * This obviously only makes sense if this widget is configured to only allow image file types.
	 * It is probably also a logical idea to set max files to one.
	 *
	 *
	 * @param editor The image editor.
	 */
	public void setEditor(final WImageEditor editor) {
		getOrCreateComponentModel().editor = editor;
	}

	/**
	 * Return the image editor associated with this file input.
	 *
	 * @return The editor or null if not set.
	 */
	public WImageEditor getEditor() {
		return getComponentModel().editor;
	}


	/**
	 * The AJAX action used when an uploaded file has been selected.
	 * <p>
	 * Setting this action causes the uploaded file links to act as AJAX triggers. The file id of the selected file is
	 * set as the action object.
	 * </p>
	 *
	 * @param action the file AJAX action
	 */
	public void setFileAjaxAction(final Action action) {
		getOrCreateComponentModel().fileAction = action;
	}

	/**
	 * The AJAX action used when an uploaded file has been selected.
	 *
	 * @return the file AJAX action
	 */
	public Action getFileAjaxAction() {
		return getComponentModel().fileAction;
	}

	/**
	 * Sets the layout of uploaded files to be a certain number of columns. Null uses the theme default.
	 *
	 * @param cols the number of columns.
	 */
	public void setColumns(final Integer cols) {
		if (cols != null && cols < 0) {
			throw new IllegalArgumentException("Must have zero or more columns");
		}
		getOrCreateComponentModel().cols = cols;
	}

	/**
	 * @return the number of columns for layout of uploaded files. Null uses the theme default.
	 */
	public Integer getColumns() {
		return getComponentModel().cols;
	}

	/**
	 * Used in handle request processing to trigger on change processing.
	 *
	 * @return true if a new file has been uploaded
	 */
	protected boolean isNewUpload() {
		return getComponentModel().newUpload;
	}

	/**
	 * @param newUpload true if a new file has been uploaded
	 */
	public void setNewUpload(final boolean newUpload) {
		getOrCreateComponentModel().newUpload = newUpload;
	}

	/**
	 * @return true if generate thumb nails for the file links.
	 */
	public boolean isUseThumbnails() {
		return getComponentModel().useThumbnails;
	}

	/**
	 * If enabled then uploaded files will display to the user as thumbnails. While this can be used for any file types
	 * it is only recommended when the WMultiFileWidget is set to accept image types only, e.g. with
	 * <code>setFileTypes(new String[] { "image/*" });</code>
	 *
	 * @param useThumbnails true if generate thumb nails for the file links.
	 */
	public void setUseThumbnails(final boolean useThumbnails) {
		getOrCreateComponentModel().useThumbnails = useThumbnails;
	}

	/**
	 * @return the position of the thumbnail image on the file link
	 */
	public ImagePosition getThumbnailPosition() {
		return getComponentModel().thumbnailPosition;
	}

	/**
	 * The position of the thumbnail image on the file link.
	 * <p>
	 * If no position is set then the text is not shown.
	 * </p>
	 *
	 * @param thumbnailPosition the position of the image
	 */
	public void setThumbnailPosition(final ImagePosition thumbnailPosition) {
		getOrCreateComponentModel().thumbnailPosition = thumbnailPosition;
	}

	/**
	 * Retrieve the thumbnail size. If null, the default size is used.
	 *
	 * @return the thumbnail size or null for default
	 */
	public Dimension getThumbnailSize() {
		return getComponentModel().thumbnailSize;
	}

	/**
	 * Set the thumbnail size. Null uses the default size.
	 * <p>
	 * To scale thumbnails to a certain height or width, use -1 on the scalable dimension. For example, to scale
	 * thumbnails to 64 pixels high but maintain the correct width ration, set Height to 64 and Width to -1.
	 * </p>
	 *
	 * @param thumbnailSize the thumbnail size or null for default
	 */
	public void setThumbnailSize(final Dimension thumbnailSize) {
		if (thumbnailSize != null) {
			if (thumbnailSize.height == 0 || thumbnailSize.width == 0) {
				throw new IllegalArgumentException(
						"Thumbnail size cannot have a height or width of zero.");
			}
			if (thumbnailSize.height == -1 && thumbnailSize.width == -1) {
				throw new IllegalArgumentException(
						"Thumbnail size cannot have both height and width set to -1.");
			}
		}
		getOrCreateComponentModel().thumbnailSize = thumbnailSize;
	}

	/**
	 * Clear the thumbnails currently set on the files. This will cause them to be generated again when requested. This
	 * can be used if the thumbanil size has changed.
	 */
	public void clearThumbnails() {
		for (FileWidgetUpload file : getFiles()) {
			file.setThumbnail(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// This is a normal page request.
		List<FileWidgetUpload> newSelections = getNewSelection(request);
		List<FileWidgetUpload> priorSelections = getValue();

		boolean changed = !selectionsEqual(newSelections, priorSelections);

		if (changed) {
			setData(newSelections);
		}

		// Check for new uploads
		if (isNewUpload()) {
			changed = true;
			setNewUpload(false);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FileWidgetUpload> getRequestValue(final Request request) {
		if (isPresent(request)) {
			return getNewSelection(request);
		} else {
			return getValue();
		}
	}

	/**
	 * Register the widget for AJAX.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (getFileAjaxAction() != null) {
			AjaxHelper.registerComponentTargetItself(getId(), request);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean beforeHandleRequest(final Request request) {
		// Check if is targeted request
		String targetParam = request.getParameter(Environment.TARGET_ID);
		boolean targetted = (targetParam != null && targetParam.equals(getTargetId()));
		if (targetted) {
			doHandleTargetedRequest(request);
			return false;
		}

		// Check if AJAX trigger and has file id in request
		if (AjaxHelper.isCurrentAjaxTrigger(this) && request.getParameter(FILE_UPLOAD_ID_KEY) != null) {
			doHandleFileAjaxActionRequest(request);
			return false;
		}

		return true;
	}

	/**
	 * Handle a file action AJAX request.
	 *
	 * @param request the request being processed
	 */
	protected void doHandleFileAjaxActionRequest(final Request request) {
		// Protect against client-side tampering of disabled components
		if (isDisabled()) {
			throw new SystemException("File widget is disabled.");
		}

		// Check for file id
		String fileId = request.getParameter(FILE_UPLOAD_ID_KEY);
		if (fileId == null) {
			throw new SystemException("No file id provided for ajax action.");
		}

		// Check valid file id
		FileWidgetUpload file = getFile(fileId);
		if (file == null) {
			throw new SystemException("Invalid file id [" + fileId + "].");
		}

		// Run the action
		final Action action = getFileAjaxAction();
		if (action == null) {
			throw new SystemException("No action set for file ajax action request.");
		}

		// Set the selected file id as the action object
		final ActionEvent event = new ActionEvent(this, "fileajax", fileId);
		Runnable later = new Runnable() {
			@Override
			public void run() {
				action.execute(event);
			}
		};

		invokeLater(later);
	}

	/**
	 * Handle a targeted request.
	 *
	 * @param request the request being processed
	 */
	protected void doHandleTargetedRequest(final Request request) {
		// Upload file request
		if (request.getFileItems(getId()) != null) {
			doHandleUploadRequest(request);
			return;
		}

		// Check for file id
		String fileId = request.getParameter(FILE_UPLOAD_ID_KEY);
		if (fileId == null) {
			throw new SystemException("No file id provided for content request.");
		}

		// Check valid file id
		FileWidgetUpload file = getFile(fileId);
		if (file == null) {
			throw new SystemException("Invalid file id [" + fileId + "].");
		}

		// Check if thumb nail requested
		boolean thumbNail = request.getParameter(FILE_UPLOAD_THUMB_NAIL_KEY) != null;
		if (thumbNail) {
			doHandleThumbnailRequest(file);
		} else {
			doHandleFileContentRequest(file);
		}
	}

	/**
	 * The request is a targeted file upload request. Upload the file and respond with the file information.
	 *
	 * @param request the request being processed.
	 */
	protected void doHandleUploadRequest(final Request request) {
		// Protect against client-side tampering of disabled/read-only fields.
		if (isDisabled() || isReadOnly()) {
			throw new SystemException("File widget cannot be updated.");
		}

		// Only process on a POST
		if (!"POST".equals(request.getMethod())) {
			throw new SystemException(
					"File widget cannot be updated by " + request.getMethod() + ".");
		}

		// Check only one file item in the request
		FileItem[] items = request.getFileItems(getId());
		if (items.length > 1) {
			throw new SystemException("More than one file item received on the request.");
		}

		// Check the client provided a fileID
		String fileId = request.getParameter(FILE_UPLOAD_ID_KEY);
		if (fileId == null) {
			throw new SystemException("No file id provided for file upload.");
		}

		// Wrap the file item
		FileItemWrap wrap = new FileItemWrap(items[0]);
		FileWidgetUpload file = new FileWidgetUpload(fileId, wrap);
		addFile(file);

		int idx = getFiles().size() - 1;

		setNewUpload(true);

		// Build response
		StringWriter writer = new StringWriter();
		XmlStringBuilder xml = new XmlStringBuilder(writer);

		UIContext uic = UIContextHolder.getCurrent();

		xml.append(XMLUtil.getXMLDeclarationWithThemeXslt(uic));

		xml.appendTagOpen("ui:ajaxResponse");
		xml.append(XMLUtil.STANDARD_NAMESPACES);
		xml.appendClose();
		xml.appendTagOpen("ui:ajaxTarget");
		xml.appendAttribute("id", getId());
		xml.appendAttribute("action", "replace");
		xml.appendClose();

		FileWidgetRendererUtil.renderFileElement(this, xml, file, idx);

		xml.appendEndTag("ui:ajaxTarget");
		xml.appendEndTag("ui:ajaxResponse");

		FileUploadXMLResponse content = new FileUploadXMLResponse(writer.getBuffer().toString());
		ContentEscape escape = new ContentEscape(content);
		throw escape;
	}

	/**
	 * Handle the thumb nail request.
	 *
	 * @param file the file to process
	 */
	protected void doHandleThumbnailRequest(final FileWidgetUpload file) {
		// Create thumb nail (if required)
		if (file.getThumbnail() == null) {
			Image thumbnail = createThumbNail(file.getFile());
			file.setThumbnail(thumbnail);
		}
		ContentEscape escape = new ContentEscape(file.getThumbnail());
		throw escape;
	}

	/**
	 * Handle the file content request.
	 *
	 * @param file the file to process
	 */
	protected void doHandleFileContentRequest(final FileWidgetUpload file) {
		ContentEscape escape = new ContentEscape(file.getFile());
		throw escape;
	}

	/**
	 * @param file the file to create a thumbnail for
	 * @return the thumbnail
	 */
	protected Image createThumbNail(final File file) {
		Image image = null;
		try {
			Dimension size = getThumbnailSize();
			image = ThumbnailUtil.createThumbnail(file.getInputStream(), file.getName(), size, file.
					getMimeType());
		} catch (Exception e) {
			LOG.error("Could not generate thumbnail for file. " + e.getMessage(), e);
		}
		return image;
	}

	/**
	 * Retrieves a URL for the uploaded file content.
	 *
	 * @param fileId the file id
	 * @return the URL to access the uploaded file.
	 */
	public String getFileUrl(final String fileId) {
		FileWidgetUpload file = getFile(fileId);
		if (file == null) {
			return null;
		}

		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(Environment.TARGET_ID, getTargetId());

		if (Util.empty(file.getFileCacheKey())) {
			// Add some randomness to the URL to prevent caching
			String random = WebUtilities.generateRandom();
			parameters.put(Environment.UNIQUE_RANDOM_PARAM, random);
		} else {
			// Remove step counter as not required for cached content
			parameters.remove(Environment.STEP_VARIABLE);
			parameters.remove(Environment.SESSION_TOKEN_VARIABLE);
			// Add the cache key
			parameters.put(Environment.CONTENT_CACHE_KEY, file.getFileCacheKey());
		}

		// File id
		parameters.put(FILE_UPLOAD_ID_KEY, fileId);

		// The targetable path needs to be configured for the portal environment.
		String url = env.getWServletPath();

		// Note the last parameter. In javascript we don't want to encode "&".
		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 * Retrieves a URL for the thumbnail of an uploaded file.
	 *
	 * @param fileId the file id
	 * @return the URL to access the thumbnail for an uploaded file.
	 */
	public String getFileThumbnailUrl(final String fileId) {
		FileWidgetUpload file = getFile(fileId);
		if (file == null) {
			return null;
		}

		// Check static resource
		Image thumbnail = file.getThumbnail();
		if (thumbnail instanceof InternalResource) {
			return ((InternalResource) thumbnail).getTargetUrl();
		}

		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(Environment.TARGET_ID, getTargetId());

		if (Util.empty(file.getThumbnailCacheKey())) {
			// Add some randomness to the URL to prevent caching
			String random = WebUtilities.generateRandom();
			parameters.put(Environment.UNIQUE_RANDOM_PARAM, random);
		} else {
			// Remove step counter as not required for cached content
			parameters.remove(Environment.STEP_VARIABLE);
			parameters.remove(Environment.SESSION_TOKEN_VARIABLE);
			// Add the cache key
			parameters.put(Environment.CONTENT_CACHE_KEY, file.getThumbnailCacheKey());
		}

		// File id
		parameters.put(FILE_UPLOAD_ID_KEY, fileId);

		// Thumbnail flag
		parameters.put(FILE_UPLOAD_THUMB_NAIL_KEY, "Y");

		// The targetable path needs to be configured for the portal environment.
		String url = env.getWServletPath();

		// Note the last parameter. In javascript we don't want to encode "&".
		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 * @param request the request to process
	 * @return the list of file selections
	 */
	private List<FileWidgetUpload> getNewSelection(final Request request) {
		// Get selected items (if any)
		String[] selectedParam = request.getParameterValues(getId() + ".selected");
		if (selectedParam == null) {
			selectedParam = new String[0];
		}
		List<String> selected = Arrays.asList(selectedParam);

		List<FileWidgetUpload> newSelection = new ArrayList<>();
		List<FileWidgetUpload> currentSelection = getValue();

		for (FileWidgetUpload file : currentSelection) {
			boolean select = selected.contains(file.getFileId());
			if (select) {
				newSelection.add(file);
			}
		}

		return newSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetId() {
		return getId();
	}

	/**
	 * Selection lists are considered equal if they have the same items (order is not important). An empty list is
	 * considered equal to a null list.
	 *
	 * @param list1 the first list to check.
	 * @param list2 the second list to check.
	 * @return true if the lists are equal, false otherwise.
	 */
	private boolean selectionsEqual(final List<?> list1, final List<?> list2) {
		return (list1 == null || list1.isEmpty()) && (list2 == null || list2.isEmpty())
				|| (list1 != null && list2 != null && list1.size() == list2.size() && list1.
				containsAll(list2));
	}

	// ----------------------------------------------------------------
	// Extrinsic state management
	// ----------------------------------------------------------------
	/**
	 * @return a new {@link MultiFileWidgetModel}.
	 */
	@Override
	protected MultiFileWidgetModel newComponentModel() {
		return new MultiFileWidgetModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MultiFileWidgetModel getComponentModel() {
		return (MultiFileWidgetModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MultiFileWidgetModel getOrCreateComponentModel() {
		return (MultiFileWidgetModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WMultiFileWidget.
	 *
	 * @author Christina Harris
	 */
	public static class MultiFileWidgetModel extends InputModel {

		/**
		 * The file types accepted by the file input.
		 */
		private Set<String> fileTypes;

		/**
		 * The maximum size of files uploaded by this component. Defaults to 10Mb.
		 */
		private long maxFileSize = 10 * 1000 * 1024;

		/**
		 * The maximum number of files which can be uploaded by this component.
		 */
		private int maxFiles;

		/**
		 * The component that will receive drag and dropped files on behalf of this widget.
		 */
		private DropZone dropzone;

		/**
		 * The image editor to associate with this instance.
		 */
		private WImageEditor editor;

		/**
		 * File ajax action.
		 */
		private Action fileAction;

		/**
		 * Cols to display uploaded files.
		 */
		private Integer cols;

		/**
		 * Flag if thumbnails should be generated for file links.
		 */
		private boolean useThumbnails;

		/**
		 * Thumnbnail size.
		 */
		private Dimension thumbnailSize;

		/**
		 * Thumbnail position.
		 */
		private ImagePosition thumbnailPosition;

		/**
		 * Uploaded file.
		 */
		private boolean newUpload;
	}

	/**
	 * File upload response. Used to send the response when a file has been uploaded successfully.
	 */
	public static class FileUploadXMLResponse implements ContentAccess {

		/**
		 * Default id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * XML response.
		 */
		private final String xml;

		/**
		 * @param xml the xml response
		 */
		public FileUploadXMLResponse(final String xml) {
			this.xml = xml;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] getBytes() {
			return xml.getBytes();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDescription() {
			return "fileui";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getMimeType() {
			return WebUtilities.CONTENT_TYPE_XML;
		}
	}

	/**
	 * Holds the uploaded file and extra details.
	 */
	public static class FileWidgetUpload implements Serializable {

		/**
		 * File identifier.
		 */
		private final String fileId;

		/**
		 * The file content.
		 */
		private final File file;

		/**
		 * Thumbnail image.
		 */
		private Image thumbnail;

		/**
		 * File cache key.
		 */
		private String fileCacheKey;

		/**
		 * Thumbnail cache key.
		 */
		private String thumbnailCacheKey;

		/**
		 * Creates a FileWidgetUpload.
		 *
		 * @param file the file to hold
		 */
		public FileWidgetUpload(final File file) {
			this(null, file);
		}

		/**
		 * Creates a FileWidgetUpload.
		 *
		 * @param id the file id
		 * @param file the file to hold
		 */
		public FileWidgetUpload(final String id, final File file) {
			if (id == null) {
				this.fileId = UUID.randomUUID().toString();
			} else {
				this.fileId = id;
			}
			this.file = file;
		}

		/**
		 * @return the file identifier
		 */
		public String getFileId() {
			return fileId;

		}

		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * @param thumbnail the files thumbnail
		 */
		public void setThumbnail(final Image thumbnail) {
			this.thumbnail = thumbnail;
		}

		/**
		 * @return the files thumbnail, or null
		 */
		public Image getThumbnail() {
			return thumbnail;
		}

		/**
		 * Retrieves the cache key for this file.
		 *
		 * @return the cacheKey
		 */
		public String getFileCacheKey() {
			return fileCacheKey;
		}

		/**
		 * A cache key is used to enable the caching of files on the client agent.
		 *
		 * @param cacheKey the cacheKey to set.
		 */
		public void setFileCacheKey(final String cacheKey) {
			this.fileCacheKey = cacheKey;
		}

		/**
		 * Retrieves the cache key for the thumbnail.
		 *
		 * @return the cacheKey
		 */
		public String getThumbnailCacheKey() {
			return thumbnailCacheKey;
		}

		/**
		 * A cache key is used to enable the caching of the thumbnail on the client agent.
		 *
		 * @param cacheKey the cacheKey to set.
		 */
		public void setThumbnailCacheKey(final String cacheKey) {
			this.thumbnailCacheKey = cacheKey;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object o) {
			return (o instanceof FileWidgetUpload) && Util.equals(fileId, ((FileWidgetUpload) o).
					getFileId());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return fileId.hashCode();
		}

	}

}
