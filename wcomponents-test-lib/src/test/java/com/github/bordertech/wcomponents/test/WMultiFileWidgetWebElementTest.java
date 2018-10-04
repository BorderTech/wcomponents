package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WMultiFileWidgetUI;
import com.github.bordertech.wcomponents.test.selenium.ByButtonText;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWMultiFileWidgetWebElement;
import java.io.File;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WMultiFileWidgetWebElementTest extends SeleniumJettyTestCase {


	/**
	 * Constructor...
	 */
	public WMultiFileWidgetWebElementTest() {
		super(new WMultiFileWidgetUI());
	}

	@Test
	@Ignore
	public void testTextFieldFeatures() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement validatingButton = driver.findWButton(new ByButtonText("Validate button", false, true));

		validatingButton.click();

		SeleniumWMultiFileWidgetWebElement wMultiFileWidgetWebElement =
			driver.findWMultiFileWidget(new ByLabel("this is a textupload file widget field", false));

		String filePath = getFilePath("attachment1.jpg");
		wMultiFileWidgetWebElement.attachFile(filePath);

		validatingButton.click();
//		validatingButton.
		assertEquals(1, wMultiFileWidgetWebElement.findFilesAttachedCount());


		int i = 0;
		i++;


	}

	private String getFilePath(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		return file.getAbsolutePath();
	}

}
