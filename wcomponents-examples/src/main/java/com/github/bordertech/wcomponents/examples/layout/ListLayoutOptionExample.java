package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.ListLayout;
import java.math.BigDecimal;

/**
 * Example showing how to use the {@link ListLayout} component.
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class ListLayoutOptionExample extends WContainer {

	/**
	 * Example list items.
	 */
	private static final String[] EXAMPLE_ITEMS = {"Apple", "Orange", "Banana", "Grape"};

	/**
	 * Creates a ColumnLayoutExample.
	 */
	public ListLayoutOptionExample() {

		final WPanel panel = new WPanel();
		panel.setLayout(new ListLayout(ListLayout.Type.FLAT, ListLayout.Alignment.CENTER, ListLayout.Separator.BAR, false, null));

		final WDropdown wdPanelType = new WDropdown();
		wdPanelType.setOptions(WPanel.Type.values());
		wdPanelType.setSelected(WPanel.Type.PLAIN);

		final WDropdown wdListLayoutType = new WDropdown();
		wdListLayoutType.setOptions(ListLayout.Type.values());
		wdListLayoutType.setSelected(ListLayout.Type.FLAT);

		final WDropdown wdListLayoutAlignment = new WDropdown();
		wdListLayoutAlignment.setOptions(ListLayout.Alignment.values());
		wdListLayoutAlignment.setSelected(ListLayout.Alignment.CENTER);

		final WDropdown wdListLayoutSeparator = new WDropdown();
		wdListLayoutSeparator.setOptions(ListLayout.Separator.values());
		wdListLayoutSeparator.setSelected(ListLayout.Separator.BAR);

		final WCheckBox wcIsOrdered = new WCheckBox();

		final WNumberField wnfGap = new WNumberField();
		wnfGap.setNumber(BigDecimal.ZERO);
		wnfGap.setStep(BigDecimal.ONE);
		wnfGap.setMinValue(BigDecimal.ZERO);

		final WRadioButtonSelect rbsGap = new WRadioButtonSelect();
		rbsGap.setOptions(Size.values());

		WFieldLayout layout = new WFieldLayout();
		add(layout);

		layout.setMargin(new Margin(null, null, Size.LARGE, null));
		layout.addField("Set containing WPanel Type", wdPanelType);
		layout.addField("Set ListLayout Type", wdListLayoutType);
		layout.addField("set ListLayout Alignment", wdListLayoutAlignment);
		layout.addField("set ListLayout Separator", wdListLayoutSeparator);
		layout.addField("Use ordered list", wcIsOrdered);
		layout.addField("Space between items", rbsGap);

		WButton button = new WButton("refresh");

		layout.addField((WLabel) null, button);

		button.setAction(new Action() {

			@Override
			public void execute(final ActionEvent event) {
				panel.setType((WPanel.Type) wdPanelType.getSelected());
				Size gap = rbsGap.getSelected() == null ? null : (Size) rbsGap.getSelected();
				panel.setLayout(new ListLayout((ListLayout.Type) wdListLayoutType.getSelected(),
						(ListLayout.Alignment) wdListLayoutAlignment.getSelected(), (ListLayout.Separator) wdListLayoutSeparator.getSelected(),
						wcIsOrdered.isSelected(), gap));
			}
		});

		add(new WHorizontalRule());
		add(panel);
		for (String item : EXAMPLE_ITEMS) {
			panel.add(new WText(item));
		}

	}

}
