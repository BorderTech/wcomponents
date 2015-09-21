package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.layout.FlowLayout.ContentAlignment;

/**
 * <p>
 * This example demonstrates the {@link FlowLayout} layout.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class FlowLayoutExample extends WPanel {

	/**
	 * Creates a FlowLayoutExample.
	 */
	public FlowLayoutExample() {
		add(new WHeading(WHeading.TITLE, "Flow layout examples"));
		add(new WHorizontalRule());

		// Left, no gap
		WPanel flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.LEFT));

		add(new WHeading(WHeading.SECTION, "Flow layout - left, no gap"));
		add(flowPanel);
		addBoxes(flowPanel, 8);

		// Left, with gap
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.LEFT, 5, 0));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - left, horizontal gap 5"));
		add(flowPanel);
		flowPanel.add(new WButton("Button 1 text"));
		flowPanel.add(new WButton("B2"));
		flowPanel.add(new WButton("Button 3"));
		flowPanel.add(new WButton("B4"));

		// Right, no gap
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.RIGHT));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - right, no gap"));
		add(flowPanel);
		addBoxes(flowPanel, 8);

		// Right, with gap
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.RIGHT, 5, 0));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - right, horizontal gap 5"));
		add(flowPanel);
		flowPanel.add(new WButton("Button 1 text"));
		flowPanel.add(new WButton("B2"));
		flowPanel.add(new WButton("Button 3"));
		flowPanel.add(new WButton("B4"));

		// Vertical
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.VERTICAL));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - vertical"));
		add(flowPanel);
		addBoxes(flowPanel, 5);

		// Center
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, 5, 0));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - center, horizontal gap 5"));
		add(flowPanel);
		addBoxes(flowPanel, 5);
		flowPanel.add(new WText("And some more text"));

		// Mixed (Outer center, center inner)
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - center + vertical, vertical gap 5"));
		add(flowPanel);

		WPanel inner = new WPanel();
		flowPanel.add(inner);
		inner.setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));
		addBoxes(inner, 5);

		// Content Alignment - TOP
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, 5, 0, ContentAlignment.TOP));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - center, horizontal gap 5, TOP"));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

		// Content Alignment - MIDDLE
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, 5, 0, ContentAlignment.MIDDLE));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - center, horizontal gap 5, MIDDLE"));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

		// Content Alignment - BASELINE
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, 5, 0, ContentAlignment.BASELINE));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - center, horizontal gap 5, BASELINE"));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

		// Content Alignment - BOTTOM
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, 5, 0, ContentAlignment.BOTTOM));

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Flow layout - center, horizontal gap 5, BOTTOM"));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

	}

	/**
	 * Adds a set of boxes to the given panel.
	 *
	 * @param panel the panel to add the boxes to.
	 * @param amount the number of boxes to add.
	 */
	private static void addBoxes(final WPanel panel, final int amount) {
		for (int i = 1; i <= amount; i++) {
			WPanel box = new WPanel(WPanel.Type.BOX);
			box.add(new WText(Integer.toString(i)));
			panel.add(box);
		}
	}

	/**
	 * Adds a set of boxes to the given panel.
	 *
	 * @param panel the panel to add the boxes to.
	 * @param amount the number of boxes to add.
	 */
	private static void addBoxesWithDiffContent(final WPanel panel, final int amount) {
		for (int i = 1; i <= amount; i++) {
			WPanel content = new WPanel(WPanel.Type.BOX);
			content.setLayout(new FlowLayout(FlowLayout.VERTICAL, 0, 3));
			for (int j = 1; j <= i; j++) {
				content.add(new WText(Integer.toString(i)));
			}
			panel.add(content);
		}
	}

}
