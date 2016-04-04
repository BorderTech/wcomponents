/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;

/**
 * An example showing how to use {@link WStyledText} to create icons using
 * <a href="https://fortawesome.github.io/Font-Awesome/">Font Awesome</a>.
 *
 * @author Mark Reeves
 * @since 2016-03-01
 */
public class WStyledTextFontAwesomeExample extends WPanel {

	/**
	 * Create the example.
	 */
	public WStyledTextFontAwesomeExample() {
		setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL, 0, 12));

		add(new WHeading(HeadingLevel.H2, "A simple icon"));
		WStyledText text = new WStyledText("Fort Awesome");
		add(text);
		text.setHtmlClass("fa fa-fort-awesome");


		add(new WHeading(HeadingLevel.H2, "Icons at various sizes"));

		text = new WStyledText("Large");
		add(text);
		text.setHtmlClass("fa fa-fort-awesome fa-lg");

		text = new WStyledText("2x");
		add(text);
		text.setHtmlClass("fa fa-fort-awesome fa-2x");

		text = new WStyledText("3x");
		add(text);
		text.setHtmlClass("fa fa-fort-awesome fa-3x");

		text = new WStyledText("4x");
		add(text);
		text.setHtmlClass("fa fa-fort-awesome fa-4x");

		text = new WStyledText("5x");
		add(text);
		text.setHtmlClass("fa fa-fort-awesome fa-500px fa-5x");

		add(new WHeading(HeadingLevel.H2, "Animated icon"));

		WContainer wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-cog fa-spin");
		wrapper.add(new WText("Spin"));

		wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-cog fa-pulse");
		wrapper.add(new WText("Pulse"));


		add(new WHeading(HeadingLevel.H2, "Rotated and flipped 2x icons"));

		wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-comment-o fa-2x");
		wrapper.add(new WText("Normal"));

		wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-comment-o fa-2x fa-rotate-90");
		wrapper.add(new WText("Rotate 90º"));

		wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-comment-o fa-2x fa-rotate-180");
		wrapper.add(new WText("Rotate 180º"));

		wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-comment-o fa-2x fa-rotate-270");
		wrapper.add(new WText("Rotate 270º"));

		wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-comment-o fa-2x fa-flip-horizontal");
		wrapper.add(new WText("Flip horizontal"));

		wrapper = new WContainer();
		add(wrapper);
		text = new WStyledText(" ");
		wrapper.add(text);
		text.setHtmlClass("fa fa-comment-o fa-2x fa-flip-vertical");
		wrapper.add(new WText("Flip vertical"));


		add(new WHeading(HeadingLevel.H2, "Stacked icons"));
		WTemplate template = new WTemplate("/com/github/bordertech/wcomponents/examples/iconStack.moustache", TemplateRendererFactory.TemplateEngine.HANDLEBARS);
		add(template);
		text = new WStyledText(" ");
		text.setHtmlClass("fa fa-camera fa-stack-1x");
		template.addTaggedComponent("lower", text);
		text = new WStyledText(" ");
		text.setHtmlClass("fa fa-ban fa-stack-2x text-ban");
		template.addTaggedComponent("upper", text);
	}
}
