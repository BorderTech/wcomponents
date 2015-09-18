package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * UserAgentInfo - Unit tests for {@link UserAgentInfo}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class UserAgentInfo_Test {

	/**
	 * IE6 user-agent header.
	 */
	private static final String IE6_HEADER = "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
	/**
	 * IE8 user-agent header.
	 */
	private static final String IE8_HEADER = "User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C)";
	/**
	 * Firefox 10 user-agent header.
	 */
	private static final String FIREFOX_HEADER = "User-Agent: Mozilla/5.0 (Windows NT 5.1; rv:10.0.2) Gecko/20100101 Firefox/10.0.2";
	/**
	 * Chrome 21.0 user-agent header.
	 */
	private static final String CHROME_HEADER = "User-Agent: Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";

	@Test
	public void testIsUnknown() {
		UserAgentInfo info = new UserAgentInfo();
		Assert.assertTrue("An unknown browser is unknown", info.isUnknown());

		info = new UserAgentInfo(null);
		Assert.assertTrue("An unknown browser is unknown", info.isUnknown());

		info = new UserAgentInfo("");
		Assert.assertTrue("An unknown browser is unknown", info.isUnknown());

		info = new UserAgentInfo(IE6_HEADER);
		Assert.assertFalse("IE6 is not unknown", info.isUnknown());
	}

	@Test
	public void testIsIE() {
		UserAgentInfo info = new UserAgentInfo();
		Assert.assertFalse("An unknown browser is not IE", info.isIE());

		info = new UserAgentInfo(CHROME_HEADER);
		Assert.assertFalse("Chrome is not IE", info.isIE());

		info = new UserAgentInfo(IE8_HEADER);
		Assert.assertTrue("IE8 is IE", info.isIE());
	}

	@Test
	public void testIsIE6() {
		UserAgentInfo info = new UserAgentInfo();
		Assert.assertFalse("An unknown browser is not IE6", info.isIE6());

		info = new UserAgentInfo(FIREFOX_HEADER);
		Assert.assertFalse("Firefox is not IE6", info.isIE6());

		info = new UserAgentInfo(IE8_HEADER);
		Assert.assertFalse("IE8 is not IE6", info.isIE6());

		info = new UserAgentInfo(IE6_HEADER);
		Assert.assertTrue("IE6 is IE6", info.isIE6());
	}
}
