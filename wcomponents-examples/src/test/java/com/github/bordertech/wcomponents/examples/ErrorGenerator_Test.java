package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link ErrorGenerator}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
//@RunWith(value = Parameterized.class)
public class ErrorGenerator_Test extends WComponentExamplesTestCase {

	/**
	 * The error message displayed when an unhandled error occurs. Obtained from DefaultSystemFailureMapper.
	 */
	private static final String ERROR_STRING = "The system is currently unavailable";

	/**
	 * Creates a new ErrorGenerator_Test.
	 */
	public ErrorGenerator_Test() {
		super(new ErrorGenerator());
	}

// TODO Look at supporting Parameterized tests wiht the BrowserRunner
//	/**
//	 * The path to test with.
//	 */
//	private final String path;
//
//
//	/**
//	 * Creates a new ErrorGenerator_Test.
//	 *
//	 * @param path the path to test with.
//	 */
//	public ErrorGenerator_Test(final String path) {
//		super(new ErrorGenerator());
//		this.path = path;
//	}
//
//	/**
//	 * @return the parameters for this test.
//	 */
//	@Parameters
//	public static List<String[]> data() {
//		return Arrays.asList(new String[][]{
//			{"WButton[2]"},
//			{"WButton[3]"},
//			{"WButton[4]"},
//			{"WButton[5]"}
//		});
//	}
	@Test
	public void testErrorButton2() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		driver.findElement(byWComponentPath("WButton[2]")).click();

		// Exception will have been caught by framework, so an error message should be displayed
		Assert.assertTrue("Should be displaying an error page", driver.getPageSource().contains(
				ERROR_STRING));
	}

	@Test
	public void testErrorButton3() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		driver.findElement(byWComponentPath("WButton[2]")).click();

		// Exception will have been caught by framework, so an error message should be displayed
		Assert.assertTrue("Should be displaying an error page", driver.getPageSource().contains(
				ERROR_STRING));
	}

	@Test
	public void testErrorButton4() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		driver.findElement(byWComponentPath("WButton[2]")).click();

		// Exception will have been caught by framework, so an error message should be displayed
		Assert.assertTrue("Should be displaying an error page", driver.getPageSource().contains(
				ERROR_STRING));
	}

	@Test
	public void testErrorButton5() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		driver.findElement(byWComponentPath("WButton[2]")).click();

		// Exception will have been caught by framework, so an error message should be displayed
		Assert.assertTrue("Should be displaying an error page", driver.getPageSource().contains(
				ERROR_STRING));
	}

}
