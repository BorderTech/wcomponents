package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * The WAjaxControl links an AJAX trigger component with one or more AJAX target components. Creating an AJAX trigger
 * will result in an AJAX request being made on the 'onChange' event of the trigger element. The implication oft his is
 * that an AJAX trigger component <em>should</em> have an Action attached (usually via setActionOnChanged).
 * </p>
 * <p>
 * During an AJAX request the  whole UI tree is serviced in the action phase but only the 'target' components related
 * by this control will be painted in the response.
 * </p>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WAjaxControl extends AbstractWComponent {

	/**
	 * The component that will trigger the AJAX request.
	 */
	private final AjaxTrigger trigger;

	/**
	 * Create a WAjaxControl with a specified trigger component.
	 *
	 * @param trigger the WComponent that will fire the AJAX request on change
	 */
	public WAjaxControl(final AjaxTrigger trigger) {
		this.trigger = trigger;
	}

	/**
	 * Create a WAjaxControl with specified trigger and target components.
	 *
	 * @param trigger the WComponent that will fire the AJAX request on change
	 * @param target the WComponent to be re-painted as a result of the AJAX request
	 */
	public WAjaxControl(final AjaxTrigger trigger, final AjaxTarget target) {
		this(trigger);

		List<AjaxTarget> targets = new ArrayList<>();
		targets.add(target);
		getComponentModel().targets = targets;
	}

	/**
	 * Create a WAjaxControl with specified trigger component and an array of target components.
	 *
	 * @param trigger the WComponent that will fire the AJAX request on change
	 * @param targets the WComponents to be re-painted in the AJAX response
	 */
	public WAjaxControl(final AjaxTrigger trigger, final AjaxTarget[] targets) {
		this(trigger);

		List<AjaxTarget> targetList = new ArrayList<>();
		targetList.addAll(Arrays.asList(targets));
		getComponentModel().targets = targetList;
	}

	/**
	 * Create a WAjaxControl with specified trigger component and a List of target components.
	 *
	 * @param trigger the WComponent that will fire the AJAX request on change
	 * @param targets the WComponents to be re-painted in the AJAX responses
	 */
	public WAjaxControl(final AjaxTrigger trigger, final List<? extends AjaxTarget> targets) {
		this(trigger);
		getComponentModel().targets = new ArrayList<>(targets);
	}

	/**
	 * Get the component that will trigger the client to make the AJAX request.
	 *
	 * @return the AJAX trigger
	 */
	public AjaxTrigger getTrigger() {
		return this.trigger;
	}

	/**
	 * Add an array of target components that should be targets for this AJAX request.
	 *
	 * @param targets the components to be repainted in the AJAX response
	 */
	public void addTargets(final AjaxTarget[] targets) {
		if (targets != null) {
			addTargets(Arrays.asList(targets));
		}
	}

	/**
	 * Add a list of target components that should be targets for this AJAX request.
	 *
	 * @param targets the components that will be repainted for the AJAX request
	 */
	public void addTargets(final List<? extends AjaxTarget> targets) {
		if (targets != null) {
			AjaxControlModel model = getOrCreateComponentModel();

			if (model.targets == null) {
				model.targets = new ArrayList<>();
			}

			model.targets.addAll(targets);
		}
	}

	/**
	 * Add a single target WComponent to this AJAX control.
	 *
	 * @param target a WComponent to be repainted
	 */
	public void addTarget(final AjaxTarget target) {
		AjaxControlModel model = getOrCreateComponentModel();

		if (model.targets == null) {
			model.targets = new ArrayList<>();
		}

		model.targets.add(target);
	}

	/**
	 * Flag to indicate the the AJAX trigger should be fired once only.
	 *
	 * @param loadOnce if <code>true</code> the target AJAX trigger only once for each load of a page
	 */
	public void setLoadOnce(final boolean loadOnce) {
		if (loadOnce) {
			getOrCreateComponentModel().loadCount = 1;
		} else {
			getOrCreateComponentModel().loadCount = -1;
		}
	}

	/**
	 * @return <code>true</code> if the trigger should be fired once only for each load of a page
	 */
	public boolean isLoadOnce() {
		return getComponentModel().loadCount == 1;
	}

	/**
	 * <p>
	 * Set how many times the trigger should fire on a page.
	 * </p>
	 * <p>
	 * <strong>Note</strong> a loadCount &gt; 0 will result in a WAjaxControl which can <strong>only fire once per page
	 * load</strong>. This method is under review (see https://github.com/BorderTech/wcomponents/issues/495).
	 * </p>
	 *
	 * @param loadCount The trigger count for this AJAX control.
	 */
	public void setLoadCount(final int loadCount) {
		getOrCreateComponentModel().loadCount = loadCount;
	}

	/**
	 * <p>
	 * Retrieve the number of times a trigger <em>may</em> be able to fire each time a page is loaded.
	 * </p>
	 * <p>
	 * <strong>Note</strong> a loadCount &gt; 0 will result in a WAjaxControl which can <strong>only fire once per page
	 * load</strong>. This method is under review (see https://github.com/BorderTech/wcomponents/issues/495).
	 * </p>
	 *
	 * @return how many times that trigger can fire on any given page.
	 */
	public int getLoadCount() {
		return getComponentModel().loadCount;
	}

	/**
	 * <p>
	 * Get the target WComponents that will be repainted as a consequence of the AJAX request.
	 * </p>
	 * <p>
	 * When the AJAX request is triggered only the target component(s) will be re-painted. An empty list is returned if
	 * no targets have been defined.
	 * </p>
	 *
	 * @return the target regions that are repainted in the AJAX response
	 */
	public List<AjaxTarget> getTargets() {
		List<AjaxTarget> targets = getComponentModel().targets;

		if (targets == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(targets);
	}

	/**
	 * <p>
	 * Get the target WComponents as an array.
	 * </p>
	 * <p>
	 * When the AJAX request is triggered only the target component(s) will be re-painted. If no targets have been
	 * registered then an empty array is returned.
	 * </p>
	 *
	 * @return an array of AJAX target components
	 */
	public WComponent[] getTargetsArray() {
		List<AjaxTarget> targets = getTargets();
		return targets.toArray(new WComponent[targets.size()]);
	}

	/**
	 * Get the delay period, in milliseconds, between the WAjaxControl being rendered in the view and it being
	 * <em>automatically</em> triggered. A WAjaxControl with a delay &gt; 0 will result in a request being made
	 * without a change to any {@link AjaxTrigger} component.
	 *
	 * @return the delay after page load before AJAX control triggered
	 */
	public int getDelay() {
		return getComponentModel().delay;
	}

	/**
	 * <p>
	 * Set a delay period, in milliseconds, between the WAjaxControl being rendered in the view and it being
	 * <em>automatically</em> triggered. A WAjaxControl with a delay &gt; 0 will result in a request being made
	 * without a change to any {@link AjaxTrigger} component.
	 * </p>
	 * <p>
	 * The use of a delay may be useful for setting up a trigger which polls for changes in a part of a UI. See
	 * {@link com.github.bordertech.wcomponents.WAjaxPollingRegion}.
	 * </p>
	 * <p>If the trigger is part of a polling region (therefore it is itself updated and potentially re-triggers itself
	 * possibly many times) then the delay <strong>must not</strong> be less than 334.</p>
	 *
	 * @param delay the delay after page load before AJAX control triggered
	 */
	public void setDelay(final int delay) {
		getOrCreateComponentModel().delay = delay;
	}

	/**
	 * Override preparePaintComponent in order to register the components for the current request.
	 *
	 * @param request the request being responded to
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		List<AjaxTarget> targets = getTargets();
		if (targets != null && !targets.isEmpty()) {
			WComponent triggerComponent = trigger == null ? this : trigger;

			UIContext triggerContext = WebUtilities.getPrimaryContext(UIContextHolder.getCurrent(),
					triggerComponent);
			UIContextHolder.pushContext(triggerContext);

			try {
				List<String> targetIds = new ArrayList<>();
				for (AjaxTarget target : getTargets()) {
					targetIds.add(target.getId());
				}
				AjaxHelper.registerComponents(targetIds, request, triggerComponent.getId());
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * @return a String representation of this component
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		List<AjaxTarget> targets = getTargets();

		buf.append("trigger=").append(trigger == null ? "null" : trigger.getClass().getSimpleName());
		buf.append(", targets=[");

		for (int i = 0; i < targets.size(); i++) {
			if (i > 0) {
				buf.append(", ");
			}

			AjaxTarget target = targets.get(i);
			buf.append(target == null ? "null" : target.getClass().getSimpleName());
		}

		buf.append(']');

		return toString(buf.toString());
	}

	/**
	 * Creates a new Component model. For type safety only.
	 *
	 * @return a new AjaxControlModel.
	 */
	@Override
	protected AjaxControlModel newComponentModel() {
		return new AjaxControlModel();
	}

	/**
	 * @return an AjaxControlModel; for type safety only
	 */
	@Override // For type safety only
	protected AjaxControlModel getComponentModel() {
		return (AjaxControlModel) super.getComponentModel();
	}

	/**
	 * @return an AjaxControlModel; for type safety only
	 */
	@Override // For type safety only
	protected AjaxControlModel getOrCreateComponentModel() {
		return (AjaxControlModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class AjaxControlModel extends ComponentModel {

		/**
		 * The components that will be repainted by the AJAX request.
		 */
		private List<AjaxTarget> targets;

		/**
		 * Specifies how many times the AJAX trigger should be fired. Zero or less represents no limit.
		 * Note up to and including WComponents 1.1.0 a loadCount &gt; 0 will result in a WAjaxControl which can
		 * <strong>only fire once per page load</strong>. This member is under review (see
		 * https://github.com/BorderTech/wcomponents/issues/495).
		 */
		private int loadCount = -1;

		/**
		 * Delay, in milliseconds, between the control loading in the view and an automatic request. If this is set, and
		 * set to a value greater than 0, the WAjaxControl will fire without <em>any</em> {@link AjaxTrigger} being changed/
		 * invoked. This can be used to set up a trigger which polls for changes.
		 */
		private int delay;
	}
}
