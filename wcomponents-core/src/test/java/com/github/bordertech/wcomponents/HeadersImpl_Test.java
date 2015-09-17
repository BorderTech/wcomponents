package com.github.bordertech.wcomponents;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link HeadersImpl}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class HeadersImpl_Test {

	@Test
	public void testAddHeadLine() {
		String line = "HeadersImpl_Test.testAddHeadLine.line";

		HeadersImpl headers = new HeadersImpl();

		// Add a headline
		headers.addHeadLine(line);

		List<String> untypedHeaders = headers.getHeadLines();
		Assert.assertEquals("Incorrect number of untyped headers", 1, untypedHeaders.size());
		Assert.assertEquals("Incorrect headers", line, untypedHeaders.get(0));
		Assert.assertNull("Should not have any JS headers", headers.getHeadLines(
				Headers.JAVASCRIPT_HEADLINE));
		Assert.assertNull("Should not have any CSS headers", headers.getHeadLines(
				Headers.CSS_HEADLINE));

		// Add the same one again
		headers.addHeadLine(line);

		untypedHeaders = headers.getHeadLines();
		Assert.assertEquals("Incorrect number of untyped headers", 2, untypedHeaders.size());
		Assert.assertEquals("Incorrect headers", line, untypedHeaders.get(0));
		Assert.assertEquals("Incorrect headers", line, untypedHeaders.get(1));
		Assert.assertNull("Should not have any JS headers", headers.getHeadLines(
				Headers.JAVASCRIPT_HEADLINE));
		Assert.assertNull("Should not have any CSS headers", headers.getHeadLines(
				Headers.CSS_HEADLINE));
	}

	@Test
	public void testAddUniqueHeadLine() {
		String line1 = "HeadersImpl_Test.testAddUniqueHeadLine.line1";
		String line2 = "HeadersImpl_Test.testAddUniqueHeadLine.line2";

		HeadersImpl headers = new HeadersImpl();

		// Add a headline
		headers.addUniqueHeadLine(line1);

		List<String> untypedHeaders = headers.getHeadLines(Headers.UNTYPED_HEADLINE);
		Assert.assertEquals("Incorrect number of untyped headers", 1, untypedHeaders.size());
		Assert.assertEquals("Incorrect headers", line1, untypedHeaders.get(0));
		Assert.assertNull("Should not have any JS headers", headers.getHeadLines(
				Headers.JAVASCRIPT_HEADLINE));
		Assert.assertNull("Should not have any CSS headers", headers.getHeadLines(
				Headers.CSS_HEADLINE));

		// Add the same one again - should not change
		headers.addUniqueHeadLine(line1);
		Assert.assertEquals("Untyped headers should not have changed", untypedHeaders, headers.
				getHeadLines(Headers.UNTYPED_HEADLINE));
		Assert.assertNull("Should not have any JS headers", headers.getHeadLines(
				Headers.JAVASCRIPT_HEADLINE));
		Assert.assertNull("Should not have any CSS headers", headers.getHeadLines(
				Headers.CSS_HEADLINE));

		// Add a different one - should be added
		headers.addUniqueHeadLine(line2);
		untypedHeaders = headers.getHeadLines();
		Assert.assertEquals("Incorrect number of untyped headers", 2, untypedHeaders.size());
		Assert.assertEquals("Incorrect headers", line1, untypedHeaders.get(0));
		Assert.assertEquals("Incorrect headers", line2, untypedHeaders.get(1));
		Assert.assertNull("Should not have any JS headers", headers.getHeadLines(
				Headers.JAVASCRIPT_HEADLINE));
		Assert.assertNull("Should not have any CSS headers", headers.getHeadLines(
				Headers.CSS_HEADLINE));
	}

	@Test
	public void testAddTypedHeadLine() {
		String untypedLine = "HeadersImpl_Test.testAddTypedHeadLine.untypedLine";
		String cssLine = "HeadersImpl_Test.testAddTypedHeadLine.cssLine";

		HeadersImpl headers = new HeadersImpl();

		// Add an untyped headline
		headers.addHeadLine(Headers.UNTYPED_HEADLINE, untypedLine);

		List<String> untypedHeaders = headers.getHeadLines(Headers.UNTYPED_HEADLINE);
		Assert.assertEquals("Incorrect number of untyped headers", 1, untypedHeaders.size());
		Assert.assertEquals("Incorrect headers", untypedLine, untypedHeaders.get(0));
		Assert.assertNull("Should not have any JS headers", headers.getHeadLines(
				Headers.JAVASCRIPT_HEADLINE));
		Assert.assertNull("Should not have any CSS headers", headers.getHeadLines(
				Headers.CSS_HEADLINE));

		// Add a css headline
		headers.addHeadLine(Headers.CSS_HEADLINE, cssLine);

		untypedHeaders = headers.getHeadLines(Headers.UNTYPED_HEADLINE);
		List cssHeaders = headers.getHeadLines(Headers.CSS_HEADLINE);
		Assert.assertEquals("Incorrect number of untyped headers", 1, untypedHeaders.size());
		Assert.assertEquals("Incorrect headers", untypedLine, untypedHeaders.get(0));
		Assert.assertEquals("Incorrect number of css headers", 1, cssHeaders.size());
		Assert.assertEquals("Incorrect headers", cssLine, cssHeaders.get(0));
		Assert.assertNull("Should not have any JS headers", headers.getHeadLines(
				Headers.JAVASCRIPT_HEADLINE));
	}

	@Test
	public void testSetContentType() {
		String contentType = "application/x-headersimpl-test-contenttype";

		HeadersImpl headers = new HeadersImpl();
		Assert.assertEquals("Default content type should be text/html", "text/html", headers.
				getContentType());

		headers.setContentType(contentType);
		Assert.assertEquals("Incorrect contentType", contentType, headers.getContentType());
	}

	@Test
	public void testReset() {
		HeadersImpl headers = new HeadersImpl();
		headers.setContentType("text/plain");
		headers.addHeadLine("dummy");

		headers.reset();
		Assert.assertEquals("Content type should be text/html after reset", "text/html", headers.
				getContentType());
		Assert.assertNull("Should not have any headers after reset", headers.getHeadLines());
	}
}
