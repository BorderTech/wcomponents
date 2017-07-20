package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.util.DeviceType;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Parameterized test for device types in {@link ServletUtil}.
 *
 * @author Jonathan Austin
 * @since 1.2.5
 */
@RunWith(Parameterized.class)
public class ServletUtilDeviceType_Test {

	private static final String MOBILE_SAFARI_IPAD = "Mozilla/5.0 (iPad; CPU OS 9_3_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13G34 Safari/601.1";

	private static final String MOBILE_SAFARI_IPHONE = "Mozilla/5.0 (iPhone; CPU OS 9_3_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13G34 Safari/601.1";

	private static final String CHROME_WIN_10 = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";

	private static final String CHROME_MOBILE = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";

	private static final String CHROME_TABLET = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev>(KHTML, like Gecko) Chrome/<Chrome Rev> Safari/<WebKit Rev>";

	private static final String CHROME_DESKTOP_OSX = "Agent-String=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

	private static final String EDGE_DESKTOP = "Mozilla/5.0 (Windows NT 10.0; <64-bit tags>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Safari/<WebKit Rev> Edge/<EdgeHTML Rev>.<Windows Build>";

	private static final String EDGE_MOBILE = "Mozilla/5.0 (Windows Phone 10.0; Android <Android Version>; <Device Manufacturer>; <Device Model>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev> Edge/<EdgeHTML Rev>.<Windows Build>";

	private static final String OPERA_MOBILE = "gent-String=Mozilla/5.0 (Linux; Android 6.0.1; Nexus 6P Build/MTC20F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Mobile Safari/537.36 OPR/37.0.2192.105088";

	private static final String FIREFOX_MOBILE = "Agent-String=Mozilla/5.0 (Android 6.0.1; Mobile; rv:48.0) Gecko/48.0 Firefox/48.0";

	private static final String UC_MOBILE = "Agent-String=Mozilla/5.0 (Linux; U; Android 6.0.1; en-US; Nexus 6P Build/MTC20F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/11.0.0.828 U3/0.8.0 Mobile Safari/534.30";

	private final DeviceType expectedDeviceType;
	private final String desc;
	private final String userAgent;

	/**
	 *
	 * @param expectedDeviceType the expected device type
	 * @param desc the description of the user agent
	 * @param userAgent the user agent
	 */
	public ServletUtilDeviceType_Test(final DeviceType expectedDeviceType, final String desc, final String userAgent) {
		this.expectedDeviceType = expectedDeviceType;
		this.desc = desc;
		this.userAgent = userAgent;
	}

	/**
	 * @return the user agents to test
	 */
	@Parameterized.Parameters
	public static Collection devices() {
		return Arrays.asList(new Object[][]{
			{DeviceType.TABLET, "Mobile Safari iPad", MOBILE_SAFARI_IPAD},
			{DeviceType.MOBILE, "Mobile Safari iPhone", MOBILE_SAFARI_IPHONE},
			{DeviceType.NORMAL, "Chrome OSX", CHROME_DESKTOP_OSX},
			{DeviceType.NORMAL, "Chrome Win 10", CHROME_WIN_10},
			{DeviceType.MOBILE, "Chrome Mobile", CHROME_MOBILE},
			{DeviceType.TABLET, "Chrome Tablet", CHROME_TABLET},
			{DeviceType.NORMAL, "Edge desktop", EDGE_DESKTOP},
			{DeviceType.MOBILE, "Edge mobile", EDGE_MOBILE},
			{DeviceType.MOBILE, "Opera Mobile", OPERA_MOBILE},
			{DeviceType.MOBILE, "Firefox Mobile", FIREFOX_MOBILE},
			{DeviceType.MOBILE, "UC Mobile", UC_MOBILE}
		});
	}

	@Test
	public void testDeviceType() {

		// HttpServletRequest
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setHeader("User-Agent", userAgent);
		Assert.assertEquals("Incorrect device type for " + desc, expectedDeviceType, ServletUtil.getDevice(request));

		// ServletRequest
		ServletRequest req = new ServletRequest(request);
		Assert.assertEquals("Incorrect device type for " + desc + " using ServletRequest", expectedDeviceType, ServletUtil.getDevice(req));
	}

}
