package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.TreeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * UIContextImpl - implementation of {@link UIContext}.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class UIContextImpl implements UIContext {

	/**
	 * The environment to use when an environment hasn't been supplied.
	 */
	private static final DummyEnvironment DUMMY = new DummyEnvironment();

	/**
	 * The root component for this context.
	 */
	private WComponent ui;

	/**
	 * The UIContext's creation time.
	 */
	private final long creationTime = System.currentTimeMillis();

	/**
	 * A map of component models, keyed by the component that they belong to.
	 */
	private final Map<WebComponent, WebModel> map = new HashMap<>();

	/**
	 * A map of temporary maps with phase scope, keyed by the components using them.
	 */
	private transient Map<WComponent, Map<Object, Object>> scratchMaps;

	/**
	 * A map of temporary maps with request scope, keyed by the components using them.
	 */
	private transient Map<WComponent, Map<Object, Object>> requestScratchMap;

	/**
	 * The framework attribute map.
	 */
	private Map<String, Object> attribMap;

	/**
	 * The environment which this context is running in.
	 */
	private Environment environment;

	/**
	 * A list of runnables to invoke later (near the end of processing the current request).
	 */
	private transient List<UIContextImplRunnable> invokeLaterRunnables;

	/**
	 * The component which needs to be given focus.
	 */
	private transient WComponent focussed;

	/**
	 * The locale for this context.
	 */
	private Locale locale;

	/**
	 * The context of the component which needs to be given focus.
	 *
	 * This is necessary to cater for repeaters, which used the same component, but have a different context per row.
	 */
	private transient UIContext focussedUIC;

	/**
	 * Indicates whether keyboard focus needs to be set to a particular component.
	 */
	private transient boolean focusRequired = false;

	private transient Headers headers;

	/**
	 * For use by internal framework code only. Sets the top level web component for this context.
	 *
	 * @param topUi the top level web component for this context.
	 */
	@Override
	public void setUI(final WComponent topUi) {
		this.ui = topUi;
	}

	/**
	 * @return the top level web component for which is context applies.
	 */
	@Override
	public WComponent getUI() {
		return ui;
	}

	/**
	 * Get the extrinsic state information for the given component.
	 *
	 * @param component the component to get the model for.
	 * @return the component's model.
	 */
	@Override
	public WebModel getModel(final WebComponent component) {
		return map.get(component);
	}

	/**
	 * Stores the extrinsic state information for the given component.
	 *
	 * @param component the component to set the model for.
	 * @param model the model to set.
	 */
	@Override
	public void setModel(final WebComponent component, final WebModel model) {
		map.put(component, model);
	}

	/**
	 * Removes the extrinsic state information for the given component. Note that this is not recursive for the
	 * children.
	 *
	 * @param component the component to remove the model for.
	 */
	@Override
	public void removeModel(final WebComponent component) {
		map.remove(component);
	}

	/**
	 * @return the set of WComponents that are storing a model.
	 */
	@Override
	public Set<WebComponent> getComponents() {
		return map.keySet();
	}

	/**
	 * Explicitly set the environment.
	 *
	 * @param environment the environment to set.
	 */
	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	/**
	 * If an environment has been set, it is returned. Otherwise a dummy environment is returned.
	 *
	 * @return the current environment.
	 */
	@Override
	public Environment getEnvironment() {
		if (environment == null) {
			return DUMMY;
		}

		return environment;
	}

	/**
	 * @return true if the current environment is a 'dummy' environment.
	 */
	@Override
	public boolean isDummyEnvironment() {
		return environment == null;
	}

	/**
	 * Adds a runnable to the list of runnables to be invoked later.
	 *
	 * @param runnable the runnable to add
	 */
	@Override
	public void invokeLater(final Runnable runnable) {
		invokeLater(this, runnable);
	}

	/**
	 * Adds a runnable to the list of runnables to be invoked later.
	 *
	 * @param uic the UIContext to invoke the runnable in.
	 * @param runnable the runnable to add
	 */
	@Override
	public void invokeLater(final UIContext uic, final Runnable runnable) {
		if (invokeLaterRunnables == null) {
			invokeLaterRunnables = new ArrayList<>();
		}

		invokeLaterRunnables.add(new UIContextImplRunnable(uic, runnable));
	}

	/**
	 * Runs the runnables that were added using {@link #invokeLater(Runnable)}.
	 */
	@Override
	public void doInvokeLaters() {
		if (invokeLaterRunnables == null) {
			return;
		}

		// The Runnables we are about to run may add their own invoke later
		// runnables, so
		// loop to make sure we process them all.
		while (!invokeLaterRunnables.isEmpty()) {
			List<UIContextImplRunnable> runnables = new ArrayList<>();
			runnables.addAll(invokeLaterRunnables);
			invokeLaterRunnables.clear();

			for (UIContextImplRunnable run : runnables) {
				UIContextHolder.pushContext(run.getContext());

				try {
					run.getRunnable().run();
				} finally {
					UIContextHolder.popContext();
				}
			}
		}
	}

	/**
	 * @return the focussed component for this UI.
	 */
	@Override
	public WComponent getFocussed() {
		return focussed;
	}

	/**
	 * Sets the component in this UIC which is to be the focus of the client browser cursor. The id of the component is
	 * used to find the focussed element in the rendered html.
	 *
	 * @param component the component that sould be the cursor focus in the rendered UI.
	 */
	@Override
	public void setFocussed(final WComponent component) {
		focussed = component;
	}

	/**
	 * Sets the component in this UIC which is to be the focus of the client browser cursor. The id of the component is
	 * used to find the focussed element in the rendered html. Since id could be different in different contexts the
	 * context of the component is also needed.
	 *
	 * @param component - the component that sould be the cursor focus in the rendered UI.
	 * @param uic - the context that the component exists in.
	 */
	@Override
	public void setFocussed(final WComponent component, final UIContext uic) {
		this.focussed = component;
		this.focussedUIC = uic;
	}

	/**
	 * @return the unique id of the focused component for this UI.
	 */
	@Override
	public String getFocussedId() {
		// No focused
		if (focussed == null) {
			return null;
		}

		// Check there is an active context and a UI set
		UIContext cuic = UIContextHolder.getCurrent();
		if (cuic == null || cuic.getUI() == null) {
			return null;
		}

		UIContext fuic = (focussedUIC == null) ? this : focussedUIC;
		boolean differentFocusContext = fuic != cuic;

		if (differentFocusContext) {
			UIContextHolder.pushContext(fuic);
		}

		String id;

		try {
			if (focussed.isHidden()) {
				return null;
			} else if (focussed instanceof Input && ((Input) focussed).isReadOnly()) {
				return null;
			} else if (focussed instanceof Disableable && ((Disableable) focussed).isDisabled()) {
				return null;
			}

			id = focussed.getId();
		} finally {
			if (differentFocusContext) {
				UIContextHolder.popContext();
			}
		}

		// Check id is focusable
		WComponent root = cuic.getUI();
		if (TreeUtil.isIdFocusable(root, id)) {
			return id;
		}

		return null;
	}

	/**
	 * Sets whether a component needs to be given focus.
	 *
	 * @param focusRequired true if focus is required, false otherwise.
	 *
	 * @see #setFocussed(WComponent, UIContext)
	 */
	@Override
	public void setFocusRequired(final boolean focusRequired) {
		this.focusRequired = focusRequired;
	}

	/**
	 * Indicates whether a component needs to be given focus.
	 *
	 * @return true if focus needs to be set.
	 *
	 * @see #setFocussed(WComponent, UIContext)
	 */
	@Override
	public boolean isFocusRequired() {
		return focusRequired;
	}

	/**
	 * Reserved for internal framework use. Retrieves a framework attribute.
	 *
	 * @param name the attribute name.
	 * @return the framework attribute with the given name.
	 */
	@Override
	public Object getFwkAttribute(final String name) {
		if (attribMap == null) {
			return null;
		}
		return attribMap.get(name);
	}

	/**
	 * Reserved for internal framework use. Sets a framework attribute.
	 *
	 * @param name the attribute name.
	 * @param value the attribute value.
	 */
	@Override
	public void setFwkAttribute(final String name, final Object value) {
		if (attribMap == null) {
			attribMap = new HashMap<>();
		}

		attribMap.put(name, value);
	}

	/**
	 * Reserved for internal framework use. Removes a framework attribute.
	 *
	 * @param name the attribute name.
	 */
	@Override
	public void removeFwkAttribute(final String name) {
		if (attribMap != null) {
			attribMap.remove(name);
		}
	}

	/**
	 * Reserved for internal framework use.
	 *
	 * @return the names of all attributes bound to this context, or null if there are no attributes.
	 */
	@Override
	public Set<String> getFwkAttributeNames() {
		if (attribMap != null) {
			return attribMap.keySet();
		}

		return null;
	}

	/**
	 * Reserved for internal framework use. Retrieves a scratch area, where data can be temporarily stored. WComponents
	 * must not rely on data being available in the scratch area after each phase.
	 *
	 * @param component the component to retrieve the scratch map for.
	 * @return the scratch map for the given component.
	 */
	@Override
	public Map<Object, Object> getScratchMap(final WComponent component) {
		if (scratchMaps == null) {
			scratchMaps = new HashMap<>();
		}

		Map<Object, Object> componentScratchMap = scratchMaps.get(component);

		if (componentScratchMap == null) {
			componentScratchMap = new HashMap<>(2);
			scratchMaps.put(component, componentScratchMap);
		}

		return componentScratchMap;
	}

	/**
	 * Reserved for internal framework use. Clears the scratch map for the given component.
	 *
	 * @param component the component to clear the scratch map for.
	 */
	@Override
	public void clearScratchMap(final WComponent component) {
		if (scratchMaps != null) {
			scratchMaps.remove(component);
		}
	}

	/**
	 * Reserved for internal framework use. Clears the scratch map.
	 */
	@Override
	public void clearScratchMap() {
		if (scratchMaps != null) {
			scratchMaps.clear();
			scratchMaps = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Object, Object> getRequestScratchMap(final WComponent component) {
		if (requestScratchMap == null) {
			requestScratchMap = new HashMap<>();
		}

		Map<Object, Object> componentScratchMap = requestScratchMap.get(component);

		if (componentScratchMap == null) {
			componentScratchMap = new HashMap<>(2);
			requestScratchMap.put(component, componentScratchMap);
		}

		return componentScratchMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearRequestScratchMap(final WComponent component) {
		if (requestScratchMap != null) {
			requestScratchMap.remove(component);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearRequestScratchMap() {
		if (requestScratchMap != null) {
			requestScratchMap.clear();
			requestScratchMap = null;
		}
	}

	/**
	 * @return the creation time of this UIContext.
	 */
	@Override
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * @return the WHeaders instance for this context.
	 */
	@Override
	public Headers getHeaders() {
		if (headers == null) {
			headers = new HeadersImpl();
		}

		return headers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * The DummyEnvironment is used when an environment hasn't been explicitly supplied.
	 *
	 * @author Martin Shevchenko
	 */
	private static final class DummyEnvironment extends AbstractEnvironment {

		/**
		 * Creates a DummyEnvironment.
		 */
		private DummyEnvironment() {
			setPostPath("unknown");
			setAppHostPath("unknown");
			setBaseUrl("unknown");
			setHostFreeBaseUrl("unknown");
			setUserAgentInfo(new UserAgentInfo());
		}
	}

	/**
	 * Duplet for tracking runnable tasks. Does not use Duplet as Runnable is not Serializable.
	 *
	 * @author Joshua Barclay
	 */
	private static final class UIContextImplRunnable {

		/**
		 * The UIContext.
		 */
		private final UIContext uicontext;

		/**
		 * The Runnable.
		 */
		private final Runnable runnable;

		/**
		 * Construct a new UIContextRunnable.
		 *
		 * @param uicontext the context.
		 * @param runnable the runnable.
		 */
		private UIContextImplRunnable(final UIContext uicontext, final Runnable runnable) {
			this.uicontext = uicontext;
			this.runnable = runnable;
		}

		/**
		 *
		 * @return the UIContext.
		 */
		public UIContext getContext() {
			return uicontext;
		}

		/**
		 *
		 * @return the runnable.
		 */
		public Runnable getRunnable() {
			return runnable;
		}

	}
}
