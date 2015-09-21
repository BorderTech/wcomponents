package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example using WMultiSelect.
 */
public class WMultiSelectExample extends WContainer {

	/**
	 * A set of shape options.
	 */
	private static final String[] SHAPES = new String[]{"Circle", "Oval", "Rectangle", "Square", "Triangle"};
	/**
	 * A set of day options.
	 */
	private static final String[] DAYS = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

	/**
	 * Creates a WMultiSelectExample.
	 */
	public WMultiSelectExample() {
		List<OptionGroup> groups = new ArrayList<>();
		groups.add(new OptionGroup("Shapes", Arrays.asList(SHAPES)));
		groups.add(new OptionGroup("Days", Arrays.asList(DAYS)));

		add(new ExamplePanel(SHAPES), "Plain");

		add(new ExamplePanel(SHAPES));

		add(new ExamplePanel(groups));

		add(new ExamplePanel(groups));

	}

	/**
	 * A panel which can be easily configured to demonstrate different configurations of a WMultiSelect.
	 */
	private static final class ExamplePanel extends WPanel {

		/**
		 * The multi-select being demonstrated.
		 */
		private final WMultiSelect multi;

		/**
		 * A text area to display the selected options.
		 */
		private final WTextArea textArea = new WTextArea();

		/**
		 * Creates an ExamplePanel.
		 *
		 * @param options the options to display in the multi-select pair.
		 */
		private ExamplePanel(final String[] options) {
			this(Arrays.asList(options));
		}

		/**
		 * Creates an ExamplePanel.
		 *
		 * @param options the options to display in the multi-select pair.
		 */
		private ExamplePanel(final List options) {
			setMargin(new Margin(0, 0, 12, 0));
			WFieldLayout layout = new WFieldLayout();
			add(layout);
			layout.setMargin(new Margin(0, 0, 6, 0));

			multi = new WMultiSelect(options);
			layout.addField("Select items", multi);

			WButton copyBtn = new WButton("Copy selected values");
			add(copyBtn);
			copyBtn.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					copySelectedValues();
				}
			});

			WFieldLayout layout2 = new WFieldLayout();
			add(layout2);
			layout2.addField("Copied values", textArea);
			textArea.setRows(5);
			textArea.setReadOnly(true);
		}

		/**
		 * Override prepaprePaintComponent to initialise the example the first time through. This just sets the text of
		 * the text area to match the initial selections.
		 *
		 * @param request the request being responded to
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);

			if (!isInitialised()) {
				copySelectedValues();
				setInitialised(true);
			}
		}

		/**
		 * Copies the selected values from the multi select pair to the text area.
		 */
		private void copySelectedValues() {
			StringBuffer stringBuf = new StringBuffer();
			List selected = multi.getSelected();

			if (selected.isEmpty()) {
				stringBuf.append("(none)");
			} else {
				for (int i = 0; i < selected.size(); i++) {
					if (i > 0) {
						stringBuf.append('\n');
					}

					stringBuf.append(selected.get(i).toString());
				}
			}

			textArea.setText(stringBuf.toString());
		}
	}
}
