package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.awt.Dimension;
import java.util.Map;

/**
 * <p>
 * The WImage component provides a way for applications to display images within their application. It may either serve
 * up a pre-defined image which is part of the application, or generate the image dynamically.
 * </p>
 *
 * <pre>
 * // Example of using a pre-defined image included in the applications class-path
 * new WImage(&quot;/com/mycompany/myapp/somePackage/logo.png&quot;, &quot;Application logo&quot;);
 * </pre>
 *
 * <pre>
 * // Example of using a dynamic image
 * WImage image = new WImage();
 * image.setImage(new com.github.bordertech.wcomponents.Image()
 * {
 *     public Dimension getSize()
 *     {
 *         // Both width and height are unknown
 *         return new Dimension(-1, -1);
 *     }
 *
 *     public String getString()
 *     {
 *         return &quot;Include a relevant description of the image here&quot;;
 *     }
 *
 *     public String getMimeType()
 *     {
 *         // e.g. if serving up a PNG image
 *         return &quot;image/png&quot;;
 *     }
 *
 *     public byte[] getBytes()
 *     {
 *         // read in or create the image binary data here.
 *     }
 * });
 * </pre>
 *
 * @author Kishan Bisht
 * @since 1.0.0
 */
public class WImage extends WBeanComponent implements Targetable, AjaxTarget {

	/**
	 * Creates a WImage with no content.
	 */
	public WImage() {
	}

	/**
	 * <p>
	 * Creates a WImage with the given static content. This is provided as a convenience method for when the image is
	 * included as static content in the class path rather than in the web application's resources.
	 * </p>
	 * <p>
	 * The mime type for the image is looked up from the "mimeType.*" mapping configuration parameters using the
	 * resource's file extension.
	 * </p>
	 *
	 * @param imageResource the resource path to the image file.
	 * @param description the image description.
	 */
	public WImage(final String imageResource, final String description) {
		this(new ImageResource(imageResource, description));
	}

	/**
	 * Creates a WImage with the given image resource.
	 *
	 * @param image the image resource
	 */
	public WImage(final ImageResource image) {
		setImage(image);
	}

	/**
	 * Creates a dynamic URL that the image can be loaded from. In fact the URL points to the main application servlet,
	 * but includes a non-null for the parameter associated with this WComponent (ie, its label). The handleRequest
	 * method below detects this when the browser requests the image
	 *
	 * @return the url to load the image from
	 */
	public String getTargetUrl() {
		if (getImageUrl() != null) {
			return getImageUrl();
		}

		Image image = getImage();

		if (image instanceof InternalResource) {
			return ((InternalResource) image).getTargetUrl();
		}

		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(Environment.TARGET_ID, getTargetId());

		if (Util.empty(getCacheKey())) {
			// Add some randomness to the URL to prevent caching
			String random = WebUtilities.generateRandom();
			parameters.put(Environment.UNIQUE_RANDOM_PARAM, random);
		} else {
			// Remove step counter as not required for cached content
			parameters.remove(Environment.STEP_VARIABLE);
			parameters.remove(Environment.SESSION_TOKEN_VARIABLE);
			// Add the cache key
			parameters.put(Environment.CONTENT_CACHE_KEY, getCacheKey());
		}

		// this variable needs to be set in the portlet environment.
		String url = env.getWServletPath();

		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 * When an img element is included in the html output of a page, the browser will make a second request to get the
	 * image contents. The handleRequest method has been overridden to detect whether the request is the "image content
	 * fetch" request by looking for the parameter that we encode in the image url.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);

		String targ = request.getParameter(Environment.TARGET_ID);
		boolean contentReqested = (targ != null && targ.equals(getTargetId()));

		if (contentReqested) {
			ContentEscape escape = new ContentEscape(getImage());
			escape.setCacheable(!Util.empty(getCacheKey()));
			throw escape;
		}
	}

	/**
	 * Sets the image.
	 *
	 * @param image the image to set.
	 */
	public void setImage(final Image image) {
		ImageModel model = getOrCreateComponentModel();
		model.image = image;
		model.imageUrl = null;
	}

	/**
	 * Sets the image to an external URL.
	 *
	 * @param imageUrl the image URL.
	 */
	public void setImageUrl(final String imageUrl) {
		ImageModel model = getOrCreateComponentModel();
		model.imageUrl = imageUrl;
		model.image = null;
	}

	/**
	 * @return the image to an external URL.
	 */
	public String getImageUrl() {
		return getComponentModel().imageUrl;
	}

	/**
	 * Retrieves the current image.
	 *
	 * @return the current image.
	 */
	public Image getImage() {
		return getComponentModel().image;
	}

	/**
	 * Retrieves the cache key for this image. This is used to enable caching of the image on the client agent.
	 *
	 * @return the cacheKey
	 */
	public String getCacheKey() {
		return getComponentModel().cacheKey;
	}

	/**
	 * A cache key is used to enable the caching of images on the client agent.
	 * <p>
	 * The cache key should be unique for each image.
	 * </p>
	 *
	 * @param cacheKey the cacheKey to set.
	 */
	public void setCacheKey(final String cacheKey) {
		getOrCreateComponentModel().cacheKey = cacheKey;
	}

	/**
	 * Retrieve the image size.
	 * <p>
	 * Returns the size set via {@link #setSize(Dimension)}. If this has not been set and an image resource is
	 * provideing the image then the size of the image resource is returned. Otherwise return null.
	 * </p>
	 *
	 * @return the size of the image.
	 */
	public Dimension getSize() {
		Dimension size = getComponentModel().size;
		if (size != null) {
			return size;
		}
		Image image = getImage();
		if (image != null) {
			return image.getSize();
		}
		return null;
	}

	/**
	 * @param size the size of the image.
	 */
	public void setSize(final Dimension size) {
		getOrCreateComponentModel().size = size;
	}

	/**
	 * Retrieve the alternative text for the image.
	 * <p>
	 * Returns the alternative text set via {@link #setAlternativeText(String)}. If this has not been set and an image
	 * resource is providing the image then the description of the image resource is returned. Otherwise return null.
	 * </p>
	 *
	 * @return the alternative text for the image.
	 */
	public String getAlternativeText() {
		String text = getComponentModel().alternativeText;
		if (text != null) {
			return text;
		}
		Image image = getImage();
		if (image != null) {
			return image.getDescription();
		}
		return null;
	}

	/**
	 * @param text the alternative text for the image
	 */
	public void setAlternativeText(final String text) {
		getOrCreateComponentModel().alternativeText = text;
	}

	/**
	 * Returns the id to use to target this component.
	 *
	 * @return this component's target id.
	 */
	@Override
	public String getTargetId() {
		return getId();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		Image image = getImage();
		String text = image == null ? null : image.getDescription();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, 1, 1);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new ImageModel.
	 */
	@Override
	protected ImageModel newComponentModel() {
		return new ImageModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ImageModel getComponentModel() {
		return (ImageModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ImageModel getOrCreateComponentModel() {
		return (ImageModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WImage.
	 */
	public static class ImageModel extends BeanAndProviderBoundComponentModel {

		private Image image;
		private String cacheKey;
		private String imageUrl;
		private String alternativeText;
		private Dimension size;
	}
}
