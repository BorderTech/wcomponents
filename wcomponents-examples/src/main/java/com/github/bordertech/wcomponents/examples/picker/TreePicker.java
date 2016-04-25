package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSkipLinks;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.layout.ListLayout;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A component which enables users to pick an example to display.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TreePicker extends WContainer {

	/**
	 * the application header panel. This is just a nice to have
	 */
	private final WPanel headerPanel = new WPanel(WPanel.Type.HEADER);

	/**
	 * The panel which contains a menu to select the examples.
	 */
	private final MenuPanel menuPanel = new MenuPanel();

	/**
	 * The panel used to display an example.
	 */
	private final ExampleSection exampleSection = new ExampleSection();

	/**
	 * When was the example last loaded?
	 */
	private final ExplanatoryText lastLoaded = new ExplanatoryText();

	/**
	 * The main panel contains the menu and the currently selected example.
	 */
	private final WPanel mainPanel = new WPanel();

	/**
	 * column widths.
	 */
	private static final int[] COL_WIDTH = {20, 80};
	/**
	 * column alignment.
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
		// Set up the HEADER

		mainPanel.setIdName("main_panel");
		mainPanel.setLayout(new ColumnLayout(COL_WIDTH, COL_ALIGN, COL_HGAP, COL_VGAP));
		mainPanel.setMargin(new Margin(COL_HGAP));

		buildUI();
	}

	/**
	 * Add all the bits in the right order.
	 */
	private void buildUI() {
		add(new WSkipLinks());

		// the application header
		add(headerPanel);
		headerPanel.add(new UtilityBar());
		headerPanel.add(new WHeading(HeadingLevel.H1, "WComponents"));

		// mainPanel holds the menu and the actual example.
		add(mainPanel);
		mainPanel.add(menuPanel);
		mainPanel.add(exampleSection);

		// An application footer?
		WPanel footer = new WPanel(WPanel.Type.FOOTER);
		footer.add(lastLoaded);

		//what goes in a footer?
		// footer.add(new ExplanatoryText("Copyright is not the answer."));
		add(footer);
	}

	/**
	 *
	 * @param date The date to format.
	 * @return a readable date and time.
	 */
	private String getMeAReadableDate(final Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, hh:mm aaa");
		return sdf.format(null == date ? (new Date()) : date);
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
	 * @return the panel which displays the examples.
	 */
//	public ExampleSection getExamplePanel() {
//		return exampleSection;
//	}

	/**
	 * Selects an example.
	 *
	 * @param data the ExampleData of the example to select.
	 */
	public void selectExample(final ExampleData data) {
		menuPanel.addToRecent(data);
		exampleSection.selectExample(data);
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
		lastLoaded.setText("Last loaded on ".concat(getMeAReadableDate(new Date())));
	}

	/**
	 * Represents a set of active components to place into the header.
	 */
	private class UtilityBar extends WPanel {

		/**
		 * This text field can be used to directly select an example, using the class name or the example name.
		 */
		private final WTextField selectOther = new WTextField();

		/**
		 * Create a utility bar.
		 */
		public UtilityBar() {
			selectOther.setToolTip("Enter the qualified name of an example.");
			setUp();
		}

		/**
		 * Add the UI controls to the utility bar.
		 */
		private void setUp() {
			setLayout(new ListLayout(ListLayout.Type.FLAT, ListLayout.Alignment.RIGHT, ListLayout.Separator.NONE,
					false));
			// The select another example button.
			final WButton selectOtherButton = new WButton("Select");
			selectOtherButton.setImage("/image/open-in-browser-w.png");
			selectOtherButton.setRenderAsLink(true);
			selectOtherButton.setAction(new ValidatingAction(exampleSection.getMessages().getValidationErrors(),
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

			setDefaultSubmitButton(selectOtherButton);
			add(new WLabel("Qualified name", selectOther));
			add(selectOther);
			add(selectOtherButton);
		}

		/**
		 * Get the field used to obtain the FQN of an example not in the menu.
		 *
		 * @return A text field.
		 */
		public WTextField getSelectOther() {
			return selectOther;
		}
	}
}
