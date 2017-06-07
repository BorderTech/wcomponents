package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Size;
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
 * @author Mark Reeves
 * @since 1.0.0
 */
public class FlowLayoutExample extends WPanel {
	/**
	 * A nice readable space.
	 */
	private static final Size SPACE = Size.MEDIUM;

	/**
	 * Creates a FlowLayoutExample.
	 */
	public FlowLayoutExample() {
		add(new WHeading(HeadingLevel.H2, "Flow layout examples"));

		// default constructor
		WPanel flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout());
		add(flowPanel);
		addBoxes(flowPanel, 5);

		// Left, no gap
		add(new WHeading(HeadingLevel.H3, "Flow layout - left, no gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.LEFT));
		add(flowPanel);
		addBoxes(flowPanel, 8);

		// Left, with gap
		add(new WHeading(HeadingLevel.H3, "Flow layout - left, horizontal gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.LEFT, SPACE));
		add(flowPanel);
		flowPanel.add(new WButton("Button 1 text"));
		flowPanel.add(new WButton("B2"));
		flowPanel.add(new WButton("Button 3"));
		flowPanel.add(new WButton("B4"));

		// Right, no gap
		add(new WHeading(HeadingLevel.H3, "Flow layout - right, no gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.RIGHT));
		add(flowPanel);
		addBoxes(flowPanel, 8);

		// Right, with gap
		add(new WHeading(HeadingLevel.H3, "Flow layout - right, horizontal gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.RIGHT, SPACE));
		add(flowPanel);
		flowPanel.add(new WButton("Button 1 text"));
		flowPanel.add(new WButton("B2"));
		flowPanel.add(new WButton("Button 3"));
		flowPanel.add(new WButton("B4"));

		// Vertical
		add(new WHeading(HeadingLevel.H3, "Flow layout - vertical"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.VERTICAL));
		add(flowPanel);
		addBoxes(flowPanel, 5);

		// Center
		add(new WHeading(HeadingLevel.H3, "Flow layout - center, horizontal gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, SPACE));
		add(flowPanel);
		addBoxes(flowPanel, 5);
		flowPanel.add(new WText("And some more text"));

		// Mixed (Outer center, center inner)

		add(new WHeading(HeadingLevel.H3, "Flow layout - center + vertical, vertical gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER));
		add(flowPanel);

		WPanel inner = new WPanel();
		flowPanel.add(inner);
		inner.setLayout(new FlowLayout(Alignment.VERTICAL, SPACE));
		addBoxes(inner, 5);

		// Content Alignment - TOP
		add(new WHeading(HeadingLevel.H3, "Flow layout - center, horizontal gap, TOP"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, SPACE, ContentAlignment.TOP));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

		// Content Alignment - MIDDLE
		add(new WHeading(HeadingLevel.H3, "Flow layout - center, horizontal gap, MIDDLE"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, SPACE, ContentAlignment.MIDDLE));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

		// Content Alignment - BASELINE
		add(new WHeading(HeadingLevel.H3, "Flow layout - center, horizontal gap, BASELINE"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, SPACE, ContentAlignment.BASELINE));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

		// Content Alignment - BOTTOM
		add(new WHeading(HeadingLevel.H3, "Flow layout - center, horizontal gap, BOTTOM"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.CENTER, SPACE, ContentAlignment.BOTTOM));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));

		// deprecated constructors
		add(new WHorizontalRule());
		add(new WHeading(HeadingLevel.H2, "Checks of deprecated constructors"));
		// These are here to check we have not broken the old constructors.

		add(new WHeading(HeadingLevel.H3, "Flow layout - left, horizontal gap and ignored vertical gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.LEFT, 6, 12));
		add(flowPanel);
		addBoxes(flowPanel, 12);
		flowPanel.add(new WText("some text"));
		flowPanel.add(new WText("some more text"));

		add(new WHeading(HeadingLevel.H3, "Vertical, ignored horizontal gap and implemented vertical gap"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.VERTICAL, 6, 12));
		add(flowPanel);
		addBoxes(flowPanel, 5);
		flowPanel.add(new WText("some text"));


		add(new WHeading(HeadingLevel.H3, "Left, horizontal gap and ignored vertical gap, content align bottom"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.LEFT, 6, 12, ContentAlignment.BOTTOM));
		add(flowPanel);
		addBoxesWithDiffContent(flowPanel, 5);
		flowPanel.add(new WText("some text"));
		flowPanel.add(new WText("some more text"));


		add(new WHeading(HeadingLevel.H3, "VERTICAL, ignored horizontal gap, vertical gap, ignored content align bottom"));
		flowPanel = new WPanel();
		flowPanel.setLayout(new FlowLayout(Alignment.VERTICAL, 6, 12, ContentAlignment.BOTTOM));
		add(flowPanel);
		addBoxes(flowPanel, 5);
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
			content.setLayout(new FlowLayout(FlowLayout.VERTICAL, 3));
			for (int j = 1; j <= i; j++) {
				content.add(new WText(Integer.toString(i)));
			}
			panel.add(content);
		}
	}

}
