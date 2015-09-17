package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WWindow;
import com.github.bordertech.wcomponents.examples.datatable.TreeTableExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWButtonExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWDropdownExample;

/**
 * An example showing {@link WWindow} usage.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WWindowExample extends WContainer {

	/**
	 * Creates a WindowExample.
	 */
	public WWindowExample() {
		final WWindow window1 = new WWindow(new TreeTableExample());
		window1.setTitle("Functional content, round-trip");
		window1.setScrollable(true);

		WButton button1 = new WButton("Show window");
		button1.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				window1.display();
			}
		});

		add(new WHeading(WHeading.SECTION, "Window with functional content, round-trip"));
		add(button1);
		add(window1);

		final WPanel ajaxExamples = new WPanel();
		ajaxExamples.add(new AjaxWDropdownExample());
		ajaxExamples.add(new AjaxWButtonExample());
		ajaxExamples.add(new WButton("submit"));
		final WWindow window2 = new WWindow(ajaxExamples);
		window2.setTitle("Functional content, AJAX-enabled");
		window2.setScrollable(true);

		WButton button2 = new WButton("Show window");
		button2.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				window2.display();
			}
		});

		add(new WHeading(WHeading.SECTION, "Window with functional content, AJAX-enabled"));
		add(button2);
		add(window2);

		final WWindow window3 = new WWindow(new WContentExample());
		window3.setTitle("Nested targetable content");
		window3.setHeight(200);
		window3.setWidth(400);
		window3.setResizable(false);

		WButton button3 = new WButton("Show window");
		button3.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				window3.display();
			}
		});

		add(new WHeading(WHeading.SECTION, "Nested targetable content"));
		add(button3);
		add(window3);

		final WWindow window4 = new WWindow(new WApplicationExample());
		window4.setTitle("Window that uses change flag");
		window4.setHeight(200);
		window4.setWidth(400);
		window4.setResizable(false);

		WButton button4 = new WButton("Show window");
		button4.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				window4.display();
			}
		});

		add(new WHeading(WHeading.SECTION, "Window that uses change flag"));
		add(button4);
		add(window4);

		add(new WHorizontalRule());
		add(new WButton("Refresh"));
	}
}
