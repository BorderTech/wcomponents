## API Changes
* Updated WAbbrText, WAbbrTextRenderer and associated tests (#157)
    * Removed WAbbrText.AbbrTextModel This custom component model is completely superfluous. The refactoring of WAbbrText brings the component into line with other WComponents and standardises API calls. **NOTE** If you have extended this model (well stop it you will go blind) you will now have to implement your own component model.
    * Deprecated WAbbrText.getAbbrText in favour of WAbbrText.getToolTip (inherited from AbstractWComponent) and WAbbrText.setAbbrText in favour of WAbbrText.setToolTIp (inherited from AbstractWComponent).
    * Updated schema and XSLT for ui:abbr to change attribute `description` to `toolTip` in line with other components.
* Method scope issue in WCardManager. Removed "removeAll(flag)" and "remove(child, flag)". (#59)
* "WebUtilities.updateBeanValue(component)" changed to ignore invisible components by default. "WebUtilities.updateBeanValue(component, flag)" added to provide this option. (#46)
* Removed deprecated com.github.bordertech.wcomponents.test.util.TreeUtil. Use com.github.bordertech.wcomponents.util.TreeUtil instead.
* Removed deprecated com.github.bordertech.wcomponents.test.util.WComponentTreeVisitor. Use com.github.bordertech.wcomponents.util.WComponentTreeVisitor instead.
* ComponentModel property for width changed from int to String in WColumn, ColumnLayout, WTableColumn and WDialog (#178).
   * getWidth changed to return a string;
   * setWidth(String) added to API
   * setWidth(int) deprecated and updated to set the width as a String with the originally intended units added or to empty string if width < 1 (WDialog, WTableColumn only);
   * WDialogRenderer & WTableColumnRenderer updated to test for empty string as optional attribute condition;
   * Updated schemas.
* WDialog ComponentModel property for height changed from int to String (natural extension of #178):
   * getHeight modified to return String;
   * setHeight(String) added;
   * setHeight(int) modified to convert into to String with "px" suffix as per original intent;
   * WDialogRenderer updated to use String optional attribute test for height;
   * updated ui:dialog schema.

* _JavaScript_ wc/ui/positionable.setBySize removed option to pass width and height in as part of the configuration object as this depended on these being unitless pixel measures.

## Enhancements
* WTabSet implements SubordinateTarget. (#159)
* WTab implements SubordinateTarget. (#158)
* Added property String htmlClass to interface WComponent and accessors to Abstract WComponent.
  This will allow any UI component to include optional string class names in the HTML class attribute of the root output element of that component.

## Bug fixes
