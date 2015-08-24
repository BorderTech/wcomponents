package com.github.openborders.theme; 

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.github.openborders.AbstractWComponentTestCase;
import com.github.openborders.Environment;
import com.github.openborders.UIContext;
import com.github.openborders.WebUtilities;
import com.github.openborders.theme.ThemeUtil;
import com.github.openborders.util.Config;

/** 
 * ThemeUtil_Test - Unit tests for {@link ThemeUtil}. 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class ThemeUtil_Test extends AbstractWComponentTestCase
{
    @After
    public void restoreConfig()
    {
        Config.reset();
    }

    @Test
    public void testGetThemeBuild()
    {
        Assert.assertEquals("Incorrect theme build", ThemeUtil.getThemeBuild(), "TEST_BUILD_NUMBER");
    }
    
    @Test
    public void testGetThemeName()
    {
        Assert.assertEquals("Incorrect theme name", ThemeUtil.getThemeName(), "theme_default");
    }
    
    @Test
    public void testGetThemeXslt()
    {
        String themePath = "/testGetThemeXslt";
        Config.getInstance().setProperty(Environment.THEME_CONTENT_PATH, themePath);
                
        String build = ThemeUtil.getThemeBuild();
        String themeName = ThemeUtil.getThemeName();
        String versionSuffix = "?build=" + WebUtilities.escapeForUrl(build) + "&theme=" + WebUtilities.escapeForUrl(themeName);
        
        UIContext uic = createUIContext();
        Assert.assertEquals("Incorrect theme path", themePath + "/xslt/all.xsl" + versionSuffix, ThemeUtil.getThemeXslt(uic));
        
        uic.setLocale(Locale.ENGLISH);
        Assert.assertEquals("Incorrect theme path", themePath + "/xslt/all_en.xsl" + versionSuffix, ThemeUtil.getThemeXslt(uic));
        
        uic.setLocale(Locale.CANADA_FRENCH);
        Assert.assertEquals("Incorrect theme path", themePath + "/xslt/all_fr-CA.xsl" + versionSuffix, ThemeUtil.getThemeXslt(uic));
    }
}
