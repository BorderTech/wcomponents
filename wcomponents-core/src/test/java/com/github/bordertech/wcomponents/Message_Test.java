package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Message - Unit tests for {@link Message}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class Message_Test {

	@Test
	public void testConstructors() {
		String messageText = "Message_Test.testConstructors.message";
		String messageField = "Message_Test.testConstructors.messageField";

		Message message = new Message(Message.INFO_MESSAGE, messageText);
		Assert.assertEquals("Incorrect message type", Message.INFO_MESSAGE, message.getType());
		Assert.assertEquals("Incorrect message text", messageText, message.toString());
		Assert.assertNull("Args should be null", message.getArgs());

		message = new Message(Message.ERROR_MESSAGE, messageText, messageField);
		Assert.assertEquals("Incorrect message type", Message.ERROR_MESSAGE, message.getType());
		Assert.assertEquals("Incorrect message text", messageText, message.toString());
		Assert.assertEquals("Incorrect message args", messageField, message.getArgs()[0]);

		try {
			message = new Message(-1234567, "");
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should contain a message", expected.getMessage());
		}
	}

	@Test
	public void testArgsAccessors() {
		String messageField1 = "Message_Test.testConstructors.messageField1";
		String messageField2 = "Message_Test.testConstructors.messageField2";

		Message message = new Message(Message.INFO_MESSAGE, "", messageField1);
		Assert.assertEquals("Incorrect message field", messageField1, message.getArgs()[0]);

		message.setArgs(messageField2);
		Assert.
				assertEquals("Incorrect message field after set", messageField2,
						message.getArgs()[0]);
	}

	@Test
	public void testTypeAccessors() {
		Message message = new Message(Message.INFO_MESSAGE, "");
		Assert.assertEquals("Incorrect message type", Message.INFO_MESSAGE, message.getType());

		message.setType(Message.WARNING_MESSAGE);
		Assert.assertEquals("Incorrect message type after set", Message.WARNING_MESSAGE, message.
				getType());

		try {
			message.setType(-1234567);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should contain a message", expected.getMessage());
		}
	}

	@Test
	public void testEquals() {
		String messageText = "Message_Test.testEquals.message";
		String arg = "Message_Test.testEquals.messageField";

		Message message1 = new Message(Message.INFO_MESSAGE, messageText, arg);

		Message message2 = new Message(Message.INFO_MESSAGE, messageText, arg);
		Assert.assertEquals("Messages should be equal", message1, message2);

		message2 = new Message(Message.SUCCESS_MESSAGE, messageText, arg);
		Assert.assertFalse("Messages with different types should not be equal", message1.equals(
				message2));

		message2 = new Message(Message.INFO_MESSAGE, messageText + "X", arg);
		Assert.assertFalse("Messages with different text should not be equal", message1.equals(
				message2));

		message2 = new Message(Message.INFO_MESSAGE, messageText, (String) null);
		Assert.assertFalse("Messages with different fields should not be equal", message1.equals(
				message2));
	}

	@Test
	public void testHashCode() {
		String text = "text";

		Message message = new Message(Message.INFO_MESSAGE, "text");
		Assert.assertEquals("Incorrect hashCode", text.hashCode(), message.hashCode());
	}
}
