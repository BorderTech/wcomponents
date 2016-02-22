package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;

/**
 * Demonstrate the {@link WStyledText} configuration options.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WStyledTextOptionsExample extends WPanel {

	/**
	 * Construct the example.
	 */
	public WStyledTextOptionsExample() {

		add(new WHeading(WHeading.MAJOR, "WStyledText Options"));

		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(30);
		add(layout);

		final WDropdown type = new WDropdown(WStyledText.Type.values());
		layout.addField("Type", type);

		final WDropdown mode = new WDropdown(WStyledText.WhitespaceMode.values());
		layout.addField("Whitespace mode", mode);

		final WCheckBox useBigTextClass = new WCheckBox();
		layout.addField("Use HTML class 'bigText'", useBigTextClass);

		final WTextArea text = new WTextArea();
		text.setRows(5);
		text.setText("Some text");
		layout.addField("Text", text).setInputWidth(100);

		final WStyledText styled = new WStyledText();

		WButton button = new WButton("apply");
		add(button);

		button.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				styled.reset();
				styled.setType((WStyledText.Type) type.getSelected());
				styled.setWhitespaceMode((WStyledText.WhitespaceMode) mode.getSelected());
				styled.setText(text.getText());
				styled.setHtmlClass(useBigTextClass.isSelected() ? "bigText" : null);
			}
		});

		add(new WHeading(WHeading.MAJOR, "Styled Text"));

		WPanel panel = new WPanel(WPanel.Type.BOX);
		panel.setMargin(new Margin(20));
		add(panel);

		panel.add(styled);

		setDefaultSubmitButton(button);


		WText cssText = new WText("<style type='text/css'>.bigText{font-size:3em;}</style>");
		cssText.setEncodeText(false);
		add(cssText);
	}

}
