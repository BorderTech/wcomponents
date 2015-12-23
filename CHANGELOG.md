## API Changes
* Updated WAbbrText, WAbbrTextRenderer and associated tests (#157)
    * Removed WAbbrText.AbbrTextModel This custom component model is completely superfluous. The refactoring of WAbbrText brings the component into line with other WComponents and standardises API calls. **NOTE** If you have extended this model (well stop it you will go blind) you will now have to implement your own component model.
    * Deprecated WAbbrText.getAbbrText in favour of WAbbrText.getToolTip (inherited from AbstractWComponent) and WAbbrText.setAbbrText in favour of WAbbrText.setToolTIp (inherited from AbstractWComponent).
    * Updated schema and XSLT for ui:abbr to change attribute `description` to `toolTip` in line with other components.
* Method scope issue in WCardManager. Removed "removeAll(flag)" and "remove(child, flag)". (#59)
* "WebUtilities.updateBeanValue(component)" changed to ignore invisible components by default. "WebUtilities.updateBeanValue(component, flag)" added to provide this option. (#46)
* Removed deprecated com.github.bordertech.wcomponents.test.util.TreeUtil. Use com.github.bordertech.wcomponents.util.TreeUtil instead.
* Removed deprecated com.github.bordertech.wcomponents.test.util.WComponentTreeVisitor. Use com.github.bordertech.wcomponents.util.WComponentTreeVisitor instead.

## Enhancements
* WTabSet implements SubordinateTarget. (#159)
* WTab implements SubordinateTarget. (#158)
* WMenu API allows for getItems(boolean recursive). (#262)

## Bug fixes
* Fixed loading WSubMenus via AJAX. Items can now be dynamically added when the WSubMenu is opened. (#250)
* AbstractWComponentTestCase now works with variable arguments on setter methods (#188)
