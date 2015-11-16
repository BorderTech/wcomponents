package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WProgressBar;
import com.github.bordertech.wcomponents.WSkipLinks;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * <p>
 * This example demonstrates the {@link WSkipLinks} component.</p>
 *
 * <p>
 * WSkipLinks is a component which has a heading and a list of internal links. These links will move the cursor point to
 * the first focusable element within a WPanel which has an accessKey set.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSkipLinksExample extends WPanel {

	/**
	 * Creates a WSkipLinksExample.
	 */
	public WSkipLinksExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL, 0, 10));
		//note: the WSKipLinks component is actually added to the ancestor WApplication
		//and is invoked by the presence of WPanel with an accessKey.

		add(new WHeading(WHeading.SECTION, "WPanel skip-links targets"));

		add(buildPanel("Panel One Title", '1'));
		add(buildPanel("Panel 2 - no access key is set on this panel"));
		add(buildPanel("Panel 1 title - access key 'x'", 'x'));
	}

	/**
	 * Creates a panel for the example.
	 *
	 * @param title the panel title.
	 * @return a panel for use in the example.
	 */
	private WPanel buildPanel(final String title) {
		WPanel panel = new WPanel(WPanel.Type.CHROME);
		panel.setTitleText(title);
		WProgressBar progress = new WProgressBar(18);
		progress.setValue(15);
		panel.add(progress);
		panel.add(new WHorizontalRule());

		WTextField input = new WTextField();
		WLabel label = new WLabel("Text input", input);
		panel.add(label);
		panel.add(input);

		return panel;
	}

	/**
	 * Creates a panel for the example.
	 *
	 * @param title the panel title.
	 * @param accessKey the panel access key
	 * @return a panel for use in the example.
	 */
	private WPanel buildPanel(final String title, final char accessKey) {
		WPanel panel = buildPanel(title);
		panel.setAccessKey(accessKey);
		return panel;
	}
}
