package com.github.bordertech.wcomponents;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WMessagesProxy_Test - Unit tests for {@link WMessagesProxy}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMessagesProxy_Test extends AbstractWComponentTestCase {

	@Test
	public void testProxy() {
		String message1 = "WMessagesProxy_Test.testProxy.message1";
		String message2 = "WMessagesProxy_Test.testProxy.message2";

		MessageContainerImpl root = new MessageContainerImpl();
		WComponent child = new DefaultWComponent();

		// Obtain messages and the proxy directly (we're not testing WMessages)
		WMessagesProxy proxy = new WMessagesProxy(child);
		WMessages messages = root.getMessages();

		// Test the proxy doesn't crash when there is no backing WMessages (should only log a warning).
		setActiveContext(createUIContext());
		proxy.info(message1);
		proxy.success(message1);
		proxy.error(message1);
		proxy.warn(message1);

		// Test the proxy passes messages to the backing WMessages
		root.add(child);

		proxy.info(message2);
		List<String> messageList = messages.getInfoMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message2, messageList.get(0));

		messages.reset();
		proxy.success(message2);
		messageList = messages.getSuccessMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message2, messageList.get(0));

		messages.reset();
		proxy.warn(message2);
		messageList = messages.getWarningMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message2, messageList.get(0));

		messages.reset();
		proxy.error(message2);
		messageList = messages.getErrorMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message2, messageList.get(0));
	}

	/**
	 * A simple message container implementation for testing.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class MessageContainerImpl extends WContainer implements MessageContainer {

		/**
		 * The WMessages instance held by this container.
		 */
		private final WMessages messages = new WMessages();

		/**
		 * Creates a MessageContainerImpl.
		 */
		private MessageContainerImpl() {
			add(messages);
		}

		/**
		 * @return the WMessages instance held by this container.
		 */
		@Override
		public WMessages getMessages() {
			return messages;
		}
	}
}
