package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
	 * The application icon url parameter.
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
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getTitle();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text);
	}

	/**
	 * Add a link to javaScript located at the specified URL to be used by the Application.
	 *
	 * @param url URL to a javaScript resource
	 * @return the application resource the URL is held in
	 */
	public ApplicationResource addJavaScriptUrl(final String url) {
		ApplicationResource res = new ApplicationResource(url);
		addJavaScript(res);
		return res;
	}

	/**
	 * Add a link to javaScript held as an internal resource to be used by the application.
	 *
	 * @param fileName the javaScript file name.
	 * @return the application resource the resource is held in
	 */
	public ApplicationResource addJavaScriptFile(final String fileName) {
		InternalResource resource = new InternalResource(fileName, fileName);
		ApplicationResource res = new ApplicationResource(resource);
		addJavaScript(res);
		return res;
	}

	/**
	 * Add a javaScript resource that is to be loaded and used by the application.
	 *
	 * @param resource the javaScript resource
	 */
	public void addJavaScript(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.jsResources == null) {
			model.jsResources = new ArrayList<>();
		}
		model.jsResources.add(resource);
	}

	/**
	 * Remove all custom javaScript resources.
	 */
	public void removeAllJavaScript() {
		getOrCreateComponentModel().jsResources = null;
	}

	/**
	 * Remove a custom javaScript resource.
	 *
	 * @param resource the javaScript resource to remove.
	 */
	public void removeJavaScript(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.jsResources != null) {
			model.jsResources.remove(resource);
		}
	}

	/**
	 * @return the list of javaScript application resources
	 */
	public List<ApplicationResource> getJavaScript() {
		WApplicationModel model = getComponentModel();
		if (model.jsResources == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(model.jsResources);
	}

	/**
	 * Add a link to CSS located at the specified URL to be used by the Application.
	 *
	 * @param url URL to a CSS resource
	 * @return the application resource the URL is held in
	 */
	public ApplicationResource addCssUrl(final String url) {
		ApplicationResource res = new ApplicationResource(url);
		addCss(res);
		return res;
	}

	/**
	 * Add a link to CSS held as an internal resource to be used by the Application.
	 *
	 * @param fileName the CSS file name.
	 * @return the application resource the resource is held in
	 */
	public ApplicationResource addCssFile(final String fileName) {
		InternalResource resource = new InternalResource(fileName, fileName);
		ApplicationResource res = new ApplicationResource(resource);
		addCss(res);
		return res;
	}

	/**
	 * Add a CSS resource that will be loaded and used by this application.
	 *
	 * @param resource the CSS resource
	 */
	public void addCss(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.cssResources == null) {
			model.cssResources = new ArrayList<>();
		}
		model.cssResources.add(resource);
	}

	/**
	 * Remove all custom CSS resources.
	 */
	public void removeAllCss() {
		getOrCreateComponentModel().cssResources = null;
	}

	/**
	 * Remove a custom CSS resource.
	 *
	 * @param resource the CSS resource to remove.
	 */
	public void removeCss(final ApplicationResource resource) {
		WApplicationModel model = getOrCreateComponentModel();
		if (model.cssResources != null) {
			model.cssResources.remove(resource);
		}
	}

	/**
	 * @return the list of CSS application resources
	 */
	public List<ApplicationResource> getCss() {
		WApplicationModel model = getComponentModel();
		if (model.cssResources == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(model.cssResources);
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

		private List<ApplicationResource> jsResources;

		private List<ApplicationResource> cssResources;
	}

	public static class ApplicationResource implements Serializable {

		private final String resourceId = UUID.randomUUID().toString();

		private final String url;

		private final InternalResource resource;

		public ApplicationResource(final String url) {
			this.url = url;
			this.resource = null;
		}

		public ApplicationResource(final InternalResource resource) {
			this.url = null;
			this.resource = resource;
		}

		public String getResourceId() {
			return resourceId;
		}

		public String getUrl() {
			return url;
		}

		public InternalResource getResource() {
			return resource;
		}

		public String getTargetUrl() {
			if (url != null) {
				return url;
			} else {
				return resource.getTargetUrl();
			}
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof ApplicationResource && resourceId.equals(((ApplicationResource) obj).getResourceId());
		}

		@Override
		public int hashCode() {
			return resourceId.hashCode();
		}

	}

}
