# Change log

## API Changes
These are API changes and enhancements which _may_ have some impact on current users.

* `WFilterControl` has been removed. This WComponent was originally designed as a low-quality row filter for `WDataTable`. It has been deprecated for some time. WDataTable is deprecated and slated for _imminent_ removal (though not in the next major release) (#306).

## Enhancements
For full details see [GitHub issues](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Aenhancement).

* Added a new property to WSuggestions: WSuggestions.Autocomplete. This is an enum which currently supports two settings `LIST` and `BOTH` but has the potential to support all settings of the HTML attribute [aria-autocomplete](https://www.w3.org/TR/wai-aria/states_and_properties#aria-autocomplete) if required. This has the usual accessors get/setAutocomplete. The default is WSuggestions.Autocomplete.BOTH which provides the same functionality as in previous versions of WSuggestions. When WSuggestions.Autocomplete.LIST is used then the client implementation will attempt to force selection of an option from the suggestions list rather than allowing free-text input (#379).
* Created a new WTemplate component that supports velocity, handlebars and plain text. Projects can also implement their own templating engines (#94).

## Major Bug fixes
For all bug fixes see [GitHub Issues](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)

## Other
* The schema has been updated to make all UI element names lowercase. Why is this not an API change? because the schema is not part of the API - it is an internal contract.

# Release 1.0.3

## API Changes
These are API changes and enhancements which _may_ have some impact on current users.

* `WDataTable.PaginationMode.SERVER` has been modified to implement `WDataTable.PaginationMode.DYNAMIC` to overcome an a11y problem inherent in `WDataTable.PagainationMode.SERVER`. Whilst `WDataTable` is deprecated it is being retained (for the foreseeable future) for backwards compatibility and therefore must meet a11y requirements. `WDataTable.PaginationMode.SERVER` has been individually deprecated and **will be removed** in a near future release.
* `WSubMenu` child count has changed. This was necessary to allow correct AJAX loading of sub-menus (#250). Applications should use `getMenuItems` instead of `getChildAt`.

### JavaScript API changes
* Client-side support of `WFilterControl` has been removed. This applied _only_ to (the deprecated) `WDataTable` but caused significant overhead during XSLT processing of all tables. The old code is available if a custom theme requires it.

## Enhancements
These are backwards-compatible API changes which are transparent for all current users. For full details see [GitHub issues](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Aenhancement).

* `WTable` API allows for setting the location of the pagination controls using `setPaginationLocation(WTable.PaginationLocation location)` (#297).
* `WTable` API allows for sub-row group selection (select all/select none) when a table has multiple row selection **and** row expansion enabled. The optional extra functionality allows for a select all/none control for every row which has one or more selectable sub-rows (#257).
* `WMessageBox` API allows for setting message box title string using `setTitleText(String title)` (#280).
* `WApplication` API allows for custom `js` and `css` resources to be easily loaded (#172).
* `AbstractWComponent` API allows for custom HTML class attribute values to be added to any WComponent for improved application-level customisation (#172).
* `WColumn` and `ColumnLayout` may now be created without specifying a width. The column widths _should_ then be specified in [application level CSS](https://github.com/BorderTech/wcomponents/wiki/Adding-custom-CSS). This is primarily aimed at applications which require responsive design (#172, #180).
* Added improved guards against clickjacking (#240).

## Major Bug fixes
For all bug fixes see [GitHub Issues](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)

* Fixed loading `WSubMenu` via AJAX. Items can now be dynamically added when the `WSubMenu` is opened (#250). **NOTE** Any application using `WSubMenu.getChildAt` **must** now use `WSubMenu.getMenuItems` as the child count _will_ be different.
* Fixed WTabSet ACCORDION interactions (#277).
* Fixed `WShuffler` to work correctly when used as an `AjaxTrigger` (#323).
* Fixed AJAX-enabled, drag & drop, multi-file upload in Firefox (#245).

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
* `AbstractWComponentTestCase` now works with variable arguments on setter methods (#188)
