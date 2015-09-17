package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.BorderLayout;

/**
 * <p>
 * This example demonstrates the {@link BorderLayout} layout.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class BorderLayoutExample extends WContainer {

	/**
	 * Some dummy text.
	 */
	private static final String DUMMY_TEXT = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. In tristique pellentesque massa, et placerat justo ullamcorper vel. Nunc scelerisque, sem ut hendrerit pharetra, tellus erat dictum felis, at facilisis metus odio ac justo. Curabitur rutrum lacus in nulla iaculis at vestibulum metus facilisis. Aenean id nulla massa. Suspendisse vitae nunc nec urna laoreet elementum. Duis in orci ac leo elementum sagittis ac non massa. Sed vel massa purus, eu facilisis ipsum.</p><p>Maecenas quis mi non metus scelerisque sagittis quis ac lacus. Fusce faucibus, urna ut viverra vulputate, tellus metus venenatis enim, eget mollis neque libero a turpis. Nullam convallis, lacus vel gravida suscipit, ipsum ante interdum libero, placerat laoreet dui magna et odio.\n\nPhasellus interdum placerat risus ut aliquam. In hac habitasse platea dictumst. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.</p>";

	/**
	 * Creates a BorderLayoutExample.
	 */
	public BorderLayoutExample() {
		// All - fill cells, no gap
		WPanel borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout());

		add(new WHeading(WHeading.SECTION, "Border layout - expandable content, no gap"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(new BoxComponent("North"), BorderLayout.NORTH);
		borderLayoutPanel.add(new BoxComponent("South"), BorderLayout.SOUTH);
		borderLayoutPanel.add(new BoxComponent("East"), BorderLayout.EAST);
		borderLayoutPanel.add(new BoxComponent("West"), BorderLayout.WEST);
		borderLayoutPanel.add(new BoxComponent("Center"), BorderLayout.CENTER);

		// All - fill cells, hgap 2, vgap 5
		borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout(2, 5));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION,
				"Border layout - expandable content, horizontal gap 2, vertical gap 5"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(new BoxComponent("North"), BorderLayout.NORTH);
		borderLayoutPanel.add(new BoxComponent("South"), BorderLayout.SOUTH);
		borderLayoutPanel.add(new BoxComponent("East"), BorderLayout.EAST);
		borderLayoutPanel.add(new BoxComponent("West"), BorderLayout.WEST);
		borderLayoutPanel.add(new BoxComponent("Center"), BorderLayout.CENTER);

		// All - small content
		borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout());

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Border layout - small content, no gap"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(new WText("North"), BorderLayout.NORTH);
		borderLayoutPanel.add(new WText("South"), BorderLayout.SOUTH);
		borderLayoutPanel.add(new WText("East"), BorderLayout.EAST);
		borderLayoutPanel.add(new WText("West"), BorderLayout.WEST);
		borderLayoutPanel.add(new WText("Center"), BorderLayout.CENTER);

		// All - large amount of text content
		borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout());

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Border layout - large amount of content"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(createPanelWithText("North", DUMMY_TEXT), BorderLayout.NORTH);
		borderLayoutPanel.add(createPanelWithText("South", DUMMY_TEXT), BorderLayout.SOUTH);
		borderLayoutPanel.add(createPanelWithText("East", DUMMY_TEXT), BorderLayout.EAST);
		borderLayoutPanel.add(createPanelWithText("West", DUMMY_TEXT), BorderLayout.WEST);
		borderLayoutPanel.add(createPanelWithText("Center", DUMMY_TEXT), BorderLayout.CENTER);

		// North only
		borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout());

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Border layout - north only"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(createPanelWithText("North", "This panel only has a northern cell."),
				BorderLayout.NORTH);

		// East / west
		borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout());

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Border layout - east/west only"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(createPanelWithText("East", "This is in the eastern cell."),
				BorderLayout.EAST);
		borderLayoutPanel.add(createPanelWithText("West", "This is in the western cell."),
				BorderLayout.WEST);

		// North / center / south
		borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout());

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Border layout - north/center/south only"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(createPanelWithText("North", "This is in the northern cell."),
				BorderLayout.NORTH);
		borderLayoutPanel.add(createPanelWithText("Center", "This is in the center cell."),
				BorderLayout.CENTER);
		borderLayoutPanel.add(createPanelWithText("South", "This is in the southern cell."),
				BorderLayout.CENTER);

		// North / center / east
		borderLayoutPanel = new WPanel();
		borderLayoutPanel.setLayout(new BorderLayout());

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Border layout - north/center/east only"));
		add(borderLayoutPanel);
		borderLayoutPanel.add(createPanelWithText("North", "This is in the northern cell."),
				BorderLayout.NORTH);
		borderLayoutPanel.add(createPanelWithText("Center", "This is in the center cell."),
				BorderLayout.CENTER);
		borderLayoutPanel.add(createPanelWithText("East", "This is in the eastern cell."),
				BorderLayout.EAST);
	}

	/**
	 * Convenience method to create a WPanel with the given title and text.
	 *
	 * @param title the panel title.
	 * @param text the panel text.
	 * @return a new WPanel with the given title and text.
	 */
	private WPanel createPanelWithText(final String title, final String text) {
		WPanel panel = new WPanel(WPanel.Type.CHROME);
		panel.setTitleText(title);
		WText textComponent = new WText(text);
		textComponent.setEncodeText(false);
		panel.add(textComponent);

		return panel;
	}
}
