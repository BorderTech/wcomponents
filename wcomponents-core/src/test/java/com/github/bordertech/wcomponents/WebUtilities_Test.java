package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.TransformXMLTestHelper;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * WebUtilities_Test - unit tests for {@link WebUtilities}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WebUtilities_Test extends AbstractWComponentTestCase {

	@BeforeClass
	public static void setUp() {
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		TransformXMLTestHelper.reloadTransformer();
	}

	/**
	 * When these tests are done put things back as they were.
	 */
	@AfterClass
	public static void tearDownClass() {
		Config.reset();
		TransformXMLTestHelper.reloadTransformer();
	}

	@Test
	public void testGetProjectVersion() {
		String testVersion = "TEST VERSION";
		Config.getInstance().setProperty(ConfigurationProperties.PROJECT_VERSION, testVersion);
		Assert.assertEquals("Wrong project version returned", testVersion, WebUtilities.
				getProjectVersion());

		try {
			Config.getInstance().clearProperty(ConfigurationProperties.PROJECT_VERSION);
			WebUtilities.getProjectVersion();
			Assert.fail("An exception should have been thrown for a null project version");
		} catch (SystemException e) {
			Assert.assertNotNull("No error message included for null project version", e.
					getMessage());
		}
	}

	@Test
	public void testGetAncestorOfClass() {
		WContainer root = new WContainer();
		WTabSet tabs = new WTabSet();
		WDropdown dropdown = new WDropdown();
		WTextField text = new WTextField();

		root.add(tabs);
		tabs.addTab(dropdown, "dropdown tab", WTabSet.TAB_MODE_SERVER);
		tabs.addTab(text, "text tab", WTabSet.TAB_MODE_SERVER);

		Assert.assertNull("Incorrect ancestor returned", WebUtilities.getAncestorOfClass(
				WTabSet.class, null));
		Assert
				.assertSame("Incorrect ancestor returned", tabs, WebUtilities.getAncestorOfClass(
						WTabSet.class, dropdown));
		Assert.assertSame("Incorrect ancestor returned", root, WebUtilities.getAncestorOfClass(
				WComponent.class, tabs));
		Assert.assertNull("Incorrect ancestor returned", WebUtilities.getAncestorOfClass(
				WButton.class, dropdown));
		Assert.assertNull("Root ancestor should be null", WebUtilities.getAncestorOfClass(
				WComponent.class, root));
	}

	@Test
	public void testGetClosestOfClass() {
		WContainer root = new WContainer();
		WTabSet tabs = new WTabSet();
		WDropdown dropdown = new WDropdown();
		WTextField text = new WTextField();

		root.add(tabs);
		tabs.addTab(dropdown, "dropdown tab", WTabSet.TAB_MODE_SERVER);
		tabs.addTab(text, "text tab", WTabSet.TAB_MODE_SERVER);

		Assert.assertNull("Incorrect ancestor returned", WebUtilities.getClosestOfClass(
				WTabSet.class, null));
		Assert.assertSame("Incorrect ancestor returned", tabs, WebUtilities.getClosestOfClass(
				WTabSet.class, dropdown));
		Assert.assertSame("Incorrect ancestor returned", tabs, WebUtilities.getClosestOfClass(
				WComponent.class, tabs));
		Assert.assertSame("Incorrect ancestor returned", dropdown,
				WebUtilities.getClosestOfClass(WComponent.class, dropdown));
		Assert.assertSame("Incorrect ancestor returned", root, WebUtilities.getClosestOfClass(
				WComponent.class, root));
		Assert.assertNull("Incorrect ancestor returned", WebUtilities.getClosestOfClass(
				WButton.class, dropdown));
	}

	@Test
	public void testGetTop() {
		WContainer root = new WContainer();
		WTabSet tabs = new WTabSet();
		WDropdown dropdown = new WDropdown();

		root.add(tabs);
		tabs.addTab(dropdown, "dropdown tab", WTabSet.TAB_MODE_CLIENT);

		Assert.assertEquals("Incorrect top component returned for child", root, WebUtilities.getTop(dropdown));
		Assert.assertEquals("Incorrect top component returned for child", root, WebUtilities.getTop(tabs));
		Assert.assertEquals("Incorrect top component returned for top", root, WebUtilities.getTop(root));
	}

	@Test
	public void testFindClosestContext() {
		WContainer root = new WContainer();
		WComponent staticChild = new DefaultWComponent();
		root.add(staticChild);

		UIContext uic1 = new UIContextImpl();
		uic1.setUI(root);
		setActiveContext(uic1);
		String rootId = root.getId();
		String staticChildId = staticChild.getId();

		Assert.assertSame("Incorrect closest uic1 for root", uic1, WebUtilities.findClosestContext(
				rootId));
		Assert.assertSame("Incorrect closest uic1 for static child", uic1,
				WebUtilities.findClosestContext(staticChildId));

		// Test UIContext where components have been removed from the tree
		UIContext uic2 = new UIContextImpl();
		uic2.setUI(root);
		setActiveContext(uic2);
		root.remove(staticChild);

		Assert.assertSame("Incorrect closest uic2 for root", uic2, WebUtilities.findClosestContext(
				rootId));
		Assert.assertSame("Incorrect closest uic2 for removed child", uic2,
				WebUtilities.findClosestContext(staticChildId));
	}

	@Test
	public void testFindClosestContextWithRepeater() {
		WContainer root = new WContainer();
		WRepeater repeater = new WRepeater();
		WComponent repeatedComponent = new WText();
		repeater.setRepeatedComponent(repeatedComponent);
		List<String> data = new ArrayList<>(Arrays.asList(new String[]{"a", "b", "c"}));

		root.add(repeater);

		UIContext uic1 = new UIContextImpl();
		uic1.setUI(root);

		UIContext uic2 = new UIContextImpl();
		uic2.setUI(root);

		root.setLocked(true);
		setActiveContext(uic1);
		repeater.setData(data);

		Assert.assertSame("Incorrect closest uic1 for root", uic1, WebUtilities.findClosestContext(
				root.getId()));

		setActiveContext(uic2);
		Assert.assertSame("Incorrect closest uic2 for root", uic2, WebUtilities.findClosestContext(
				root.getId()));

		setActiveContext(uic1);
		UIContext rowContext = repeater.getRowContexts().get(0);
		setActiveContext(rowContext);
		String repeatedId = repeatedComponent.getId();
		setActiveContext(uic1);

		Assert.assertSame("Incorrect closest context for repeated row", rowContext,
				WebUtilities.findClosestContext(repeatedId));

		setActiveContext(uic2);
		Assert.assertSame("Incorrect closest uic2 for deleted row", uic2, WebUtilities.
				findClosestContext(repeatedId));
	}

	@Test
	public void testGetComponentById() {
		WContainer root = new WContainer();
		WComponent staticChild = new DefaultWComponent();
		root.add(staticChild);

		UIContext uic1 = new UIContextImpl();
		uic1.setUI(root);
		setActiveContext(uic1);

		String staticChildId = getComponentId(staticChild, uic1);

		Assert.assertSame("Incorrect component for root", root, WebUtilities.getComponentById(root.
				getId())
				.getComponent());
		Assert.assertSame("Incorrect context for root", uic1, WebUtilities.getComponentById(root.
				getId()).getContext());
		Assert.assertSame("Incorrect component for static child", staticChild,
				WebUtilities.getComponentById(staticChildId).getComponent());
		Assert.assertSame("Incorrect context for static child", uic1, WebUtilities.getComponentById(
				root.getId())
				.getContext());

		// Test UIContext where components have been removed from the tree
		UIContext uic2 = new UIContextImpl();
		uic2.setUI(root);
		setActiveContext(uic2);
		root.remove(staticChild);

		Assert.assertSame("Incorrect component for root", root, WebUtilities.getComponentById(root.
				getId())
				.getComponent());
		Assert.assertSame("Incorrect context for root", uic2, WebUtilities.getComponentById(root.
				getId()).getContext());
		Assert.assertNull("Incorrect component for removed child", WebUtilities.getComponentById(
				staticChildId));
	}

	@Test
	public void testGetComponentByIdWithRepeater() {
		WContainer root = new WContainer();
		WRepeater repeater = new WRepeater();
		WComponent repeatedComponent = new WText();
		repeater.setRepeatedComponent(repeatedComponent);
		List<String> data = new ArrayList<>(Arrays.asList(new String[]{"a", "b", "c"}));

		root.add(repeater);

		UIContext uic1 = new UIContextImpl();
		uic1.setUI(root);

		UIContext uic2 = new UIContextImpl();
		uic2.setUI(root);

		root.setLocked(true);
		setActiveContext(uic1);
		repeater.setData(data);

		Assert.assertSame("Incorrect component for root", root, WebUtilities.getComponentById(root.
				getId())
				.getComponent());
		Assert
				.assertSame("Incorrect context1 for root", uic1, WebUtilities.getComponentById(root.
						getId()).getContext());

		setActiveContext(uic2);
		Assert.assertSame("Incorrect component for root", root, WebUtilities.getComponentById(root.
				getId())
				.getComponent());
		Assert
				.assertSame("Incorrect context2 for root", uic2, WebUtilities.getComponentById(root.
						getId()).getContext());

		setActiveContext(uic1);
		UIContext rowContext = repeater.getRowContexts().get(0);
		String repeatedId = getComponentId(repeatedComponent, rowContext);

		Assert.assertSame("Incorrect component for repeated row", repeatedComponent,
				WebUtilities.getComponentById(repeatedId).getComponent());
		Assert.assertSame("Incorrect context for repeated row", rowContext, WebUtilities.
				getComponentById(repeatedId)
				.getContext());

		setActiveContext(uic2);
		Assert.assertNull("Incorrect component for deleted row", WebUtilities.getComponentById(
				repeatedId));
	}

	@Test
	public void testEscapeForUrl() {
		Assert.
				assertEquals("Incorrectly encoded null string", null, WebUtilities.
						escapeForUrl(null));
		Assert.assertEquals("Incorrectly encoded empty string", "", WebUtilities.escapeForUrl(""));
		Assert.
				assertEquals("Incorrectly encoded 1 char string", "x", WebUtilities.
						escapeForUrl("x"));
		Assert.assertEquals("Incorrectly encoded 1 special char string", "%20", WebUtilities.
				escapeForUrl(" "));

		// Text with multiple escapes
		String in = "Hello world slash/ question? amper& quote\" apos'";
		String expected = "Hello%20world%20slash%2f%20question%3f%20amper%26%20quote%22%20apos%27";
		Assert.assertEquals("Incorrectly escaped url", expected, WebUtilities.escapeForUrl(in));

		// Extended characters - 2 char encoding
		in = "\u0451";
		expected = "%d1%91";
		Assert.assertEquals("Incorrectly escaped url", expected, WebUtilities.escapeForUrl(in));

		// Extended characters - 3 char encoding
		in = "\u1eae";
		expected = "%e1%ba%ae";
		Assert.assertEquals("Incorrectly escaped url", expected, WebUtilities.escapeForUrl(in));

		// Extended characters - 2 and 3 char encoding
		in = "_\u0451\u1eae_";
		expected = "_%d1%91%e1%ba%ae_";
		Assert.assertEquals("Incorrectly escaped url", expected, WebUtilities.escapeForUrl(in));
	}

	@Test
	public void testEncode() {
		Assert.assertEquals("Incorrectly encoded null string", null, WebUtilities.encode(null));
		Assert.assertEquals("Incorrectly encoded empty string", "", WebUtilities.encode(""));
		Assert.assertEquals("Incorrectly encoded 1 char string", "x", WebUtilities.encode("x"));
		Assert.assertEquals("Incorrectly encoded 1 special char string", "&amp;", WebUtilities.
				encode("&"));
		Assert.assertEquals("Incorrectly encoded open bracket", "&#123;", WebUtilities.encode("{"));
		Assert.assertEquals("Incorrectly encoded close bracket", "&#125;", WebUtilities.encode("}"));

		String in = "Hello world greater> less< amper& quote\"\t\r\n";
		String expected = "Hello world greater&gt; less&lt; amper&amp; quote&quot;\t\r\n";
		Assert.assertEquals("Incorrectly encoded value", expected, WebUtilities.encode(in));

		in = characterRange(0, 32);
		Assert.assertEquals("Encode should blat special characters", "\t\n\r ", WebUtilities.encode(in));
	}

	@Test
	public void testDecode() {
		Assert.assertEquals("Incorrectly decoded null string", null, WebUtilities.decode(null));
		Assert.assertEquals("Incorrectly decoded empty string", "", WebUtilities.decode(""));
		Assert.assertEquals("Incorrectly decoded 1 char string", "x", WebUtilities.decode("x"));
		Assert.assertEquals("Incorrectly decoded 1 special char string", "&", WebUtilities.decode(
				"&amp;"));
		Assert.assertEquals("Incorrectly decoded open bracket", "{", WebUtilities.decode("&#123;"));
		Assert.assertEquals("Incorrectly decoded close bracket", "}", WebUtilities.decode("&#125;"));

		String in = "Hello world greater&gt; less&lt; amper&amp; quote&quot;";
		String expected = "Hello world greater> less< amper& quote\"";
		Assert.assertEquals("Incorrectly decoded value", expected, WebUtilities.decode(in));

		// Finally, check a encode/decode pair
		String encoded = WebUtilities.encode(expected);
		Assert.assertEquals("Incorrectly encoded/decoded value", expected, WebUtilities.decode(
				encoded));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPathNullURL() {
		// Should not allow null base URL
		WebUtilities.getPath(null, Collections.emptyMap());
	}

	@Test
	public void testGetPathSimple() {
		String baseUrl = "/foo";
		String url = WebUtilities.getPath(baseUrl, null);
		Assert.assertEquals("Incorrect path returned for base URL", baseUrl, url);
	}

	@Test
	public void testGetPathWithParameter() {
		// Simple case with adding one param
		String baseUrl = "/foo";
		Map<String, String> params = new HashMap<>();
		params.put("a", "b");
		String url = WebUtilities.getPath(baseUrl, params);
		String expected = "/foo?a=b";
		Assert.assertEquals("Incorrect path returned for base URL with one parameter", expected, url);
	}

	@Test
	public void testGetPathWithExistingParameter() {
		// Case with existing params and two in the map
		String baseUrl = "/foo?a=b";
		Map<String, String> params = new LinkedHashMap<>();
		params.put("c", "d");
		params.put("e", "f");
		String url = WebUtilities.getPath(baseUrl, params);
		String expected = "/foo?a=b&amp;c=d&amp;e=f";
		Assert.assertEquals("Incorrect path returned for base URL with existing parameters", expected, url);
	}

	@Test
	public void testGetPathWithAsJavascriptURL() {
		// Case with existing params and two in the map
		String baseUrl = "/foo?a=b";
		Map<String, String> params = new HashMap<>();
		params.put("c", "d");
		params.put("e", "f");
		// As a javascript url
		String url = WebUtilities.getPath(baseUrl, params, true);
		String expected = "/foo?a=b&c=d&e=f";
		Assert.assertEquals("Incorrect path returned for URL for javascript", expected, url);
	}

	@Test
	public void testGenerateRandom() {
		String random1 = WebUtilities.generateRandom();
		String random2 = WebUtilities.generateRandom();

		Assert.assertNotSame("Generated strings should be different", random1, random2);
	}

	@Test
	public void testIsActiveNamingContext() {
		Assert.assertFalse("Component is not a naming context",
				WebUtilities.isActiveNamingContext(new DefaultWComponent()));

		WContainer naming = new WContainer();
		// Not active
		Assert.assertFalse("Component is not an active naming context", WebUtilities.
				isActiveNamingContext(naming));

		// Make active (but no ID)
		naming.setNamingContext(true);
		Assert.assertFalse("Component is not an active naming context as no ID", WebUtilities.
				isActiveNamingContext(naming));

		// Set ID
		naming.setIdName("id");
		Assert.assertTrue("Component is an active naming context", WebUtilities.
				isActiveNamingContext(naming));
	}

	@Test
	public void testGetParentNamingContext() {
		// Create naming contexts
		WNamingContext context1 = new WNamingContext("A");
		WNamingContext context2 = new WNamingContext("B");
		WNamingContext context3 = new WNamingContext("C");

		// Children
		WContainer child1 = new WContainer();
		WContainer child2 = new WContainer();
		WContainer child3 = new WContainer();

		// Make context2 inactive
		context2.setNamingContext(false);

		// Make tree of components
		context1.add(child1);
		child1.add(context2);
		context2.add(child2);
		child2.add(context3);
		context3.add(child3);

		// Test tree
		Assert.assertNull("Naming context for context1 should be null", WebUtilities.
				getParentNamingContext(context1));

		Assert.assertEquals("Naming context for child1 should be context1", context1,
				WebUtilities.getParentNamingContext(child1));
		Assert.assertEquals("Naming context for child2 should be context1", context1,
				WebUtilities.getParentNamingContext(child2));
		Assert.assertEquals("Naming context for child3 should be context3", context3,
				WebUtilities.getParentNamingContext(child3));
	}

	@Test
	public void testRenderWithPlainText() {
		String msg = "Test error message";
		WText text = new WText(msg);
		String output = WebUtilities.render(text);
		Assert.assertEquals("Invalid output returned", msg, output);
	}

	@Test
	public void testRenderWithXML() {
		WText text = new WText(TransformXMLTestHelper.TEST_XML);
		text.setEncodeText(false);
		String output = WebUtilities.render(text);
		Assert.assertEquals("Invalid output with XML", TransformXMLTestHelper.TEST_XML, output);
	}

	@Test(expected = SystemException.class)
	public void testRenderToHtmlWithPlainText() {
		String msg = "Test error message";
		WText text = new WText(msg);
		// Text will fail as it is not valid XML
		String output = WebUtilities.renderWithTransformToHTML(new MockRequest(), text, false);
		Assert.assertEquals("Invalid html output returned", msg, output);
	}

	@Test
	public void testRenderToHtmlWithXML() {
		WText text = new WText(TransformXMLTestHelper.TEST_XML);
		text.setEncodeText(false);
		// Dont use PageShell as it wraps the XML with ui:root and test xslt does not pass the other tags
		String output = WebUtilities.renderWithTransformToHTML(new MockRequest(), text, false);
		Assert.assertEquals("Invalid html output with XML", TransformXMLTestHelper.EXPECTED, output);
	}

	@Test
	public void testEncodeBrackets() {
		String in = "{}<{}>";
		String out = "&#123;&#125;<&#123;&#125;>";
		Assert.assertEquals("Encode brackets not correct", out, WebUtilities.encodeBrackets(in));
	}

	@Test
	public void testEncodeBracketsWithNoBrackets() {
		String in = "<oranges>&#123;&#125;";
		String out = in;
		Assert.assertEquals("Encode brackets not correct", out, WebUtilities.encodeBrackets(in));
	}

	@Test
	public void testDecodeBrackets() {
		String in = "&#123;&#125;<>&#123;&#125;a";
		String out = "{}<>{}a";
		Assert.assertEquals("Decode brackets not correct", out, WebUtilities.decodeBrackets(in));
	}

	@Test
	public void testDecodeBracketsWithNoBrackets() {
		String in = "{}<>";
		String out = in;
		Assert.assertEquals("Decode brackets not correct", out, WebUtilities.decodeBrackets(in));
	}

	@Test
	public void testDoubleEncodeBrackets() {
		String in = "&#123;&#125;<>";
		String out = "&amp;#123;&amp;#125;<>";
		Assert.assertEquals("Double encode brackets not correct", out, WebUtilities.doubleEncodeBrackets(in));
	}

	@Test
	public void testDoubleEncodeBracketsWithNoBrackets() {
		String in = "then you win";
		String out = in;
		Assert.assertEquals("Double encode brackets not correct", out, WebUtilities.doubleEncodeBrackets(in));
	}

	@Test
	public void testDoubleEncodeBracketsWithMultipleMatches() {
		String in = "&#123;&#125;<> &#123;&#125;&#125;";
		String out = "&amp;#123;&amp;#125;<> &amp;#123;&amp;#125;&amp;#125;";
		Assert.assertEquals("Double encode brackets not correct", out, WebUtilities.doubleEncodeBrackets(in));
	}

	@Test
	public void testDoubleDecodeBrackets() {
		String in = "&amp;#123;&amp;#125;<>";
		String out = "&#123;&#125;<>";
		Assert.assertEquals("Double decode brackets not correct", out, WebUtilities.doubleDecodeBrackets(in));
	}

	@Test
	public void testDoubleDecodeBracketsWithNoMatches() {
		String in = "then you win";
		String out = in;
		Assert.assertEquals("Double decode brackets not correct", out, WebUtilities.doubleDecodeBrackets(in));
	}

	@Test
	public void testCreateTargetUrl() {

		String baseUrl = "/path";

		// Setup context
		UIContext uic = createUIContext();
		MockWEnvironment env = new MockWEnvironment();
		env.setPostPath(baseUrl);
		uic.setEnvironment(env);
		setActiveContext(uic);

		// Target URL with no hidden or additional parameters
		Targetable target = new MyTargetable();
		HashMap<String, String> expectedParams = new LinkedHashMap<>();
		expectedParams.put("wc_target", "TARGET");
		expectedParams.put("no-cache", null);
		String url = WebUtilities.createTargetUrl(target, null);
		assertCreatedURLCorrect("Target URL with no hidden or additional parameters. ", url, baseUrl, expectedParams, "&");

		// Target URL with hidden parameters
		// Setup hidden parameters
		HashMap<String, String> hiddenParams = new LinkedHashMap<>();
		hiddenParams.put(Environment.SESSION_TOKEN_VARIABLE, "session");
		hiddenParams.put(Environment.STEP_VARIABLE, "1");
		env.setHiddenParameters(hiddenParams);
		uic.setEnvironment(env);
		// Expected params
		expectedParams = new LinkedHashMap<>();
		expectedParams.put("wc_s", "1");
		expectedParams.put("wc_target", "TARGET");
		expectedParams.put("no-cache", null);
		url = WebUtilities.createTargetUrl(target, null);
		assertCreatedURLCorrect("Target URL with hidden parameter. ", url, baseUrl, expectedParams, "&");

		// Target URL with hidden parameters and additional
		// Setup additional params
		HashMap<String, String> additionalParams = new LinkedHashMap<>();
		additionalParams = new HashMap<>();
		additionalParams.put("c", "d");
		additionalParams.put("e", "f");
		// Expected params
		expectedParams = new LinkedHashMap<>();
		expectedParams.put("wc_s", "1");
		expectedParams.put("wc_target", "TARGET");
		expectedParams.put("no-cache", null);
		expectedParams.putAll(additionalParams);
		url = WebUtilities.createTargetUrl(target, null, additionalParams);
		assertCreatedURLCorrect("Target URL with hidden parameters and additional. ", url, baseUrl, expectedParams, "&");
	}

	@Test
	public void testCreateTargetUrlWithCache() {

		String baseUrl = "/path";
		String cacheKey = "CACHE";

		// Setup context
		UIContext uic = createUIContext();
		MockWEnvironment env = new MockWEnvironment();
		env.setPostPath(baseUrl);
		uic.setEnvironment(env);
		setActiveContext(uic);

		// Target URL with no hidden or additional parameters
		Targetable target = new MyTargetable();
		HashMap<String, String> expectedParams = new LinkedHashMap<>();
		expectedParams.put("wc_target", "TARGET");
		expectedParams.put("contentCacheKey", cacheKey);
		String url = WebUtilities.createTargetUrl(target, cacheKey);
		assertCreatedURLCorrect("Target URL with no hidden or additional parameters and CACHE. ", url, baseUrl, expectedParams, "&");

		// Target URL with hidden parameters
		// Setup hidden parameters
		HashMap<String, String> hiddenParams = new LinkedHashMap<>();
		hiddenParams.put(Environment.SESSION_TOKEN_VARIABLE, "session");
		hiddenParams.put(Environment.STEP_VARIABLE, "1");
		env.setHiddenParameters(hiddenParams);
		uic.setEnvironment(env);
		// Expected params
		expectedParams = new LinkedHashMap<>();
		expectedParams.put("wc_target", "TARGET");
		expectedParams.put("contentCacheKey", cacheKey);
		url = WebUtilities.createTargetUrl(target, cacheKey);
		assertCreatedURLCorrect("Target URL with hidden parameter and CACHE. ", url, baseUrl, expectedParams, "&");

		// Target URL with hidden parameters and additional
		// Setup additional params
		HashMap<String, String> additionalParams = new LinkedHashMap<>();
		additionalParams = new HashMap<>();
		additionalParams.put("c", "d");
		additionalParams.put("e", "f");
		// Expected params
		expectedParams = new LinkedHashMap<>();
		expectedParams.put("wc_target", "TARGET");
		expectedParams.put("contentCacheKey", cacheKey);
		expectedParams.putAll(additionalParams);
		url = WebUtilities.createTargetUrl(target, cacheKey, additionalParams);
		assertCreatedURLCorrect("Target URL with hidden parameters and additional and CACHE. ", url, baseUrl, expectedParams, "&");
	}

	/**
	 * Generates a range of characters.
	 *
	 * @param from The first character in the range (must be > 0).
	 * @param to The last character in the range (must be >= from).
	 * @return A string containing the character range.
	 */
	private static String characterRange(final int from, final int to) {
		StringBuilder result = new StringBuilder(to);
		for (int i = from; i <= to; i++) {
			result.append((char) i);
		}
		return result.toString();
	}

	/**
	 * Compare the URLS. The parameters of the URL must be equal but they do not have to be in the same order.
	 *
	 * @param msgPrefix message prefix for assert messages
	 * @param actualUrl the actual value
	 * @param expectedBase the expected value
	 * @param expectedParams the expected parameters
	 * @param separator the separator
	 */
	private void assertCreatedURLCorrect(final String msgPrefix, final String actualUrl, final String expectedBase, final Map<String, String> expectedParams, final String separator) {
		// compare the path section of urls (string compare)
		int paramStartIndex = actualUrl.indexOf('?');

		String actualURLBase = actualUrl.substring(0, paramStartIndex);
		String actualURLParams = actualUrl.substring(paramStartIndex + 1);

		// if the path elements of the url are not equal bail out now.
		Assert.assertEquals(msgPrefix + "The path elements of the URLs are not equal", expectedBase, actualURLBase);

		// Extract actual parameters
		Map<String, String> actualParams = new LinkedHashMap<>();
		String[] actualParamsSplit = actualURLParams.split(separator);
		for (String actual : actualParamsSplit) {
			String split[] = actual.split("=");
			actualParams.put(split[0], split[1]);
		}

		// Check parameter keys
		Assert.assertEquals(msgPrefix + "Expected parameter keys not on URL", expectedParams.keySet(), actualParams.keySet());

		// Check parameter values
		for (Map.Entry<String, String> entry : expectedParams.entrySet()) {
			String key = entry.getKey();
			String actualValue = actualParams.get(key);
			String expectedValue = entry.getValue();
			if (expectedValue == null) {
				// Null value used to indicate value is random and cannot be checked but is at least present
				Assert.assertNotNull(msgPrefix + "Parameter [" + key + "] has no value", actualValue);
			} else {
				Assert.assertEquals(msgPrefix + "Parameter [" + key + "] has incorrect value", expectedValue, actualValue);
			}
		}
	}

	/**
	 * A convenience method to retrieve the ID of the component in the given context.
	 *
	 * @param component the component.
	 * @param uic the context.
	 * @return the ID of the component in the given context.
	 */
	private static String getComponentId(final WComponent component, final UIContext uic) {
		UIContextHolder.pushContext(uic);

		try {
			return component.getId();
		} finally {
			UIContextHolder.popContext();
		}
	}

	private static class MyTargetable extends AbstractWComponent implements Targetable {

		@Override
		public String getTargetId() {
			return "TARGET";
		}

	}
}
