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
	 * @return true if the image editor includes face detection.
	 */
	public boolean getIsFace() {
		return getComponentModel().isFace;
	}

	/**
	 * Set to true to turn on face detection.
	 *
	 * @param isFace turn face detection on or off.
	 */
	public void setIsFace(final boolean isFace) {
		ImageEditModel model = getOrCreateComponentModel();
		model.isFace = isFace;
	}

	/**
	 * Determine if the image editor will render inline.
	 * @return true if it will render inline, otherwise false (will render in popup)
	 */
	public boolean getRenderInline() {
		return getComponentModel().renderInline;
	}

	/**
	 * If true then the image editor will render where it is added in the tree instead of in a popup.
	 * @param renderInline Set to true to render inline.
	 */
	public void setRenderInline(final boolean renderInline) {
		ImageEditModel model = getOrCreateComponentModel();
		model.renderInline = renderInline;
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
		private boolean isFace;
		private boolean renderInline;
	}
}
