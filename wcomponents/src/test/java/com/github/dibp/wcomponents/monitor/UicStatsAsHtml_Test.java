package com.github.dibp.wcomponents.monitor;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;

import com.github.dibp.wcomponents.AbstractWComponentTestCase;
import com.github.dibp.wcomponents.UIContext;
import com.github.dibp.wcomponents.UIContextImpl;
import com.github.dibp.wcomponents.WApplication;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WLabel;

/**
 * Unit tests for {@link UicStatsAsHtml}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class UicStatsAsHtml_Test extends AbstractWComponentTestCase
{
    /** template for first line of expected html in testWriter. */
    private final static String UICSTATS_HTML1 = "<dt>Total root wcomponents found in UIC</dt><dd><<NUM_ROOTS>></dd>";

    /** template for second line of expected html in testWriter. */
    private final static String UICSTATS_HTML2 = "<b>Number of components in tree:</b> <<NUM_COMPONENTS>>";

    /** template for third line of expected html in testWriter. */
    private final static String UICSTATS_HTML3 = "<td><<CLASS_NAME>></td><td>Serializable</td>";

    /**
     * Test writer.
     */
    @Test
    public void testWriter()
    {
        // Set up a mock UI to test
        WApplication app = new WApplication();
        app.setLocked(true);

        UIContext uic = new UIContextImpl();
        uic.setUI(app);
        setActiveContext(uic);

        WButton button = new WButton("PUSH");
        app.add(button);
        WLabel label = new WLabel("HERE");
        app.add(label);

        // Run the UIStats extract
        UicStats stats = new UicStats(uic);
        stats.analyseWC(app);

        StringWriter outStr = new StringWriter();
        PrintWriter writer = new PrintWriter(outStr);

        UicStatsAsHtml.write(writer, stats);

        // expecting 1 root class, 3 components, class names as shown
        String uicStatsHtml1 = UICSTATS_HTML1.replaceAll("<<NUM_ROOTS>>", "1");
        String uicStatsHtml2 = UICSTATS_HTML2.replaceAll("<<NUM_COMPONENTS>>", "4");
        String uicStatsHtml31 = UICSTATS_HTML3.replaceAll("<<CLASS_NAME>>", "com.github.dibp.wcomponents.WApplication");
        String uicStatsHtml32 = UICSTATS_HTML3.replaceAll("<<CLASS_NAME>>", "com.github.dibp.wcomponents.WButton");
        String uicStatsHtml33 = UICSTATS_HTML3.replaceAll("<<CLASS_NAME>>", "com.github.dibp.wcomponents.WLabel");

        String[] expectedResults = { uicStatsHtml1, uicStatsHtml2, uicStatsHtml31, uicStatsHtml32, uicStatsHtml33, };

        String result = outStr.toString();
        for (int i = 0; i < expectedResults.length; i++)
        {
            Assert.assertTrue("result should contain substring " + i + "  ", result.indexOf(expectedResults[i]) != -1);
        }
    }
}
