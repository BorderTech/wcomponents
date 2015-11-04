package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * <p>
 * WDialog is used to display pop-up content. It uses theme and skin features which keep the dialog associated with its
 * parent window. Using a {@link #MODAL} dialog eliminates much of the workflow complication involved when using the
 * {@link WWindow} component.
 * </p>
 * <p>
 * The content of the dialog is held in a {@link WNamingContext} with an id of "dlg" to make the ids of the content
 * unique and have the same id prefix.
 * </p>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WDialog extends AbstractWComponent implements Container, AjaxTarget {

	/**
	 * This is the "normal" state for the Dialog component, when the dialog is not visible.
	 */
	public static final int INACTIVE_STATE = 0;

	/**
	 * This state is when the dialog is open and the initial render of the content is complete.
	 */
	public static final int ACTIVE_STATE = 2;

	/**
	 * In this mode the dialog retains the input focus while open. The user cannot switch windows until the dialog box
	 * is closed.
	 */
	public static final int MODAL = 0;

	/**
	 * In this mode the dialog displays even when the user switches input focus to the window.
	 */
	public static final int MODELESS = 1;

	/**
	 * The button used to trigger the dialog.
	 */
	private final WButton trigger;

	/**
	 * The content holder exists to keep the content hidden from normal requests, yet still have the content attached to
	 * the wcomponent tree. Being part of the tree enables embedded targetables and other components to be found.
	 * <p>
	 * The holder is a naming context to make the ids in the dialog all unique and have the same prefix.
	 * </p>
	 */
	private final WNamingContext holder = new WNamingContext("dlg") {
		@Override
		public boolean isVisible() {
			// The content is only visible when the dialog is being displayed
			return getState() == ACTIVE_STATE;
		}

		@Override
		public String getNamingContextId() {
			// Build Id
			StringBuffer id = new StringBuffer();
			id.append(WDialog.this.getId());
			id.append(ID_CONTEXT_SEPERATOR);
			id.append(getIdName());
			return id.toString();
		}
	};

	/**
	 * Creates a WDialog.
	 */
	public WDialog() {
		this(null);
	}

	/**
	 * Creates a WDialog containing the given content.
	 *
	 * @param content the dialog content.
	 */
	public WDialog(final WComponent content) {
		this(content, null);
	}

	/**
	 * Creates a WDialog containing the given content and trigger. The dialog will be opened client-side, without a
	 * round-trip.
	 *
	 * @param content the dialog content.
	 * @param trigger the WButton used to trigger the dialog to display.
	 */
	public WDialog(final WComponent content, final WButton trigger) {
		add(holder);

		if (content != null) {
			setContent(content);
		}

		this.trigger = trigger;

		if (trigger != null) {
			add(trigger);
		}
	}

	/**
	 * Set the WComponent which will handle the content for this dialog.
	 *
	 * @param content the dialog content.
	 */
	public void setContent(final WComponent content) {
		getOrCreateComponentModel().content = content;

		// There should only be one content.
		holder.removeAll();
		holder.add(content);
	}

	/**
	 * @return the WComponent that handles the content for this dialog.
	 */
	public WComponent getContent() {
		return getComponentModel().content;
	}

	/**
	 * @return the WButton which is used to trigger the dialog.
	 */
	public WComponent getTrigger() {
		return trigger;
	}

	/**
	 * Signals that the dialog should be opened.
	 */
	public void display() {
		getOrCreateComponentModel().state = ACTIVE_STATE;
	}

	/**
	 * @return The height of the dialog. The default is 600 pixels.
	 */
	public int getHeight() {
		return getComponentModel().height;
	}

	/**
	 * Sets the dialog height. A value of &lt;=0 means "unspecified".
	 *
	 * @param height The height of the dialog, in pixels.
	 */
	public void setHeight(final int height) {
		getOrCreateComponentModel().height = height;
	}

	/**
	 * @return The width of the dialog. The default is 800 pixels.
	 */
	public int getWidth() {
		return getComponentModel().width;
	}

	/**
	 * Sets the dialog width. A value of &lt;=0 means "unspecified".
	 *
	 * @param width The width of the dialog, in pixels.
	 */
	public void setWidth(final int width) {
		getOrCreateComponentModel().width = width;
	}

	/**
	 * @return true if the dialog is resizable.
	 */
	public boolean isResizable() {
		return getComponentModel().resizable;
	}

	/**
	 * Sets whether the dialog is resizable.
	 *
	 * @param resizable true if the dialog should be resizable, false if not.
	 */
	public void setResizable(final boolean resizable) {
		getOrCreateComponentModel().resizable = resizable;
	}

	/**
	 * Set the relationship of the dialog to the parent window.
	 *
	 * @param mode the dialog mode; {@link #MODAL} or {@link #MODELESS}.
	 */
	public void setMode(final int mode) {
		getOrCreateComponentModel().mode = mode;
	}

	/**
	 * Returns the relationship of the dialog to the parent window.
	 *
	 * @return {@link #MODAL} or {@link #MODELESS}.
	 */
	public int getMode() {
		return getComponentModel().mode;
	}

	/**
	 * @return the dialog title.
	 */
	public String getTitle() {
		return I18nUtilities.format(null, getComponentModel().title);
	}

	/**
	 * Sets the dialog title.
	 *
	 * @param title the title to set, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setTitle(final String title, final Serializable... args) {
		getOrCreateComponentModel().title = I18nUtilities.asMessage(title, args);
	}

	// -------------------------------------------------------------
	// Action and Event Handling
	// -------------------------------------------------------------
	/**
	 * Override handleRequest in order to perform processing specific to this component.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		boolean ajaxTargeted = isAjaxTargeted();

		if (ajaxTargeted) {
			// This is necessary to support dialogs with a trigger,
			// where the display() method is not called explicitly
			getOrCreateComponentModel().state = ACTIVE_STATE;
		} else if (getComponentModel().state == ACTIVE_STATE && !ajaxTargeted) {
			getOrCreateComponentModel().state = INACTIVE_STATE;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (getContent() != null) {
			AjaxHelper.registerContainer(getId(), getId(), getContent().getId(), request);
		}
	}

	/**
	 * Indicates whether the dialog is currently the target of an AJAX operation.
	 *
	 * @return true if the dialog is currently AJAX targeted, otherwise false.
	 */
	protected final boolean isAjaxTargeted() {
		// If the AJAX target is within the dialog, it should be visible.
		AjaxOperation operation = AjaxHelper.getCurrentOperation();
		if (operation == null) {
			return false;
		}

		String dialogId = getId();
		String containerId = operation.getTargetContainerId();

		if (containerId != null && containerId.startsWith(dialogId)) {
			// target is the dialog, or somewhere within the dialog
			return true;
		}

		if (operation.getTargets() != null && UIContextHolder.getCurrent() != null) {
			for (String targetId : operation.getTargets()) {
				if (targetId.startsWith(dialogId)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retrieves the state of the dialog in the given context.
	 *
	 * @return the current state of the dialog.
	 */
	public int getState() {
		return getComponentModel().state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	@Override
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getTitle();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text, -1, -1) + childrenToString(getContent());
	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new DialogModel.
	 */
	@Override
	protected DialogModel newComponentModel() {
		return new DialogModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DialogModel getComponentModel() {
		return (DialogModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DialogModel getOrCreateComponentModel() {
		return (DialogModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the state information of a WDialog.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class DialogModel extends ComponentModel {

		/**
		 * The current state of the dialog.
		 */
		private int state = INACTIVE_STATE;

		/**
		 * The dialog title text.
		 */
		private Serializable title;

		/**
		 * The dialog width, in pixels.
		 */
		private int width = 0;

		/**
		 * The dialog height, in pixels.
		 */
		private int height = 0;

		/**
		 * Indicates whether the dialog should be resizable.
		 */
		private boolean resizable = true;

		/**
		 * The content to be displayed in the dialog.
		 */
		private WComponent content;

		/**
		 * The relationship between the parent window and this dialog window. Possible values are {@link #MODAL} and
		 * {@link #MODELESS}.
		 */
		private int mode = MODELESS;
	}
}
