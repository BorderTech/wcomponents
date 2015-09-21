package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WMessages_Test - Unit tests for {@link WMessages}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMessages_Test extends AbstractWComponentTestCase {

	@Test
	public void testGetInstance() {
		WContainer root = new MessageContainerImpl();
		WComponent child = new DefaultWComponent();

		// Try obtaining the instance before the child is added to a tree that has a container
		WMessages instance = WMessages.getInstance(child);

		Assert.assertNotNull("Obtained instance should be a proxy if there is no message container",
				instance);

		Assert.assertEquals("Obtained instance should be a proxy if there is no message container",
				WMessagesProxy.class, instance.getClass());

		root.add(child);

		// Now try obtaining the instance for a tree with a container
		instance = WMessages.getInstance(child);

		Assert.assertNotNull("Obtained instance not be null", instance);

		Assert.
				assertFalse(
						"Obtained instance should not be a proxy if there is a message container",
						WMessagesProxy.class.equals(instance.getClass()));

		WMessages instance2 = WMessages.getInstance(root);

		Assert.assertSame("Same instance should be returned for any WComponent in the tree",
				instance, instance2);
	}

	@Test
	public void testGetInfoMessages() {
		WMessages messages = new WMessages();

		String message = "testGetInfoMessages";
		messages.info(message);
		List<String> messageList = messages.getInfoMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message, messageList.get(0));
	}

	@Test
	public void testGetSuccessMessages() {
		WMessages messages = new WMessages();

		String message = "testGetInfoMessages";
		messages.success(message);
		List<String> messageList = messages.getSuccessMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message, messageList.get(0));
	}

	@Test
	public void testGetWarningMessages() {
		WMessages messages = new WMessages();

		String message = "testGetInfoMessages";
		messages.warn(message);
		List<String> messageList = messages.getWarningMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message, messageList.get(0));
	}

	@Test
	public void testGetErrorMessages() {
		WMessages messages = new WMessages();

		String message = "testGetInfoMessages";
		messages.error(message);
		List<String> messageList = messages.getErrorMessages();
		Assert.assertEquals("Incorrect number of messages", 1, messageList.size());
		Assert.assertEquals("Incorrect message", message, messageList.get(0));
	}

	@Test
	public void testHasMessage() {
		WMessages messages = new WMessages();
		UIContext uic = new UIContextImpl();
		setActiveContext(uic);

		Assert.assertFalse("Should have no messages by default", messages.hasMessages());

		messages.addMessage(new Message(Message.SUCCESS_MESSAGE, "x"));
		Assert.assertTrue("Should be true if there are messages", messages.hasMessages());

		messages = new WMessages();
		messages.addMessage(new Message(Message.INFO_MESSAGE, "x"));
		Assert.assertTrue("Should be true if there are messages", messages.hasMessages());

		messages = new WMessages();
		messages.addMessage(new Message(Message.WARNING_MESSAGE, "x"));
		Assert.assertTrue("Should be true if there are messages", messages.hasMessages());

		messages = new WMessages();
		messages.addMessage(new Message(Message.ERROR_MESSAGE, "x"));
		Assert.assertTrue("Should be true if there are messages", messages.hasMessages());

		messages = new WMessages();

		Diagnostic error = new DiagnosticImpl(Diagnostic.ERROR, new WTextField(), "Error");
		List<Diagnostic> errors = new ArrayList<>();
		errors.add(error);

		messages.getValidationErrors().setErrors(errors);
		Assert.assertTrue("Should be true if there are messages", messages.hasMessages());
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
