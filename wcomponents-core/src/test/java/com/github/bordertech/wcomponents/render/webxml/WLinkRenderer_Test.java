package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.TestAction;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLink;
import com.github.bordertech.wcomponents.WPanel;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WLinkRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WLinkRenderer_Test extends AbstractWebXmlRendererTestCase {

	private static final String TEXT = "WLink_Test.text";
	private static final String LINK_URL = "http://localhost/WLink_Test.href?a=b&c=d";
	private static final String IMAGE_URL = "http://localhost/404.jpg";
	private static final char ACCESS_KEY = 'K';
	private static final String TITLE = "WLink_Test.title";
	private static final String REL = "WLink_Test.rel";

	@Test
	public void testRendererCorrectlyConfigured() {
		WLink component = new WLink();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WLinkRenderer);
	}

	@Test
	public void testRenderedFormat() throws IOException, SAXException, XpathException {
		// Test individual options
		WLink link = new WLink();
		assertSchemaMatch(link);

		link = new WLink();
		link.setText(TEXT);
		link.setOpenNewWindow(false);
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(TEXT, "normalize-space(//ui:link)", link);
		assertXpathNotExists("//ui:link/@type", link);
		assertXpathNotExists("//ui:link/@imagePosition", link);

		link = new WLink();
		link.setAccessKey(ACCESS_KEY);
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(Character.toString(ACCESS_KEY), "//ui:link/@accessKey", link);

		link = new WLink();
		link.setToolTip(TITLE);
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(TITLE, "//ui:link/@toolTip", link);

		link = new WLink();
		link.setRel(REL);
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(REL, "//ui:link/@rel", link);

		link = new WLink(null, LINK_URL);
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(LINK_URL, "//ui:link/@url", link);

		link = new WLink(null, LINK_URL);
		link.setOpenNewWindow(false);
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(LINK_URL, "//ui:link/@url", link);

		// Test all attributes at once
		link = new WLink(TEXT, LINK_URL);
		link.setAccessKey(ACCESS_KEY);
		link.setToolTip(TITLE);
		link.setRel(REL);
		link.setOpenNewWindow(true);
		link.setImageUrl(IMAGE_URL);
		link.setRenderAsButton(true);
		link.setImagePosition(WLink.ImagePosition.EAST);

		assertSchemaMatch(link);
		assertXpathEvaluatesTo(TEXT, "normalize-space(//ui:link)", link);
		assertXpathEvaluatesTo(Character.toString(ACCESS_KEY), "//ui:link/@accessKey", link);
		assertXpathEvaluatesTo(TITLE, "//ui:link/@toolTip", link);
		assertXpathEvaluatesTo(REL, "//ui:link/@rel", link);
		assertXpathEvaluatesTo(LINK_URL, "//ui:link/@url", link);
		assertXpathEvaluatesTo(IMAGE_URL, "//ui:link/@imageUrl", link);
		assertXpathEvaluatesTo("button", "//ui:link/@type", link);
		assertXpathEvaluatesTo("e", "//ui:link/@imagePosition", link);
		assertXpathEvaluatesTo(link.getTargetWindowName(), "//ui:link/ui:windowAttributes/@name",
				link);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WLink link = new WLink(getMaliciousContent(), "#");
		link.setRel(getMaliciousAttribute("ui:link"));

		assertSafeContent(link);

		link.setToolTip(getMaliciousAttribute("ui:link"));
		assertSafeContent(link);

		link.setAccessibleText(getMaliciousAttribute("ui:link"));
		assertSafeContent(link);
	}

	@Test
	public void testWindowSizeAttributes() throws IOException, SAXException, XpathException {
		WLink link = new WLink.Builder(TEXT, LINK_URL).build();
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(TEXT, "normalize-space(//ui:link)", link);
		assertXpathEvaluatesTo(LINK_URL, "//ui:link/@url", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@top", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@left", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@width", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@height", link);

		final int top = 123;
		final int left = 234;
		final int width = 345;
		final int height = 456;

		link = new WLink.Builder(TEXT, LINK_URL)
				.top(top).left(left).width(width).height(height)
				.build();

		assertSchemaMatch(link);
		assertXpathEvaluatesTo(TEXT, "normalize-space(//ui:link)", link);
		assertXpathEvaluatesTo(LINK_URL, "//ui:link/@url", link);
		assertXpathEvaluatesTo(String.valueOf(top), "//ui:link/ui:windowAttributes/@top", link);
		assertXpathEvaluatesTo(String.valueOf(left), "//ui:link/ui:windowAttributes/@left", link);
		assertXpathEvaluatesTo(String.valueOf(width), "//ui:link/ui:windowAttributes/@width", link);
		assertXpathEvaluatesTo(String.valueOf(height), "//ui:link/ui:windowAttributes/@height", link);
	}

	@Test
	public void testWindowFlags() throws IOException, SAXException, XpathException {
		WLink link = new WLink.Builder(TEXT, LINK_URL).build();
		assertSchemaMatch(link);
		assertXpathEvaluatesTo(TEXT, "normalize-space(//ui:link)", link);
		assertXpathEvaluatesTo(LINK_URL, "//ui:link/@url", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@resizable", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@showMenubar", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@showToolbar", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@showLocation", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@showStatus", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@showScrollbars", link);
		assertXpathNotExists("//ui:link/ui:windowAttributes/@showDirectories", link);

		// TODO this doesn't test that the renderer is using the correct properties to render.
		// We need a convenient way of testing that only one attribute is set at a time.
		link = new WLink.Builder(TEXT, LINK_URL)
				.resizable(true)
				.menubar(true)
				.toolbar(true)
				.location(true)
				.status(true)
				.scrollbars(true)
				.directories(true)
				.build();

		assertSchemaMatch(link);
		assertXpathEvaluatesTo(TEXT, "normalize-space(//ui:link)", link);
		assertXpathEvaluatesTo(LINK_URL, "//ui:link/@url", link);
		assertXpathEvaluatesTo("true", "//ui:link/ui:windowAttributes/@resizable", link);
		assertXpathEvaluatesTo("true", "//ui:link/ui:windowAttributes/@showMenubar", link);
		assertXpathEvaluatesTo("true", "//ui:link/ui:windowAttributes/@showToolbar", link);
		assertXpathEvaluatesTo("true", "//ui:link/ui:windowAttributes/@showLocation", link);
		assertXpathEvaluatesTo("true", "//ui:link/ui:windowAttributes/@showStatus", link);
		assertXpathEvaluatesTo("true", "//ui:link/ui:windowAttributes/@showScrollbars", link);
		assertXpathEvaluatesTo("true", "//ui:link/ui:windowAttributes/@showDirectories", link);
	}

	@Test
	public void testDoPaintWithAction() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WLink link = new WLink("test", "http://test");
		WPanel target1 = new WPanel();
		WPanel target2 = new WPanel();

		root.add(link);
		root.add(target1);
		root.add(target2);

		// No Targets (target itself)
		link.setAction(new TestAction());
		assertSchemaMatch(root);
		assertXpathEvaluatesTo(link.getId(), "//ui:ajaxtrigger/@triggerId", root);
		assertXpathEvaluatesTo("1", "count(//ui:ajaxtrigger/ui:ajaxtargetid)", root);
		assertXpathEvaluatesTo(link.getId(), "//ui:ajaxtrigger/ui:ajaxtargetid[1]/@targetId", root);

		// With Targets
		link.setAction(new TestAction(), target1, target2);
		assertSchemaMatch(root);
		assertXpathEvaluatesTo(link.getId(), "//ui:ajaxtrigger/@triggerId", root);
		assertXpathEvaluatesTo("2", "count(//ui:ajaxtrigger/ui:ajaxtargetid)", root);
		assertXpathEvaluatesTo(target1.getId(), "//ui:ajaxtrigger/ui:ajaxtargetid[1]/@targetId",
				root);
		assertXpathEvaluatesTo(target2.getId(), "//ui:ajaxtrigger/ui:ajaxtargetid[2]/@targetId",
				root);
	}

}
