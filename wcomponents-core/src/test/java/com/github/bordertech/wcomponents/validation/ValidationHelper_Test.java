package com.github.bordertech.wcomponents.validation;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * ValidationHelper_Test - unit tests for {@link ValidationHelper}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ValidationHelper_Test {

	@Test
	public void testExtractDiagnostics() {
		List<Diagnostic> diags = new ArrayList<>();

		Diagnostic error1 = new DiagnosticImpl(Diagnostic.ERROR, null, null);
		Diagnostic error2 = new DiagnosticImpl(Diagnostic.ERROR, null, null);
		Diagnostic warn = new DiagnosticImpl(Diagnostic.WARNING, null, null);
		Diagnostic info = new DiagnosticImpl(Diagnostic.INFO, null, null);

		// Test when empty
		Assert.assertEquals("Should not have any error diagnostics", 0, ValidationHelper.
				extractDiagnostics(diags, Diagnostic.ERROR).size());
		Assert.assertEquals("Should not have any warning diagnostics", 0, ValidationHelper.
				extractDiagnostics(diags, Diagnostic.WARNING).size());
		Assert.assertEquals("Should not have any info diagnostics", 0, ValidationHelper.
				extractDiagnostics(diags, Diagnostic.INFO).size());

		// Test with a single error
		diags.add(error1);
		Assert.assertEquals("Should not have any warning diagnostics", 0, ValidationHelper.
				extractDiagnostics(diags, Diagnostic.WARNING).size());
		Assert.assertEquals("Should not have any info diagnostics", 0, ValidationHelper.
				extractDiagnostics(diags, Diagnostic.INFO).size());

		List<Diagnostic> extractedDiags = ValidationHelper.extractDiagnostics(diags,
				Diagnostic.ERROR);
		Assert.assertEquals("Should have one error diagnostic", 1, extractedDiags.size());
		Assert.assertTrue("Extracted diags should contain error1", extractedDiags.contains(error1));

		// Test with multiple diagnostics
		diags.add(warn);
		diags.add(info);
		diags.add(error2);

		extractedDiags = ValidationHelper.extractDiagnostics(diags, Diagnostic.ERROR);
		Assert.assertEquals("Should have two error diagnostic", 2, extractedDiags.size());
		Assert.assertTrue("Extracted diags should contain error1", extractedDiags.contains(error1));
		Assert.assertTrue("Extracted diags should contain error2", extractedDiags.contains(error2));

		extractedDiags = ValidationHelper.extractDiagnostics(diags, Diagnostic.WARNING);
		Assert.assertEquals("Should have one error diagnostic", 1, extractedDiags.size());
		Assert.assertTrue("Extracted diags should contain warning diag", extractedDiags.contains(
				warn));

		extractedDiags = ValidationHelper.extractDiagnostics(diags, Diagnostic.INFO);
		Assert.assertEquals("Should have one error diagnostic", 1, extractedDiags.size());
		Assert.assertTrue("Extracted diags should contain info diag", extractedDiags.contains(info));
	}
}
