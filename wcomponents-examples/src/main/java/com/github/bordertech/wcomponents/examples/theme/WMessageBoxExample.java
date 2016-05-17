package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.List;

/**
 * This component shows the different usages of the {@link WMessageBox} component.
 *
 * @author Ming Gao
 * @author Adam Millard
 * @author Yiannis Paschalidis
 */
public class WMessageBoxExample extends WContainer {

	/**
	 * The message box to demonstrate.
	 */
	private final WMessageBox messageBox = new WMessageBox(WMessageBox.SUCCESS);

	/**
	 * The text field used to enter messages.
	 */
	private final WTextArea txtAdd = new WTextArea();

	/**
	 * The list of messages which are being displayed.
	 */
	private final List<String> messageList = new ArrayList<>();

	/**
	 * The dropdown used to select which message to remove.
	 */
	private final WDropdown selRemove = new WDropdown();

	/**
	 * The button to remove a single message.
	 */
	private final WButton btnRemove = new WButton("Remove selected message");

	/**
	 * The dropdown used to remove all messages.
	 */
	private final WButton btnRemoveAll = new WButton("Remove all messages");

	/**
	 * the radio button select to control the message type.
	 */
	private final WRadioButtonSelect messageBoxTypeSelect;

	/**
	 * check box group to make the message box visible/invisible.
	 */
	private final WCheckBox cbVisible = new WCheckBox(true);

	/**
	 * Text field to set the message box title.
	 */
	private final WTextField tfTitle = new WTextField();

	/**
	 * Creates a WMessageBoxExample.
	 */
	public WMessageBoxExample() {

		add(messageBox);

		WFieldSet fieldSet = new WFieldSet("Configuration");
		WFieldLayout fieldLayout = new WFieldLayout();
		fieldSet.add(fieldLayout);

		messageBoxTypeSelect = new WRadioButtonSelect(WMessageBox.Type.values());
		messageBoxTypeSelect.setSelected(WMessageBox.Type.SUCCESS);
		messageBoxTypeSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);

		fieldLayout.addField("Message Box Type", messageBoxTypeSelect);
		fieldLayout.addField("Visible", cbVisible);
		fieldLayout.addField("Title", tfTitle);

		// Apply Button
		WButton apply = new WButton("Apply");
		apply.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				applySettings();
			}
		});

		fieldSet.add(apply);
		add(fieldSet);

		WButton btnAddMessage = new WButton("Add new Message");
		fieldLayout.addField("Add new message", txtAdd);
		fieldLayout.addField(btnAddMessage);
		btnAddMessage.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String txt = txtAdd.getText();
				if (!Util.empty(txt)) {
					// NOTE: one would NOT usually unencode content from a user input: this is VERY dangerous.
					messageBox.addMessage(false, txt.trim());
					applySettings();
				}
			}
		});

		fieldLayout.addField("Remove message", selRemove);
		fieldLayout.addField(btnRemoveAll);

		selRemove.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				if (!"".equals(selRemove.getSelected())) {
					int sel = messageList.indexOf(selRemove.getSelected());

					messageBox.removeMessages(sel - 1);
					applySettings();
				}
			}
		});

		btnRemoveAll.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messageBox.clearMessages();
				applySettings();
			}
		});

		add(new WAjaxControl(selRemove, new AjaxTarget[]{messageBox, fieldLayout}));
	}


	/**
	 * applySettings is used to apply the setting to the various controls on the page.
	 */
	public void applySettings() {
		messageList.clear();

		messageList.add("");

		for (int i = 1; messageBox.getMessages().size() >= i; i++) {
			messageList.add(String.valueOf(i));
		}

		selRemove.setOptions(messageList);
		selRemove.resetData();
		btnRemove.setDisabled(messageList.isEmpty());
		btnRemoveAll.setDisabled(messageList.isEmpty());
		messageBox.setType(
				(com.github.bordertech.wcomponents.WMessageBox.Type) messageBoxTypeSelect.
				getSelected());
		messageBox.setVisible(cbVisible.isSelected());

		if (tfTitle.getText() != null && !"".equals(tfTitle.getText())) {
			messageBox.setTitleText(tfTitle.getText());
		} else {
			messageBox.setTitleText(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			messageBox.addMessage("Message Box Example");
			applySettings();
			setInitialised(true);
		}
	}

}
