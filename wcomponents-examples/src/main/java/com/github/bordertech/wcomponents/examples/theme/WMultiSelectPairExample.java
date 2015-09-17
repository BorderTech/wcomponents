package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.WTabSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example showing use of a {@link WMultiSelectPair}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMultiSelectPairExample extends WContainer {

	/**
	 * A set of shape options.
	 */
	private static final String[] SHAPES = new String[]{"Circle", "Oval", "Rectangle", "Square", "Triangle"};
	/**
	 * A set of day options.
	 */
	private static final String[] DAYS = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

	/**
	 * Creates a WMultiSelectPairExample.
	 */
	public WMultiSelectPairExample() {
		List<OptionGroup> groups = new ArrayList<>();
		groups.add(new OptionGroup("Shapes", Arrays.asList(SHAPES)));
		groups.add(new OptionGroup("Days", Arrays.asList(DAYS)));

		WTabSet exampleTabs = new WTabSet();

		ExamplePanel panel = new ExamplePanel(SHAPES, false);
		exampleTabs.addTab(panel, "Plain", WTabSet.TAB_MODE_SERVER);

		panel = new ExamplePanel(SHAPES, true);
		exampleTabs.addTab(panel, "Shuffle", WTabSet.TAB_MODE_SERVER);

		panel = new ExamplePanel(groups, false);
		exampleTabs.addTab(panel, "Groups", WTabSet.TAB_MODE_SERVER);

		panel = new ExamplePanel(groups, true);
		exampleTabs.addTab(panel, "Groups + Shuffle", WTabSet.TAB_MODE_SERVER);

		add(exampleTabs);
	}

	/**
	 * A panel which can be easily configured to demonstrate different configurations of a WMultiSelectPair.
	 */
	private static final class ExamplePanel extends WContainer {

		/**
		 * The multi-select pair being demonstrated.
		 */
		private final WMultiSelectPair multi;

		/* Read only multi select pair to reflect the selected values. */
		private final WMultiSelectPair roMSP;

		/**
		 * Creates an ExamplePanel.
		 *
		 * @param options the options to display in the multi-select pair.
		 * @param shuffle whether to enable option shuffling for the multi-select pair.
		 */
		private ExamplePanel(final String[] options, final boolean shuffle) {
			this(Arrays.asList(options), shuffle);
		}

		/**
		 * Creates an ExamplePanel.
		 *
		 * @param options the options to display in the multi-select pair.
		 * @param shuffle whether to enable option shuffling for the multi-select pair.
		 */
		private ExamplePanel(final List options, final boolean shuffle) {
			WFieldLayout layout = new WFieldLayout();
			add(layout);

			multi = new WMultiSelectPair(options);
			roMSP = new WMultiSelectPair(options);
			multi.setShuffle(shuffle);
			roMSP.setShuffle(shuffle);
			layout.addField("Select items", multi);
			layout.addField("Copied values (read only control)", roMSP);
			roMSP.setReadOnly(true);

			WButton copyBtn = new WButton("Copy selected values");
			layout.addField((WLabel) null, copyBtn);
			copyBtn.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					copySelectedValues();
				}
			});
			copyBtn.setAjaxTarget(roMSP);
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
				setInitialised(true);
				copySelectedValues();
			}

		}

		/**
		 * Copies the selected values from the multi select pair to the text area.
		 */
		private void copySelectedValues() {
			roMSP.setData(multi.getData()); //this is just to reset the order if the options have been shuffled
			roMSP.setSelected(multi.getSelected());
		}
	}
}
