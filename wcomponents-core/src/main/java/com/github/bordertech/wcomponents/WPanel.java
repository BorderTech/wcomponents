package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.layout.LayoutManager;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A WPanel is used to group components together. It can optionally provide additional styling for the group, for
 * example to display a titled border. Layouts can be used to arrange components within the panel.
 * </p>
 * <p>
 * Users can add a {@link WButton} ({@link #setDefaultSubmitButton(WButton)}) to this component that will submit when
 * the user hits the Enter key and the cursor is in an input field inside the panel.
 * </p>
 *
 * @author Christina Harris
 * @since 1.0.0
 * @author Yiannis Paschalidis
 */
public class WPanel extends WContainer implements AjaxInternalTrigger, AjaxTarget, SubordinateTarget, Marginable,
		DropZone {

	/**
	 * An enumeration of available panel types.
	 */
	public enum Type {
		/**
		 * A plain panel.
		 */
		PLAIN,
		/**
		 * An action panel is similar to CHROME but with a different appearance. It is intended to be used once per
		 * screen to highlight the main area of activity.
		 */
		ACTION,
		/**
		 * A titled panel which is similar to CHROME but with a fancier border.
		 */
		BANNER,
		/**
		 * A 'block' type panel has padding around the edges.
		 */
		BLOCK,
		/**
		 * A box panel has a border.
		 */
		BOX,
		/**
		 * A panel with a title displayed in a border.
		 */
		CHROME,
		/**
		 * The feature panel is highlighted by a background colour and border.
		 */
		FEATURE,
		/**
		 * The footer panel is only intended for use at the very bottom of an application.
		 */
		FOOTER,
		/**
		 * The header panel is only intended for use at the very top of an application.
		 */
		HEADER
	};

	/**
	 * The available types of panel mode.
	 */
	public enum PanelMode {
		/**
		 * A lazy panel will load its content via AJAX when it is made visible.
		 */
		LAZY,
		/**
		 * An eager panel will load its content via AJAX immediately after the page is loaded.
		 */
		EAGER
	};

	/**
	 * Creates a {@link Type#PLAIN} WPanel.
	 */
	public WPanel() {
		this(Type.PLAIN);
	}

	/**
	 * Creates a WPanel of the specified type.
	 *
	 * @param type the type of panel to create.
	 */
	public WPanel(final Type type) {
		getComponentModel().type = type;
	}

	/**
	 * Sets the button that should be submitted when the user hits enter key and cursor is inside this panel.
	 *
	 * @param defaultSubmitButton the default submit button.
	 */
	public void setDefaultSubmitButton(final WButton defaultSubmitButton) {
		getOrCreateComponentModel().defaultSubmitButton = defaultSubmitButton;
	}

	/**
	 * @return The button that will be submitted if the user hits the enter key when the cursor is in this field.
	 */
	public WButton getDefaultSubmitButton() {
		return getComponentModel().defaultSubmitButton;
	}

	/**
	 * @return the panel type.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * Sets the panel type.
	 *
	 * @param type the default panel type.
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type;
	}

	/**
	 * @return this WPanel's mode of operation
	 */
	public PanelMode getMode() {
		return getComponentModel().mode;
	}

	/**
	 * Sets this WPanel's mode of operation.
	 *
	 * @param mode the mode of operation.
	 */
	public void setMode(final PanelMode mode) {
		getOrCreateComponentModel().mode = mode;
	}

	/**
	 * Set the accesskey (shortcut key) that will activate the panel.
	 *
	 * @param accessKey The key which activates this panel.
	 */
	public void setAccessKey(final char accessKey) {
		getOrCreateComponentModel().accessKey = accessKey;
	}

	/**
	 * @return the shortcut key that will focus the panel when used.
	 */
	public char getAccessKey() {
		return getComponentModel().accessKey;
	}

	/**
	 * Returns the accesskey character as a String. If the character is not a letter or digit then <code>null</code> is
	 * returned.
	 *
	 * @return The accesskey character as a String (may be <code>null</code>).
	 */
	public String getAccessKeyAsString() {
		char accessKey = getAccessKey();

		if (Character.isLetterOrDigit(accessKey)) {
			return String.valueOf(accessKey);
		}

		return null;
	}

	/**
	 * Sets the panel title.
	 *
	 * @param title the panel title to set, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setTitleText(final String title, final Serializable... args) {
		PanelModel model = getOrCreateComponentModel();
		model.title = I18nUtilities.asMessage(title, args);
	}

	/**
	 * @return the panel title.
	 */
	public String getTitleText() {
		return I18nUtilities.format(null, getComponentModel().title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final com.github.bordertech.wcomponents.Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.github.bordertech.wcomponents.Margin getMargin() {
		return getComponentModel().margin;
	}

	/**
	 * Sets the panel layout manager.
	 *
	 * @param layout the panel layout manager.
	 */
	public void setLayout(final LayoutManager layout) {
		getOrCreateComponentModel().layout = layout;
	}

	/**
	 * @return the panel layout manager.
	 */
	public LayoutManager getLayout() {
		return getComponentModel().layout;
	}

	/**
	 * <p>
	 * Adds the given component as a child of this component. The constraints are made available to the
	 * {@link LayoutManager} if it requires them.</p>
	 *
	 * @param component the component to add.
	 * @param constraints the layout constraints
	 */
	public void add(final WComponent component, final Serializable... constraints) {
		add(component);

		if (constraints != null && constraints.length > 0) {
			PanelModel model = getOrCreateComponentModel();

			if (model.layoutConstraints == null) {
				model.layoutConstraints = new HashMap<>();
			}

			model.layoutConstraints.put(component, constraints);
		}
	}

	/**
	 * <p>
	 * Adds the given component as a child of this component. The constraints are made available to the
	 * {@link LayoutManager} if it requires them.</p>
	 *
	 * @param component the component to add.
	 * @param constraints the layout constraints
	 */
	public void add(final WComponent component, final Serializable constraints) {
		add(component);

		PanelModel model = getOrCreateComponentModel();

		if (model.layoutConstraints == null) {
			model.layoutConstraints = new HashMap<>();
		}

		model.layoutConstraints.put(component, constraints);
	}

	/**
	 * Removes the given component from this component's list of children. This method has been overriden to remove any
	 * associated layout constraints.
	 *
	 * @param aChild the child component to remove
	 */
	@Override
	public void remove(final WComponent aChild) {
		super.remove(aChild);
		PanelModel model = getOrCreateComponentModel();

		if (model.layoutConstraints == null) {
			Map<WComponent, Serializable> defaultConstraints = ((PanelModel) getDefaultModel()).layoutConstraints;

			if (defaultConstraints != null) {
				model.layoutConstraints = new HashMap<>(defaultConstraints);
			}
		}

		if (model.layoutConstraints != null) {
			model.layoutConstraints.remove(aChild);

			// Deallocate constraints list if possible, to reduce session size.
			if (model.layoutConstraints.isEmpty()) {
				model.layoutConstraints = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		Type type = getType();
		return toString(type == null ? null : type.toString());
	}

	/**
	 * Retrieves the layout constraints for the given component, if they have been set.
	 *
	 * @param child the child component to retrieve the constraints for.
	 * @return the layout constraints for the given child, if set.
	 */
	public Serializable getLayoutConstraints(final WComponent child) {
		PanelModel model = getComponentModel();

		if (model.layoutConstraints != null) {
			return model.layoutConstraints.get(child);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected PanelModel getComponentModel() {
		return (PanelModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected PanelModel getOrCreateComponentModel() {
		return (PanelModel) super.getOrCreateComponentModel();
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new PanelModel.
	 */
	@Override
	protected PanelModel newComponentModel() {
		return new PanelModel();
	}

	/**
	 * A class used to hold the list of options for this component.
	 *
	 * @author Jonathan Austin
	 */
	public static class PanelModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The type of panel.
		 */
		private Type type;

		/**
		 * The panel title.
		 */
		private Serializable title;

		/**
		 * Indicates how the panel should operate.
		 */
		private PanelMode mode;

		/**
		 * The key shortcut that activates the panel.
		 */
		private char accessKey = '\0';

		/**
		 * The button to trigger when cursor inside div.
		 */
		private WButton defaultSubmitButton;

		/**
		 * The margins to be used on the panel.
		 */
		private com.github.bordertech.wcomponents.Margin margin;

		/**
		 * The panel's layout manager.
		 */
		private LayoutManager layout;

		/**
		 * A map of Layout constraints by child component.
		 */
		private Map<WComponent, Serializable> layoutConstraints;
	}

}
