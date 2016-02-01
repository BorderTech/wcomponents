package com.github.bordertech.wcomponents;

import java.awt.Dimension;

/**
 * <p>
 * EXPERIMENTAL API WARNING - this API is under development and is likely to change.
 *
 * The WImageEditor component provides a means for allowing the user to edit images and submit the result.
 * The edited image may be pre-existing and provided as a WImage or newly acquired (upload or camera).
 * </p>
 *
 * @author Rick Brown
 * @since 1.0.3
 */
public class WImageEditor extends AbstractWComponent {

	/**
	 * Creates an imageEditor..
	 *
	 */
	public WImageEditor() {

	}

	/**
	 * @return the overlay image URL.
	 */
	public String getOverlayUrl() {
		return getComponentModel().overlayUrl;
	}

	/**
	 * Sets the overlay image URL.
	 * An overlay image is rendered on top of the image being edited (but does not form part of the saved image).
	 *
	 * @param overlayUrl the overlay image URL.
	 */
	public void setOverlayUrl(final String overlayUrl) {
		ImageEditModel model = getOrCreateComponentModel();
		model.overlayUrl = overlayUrl;
	}

	/**
	 * @return true if the image editor should allow camera image acquisition.
	 */
	public boolean getUseCamera() {
		return getComponentModel().useCamera;
	}

	/**
	 * Set to true if you wish to allow the user to capture an image from an attached camera.
	 * This feature is completely dependent on browser support and will essentially be ignored if the browser does not
	 * provide the necessary APIs.
	 *
	 * @param useCamera the overlay image URL.
	 */
	public void setUseCamera(final boolean useCamera) {
		ImageEditModel model = getOrCreateComponentModel();
		model.useCamera = useCamera;
	}

	/**
	 * Retrieve the editor size.
	 * <p>
	 * Returns the size set via {@link #setSize(Dimension)}.
	 * </p>
	 *
	 * @return the size of the image editor.
	 */
	public Dimension getSize() {
		Dimension size = getComponentModel().size;
		return size;
	}

	/**
	 * @param size the size of the image editor.
	 */
	public void setSize(final Dimension size) {
		getOrCreateComponentModel().size = size;
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new ImageEditModel.
	 */
	@Override
	protected ImageEditModel newComponentModel() {
		return new ImageEditModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ImageEditModel getComponentModel() {
		return (ImageEditModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ImageEditModel getOrCreateComponentModel() {
		return (ImageEditModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WImageEditor.
	 */
	public static class ImageEditModel extends ComponentModel {
		private String overlayUrl;
		private Dimension size;
		private boolean useCamera;
	}
}
