package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.examples.SeleniumTests;
import com.github.bordertech.wcomponents.examples.WComponentExamplesTestCase;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * PetStore tests - high level tests for the PetStore.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class PetStoreApp_Test extends WComponentExamplesTestCase {

	/**
	 * Construct test.
	 */
	public PetStoreApp_Test() {
		super(new PetStoreApp());
	}

	@Test
	public void testDefaultView() {
		WebDriver driver = getDriver();

		String source = driver.getPageSource();

		Assert.assertTrue("Incorrect default page - should have product listing", source.contains("Product listing."));
	}

	@Test
	public void testAddDogToCart() {
		SeleniumWComponentsWebDriver driver = getDriver();

		// Find the 2nd text field inside the product table, and enter "1"
		WebElement element = driver.findWTextField(byWComponentPath("ProductTable/WTextField[1]"));
		element.sendKeys("1");

		// Click the "Update cart" button
		element = driver.findElement(By.xpath("//*[text()='Update cart']"));
		element.click();

		// Check for the success message.
		String source = driver.getPageSource();

		//Example no longer performs update - nothing to test.
//		Assert.
//				assertTrue("Should have added dog to cart", source.contains("Added Cat to cart"));
//
//		// Find the 2nd text field in the entire UI (will be the 2nd text field inside the product table).
//		element = driver.findElement(byWComponentPath("WTextField[1]"));
//		Assert.assertEquals("Incorrect number of dogs in cart", "1", element.getAttribute("value"));
	}
}
