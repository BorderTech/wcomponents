/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.util.HtmlClassUtil;

/**
 * An example showing how to use {@link WStyledText} to create icons using
 * <a href="https://fortawesome.github.io/Font-Awesome/">Font Awesome</a>.
 *
 * @author Mark Reeves
 * @since 2016-03-01
 */
public class HtmlClassUtilExample extends WPanel {

	/**
	 * Create the example.
	 */
	public HtmlClassUtilExample() {

		add(new WHeading(HeadingLevel.H2, "Center aligned text"));
		WPanel classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.add(new WText("Some centered content."));
		classedPanel.setHtmlClass(HtmlClassUtil.HtmlClassName.ALIGN_CENTER);

		add(new WHeading(HeadingLevel.H2, "Right aligned text"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.add(new WText("Some right aligned content."));
		classedPanel.setHtmlClass(HtmlClassUtil.HtmlClassName.ALIGN_RIGHT);


		add(new WHeading(HeadingLevel.H2, "Left aligned text inside centered text"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.setHtmlClass(HtmlClassUtil.HtmlClassName.ALIGN_CENTER);
		classedPanel.add(new WText("Some centered content."));
		WPanel innerPanel = new WPanel();
		classedPanel.add(innerPanel);
		innerPanel.add(new WText("Some left aligned content."));
		innerPanel.setHtmlClass(HtmlClassUtil.HtmlClassName.ALIGN_LEFT);
		classedPanel.add(new WText("Some more centered content."));


		add(new WHeading(HeadingLevel.H2, "scrolling panel"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.setHtmlClass(HtmlClassUtil.HtmlClassName.HORIZONTAL_SCROLL);
		classedPanel.add(new WText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));


		add(new WHeading(HeadingLevel.H2, "Panel out of viewport"));
		classedPanel = new WPanel(WPanel.Type.BOX);
		add(classedPanel);
		classedPanel.setHtmlClass(HtmlClassUtil.HtmlClassName.OFF_SCREEN);
		classedPanel.add(new WText("Some out of viewport content."));


		add(new WHeading(HeadingLevel.H2, "included icons"));
		add(new WHeading(HeadingLevel.H3, "Help icons"));
		WButton button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Help");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_HELP);
		add(button);
		button = new WButton("Help");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_HELP_BEFORE);
		add(button);
		button = new WButton("Help");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_HELP_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Info icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Information");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_INFO);
		add(button);
		button = new WButton("Information");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_INFO_BEFORE);
		add(button);
		button = new WButton("Information");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_INFO_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Warning icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Warning");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_WARN);
		add(button);
		button = new WButton("Warning");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_WARN_BEFORE);
		add(button);
		button = new WButton("Warning");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_WARN_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Error icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Error");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_ERROR);
		add(button);
		button = new WButton("Error");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_ERROR_BEFORE);
		add(button);
		button = new WButton("Error");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_ERROR_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Success icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Success");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SUCCESS);
		add(button);
		button = new WButton("Success");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SUCCESS_BEFORE);
		add(button);
		button = new WButton("Success");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SUCCESS_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Add icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Add");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_ADD);
		add(button);
		button = new WButton("Add");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_ADD_BEFORE);
		add(button);
		button = new WButton("Add");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_ADD_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Delete icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Delete");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_DELETE);
		add(button);
		button = new WButton("Delete");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_DELETE_BEFORE);
		add(button);
		button = new WButton("Delete");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_DELETE_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Edit icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Edit");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_EDIT);
		add(button);
		button = new WButton("Edit");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_EDIT_BEFORE);
		add(button);
		button = new WButton("Edit");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_EDIT_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Save icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Save");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SAVE);
		add(button);
		button = new WButton("Save");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SAVE_BEFORE);
		add(button);
		button = new WButton("Save");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SAVE_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Search icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Search");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SEARCH);
		add(button);
		button = new WButton("Search");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SEARCH_BEFORE);
		add(button);
		button = new WButton("Search");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_SEARCH_AFTER);
		add(button);

		add(new WHeading(HeadingLevel.H3, "Cancel icons"));
		button = new WButton("\u200b"); // \u200b is a zero-width space.
		button.setToolTip("Cancel");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_CANCEL);
		add(button);
		button = new WButton("Cancel");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_CANCEL_BEFORE);
		add(button);
		button = new WButton("Cancel");
		button.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_CANCEL_AFTER);
		add(button);


		add(new WHeading(HeadingLevel.H2, "Non-standard icons"));
		add(new WHeading(HeadingLevel.H3, "Before text content"));
		WStyledText text = new WStyledText("Fort Awesome");
		add(text);
		text.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_BEFORE.toString() + " fa-fort-awesome");
		add(new WHeading(HeadingLevel.H3, "After text content"));
		text = new WStyledText("Fort Awesome");
		add(text);
		text.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON_AFTER.toString() + " fa-fort-awesome");
		add(new WHeading(HeadingLevel.H3, "As content"));
		text = new WStyledText("\u200b");
		add(text);
		text.setHtmlClass(HtmlClassUtil.HtmlClassName.ICON.toString() + " fa-fort-awesome");

	}
}
