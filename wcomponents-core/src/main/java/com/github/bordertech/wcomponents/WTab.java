package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WTabSet.TabMode;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * WTab encapsulates a tab on a tab set. The contents may or may not be rendered, depending on the tab mode. This class
 * is not intended to be instantiated outside {@link WTabSet}'s addTab methods.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTab extends AbstractNamingContextContainer implements Disableable, SubordinateTarget {

	/**
	 * The tab label.
	 */
	private final WDecoratedLabel label;

	/**
	 * Creates a new tab.
	 *
	 * @param content the tab content
	 * @param tabName the tab label
	 * @param mode the {@link TabMode | tab mode}.
	 */
	protected WTab(final WComponent content, final String tabName, final TabMode mode) {
		this(content, tabName, mode, (char) 0);
	}

	/**
	 * Creates a new tab.
	 *
	 * @param content the tab content
	 * @param tabName the tab label
	 * @param mode the {@link TabMode | tab mode}.
	 * @param accessKey the access key used to activate this tab
	 */
	protected WTab(final WComponent content, final String tabName, final TabMode mode,
			final char accessKey) {
		this(content, new WDecoratedLabel(tabName), mode, accessKey);
	}

	/**
	 * Creates a new tab.
	 *
	 * @param content the tab content
	 * @param label the tab label, which may contain rich content.
	 * @param mode the {@link TabMode | tab mode}.
	 */
	protected WTab(final WComponent content, final WDecoratedLabel label, final TabMode mode) {
		this(content, label, mode, (char) 0);
	}

	/**
	 * Creates a new tab.
	 *
	 * @param content the tab content
	 * @param label the tab label, which may contain rich content.
	 * @param mode the {@link TabMode | tab mode}.
	 * @param accessKey the access key used to activate this tab
	 */
	protected WTab(final WComponent content, final WDecoratedLabel label, final TabMode mode,
			final char accessKey) {
		if (label == null) {
			throw new IllegalArgumentException("A label must be specified");
		}

		this.label = label;

		add(label);
		add(content);

		setAccessKey(accessKey);
		setMode(mode);
	}

	/**
	 * Indicates whether this tab is open in the given context.
	 *
	 * @return true if this tab is open in the given context, otherwise false.
	 */
	public boolean isOpen() {
		WTabSet tabSet = WebUtilities.getAncestorOfClass(WTabSet.class, this);
		return tabSet.getActiveTabs().contains(this);
	}

	/**
	 * @return the tab content.
	 */
	public WComponent getContent() {
		return getChildCount() == 2 ? getChildAt(1) : null;
	}

	/**
	 * Sets the tab content.
	 *
	 * @param content the tab content.
	 */
	public void setContent(final WComponent content) {
		WComponent oldContent = getContent();

		if (oldContent != null) {
			remove(oldContent);
		}

		if (content != null) {
			add(content);
		}
	}

	/**
	 * @param mode the tab mode.
	 */
	public void setMode(final TabMode mode) {
		getOrCreateComponentModel().mode = mode;
	}

	/**
	 * @return the tab mode.
	 */
	public TabMode getMode() {
		return getComponentModel().mode;
	}

	/**
	 * Set the accesskey (shortcut key) that will activate the tab.
	 *
	 * @param accesskey The key (in combination with the Alt key) that activates this element.
	 */
	public void setAccessKey(final char accesskey) {
		getOrCreateComponentModel().accessKey = accesskey;
	}

	/**
	 * Te accesskey (shortcut key) that will activate the tab.
	 *
	 * @return The key that in combination with Alt will focus this input.
	 */
	public char getAccessKey() {
		return getComponentModel().accessKey;
	}

	/**
	 * Indicates whether the WTabSet is disabled in the given context.
	 *
	 * @return true if the input is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether the WTabSet is disabled.
	 *
	 * @param disabled true to disable this tab, false to enable.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final Request request) {
		// If is LAZY or DYNAMIC and is the current trigger, then process the WTabSet for the correct open/closed tabs
		if ((TabMode.LAZY.equals(getMode()) || TabMode.DYNAMIC.equals(getMode())) && AjaxHelper.
				isCurrentAjaxTrigger(this)) {
			WTabSet tabSet = (WTabSet) getParent();
			tabSet.handleRequest(request);
		}
	}

	/**
	 * Override preparePaintComponent in order to correct the visibility of the tab's content before it is rendered.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		WComponent content = getContent();

		if (content != null) {
			switch (getMode()) {
				case EAGER: {
					// Will always be visible
					content.setVisible(true);
					AjaxHelper.registerContainer(getId(), getId() + "-content", content.getId(),
							request);
					break;
				}
				case LAZY: {
					content.setVisible(isOpen());
					if (!isOpen()) {
						AjaxHelper.registerContainer(getId(), getId() + "-content", content.getId(),
								request);
					}

					break;
				}
				case DYNAMIC: {
					content.setVisible(isOpen());
					AjaxHelper.registerContainer(getId(), getId() + "-content", content.getId(),
							request);
					break;
				}
				case SERVER: {
					content.setVisible(isOpen());
					break;
				}
				case CLIENT: {
					// Will always be visible
					content.setVisible(true);
					break;
				}
			}
		}
	}

	/**
	 * @return the tab label.
	 */
	public WDecoratedLabel getTabLabel() {
		return label;
	}

	/**
	 * A convenience method to set the body text of the decorated label.
	 *
	 * @param text the text to set, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setText(final String text, final Serializable... args) {
		label.setText(text, args);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getTabLabel().getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, 1, 1);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new Component model.
	 *
	 * @return a new TabSetModel.
	 */
	@Override // For type safety only
	protected TabModel newComponentModel() {
		return new TabModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected TabModel getComponentModel() {
		return (TabModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected TabModel getOrCreateComponentModel() {
		return (TabModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WTab.
	 */
	public static class TabModel extends ComponentModel {

		/**
		 * The key shortcut that activates the tab.
		 */
		private char accessKey = '\0';

		/**
		 * The tab mode.
		 */
		private TabMode mode;
	}

}
