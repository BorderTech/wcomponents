package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.examples.SeleniumTests;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
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
public class PetStoreApp_Test extends WComponentSeleniumTestCase {

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

		Assert.assertTrue("Incorrect default page - should have product listing", source.indexOf(
				"Product listing.") != -1);
	}

	@Test
	public void testAddDogToCart() {
		WebDriver driver = getDriver();

		// Find the 2nd text field inside the product table, and enter "1"
		WebElement element = driver.findElement(byWComponentPath("ProductTable/WTextField[1]"));
		element.sendKeys("1");

		// Click the "Update cart" button
		element = driver.findElement(By.xpath("//*[text()='Update cart']"));
		element.click();

		// Check for the success message.
		String source = driver.getPageSource();
		Assert.
				assertTrue("Should have added dog to cart",
						source.indexOf("Added Dog to cart") != -1);

		// Find the 2nd text field in the entire UI (will be the 2nd text field inside the product table).
		element = driver.findElement(byWComponentPath("WTextField[1]"));
		Assert.assertEquals("Incorrect number of dogs in cart", "1", element.getAttribute("value"));
	}
}
