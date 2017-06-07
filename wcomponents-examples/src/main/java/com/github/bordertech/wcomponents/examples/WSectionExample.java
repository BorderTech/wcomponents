package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSection;
import com.github.bordertech.wcomponents.WSection.SectionMode;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.examples.menu.MenuBarExample;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * Demonstrate how {@link WSection} can be used.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSectionExample extends WContainer {

	/**
	 * Default box margin.
	 */
	private static final Margin DEFAULT_BOX_MARGIN = new Margin(Size.ZERO, Size.ZERO, Size.MEDIUM, Size.ZERO);

	/**
	 * Default example margin.
	 */
	private static final Margin DEFAULT_MARGIN = new Margin(Size.XL);

	/**
	 * Build example.
	 */
	public WSectionExample() {
		// Basic Section
		WText selectedMenuText = new WText();
		add(selectedMenuText);
		WPanel box = new WPanel(WPanel.Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		add(box);

		WSection section = new WSection("Basic Section");
		box.add(section);
		MenuBarExample mbEx = new MenuBarExample(selectedMenuText);
		section.getContent().add(mbEx.getMenu());
		section.getContent().add(new WText("Hello World"));
		section.setMargin(DEFAULT_MARGIN);

		WButton help = new WButton("");
		help.setImage("/image/help.png");
		//help.setRenderAsLink(true);
		help.setToolTip("Help");
		section.getDecoratedLabel().setTail(help);

		// Eager Section
		box = new WPanel(WPanel.Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		add(box);
		section = new WSection("Eager Section");
		section.setMode(SectionMode.EAGER);
		section.setMargin(DEFAULT_MARGIN);
		box.add(section);
		section.getContent().add(new WText("Hello World"));

		// Lazy Section
		box = new WPanel(WPanel.Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		add(box);

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		box.add(layout);

		WCheckBox chb = new WCheckBox();
		layout.addField("Show and hide lazy section", chb);

		section = new WSection("Lazy Section");
		section.setMode(SectionMode.LAZY);
		section.setMargin(DEFAULT_MARGIN);
		section.getContent().add(new WText("Hello World"));
		box.add(section);

		WSubordinateControl control = new WSubordinateControl();
		box.add(control);

		control.addRule(new Rule(new Equal(chb, "true"), new Show(section), new Hide(section)));

		add(new WButton("submit"));

		section = new WSection("MenuBar in nested panel");
		add(section);

		WPanel nestedPanel = new WPanel();

		section.getContent().add(nestedPanel);
		mbEx = new MenuBarExample(selectedMenuText);
		WMenu exMenu = mbEx.getMenu();
		exMenu.addHtmlClass("wc-neg-margin");
		nestedPanel.add(exMenu);
		nestedPanel.add(new ExplanatoryText("Hello World from a nested panel"));

		section.getContent().add(new ExplanatoryText("Hello World from the top panel"));
	}

}
