package com.github.bordertech.wcomponents.util;

import java.io.StringWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * WhiteSpaceFilterPrintWriter_Test - unit tests for {@link WhiteSpaceFilterPrintWriter}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class WhiteSpaceFilterPrintWriter_Test {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WhiteSpaceFilterPrintWriter_Test.class);

	/**
	 * Error message prefix to display on test failure.
	 */
	private static final String ERROR_MESSAGE = "Incorrect filter result for: \"";

	@Test
	public void testSimpleHTML1() {
		//Shouldn't strip anything
		String input = "<html> <body>foo</body> </html>";
		String expected = "<html> <body>foo</body> </html>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testSimpleHTML2() {
		//should strip leading whitespace and extra whitespace between text and tags
		String input = " <ui:root>  foo    foo  </ui:root>";
		String expected = "<ui:root> foo foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testPre() {
		//shouldn't strip anything within the pre block
		String input = "<ui:root>   foo   <pre>   foo   foo   </pre>   foo   </ui:root>";
		String expected = "<ui:root> foo <pre>   foo   foo   </pre> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testPreSingleAttribute() {
		//shouldn't strip anything within the pre block with a single attribute
		String input = "<ui:root><PRE style='font-weight: bold'>   foo   foo   </PRE></ui:root>";
		String expected = "<ui:root><PRE style='font-weight: bold'>   foo   foo   </PRE></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testPreMultipleAttributes() {
		//shouldn't strip anything within the pre block with multiple attributes
		String input = "<ui:root><pre id='</pre>' dquoted=\"single'quote\" quoted='double\"quote'>  foo  foo  </pre></ui:root>   ";
		String expected = "<ui:root><pre id='</pre>' dquoted=\"single'quote\" quoted='double\"quote'>  foo  foo  </pre></ui:root> ";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testParagraphTag1() {
		//Make sure it doesn't get <p> confused with the start of <pre>
		//should strip whitespace within the paragraph
		String input = "<ui:root>   foo   <p>   foo   foo   </p>   foo   </ui:root>";
		String expected = "<ui:root> foo <p> foo foo </p> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testParagraphTag2() {
		//Make sure it doesn't get <p> confused with the start of <pre>
		//shouldn't get confused by self-closing tag
		String input = "<ui:root>   foo   <p/>   foo   </ui:root>";
		String expected = "<ui:root> foo <p/> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testFakePrehistoricTag() {
		//tests that <prexyz> isn't confused with <pre>
		//should strip whitespace within the "prehistoric" block
		String input = "<ui:root>   foo   <prehistoric>   foo   foo   </prehistoric>   foo   </ui:root>";
		String expected = "<ui:root> foo <prehistoric> foo foo </prehistoric> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testTextAreaNoAttribute() {
		//shouldn't strip anything within the text area block
		String input = "<ui:root>   foo   <textarea>   foo   foo   </textarea>   foo   </ui:root>";
		String expected = "<ui:root> foo <textarea>   foo   foo   </textarea> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testTextAreaSingleAttribute() {
		//shouldn't strip anything within the text area block or modify attribute
		String input = "<ui:root><TEXTAREA name='foo'>   foo   foo   </TEXTAREA></ui:root>";
		String expected = "<ui:root><TEXTAREA name='foo'>   foo   foo   </TEXTAREA></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testTextAreaMultipleAttributes() {
		//shouldn't strip anything within the text area block or modify attributes
		String input = "<ui:root><textarea name='foo' dquoted=\"single'quote\" quoted='double\"quote'>  foo  foo  </textarea></ui:root>   ";
		String expected = "<ui:root><textarea name='foo' dquoted=\"single'quote\" quoted='double\"quote'>  foo  foo  </textarea></ui:root> ";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testCdata() {
		//shouldn't strip anything within the cdata block
		String input = "<ui:root>   foo   <![CDATA[   foo   foo   ]]>   foo   </ui:root>";
		String expected = "<ui:root> foo <![CDATA[   foo   foo   ]]> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testScript() {
		//shouldn't strip anything within the script block
		String input = "<html>   <head><script>  var   foo = 'foo' ; </script> </head>  <body>   foo   </ui:root>";
		String expected = "<html> <head><script>  var   foo = 'foo' ; </script> </head> <body> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testScript2() {
		//shouldn't strip anything within the script block
		String input = "<html>   <head><script>//<!--\nvar   foo = 'foo';\n//--></script> </head>  <body>   foo   </ui:root>";
		String expected = "<html> <head><script>//<!--\nvar   foo = 'foo';\n//--></script> </head> <body> foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testComment1() {
		//should strip out everything in the comment block
		String input = "<ui:root><!-- foo --></ui:root>";
		String expected = "<ui:root><!-- --></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testComment2() {
		//should strip out everything in the comment blocks, strip whitespace and leave the rubbish tags intact
		String input = "<ui:root> <!> <!-> <!-->foo->foo--foo--><!---->   foo  foo  </ui:root>";
		String expected = "<ui:root> <!> <!-> <!-- --><!-- --> foo foo </ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testUiElementsBasic() {
		// ui:text - Whitespace should NOT be stripped
		String input = "<ui:root><ui:text>  foo  foo  <ui:text></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:textarea - Whitespace should NOT be stripped
		input = "<ui:root><ui:textarea>  foo  foo  <ui:textarea></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:message - Whitespace should NOT be stripped
		input = "<ui:root><ui:message>  foo  foo  <ui:message></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:error - Whitespace should NOT be stripped
		input = "<ui:root><ui:error>  foo  foo  <ui:error></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:foo - Whitespace should be stripped
		input = "<ui:root><ui:foo>  foo  foo  <ui:foo></ui:root>";
		String expected = "<ui:root><ui:foo> foo foo <ui:foo></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));

		// Test elements that start with the same character as elements that are stripped
		// ui:tfoo - Whitespace should be stripped
		input = "<ui:root><ui:tfoo>  foo  foo  <ui:tfoo></ui:root>";
		expected = "<ui:root><ui:tfoo> foo foo <ui:tfoo></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));

		// ui:textfoo - Whitespace should be stripped
		input = "<ui:root><ui:textfoo>  foo  foo  <ui:textfoo></ui:root>";
		expected = "<ui:root><ui:textfoo> foo foo <ui:textfoo></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));

		// ui:mfoo - Whitespace should be stripped
		input = "<ui:root><ui:mfoo>  foo  foo  <ui:mfoo></ui:root>";
		expected = "<ui:root><ui:mfoo> foo foo <ui:mfoo></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));

		// ui:efoo - Whitespace should be stripped
		input = "<ui:root><ui:efoo>  foo  foo  <ui:efoo></ui:root>";
		expected = "<ui:root><ui:efoo> foo foo <ui:efoo></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testUiElementsWithWhitespace() {
		// ui:text - Whitespace should NOT be stripped
		String input = "<ui:root><ui:text>  foo  \n \t \r \" '  foo  <ui:text></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:textarea - Whitespace should NOT be stripped
		input = "<ui:root><ui:textarea>  foo  \n \t \r \" '  foo  <ui:textarea></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:message - Whitespace should NOT be stripped
		input = "<ui:root><ui:message>  foo  \n \t \r \" '  foo  <ui:message></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:error - Whitespace should NOT be stripped
		input = "<ui:root><ui:error>  foo  \n \t \r \" '  foo  <ui:error></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:foo - Whitespace should be stripped
		input = "<ui:root><ui:foo>  foo  \n \t \r \" '  foo  <ui:foo></ui:root>";
		String expected = "<ui:root><ui:foo> foo \" ' foo <ui:foo></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	@Test
	public void testUiElementsWithAttributesAndElements() {
		// ui:textarea - Whitespace should NOT be stripped
		String input = "<ui:root><ui:textarea a1='1  '   a2=\"2  \" >  foo  \n \t \r \" '  foo  <other>other text \n </other>  <ui:textarea></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:text - Whitespace should NOT be stripped
		input = "<ui:root><ui:text type=\"plain\" space=\"preserve\">line1\nline2</ui:text></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:message - Whitespace should NOT be stripped
		input = "<ui:root><ui:message a1='1  '   a2=\"2  \" >  foo  \n \t \r \" '  foo  <other>other text \n </other>  <ui:message></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:error - Whitespace should NOT be stripped
		input = "<ui:root><ui:error a1='1  '   a2=\"2  \" >  foo  \n \t \r \" '  foo  <other>other text \n </other>  <ui:error></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', input, filter(input));

		// ui:foo - Whitespace should be stripped
		input = "<ui:root><ui:foo a1='1  '   a2=\"2  \" >  foo  \n \t \r \" '  foo  <other>other text \n </other>  <ui:foo></ui:root>";
		String expected = "<ui:root><ui:foo a1='1  '   a2=\"2  \" > foo \" ' foo <other>other text </other> <ui:foo></ui:root>";
		Assert.assertEquals(ERROR_MESSAGE + input + '"', expected, filter(input));
	}

	/**
	 * @param input the test input
	 * @return the filtered result
	 */
	private String filter(final String input) {
		StringWriter stringWriter = new StringWriter();

		WhiteSpaceFilterPrintWriter filter = new WhiteSpaceFilterPrintWriter(stringWriter);
		filter.print(input);
		filter.flush();
		filter.close();

		String output = stringWriter.getBuffer().toString();

		LOG.debug("Before filter: \n\"" + input + "\"");
		LOG.debug("After filter: \n\"" + output + "\"");

		return output;
	}
}
