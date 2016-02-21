package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * The WComponent interface.
 * <p>
 * In the WComponent library, only the {@link AbstractWComponent} implements this interface directly. All other
 * components extend from the abstract class.
 * </p>
 * <p>
 * There are a few other interfaces which extend this one. They are used to provide additional functionality (e.g.
 * {@link Container} which can contain other components), or used as a marker to denote capabilities (e.g.
 * {@link AjaxTarget} can be updated with AJAX).
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 */
public interface WComponent extends WebComponent {

	/**
	 * The default id/name separator character.
	 */
	String ID_CONTEXT_SEPERATOR = "-";

	/**
	 * The reserved character to prefix framework assigned ids.
	 */
	String ID_FRAMEWORK_ASSIGNED_SEPERATOR = "_";

	/**
	 * The default application context id.
	 */
	String DEFAULT_APPLICATION_ID = "A";

	/**
	 * The default id name for components with no parent and no id name set.
	 */
	String DEFAULT_NO_ID = "_X";

	/**
	 * The default internal id name for components with no parent.
	 */
	String DEFAULT_INTERNAL_ID = "_Z";

	/**
	 * User assigned IDs must start with a letter and followed by letters, digits or underscores.
	 */
	String ID_VALIDATION_PATTERN = "[a-zA-Z][0-9a-zA-Z_]*";

	/**
	 * @return the internal identifier of this Component based on its position in the component tree.
	 */
	String getInternalId();

	/**
	 * @return the component identifier of this Component (if any).
	 */
	String getIdName();

	/**
	 * Set the component identifier of this Component (if any). Component identifiers must obey the following syntax
	 * restrictions:
	 * <ul>
	 * <li>Must not be a zero-length String.</li>
	 * <li>First character must be a letter.</li>
	 * <li>Underscore ('_') as the first character is reserved by the framework.</li>
	 * <li>Subsequent characters must be a letter, a digit or an underscore ('_').</li>
	 * </ul>
	 * <p>
	 * The specified identifier must be unique among all the components that are descendents of the nearest ancestor
	 * Component that is an active {@link NamingContextable}.
	 * </p>
	 *
	 * @param idName the id name
	 */
	void setIdName(final String idName);

	/**
	 * Retrieves the name for this WComponent.
	 *
	 * @return the name for this WComponent in the current context.
	 * @deprecated No longer used. Use {@link #getId()} instead.
	 */
	@Override
	@Deprecated
	String getName();

	/**
	 * Return an identifier for this component, generating one if necessary.
	 * <p>
	 * The implementation must follow these steps in determining the Id:
	 * </p>
	 * If has id name set:
	 * <ul>
	 * <li>Find the closest ancestor to this component in the tree hierarchy that is an active
	 * {@link NamingContextable}.</li>
	 * <li>If no context found, then let the ID be the idname. </li>
	 * <li>If a context found, then let the ID be {@link NamingContextable#getNamingContextId()} +
	 * {@link WComponent#ID_CONTEXT_SEPERATOR} + id name.</li>
	 * </ul>
	 * If no id name set:
	 * <ul>
	 * <li>If the component's parent is null, then let the ID be {@link WComponent#DEFAULT_NO_ID}.</li>
	 * <li>If has a parent, then let the ID prefix be parent.getId() or parent.getNamingContextID() +
	 * {@link WComponent#ID_CONTEXT_SEPERATOR} if the parent is an active NamingContext. Then let the ID be prefix +
	 * generate unique id.</li>
	 * </ul>
	 *
	 * @return the id for this WComponent in the current context.
	 */
	@Override
	String getId();

	/**
	 * <p>
	 * This is the main entry point during request handling. Only the "top-level" component will have this method called
	 * - other components will have {@link #handleRequest(Request)} called. For efficiency, only {@link #isVisible()
	 * visible} components are asked to handle the request.
	 * </p>
	 * The basic workflow is:
	 * <ol>
	 * <li>Collate the list of visible components, in depth-first order. Depth-first traversal is used to ensure that
	 * when a parent component's handleRequest method is called, all of its children have already handled the request
	 * and are in a stable state.</li>
	 * <li>Call handle request for each visible component found, in order.</li>
	 * <li>At this point, all the components should be in a stable state, and any runnables added using
	 * {@link #invokeLater(Runnable)} will be invoked. These runnables can include e.g. {@link Action Actions} on
	 * buttons.</li>
	 * </ol>
	 * <p>
	 * Applications should not call this method directly.
	 * </p>
	 *
	 * @param request the request being responded to.
	 */
	@Override
	void serviceRequest(final Request request);

	/**
	 * <p>
	 * Adds a runnable that will be processed after the completion of the current serviceRequest method. This method is
	 * intended to be called from subcomponents' handleRequest methods, to permit processing to continue once the entire
	 * WComponent tree has been updated from the incoming HTTP request.
	 * </p>
	 * <p>
	 * If this method is invoked more than once, each Runnable will be invoked in turn.
	 * </p>
	 *
	 * @param runnable the Runnable to execute after the serviceRequest method has otherwise completed.
	 */
	void invokeLater(final Runnable runnable);

