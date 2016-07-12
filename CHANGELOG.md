# Change log
## API Changes

## Bug Fixes
* Updated DataListInterceptor and ServletUtil to allow DataLists to be sent as HTML rather than XML (#747).

## Enhancements

# Release 1.2.0
## API Changes
* FlowLayout API modified as part of fixing #636. This was required to remove ambiguity from the API.
  * Added constructors to FlowLayout:
    * `FlowLayout(Alignment, int)` and
    * `FlowLayout(Alignment, int, ContentAlignment)`.
  * Deprecated constructor `FlowLayout(Alignment, int, int)` in favour of `FlowLayout(Alignment, int)`.
  * Deprecated constructor `FlowLayout(Alignment, int, int, ContentAlignment)` in favour of
    `FlowLayout(Alignment, int, ContentAlignment)`.
  * Deprecated `getHGap()` and `getVGap()` in favour of `getGap()`.
* ListLayout API modified as part of fixing #655. This was required to remove ambiguity from the API.
  * Added convenience constructors to ListLayout:
    * `ListLayout()`;
    * `ListLayout(final Type type)`;
    * `ListLayout(final boolean ordered)`; and
    * `ListLayout(final Type type, final Alignment alignment)`.
  * Added constructor `ListLayout(final Type type, final Alignment alignment, final Separator separator,
    final boolean ordered, final int gap)`
  * Deprecated constructor `ListLayout(final Type type, final Alignment alignment, final Separator separator,
    final boolean ordered, final int hgap, final int vgap)` in favour of `ListLayout(final Type type,
    final Alignment alignment, final Separator separator, final boolean ordered, final int gap)`.
  * Deprecated `getHGap()` and `getVGap()` in favour of `getGap()`.
* WList API modified as part of fixing #655. This was required to remove ambiguity from the API.
  * Added constructor `WList(final Type type, final int gap)` and deprecated constructor `WList(final Type type,
    final int hgap, final int vgap)` in favour of the new constructor.
  * Deprecated `getHGap()` and `getVGap()` in favour of `getGap()`.
* WTab updated `setMode(TabMode)` to set TabMode.DYNAMIC if the mode being set is TabMode.SERVER. This is required for
  fixing a11y problems in TabMode.SERVER as per #692.
* WCollapsible updated setMode(CollapsibleMode) to set CollapsibleMode.DYNAMIC if the mode being set is
  CollapsibleMode.SERVER. This is required for fixing a11y problems in CollapsibleMode.SERVER as per #694.
* WDialog.isResizeable will always return true as part of fixing #606
* WAjaxControl: deprecated set/getLoadCount. In future use set/isLoadOnce. Required for #495.
* WDataTable PaginationMode.SERVER remapped to PaginationMode.DYNAMIC (has been enforced in client for over 2 years);
  removed submitOnRowSelect (never supported in client); SortMode.SERVER mapped to SortMode.DYNAMIC;
  ExpansionMode.SERVER mapped to ExpansionMode.DYNAMIC. These were all required as part of #701.
* Removed support for unencoded options in WCheckBoxSelect, WDropdown, WMultiDropdown, WMultiSelectPair, WMultiSelect,
  WRadioButtonSelect and WSingleSelect. In all cases except WCheckBoxSelect using unencoded text could result in
  catastrophic failure as these must not contain HTML. Method `getDescEncode()` has been deprecated, made final and
  will always return true; method `setDescEncode(boolean)` has been deprecated, made final and is now a no-op.
* Removed "round trip" mode WCollapsibleToggle. Constructor `WCollapsibleToggle(boolean)` has been deprecated in favour
  of `WCollapsibleToggle)`. Constructor `WCollapsibleToggle(boolean, CollapsibleGroup )` has been deprecated in favour
  of `WCollapsibleToggle(CollapsibleGroup)` Method `isClientSide()` deprecated as it will always return true #598.
* Deprecated WAjaxPollingRegion #500.

## Bug Fixes
* Fixed an accessibility error caused by unexpected side effects of WCollapsibleToggle #598.
* Fixed accessibility issue in WCollapsible #694.
* Changed output of WMenu Type TREE from role tree to role menu #619.
* Fixed bug which could result in messages causing XML validation failure #707.
* Fixed issue which caused WTextarea to not include changes in AJAX posts when in rich-text mode #700.
* Fixed accessibility problems in WDataTable #701.
* WAjaxControl addressed API errors #495
* Added HTML sanitizer and character entity unescaoer #620.
* Fixed accessibility problems in WDialog #606.
* Fixed accessibility problems in WCollapsible #692.
* Fixed accessibility problems in WTabSet #692.
* Fixed a bug which prevented themes overriding font sizes and gaps in any unit other than rems #685.
* Ensure missing label warning is in viewport #681.
* Various table bugs fixed #666, #667, #670.
* Removed flash on page load in slower browsers #664.
* Ensure WDialog is not opened larger than viewport #663
* FlowLayoutRenderer output the hgap or vgap relevant to the Alignment #636.
* ListLayoutRenderer output the hgap or vgap relevant to the Alignment #655.
* WSubMenu MenuMode.SERVER is internally mapped to MenuMode.DYNAMIC #687.
* WButtonRenderer and WLinkRenderer updated to look for WImage description as fall-back if no text equivalent is set
  #650.
* Fixed GridLayout cell alignment on small screens #652.

## Enhancements
* Added WTabSet as a potential target of WCollapsibleToggle. This is only effective if the WTabSet is of TabSetType
  ACCORDION #627.
* Changed the client handling of WTable Actions with constraints to make the action button disabled if "error" level
  constraints aren't met rather than outputting an error alert #618.
* Reversed the date input polyfill used by WDateField. Previously we created all of the UI artefacts needed to support
  the polyfill and removed them if the browser supported native date inputs. This has been reversed to output a wrapper
  and input in XSLT and add the other artefacts if native date inputs are not supported. This significantly improves
  the use of WDateField on mobile devices and removes a flicker caused by removing elements. In some cases the calendar
  launch button was seen to persist in the view after being removed from the component. THis change also fixes that
  artefaction issue. #698.
* Made layout responsive design opt-in to prevent issues with unwanted collapsing columns and grids #682. To make a
  WRow (and its WColumns), or WPanel with either ColumnLayout or GridLayout invoke the default responsive design use
  `setHtmlClass(HtmlClassUtil.htmlClassName.RESPOND)`.
* Added utility class `com.github.bordertech.wcomponents.util.HtmlClassUtil` which provides an enum of HTML class
  attribute values which may be used in `setHtmlClass`. Part of #682.
* Added new boolean property `sanitizeOnOutput` to WComponents which can output unencoded HTML (WText, WTextArea,
  WLabel). This defaults to false. If set true then the content of the component will be run through the HTML
  sanitizer using the lax policy. This is a necessary extension of #620.
* New utility class `com.github.bordertech.wcomponents.util.HtmlSanitizer` which can be used to sanitize HTML input
  (as shown in WTextArea). Needed for #620.
  * method `sanitize(String)` sanitizes using a strict policy.
  * method `sanitize(String, boolean)` sanitizes using an optional lax policy.
  * Policy definition files may be overridden using properties.
* New utility class `com.github.bordertech.wcomponents.util.StringEscapeHTMLToXML` which extends apache-commons lang3
  StringEscapeUtils. It adds exactly one (static) method `unescapeToXML(String)` which will convert HTML character
  entities to their unicode characters but will not unescape the five basic XML character entities. Very handy for
  converting HTML to valid XML! Needed for #620.
* Added a default margin style for p elements. This was added during testing of the fix for #620 to improve consistency
  of output.
* WTextArea in rich text mode will now honour this mode when read-only. Added as part of #620.
* WText, when encoding is off, will correctly handle HTML entities which are not XML entities. Added as part of #620.
* Enhanced separation of labels and inputs to improve a11y #683.
* Some responsive design improvements #671, #656.
* Updated the transform of WTab to allow rich content in the tab "button" #669.
* Re-implemented basic support for theme-level inclusion of web analytics #398.
* Updated Margin output in line with hgap/vgap to improve responsiveness and improve style guide compliance
  enforceability.

# Release 1.1.8
## Bug fixes
* Table constraints enforced #647
  [Issues](https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.8)

# Release 1.1.7
## Bug fixes
* Table pagination select mis-calculation #641
* Fix multiformcomponent plus icon #644
  [Issues]([https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.7)

# Release 1.1.6
## Bug fixes
* #626, #629, #631, #634, #635
  [Issues](https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.6)

# Release 1.1.5
## Bug fixes
* #610 #611 #612 #613 #616
  [Issues](https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.5)

# Release 1.1.4

## Bug fixes
* #562 #567 #574 #576 #577 #579 #583 #586 #588 #590 #592 #593 #601
  [Issues](https://github.com/BorderTech/wcomponents/issues?utf8=✓&q=is%3Aissue+milestone%3A1.1.4+)

# Release 1.1.3

## Bug fixes
* #542 #543 #546 #548 #549 #552 #554 #556

# Release 1.1.2

## Bug fixes
* #517 #521 #523 #525 #526 #527 #528 #529 #530 #531 #532 #533 #534

# Release 1.1.1

## API Changes
* WAudio and WVideo Controls.ALL, and Controls.DEFAULT have been **deprecated** as they no have no impact on the
  controls (#503);
* WAudio and WVideo Controls.NONE have been **deprecated** as they are not compatible with WCAG requirements that media
  be able to be turned off. These were always incompatible with autoplay to prevent a11y failure. This means that any
  media component with Controls.NONE has never been able to work and **will never** be able to work (#503).

## Enhancements
* Re-instated client side support for disabled state in WAudio and WVideo but this only applies when Controls.PLAY_PAUSE
  is used as there is no native disabled support in audio or video elements. The play/pause button **will not** be
  disabled if autoplay is set as this causes an a11y failure.

## Major Bug fixes
* Worked around an issue which prevented Sass from compiling on Linux with glibc below v13 and a related issue which
  prevented Sass from compiling correctly on Windows (#494).

# Release 1.1.0

## API Changes
These are API changes and enhancements which _may_ have some impact on current users.

* `WFilterControl` has been removed. This WComponent was originally designed as a low-quality row filter for `WDataTable`. It has been deprecated for some time. WDataTable is deprecated and slated for _imminent_ removal (though not in the next major release) (#306).
* Handle multi part AJAX requests. Tightened up the com.github.bordertech.wcomponents.Request interface for the return type of getParameters and getFiles (#341).

### New components
* Created new WTree component which is to be used for vertical and horizontal trees (#263).
* Created a new WTemplate component that supports velocity, handlebars and plain text. Projects can also implement their own templating engines (#94).

## Enhancements
For full details see [closed enhancements](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Aenhancement).

* Added a new property to WSuggestions: WSuggestions.Autocomplete. This is an enum which currently supports two settings `LIST` and `BOTH` but has the potential to support all settings of the HTML attribute [aria-autocomplete](https://www.w3.org/TR/wai-aria/states_and_properties#aria-autocomplete) if required. This has the usual accessors get/setAutocomplete. The default is WSuggestions.Autocomplete.BOTH which provides the same functionality as in previous versions of WSuggestions. When WSuggestions.Autocomplete.LIST is used then the client implementation will attempt to force selection of an option from the suggestions list rather than allowing free-text input (#379).
* Make server side transform disabled by default and do not do a server side transform if a theme content path has been set (#377).
* Improved mobile/responsive aspects of client rendering (#344).
* Simplified access to client code, especially CSS (#172).

## Major Bug fixes
For bug fixes in this release see [closed bugs 2016 Q1](https://github.com/BorderTech/wcomponents/issues?utf8=✓&q=is%3Aissue+milestone%3A%222016+Q1%22+is%3Aclosed+label%3Abug).
For all bug fixes see [closed bugs](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)
* Fix WTree AJAX actions. Introduced new AjaxInternalTrigger marker interface to indicate which components use AJAX to maintain their internal state (#447).
* Fix WTab in a WTabGroup causing a ClassCastException (#442).
* Fix WTable to allow setting row headers (#384).
* Fix an issue in WTable pagination where a table with many pages could cause some browsers to fail to render a view (#409).
* Fixed a bug which caused an AJAX trigger to fail when the trigger was inside a WDialog and the target was outside the WDialog (#457).

## Other
* The schema has been updated to make all UI element names lowercase. Why is this not an API change? because the schema is not part of the API - it is an internal contract.

# Release 1.0.4
## API Changes
None.

##Enhancements
None.

## Major Bug fixes
For all bug fixes see [closed bugs](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)


# Release 1.0.3

## API Changes
These are API changes and enhancements which _may_ have some impact on current users.

* `WDataTable.PaginationMode.SERVER` has been modified to implement `WDataTable.PaginationMode.DYNAMIC` to overcome an a11y problem inherent in `WDataTable.PagainationMode.SERVER`. Whilst `WDataTable` is deprecated it is being retained (for the foreseeable future) for backwards compatibility and therefore must meet a11y requirements. `WDataTable.PaginationMode.SERVER` has been individually deprecated and **will be removed** in a near future release.
* `WSubMenu` child count has changed. This was necessary to allow correct AJAX loading of sub-menus (#250). Applications should use `getMenuItems` instead of `getChildAt`.

### JavaScript API changes
* Client-side support of `WFilterControl` has been removed. This applied _only_ to (the deprecated) `WDataTable` but caused significant overhead during XSLT processing of all tables. The old code is available if a custom theme requires it.

## Enhancements
These are backwards-compatible API changes which are transparent for all current users. For full details see [closed enhancements](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Aenhancement).

* `WTable` API allows for setting the location of the pagination controls using `setPaginationLocation(WTable.PaginationLocation location)` (#297).
* `WTable` API allows for sub-row group selection (select all/select none) when a table has multiple row selection **and** row expansion enabled. The optional extra functionality allows for a select all/none control for every row which has one or more selectable sub-rows (#257).
* `WMessageBox` API allows for setting message box title string using `setTitleText(String title)` (#280).
* `WApplication` API allows for custom `js` and `css` resources to be easily loaded (#172).
* `AbstractWComponent` API allows for custom HTML class attribute values to be added to any WComponent for improved application-level customisation (#172).
* `WColumn` and `ColumnLayout` may now be created without specifying a width. The column widths _should_ then be specified in [application level CSS](https://github.com/BorderTech/wcomponents/wiki/Adding-custom-CSS). This is primarily aimed at applications which require responsive design (#172, #180).
* Added improved guards against clickjacking (#240).

## Major Bug fixes
For all bug fixes see [closed bugs](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)

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
