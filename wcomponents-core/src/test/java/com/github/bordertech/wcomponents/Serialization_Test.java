package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.registry.UIRegistry;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.Assert;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * Tests to ensure that WComponent graphs serialize correctly.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class Serialization_Test extends AbstractWComponentTestCase {

	/**
	 * A test string.
	 */
	private static final String TEST_STRING = "Batman";

	@Test
	public void testCoreSerialization() {
		WCardManager cardMgr = new WCardManager();
		assertSerializable(cardMgr);

		RadioButtonGroup radioGroup = new RadioButtonGroup();
		WRadioButton radio1 = radioGroup.addRadioButton(1);
		WRadioButton radio2 = radioGroup.addRadioButton(2);

		WPanel radioPanel = new WPanel();
		radioPanel.add(radio1);
		radioPanel.add(radio2);
		radioPanel.add(radioGroup);
		assertSerializable(radioPanel);

		WAbbrText abbrText = new WAbbrText("Hello", "Hi");
		assertSerializable(abbrText);

		WButton button = new WButton("Test Button");
		assertSerializable(button);

		WCheckBox checkBox = new WCheckBox();
		assertSerializable(checkBox);

		WDropdown drop = new WDropdown();
		assertSerializable(drop);

		WLabel label = new WLabel();
		assertSerializable(label);

		WText text = new WText();
		assertSerializable(text);

		WTextField textField = new WTextField();
		assertSerializable(textField);

		cardMgr.add(radioPanel);
		pipe(cardMgr);
	}

	@Test
	public void testComponentModelSerialization() {
		WTextField name = new WTextField();
		UIContext uic = createUIContext();
		setActiveContext(uic);

		name.setText(TEST_STRING);
		Assert.assertEquals("Initial text incorrect", TEST_STRING, name.getText());

		WebModel model = uic.getModel(name);
		model = (WebModel) pipe(model);
		uic.setModel(name, model);
		Assert.assertEquals("Text incorrect after serialization", TEST_STRING, name.getText());
	}

	@Test
	public void testUIContextSerialization() {
		MyApplication app = (MyApplication) UIRegistry.getInstance().getUI(MyApplication.class.
				getName());

		UIContextImpl uic = new UIContextImpl();
		setActiveContext(uic);
		uic.setUI(app);

		WTextField name = app.getText();
		name.setText(TEST_STRING);
		Assert.assertEquals("Initial text incorrect", TEST_STRING, name.getText());

		// Pipe the ui context
		uic = (UIContextImpl) pipe(uic);
		setActiveContext(uic);

		// Components returned should be the same instance
		MyApplication pipedApp = (MyApplication) uic.getUI();
		Assert.assertSame("Should be the same component instance", app, pipedApp);
		Assert.assertSame("Should be the same text field instance", app.getText(), pipedApp.
				getText());

		Assert.assertEquals("Text incorrect after serialization", TEST_STRING, name.getText());
	}

	/**
	 * Asserts that the given object can be serialized.
	 *
	 * @param obj the object to serialize.
	 */
	private void assertSerializable(final Object obj) {
		try {
			pipe(obj);
		} catch (SystemException e) {
			String message = "Failed to serialize " + obj;
			LogFactory.getLog(Serialization_Test.class).error(message, e);
			Assert.fail(message);
		}
	}

	/**
	 * Take a copy of an input object via serialization.
	 *
	 * @param obj the object to copy
	 *
	 * @return the copy of the object
	 */
	private static Object pipe(final Object obj) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.close();

			byte[] bytes = bos.toByteArray();

			FileOutputStream fos = new FileOutputStream("SerializeText.txt");
			fos.write(bytes);
			fos.flush();
			fos.close();

			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object out = ois.readObject();
			return out;
		} catch (Exception ex) {
			throw new SystemException("Failed to pipe " + obj, ex);
		}
	}

	/**
	 * A simple UI to use in the test. This just contains a single text field which can hold some text.
	 */
	protected static final class MyApplication extends WApplication {

		/**
		 * The single text field which comprises the UI.
		 */
		private final WTextField text = new WTextField();

		/**
		 * Creates a MyApplication.
		 */
		public MyApplication() {
			add(text);
		}

		/**
		 * @return the text field
		 */
		public WTextField getText() {
			return text;
		}
	}
}
