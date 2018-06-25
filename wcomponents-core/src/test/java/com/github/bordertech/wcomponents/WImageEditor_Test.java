package com.github.bordertech.wcomponents;

import org.junit.Test;

import java.awt.Dimension;

/**
 * Unit tests for {@link WImageEditor}
 */
public class WImageEditor_Test extends AbstractWComponentTestCase {

	@Test
	public void testDuplicateComponentModels() {
		WImageEditor imageEditor = new WImageEditor();
		assertNoDuplicateComponentModels(imageEditor, "overlayUrl", "test");
		assertNoDuplicateComponentModels(imageEditor, "useCamera", true);
		assertNoDuplicateComponentModels(imageEditor,"isFace", true);
		assertNoDuplicateComponentModels(imageEditor, "renderInline", true);
		assertNoDuplicateComponentModels(imageEditor, "size", new Dimension(100,213));
	}
}
