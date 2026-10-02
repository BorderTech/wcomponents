package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WFigure;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
import com.github.bordertech.wcomponents.util.HtmlIconUtil;

/**
 * An example showing how to use {@link HtmlClassProperties}.
 *
 * @author Mark Reeves
 * @since 1.2.1
 */
public class HtmlClassPropertiesExample extends WPanel {

	/**
	 * Create the example.
	 */
	public HtmlClassPropertiesExample() {

		add(new WHeading(HeadingLevel.H2, "Center aligned text"));
		WPanel classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.add(new WText("Some centered content."));
		classedPanel.setHtmlClass(HtmlClassProperties.ALIGN_CENTER);

		add(new WHeading(HeadingLevel.H2, "Right aligned text"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.add(new WText("Some right aligned content."));
		classedPanel.setHtmlClass(HtmlClassProperties.ALIGN_RIGHT);

		add(new WHeading(HeadingLevel.H2, "Left aligned text inside centered text"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.setHtmlClass(HtmlClassProperties.ALIGN_CENTER);
		classedPanel.add(new WText("Some centered content."));
		WPanel innerPanel = new WPanel();
		classedPanel.add(innerPanel);
		innerPanel.add(new WText("Some left aligned content."));
		innerPanel.setHtmlClass(HtmlClassProperties.ALIGN_LEFT);
		classedPanel.add(new WText("Some more centered content."));

		add(new WHeading(HeadingLevel.H2, "Bordered panel"));
		classedPanel = new WPanel(WPanel.Type.PLAIN);
		add(classedPanel);
		classedPanel.setHtmlClass(HtmlClassProperties.BORDER);
		classedPanel.add(new ExplanatoryText("This is a panel with a border. The content sits directly against the border. This is not recommended"
				+ "for a WPanel one should use WPanel.Type.BOX. Below this is a more suitable example with an image inside a WFigure."));
		WImage image = new WImage("/com/github/bordertech/wcomponents/examples/portlet-portrait.jpg", "Portrait");
		image.setHtmlClass(HtmlClassProperties.BORDER);
		add(new WFigure(image, "A sample image with a border"));

		add(new WHeading(HeadingLevel.H2, "scrolling panel"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.setHtmlClass(HtmlClassProperties.HORIZONTAL_SCROLL);
		classedPanel.add(new WText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
				+ "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));

		add(new WHeading(HeadingLevel.H2, "Panel out of viewport"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.setHtmlClass(HtmlClassProperties.OFF_SCREEN);
		classedPanel.add(new WText("Some out of viewport content."));

		add(new WHeading(HeadingLevel.H2, "Included icons"));

		add(new WHeading(HeadingLevel.H3, "Help icons"));
		add(setupButtonsDefinedIcons("Help", HtmlClassProperties.ICON_HELP));

		add(new WHeading(HeadingLevel.H3, "Info icons"));
		add(setupButtonsDefinedIcons("Information", HtmlClassProperties.ICON_INFO));

		add(new WHeading(HeadingLevel.H3, "Warning icons"));
		add(setupButtonsDefinedIcons("Warning", HtmlClassProperties.ICON_WARN));

		add(new WHeading(HeadingLevel.H3, "Error icons"));
		add(setupButtonsDefinedIcons("Error", HtmlClassProperties.ICON_ERROR));

		add(new WHeading(HeadingLevel.H3, "Success icons"));
		add(setupButtonsDefinedIcons("Success", HtmlClassProperties.ICON_SUCCESS));

		add(new WHeading(HeadingLevel.H3, "Add icons"));
		add(setupButtonsDefinedIcons("Add", HtmlClassProperties.ICON_ADD));

		add(new WHeading(HeadingLevel.H3, "Delete icons"));
		add(setupButtonsDefinedIcons("Delete", HtmlClassProperties.ICON_DELETE));

		add(new WHeading(HeadingLevel.H3, "Edit icons"));
		add(setupButtonsDefinedIcons("Edit", HtmlClassProperties.ICON_EDIT));

		add(new WHeading(HeadingLevel.H3, "Save icons"));
		add(setupButtonsDefinedIcons("Save", HtmlClassProperties.ICON_SAVE));

		add(new WHeading(HeadingLevel.H3, "Search icons"));
		add(setupButtonsDefinedIcons("Search", HtmlClassProperties.ICON_SEARCH));

		add(new WHeading(HeadingLevel.H3, "Cancel icons"));
		add(setupButtonsDefinedIcons("Cancel", HtmlClassProperties.ICON_CANCEL));

		add(new WHeading(HeadingLevel.H3, "Menu icons"));
		add(setupButtonsDefinedIcons("Menu", HtmlClassProperties.ICON_MENU));

		add(new WHeading(HeadingLevel.H2, "Non-standard icons"));
		add(new ExplanatoryText("This example shows how to add a Font Awesome icon not in the set exposed by HtmlClassProperties."));
		add(setupButtonsCustomIcons("Settings", "wc-fa fa-solid fa-gear"));

	}

	private WPanel setupButtonsDefinedIcons(final String buttonText, final HtmlClassProperties icon) {
		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, Size.MEDIUM, FlowLayout.ContentAlignment.BOTTOM));
		// Button with no position (show text as tooltip)
		WButton button = new WButton(buttonText);
		button.setImageIconClass(icon);
		panel.add(button);
		// Button with WEST
		button = new WButton(buttonText);
		button.setImageIconClass(icon);
		button.setImagePosition(WButton.ImagePosition.WEST);
		panel.add(button);
		// Button with EAST
		button = new WButton(buttonText);
		button.setImageIconClass(icon);
		button.setImagePosition(WButton.ImagePosition.EAST);
		panel.add(button);
		return panel;
	}

	private WPanel setupButtonsCustomIcons(final String buttonText, final String customIcon) {
		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, Size.MEDIUM, FlowLayout.ContentAlignment.BOTTOM));
		// Button with no position (show text as tooltip)
		WButton button = new WButton(buttonText);
		button.setImageIconClass(HtmlIconUtil.getIconClasses(customIcon));
		panel.add(button);
		// Button with WEST
		button = new WButton(buttonText);
		button.setImageIconClass(HtmlIconUtil.getIconClasses(customIcon));
		button.setImagePosition(WButton.ImagePosition.WEST);
		panel.add(button);
		// Button with EAST
		button = new WButton(buttonText);
		button.setImageIconClass(HtmlIconUtil.getIconClasses(customIcon));
		button.setImagePosition(WButton.ImagePosition.EAST);
		panel.add(button);
		return panel;
	}

}
