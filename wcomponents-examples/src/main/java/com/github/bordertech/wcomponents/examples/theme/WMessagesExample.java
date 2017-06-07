package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WContainer;
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
		messages.setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL, Size.LARGE));
		add(messages);

		messages.error("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.error("<a href='http://localhost'>Message with a link</a>", false);
		messages.error("This is a message with an &bull; entitity");
		messages.error("This is a message with an &bull; entitity", false);


		messages.warn("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.warn("<a href='http://localhost'>Message with a link</a>", false);
		messages.warn("This is a message with an &bull; entitity");
		messages.warn("This is a message with an &bull; entitity", false);

		messages.success("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.success("<a href='http://localhost'>Message with a link</a>", false);
		messages.success("This is a message with an &bull; entitity");
		messages.success("This is a message with an &bull; entitity", false);

		messages.info("Message with encoded mark-up: <a href='http://localhost'>link</a>");
		messages.info("<a href='http://localhost'>Message with a link</a>", false);
		messages.info("This is a message with an &bull; entitity");
		messages.info("This is a message with an &bull; entitity", false);

		// WMessages with no messages should be hidden.
		messages = new WMessages();
		messages.setIdName("hidden_messages");
		add(messages);
	}

}
