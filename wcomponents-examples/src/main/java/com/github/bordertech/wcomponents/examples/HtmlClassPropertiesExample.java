package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WFigure;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
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


		add(new WHeading(HeadingLevel.H2, "included icons"));
		add(new WHeading(HeadingLevel.H3, "Help icons"));
		WButton button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Help");
		button.setHtmlClass(HtmlClassProperties.ICON_HELP);
		add(button);
		button = new WButton("Help");
		button.setHtmlClass(HtmlClassProperties.ICON_HELP_BEFORE);
		add(button);
		button = new WButton("Help");
		button.setHtmlClass(HtmlClassProperties.ICON_HELP_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Info icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Information");
		button.setHtmlClass(HtmlClassProperties.ICON_INFO);
		add(button);
		button = new WButton("Information");
		button.setHtmlClass(HtmlClassProperties.ICON_INFO_BEFORE);
		add(button);
		button = new WButton("Information");
		button.setHtmlClass(HtmlClassProperties.ICON_INFO_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Warning icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Warning");
		button.setHtmlClass(HtmlClassProperties.ICON_WARN);
		add(button);
		button = new WButton("Warning");
		button.setHtmlClass(HtmlClassProperties.ICON_WARN_BEFORE);
		add(button);
		button = new WButton("Warning");
		button.setHtmlClass(HtmlClassProperties.ICON_WARN_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Error icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Error");
		button.setHtmlClass(HtmlClassProperties.ICON_ERROR);
		add(button);
		button = new WButton("Error");
		button.setHtmlClass(HtmlClassProperties.ICON_ERROR_BEFORE);
		add(button);
		button = new WButton("Error");
		button.setHtmlClass(HtmlClassProperties.ICON_ERROR_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Success icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Success");
		button.setHtmlClass(HtmlClassProperties.ICON_SUCCESS);
		add(button);
		button = new WButton("Success");
		button.setHtmlClass(HtmlClassProperties.ICON_SUCCESS_BEFORE);
		add(button);
		button = new WButton("Success");
		button.setHtmlClass(HtmlClassProperties.ICON_SUCCESS_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Add icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Add");
		button.setHtmlClass(HtmlClassProperties.ICON_ADD);
		add(button);
		button = new WButton("Add");
		button.setHtmlClass(HtmlClassProperties.ICON_ADD_BEFORE);
		add(button);
		button = new WButton("Add");
		button.setHtmlClass(HtmlClassProperties.ICON_ADD_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Delete icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Delete");
		button.setHtmlClass(HtmlClassProperties.ICON_DELETE);
		add(button);
		button = new WButton("Delete");
		button.setHtmlClass(HtmlClassProperties.ICON_DELETE_BEFORE);
		add(button);
		button = new WButton("Delete");
		button.setHtmlClass(HtmlClassProperties.ICON_DELETE_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Edit icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Edit");
		button.setHtmlClass(HtmlClassProperties.ICON_EDIT);
		add(button);
		button = new WButton("Edit");
		button.setHtmlClass(HtmlClassProperties.ICON_EDIT_BEFORE);
		add(button);
		button = new WButton("Edit");
		button.setHtmlClass(HtmlClassProperties.ICON_EDIT_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Save icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Save");
		button.setHtmlClass(HtmlClassProperties.ICON_SAVE);
		add(button);
		button = new WButton("Save");
		button.setHtmlClass(HtmlClassProperties.ICON_SAVE_BEFORE);
		add(button);
		button = new WButton("Save");
		button.setHtmlClass(HtmlClassProperties.ICON_SAVE_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Search icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Search");
		button.setHtmlClass(HtmlClassProperties.ICON_SEARCH);
		add(button);
		button = new WButton("Search");
		button.setHtmlClass(HtmlClassProperties.ICON_SEARCH_BEFORE);
		add(button);
		button = new WButton("Search");
		button.setHtmlClass(HtmlClassProperties.ICON_SEARCH_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Cancel icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Cancel");
		button.setHtmlClass(HtmlClassProperties.ICON_CANCEL);
		add(button);
		button = new WButton("Cancel");
		button.setHtmlClass(HtmlClassProperties.ICON_CANCEL_BEFORE);
		add(button);
		button = new WButton("Cancel");
		button.setHtmlClass(HtmlClassProperties.ICON_CANCEL_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Menu icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Menu");
		button.setHtmlClass(HtmlClassProperties.ICON_MENU);
		add(button);
		button = new WButton("Menu");
		button.setHtmlClass(HtmlClassProperties.ICON_MENU_BEFORE);
		add(button);
		button = new WButton("Menu");
		button.setHtmlClass(HtmlClassProperties.ICON_MENU_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H2, "Non-standard icons"));
		add(new ExplanatoryText("This example shows how to add a Font Awesome icon not in the set exposed by HtmlClassProperties."));
		// using the icon helpers
		String cogIcon = "fa-cog";
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Settings");
		button.setHtmlClass(HtmlIconUtil.getIconClasses(cogIcon));
		add(button);
		button = new WButton("Settings");
		button.setHtmlClass(HtmlIconUtil.getIconClasses(cogIcon, HtmlIconUtil.IconPosition.BEFORE));
		add(button);
		button = new WButton("Settings");
		button.setHtmlClass(HtmlIconUtil.getIconClasses(cogIcon, HtmlIconUtil.IconPosition.AFTER));
		add(button);
		//
	}
}
