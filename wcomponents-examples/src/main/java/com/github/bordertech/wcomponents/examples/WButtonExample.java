package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WButton.ImagePosition;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;

/**
 * <p>
 * An example showing the available styles of the {@link WButton} component.
 * </p>
 * <p>
 * Also see {@link ButtonOptionsExample Button configuration options} example.
 * </p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0 - added anti-pattern examples.
 */
public class WButtonExample extends WPanel implements MessageContainer {

	/**
	 * The message box used to display messages to the user.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * A plain button.
	 */
	private final WButton plainBtn = new WButton("Plain");

	/**
	 * This button will be rendered as a link.
	 */
	private final WButton linkBtn = new WButton("Link");

	/**
	 * Creates a WButtonExample which has a sequence of many WButton examples.
	 */
	@SuppressWarnings("serial")
	public WButtonExample() {
		add(messages);

		// Plain button
		add(new WHeading(HeadingLevel.H2, "Plain button"));
		add(plainBtn);

		// Button rendered as a link
		add(new WHeading(HeadingLevel.H2, "Link button"));
		add(new ExplanatoryText(
				"It is a mere design artifact to make a button look like a link but it is strongly recommended "
						+ "that you do not do this as it can cause issues for users."));

		linkBtn.setRenderAsLink(true);
		add(linkBtn);

		// Buttons to demonstrate use of Actions and dynamically changed button text.
		final WButton disableBtn = new WButton("Disable buttons");
		add(new WHeading(HeadingLevel.H2, "Button with actions"));
		add(new WText("This button disables/enables the plain and link buttons above."));
		add(disableBtn);

		disableBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				boolean disabled = !plainBtn.isDisabled();
				plainBtn.setDisabled(disabled);
				linkBtn.setDisabled(disabled);
				disableBtn.setText(disabled ? "Enable buttons" : "Disable buttons");
			}
		});

		add(new WHorizontalRule());

		//disabled state
		addDisabledExamples();

		add(new WHorizontalRule());
		addImageExamples();

		add(new WHorizontalRule());
		addDefaultSubmitButtonExample();

		add(new WHorizontalRule());
		addAntiPatternExamples();
	}

	/**
	 * An example to cover the gamut of image buttons both with and without visible text and with both renderAsLink and
	 * render as button.
	 */
	private void addImageExamples() {
		// Button rendered with an image only
		add(new WHeading(HeadingLevel.H2, "Image buttons"));
		add(new ExplanatoryText("This example shows how to use an image inside a WButton."));

		add(new WHeading(HeadingLevel.H3, "Just an image"));
		add(new ExplanatoryText(
				"This example shows how to use an image as the only content of a WButton. "
				+ "The button must still have text content to adequately explain the button's purpose."));

		add(new WHeading(HeadingLevel.H4, "Image in a button"));
		add(makeImageButton("Save", false));

		add(new WHeading(HeadingLevel.H4, "Image button without button style"));
		add(new ExplanatoryText(
				"This example shows how to use an image as the only content of a WButton when styled to be without its button appearance. "
				+ "If you are creating a button containing only an image you should be careful as it may not be obvious to the application user that the 'image' is actually a 'button'."
				+ "The button must still have text content to adequately explain the button's purpose."));
		add(makeImageButton("Save", true));

		add(new WHeading(HeadingLevel.H3, "Image and text"));
		add(new ExplanatoryText(
				"This example shows how to use an image and text as the content of a button."));

		add(new WHeading(HeadingLevel.H4, "Rendered as a button"));

		WPanel buttonLayoutPanel = new WPanel(WPanel.Type.BOX);
		buttonLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0,
				FlowLayout.ContentAlignment.BOTTOM));
		add(buttonLayoutPanel);
		buttonLayoutPanel.
				add(makeImageButtonWithPosition("Image on the North", ImagePosition.NORTH));
		buttonLayoutPanel.add(makeImageButtonWithPosition("Image on the East", ImagePosition.EAST));
		buttonLayoutPanel.
				add(makeImageButtonWithPosition("Image on the South", ImagePosition.SOUTH));
		buttonLayoutPanel.add(makeImageButtonWithPosition("Image on the West", ImagePosition.WEST));

		add(new WHeading(HeadingLevel.H4, "Rendered as a link"));
		add(new ExplanatoryText(
				"This example shows how to use an image and text as the content of a button without the button styling."));
		buttonLayoutPanel = new WPanel(WPanel.Type.BOX);
		buttonLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0,
				FlowLayout.ContentAlignment.BOTTOM));
		add(buttonLayoutPanel);
		buttonLayoutPanel.add(makeImageButtonWithPosition("Image on the North", ImagePosition.NORTH,
				true));
		buttonLayoutPanel.add(makeImageButtonWithPosition("Image on the East", ImagePosition.EAST,
				true));
		buttonLayoutPanel.add(makeImageButtonWithPosition("Image on the South", ImagePosition.SOUTH,
				true));
		buttonLayoutPanel.add(makeImageButtonWithPosition("Image on the West", ImagePosition.WEST,
				true));
	}

	/**
	 * Helper to create a button with an image and an action.
	 *
	 * @param text the text content of the button
	 * @param pos the position of the button image relative to the text
	 * @param asLink indicates the button should be rendered as a link
	 * @return a button
	 */
	private WButton makeImageButtonWithPosition(final String text, final ImagePosition pos,
			final Boolean asLink) {
		WButton button = new WButton(text);
		button.setImage("/image/tick.png");
		if (pos != null) {
			button.setImagePosition(pos);
		}
		button.setActionObject(button);
		button.setAction(new ExampleButtonAction());
		if (asLink) {
			button.setRenderAsLink(true);
		}
		return button;
	}

	/**
	 *
	 * Helper to create a button with text, an image and an action.
	 *
	 * @param text the text content of the button
	 * @param pos the position of the button image relative to the text
	 * @return a button
	 */
	private WButton makeImageButtonWithPosition(final String text, final ImagePosition pos) {
		return makeImageButtonWithPosition(text, pos, false);
	}

	/**
	 * Helper to create a button with an image and an action.
	 *
	 * @param text the text content of the button
	 * @param asLink indicates the button should be rendered as a link
	 * @return a button
	 */
	private WButton makeImageButton(final String text, final Boolean asLink) {
		return makeImageButtonWithPosition(text, null, asLink);
	}

	/**
	 * Examples showing how to set a WButton as the default submit button for an input control.
	 */
	private void addDefaultSubmitButtonExample() {
		add(new WHeading(HeadingLevel.H3, "Default submit button"));

		add(new ExplanatoryText(
				"This example shows how to use an image as the only content of a WButton. "
				+ "In addition this text field submits the entire screen using the image button to the right of the field."));
		// We use WFieldLayout to lay out a label:input pair. In this case the input is a
		//compound control of a WTextField and a WButton.
		WFieldLayout imageButtonFieldLayout = new WFieldLayout();
		imageButtonFieldLayout.setLabelWidth(25);
		add(imageButtonFieldLayout);
		// the text field and the button both need to be defined explicitly to be able to add them into a wrapper
		WTextField textFld = new WTextField();
		//and finally we get to the actual button
		WButton button = new WButton("Flag this record for follow-up");
		button.setImage("/image/flag.png");
		button.getImageHolder().setCacheKey("eg-button-flag");

		button.setActionObject(button);
		button.setAction(new ExampleButtonAction());
		//we can set the image button to be the default submit button for the text field.
		textFld.setDefaultSubmitButton(button);
		//There are many way of putting multiple controls in to a WField's input.
		//We are using a WContainer is the one which is lowest impact in the UI.
		WContainer imageButtonFieldContainer = new WContainer();
		imageButtonFieldContainer.add(textFld);
		//Use a WText to push the button off of the text field by an appropriate (user-agent determined) amount.
		imageButtonFieldContainer.add(new WText("\u2002")); //an en space is half an em. a none-breaking space \u00a0 could also be used but will have no effect on inter-node wrapping
		imageButtonFieldContainer.add(button);
		//Finally add the input wrapper to the WFieldLayout
		imageButtonFieldLayout.addField("Enter record ID", imageButtonFieldContainer);
	}

	/**
	 * Examples of disabled buttons in various guises.
	 */
	private void addDisabledExamples() {
		add(new WHeading(HeadingLevel.H2, "Examples of disabled buttons"));

		WPanel disabledButtonLayoutPanel = new WPanel(WPanel.Type.BOX);
		disabledButtonLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0,
				FlowLayout.ContentAlignment.BASELINE));
		add(disabledButtonLayoutPanel);

		WButton button = new WButton("Disabled button");
		button.setDisabled(true);
		disabledButtonLayoutPanel.add(button);
		button = new WButton("Disabled button as link");
		button.setDisabled(true);
		button.setRenderAsLink(true);
		disabledButtonLayoutPanel.add(button);

		add(new WHeading(HeadingLevel.H3, "Examples of disabled buttons displaying only an image"));
		disabledButtonLayoutPanel = new WPanel(WPanel.Type.BOX);
		disabledButtonLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0,
				FlowLayout.ContentAlignment.BASELINE));
		add(disabledButtonLayoutPanel);

		button = new WButton("Disabled button");
		button.setDisabled(true);
		button.setImage("/image/tick.png");
		button.setToolTip("Checking currently disabled");
		disabledButtonLayoutPanel.add(button);
		button = new WButton("Disabled button as link");
		button.setDisabled(true);
		button.setRenderAsLink(true);
		button.setImage("/image/tick.png");
		button.setToolTip("Checking currently disabled");
		disabledButtonLayoutPanel.add(button);

		add(new WHeading(HeadingLevel.H3,
				"Examples of disabled buttons displaying an image with imagePosition EAST"));

		disabledButtonLayoutPanel = new WPanel(WPanel.Type.BOX);
		disabledButtonLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0,
				FlowLayout.ContentAlignment.BASELINE));
		add(disabledButtonLayoutPanel);
		button = new WButton("Disabled button");
		button.setDisabled(true);
		button.setImage("/image/tick.png");
		button.setToolTip("Checking currently disabled");
		button.setImagePosition(ImagePosition.EAST);
		disabledButtonLayoutPanel.add(button);
		button = new WButton("Disabled button as link");
		button.setDisabled(true);
		button.setRenderAsLink(true);
		button.setImage("/image/tick.png");
		button.setToolTip("Checking currently disabled");
		button.setImagePosition(ImagePosition.EAST);
		disabledButtonLayoutPanel.add(button);
	}

	/**
	 * Examples of what not to do when using WButton.
	 */
	private void addAntiPatternExamples() {
		add(new WHeading(HeadingLevel.H2, "WButton anti-pattern examples"));
		add(new WMessageBox(WMessageBox.WARN,
				"These examples are purposely bad and should not be used as samples of how to use WComponents but samples of how NOT to use them."));

		add(new WHeading(HeadingLevel.H3, "WButton without a good label"));
		add(new WButton("\u2002"));
		add(new ExplanatoryText("A button without a text label is very bad"));

		add(new WHeading(HeadingLevel.H3, "WButton with a WImage but without a good label"));
		WButton button = new WButton("\u00a0");
		button.setImage("/image/help.png");
		add(button);
		add(new ExplanatoryText(
				"A button without a text label is very bad, even if you think the image is sufficient. The text label becomes the image alt text."));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WMessages getMessages() {
		return messages;
	}

	/**
	 * Override handleRequest in order to perform processing specific to this example. Normally, you would attach
	 * actions to a button rather than calling "isPressed" on each button.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (plainBtn.isPressed()) {
			WMessages.getInstance(this).info("Plain button pressed.");
		}

		if (linkBtn.isPressed()) {
			WMessages.getInstance(this).info("Link button pressed.");
		}
	}

	/**
	 * Sample Action for most of the buttons in this example.
	 *
	 * @author exbtma
	 */
	@SuppressWarnings("serial")
	private final class ExampleButtonAction implements Action {

		/**
		 * the confirmation message that the button was pressed.
		 */
		private static final String MESSAGE = " was pressed";

		@Override
		public void execute(final ActionEvent event) {
			if (event.getActionObject() == null) {
				return;
			}

			messages.info(((WButton) event.getActionObject()).getText() + MESSAGE);
		}

	}
}
