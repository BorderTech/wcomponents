package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WProgressBar;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WProgressBarRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WProgressBarRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WProgressBar progressBar = new WProgressBar();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(progressBar) instanceof WProgressBarRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		BeanProvider provider = new BeanProvider() {
			@Override
			public Object getBean(final BeanProviderBound beanProviderBound) {
				return 13;
			}
		};

		WProgressBar progressBar = new WProgressBar(WProgressBar.ProgressBarType.NORMAL,
				WProgressBar.UnitType.FRACTION);
		progressBar.setBeanProvider(provider);
		progressBar.setMax(33);

		assertSchemaMatch(progressBar);

		progressBar = new WProgressBar(WProgressBar.ProgressBarType.SMALL,
				WProgressBar.UnitType.PERCENTAGE);
		progressBar.setBeanProvider(provider);
		progressBar.setMax(33);

		assertSchemaMatch(progressBar);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WProgressBar progressBar = new WProgressBar(WProgressBar.ProgressBarType.NORMAL,
				WProgressBar.UnitType.FRACTION);
		progressBar.setMax(100);

		progressBar.setText(getMaliciousContent());

		assertSafeContent(progressBar);

		progressBar.setToolTip(getMaliciousAttribute("ui:progressbar"));
		assertSafeContent(progressBar);

		progressBar.setAccessibleText(getMaliciousAttribute("ui:progressbar"));
		assertSafeContent(progressBar);
	}
}
