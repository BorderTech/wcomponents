package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WHiddenComment;
import com.github.bordertech.wcomponents.WPanel;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * JUnit test case for {@link WHiddenCommentRenderer}.
 *
 * @author Darian Bridge
 * @since 1.0.0
 */
public class WHiddenCommentRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Xpath for the comment element.
	 */
	private static final String COMMENT_XPATH = "//ui:comment";

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WHiddenComment hiddenComment = new WHiddenComment("comment");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(hiddenComment) instanceof WHiddenCommentRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		String textString1 = "test comment1";
		String textString2 = "test comment2";
		WHiddenComment comment = new WHiddenComment();

		// No comment
		assertSchemaMatch(comment);
		assertXpathEvaluatesTo("", COMMENT_XPATH, comment);

		// Default comment
		comment.setText(textString1);
		assertSchemaMatch(comment);
		assertXpathEvaluatesTo(textString1, COMMENT_XPATH, comment);

		// Context Comment
		setActiveContext(createUIContext());
		comment.setText(textString2);
		assertSchemaMatch(comment);
		assertXpathEvaluatesTo(textString2, COMMENT_XPATH, comment);
	}

	@Test
	public void testMultipleComments() throws IOException, SAXException, XpathException {
		String textString1 = "test comment1";
		String textString2 = "test comment2";
		WHiddenComment comment1 = new WHiddenComment(textString1);
		WHiddenComment comment2 = new WHiddenComment(textString2);
		WPanel panel = new WPanel();
		panel.add(comment1);
		panel.add(comment2);

		// Validate
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("2", "count(//ui:comment)", panel);
	}

	@Test
	public void testEncodedComment() throws IOException, SAXException, XpathException {
		String textString = "test <br/> > <";
		WHiddenComment comment = new WHiddenComment(textString);

		// Validate
		assertSchemaMatch(comment);
		assertXpathEvaluatesTo(textString, COMMENT_XPATH, comment);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WHiddenComment comment = new WHiddenComment(getMaliciousContent());
		assertSafeContent(comment);
	}
}
