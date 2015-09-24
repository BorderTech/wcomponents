package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * The WAjaxControl links an ajax trigger component with one or more ajax target components. Creating an ajax trigger
 * will result in an ajax request being made on the 'onChange' event of the trigger element. For an ajax request the
 * whole UI tree is serviced in the action phase but only the 'target' components related by this control will be
 * painted in the response. Repainting only small parts of the UI aims to improve client response times.
 * </p>
 * <p>
 * On the client-side, an AJAX target region must have a single html element as its parent and this element <b>must</b>
 * have an id. In the AJAX response the whole element is replaced by the response. Components which do not implement the
 * {@link AjaxTarget} interface can be wrapped in a {@link com.github.bordertech.wcomponents.WPanel}.
 * </p>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WAjaxControl extends AbstractWComponent {

	/**
	 * The component, that onChange, will trigger the ajax request.
	 */
	private final AjaxTrigger trigger;

	/**
	 * Constructor. Set the trigger for the ajax control.
	 *
	 * @param trigger The WComponent that will fire the ajax request on change.
	 */
	public WAjaxControl(final AjaxTrigger trigger) {
		this.trigger = trigger;
	}

	/**
	 * Constructor. Set the trigger and a target for the ajax control.
	 *
	 * @param trigger The WComponent that will fire the ajax request on change.
	 * @param target The WComponent to be re-painted as a result of the ajax request.
	 */
	public WAjaxControl(final AjaxTrigger trigger, final AjaxTarget target) {
		this(trigger);

		List<AjaxTarget> targets = new ArrayList<>();
		targets.add(target);
		getComponentModel().targets = targets;
	}

	/**
	 * Constructor. Set the trigger and multiple targets for the ajax control.
	 *
	 * @param trigger The WComponent that will fire the ajax request on change.
	 * @param targets The WComponents to be re-painted in the ajax response.
	 */
	public WAjaxControl(final AjaxTrigger trigger, final AjaxTarget[] targets) {
		this(trigger);

		List<AjaxTarget> targetList = new ArrayList<>();
		targetList.addAll(Arrays.asList(targets));
		getComponentModel().targets = targetList;
	}

	/**
	 * Constructor. Set the trigger and multiple targets for the ajax control.
	 *
	 * @param trigger The WComponent that will fire the ajax request on change.
	 * @param targets The WComponents to be re-painted in the ajax responses.
	 */
	public WAjaxControl(final AjaxTrigger trigger, final List<? extends AjaxTarget> targets) {
		this(trigger);
		getComponentModel().targets = new ArrayList<>(targets);
	}

	/**
	 * Get the {@link WComponent} that on change will trigger the cient to make the ajax request.
	 *
	 * @return The AJAX trigger.
	 */
	public AjaxTrigger getTrigger() {
		return this.trigger;
	}

	/**
	 * Add an ajax target for this control. When the ajax request is triggered only the target components will be
	 * re-painted.
	 *
	 * @param targets The UI components to be repainted in the ajax response.
	 */
	public void addTargets(final AjaxTarget[] targets) {
		if (targets != null) {
			addTargets(Arrays.asList(targets));
		}
	}

	/**
	 * Add target regions that should be repainted for this ajax request.
	 *
	 * @param targets The components that will be repainted for the ajax request.
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
	 * Add a WComponent that will be repainted after the ajax request has been handled.
	 *
	 * @param target A WComponent to be repainted.
	 */
	public void addTarget(final AjaxTarget target) {
		AjaxControlModel model = getOrCreateComponentModel();

		if (model.targets == null) {
			model.targets = new ArrayList<>();
		}

		model.targets.add(target);
	}

	/**
	 * Flag to indicate the the ajax trigger should be fired once only.
	 *
	 * @param loadOnce if <code>true</code> the target ajax trigger only once for a page.
	 */
	public void setLoadOnce(final boolean loadOnce) {
		if (loadOnce) {
			getOrCreateComponentModel().loadCount = 1;
		} else {
			getOrCreateComponentModel().loadCount = -1;
		}
	}

	/**
	 * @return <code>true</code> if the trigger should be fired once only for the page.
	 */
	public boolean isLoadOnce() {
		return getComponentModel().loadCount == 1;
	}

	/**
	 * Set how many times the trigger should fire on a page.
	 *
	 * @param loadCount The trigger count for this ajax control.
	 */
	public void setLoadCount(final int loadCount) {
		getOrCreateComponentModel().loadCount = loadCount;
	}

	/**
	 * @return how many times that trigger can fire on any given page.
	 */
	public int getLoadCount() {
		return getComponentModel().loadCount;
	}

	/**
	 * Get the target WComponents that will be repainted as a consequence of the AJAX request. An empty list is returned
	 * if no targets have been defined.
	 *
	 * @return The target regions that are repainted in the ajax response.
	 */
	public List<AjaxTarget> getTargets() {
		List<AjaxTarget> targets = getComponentModel().targets;

		if (targets == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(targets);
	}

	/**
	 * Returns the target WComponents as an array. If no targets have been registered then an empty array is returned.
	 *
	 * @return An array of ajax target components.
	 */
	public WComponent[] getTargetsArray() {
		List<AjaxTarget> targets = getTargets();
		return targets.toArray(new WComponent[targets.size()]);
	}

	/**
	 * @return the delay after page load before ajax control triggered.
	 */
	public int getDelay() {
		return getComponentModel().delay;
	}

	/**
	 * @param delay the delay after page load before ajax control triggered.
	 */
	public void setDelay(final int delay) {
		getOrCreateComponentModel().delay = delay;
	}

	/**
	 * Override preparePaintComponent in order to register the components for the current request.
	 *
	 * @param request the request being responded to.
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
	 * @return a String representation of this component, for debugging purposes.
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
	 * Creates a new Component model.
	 *
	 * @return a new AjaxControlModel.
	 */
	@Override // For type safety only
	protected AjaxControlModel newComponentModel() {
		return new AjaxControlModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected AjaxControlModel getComponentModel() {
		return (AjaxControlModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
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
		 * The components that will be repainted by the ajax request.
		 */
		private List<AjaxTarget> targets;

		/**
		 * Specifies how many times the ajax trigger should be fired.
		 */
		private int loadCount = -1;

		/**
		 * Delay after page load, in milliseconds.
		 */
		private int delay;
	}
}
