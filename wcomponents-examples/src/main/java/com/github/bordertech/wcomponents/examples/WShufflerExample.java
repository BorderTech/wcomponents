package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WShuffler;
import com.github.bordertech.wcomponents.WText;
import java.util.Arrays;

/**
 * Demonstrate how the {@link WShuffler} component can be used.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WShufflerExample extends WContainer {

	/**
	 * Shuffler component.
	 */
	private final WShuffler shuffler = new WShuffler();
	/**
	 * Text field to display the options.
	 */
	private final WText order = new WText();

	/**
	 * Construct the example.
	 */
	public WShufflerExample() {
		add(new WHeading(WHeading.SECTION, "WShuffler examples"));

		WFieldLayout layout = new WFieldLayout();
		add(layout);
		WContainer panel = new WContainer();
		shuffler.setOptions(Arrays.asList(new String[]{"A", "B", "C", "D", "E"}));
		shuffler.setToolTip("Shuffle the options");
		panel.add(shuffler);
		panel.add(order);
		layout.addField("Shuffler", panel);

		WShuffler shuffler2 = new WShuffler(Arrays.asList(new String[]{"X", "Y", "Z"}));
		shuffler2.setReadOnly(true);
		shuffler2.setToolTip("Example of readonly shuffler");
		layout.addField("Readonly shuffler", shuffler2);

		shuffler2 = new WShuffler(Arrays.asList(new String[]{"X", "Y", "Z"}));
		shuffler2.setDisabled(true);
		shuffler2.setToolTip("Example of disabled shuffler");
		layout.addField("Disabled shuffler", shuffler2);

		shuffler2 = new WShuffler(Arrays.asList(new String[]{"X", "Y", "Z"}));
		shuffler2.setRows(10);
		shuffler2.setToolTip("Example of shuffler with rows attribute");
		layout.addField("Shuffler with rows attribute", shuffler2);

		shuffler2 = new WShuffler();
		shuffler2.setToolTip("Example of shuffler with no options");
		layout.addField("Shuffler with no options", shuffler2);

		add(new WButton("Submit"));
	}

	/**
	 * Concatenate the options to display in the text field.
	 *
	 * @param request the current request being processed
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		StringBuffer text = new StringBuffer("Options: ");

		for (Object option : shuffler.getOptions()) {
			text.append(option).append(", ");
		}

		order.setText(text.toString());
	}
}
