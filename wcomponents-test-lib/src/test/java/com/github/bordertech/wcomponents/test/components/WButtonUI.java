package com.github.bordertech.wcomponents.test.components;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Message;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.util.HtmlIconUtil;

public class WButtonUI extends WApplication {

	public static final String FA_TRASH = "fa-trash";
	private WMessages wMessages = new WMessages();
	private WPanel panel = new WPanel();
	private WButton textButton = new WButton("Text Button");
	private WButton imageButton = new WButton("\u200b");

	public WButtonUI() {

		imageButton.setAccessibleText("Delete file");
		imageButton.setToolTip("Delete file");
		imageButton.setHtmlClass(HtmlIconUtil.getIconClasses(FA_TRASH));
		textButton.setAccessibleText("Text Button");
		textButton.setToolTip("Text Button");
		wMessages.setIdName("messages");

		textButton.setAction(new Action() {
			@Override
			public void execute(ActionEvent event) {
				wMessages.addMessage(new Message(Message.INFO_MESSAGE, "text button clicked"));
			}
		});

		imageButton.setAction(new Action() {
			@Override
			public void execute(ActionEvent event) {
				wMessages.addMessage(new Message(Message.INFO_MESSAGE, "image button clicked"));
			}
		});

		panel.add(wMessages);
		panel.add(textButton);
		panel.add(imageButton);
		add(panel);
	}

	public WButton getImageButton() {
		return imageButton;
	}

	public WButton getTextButton() {
		return textButton;
	}

	public WMessages getwMessages() {
		return wMessages;
	}
}
