package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link ErrorGenerator}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(value = Parameterized.class)
public class ErrorGenerator_Test extends WComponentSeleniumTestCase {

	/**
	 * The error message displayed when an unhandled error occurs. Obtained from DefaultSystemFailureMapper.
	 */
	private static final String ERROR_STRING = "The system is currently unavailable";

	/**
	 * The path to test with.
	 */
	private final String path;

	/**
	 * Creates a new ErrorGenerator_Test.
	 *
	 * @param path the path to test with.
	 */
	public ErrorGenerator_Test(final String path) {
		super(new ErrorGenerator());
		this.path = path;
	}

	/**
	 * @return the parameters for this test.
	 */
	@Parameters
	public static List<String[]> data() {
		return Arrays.asList(new String[][]{
			{"WButton[2]"},
			{"WButton[3]"},
			{"WButton[4]"},
			{"WButton[5]"}
		});
	}

	@Test
	public void testError() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		driver.findElement(byWComponentPath(path)).click();

		// Exception will have been caught by framework, so an error message should be displayed
		Assert.assertTrue("Should be displaying an error page", driver.getPageSource().contains(
				ERROR_STRING));
	}
}
