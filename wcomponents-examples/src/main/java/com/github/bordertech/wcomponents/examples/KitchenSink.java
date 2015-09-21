package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * Shows off everything but the WKitchenSink.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class KitchenSink extends WPanel {

	/**
	 * The tabset that holds all the examples.
	 *
	 * Tabs are set to be client-side so we can test the rendering performance of all of the components in one hit.
	 */
	private final WTabSet tabs = new WTabSet();

	/**
	 * A button to reset all the examples.
	 */
	private final WButton resetButton = new WButton("Reset All");

	/**
	 * Creates a KitchenSink example.
	 */
	public KitchenSink() {
		super(Type.BLOCK);
		add(resetButton);

		resetButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				KitchenSink.this.reset();
			}
		});

		tabs.addTab(new WButtonExample(), "Button", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new CheckBoxExample(), "CheckBox", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new RadioButtonExample(), "RadioButton", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new TextAreaExample(), "TextArea", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new TextFieldExample(), "TextField", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new EntryFieldExample(), "Entry Fields", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new DuplicatorGroup(), "Dynamic Additions", WTabSet.TAB_MODE_CLIENT);
		tabs.addTab(new TextDuplicator(), "Text Duplicator", WTabSet.TAB_MODE_CLIENT);

		add(new WHeading(WHeading.MAJOR, "Selection of tests"));
		add(tabs);
	}

	/**
	 * This component demonstrates the ability to dynamically build a UI. It adds TextDuplicators or copies of itself,
	 * depending on which button is pressed.
	 */
	public static class DuplicatorGroup extends WPanel {

		/**
		 * A button used to add a TextDuplicator to this DuplicatorGroup.
		 */
		private final WButton addDupBtn = new WButton("Add Duplicator");

		/**
		 * A button used to add a DuplicatorGroup to this DuplicatorGroup.
		 */
		private final WButton addGroupBtn = new WButton("Add Duplicator Group");

		/**
		 * A button used to remove all the dynamically added components.
		 */
		private final WButton removeAllBtn = new WButton("Remove All");

		/**
		 * A container for all the dynamically added components.
		 */
		private final WPanel dupPanel = new WPanel();

		/**
		 * Creates a DuplicatorGroup.
		 */
		public DuplicatorGroup() {
			setLayout(new FlowLayout(Alignment.VERTICAL));
			dupPanel.setLayout(new FlowLayout(Alignment.VERTICAL));

			WContainer btnPanel = new WContainer();
			btnPanel.add(addDupBtn);
			btnPanel.add(addGroupBtn);
			btnPanel.add(removeAllBtn);

			add(new WText("Duplicator Group"));
			add(btnPanel);
			add(dupPanel);
		}

		/**
		 * Override handleRequest to implement processing specific to this component. Normally, applications should be
		 * adding Actions to WButtons rather than overriding this method. See the KitchenSink's resetButton for an
		 * example.
		 *
		 * @param request the request being responded to.
		 */
		@Override
		public void handleRequest(final Request request) {
			if (addDupBtn.isPressed()) {
				dupPanel.add(new TextDuplicatorHandleRequestImpl("Duplicator "
						+ (dupPanel.getChildCount() + 1)));
			} else if (addGroupBtn.isPressed()) {
				dupPanel.add(new DuplicatorGroup());
			} else if (removeAllBtn.isPressed()) {
				dupPanel.removeAll();
			}
		}
	}
}
