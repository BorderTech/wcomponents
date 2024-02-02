package com.github.bordertech.wcomponents;

import java.awt.Dimension;
import org.junit.Test;

/**
 * Unit tests for {@link WImageEditor}
 */
public class WImageEditor_Test extends AbstractWComponentTestCase {

	@Test
	public void testDuplicateComponentModels() {
		WImageEditor imageEditor = new WImageEditor();
		assertNoDuplicateComponentModels(imageEditor, "overlayUrl", "test");
		assertNoDuplicateComponentModels(imageEditor, "useCamera", true);
		assertNoDuplicateComponentModels(imageEditor, "renderInline", true);
		assertNoDuplicateComponentModels(imageEditor, "size", new Dimension(100, 213));
	}
}
