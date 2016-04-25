package com.github.bordertech.wcomponents.test.example;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * <p>
 * This class is an example showing various ways of testing a WComponent UI using Selenium. The same UI is tested
 * multiple times using different approaches.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExampleSeleniumTest extends WComponentSeleniumTestCase {

	/**
	 * Creates a ExampleSeleniumTest. The WComponent which is passed to the superclass's constructor is the UI to be
	 * tested. Small sections of application code may need to be wrapped in a shell which puts them in the correct
	 * state.
	 */
	public ExampleSeleniumTest() {
		super(new ExampleUI());
	}

	/**
	 * This test implementation uses ByWComponentPath to find the HTML controls. A ByWComponentPath is built using a
	 * full or partial set of classes which describe a path through the WComponent Tree structure. This is the preferred
	 * method of testing WComponent UIs, as it should be stable between releases, and does not require applications to
	 * expose UI structure via getters.
	 *
	 * The UI structure tree structure used in this example is as follows:
	 * <pre>
	 *    ExampleUI
	 *       WTabSet
	 *          WTab
	 *            TextDuplicator
	 *              WTextField
	 *              WButton
	 *              WButton
	 *          WTab
	 *            TextDuplicatorWithGetters
	 *              WTextField
	 *              WButton
	 *              WButton
	 * </pre>
	 *
	 * So to obtain the first WTextField, any of the following paths could be used:
	 *
	 * <dl>
	 * <dt>Full path, including tab index</dt>
	 * <dd>ExampleUI/WTabSet/WTab[0]/ExampleSeleniumTest$TextDuplicator/WTextField</dd>
	 *
	 * <dt>Full path, minus tab index (first tab is used)</dt>
	 * <dd>ExampleUI/WTabSet/WTab/ExampleSeleniumTest$TextDuplicator/WTextField</dd>
	 *
	 * <dt>First text field in a TextDuplicator</dt>
	 * <dd>ExampleSeleniumTest$TextDuplicator/WTextField</dd>
	 *
	 * <dt>First text field in the first tab</dt>
	 * <dd>WTab/WTextField</dd>
	 *
	 * <dt>First text field in the whole UI</dt>
	 * <dd>WTextField</dd>
	 * </dl>
	 *
	 * The amount of detail you will need to specify for the paths in your tests will depend on how your UI is
	 * structured. If you have created reusable application components (e.g. a "NamePanel"), you will be able to match
	 * on this directly in the path rather than having to look for the WComponent[x]/WComponent[y]/WComponent[z].
	 */
	@Test
	public void testDuplicatorWithNoGettersWComponentPathImpl() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Paths to frequently used components
		By textFieldPath = byWComponentPath("ExampleSeleniumTest$TextDuplicator/WTextField");

		// Enter some text and use the duplicate button
		driver.findElement(textFieldPath).sendKeys("dummy");
		driver.findElement(byWComponentPath("ExampleSeleniumTest$TextDuplicator/WButton[0]"))
				.click();
		Assert.assertEquals("Incorrect text field text after duplicate", "dummydummy",
				driver.findElement(textFieldPath).getAttribute("value"));

		// Clear the text
		driver.findElement(byWComponentPath("ExampleSeleniumTest$TextDuplicator/WButton[1]"))
				.click();
		Assert.assertEquals("Incorrect text field text after clear", "",
				driver.findElement(textFieldPath).getAttribute("value"));
	}

	/**
	 * This test implementation uses a 'traditional' Selenium approach, matching on HTML elements. This method should
	 * not generally be used for testing WComponent applications, as the HTML output can change without notice between
	 * different theme versions; the rendered HTML is transform-dependant, not WComponent-dependant.
	 */
	@Test
	public void testDuplicatorWithNoGettersHtmlImpl() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Enter some text and use the duplicate button
		driver.findElement(By.xpath("//input[@type='text']")).sendKeys("dummy");
		driver.findElement(By.xpath("//*[text()='Duplicate']")).click();
		Assert.assertEquals("Incorrect text field text after duplicate", "dummydummy",
				driver.findElement(By.xpath("//input[@type='text']")).getAttribute("value"));

		// Clear the text
		driver.findElement(By.xpath("//*[text()='Clear']")).click();
		Assert.assertEquals("Incorrect text field text after clear", "",
				driver.findElement(By.xpath("//input[@type='text']")).getAttribute("value"));
	}

	/**
	 * This test implementation uses ByWComponentPath to find the HTML controls. See
	 * {@link #testDuplicatorWithNoGettersWComponentPathImpl()} for an explanation of how the paths function.
	 */
	@Test
	public void testDuplicatorWithGettersWComponentPathImpl() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Paths to frequently used components
		By textFieldPath = byWComponentPath(
				"ExampleSeleniumTest$TextDuplicatorWithGetters/WTextField");

		// Select the 2nd tab
		driver.findElement(byWComponentPath("WTab[1]")).click();

		// Enter some text and use the duplicate button
		driver.findElement(textFieldPath).sendKeys("dummy");
		driver.findElement(byWComponentPath(
				"ExampleSeleniumTest$TextDuplicatorWithGetters/WButton[0]")).click();
		Assert.assertEquals("Incorrect text field text after duplicate", "dummydummy",
				driver.findElement(textFieldPath).getAttribute("value"));

		// Clear the text
		driver.findElement(byWComponentPath(
				"ExampleSeleniumTest$TextDuplicatorWithGetters/WButton[1]")).click();
		Assert.assertEquals("Incorrect text field text after clear", "",
				driver.findElement(textFieldPath).getAttribute("value"));
	}

	/**
	 * This test implementation uses ByWComponent to find the HTML controls. This is the easiest approach to use, but
	 * requires that the UI being tested has been built with getters for every control, which is not usually practical.
	 */
	@Test
	public void testDuplicatorWithGettersWComponentImpl() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();
		ExampleUI ui = (ExampleUI) getUi();

		// UI components
		WTextField textField = ui.getTextDuplicatorWithGetters().getTextField();
		WButton duplicateButton = ui.getTextDuplicatorWithGetters().getDuplicateButton();
		WButton clearButton = ui.getTextDuplicatorWithGetters().getClearButton();

		// Select the 2nd tab
		driver.findElement(byWComponentPath("WTab[1]")).click();

		// Enter some text and use the duplicate button
		driver.findElement(byWComponent(textField)).sendKeys("dummy");
		driver.findElement(byWComponent(duplicateButton)).click();
		Assert.assertEquals("Incorrect text field text after duplicate", "dummydummy",
				driver.findElement(byWComponent(textField)).getAttribute("value"));

		// Clear the text
		driver.findElement(byWComponent(clearButton)).click();
		Assert.assertEquals("Incorrect text field text after clear", "",
				driver.findElement(byWComponent(textField)).getAttribute("value"));
	}

	/**
	 * <p>
	 * A simple UI to test, which contains a WTabSet with two tabs. The first tab is client-side, so is always present.
	 * The second tab is lazy-loaded via AJAX.</p>
	 *
	 * <p>
	 * The first tab contains an example where the fields are not exposed via getters. The second tab contains the same
	 * example, but with getters.</p>
	 */
	private static final class ExampleUI extends WContainer {

		/**
		 * The text duplicator used in the example.
		 */
		private final TextDuplicator textDuplicator = new TextDuplicator();

		/**
		 * The text duplicator (with getters) used in the example.
		 */
		private final TextDuplicatorWithGetters textDuplicatorWithGetters
				= new TextDuplicatorWithGetters();

		/**
		 * Creates an ExampleUI.
		 */
		private ExampleUI() {
			WTabSet tabs = new WTabSet();
			add(tabs);

			tabs.addTab(textDuplicator, "No getter", WTabSet.TAB_MODE_CLIENT);
			tabs.addTab(textDuplicatorWithGetters, "With getter", WTabSet.TAB_MODE_LAZY);
		}

		/**
		 * @return the text duplicator.
		 */
		public TextDuplicator getTextDuplicator() {
			return textDuplicator;
		}

		/**
		 * @return the text duplicator with getters.
		 */
		public TextDuplicatorWithGetters getTextDuplicatorWithGetters() {
			return textDuplicatorWithGetters;
		}
	}

	/**
	 * A text duplicator example.
	 */
	private static class TextDuplicator extends WContainer {

		/**
		 * The text field which the actions modify the state of.
		 */
		private final WTextField textFld = new WTextField();

		/**
		 * The text field which the actions modify the state of.
		 */
		private final WButton duplicateButton = new WButton("Duplicate");

		/**
		 * The text field which the actions modify the state of.
		 */
		private final WButton clearButton = new WButton("Clear");

		/**
		 * Creates a TextDuplicator.
		 */
		TextDuplicator() {
			duplicateButton.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					String text = textFld.getText();
					textFld.setText(text + text);
				}
			});

			clearButton.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					textFld.setText("");
				}
			});

			add(new WLabel("Text to duplicate", textFld));
			add(textFld);
			add(duplicateButton);
			add(clearButton);
		}

		/**
		 * @return the text field.
		 */
		WTextField getTextField() {
			return textFld;
		}

		/**
		 * @return the "duplicate" button.
		 */
		WButton getDuplicateButton() {
			return duplicateButton;
		}

		/**
		 * @return the "clear" button.
		 */
		WButton getClearButton() {
			return clearButton;
		}
	}

	/**
	 * An extension of the text duplicator example which exposes the fields.
	 */
	private static class TextDuplicatorWithGetters extends TextDuplicator {

		/**
		 * @return the text field.
		 */
		@Override
		public WTextField getTextField() {
			return super.getTextField();
		}

		/**
		 * @return the "duplicate" button.
		 */
		@Override
		public WButton getDuplicateButton() {
			return super.getDuplicateButton();
		}

		/**
		 * @return the "clear" button.
		 */
		@Override
		public WButton getClearButton() {
			return super.getClearButton();
		}
	}
}
