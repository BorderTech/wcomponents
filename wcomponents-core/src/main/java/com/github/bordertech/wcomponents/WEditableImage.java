package com.github.bordertech.wcomponents;

/**
 * <p>
 * The WEditableImage component provides a way for the user to edit and upload modifications to an image.
 * Uploads are sent to the server by means of the associated file upload widget.
 *
 * @author Rick Brown
 * @since 1.0.3
 */
public class WEditableImage extends WImage {

	/**
	 * Creates a WEditableImage associated with the file widget used to upload edited results.
	 *
	 * The filename of the uploaded image will be the ID of the WImage.
	 *
	 * @param editUploader The file upload widget that will receive edited images.
	 */
	public WEditableImage(final WMultiFileWidget editUploader) {
		getOrCreateComponentModel().editor = editUploader;
	}

	/**
	 * Return the WMultiFileWidget associated with this editable image.
	 *
	 * @return The file upload widget.
	 */
	public WMultiFileWidget getEditUploader() {
		return getComponentModel().editor;
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new ImageModel.
	 */
	@Override
	protected EditableImageModel newComponentModel() {
		return new EditableImageModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected EditableImageModel getComponentModel() {
		return (EditableImageModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected EditableImageModel getOrCreateComponentModel() {
		return (EditableImageModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WEditableImage.
	 */
	public static class EditableImageModel extends ImageModel {
		private WMultiFileWidget editor;
	}
}
