package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.layout.FlowLayout;

/**
 * Example of using encoded and not encoded messages in {@link WMessages}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMessagesExample extends WContainer {

	/**
	 * Construct example.
	 */
	public WMessagesExample() {
		WMessages messages = new WMessages(true);
		messages.setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL, 0, 12));
		add(messages);

		messages.error("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.error("<a href='http://localhost'>Message with a link</a>", false);

		messages.warn("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.warn("<a href='http://localhost'>Message with a link</a>", false);

		messages.success("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.success("<a href='http://localhost'>Message with a link</a>", false);

		messages.info("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.info("<a href='http://localhost'>Message with a link</a>", false);

	}

}
