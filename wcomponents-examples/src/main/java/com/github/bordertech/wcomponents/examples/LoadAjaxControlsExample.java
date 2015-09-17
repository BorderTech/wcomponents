package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WCollapsible.CollapsibleMode;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WDropdown.DropdownType;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WMultiDropdown;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPanel.PanelMode;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExampleLookupTable.TableWithNullOption;
import com.github.bordertech.wcomponents.examples.datatable.TreeTableExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxPollingWButtonExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWButtonExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWCheckboxExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWDropdownExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWPanelExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWRadioButtonSelectExample;
import java.util.Date;

/**
 * Example showing AJAX controls being loaded via AJAX.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class LoadAjaxControlsExample extends WContainer {

	/**
	 * Construct the example.
	 */
	public LoadAjaxControlsExample() {
		WPanel content = new WPanel();

		WCollapsible collapse = new WCollapsible(content, "Content with AJAX components",
				CollapsibleMode.DYNAMIC);
		add(collapse);

		content.add(new WHeading(WHeading.SECTION, "Lists using datalist"));

		WFieldLayout layout = new WFieldLayout();
		createFields(layout, "icao");
		content.add(layout);

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Lists using different datalist"));

		WFieldLayout layout2 = new WFieldLayout();
		createFields(layout2, new TableWithNullOption("icao"));
		content.add(layout2);

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Eager Panel"));

		WPanel eager = new WPanel(WPanel.Type.BOX);
		eager.setMode(PanelMode.EAGER);
		eager.add(new WText("Eager panel content loaded via ajax."));
		content.add(eager);

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Ajax - Button"));
		content.add(new AjaxWButtonExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Ajax - CheckBox"));
		content.add(new AjaxWCheckboxExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Ajax - Dropdown"));
		content.add(new AjaxWDropdownExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Ajax - Table"));
		content.add(new TreeTableExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Ajax - Polling"));
		content.add(new AjaxPollingWButtonExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Ajax - Lazy Panel"));
		content.add(new AjaxWPanelExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Ajax - RadioButtonSelect"));
		content.add(new AjaxWRadioButtonSelectExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Dialog"));
		content.add(new WDialogExample());

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Tab Modes"));
		WTabSet tabset1 = new WTabSet();
		tabset1.
				addTab(new DateText("Tab Content 1"), "Tab 1 (client)", WTabSet.TAB_MODE_CLIENT, '1');
		tabset1.
				addTab(new DateText("Tab Content 2"), "Tab 2 (client)", WTabSet.TAB_MODE_CLIENT, '2');
		tabset1.
				addTab(new DateText("Tab Content 3"), "Tab 3 (server)", WTabSet.TAB_MODE_SERVER, '3');
		tabset1.addTab(new DateText("Tab Content 4"), "Tab 4 (lazy)", WTabSet.TAB_MODE_LAZY, '4');
		tabset1.addTab(new DateText("Tab Content 5"), "Tab 5 (dynamic)", WTabSet.TAB_MODE_DYNAMIC,
				'5');
		tabset1.addTab(new DateText("Tab Content 6"), "Tab 6 (eager)", WTabSet.TAB_MODE_EAGER, '6');
		content.add(tabset1);

		content.add(new WHorizontalRule());
		content.add(new WHeading(WHeading.SECTION, "Collapsibles"));
		content.add(new WCollapsible(new DateText("Eager Collapsible Content"), "Eager Mode",
				CollapsibleMode.EAGER));
		content.add(new WCollapsible(new DateText("Lazy Collapsible Content"), "Lazy Mode",
				CollapsibleMode.LAZY));
		content.add(new WCollapsible(new DateText("Dynamic Collapsible Content"), "Dynamic Mode",
				CollapsibleMode.DYNAMIC));

	}

	/**
	 * @param layout the layout to add the fields to
	 * @param table the lookupTable to use on the fields
	 */
	private void createFields(final WFieldLayout layout, final Object table) {
		WDropdown down = new WDropdown(table);
		down.setType(DropdownType.COMBO);
		layout.addField("Combo Dropdown", down);

		WMultiDropdown multi = new WMultiDropdown(table);
		layout.addField("Multi Dropdown", multi);

		WMultiSelect select = new WMultiSelect(table);
		layout.addField("Multi Select", select);
	}

	/**
	 * A text component with some dynamic data, so we can see that content changes.
	 */
	private static final class DateText extends WText {

		/**
		 * Creates a DateText.
		 *
		 * @param text the text to prefix before the date.
		 */
		private DateText(final String text) {
			super(text);
			setEncodeText(false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText() {
			return super.getText() + "<br/> Text generated at: " + new Date();
		}
	}

}
