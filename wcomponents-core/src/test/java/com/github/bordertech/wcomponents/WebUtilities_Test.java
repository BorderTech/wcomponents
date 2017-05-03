package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.TransformXMLTestHelper;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.commons.httpclient.util.URIUtil;
import org.junit.AfterClass;
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

	// @Test
	// public void testGetWComponentPath()
	// {
	// // Simple test, one root element.
	// WContainer root = new WContainer();
	// UIContext uic = new UIContextImpl();
	// uic.setUI(root);
	//
	// List<WComponentPathElement> path = WebUtilities.getWComponentPath(root, root.getId(), false);
	// List<WComponentPathElement> expected = Arrays.asList(new WComponentPathElement[]
	// {
	// new WComponentPathElement(root)
	// });
	// Assert.assertEquals("Incorrect path", expected, path);
	//
	// // Add a static child
	// WContainer staticChild = new WContainer();
	// root.add(staticChild);
	//
	// path = WebUtilities.getWComponentPath(root, staticChild.getId(), false);
	// expected = Arrays.asList(new WComponentPathElement[]
	// {
	// new WComponentPathElement(root),
	// new WComponentPathElement(staticChild)
	// });
	// Assert.assertEquals("Incorrect path", expected, path);
	//
	// // Add a dynamic child
	// root.setLocked(true);
	// setActiveContext(uic);
	// WComponent dynamicChild = new DefaultWComponent();
	// staticChild.add( dynamicChild);
	//
	// path = WebUtilities.getWComponentPath(root, dynamicChild.getId(), false);
	// expected = Arrays.asList(new WComponentPathElement[]
	// {
	// new WComponentPathElement(root),
	// new WComponentPathElement(staticChild),
	// new WComponentPathElement(dynamicChild),
	// });
	// Assert.assertEquals("Incorrect path", expected, path);
	//
	// // Test against another context with strict - should not find dynamic child
	// String dynamicChildId = dynamicChild.getId();
	// UIContext otherUic = new UIContextImpl();
	// otherUic.setUI(root);
	// setActiveContext(otherUic);
	//
	// path = WebUtilities.getWComponentPath(root, dynamicChildId, false);
	// Assert.assertNull("Path should not have been found", path);
	//
	// // Test against another context with tolerant - should return up to the static child
	// path = WebUtilities.getWComponentPath(root, dynamicChildId, true);
	// expected = Arrays.asList(new WComponentPathElement[]
	// {
	// new WComponentPathElement(root),
	// new WComponentPathElement(staticChild)
	// });
	// Assert.assertEquals("Incorrect path", expected, path);
	// }
	//
	// @Test
	// public void testGetWComponentPathWithRepeater()
	// {
	// WContainer root = new WContainer();
	// UIContext uic = new UIContextImpl();
	// uic.setUI(root);
	// WRepeater repeater = new WRepeater();
	// WComponent repeatedComponent = new WText();
	// List<String> data = new ArrayList<String>(Arrays.asList(new String[] { "a", "b", "c" }));
	//
	// repeater.setRepeatedComponent(repeatedComponent);
	// root.add(repeater);
	//
	// setActiveContext(uic);
	// repeater.setData(data);
	// List<UIContext> contexts = repeater.getRowContexts();
	//
	// for (int i = 0; i < data.size(); i++)
	// {
	// UIContext rowContext = contexts.get(i);
	// String repeatedComponentId = getComponentId(repeatedComponent, rowContext);
	//
	// List<WComponentPathElement> path = WebUtilities.getWComponentPath(root, repeatedComponentId, false);
	// List<WComponentPathElement> expected = Arrays.asList(new WComponentPathElement[]
	// {
	// new WComponentPathElement(root),
	// new WComponentPathElement(repeater, i),
	// new WComponentPathElement(repeater.getRepeatRoot()),
	// new WComponentPathElement(repeatedComponent)
	// });
	//
	// Assert.assertEquals("Incorrect path for row " + i, expected, path);
	// }
	//
	// // Test when a row is removed from the repeater
	// UIContext rowContext = contexts.get(contexts.size() - 1);
	// data.remove(data.size() - 1);
	//
	// // Strict should return null
	// UIContextHolder.pushContext(rowContext);
	// String repeatedComponentId = getComponentId(repeatedComponent, rowContext);
	//
	// List<WComponentPathElement> path = WebUtilities.getWComponentPath(root, repeatedComponentId, false);
	// Assert.assertNull("Path should not have been found in strict mode after row removal", path);
	//
	// // Tolerant should return up to the repeater
	// path = WebUtilities.getWComponentPath(root, repeatedComponentId, true);
	// List<WComponentPathElement> expected = Arrays.asList(new WComponentPathElement[]
	// {
	// new WComponentPathElement(root),
	// new WComponentPathElement(repeater),
	// new WComponentPathElement(repeater.getRepeatRoot()),
	// new WComponentPathElement(repeatedComponent)
	// });
	//
	// Assert.assertEquals("Incorrect tolerant path after row removal", expected, path);
	// }
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

	@Test
	public void testGetPath() {
		// Simple case
		String url = "/foo";
		String expected = "/foo";
		Assert.assertEquals("Incorrect path returned for " + url, expected, WebUtilities.
				getPath(url, null));

		// Simple case with one param
		Map<String, String> params = new HashMap<>();
		params.put("a", "b");

		url = "/foo";
		expected = "/foo?a=b";
		Assert.assertEquals("Incorrect path returned for " + url + " with a=b", expected,
				WebUtilities.getPath(url, params));

		// Case with existing params and two in the map
		params = new HashMap<>();
		params.put("c", "d");
		params.put("e", "f");

		url = "/foo?a=b";
		expected = "/foo?a=b&amp;c=d&amp;e=f";

		assertURLEquals(expected, WebUtilities.getPath(url, params), "&amp;");

		// As a javascript url
		expected = "/foo?a=b&c=d&e=f";
		assertURLEquals(expected, WebUtilities.getPath(url, params, true), "&");
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
	public void testEscapeUrlBasic() {
		String in = "http://test.com";
		Assert.assertEquals("Basic url not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlBasic2() {
		String in = "http://test.com/";
		Assert.assertEquals("Basic url not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlBasicParameters() {
		String in = "http://test.com?a=foo";
		Assert.assertEquals("Basic url not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlBasicParameters2() {
		String parms = "a=foo&b=g h j";
		String in = "http://test.com?" + parms;
		String out = "http://test.com?" + encodeQuery(parms);
		Assert.assertEquals("Basic url not escaped correctly", out, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlBasicParameters3() {
		String parms = "a=foo&b=<>{{}}";
		String in = "http://test.com?" + parms;
		String out = "http://test.com?" + encodeQuery(parms);
		Assert.assertEquals("Basic url not escaped correctly", out, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlRelBasic() {
		String in = "rel/";
		Assert.assertEquals("Basic url not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlRelBasicParameters() {
		String in = "rel?a=foo";
		Assert.assertEquals("Basic url not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlRelBasicParameters2() {
		String parms = "a=foo&b=g h j";
		String in = "rel?" + parms;
		String out = "rel?" + encodeQuery(parms);
		Assert.assertEquals("Basic url not escaped correctly", out, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlRelBasicParameters3() {
		String parms = "a=foo&b=<>{{}}";
		String in = "rel?" + parms;
		String out = "rel?" + encodeQuery(parms);
		Assert.assertEquals("Basic url not escaped correctly", out, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlRef() {
		String in = "#REF1";
		Assert.assertEquals("Basic Reference not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlRef2() {
		String in = "rel?#REF1";
		Assert.assertEquals("Basic Reference not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEscapeUrlRef3() {
		String in = "rel?a=foo#REF1";
		Assert.assertEquals("Basic Reference not escaped correctly", in, WebUtilities.percentEncodeUrl(in));
	}

	@Test
	public void testEncodeUrl() {
		String in = "http://test.com?a=foo&b=<>{}#REF1";
		String out = "http://test.com?a=foo&amp;b=%3C%3E%7B%7D#REF1";
		Assert.assertEquals("Encoded URL not correct", out, WebUtilities.encodeUrl(in));
	}

//	@Test
//	public void testContainsBrackets() {
//		Assert.assertTrue("Contains a open bracket", WebUtilities.containsBrackets("{"));
//		Assert.assertTrue("Contains a closed bracket", WebUtilities.containsBrackets("}"));
//		Assert.assertFalse("Contains an encoded open bracket", WebUtilities.containsBrackets("&#123;"));
//		Assert.assertFalse("Contains an encoded closed bracket", WebUtilities.containsBrackets("&#125;"));
//		Assert.assertFalse("Contains a double encoded open bracket", WebUtilities.containsBrackets("&amp;#123;"));
//		Assert.assertFalse("Contains a double encoded closed bracket", WebUtilities.containsBrackets("&amp;#125;"));
//	}

//	@Test
//	public void testContainsEncodeBrackets() {
//		Assert.assertTrue("Contains an encoded open bracket", WebUtilities.containsEncodedBrackets("&#123;"));
//		Assert.assertTrue("Contains an encoded closed bracket", WebUtilities.containsEncodedBrackets("&#125;"));
//		Assert.assertFalse("Contains a double encoded open bracket", WebUtilities.containsEncodedBrackets("&amp;#123;"));
//		Assert.assertFalse("Contains a double encoded closed bracket", WebUtilities.containsEncodedBrackets("&amp;#125;"));
//		Assert.assertFalse("Contains a open bracket", WebUtilities.containsEncodedBrackets("{"));
//		Assert.assertFalse("Contains a closed bracket", WebUtilities.containsEncodedBrackets("}"));
//	}

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

	/**
	 *
	 * @param input the query string input
	 * @return the percent encoded query string
	 */
	private String encodeQuery(final String input) {
		try {
			return URIUtil.encodeQuery(input);
		} catch (Exception e) {
			throw new IllegalStateException("Could not encode input string [" + input + "].");
		}
	}

	/**
	 * Generates a range of characters.
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

//	/**
//	 * Set up and execute the updateBeanValue method with the given parameter.
//	 * If the parameter is null then the default updateBeanValue(component) method will be invoked.
//	 *
//	 * @param visibleOnly the parameter to pass to WebUtilities.updateBeanValue(component, visibleOnly).
//	 */
//	private void runUpdateBeanValue(final Boolean visibleOnly) {
//		final String directChild = "directChild";
//		final String grandChild = "grandChild";
//		final String invisibleGrandChild = "invisibleGrandChild";
//		final String childOfInvisibleContainer = "childOfInvisibleContainer";
//
//		Map<String, String> beanMap = new HashMap<>();
//		beanMap.put(directChild, null);
//		beanMap.put(grandChild, null);
//		beanMap.put(invisibleGrandChild, null);
//		beanMap.put(childOfInvisibleContainer, null);
//
//		WContainer root = new WContainer();
//		root.setBean(beanMap);
//		WTextField childTextField = new WTextField();
//		childTextField.setBeanProperty(directChild);
//		root.add(childTextField);
//
//		WContainer childContainer = new WContainer();
//		root.add(childContainer);
//		WTextField grandChildTextField = new WTextField();
//		grandChildTextField.setBeanProperty(grandChild);
//		childContainer.add(grandChildTextField);
//
//		WTextField invisibleGrandChildTextField = new WTextField();
//		invisibleGrandChildTextField.setBeanProperty(invisibleGrandChild);
//		invisibleGrandChildTextField.setVisible(false);
//		childContainer.add(invisibleGrandChildTextField);
//
//		WContainer invisibleContainer = new WContainer();
//		invisibleContainer.setVisible(false);
//		root.add(invisibleContainer);
//		WTextField childOfInivisbleContainerTextField = new WTextField();
//		childOfInivisbleContainerTextField.setBeanProperty(childOfInvisibleContainer);
//		invisibleContainer.add(childOfInivisbleContainerTextField);
//
//		root.setLocked(true);
//		setActiveContext(createUIContext());
//
//		childTextField.setData(directChild);
//		grandChildTextField.setData(grandChild);
//		invisibleGrandChildTextField.setData(invisibleGrandChild);
//		childOfInivisbleContainerTextField.setData(childOfInvisibleContainer);
//
//		if (visibleOnly == null) {
//			WebUtilities.updateBeanValue(root);
//		} else {
//			WebUtilities.updateBeanValue(root, visibleOnly);
//		}
//
//		Assert.assertEquals("updateBeanValue failed to update directChild with visibleOnly=[" + visibleOnly + "]", directChild, beanMap.get(directChild));
//		Assert.assertEquals("updateBeanValue failed to update grandChild with visibleOnly=[" + visibleOnly + "]", grandChild, beanMap.get(grandChild));
//		Assert.assertEquals("updateBeanValue updated an incorrect value for invisibleGrandChild with visibleOnly=[" + visibleOnly + "]", BooleanUtils.isNotFalse(visibleOnly) ? null : invisibleGrandChild, beanMap.get(invisibleGrandChild));
//		Assert.assertEquals("updateBeanValue updated an incorrect value for childOfInvisibleContainer with visibleOnly=[" + visibleOnly + "]", BooleanUtils.isNotFalse(visibleOnly) ? null : childOfInvisibleContainer, beanMap.get(childOfInvisibleContainer));
//	}
	/**
	 * Compare the URLS. The parameters of the URL must be equal but they do not have to be in the same order.
	 *
	 * @param actual the actual value
	 * @param expected the expected value
	 * @param separator the separator
	 */
	private void assertURLEquals(final String actual, final String expected, final String separator) {
		// compare the path section of urls (string compare)
		int paramStartIndex = actual.indexOf('?');

		// if the path elements of the url are not equal bail out now.
		Assert.assertTrue("The path elements of the URLs are not equal",
				expected.startsWith(actual.substring(0, paramStartIndex)));

		// now compare the parameters of each URL
		String expectedURLParams = expected.substring(paramStartIndex + 1);
		String actualURLParams = actual.substring(paramStartIndex + 1);

		String[] expectedParams = expectedURLParams.split(separator);
		String[] actualParams = actualURLParams.split(separator);

		int params = expectedParams.length;

		Assert.assertEquals("The number of parameters in URLs are not equal", params,
				actualParams.length);

		List<String> expectedParamArray = Arrays.asList(expectedParams);
		List<String> actualParamArray = Arrays.asList(actualParams);

		Assert.assertTrue("The parameters contained in the URLs are not equal",
				actualParamArray.containsAll(expectedParamArray));
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
}
