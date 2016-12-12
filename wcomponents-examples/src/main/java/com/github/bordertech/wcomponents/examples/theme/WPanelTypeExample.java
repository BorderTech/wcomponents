package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.menu.MenuBarExample;
import com.github.bordertech.wcomponents.layout.ListLayout;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Or;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
import com.github.bordertech.wcomponents.util.Util;

/**
 * This class demonstrates setting a {@link WPanel} type dynamically. The rest of the configuration infrastructure of this example is used to
 * switch commonly found content components in the target panel depending on the WPanel Type.
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WPanelTypeExample extends WContainer {

	/**
	 * The Target WPanel.
	 */
	private final WPanel panel;
	/**
	 * Used to select the WPanel Type.
	 */
	private final WDropdown panelType;

	/**
	 * A WHeading shown in the panel if it is a heading panel.
	 */
	private final WHeading heading;

	/**
	 * A set of utilities placed into a header panel. This is an emulation of a common design paradigm.
	 */
	private final WPanel utilBar;

	/**
	 * Used to set the panel content when the WPanel is not a header panel.
	 */
	private final WTextArea panelContent;

	/**
	 * Used to display the panel content when the WPanel is not a header panel.
	 */
	private final WTextArea panelContentRO;

	/**
	 * Required so that a subordinate can show/hide the content setting WTextArea.
	 */
	private WField contentField;

	/**
	 * Checkbox to allow the user to show a "utility bar" in a header panel.
	 */
	private final WCheckBox showUtilBar;

	/**
	 * Required so that a subordinate can show/hide the checkbox field.
	 */
	private WField showUtilBarField;

	/**
	 * A text input to set heading text.
	 */
	private final WTextField tfHeading;

	/**
	 * Required so that a subordinate can show/hide the heading input.
	 */
	private WField headingField;

	/**
	 * Checkbox to allow the user to show a menu bar in a header panel.
	 */
	private final WCheckBox showMenu;

	private WField showMenuField;

	/**
	 * A menu bar shown in a header panel.
	 */
	private final WMenu menu;

	/**
	 * This is needed to be able to reuse a menu from another example, it is only here for convenience and plays no part in this example.
	 */
	private final WText selectedMenuText;

	/**
	 * A button to apply configuration options to the target WPanel.
	 */
	private final WButton applyConfigButton;

	/**
	 * Default dummy content for the panel.
	 */
	private static final String SAMPLE_CONTENT = "Content of the panel";

	/**
	 * Default dummy heading text.
	 */
	private static final String SAMPLE_HEADER = "Heading";

	/**
	 * Default dummy panel title text.
	 */
	private static final String SAMPLE_TITLE_TEXT = "Section heading";

	/**
	 * Construct the example.
	 */
	public WPanelTypeExample() {
		// instantiate components
		panel = new WPanel(WPanel.Type.PLAIN);
		panelType = new WDropdown();
		heading = new WHeading(HeadingLevel.H1, SAMPLE_HEADER);
		utilBar = new WPanel();
		panelContent = new WTextArea();
		panelContentRO = new WTextArea();
		showUtilBar = new WCheckBox(true);
		showMenu = new WCheckBox(true);
		tfHeading = new WTextField();
		selectedMenuText = new WText();
		menu = (new MenuBarExample(selectedMenuText)).getMenu();
		menu.addHtmlClass(HtmlClassProperties.RESPOND);
		applyConfigButton = new WButton("Apply");

		//set immutable options
		panelContentRO.setReadOnly(true);
		panelContent.setRichTextArea(true);
		panelContentRO.setRichTextArea(true);
		panelType.setOptions(WPanel.Type.values());
		panelType.setSelected(WPanel.Type.PLAIN);
		applyConfigButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				updateUI();
			}
		});

		// set initial properties
		panel.setTitleText(SAMPLE_TITLE_TEXT);
		panel.setType(WPanel.Type.PLAIN);
		panelContent.setText(SAMPLE_CONTENT);
		panelContentRO.setText(SAMPLE_CONTENT);
		tfHeading.setText(SAMPLE_HEADER);

		buildUI();
	}

	/**
	 * Set up the WPanel so that the appropriate items are visible based on configuration settings.
	 */
	public void updateUI() {
		if (!Util.empty(panelContent.getText())) {
			panelContentRO.setData(panelContent.getData());
		} else {
			panelContentRO.setText(SAMPLE_CONTENT);
		}

		panel.setType((WPanel.Type) panelType.getSelected());

		String headingText = tfHeading.getText();
		if (!Util.empty(tfHeading.getText())) {
			heading.setText(tfHeading.getText());
			panel.setTitleText(headingText);
		} else {
			heading.setText(SAMPLE_HEADER);
			panel.setTitleText(SAMPLE_TITLE_TEXT);
		}
	}

	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		boolean isHeader = panel.getType() == WPanel.Type.HEADER;
		heading.setVisible(isHeader);
		utilBar.setVisible(isHeader && showUtilBar.isSelected());
		menu.setVisible(isHeader && showMenu.isSelected());
		panelContentRO.setVisible(!isHeader);
	}

	/**
	 * Add the components in the required order.
	 */
	private void buildUI() {
		buildTargetPanel();
		buildConfigOptions();
		add(new WHorizontalRule());
		add(panel);
		add(new WHorizontalRule());

		// We need this reflection of the selected menu item just so we can reuse the menu from the
		// MenuBarExample. It serves no purpose in this example so I am going to hide it.
		WPanel hiddenPanel = new WPanel() {
			@Override
			public boolean isHidden() {
				return true;
			}
		};
		hiddenPanel.add(selectedMenuText);
		add(hiddenPanel);
		add(new WAjaxControl(applyConfigButton, panel));
		buildSubordinates();
	}

	/**
	 * Set up the UI for the configuration options.
	 */
	private void buildConfigOptions() {
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setMargin(new Margin(0, 0, 12, 0));
		layout.addField("Select a WPanel Type", panelType);
		contentField = layout.addField("Panel content", panelContent);
		headingField = layout.addField("heading", tfHeading);
		showMenuField = layout.addField("Show menu", showMenu);
		showUtilBarField = layout.addField("Show utility bar", showUtilBar);
		layout.addField((WLabel) null, applyConfigButton);
		add(layout);
	}

	/**
	 * Set up the target panel contents.
	 */
	private void buildTargetPanel() {
		setUpUtilBar();
		panel.add(utilBar);
		panel.add(heading);
		panel.add(panelContentRO);
		panel.add(menu);
	}

	/**
	 * The subordinate controls used to show/hide parts of the configuration options based on the selected WPanel Type.
	 */
	private void buildSubordinates() {
		WSubordinateControl control = new WSubordinateControl();
		Rule rule = new Rule();
		rule.setCondition(new Equal(panelType, WPanel.Type.HEADER));
		rule.addActionOnTrue(new Show(showUtilBarField));
		rule.addActionOnTrue(new Show(showMenuField));
		rule.addActionOnTrue(new Hide(contentField));
		rule.addActionOnFalse(new Hide(showUtilBarField));
		rule.addActionOnFalse(new Hide(showMenuField));
		rule.addActionOnFalse(new Show(contentField));
		control.addRule(rule);
		rule = new Rule();
		rule.setCondition(
			new Or(
				new Equal(panelType, WPanel.Type.CHROME),
				new Equal(panelType, WPanel.Type.ACTION),
				new Equal(panelType, WPanel.Type.HEADER)
			));
		rule.addActionOnTrue(new Show(headingField));
		rule.addActionOnFalse(new Hide(headingField));
		control.addRule(rule);
		add(control);
	}

	/**
	 * Add some UI to a "utility bar" type structure.
	 */
	private void setUpUtilBar() {
		utilBar.setLayout(new ListLayout(ListLayout.Type.FLAT, ListLayout.Alignment.RIGHT, ListLayout.Separator.NONE, false));
		WTextField selectOther = new WTextField();
		selectOther.setToolTip("Enter text.");
		utilBar.add(selectOther);
		utilBar.add(new WButton("Go"));
		utilBar.add(new WButton("A"));
		utilBar.add(new WButton("B"));
		utilBar.add(new WButton("C"));
		utilBar.setVisible(false);
	}

}
