package com.github.bordertech.wcomponents;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WMessageBox_Test - Unit tests for {@link WMessageBox}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WMessageBox_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		String message = "WMessageBox_Test.testConstructors.message";

		try {
			new WMessageBox(null);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should have a message", expected.getMessage());
		}

		WMessageBox messageBox = new WMessageBox(WMessageBox.ERROR);
		Assert.assertEquals("Incorrect message type", WMessageBox.ERROR, messageBox.getType());
		Assert.assertFalse("Message box should not have any messages", messageBox.hasMessages());

		messageBox = new WMessageBox(WMessageBox.WARN, message);
		Assert.assertEquals("Incorrect message type", WMessageBox.WARN, messageBox.getType());
		List<String> messages = messageBox.getMessages();
		Assert.assertEquals("Messagebox should have one message", 1, messages.size());
		Assert.assertEquals("Incorrect message", message, messages.get(0));
	}

	@Test
	public void testHasMessages() {
		WMessageBox messageBox = new WMessageBox(WMessageBox.SUCCESS);
		Assert.assertFalse("Should not have any messages by default", messageBox.hasMessages());

		messageBox.setLocked(true);
		setActiveContext(createUIContext());
		messageBox.addMessage("message1");
		Assert.assertTrue("Should have static message", messageBox.hasMessages());

		messageBox.clearMessages();
		messageBox.addMessage("message1");
		Assert.assertTrue("Should have dynamic message", messageBox.hasMessages());

		resetContext();
		Assert.assertFalse("Should not have any messages by default", messageBox.hasMessages());
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WMessageBox(WMessageBox.SUCCESS), "type", WMessageBox.SUCCESS,
				WMessageBox.ERROR, WMessageBox.WARN);
	}

	@Test
	public void testRemoveMessage() {
		String staticMessage1 = "WMessageBox_Test.testRemoveMessage.staticMessage1";
		String staticMessage2 = "WMessageBox_Test.testRemoveMessage.staticMessage2";
		String dynamicMessage1 = "WMessageBox_Test.testRemoveMessage.dynMessage1";
		String dynamicMessage2 = "WMessageBox_Test.testRemoveMessage.dynMessage2";

		UIContext uic1 = createUIContext();

		WMessageBox messageBox = new WMessageBox(WMessageBox.INFO);
		messageBox.addMessage(staticMessage1);
		messageBox.addMessage(staticMessage2);
		messageBox.setLocked(true);

		setActiveContext(uic1);
		messageBox.addMessage(dynamicMessage1);
		messageBox.addMessage(dynamicMessage2);

		// Remove static message 2
		messageBox.removeMessage(1);
		List<String> messages = messageBox.getMessages();
		Assert.assertEquals("Incorrect number of messages", 3, messages.size());
		Assert.assertTrue("Context with dynamic messages should contain static message 1", messages.
				contains(staticMessage1));
		Assert.assertTrue("Context with dynamic messages should contain dynamic message 1",
				messages.contains(dynamicMessage1));
		Assert.assertTrue("Context with dynamic messages should contain dynamic message 2",
				messages.contains(dynamicMessage2));

		resetContext();
		messages = messageBox.getMessages();
		Assert.assertEquals("Incorrect number of messages", 2, messages.size());
		Assert.assertTrue("Contexts in default state should contain static message 1", messages.
				contains(staticMessage1));
		Assert.assertTrue("Contexts in default state should contain static message 2", messages.
				contains(staticMessage2));

		// Remove dynamic message 1 from uic 1
		setActiveContext(uic1);
		messageBox.removeMessages(1);
		messages = messageBox.getMessages();
		Assert.assertEquals("Incorrect number of messages", 2, messages.size());
		Assert.assertTrue("Should contain static message 1", messages.contains(staticMessage1));
		Assert.assertTrue("Should contain dynamic message 2", messages.contains(dynamicMessage2));

		resetContext();
		messages = messageBox.getMessages();
		Assert.assertEquals("Incorrect number of messages", 2, messages.size());
		Assert.assertTrue("Contexts in default state should contain static message 1", messages.
				contains(staticMessage1));
		Assert.assertTrue("Contexts in default state should contain static message 2", messages.
				contains(staticMessage2));
	}

	@Test
	public void testClearMessages() {
		WMessageBox messageBox = new WMessageBox(WMessageBox.INFO);

		messageBox.addMessage("dummy");

		Assert.assertTrue("Should have static message after add", messageBox.hasMessages());
		messageBox.clearMessages();
		Assert.assertFalse("Should clear messages", messageBox.hasMessages());

		messageBox.addMessage("dummy");

		messageBox.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertTrue("Should have dynamic message after add", messageBox.hasMessages());
		messageBox.clearMessages();
		Assert.assertFalse("Dynamic clear Assert.failed", messageBox.hasMessages());
	}


	@Test
	public void testTitleTextAccessors() {
		//assertAccessorsCorrect(new SimpleComponent(), "htmlClass", null, "foo", "bar");
		WMessageBox comp = new WMessageBox(WMessageBox.INFO);
		comp.setLocked(true);
		setActiveContext(createUIContext());
		String text = "my test text";

		comp.setTitleText(text);
		Assert.assertEquals("Dynamic accessible text incorrect", text, comp.getTitleText());

		resetContext();
		Assert.assertNull("Default accessible text incorrect", comp.getTitleText());
	}
}
