package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.examples.menu.MenuBarExample;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.ListLayout;

/**
 * This class demonstrates setting a {@link WPanel} type dynamically.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPanelTypeExample extends WContainer {

	/**
	 * The Target WPanel.
	 */
	private final WPanel panel = new WPanel();

	private final WHeading heading = new WHeading(HeadingLevel.H1, "Heading");
	private final ExplanatoryText text = new ExplanatoryText("Content of the panel");
	private final WPanel utilBar = new WPanel();

	/**
	 * Construct the example.
	 */
	public WPanelTypeExample() {
		buildUI();
	}

	/**
	 * Add the components in the required order.
	 */
	private void buildUI() {
		final WText selectedMenuText = new WText();
		final WDropdown panelType = new WDropdown();
		panelType.setOptions(WPanel.Type.values());
		panelType.setSelected(WPanel.Type.PLAIN);
		//set up the refresh button
		WButton button = new WButton("Update");
		// button.setImage("/image/refresh.png");
		// button.getImageHolder().setCacheKey("eg-panelType-refresh");
		button.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				panel.setType((WPanel.Type) panelType.getSelected());

				boolean isHeader = panel.getType() == WPanel.Type.HEADER;
				heading.setVisible(isHeader);
				utilBar.setVisible(isHeader);
				text.setVisible(!isHeader);
			}
		});

		setUpUtilBar();

		//a holder for the label, dropdown and refresh button
		WFieldLayout layout = new WFieldLayout();
		layout.setMargin(new Margin(0, 0, 12, 0));
		layout.addField("Select a WPanel Type", panelType);
		layout.addField((WLabel) null, button);
		//set up the target panel and its contents
		panel.setTitleText("Panel title");
		panel.setType(WPanel.Type.PLAIN);
		panel.add(utilBar);
		panel.add(heading);
		panel.add((new MenuBarExample(selectedMenuText)).getMenu());
		panel.add(text);

		add(layout);
		add(panel);
		add(selectedMenuText);
		add(new WAjaxControl(button, panel));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request); //To change body of generated methods, choose Tools | Templates.
		boolean isHeader = panel.getType() == WPanel.Type.HEADER;
		heading.setVisible(isHeader);
		utilBar.setVisible(isHeader);
		text.setVisible(!isHeader);
	}

	/**
	 * Add some UI to a utility bar.
	 */
	private void setUpUtilBar() {
		utilBar.setLayout(new ListLayout(ListLayout.Type.FLAT, ListLayout.Alignment.RIGHT, ListLayout.Separator.NONE,
				false));
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