	/**
	 * Subclasses should override this method in order to provide specific request handling logic. For example, a text
	 * field may set its value to the value of a request parameter.
	 *
	 * @param request the request being responded to.
	 */
	void handleRequest(final Request request);

	/**
	 * Applications can call this method during event handling to indicate that we should forward to a given url. The
	 * event handling will complete and the forwarding will take place before painting.
	 *
	 * @param url the URL to forward to
	 */
	void forward(final String url);

	/**
	 * Prepares this component and all child components for immediate painting (e.g. rendering to XML). Note that the
	 * parent's preparePaint method is called before the childrens'.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	void preparePaint(final Request request);

	/**
	 * Paints the component by rendering it in the given context.
	 *
	 * @param renderContext the context to render to.
	 */
	@Override
	void paint(final RenderContext renderContext);

	/**
	 * <p>
	 * The validate method should be called by an {@link Action}, or {@link ValidatingAction} at points in the
	 * application lifecycle where it makes sense to perform validation.
	 * </p>
	 * <p>
	 * No side effect of displaying error markers implied by this. This is a pure "function" except that it stores the
	 * results in the <code>diags</code> list.
	 * </p>
	 * <p>
	 * It is the responsibility of the validatable component to call any of its children that may also require
	 * validation.
	 * </p>
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	void validate(final List<Diagnostic> diags);

	/**
	 * <p>
	 * This does not affect the diag list at all. The ValidatableComponent should visually mark any fields or blocks
	 * that have errors in the given diag list.
	 * </p>
	 * <p>
	 * It is the responsibility of the validatable component to call any of its children that may also be validatable.
	 * </p>
	 *
	 * @param diags the list of current validation diagnostics.
	 */
	void showErrorIndicators(final List<Diagnostic> diags);

	/**
	 * <p>
	 * This does not affect the diag list at all. The ValidatableComponent should visually mark any fields or blocks
	 * that have warnings in the given diag list.
	 * </p>
	 * <p>
	 * It is the responsibility of the validatable component to call any of its children that may also be validatable.
	 * The default implemntation does nothing.
	 * </p>
	 *
	 * @param diags the list of diagnostics for this component.
	 */
	void showWarningIndicators(final List<Diagnostic> diags);

	/**
	 * <p>
	 * The shared attributes of a component (and all its children) can be locked, preventing users/developers from
	 * making further updates. However, attribute values can still be updated on a per session basis.
	 * </p>
	 * <p>
	 * This method should normally never be called from application code.
	 * </p>
	 *
	 * @param lock true to lock the component, false to unlock
	 */
	void setLocked(final boolean lock);

	/**
	 * Indicates whether this component is locked. If the component is locked, shared attribute values can not be
	 * updated.
	 *
	 * @return true if the component is locked, false if not.
	 */
	boolean isLocked();

	/**
	 * Optionally use this flag to test if some arbitrary initialisation has been performed by this component.
	 *
	 * @return true if the component has been marked as initialised, false otherwise.
	 * @see #setInitialised(boolean)
	 */
	boolean isInitialised();

	/**
	 * Optionally use this flag to store whether some arbitrary initialisation has been performed by this component on
	 * the given session. This should normally only be used after the UI has been constructed.
	 *
	 * @param flag the initialised flag.
	 */
	void setInitialised(final boolean flag);

	/**
	 * Indicates whether this component should take part in validation processing.
	 *
	 * @return true if this component is validateable, false if not.
	 */
	boolean isValidate();

	/**
	 * Sets whether this component should take part in validation processing.
	 *
	 * @param flag true if this component should be validated, false if not.
	 */
	void setValidate(final boolean flag);

	/**
	 * Indicates whether this component is visible. Invisible components are normally excluded from all event handling
	 * and painting.
	 *
	 * @return true if this component is visible, false if invisible.
	 */
	boolean isVisible();

	/**
	 * Sets the visibility of this component. Invisible components are normally excluded from all event handling and
	 * painting.
	 *
	 * @param visible true to set this component visible, false for invisible.
	 */
	void setVisible(final boolean visible);

	/**
	 * Indicates whether this component is hidden. Hidden components take part in event handling and painting, but are
	 * not visible on the client.
	 *
	 * @return true if this component is hidden, false if displayed.
	 */
	boolean isHidden();

	/**
	 * Indicates whether this component has a tab index.
	 *
	 * @return false - For the moment, turn off the tab index feature and see what happens.
	 */
	boolean hasTabIndex();

	/**
	 * @return the tab index for this component.
	 */
	int getTabIndex();

	/**
	 * @return the label associated with this component, or null if there is no label.
	 */
	WLabel getLabel();

