package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.PerformanceTests;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Rudimentary performance test of HTML Sanitizer.
 * @author Mark Reeves
 * @since 1.2.0
 */
@Category(PerformanceTests.class)
public class HTMLSanitizerPerformance_Test extends AbstractWComponentTestCase {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(HTMLSanitizerPerformance_Test.class);

	private static final String LONG_HTML = "<div><p id='one' class='dot' style='padding:1em;'>Lorem ipsum dolor sit "
			+ "amet, consectetur "
			+ "adipiscing elit. Vestibulum eget justo eget ipsum placerat tempor. Nullam at auctor quam. Donec at "
			+ "ante leo. Proin a eros nunc. Cras euismod, lorem quis viverra porta, nisl orci hendrerit mi, sit "
			+ "amet hendrerit lectus massa eget eros. Cras semper est in aliquet laoreet. Phasellus volutpat "
			+ "iaculis ultrices. Sed ac feugiat nibh, at facilisis arcu. Nulla elit ligula, molestie sit amet "
			+ "mauris ut, placerat gravida arcu.</p><p accesskey='A'>Morbi et ante imperdiet, egestas nunc vitae, "
			+ "mollis dui. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis "
			+ "egestas. Maecenas quis metus in ante dictum ultrices. Maecenas justo mi, euismod laoreet consectetur"
			+ " sed, porttitor mollis tortor. Ut gravida sapien ac nunc porttitor suscipit. Nulla in nibh sit amet "
			+ "orci placerat facilisis. Duis vel consequat metus. Vivamus tempus euismod pellentesque. Interdum et "
			+ "malesuada fames ac ante ipsum primis in faucibus. Aliquam bibendum ut metus eu dignissim.</p>"
			+ "<p tabindex='0'>Sed sagittis est nisi, eu vulputate ex laoreet a. Aliquam luctus, orci ut lobortis "
			+ "sagittis, arcu odio imperdiet ante, quis mollis massa dolor elementum elit. Integer sed dui sed "
			+ "metus dapibus elementum nec a enim. Suspendisse nec commodo ex. Vivamus faucibus ante sed sem "
			+ "condimentum eleifend vitae et sapien. Nullam ultrices libero id nunc pretium commodo. Nullam "
			+ "posuere elit bibendum fringilla maximus.</p><p role='tab'>Mauris nec aliquet nulla. Sed sed "
			+ "fermentum odio. Cras ut lacus gravida, tempus turpis ac, rhoncus elit. In dictum dui libero, quis "
			+ "tempor mi aliquam ut. Ut fringilla venenatis scelerisque. Suspendisse eu orci vel nisl tempor "
			+ "vehicula eu non ligula. Nam sit amet urna eu turpis tempus imperdiet.</p><p lang='en-gb'>Praesent "
			+ "in turpis est. Etiam sit amet ultrices sapien. Quisque rutrum porta vulputate. Proin pulvinar, nibh"
			+ " feugiat accumsan dapibus, nisl mauris tempor libero, a blandit massa ipsum sed lacus. Fusce id elit"
			+ " non eros laoreet mollis. Aliquam consectetur ligula et rhoncus mattis. Fusce posuere neque id ante "
			+ "sodales sagittis. Aenean suscipit consectetur felis ut pulvinar. Phasellus dapibus justo eget "
			+ "sodales mattis. Quisque commodo sem quis lectus convallis, facilisis lacinia odio vehicula. In a "
			+ "justo sapien.</p></div><ul onclick='alert();'><li class='dot'>one</li><li class='dot'>two</li>"
			+ "<li class='dot'>three</li><li class='dot'>for</li><li class='dot'>five</li></ul>";


	@Test
	public void testSanitizerPerformance2() {
		final int fewLoops = 20;
		final int manyLoops = fewLoops * 1000;

		long fewLoopTotalTime = timeSanitizedData(fewLoops);
		long manyLoopTotalTime = timeSanitizedData(manyLoops);

		long fewLoopTime = fewLoopTotalTime / fewLoops;
		long manyLoopTime = manyLoopTotalTime / manyLoops;


		LOG.info(String.valueOf(fewLoops) + " runs of sanitizer time: " + (fewLoopTotalTime / 1000000000.0) + "s");
		LOG.info(String.valueOf(manyLoops) + " runs of sanitizer time: " + (manyLoopTotalTime / 1000000000.0) + "s");

		LOG.info(String.valueOf(fewLoops) + " runs of sanitizer time: " + (fewLoopTime / 1000000.0) + "ms per run");
		LOG.info(String.valueOf(manyLoops) + " runs of sanitizer time: " + (manyLoopTime / 1000000.0) + "ms per run");

		Assert.assertTrue("Sanitizing many entries should not exceed sanitizing few",
				manyLoopTime <= fewLoopTime);
	}


	/**
	 * run many sanitized getData calls.
	 * @param count the number of loops to run
	 * @return the time taken to run all the runnables.
	 */
	private long timeSanitizedData(final int count) {
		Runnable runnable;
		runnable = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < count; i++) {
					HtmlSanitizerUtil.sanitize(LONG_HTML);
				}
			}
		};
		return time(runnable);
	}
}
