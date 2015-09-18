package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;

/**
 * An example showing use of the {@link WPanel} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WPanelExample extends WContainer {

	/**
	 * Creates a WPanelExample.
	 */
	public WPanelExample() {
		int index = 0;

		for (WPanel.Type panelType : WPanel.Type.values()) {
			if (index++ > 0) {
				add(new WHorizontalRule());
			}

			add(new WHeading(WHeading.SECTION, panelType.toString()));

			WPanel panel = new WPanel(panelType);
			panel.setTitleText("Panel title");

			// TODO: This is bad - use a layout instead
			WText text = new WText("Panel text<br />");
			text.setEncodeText(false);
			panel.add(text);

			WButton button = new WButton("Panel button");
			button.setRenderAsLink(true);
			panel.add(button);
			panel.setDefaultSubmitButton(button);
			add(panel);
		}
	}
}
