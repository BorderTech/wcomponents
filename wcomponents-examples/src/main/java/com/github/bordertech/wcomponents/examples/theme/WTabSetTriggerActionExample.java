package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;
import java.util.Date;

/**
 * <p>
 * This example demonstrates how an {@link Action} can be associated with a {@link WTabSet} component. This action gets
 * executed when the tab selection changes. Associate the action to the <code>tabset</code> using
 * {@link WTabSet#setActionOnChange(Action)}.</p>
 *
 * <p>
 * The source object of {@link ActionEvent} will be the <code>tabset</code> of the action.</p>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WTabSetTriggerActionExample extends WContainer {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a WTabSetTriggerActionExample.
	 */
	public WTabSetTriggerActionExample() {
		final WTabSet tabset = new WTabSet();
		final WText text1 = new WText("Welcome to First tab!");
		final WText text2 = new WText("Welcome to Second tab!");
		final WText text3 = new WText("Welcome to Third tab!");

		Action action = new Action() {
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(final ActionEvent event) {
				WTabSet tabSet = (WTabSet) event.getSource();
				int activeIndex = tabSet.getActiveIndex();

				String time = " The time is now " + new Date().toString();

				if (activeIndex == tabset.getTabIndex(text1)) {
					text1.setText(text1.getText() + time);
				} else if (activeIndex == tabset.getTabIndex(text2)) {
					text2.setText(text2.getText() + time);
				} else if (activeIndex == tabset.getTabIndex(text3)) {
					text3.setText(text3.getText() + time);
				}

			}

		};

		tabset.setActionOnChange(action);

		tabset.addTab(text1, "First tab", WTabSet.TAB_MODE_SERVER, 'F');
		tabset.addTab(text2, "Second tab", WTabSet.TAB_MODE_SERVER, 'S');
		tabset.addTab(text3, "Third tab", WTabSet.TAB_MODE_SERVER, 'T');

		add(tabset);
	}
}
