# Change log

## API Changes
These are API changes and enhancements which _may_ have some impact on current users.

* `WDataTable.PaginationMode.SERVER` has been modified to implement `WDataTable.PaginationMode.DYNAMIC` to overcome an a11y problem inherent in `WDataTable.PagainationMode.SERVER`. Whilst `WDataTable` is deprecated it is being retained (for the foreseeable future) for backwards compatibility and therefore must meet a11y requirements. `WDataTable.PaginationMode.SERVER` has been individually deprecated and **will be removed** in a near future release.

## Enhancements
These are backwards-compatible API changes which are transparent for all current users. For full details see [GitHub issues](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Aenhancement).

* Improved AJAX updates so "busy" regions maintain their dimensions and do not collapse to 0px.
* `WTable` API allows for setting the location of the pagination controls using `setPaginationLocation(WTable.PaginationLocation location)` (#297).
* `WMessageBox` API allows for setting message box title string using `setTitleText(String title)` (#280).

## Major Bug fixes
For all bug fixes see [GitHub Issues](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)

* Fixed WTabSet ACCORDION interactions (#277).

# Release 1.0.2
## API Changes
* Updated `WAbbrText`, `WAbbrTextRenderer` and associated tests (#157)
    * Removed `WAbbrText.AbbrTextModel` This custom component model is completely superfluous. The refactoring of `WAbbrText` brings the component into line with other WComponents and standardises API calls. **NOTE** If you have extended this model (well stop it you will go blind) you will now have to implement your own component model.
    * Deprecated `WAbbrText.getAbbrText` in favour of `WAbbrText.getToolTip` (inherited from `AbstractWComponent`) and `WAbbrText.setAbbrText` in favour of `WAbbrText.setToolTIp` (inherited from `AbstractWComponent`).
    * Updated schema and XSLT for ui:abbr to change attribute `description` to `toolTip` in line with other components.
* Method scope issue in `WCardManager`. Removed `removeAll(flag)` and `remove(child, flag)`. (#59)
* `WebUtilities.updateBeanValue(component)` changed to ignore invisible components by default. `WebUtilities.updateBeanValue(component, flag)` added to provide this option. (#46)
* Removed deprecated com.github.bordertech.wcomponents.test.util.TreeUtil. Use com.github.bordertech.wcomponents.util.TreeUtil instead.
* Removed deprecated `com.github.bordertech.wcomponents.test.util.WComponentTreeVisitor`. Use `com.github.bordertech.wcomponents.util.WComponentTreeVisitor` instead.

## Enhancements
* `WMenu` API allows for `getItems(boolean recursive)` (#262).
* `WTabSet` implements `SubordinateTarget` (#159).
* `WTab` implements `SubordinateTarget` (#158).

## Major Bug fixes
* Fixed loading `WSubMenu` via AJAX. Items can now be dynamically added when the `WSubMenu` is opened. (#250)
* `AbstractWComponentTestCase` now works with variable arguments on setter methods (#188)
