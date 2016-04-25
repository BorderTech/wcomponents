package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WHorizontalRule;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WHorizontalRuleRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WHorizontalRuleRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WHorizontalRule horizontalRule = new WHorizontalRule();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(horizontalRule) instanceof WHorizontalRuleRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WHorizontalRule horizontalRule = new WHorizontalRule();
		assertXpathExists("//html:hr", horizontalRule); /* The html namespace is required. */
	}
}
