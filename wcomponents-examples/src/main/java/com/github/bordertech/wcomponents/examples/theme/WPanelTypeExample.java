package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.menu.MenuBarExample;
import com.github.bordertech.wcomponents.layout.FlowLayout;

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

	/**
	 * Construct the example.
	 */
	public WPanelTypeExample() {
		final WText selectedMenuText = new WText();
		final WDropdown panelType = new WDropdown();
		panelType.setOptions(WPanel.Type.values());
		panelType.setSelected(WPanel.Type.PLAIN);
		//set up the refresh button
		WButton button = new WButton("Update");
		button.setImage("/image/refresh.png");
		button.getImageHolder().setCacheKey("eg-panelType-refresh");
		button.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				panel.setType((WPanel.Type) panelType.getSelected());
			}
		});

		//a holder for the label, dropdown and refresh button
		WPanel layoutPanel = new WPanel();
		add(layoutPanel);
		layoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0,
				FlowLayout.ContentAlignment.BASELINE));
		layoutPanel.setMargin(new Margin(0, 0, 12, 0));
		WLabel selectLabel = new WLabel("Select a WPanel Type", panelType);
		layoutPanel.add(selectLabel);
		layoutPanel.add(panelType);
		layoutPanel.add(button);

		//set up the target panel and its contents
		panel.setTitleText("Panel title");
		panel.setType(WPanel.Type.PLAIN);
		panel.add((new MenuBarExample(selectedMenuText)).getMenu());
		panel.add(new WText("Content of Panel"));

		add(panel);
		add(selectedMenuText);
	}
}
