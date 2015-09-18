package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSkipLinks;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.layout.ListLayout;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.util.List;

/**
 * A component which enables users to pick an example to display.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TreePicker extends WPanel implements MessageContainer {

	/**
	 * the application header panel. This is just a nice to have
	 */
	private final WPanel headerPanel = new WPanel(WPanel.Type.HEADER);

	/**
	 * The top-level messages instance for this UI. Examples may also have their own instance.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * The panel which contains a menu to select the examples.
	 */
	private final MenuPanel menuPanel = new MenuPanel();

	/**
	 * The panel used to display an example.
	 */
	private final ExamplePanel examplePanel = new ExamplePanel();

	/**
	 * This text field can be used to directly select an example, using the class name or the example name.
	 */
	private final WTextField selectOther = new WTextField();

	/**
	 * Resets the UIContext of the current example.
	 */
	private final WButton resetExampleButton = new WButton("Reset example");

	/**
	 * This button has no action, but causes a round trip.
	 */
	private final WButton refreshButton = new WButton("Refresh page");

	/**
	 * The main panel contains the menu and the currently selected example.
	 */
	private final WPanel mainPanel = new WPanel();

	/**
	 * column widths.
	 */
	private static final int[] COL_WIDTH = {20, 80};
	/**
	 * column allignment.
	 */
	private static final ColumnLayout.Alignment[] COL_ALIGN = {ColumnLayout.Alignment.LEFT, ColumnLayout.Alignment.LEFT};
	/**
	 * column hgap.
	 */
	private static final int COL_HGAP = 12;
	/**
	 * column vgap.
	 */
	private static final int COL_VGAP = 0;

	/**
	 * Creates a TreePicker.
	 */
	public TreePicker() {
		add(headerPanel);
		headerPanel.add(new WSkipLinks());
		/* The utility bar contains the example selector and a bunch of buttons */
		WPanel utilityBar = new WPanel();
		headerPanel.add(utilityBar);
		utilityBar.setLayout(new ListLayout(ListLayout.Type.FLAT, ListLayout.Alignment.RIGHT,
				ListLayout.Separator.NONE, false, COL_HGAP, 0));

		// Add an image for the banner
		//WImage logo = new WImage("/com/github/bordertech/wcomponents/examples/picker/wclogo_small.gif", "WComponents examples");
		//String version = Config.getInstance().getString("wcomponents-examples.version");
		//logo.setCacheKey("wc.treepicker.logo." + version);
		//add(logo, "logo");
		headerPanel.add(new WHeading(WHeading.TITLE, "WComponents"));

		add(messages, "messages");

		selectOther.setToolTip(
				"Enter a partial name for one of the examples below, or a fully qualified class name for an arbitrary component.");

		// Set a validating action on itself to avoid client side validation
		resetExampleButton.setAction(new ValidatingAction(messages.getValidationErrors(),
				resetExampleButton) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				examplePanel.resetExample();
			}
		});
		resetExampleButton.setImage("/image/cancel-w.png");
		resetExampleButton.setRenderAsLink(true);
		resetExampleButton.setToolTip("reset");
		//resetExampleButton.setImagePosition(ImagePosition.EAST);

		WButton selectOtherButton = new WButton("Select");
		selectOtherButton.setImage("/image/open-in-browser-w.png");
		selectOtherButton.setRenderAsLink(true);
		selectOtherButton.setToolTip("select other");

		// Set a validating action on itself to avoid client side validation
		selectOtherButton.setAction(new ValidatingAction(messages.getValidationErrors(),
				selectOtherButton) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				if (!Util.empty(selectOther.getText())) {
					ExampleData example = menuPanel.getClosestMatch(selectOther.getText());

					if (example != null) {
						selectExample(example);
					}
				}
			}
		});
//
//        WButton logoutButton = new WButton("Logout")
//        {
//            @Override
//            public void handleRequest(final Request request)
//            {
//                super.handleRequest(request);
//
//                if (isPressed())
//                {
//                    request.logout();
//                }
//            }
//        };
//        // Set a validating action on itself to avoid client side validation
//        logoutButton.setAction(new ValidatingAction(messages.getValidationErrors(), logoutButton)
//        {
//            @Override
//            public void executeOnValid(final ActionEvent event)
//            {
//                // Do Nothing
//            }
//        });

		// Set a validating action on itself to avoid client side validation
		refreshButton.setAction(
				new ValidatingAction(messages.getValidationErrors(), refreshButton) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				// Do Nothing
			}
		});
		refreshButton.setImage("/image/refresh-w.png");
		refreshButton.setRenderAsLink(true);
		refreshButton.setToolTip("refresh");
		//refreshButton.setImagePosition(ImagePosition.EAST);

		utilityBar.setDefaultSubmitButton(selectOtherButton);

		WContainer selectContainer = new WContainer();
		selectContainer.add(new WLabel("Select other example\u00a0", selectOther));
		selectContainer.add(selectOther);
		selectContainer.add(selectOtherButton);
		utilityBar.add(selectContainer);
		utilityBar.add(resetExampleButton);
		utilityBar.add(refreshButton);
		//utilityBar.add(logoutButton);
		mainPanel.setLayout(new ColumnLayout(COL_WIDTH, COL_ALIGN, COL_HGAP, COL_VGAP));
		mainPanel.setMargin(new com.github.bordertech.wcomponents.Margin(COL_HGAP));
		mainPanel.add(menuPanel);
		mainPanel.add(examplePanel);
		add(mainPanel, "mainPanel");
	}

	/**
	 * Adds a grouped set of examples to the menu.
	 *
	 * @param groupName the name of the group for the examples, or null to add to the menu directly.
	 * @param entries the examples to add to the group.
	 */
	public void addExamples(final String groupName, final ExampleData[] entries) {
		menuPanel.addExamples(groupName, entries);
	}

	/**
	 * <p>
	 * Override handleRequest in order to perform custom processing for this component.</p>
	 *
	 * <p>
	 * Normally, applications should not look at the request directly, but we look for an "example" parameter here so
	 * that developers can bookmark particular examples which they are interested in.</p>
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		String exampleStr = request.getParameter("example");

		if (exampleStr != null) {
			ExampleData example = menuPanel.getClosestMatch(exampleStr);
			selectExample(example);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WMessages getMessages() {
		return messages;
	}

	/**
	 * @return the panel which displays the examples.
	 */
	public ExamplePanel getExamplePanel() {
		return examplePanel;
	}

	/**
	 * Selects an example.
	 *
	 * @param data the ExampleData of the example to select.
	 */
	public void selectExample(final ExampleData data) {
		menuPanel.addToRecent(data);
		examplePanel.selectExample(data);
	}

	/**
	 * Override preparePaintComponent in order to populate the recently accessed menu when a user accesses this panel
	 * for the first time.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			List<ExampleData> recent = menuPanel.getRecent();

			if (!recent.isEmpty()) {
				selectExample(recent.get(0));
			}

			setInitialised(true);
		}
	}
}