	/**
	 * Requests that this component be given keyboard focus when rendered.
	 */
	void setFocussed();

	/**
	 * Resets this component and its children to their initial state.
	 */
	void reset();

	/**
	 * This method removes unnecessary component models from the user session for this component and all its
	 * descendants. A component model is deemed unnecessary when it has the same state as the component's default
	 * component model.
	 */
	void tidyUpUIContextForTree();

	/**
	 * <p>
	 * WComponents must implement this method in order to protect any session based information they store from being
	 * incorrectly removed by the {@link #tidyUpUIContextForTree()} method.
	 * </p>
	 * <p>
	 * It is possible you can write a more exact check for the default state here than the method on the component model
	 * itself can provide.
	 * </p>
	 *
	 * @return true if the component is in it's default state, otherwise false.
	 */
	boolean isDefaultState();

	/**
	 * @return the current parent of this component.
	 */
	Container getParent();

	/**
	 * Retrieves the tag used to identify this component in a Velocity template.
	 *
	 * @return the component's tag.
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	String getTag();

	/**
	 * Sets the tag used to identify this component in a Velocity template.
	 *
	 * @param tag the component's tag to set.
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	void setTag(final String tag);

	/**
	 * Retrieves the environment for the current session. If there is no environment for the session, a dummy
	 * environment is returned.
	 *
	 * @return the environment for the user session.
	 */
	Environment getEnvironment();

	/**
	 * Sets the environment.
	 *
	 * @param environment the environment to set.
	 */
	void setEnvironment(final Environment environment);

	/**
	 * Retrieves the headers.
	 *
	 * @return the headers.
	 */
	Headers getHeaders();

	/**
	 * Exposes the base URL for this environment. Renderers can call this method to construct URIs to sub-resources.
	 *
	 * The baseurl is ultimately derived from com.github.bordertech.wcomponents.Environment
	 *
	 * @return the base URL
	 */
	String getBaseUrl();

	/**
	 * Associates an arbitrary attribute with this component.
	 *
	 * @param key the attribute key.
	 * @param value the attribute value.
	 */
	void setAttribute(final String key, final Serializable value);

	/**
	 * Retrieves an arbitrary attribute which has been previously associated with this component.
	 *
	 * @param key the attribute key.
	 * @return value the attribute value if set, otherwise null.
	 */
	Serializable getAttribute(final String key);

	/**
	 * Removes an arbitrary attribute.
	 *
	 * @param key the attribute key.
	 * @return the value for the attribute which was removed, or null if no attribute was found with the given key.
	 */
	Serializable removeAttribute(final String key);

	/**
	 * Sets the component's tool tip. Note that not all components support displaying tool tips.
	 *
	 * @param text the tool tip text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	void setToolTip(final String text, Serializable... args);

	/**
	 * Retrieves the component's tool tip.
	 *
	 * @return the component's tool tip.
	 */
	String getToolTip();

	/**
	 * Sets the text used by screen readers to describe the component.
	 *
	 * @param text the screen reader text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 * @deprecated use setToolTip
	 */
	void setAccessibleText(final String text, Serializable... args);

	/**
	 * Retrieves the text used by screen readers to describe the component.
	 *
	 * @return the screen reader text.
	 * @deprecated use getToolTip
	 */
	String getAccessibleText();

	/**
	 * Sets the flag if tracking is enabled for this component. This flag is used by {@link #isTracking()}.
	 *
	 * @param track set true if tracking is enabled for this component.
	 */
	void setTrackingEnabled(final boolean track);

	/**
	 * Returns true if tracking is enabled for this component.This flag is used by {@link #isTracking()}.
	 *
	 * @return true if tracking is enabled for this component.
	 */
	boolean isTrackingEnabled();

	/**
	 * Returns true if this component should be tracked.
	 * <p>
	 * A component will only be tracked if {@link #setTrackingEnabled(boolean)} is set true and the component has an id
	 * set via {@link #setIdName(String)}. If the id has not been set, then the id used for tracking will be dynamic and
	 * constantly changing which makes it useless for analysing.
	 * </p>
	 *
	 * @return true if this component should be tracked.
	 */
	boolean isTracking();

	/**
	 * Sets additional HTML class name string for this component. Multiple HTML class names may be added to an instance
	 * of a component using a space separated string. Some values in the HTML class name attribute are determined in the
	 * theme and are used for core functionality and styling.
	 *
	 * @param className the HTML class attribute's value to add to the component.
	 * @param args optional arguments for the message format string.
	 */
	void setHtmlClass(final String className, Serializable... args);

	/**
	 * Returns the HTML class name string to apply to a component. Some values in the HTML class name attribute are
	 * determined in the theme and are used for core functionality and styling. This method will only return class name
	 * values which are added in the application, it has no knowledge of theme's class names.
	 *
	 * @return the value to add to the HTML class attribute of the output component.
	 */
	String getHtmlClass();

}
