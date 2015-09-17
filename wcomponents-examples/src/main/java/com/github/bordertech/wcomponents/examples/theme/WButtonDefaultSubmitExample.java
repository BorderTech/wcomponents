package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;

/**
 * The DefaultSubmit control allows us to stipulate which submit button should be triggered when the user hits the Enter
 * key inside an input field.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WButtonDefaultSubmitExample extends WContainer {

	/**
	 * Creates a WButtonDefaultSubmitExample.
	 */
	public WButtonDefaultSubmitExample() {
		WPanel panel = new WPanel();

		WTextField inputA = new WTextField();
		WCheckBox inputB = new WCheckBox();
		RadioButtonGroup rgroup = new RadioButtonGroup();
		WRadioButton rb1 = rgroup.addRadioButton(1);
		WRadioButton rb2 = rgroup.addRadioButton(2);

		final WMessageBox info = new WMessageBox(WMessageBox.INFO);

		WButton button1 = new WButton("Button1");
		button1.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				info.clearMessages();
				info.addMessage("Button1 was submitted!");
			}
		});

		WButton button2 = new WButton("Button2");
		button2.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				info.clearMessages();
				info.addMessage("Button2 was submitted!");
			}
		});

		add(new WText(
				"The cursor position determines which submit button is triggered when the user hits the ENTER key."));

		// TODO: This is bad - use a layout instead
		WText lineBreak = new WText("<br />");
		lineBreak.setEncodeText(false);
		add(lineBreak);
		lineBreak = new WText("<br />");
		lineBreak.setEncodeText(false);
		add(lineBreak);

		add(info);

		panel.setDefaultSubmitButton(button1);
		WFieldLayout layout = new WFieldLayout();
		panel.add(layout);

		inputA.setDefaultSubmitButton(button1);
		layout.addField("Cursor here will submit button 1", inputA);

		inputB.setDefaultSubmitButton(button2);

		layout.addField("Cursor here will submit button 2", inputB);

		WContainer group = new WContainer();
		group.add(rb1);
		group.add(rb2);
		group.add(rgroup);

		// this input doesn't explicitly add a default submit button. So the button defined by the surrounding div
		// (panel) will be submitted (button1).
		layout.addField("Cursor here will submit button 1", group);

		add(panel);
		add(button1);
		add(button2);
	}
}
