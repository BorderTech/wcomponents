package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WApplication}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WApplication_Test extends AbstractWComponentTestCase {

	@Test
	public void testUnsavedChangesAccessors() throws Exception {
		WApplication application = new WApplication();
		application.setLocked(true);

		UIContext uic1 = createUIContext();
		setActiveContext(uic1);

		// Flag should be false by default
		Assert.assertFalse("UnsavedChanges should be false by default", application.
				hasUnsavedChanges());

		// Set flag to true
		application.setUnsavedChanges(true);
		Assert.assertTrue("UnsavedChanges should be true", application.hasUnsavedChanges());

		// Set flag to false
		application.setUnsavedChanges(false);
		Assert.assertFalse("UnsavedChanges should be false", application.hasUnsavedChanges());

		// Test a second context
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);
		application.setUnsavedChanges(true);
		Assert.assertTrue("UnsavedChanges should be true for second context", application.
				hasUnsavedChanges());

		setActiveContext(uic1);
		Assert.assertFalse("UnsavedChanges should be false for first context", application.
				hasUnsavedChanges());
	}

	@Test
	public void testTitleAccessors() {
		assertAccessorsCorrect(new WApplication(), "title", null, "A", "B");
	}

	@Test
	public void testAppendIdAccessors() {
		assertAccessorsCorrect(new WApplication(), "appendID", false, true, false);
	}

	@Test
	public void testIdNameAccessors() {
		assertAccessorsCorrect(new WApplication(), "idName", WApplication.DEFAULT_APPLICATION_ID,
				"XX", "YY");
	}

	@Test
	public void testNamingContextDefault() {
		WApplication appl = new WApplication();
		Assert.assertEquals("Incorrect default naming context", "", appl.getNamingContextId());
		Assert.assertEquals("Incorrect default id", WApplication.DEFAULT_APPLICATION_ID, appl.
				getId());
		Assert.assertEquals("Incorrect default id name", WApplication.DEFAULT_APPLICATION_ID, appl.
				getIdName());
		Assert.assertFalse("Append should default to false", appl.isAppendID());

		// Put in another context
		WNamingContext context = new WNamingContext("TEST");
		context.add(appl);
		// Append false should be ignored
		Assert.assertEquals("Incorrect default naming context", "TEST-A", appl.getNamingContextId());
	}

	@Test
	public void testNamingContextDefaultWithAppend() {
		WApplication appl = new WApplication();
		appl.setAppendID(true);
		Assert.assertEquals("Incorrect default naming context with append",
				WApplication.DEFAULT_APPLICATION_ID, appl.getNamingContextId());
		Assert.assertEquals("Incorrect default id with append", WApplication.DEFAULT_APPLICATION_ID,
				appl.getId());
		Assert.assertEquals("Incorrect default id name with append",
				WApplication.DEFAULT_APPLICATION_ID, appl.getIdName());
		Assert.assertTrue("Append should be true", appl.isAppendID());
	}

	@Test
	public void testAddJsResource() {
		WApplication application = new WApplication();
		application.setLocked(true);

		UIContext uic1 = createUIContext();
		setActiveContext(uic1);

		// Resources should be empty by default.
		Assert.assertTrue("JavaScript resources should be empty by default", application.getJsResources().isEmpty());

		// Add JS URL
		WApplication.ApplicationResource resource = new WApplication.ApplicationResource("URL");
		application.addJsResource(resource);
		Assert.assertEquals("JavaScript Resource should have been added", 1, application.getJsResources().size());
		Assert.assertTrue("JavaScript Resource should be in the list", application.getJsResources().contains(resource));

		// Test a second context
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);

		// Resource should be empty by default.
		Assert.assertTrue("JavaScript resources should be empty by default on second context", application.getJsResources().isEmpty());

		// Add JS URL2
		WApplication.ApplicationResource resource2 = new WApplication.ApplicationResource("URL2");
		application.addJsResource(resource2);
		Assert.assertEquals("JavaScript Resource should have been added to second context", 1, application.getJsResources().size());
		Assert.assertTrue("JavaScript Resource should be in the list on the second context", application.getJsResources().contains(resource2));

		// Check first context not changed
		setActiveContext(uic1);
		Assert.assertEquals("JavaScript Resource should still only have one item on first context", 1, application.getJsResources().size());
		Assert.assertTrue("JavaScript Resource should still be in the list on the first context", application.getJsResources().contains(resource));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddJsUrlException() {
		new WApplication().addJsUrl(null);
	}

	@Test
	public void testAddJsUrl() {
		WApplication application = new WApplication();
		WApplication.ApplicationResource res = application.addJsUrl("URL");
		Assert.assertEquals("JavaScript URL should be on the resource detail", "URL", res.getUrl());
		Assert.assertEquals("JavaScript URL resource should have been added", 1, application.getJsResources().size());
		Assert.assertTrue("JavaScript URL resource should be in set", application.getJsResources().contains(res));

		// Add Same URL should not add an extra resource
		application.addJsUrl("URL");
		Assert.assertEquals("A JavaScript URL that is already added should not be added", 1, application.getJsResources().size());

		// Add Different URL
		application.addJsUrl("URL2");
		Assert.assertEquals("A different JavaScript URL should be added", 2, application.getJsResources().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddJsFileException() {
		new WApplication().addJsFile(null);
	}

	@Test
	public void testAddJsFileName() {
		WApplication application = new WApplication();
		WApplication.ApplicationResource res = application.addJsFile("FILE");
		Assert.assertEquals("JavaScript FILE should be on the resource detail", "FILE", res.getResource().getResourceName());
		Assert.assertEquals("JavaScript FILE resource should have been added", 1, application.getJsResources().size());
		Assert.assertTrue("JavaScript FILE resource should be in set", application.getJsResources().contains(res));

		// Add Same FILE should not add an extra resource
		application.addJsFile("FILE");
		Assert.assertEquals("A JavaScript FILE that is already added should not be added", 1, application.getJsResources().size());

		// Add Diffenrt FILE
		application.addJsFile("FILE2");
		Assert.assertEquals("A different JavaScript FILE should be added", 2, application.getJsResources().size());
	}

	@Test
	public void testRemoveJsResource() {
		WApplication application = new WApplication();
		application.setLocked(true);

		UIContext uic1 = createUIContext();
		setActiveContext(uic1);

		// Add resources
		application.addJsFile("FileName1a");
		application.addJsFile("FileName1b");
		Assert.assertEquals("JavaScript resources should have been added", 2, application.getJsResources().size());

		// Second context
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);

		// Add resources
		WApplication.ApplicationResource res2a = application.addJsFile("FileName2a");
		application.addJsFile("FileName2b");
		Assert.assertEquals("JavaScript resources should have been added on second context", 2, application.getJsResources().size());

		// Remove resource
		application.removeJsResource(res2a);
		Assert.assertEquals("Should only have one javascript resource on second context", 1, application.getJsResources().size());
		Assert.assertFalse("JavaScript resource should have been removed on second conext", application.getJsResources().contains(res2a));

		// Check first context not changed
		setActiveContext(uic1);
		Assert.assertEquals("JavaScript resource should not have changed on first context", 2, application.getJsResources().size());
	}

	@Test
	public void testRemoveAllJsResources() {
		WApplication application = new WApplication();
		application.setLocked(true);

		UIContext uic1 = createUIContext();
		setActiveContext(uic1);

		// Add resources
		application.addJsFile("FileName1a");
		application.addJsFile("FileName1b");
		Assert.assertEquals("JavaScript resources should have been added", 2, application.getJsResources().size());

		// Second context
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);

		// Add resources
		application.addJsFile("FileName2a");
		application.addJsFile("FileName2b");
		Assert.assertEquals("JavaScript resources should have been added on second context", 2, application.getJsResources().size());

		// Remove all resources
		application.removeAllJsResources();
		Assert.assertTrue("JavaScript resources should be empty on second conext", application.getJsResources().isEmpty());

		// Check first context not changed
		setActiveContext(uic1);
		Assert.assertEquals("JavaScript resources should not have changed on first context", 2, application.getJsResources().size());
	}

	@Test
	public void testAddCssResource() {
		WApplication application = new WApplication();
		application.setLocked(true);

		UIContext uic1 = createUIContext();
		setActiveContext(uic1);

		// Resources should be empty by default.
		Assert.assertTrue("CSS resources should be empty by default", application.getCssResources().isEmpty());

		// Add CSS URL
		WApplication.ApplicationResource resource = new WApplication.ApplicationResource("URL");
		application.addCssResource(resource);
		Assert.assertEquals("CSS Resource should have been added", 1, application.getCssResources().size());
		Assert.assertTrue("CSS Resource should be in the list", application.getCssResources().contains(resource));

		// Test a second context
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);

		// Resource should be empty by default.
		Assert.assertTrue("CSS resources should be empty by default on second context", application.getCssResources().isEmpty());

		// Add CSS URL2
		WApplication.ApplicationResource resource2 = new WApplication.ApplicationResource("URL2");
		application.addCssResource(resource2);
		Assert.assertEquals("CSS Resource should have been added to second context", 1, application.getCssResources().size());
		Assert.assertTrue("CSS Resource should be in the list on the second context", application.getCssResources().contains(resource2));

		// Check first context not changed
		setActiveContext(uic1);
		Assert.assertEquals("CSS Resource should still only have one item on first context", 1, application.getCssResources().size());
		Assert.assertTrue("CSS Resource should still be in the list on the first context", application.getCssResources().contains(resource));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddCssUrlException() {
		new WApplication().addCssUrl(null);
	}

	@Test
	public void testAddCssUrl() {
		WApplication application = new WApplication();
		WApplication.ApplicationResource res = application.addCssUrl("URL");
		Assert.assertEquals("CSS URL should be on the resource detail", "URL", res.getUrl());
		Assert.assertEquals("CSS URL resource should have been added", 1, application.getCssResources().size());
		Assert.assertTrue("CSS URL resource should be in set", application.getCssResources().contains(res));

		// Add Same URL should not add an extra resource
		application.addCssUrl("URL");
		Assert.assertEquals("A CSS URL that is already added should not be added", 1, application.getCssResources().size());

		// Add Different URL
		application.addCssUrl("URL2");
		Assert.assertEquals("A different CSS URL should be added", 2, application.getCssResources().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddCssFileException() {
		new WApplication().addCssFile(null);
	}

	@Test
	public void testAddCssFileName() {
		WApplication application = new WApplication();
		WApplication.ApplicationResource res = application.addCssFile("FILE");
		Assert.assertEquals("CSS FILE should be on the resource detail", "FILE", res.getResource().getResourceName());
		Assert.assertEquals("CSS FILE resource should have been added", 1, application.getCssResources().size());
		Assert.assertTrue("CSS FILE resource should be in set", application.getCssResources().contains(res));

		// Add Same FILE should not add an extra resource
		application.addCssFile("FILE");
		Assert.assertEquals("A CSS FILE that is already added should not be added", 1, application.getCssResources().size());

		// Add Diffenrt FILE
		application.addCssFile("FILE2");
		Assert.assertEquals("A different CSS FILE should be added", 2, application.getCssResources().size());
	}

	@Test
	public void testRemoveCssResource() {
		WApplication application = new WApplication();
		application.setLocked(true);

		UIContext uic1 = createUIContext();
		setActiveContext(uic1);

		// Add resources
		application.addCssFile("FileName1a");
		application.addCssFile("FileName1b");
		Assert.assertEquals("CSS resources should have been added", 2, application.getCssResources().size());

		// Second context
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);

		// Add resources
		WApplication.ApplicationResource res2a = application.addCssFile("FileName2a");
		application.addCssFile("FileName2b");
		Assert.assertEquals("CSS resources should have been added on second context", 2, application.getCssResources().size());

		// Remove resource
		application.removeCssResource(res2a);
		Assert.assertEquals("Should only have one css resource on second context", 1, application.getCssResources().size());
		Assert.assertFalse("CSS resource should have been removed on second conext", application.getCssResources().contains(res2a));

		// Check first context not changed
		setActiveContext(uic1);
		Assert.assertEquals("CSS resource should not have changed on first context", 2, application.getCssResources().size());
	}

	@Test
	public void testRemoveAllCssResources() {
		WApplication application = new WApplication();
		application.setLocked(true);

		UIContext uic1 = createUIContext();
		setActiveContext(uic1);

		// Add resources
		application.addCssFile("FileName1a");
		application.addCssFile("FileName1b");
		Assert.assertEquals("CSS resources should have been added", 2, application.getCssResources().size());

		// Second context
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);

		// Add resources
		application.addCssFile("FileName2a");
		application.addCssFile("FileName2b");
		Assert.assertEquals("CSS resources should have been added on second context", 2, application.getCssResources().size());

		// Remove all resources
		application.removeAllCssResources();
		Assert.assertTrue("CSS resources should be empty on second conext", application.getCssResources().isEmpty());

		// Check first context not changed
		setActiveContext(uic1);
		Assert.assertEquals("CSS resources should not have changed on first context", 2, application.getCssResources().size());
	}
}
