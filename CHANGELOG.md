## API Changes
* Updated WAbbrText, WAbbrTextRenderer and associated tests (#157)
    * Removed WAbbrText.AbbrTextModel This custom component model is completely superfluous. The refactoring of WAbbrText brings the component into line with other WComponents and standardises API calls. **NOTE** If you have extended this model (well stop it you will go blind) you will now have to implement your own component model.
    * Deprecated WAbbrText.getAbbrText in favour of WAbbrText.getToolTip (inherited from AbstractWComponent) and WAbbrText.setAbbrText in favour of WAbbrText.setToolTIp (inherited from AbstractWComponent).
    * Updated schema and XSLT for ui:abbr to change attribute `description` to `toolTip` in line with other components.
* Method scope issue in WCardManager. Removed "removeAll(flag)" and "remove(child, flag)". (#59)
* "WebUtilities.updateBeanValue(component)" changed to ignore invisible components by default. "WebUtilities.updateBeanValue(component, flag)" added to provide this option. (#46)

## Enhancements
* WTabSet implements SubordinateTarget. (#159)
* WTab implements SubordinateTarget. (#158)

## Bug fixes
