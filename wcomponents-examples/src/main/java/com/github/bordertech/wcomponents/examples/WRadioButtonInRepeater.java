package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;
import java.util.Arrays;

/**
 * Demonstrate how {@link RadioButtonGroup} and {@link WRadioButton} can be used with a {@link WRepeater}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WRadioButtonInRepeater extends WContainer {

	/**
	 * The radio button group that holds the radio buttons.
	 */
	private final RadioButtonGroup group = new RadioButtonGroup();
	/**
	 * The repeater for the radio buttons.
	 */
	private final WRepeater repeater = new WRepeater(new MyComponent(group));
	/**
	 * The text of the currently selected value.
	 */
	private final WText text = new WText();

	/**
	 * Construct the example.
	 */
	public WRadioButtonInRepeater() {

		/*
         * NOTE
         * a set of radio buttons represent answers to a question where each radio
         * button is unique and mutually-exclusive response. To create an accessible
         * group of radio buttons there must be some way to supply an adequate
         * 'question'. This is most commonly a WFieldSet or a column in a WTable.
		 */
		add(group);

		WFieldSet fset = new WFieldSet("Select an option");
		add(fset);
		fset.setMargin(new Margin(0, 0, 6, 0));
		fset.add(repeater);
		WFieldLayout layout = new WFieldLayout();
		fset.add(layout);
		layout.addField("Outside Repeater - Option D", group.addRadioButton("D"));
		layout.addField("Outside Repeater - Option E", group.addRadioButton("E"));
		layout.addField("Outside Repeater - Option F", group.addRadioButton("F"));

		/*
         * An ajax control should only control items which are placed AFTER the
         * controller (in source order)
		 */
		WPanel msgPanel = new WPanel();
		add(msgPanel);

		final WMessages messages = new WMessages();
		msgPanel.add(messages);

		group.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.info("Changed to " + event.getActionCommand());
			}
		});

		add(new WButton("submit"));

		WButton button1 = new WButton("target for subordinate for option B");
		add(button1);

		WPanel txtPanel = new WPanel();
		add(txtPanel);

		txtPanel.add(text);

		WAjaxControl ajax = new WAjaxControl(group, new AjaxTarget[]{msgPanel, txtPanel});
		add(ajax);

		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(group, "B");
		builder.whenTrue().enable(button1);
		builder.whenFalse().disable(button1);
		add(builder.build());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			repeater.setData(Arrays.asList(new String[]{"A", "B", "C"}));
			setInitialised(true);
		}

		text.setText("Selected: " + (group.getSelectedValue() == null ? "" : group.
				getSelectedValue().toString()));

	}

	/**
	 * A Class that has a radio button that is repeated.
	 */
	public static class MyComponent extends WBeanContainer {

		/**
		 * Label for the radio button.
		 */
		private final WLabel label;

		/**
		 * Construct the repeated component.
		 *
		 * @param group the radio button group
		 */
		public MyComponent(final RadioButtonGroup group) {
			WRadioButton radio = group.addRadioButton();

			label = new WLabel("Label", radio);

			add(label);
			add(radio);
			add(new WHorizontalRule());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);
			if (!isInitialised()) {
				label.setText("Option " + (String) getBean());
				setInitialised(true);
			}
		}
	}
}
