package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This component must be used as the top level component for an application. It provides application-wide state
 * information, such as unsaved changes.</p>
 *
 * Applications can not be nested, but multiple applications can be present on a single screen, usually in a Portlet
 * environment.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WApplication extends AbstractMutableContainer implements AjaxTarget, DropZone {

	/**
	 * The application icon url.
	 */
	private static final String ICON_URL = Config.getInstance().getString(
			"bordertech.wcomponents.application.icon.url");

	/**
	 * Indicates whether the application has unsaved changes.
	 *
	 * @return true if there are unsaved changes, otherwise false.
	 */
	public boolean hasUnsavedChanges() {
		return getComponentModel().unsavedChanges;
	}

	/**
	 * <p>
	 * Sets the unsavedChanges flag.</p>
	 *
	 * <p>
	 * The unsavedChanges flag is used by the Themes to display a warning message if the user tries to navigate away and
	 * the flag is set to true.</p>
	 *
	 * @param unsavedChanges true if there are unsavedChanges
	 */
	public void setUnsavedChanges(final boolean unsavedChanges) {
		getOrCreateComponentModel().unsavedChanges = unsavedChanges;
	}

	/**
	 * Returns the application's title.
	 *
	 * @return the title.
	 */
	public String getTitle() {
		return I18nUtilities.format(null, getComponentModel().title);
	}

	/**
	 * Sets the application title.
	 *
	 * @param title The title to set.
	 */
	public void setTitle(final String title) {
		getOrCreateComponentModel().title = title;
	}

	/**
	 * @return true if append application ID to IDs, otherwise false.
	 */
	public boolean isAppendID() {
		return getComponentModel().appendID;
	}

	/**
	 * @param appendID set rue if append application ID to IDs
	 */
	public void setAppendID(final boolean appendID) {
		getOrCreateComponentModel().appendID = appendID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdName() {
		String name = super.getIdName();
		return name == null ? DEFAULT_APPLICATION_ID : name;
	}

	/**
	 * @return true as always a naming context
	 */
	@Override
	public boolean isNamingContext() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamingContextId() {
		boolean append = isAppendID();

		if (!append) {
			// Check if this is the top level name context
			NamingContextable top = WebUtilities.getParentNamingContext(this);
			if (top != null) {
				// Not top context, so always append
				append = true;
			}
		}

		if (append) {
			return getId();
		} else {
			return "";
		}
	}

	/**
	 * <p>
	 * This method is called when a wrong step error has occurred, for example as a result of the user using the
	 * browser's navigation controls..</p>
	 *
	 * <p>
	 * The method can be overridden to allow projects to handle the step error in a manner appropriate to their
	 * Application.</p>
	 */
	public void handleStepError() {
		// No Action
	}

	/**
	 * Returns the closest WApplication instance (ancestor component) from the given base component.
	 *
	 * @param base the component from which we start scanning up the tree for a WApplication instance.
	 * @return the closest WApplication instance from the given base component.
	 */
	public static WApplication instance(final WComponent base) {
		WApplication appl = WebUtilities.getClosestOfClass(WApplication.class, base);
		return appl;
	}

	/**
	 * Add custom javaScript located at the specified URL to be used by the Application.
	 *
	 * @param url URL to a javaScript resource
	 * @return the application resource the URL details are held in
	 */
	public ApplicationResource addJsUrl(final String url) {
		if (Util.empty(url)) {
			throw new IllegalArgumentException("A URL must be provided.");
		}
		ApplicationResource res = new ApplicationResource(url);
		addJsResource(res);
		return res;
	}

	/**
	 * Add custom javaScript held as an internal resource to be used by the application.
	 *
	 * @param fileName the javaScript file name.
	 * @return the application resource the resource details are held in
	 */
	public ApplicationResource addJsFile(final String fileName) {
		if (Util.empty(fileName)) {
			throw new IllegalArgumentException("A file name must be provided.");
		}
		InternalResource resource = new InternalResource(fileName, fileName);
		ApplicationResource res = new ApplicationResource(resource);
		addJsResource(res);
		return res;
	}

	/**
	 * Add a custom javaScript resource that is to be loaded and used by the application.
	 *
	 * @param resource the javaScript resource
	 */
	public void addJsResource(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.jsResources == null) {
			model.jsResources = new ArrayList<>();
		} else if (model.jsResources.contains(resource)) {
			return;
		}
		model.jsResources.add(resource);
	}

	/**
	 * Remove all custom javaScript resources.
	 */
	public void removeAllJsResources() {
		getOrCreateComponentModel().jsResources = null;
	}

	/**
	 * Remove a custom javaScript resource.
	 *
	 * @param resource the javaScript resource to remove.
	 */
	public void removeJsResource(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.jsResources != null) {
			model.jsResources.remove(resource);
		}
	}

	/**
	 * @return the set of custom javaScript application resources
	 */
	public List<ApplicationResource> getJsResources() {
		WApplicationModel model = getComponentModel();
		if (model.jsResources == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(model.jsResources);
	}

	/**
	 * Add custom CSS located at the specified URL to be used by the Application.
	 *
	 * @param url URL to a CSS resource
	 * @return the application resource the URL details are held in
	 */
	public ApplicationResource addCssUrl(final String url) {
		if (Util.empty(url)) {
			throw new IllegalArgumentException("A URL must be provided.");
		}
		ApplicationResource res = new ApplicationResource(url);
		addCssResource(res);
		return res;
	}

	/**
	 * Add custom CSS held as an internal resource to be used by the Application.
	 *
	 * @param fileName the CSS file name.
	 * @return the application resource the resource details are held in
	 */
	public ApplicationResource addCssFile(final String fileName) {
		if (Util.empty(fileName)) {
			throw new IllegalArgumentException("A file name must be provided.");
		}
		InternalResource resource = new InternalResource(fileName, fileName);
		ApplicationResource res = new ApplicationResource(resource);
		addCssResource(res);
		return res;
	}

	/**
	 * Add a custom CSS resource that will be loaded and used by this application.
	 *
	 * @param resource the CSS resource
	 */
	public void addCssResource(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.cssResources == null) {
			model.cssResources = new ArrayList<>();
		} else if (model.cssResources.contains(resource)) {
			return;
		}
		model.cssResources.add(resource);
	}

	/**
	 * Remove all custom CSS resources.
	 */
	public void removeAllCssResources() {
		getOrCreateComponentModel().cssResources = null;
	}

	/**
	 * Remove a custom CSS resource.
	 *
	 * @param resource the CSS resource to remove.
	 */
	public void removeCssResource(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.cssResources != null) {
			model.cssResources.remove(resource);
		}
	}

	/**
	 * @return the list of custom CSS resources
	 */
	public List<ApplicationResource> getCssResources() {
		WApplicationModel model = getComponentModel();
		if (model.cssResources == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(model.cssResources);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getTitle();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text);
	}

	/**
	 * @return the application icon URL or null if not set
	 */
	public static String getIcon() {
		return ICON_URL;
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new model appropriate for this component.
	 *
	 * @return a new {@link WApplicationModel}.
	 */
	@Override
	protected WApplicationModel newComponentModel() {
		return new WApplicationModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected WApplicationModel getComponentModel() {
		return (WApplicationModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected WApplicationModel getOrCreateComponentModel() {
		return (WApplicationModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WApplication.
	 */
	public static class WApplicationModel extends ComponentModel {

		/**
		 * Indicates whether the application has unsaved changes.
		 */
		private boolean unsavedChanges;

		/**
		 * The application title.
		 */
		private String title;

		/**
		 * Flag to append the Application ID to the children IDs.
		 */
		private boolean appendID;

		/**
		 * List of custom javaScript resources.
		 */
		private ArrayList<ApplicationResource> jsResources;

		/**
		 * List of custom CSS resources.
		 */
		private ArrayList<ApplicationResource> cssResources;
	}

	/**
	 * Holds the details of CSS and JS custom resources.
	 */
	public static class ApplicationResource implements Serializable {

		/**
		 * Unique resource id.
		 */
		private final String resourceId;

		/**
		 * Resource URL.
		 */
		private final String url;

		/**
		 * Internal resource.
		 */
		private final InternalResource resource;

		/**
		 * @param url URL to a resource
		 */
		public ApplicationResource(final String url) {
			if (Util.empty(url)) {
				throw new IllegalArgumentException("A URL must be provided.");
			}
			this.resourceId = "url:" + url;
			this.url = url;
			this.resource = null;
		}

		/**
		 * @param resource internal resource
		 */
		public ApplicationResource(final InternalResource resource) {
			if (resource == null) {
				throw new IllegalArgumentException("A resource must be provided.");
			}
			this.resourceId = "file:" + resource.getResourceName();
			this.url = null;
			this.resource = resource;
		}

		/**
		 * @return the resource id
		 */
		public String getResourceId() {
			return resourceId;
		}

		/**
		 * @return the resource URL
		 */
		public String getUrl() {
			return url;
		}

		/**
		 *
		 * @return the internal resource
		 */
		public InternalResource getResource() {
			return resource;
		}

		/**
		 * @return the target URL of the resource
		 */
		public String getTargetUrl() {
			return url == null ? resource.getTargetUrl() : url;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object obj) {
			return obj instanceof ApplicationResource && resourceId.equals(((ApplicationResource) obj).getResourceId());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return resourceId.hashCode();
		}

	}

}
