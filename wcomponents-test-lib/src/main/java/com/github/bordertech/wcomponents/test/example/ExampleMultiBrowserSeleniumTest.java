package com.github.bordertech.wcomponents.test.example;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import org.junit.runner.RunWith;

/**
 * <p>
 * This class demonstrates running a test using multiple browsers. All that is necessary is the
 * "@RunWith(MultiBrowserRunner.class)" annotation at class level.</p>
 *
 * <p>
 * Note that this test class just inherits tests from the {@link ExampleSeleniumTest} example test case.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(MultiBrowserRunner.class)
public class ExampleMultiBrowserSeleniumTest extends ExampleSeleniumTest {
}
