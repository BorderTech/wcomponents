package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The state information for a WebComponent tree (a UI) is stored in a UIContext. The reason for this is to minimize the
 * size of the footprint in the session. A UI can be shared between multiple sessions but there is only one UIContext
 * per session. This is a form of the flyweight pattern.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public interface UIContext extends Serializable {

	/**
	 * @return the top level web component for which is context applies.
	 */
	WComponent getUI();

	/**
	 * For use by internal framework code only. Sets the top level web component for this context.
	 *
	 * @param ui the top level web component for this context.
	 */
	void setUI(WComponent ui);

	/**
	 * Get the extrinsic state information for the given component.
	 *
	 * @param component the component to get the model for.
	 * @return the component's model.
	 */
	WebModel getModel(WebComponent component);

	/**
	 * Stores the extrinsic state information for the given component.
	 *
	 * @param component the component to set the model for.
	 * @param model the model to set.
	 */
	void setModel(WebComponent component, WebModel model);

	/**
	 * Removes the extrinsic state information for the given component. Note that this is not recursive for the
	 * children.
	 *
	 * @param component the component to remove the model for.
	 */
	void removeModel(WebComponent component);

	/**
	 * @return the set of WComponents that are storing a model.
	 */
	Set getComponents();

	/**
	 * Explicitly sets the environment.
	 *
	 * @param environment the environment to set.
	 */
	void setEnvironment(Environment environment);

	/**
	 * If we've got an environment, return it. Otherwise return a dummy environment.
	 *
	 * @return the current environment.
	 */
	Environment getEnvironment();

	/**
	 * @return true if the current environment is a 'dummy' environment.
	 */
	boolean isDummyEnvironment();

	/**
	 * Adds a runnable to the list of runnables to be invoked later. The runnable will be invoked against this
	 * UIContext.
	 *
	 * @param runnable the runnable to add
	 */
	void invokeLater(Runnable runnable);

	/**
	 * Adds a runnable to the list of runnables to be invoked later against the given UIContext.
	 *
	 * @param uic the context to run the runnable in.
	 * @param runnable the runnable to add
	 */
	void invokeLater(UIContext uic, Runnable runnable);

	/**
	 * Runs the runnables that were added using {@link #invokeLater(Runnable)}.
	 */
	void doInvokeLaters();

	/**
	 * Sets the component in this UIC which is to be the focus of the client browser cursor. The id of the component is
	 * used to find the focussed element in the rendered html.
	 *
	 * @param component the component that sould be the cursor focus in the rendered UI.
	 */
	void setFocussed(WComponent component);

	/**
	 * Sets the component in this UIC which is to be the focus of the client browser cursor. The id of the component is
	 * used to find the focussed element in the rendered html. Since id could be different in different contexts the
	 * context of the component is also needed.
	 *
	 * @param component - the component that sould be the cursor focus in the rendered UI.
	 * @param uic - the context that the component exists in.
	 */
	void setFocussed(WComponent component, UIContext uic);

	//=== Framework support ===
	/**
	 * @return the unique id of the focussed component for this UI.
	 */
	String getFocussedId();

	/**
	 * @return the focussed component for this UI.
	 */
	WComponent getFocussed();

	/**
	 * Sets whether a component needs to be given focus.
	 *
	 * @param focusRequired true if focus is required, false otherwise.
	 *
	 * @see #setFocussed(WComponent, UIContext)
	 */
	void setFocusRequired(boolean focusRequired);

	/**
	 * Indicates whether a component needs to be given focus.
	 *
	 * @return true if focus needs to be set.
	 *
	 * @see #setFocussed(WComponent, UIContext)
	 */
	boolean isFocusRequired();

	/**
	 * Reserved for internal framework use. Retrieves a framework attribute.
	 *
	 * @param name the attribute name.
	 * @return the framework attribute with the given name.
	 */
	Object getFwkAttribute(String name);

	/**
	 * Reserved for internal framework use. Sets a framework attribute.
	 *
	 * @param name the attribute name.
	 * @param value the attribute value.
	 */
	void setFwkAttribute(String name, Object value);

	/**
	 * Reserved for internal framework use. Removes a framework attribute.
	 *
	 * @param name the attribute name.
	 */
	void removeFwkAttribute(String name);

	/**
	 * Reserved for internal framework use.
	 *
	 * @return the names of all attributes bound to this context.
	 */
	Set getFwkAttributeNames();

	/**
	 * Reserved for internal framework use. Retrieves a scratch area with phse scope, where data can be temporarily
	 * stored. WComponents must not rely on data being available in the scratch area after each phase.
	 *
	 * @param component the component to retrieve the scratch map for.
	 * @return the scratch map with phase scope for the given component.
	 */
	Map getScratchMap(WComponent component);

	/**
	 * Reserved for internal framework use. Clears the scratch map with phase scope for the given component.
	 *
	 * @param component the component to clear the scratch map for.
	 */
	void clearScratchMap(WComponent component);

	/**
	 * Reserved for internal framework use. Clears the scratch map with phase scope.
	 */
	void clearScratchMap();

	/**
	 * Reserved for internal framework use. Get scratch map with request scope.
	 *
	 * @param component the component to retrieve the scratch map for.
	 * @return the scratch map with request scope
	 */
	Map<Object, Object> getRequestScratchMap(WComponent component);

	/**
	 * Reserved for internal framework use. Clears the scratch map with request scope.
	 *
	 * @param component the component to clear the scratch map for.
	 */
	void clearRequestScratchMap(WComponent component);

	/**
	 * Reserved for internal framework use. Clears the scratch map with request scope.
	 */
	void clearRequestScratchMap();

	/**
	 * Returns the creation time of this UIContext, which can be used to approximate the user session creation time.
	 *
	 * @return the creation time of this UIContext.
	 */
	long getCreationTime();

	/**
	 * @return the headers instance for this UIContext.
	 */
	Headers getHeaders();

	/**
	 * Retrieves the locale for this context.
	 *
	 * @return the Locale for this context, or null if no locale has been set.
	 */
	Locale getLocale();

	/**
	 * Sets the locale for this context.
	 *
	 * @param locale the Locale to set, or null for the default locale.
	 */
	void setLocale(Locale locale);
}
