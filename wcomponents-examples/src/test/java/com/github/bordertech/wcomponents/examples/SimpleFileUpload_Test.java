package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link SimpleFileUpload}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class SimpleFileUpload_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new SimpleFileUpload_Test.
	 */
	public SimpleFileUpload_Test() {
		super(new SimpleFileUpload());
	}

	@Test
	public void testExample() throws IOException {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		Assert.assertEquals("Incorrect default text", "", driver.findElement(byWComponentPath(
				"WTextArea")).getText());

		// Upload nothing
		driver.findElement(byWComponentPath("WButton")).click();
		Assert.assertEquals("Should not have uploaded", "nothing uploaded", driver.findElement(
				byWComponentPath("WTextArea")).getText());

		// Upload a file
		String testText = "SimpleFileUpload_Test.testExample. The time is: " + new Date();

		File tempFile = createTempFile(testText);

		driver.findElement(byWComponentPath("WFileWidget")).findElement(By.xpath(
				"descendant-or-self::input")).sendKeys(tempFile.getAbsolutePath());

		driver.findElement(byWComponentPath("WButton")).click();
		Assert.assertEquals("Incorrect data uploaded", testText, driver.findElement(
				byWComponentPath("WTextArea")).getText());
	}

	/**
	 * Creates a temp file for testing.
	 *
	 * @param content the content to write to the file.
	 * @return a handle to the file.
	 * @throws IOException if there is an error writing the file.
	 */
	private static File createTempFile(final String content) throws IOException {
		File tempFile = File.createTempFile("SimpleFileUpload_Test", "tmp");
		tempFile.deleteOnExit();

		OutputStream out = new FileOutputStream(tempFile);
		out.write(content.getBytes("UTF-8"));
		out.close();

		return tempFile;
	}
}
