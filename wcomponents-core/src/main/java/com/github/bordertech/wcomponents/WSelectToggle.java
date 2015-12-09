package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WDataTable.SelectMode;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is component can be used to select all/none within a containing component, for example a {@link WPanel} or
 * {@link WFieldSet} containing {@link WCheckBox}es, or a {@link WDataTable} with multiple row selection enabled.</p>
 *
 * <p>
 * Note that the target component must render out with an ID, so a plain {@link WComponent} or {@link WContainer} is not
 * suitable.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSelectToggle extends AbstractWComponent implements Disableable, AjaxTarget {

	/**
	 * Describes the tri-state nature of a selectToggle.
	 */
	public enum State {
		/**
		 * Indicates that all items are selected.
		 */
		ALL,
		/**
		 * Indicates that some items are selected.
		 */
		SOME,
		/**
		 * Indicates that no items are selected.
		 */
		NONE
	}

	/**
	 * Creates a client-side WSelectToggle.
	 */
	public WSelectToggle() {
		this(true);
	}

	/**
	 * Creates a WSelectToggle.
	 *
	 * @param clientSide if true, selection is handled client-side
	 */
	public WSelectToggle(final boolean clientSide) {
		getComponentModel().clientSide = clientSide;
	}

	/**
	 * Creates a WSelectToggle for the given target.
	 *
	 * @param clientSide if true, selection is handled client-side.
	 * @param target the target container.
	 */
	public WSelectToggle(final boolean clientSide, final WComponent target) {
		this(clientSide);
		getComponentModel().target = target;
	}

	/**
	 * Override handleRequest to handle selection toggling if server-side processing is being used.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (!isDisabled()) {
			final SelectToggleModel model = getComponentModel();
			String requestParam = request.getParameter(getId());

			final State newValue;
			if ("all".equals(requestParam)) {
				newValue = State.ALL;
			} else if ("none".equals(requestParam)) {
				newValue = State.NONE;
			} else if ("some".equals(requestParam)) {
				newValue = State.SOME;
			} else {
				newValue = model.state;
			}

			if (!newValue.equals(model.state)) {
				setState(newValue);
			}
			if (!model.clientSide && model.target != null && !State.SOME.equals(newValue)) {
				// We need to change the selections *after* all components
				// Have updated themselves from the request, as they may change
				// their values when their handleRequest methods are called.
				invokeLater(new Runnable() {
					@Override
					public void run() {
						setSelections(model.target, State.ALL.equals(newValue));
					}
				});
			}
		}
	}

	/**
	 * Sets the selections for the given context.
	 *
	 * @param component the container to modify the selected state for.
	 * @param selected if true, select everything. If false, deselect everything.
	 */
	private static void setSelections(final WComponent component, final boolean selected) {
		if (component instanceof WCheckBox) {
			((WCheckBox) component).setSelected(selected);
		} else if (component instanceof WCheckBoxSelect) {
			WCheckBoxSelect select = (WCheckBoxSelect) component;
			select.setSelected(selected ? select.getOptions() : new ArrayList(0));
		} else if (component instanceof WMultiSelect) {
			WMultiSelect list = (WMultiSelect) component;
			list.setSelected(selected ? list.getOptions() : new ArrayList(0));
		} else if (component instanceof WDataTable) {
			WDataTable table = (WDataTable) component;

			if (table.getSelectMode() == SelectMode.MULTIPLE) {
				if (selected) {
					TableDataModel model = table.getDataModel();
					int rowCount = model.getRowCount();

					List<Integer> indices = new ArrayList<>(rowCount);

					for (int i = 0; i < rowCount; i++) {
						if (model.isSelectable(i)) {
							indices.add(i);
						}
					}

					table.setSelectedRows(indices);
				} else {
					table.setSelectedRows(new ArrayList<Integer>(0));
				}
			}
		} else if (component instanceof Container) {
			Container container = (Container) component;
			final int childCount = container.getChildCount();

			for (int i = 0; i < childCount; i++) {
				WComponent child = container.getChildAt(i);
				setSelections(child, selected);
			}
		}
	}

	/**
	 * Indicates whether the toggle should occur client- or server-side.
	 *
	 * @return true if the toggle should occur client-side, false for server-side.
	 */
	public boolean isClientSide() {
		return getComponentModel().clientSide;
	}

	/**
	 * Sets whether the toggle should occur client- or server-side.
	 *
	 * @param clientSide true for client-side, false for server-side.
	 */
	public void setClientSide(final boolean clientSide) {
		getOrCreateComponentModel().clientSide = clientSide;
	}

	/**
	 * @return Returns the target.
	 */
	public WComponent getTarget() {
		return getComponentModel().target;
	}

	/**
	 * Sets the target.
	 *
	 * @param target The target to set.
	 */
	public void setTarget(final WComponent target) {
		getOrCreateComponentModel().target = target;
	}

	/**
	 * Indicates whether the control should render as text.
	 *
	 * @return true to render as text, false to render as a checkbox.
	 */
	public boolean isRenderAsText() {
		return getComponentModel().renderAsText;
	}

	/**
	 * Sets whether the control should render as text.
	 *
	 * @param renderAsText true to render as text, false to render as a checkbox.
	 */
	public void setRenderAsText(final boolean renderAsText) {
		getOrCreateComponentModel().renderAsText = renderAsText;
	}

	/**
	 * @return the current state.
	 */
	public State getState() {
		return getComponentModel().state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the state to set.
	 */
	public void setState(final State state) {
		getOrCreateComponentModel().state = state;
	}

	/**
	 * Indicates whether the select toggle is disabled.
	 *
	 * @return true if the toggle is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether the select toggle is disabled.
	 *
	 * @param disabled if true, the input is disabled. If false, it is enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * Holds the extrinsic state information of a WSelectToggle.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class SelectToggleModel extends ComponentModel {

		/**
		 * Indicates whether toggling should occur on the client or server.
		 */
		private boolean clientSide = true;
		/**
		 * The target component to toggle selections in.
		 */
		private WComponent target;
		/**
		 * Indicates whether the control should be rendered as text.
		 */
		private boolean renderAsText = false;

		private State state = State.NONE;
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new SelectToggleModel.
	 */
	@Override
	protected ComponentModel newComponentModel() {
		return new SelectToggleModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SelectToggleModel getComponentModel() {
		return (SelectToggleModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SelectToggleModel getOrCreateComponentModel() {
		return (SelectToggleModel) super.getOrCreateComponentModel();
	}
}
