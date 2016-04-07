package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A WButton is used to submit the contents of the form to the server. An {@link Action} can be associated with the
 * button to execute application-specific code when the button is pressed.
 * </p>
 * <pre>
 * // Create a button with the text &quot;Submit&quot;.
 * WButton button = new WButton(&quot;Submit&quot;);
 *
 * // Set an action to run when the button is clicked.
 * button.setAction(new Action() {
 *   public void execute(final ActionEvent event) {
 *     System.out.println(&quot;The button was clicked at &quot; + new Date());
 *   }
 * });
 * </pre>
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class WButton extends WBeanComponent implements Container, Disableable, AjaxTrigger,
		AjaxTarget,
		SubordinateTarget {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WButton.class);

	/**
	 * The default value to use when no value has been explicitly specified.
	 */
	protected static final String NO_VALUE = "x";

	/**
	 * A holder for a button image, if set.
	 */
	private final ButtonImage buttonImage = new ButtonImage(this);

	/**
	 * This is used to control the position of the image on the button relative to the text label. If a button has an
	 * image and ImagePosition is not set then the text label will not be visible on the button.
	 */
	public enum ImagePosition {
		/**
		 * Image is in the North position.
		 */
		NORTH,
		/**
		 * Image is in the East position.
		 */
		EAST,
		/**
		 * Image is in the South position.
		 */
		SOUTH,
		/**
		 * Image is in the West position.
		 */
		WEST
	};

	/**
	 * Creates a WButton with no text or image. The button text must be set after construction.
	 */
	public WButton() {
		add(buttonImage);
	}

	/**
	 * Creates a WButton with the specified text.
	 *
	 * @param text the button text, using {@link MessageFormat} syntax.
	 *
	 * <pre>
	 * // Will create a button with the text &quot;Hello world&quot;
	 * new WButton(&quot;Hello world&quot;);
	 * </pre>
	 */
	public WButton(final String text) {
		this();
		setText(text);
	}

	/**
	 * Constructor. Set the button text and access key. Access keys are not case sensitive.
	 *
	 * @param text The button text.
	 * @param accessKey The shortcut key that activates the button.
	 */
	public WButton(final String text, final char accessKey) {
		this(text);
		setAccessKey(accessKey);
	}

	/**
	 * Indicates whether this button is disabled in the given context.
	 *
	 * @return true if this button is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether this button is disabled when loaded. If your button is disabled by a
	 * {@link com.github.bordertech.wcomponents.subordinate.WSubordinateControl} then it is not necessary to explicitly
	 * set it has disabled.
	 *
	 * @param disabled if true, this button is disabled. If false, it is enabled
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	// ================================
	// Action/Event handling
	/**
	 * Override handleRequest in order to perform processing for this component. This implementation checks whether the
	 * button has been pressed in the request.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		// Clear pressed
		clearPressed();

		String requestValue = request.getParameter(getId());
		boolean pressed = "x".equals(requestValue);

		// Only process on a POST
		if (pressed && !"POST".equals(request.getMethod())) {
			LOG.warn("Button pressed on a request that is not a POST. Will be ignored.");
			return;
		}

		setPressed(pressed, request);
	}

	/**
	 * Override preparePaintComponent to register an AJAX operation if this button is AJAX enabled.
	 *
	 * @param request the request being responded to
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		UIContext uic = UIContextHolder.getCurrent();

		if (isAjax() && uic.getUI() != null) {
			AjaxTarget target = getAjaxTarget();
			AjaxHelper.registerComponent(target.getId(), request, getId());
		}
	}

	/**
	 * Clear button is pressed flag.
	 */
	protected void clearPressed() {
		getOrCreateComponentModel().isPressed = false;
	}

	/**
	 * Indicates whether this button has been pressed for the current request.
	 *
	 * @return true if this button is pressed, false otherwise
	 */
	public boolean isPressed() {
		return getComponentModel().isPressed;
	}

	/**
	 * Sets whether this button is pressed. You probably do not want to invoke this manually, it is called from
	 * {@link #handleRequest}. If the button is pressed its Action is queued so it is invoked after the entire request
	 * has been handled.
	 *
	 * @param pressed true for pressed, false for not pressed
	 * @param request the Request that is being responded to
	 */
	protected void setPressed(final boolean pressed, final Request request) {
		if (pressed && isDisabled()) {
			// Protect against client-side tampering of disabled/read-only fields.
			LOG.warn("A disabled button has been triggered. " + getText() + ". " + getId());
			return;
		}

		getOrCreateComponentModel().isPressed = pressed;

		// If an action has been supplied then execute it, but only after
		// handle request has been performed on the entire component tree.
		if (pressed) {
			final Action action = getAction();

			if (action == null) {
				// Need to run the "afterActionExecute" as late as possible.
				Runnable later = new Runnable() {
					@Override
					public void run() {
						focusMe();
					}
				};
				invokeLater(later);
			} else {
				final ActionEvent event = new ActionEvent(this, getActionCommand(),
						getActionObject());

				Runnable later = new Runnable() {
					@Override
					public void run() {
						beforeActionExecute(request);
						action.execute(event);
						afterActionExecute(request);
					}
				};

				invokeLater(later);
			}
		}
	}

	/**
	 * Called before the button action is executed. Subclasses may override. Provides an opportunity to do preparation
	 * work before the action.execute
	 *
	 * @param request the request that is being responded to.
	 */
	protected void beforeActionExecute(final Request request) {
		// By default, do nothing.
	}

	/**
	 * Called after the button action has been executed. Subclasses may override. Provides an opportunity to do cleanup
	 * work after the action.execute. In this implementation an actioned button will try to refocus itself if nothing
	 * else has focus.
	 *
	 * @param request the request that is being responded to.
	 */
	protected void afterActionExecute(final Request request) {
		focusMe();
	}

	// TODO: Remove this as it commonly results in accessibility errors so should be removed ASAP. Initial focus should
	// be determined on a case-by-case basis not in this abstract manner.
	/**
	 * Sets the focus back to this button unless explicity set elsewhere.
	 */
	protected void focusMe() {
		if (UIContextHolder.getCurrent().getFocussed() == null) {
			setFocussed();
		}
	}

	// ================================
	// Attributes
	/**
	 * @return the action to execute when the button is pressed
	 */
	public Action getAction() {
		return getComponentModel().action;
	}

	/**
	 * Sets the action that you want run if the button is pressed.
	 *
	 * @param action the action to execute when the button is pressed
	 *
	 * <pre>
	 * // Causes a message to be printed to the log whenever the button is pressed.
	 * setAction(new Action() {
	 *   public void execute(final ActionEvent event) {
	 *     LOG.info(&quot;The button was clicked at &quot; + new Date());
	 *   }
	 * });
	 * </pre>
	 */
	public void setAction(final Action action) {
		getOrCreateComponentModel().action = action;
	}

	/**
	 * Return the button text. Returns:
	 * <ul>
	 * <li>user text if set; otherwise</li>
	 * <li>shared text if set;</li>
	 * <li>user value if set;</li>
	 * <li>bean value if present; or</li>
	 * <li>shared value.</li>
	 * </ul>
	 *
	 * @return the button text
	 */
	public String getText() {
		Object text = getComponentModel().text;

		if (text == null) {
			Object value = getData();
			if (value != null) {
				text = value.toString();
			}
		}

		return I18nUtilities.format(null, text);
	}

	/**
	 * Sets the button text. A button must have text even if it only displays an image. In this case the text is used
	 * to provide a text alternative to the image.
	 *
	 * @param text the button text, using {@link MessageFormat} syntax
	 * @param args optional arguments for the button text format string
	 *
	 * <pre>
	 * // Sets the button text to &quot;Hello world&quot;
	 * myButton.setText(&quot;Hello world&quot;);
	 * </pre>
	 *
	 * <pre>
	 * // Sets the button text to &quot;Secret agent James Bond, 007&quot;
	 * myButton.setText(&quot;Secret agent {0}, {1,number,000}&quot;, &quot;James Bond&quot;, 7);
	 * </pre>
	 */
	public void setText(final String text, final Serializable... args) {
		ButtonModel model = getOrCreateComponentModel();
		model.text = I18nUtilities.asMessage(text, args);
	}

	/**
	 * Return the button value. By default the value is the same as the text placed on the button.
	 *
	 * @return the button value
	 */
	public String getValue() {
		Object value = getData();
		if (value != null) {
			return value.toString();
		}

		String text = getText();
		return text == null ? NO_VALUE : text;
	}

	/**
	 * Set the button value.
	 *
	 * @param value the button value
	 */
	public void setValue(final String value) {
		setData(value);
	}

	/**
	 * Same as getValue(). This method exists simply to clarify the relationship between the WButton, its Action, and
	 * the ActionEvent sent to the execute() method of the Action.
	 *
	 * @return the action command
	 */
	public String getActionCommand() {
		return getValue();
	}

	/**
	 * Same as setValue(). This method exists simply to clarify the relationship between the WButton, its Action, and
	 * the ActionEvent sent to the execute() method of the Action.
	 *
	 * @param actionCommand the action command
	 */
	public void setActionCommand(final String actionCommand) {
		setValue(actionCommand);
	}

	/**
	 * Returns the data object that has been associated with this button, else null. For convenience, this data object
	 * is passed to the execute() method of the button's associated Action, in the ActionEvent parameter.
	 *
	 * @return the action object
	 */
	public Object getActionObject() {
		return getComponentModel().actionObject;
	}

	/**
	 * Associate this button with a data object that can be easily accessed in the execute() method of the button's
	 * associated Action.
	 *
	 * @param data the action object
	 */
	public void setActionObject(final Serializable data) {
		getOrCreateComponentModel().actionObject = data;
	}

	/**
	 * Return the image to display on the button.
	 *
	 * @return the image
	 */
	public Image getImage() {
		return getComponentModel().image;
	}

	/**
	 * Sets the image to display on the button. The text alternative for the image is generated from the button text.
	 *
	 * @param image the image, or null for no image
	 */
	public void setImage(final Image image) {
		ButtonModel model = getOrCreateComponentModel();
		model.image = image;
		model.imageUrl = null;
	}

	/**
	 * Sets the image to display on the button. The image will be read from the application's class path rather than
	 * from its web docs. The text alternative for the image is generated from the button text.
	 *
	 * @param image the relative path to the image resource, or null for no image
	 */
	public void setImage(final String image) {
		setImage(new ImageResource(image));
	}

	/**
	 * Return the {@link WImage} used by this button to hold the {@link Image} resource.
	 * <p>
	 * If the button is not using an Image resource, it will return null.
	 * </p>
	 *
	 * @return the WImage holding the Image resource or null if the button is not using an Image resource
	 */
	public WImage getImageHolder() {
		return getImage() == null ? null : buttonImage;
	}

	/**
	 * Return the URL of the image to display on the button.
	 *
	 * @return the image url
	 */
	public String getImageUrl() {
		return getImage() == null ? getComponentModel().imageUrl : getImageHolder().getTargetUrl();
	}

	/**
	 * Sets the URL of the image to display on the button. This is the optimum way to set an image on a button as it
	 * allows the image to be served from a CDN and cached on the client for future use. In this way the same "image"
	 * may be used for many buttons (or other components) with no extra bandwidth use. The text alternative for the
	 * image is generated from the button text.
	 *
	 * @param imageUrl the image url, or null for no image
	 */
	public void setImageUrl(final String imageUrl) {
		ButtonModel model = getOrCreateComponentModel();
		model.imageUrl = imageUrl;
		model.image = null;
	}

	/**
	 * @return the position of the image
	 */
	public ImagePosition getImagePosition() {
		return getComponentModel().imagePosition;
	}

	/**
	 * The position of the image on the button relative to the button text. If the button has an image and this is not
	 * set then the button text is used as the text alternative for the image and is not displayed on the button.
	 *
	 * @param imagePosition the position of the image
	 */
	public void setImagePosition(final ImagePosition imagePosition) {
		getOrCreateComponentModel().imagePosition = imagePosition;
	}

	/**
	 * @return true if this button should be rendered as a link.
	 */
	public boolean isRenderAsLink() {
		return getComponentModel().renderAsLink;
	}

	/**
	 * Sets whether this button should be rendered like a link. This is a visual analog to a link, it <strong>does
	 * not</strong> cause the button to output a html 'a' element since the button still has to have a button element to
	 * accessibly undertake performance of any action which is not navigation. If you want to navigate with a link then
	 * you should be using WLink.
	 *
	 * @param renderAsLink true if this button should be rendered like a link.
	 */
	public void setRenderAsLink(final boolean renderAsLink) {
		getOrCreateComponentModel().renderAsLink = renderAsLink;
	}

	/**
	 * Indicates whether this button will trigger a WPopup when used.
	 *
	 * @return true if this button triggers a popup, false otherwise.
	 */
	public boolean isPopupTrigger() {
		return getComponentModel().popupTrigger;
	}

	/**
	 * Sets whether this button will trigger a WPopup when used. This flag is used to provide information to the
	 * client on the button's intent. The actual invocation of the WPopup is done elsewhere, e.g. in the button's
	 * action.
	 *
	 * <p>
	 * This <strong>must</strong> be set on every button which has a submit action or causes an ajax action if the
	 * resulting payload contains a WPopup. This is mandatory to meet accessibility guidelines as it is the only way the
	 * UI can forewarn assistive technologies that a button will cause a pop-up.
	 * </p>
	 *
	 * @param popupTrigger The popupTrigger to set.
	 */
	public void setPopupTrigger(final boolean popupTrigger) {
		getOrCreateComponentModel().popupTrigger = popupTrigger;
	}

	/**
	 * The accesskey is a shortcut key that will focus the input element when used in combination with the Alt key.
	 *
	 * @return The key that in combination with Alt will focus this input.
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
	 * Set the accesskey (shortcut key) that will activate the button. Some chars will not work in some browsers and
	 * most chars are reserved for use by some AT or other so the actual number of available access keys is pretty much
	 * zero. Good luck!
	 *
	 * @param accesskey the key (in combination with the Alt/Meta key) that activates this element
	 */
	public void setAccessKey(final char accesskey) {
		getOrCreateComponentModel().accessKey = accesskey;
	}

	/**
	 * Indicates whether this button is AJAX enabled. A button is an AJAX button if it has a
	 * {@link #setAjaxTarget(AjaxTarget) target set}.
	 *
	 * @return true if this button is AJAX enabled, false otherwise
	 */
	public boolean isAjax() {
		return getAjaxTarget() != null;
	}

	/**
	 * @return the default AJAX target for this button
	 */
	public AjaxTarget getAjaxTarget() {
		return getComponentModel().ajaxTarget;
	}

	/**
	 * Sets the AJAX target for the button. If a target is supplied, a an AJAX request is made rather than a round-trip
	 * to the server. The AJAX response will only contain the (possibly updated) target element rather than the entire
	 * UI. If a button has to target more than one component a {@link WAjaxControl} should be used instead.
	 *
	 * @param ajaxTarget the AJAX target.
	 */
	public void setAjaxTarget(final AjaxTarget ajaxTarget) {
		getOrCreateComponentModel().ajaxTarget = ajaxTarget;
	}

	/**
	 * Indicates whether there are unsaved changes in the given context.
	 *
	 * @return true if there are unsaved changes, false if not.
	 */
	public boolean isUnsavedChanges() {
		return getComponentModel().unsavedChanges;
	}

	/**
	 * This feature exists to pass information to the client to inform it that the server side thinks there are unsaved
	 * changes. It is used when a particular view is part of a multi-page process and the steps are not saved
	 * individually. The warning is triggered by a button or menu item with {@link #isCancel() cancel set true}.
	 *
	 * @param hasUnsavedChanges true if there are unsaved changes, false if not.
	 */
	public void setUnsavedChanges(final boolean hasUnsavedChanges) {
		getOrCreateComponentModel().unsavedChanges = hasUnsavedChanges;
	}

	/**
	 * @return the confirmation message for the button.
	 */
	public String getMessage() {
		return I18nUtilities.format(null, getComponentModel().message);
	}

	/**
	 * Sets the confirmation message for the button. If a button has a confirmation message then the user is asked to
	 * confirm each invocation of the button unless the button also has {@link #isCancel() cancel set true} in which
	 * case the confirmation is only required if either there are {@link #isUnsavedChanges() unsaved changes on the
	 * server} or the user has changed the value of any control in the current view without saving.
	 *
	 * @param message the confirmation message to display, using {@link MessageFormat} syntax
	 * @param args optional arguments for the message format string
	 */
	public void setMessage(final String message, final Serializable... args) {
		getOrCreateComponentModel().message = I18nUtilities.asMessage(message, args);
	}

	/**
	 * @return true if the button is a cancel control and will warn the user of unsaved changes, otherwise false
	 */
	public boolean isCancel() {
		return getComponentModel().cancel;
	}

	/**
	 * Sets the button as a cancel button. A cancel button is the same as any other button but has the following
	 * characteristics:
	 *
	 * <ul>
	 * <li>it can trigger an unsaved changes warning; and</li>
	 * <li>it will not invoke client side validation;</li>
	 * </ul>
	 * <p>
	 * The action applied to a cancel button is normally one to undo the current process and possibly navigate to an
	 * initial view. A cancel button could be used in contexts such as going backwards through a multi-step process;
	 * returning to a previous view without committing changes in the current view; or logging out of an application.
	 * </p>
	 *
	 * @param cancel true if the button is a cancel control and will warn the user of unsaved changes, otherwise false
	 */
	public void setCancel(final boolean cancel) {
		getOrCreateComponentModel().cancel = cancel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	@Override
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * @return a String representation of this component for debugging purposes
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text, -1, -1);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new ButtonModel
	 */
	@Override
	protected ButtonModel newComponentModel() {
		return new ButtonModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ButtonModel getComponentModel() {
		return (ButtonModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ButtonModel getOrCreateComponentModel() {
		return (ButtonModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WButton.
	 */
	public static class ButtonModel extends BeanAndProviderBoundComponentModel {

		/**
		 * Indicates whether the button has been pressed in the current request/response cycle.
		 */
		private boolean isPressed;

		/**
		 * The text to display on the button.
		 */
		private Serializable text;

		/**
		 * The object to include in the action event when the action is triggered.
		 */
		private Object actionObject;

		/**
		 * The target component to repaint (via AJAX) when the button is pressed.
		 */
		private AjaxTarget ajaxTarget;

		/**
		 * If not null, it will be taken as a URL to use as image.
		 */
		private String imageUrl;

		/**
		 * The position of the image.
		 */
		private ImagePosition imagePosition;

		/**
		 * The image to display on the button.
		 */
		private Image image;

		/**
		 * The action to execute when the button is pressed.
		 */
		private Action action;

		/**
		 * If true, the button should be rendered as a hyperlink rather than a button.
		 */
		private boolean renderAsLink;

		/**
		 * The key shortcut that activates the button.
		 */
		private char accessKey = '\0';

		/**
		 * Indicates whether the button triggers a WPopup.
		 */
		private boolean popupTrigger;

		/**
		 * Indicates whether there are unsaved changes.
		 */
		private boolean unsavedChanges;

		/**
		 * The confirmation message to be shown.
		 */
		private Serializable message;

		/**
		 * Act as a cancel control and warn the user of unsaved changes.
		 */
		private boolean cancel;
	}

	/**
	 * This WImage implementation delegates to the button's image and is only used to serve up the image for the button.
	 */
	private static final class ButtonImage extends WImage {

		/**
		 * The button containing the button image.
		 */
		private final WButton button;

		/**
		 * Creates a button image.
		 *
		 * @param button the owning button
		 */
		private ButtonImage(final WButton button) {
			this.button = button;
		}

		/**
		 * Override isVisible to only return true if the button has an image.
		 *
		 * @return true if this component is visible, false if invisible
		 */
		@Override
		public boolean isVisible() {
			return button.getImage() != null;
		}

		/**
		 * Override getImage to return the button's image.
		 *
		 * @return the button image
		 */
		@Override
		public Image getImage() {
			return button.getImage();
		}
	}
}
