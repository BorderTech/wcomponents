# Change log

# Release [in-progress]

## API Changes

## Bug Fixes
* Fixed bug in able actions which could result in action buttons being in the incorrect (enabled) state if the table
  isloaded via AJAX #1049.
* Fixed a bug in modalShim which resulted in accesskeys being stripped from inside WDialog content #1051.
* Fixed AbstractContainerHelper to transform custom error pages #1103.

## Enhancements

# Release 1.3.2

## Bug Fixes
* Fixed a bug in XSLT which caused processor errors if an instance of Input has more than one validation error #1042.
* Fixed a bug in subordinate JavaScript which caused WMultiSelectPair to fail as a subordinate trigger #1044.

## Enhancements
* Reduced logging "noise" when the corrupt characters flag is enabled #1039

# Release 1.3.1

## API Changes
* Deprecated xslt server side property. Will always return true.

## Bug Fixes

## Enhancements

# Release 1.3.0

## API Changes

* 'UIContext' interface has been changed to include a new request scope scratch map #192.

## Bug Fixes

* Fix bug which allowed files larger than max allowed size to be uploaded using WMultiFileWidget #1024.
* Fixed position of validation errors in WMultiSelectPair #1021.
* Fixed content of labels in error messages #1019.
* Prevent pending i18n bug by applying a cache buster to requests for i18n resource bundles #1016.
* Fix possible a11y flaw by updating exposure of minlength to comply with HTML spec #1006.
* Fix examples-lde default config #1005
* Ensure file upload progress bars are shown for all asynchronous file uploads #1000.
* Fix a11y issue by ensuring popups are always scrollable and resizeable #997.
* Handle Google translate locales in i18n #994.
* Fixed WMultiFileWidget and WrongStepAjaxInterceptor that were sending XML responses that were not being
  transformed #990.

## Enhancements

* Output component name hint for Inputs in a read-only state to improve Selenium testing #1029.
* Improve performance by not calling unnecessary preparePaint in DataListInterceptor #975.
* Improve performance and type-safety of XSLT.
* `WBeanComponent` has been changed that when a `BeanProvider` is being used it can now use the new request scope
  scratch map to hold the bean. To opt into this functionality set the runtime parameter
  `bordertech.wcomponents.bean.provider.request.scope.enabled` to true #192.
* ServletResponse changed to provide access to the backing httpServletResponse #803.

# Release 1.2.15

## API Changes

## Bug Fixes

## Enhancements
* Allow handlebars templates to be rendered on the server to complete theme i18n.
* Backport interceptor chain fix #993.
* Fix loading shim "stuck" on IE8

# Release 1.2.14

## API Changes

## Bug Fixes

## Enhancements
* Remove corrupt characters before processing the XSLT #980. Runtime parameter needs to be set to true:-
      bordertech.wcomponents.xslt.allow.corrupt.characters

# Release 1.2.13

## API Changes

## Bug Fixes

* Fixed a bug which could cause dialogs to be mis-positioned on mobiles #977.

## Enhancements

# Release 1.2.12

## API Changes

## Bug Fixes

* Fixed issues which could cause resize and positioning errors in WDialog #958.
* Fixed WDialog that was not handling its open state correctly #963 QC160323.
* Fixed issues which could cause a WDialog to prevent refocus of the opener control when the dialog was closed #965.
* Fixed an issue which could result in a dialog opener to _not_ open its dialog if the opener contained an image #967.
* Fixed an issue which resulted in WShuffler outputting the incorrect HTML element when in read-only mode #972.
* Fixed an issue which could result in the content of a table header overlaying the sort icon and/or the sort icon being mis-positioned when the header content wrapped #973.

## Enhancements

* New DialogOpenTrigger interface to identify components that can open a dialog. WDialog can also have an action
  set via setTriggerOpenAction(action) to run when the dialog is opened via a trigger #963.

# Release 1.2.11

## API Changes

## Bug Fixes

* Fixed `WTree` AJAX expansion #934, QC158631.
* Fixed `WFieldSet` to handle `WRepeaters` correctly when Mandatory is set #936 QC158462.
* Fixed a flaw in client code of `WSelectToggle` which could cause unexpected results if the target component(s) included a `WTable` with **single** row selection #938.
* Fixed a flaw which could result in dialogs sometimes running partly off screen (#940).
* Fixed an issue which could result in a WTableAction trigger button becoming enabled if disabled row(s) were selected. This is part of #943.
* Fixed an issue where a dialog trigger button's action could be erroneously fired by a dialog re-opening on page load #945.
* Fixed missing "day of week column headers" in calendar date picker (#942).
* Fixed `WSubMenu` AJAX not working (#889).
* Fixed `WDateField`, `WPartialDateField` and `WNumberField` validation. Only do validation if value is valid. (#951).
* Fixed `WTabSet` to allow `ACCORDIAN` tabsets to have no tabs open (#915).

## Enhancements
* Allow TinyMCE global configuration to be overriden without a theme rebuild.

# Release 1.2.9

## API Changes

## Bug Fixes

* Fixed table row selection/dialog bug #929.

## Enhancements


# Release 1.2.8

## API Changes

## Bug Fixes

* Mitigated a11y issues in WTable (#926).

## Enhancements

* Added a workaround for a CSS bug in IE11 (#924).

# Release 1.2.7
## API Changes

* Deprecated the following WPanel Types (part of #689, #639):
  * Type.ACTION: deprecated in favour of WSection to remove API ambiguity.
  * Type.BANNER: deprecated in favour of WSection as never implemented but JavaDoc points to WSection equivalence
  * Type.BLOCK: deprecated in favour of Type.PLAIN and Margin as no longer serves any useful purpose
  * Type.CHROME: deprecated in favour of WSection to remove API ambiguity.

## Bug Fixes

* Applied a workaround for buggy implementation of placeholders in textareas in IE11 #911.
* Fixed bug in WSuggestion where user text was not updated after first use (QC158630).
* Fixed i18n race condition causing placeholder, message title (all theme messages) not to display in some browsers (QC158400).
* Reverted date field to previous behavior of attempting to honor invalid input value after round trip (QC157989).

## Enhancements

* Added CSS support for an additional class `wc-neg-margin` on WMenu Type.BAR to force the "docking" style if it is not possible to make the menubar the first child of a WPanel with no layout #916.
* Updated JavaScript Widgets used to determine WDateField and WPartialDateField to remove some potential access errors when either of these components is in a read-only state #907.

# Release 1.2.6
## API Changes
* None

## Bug Fixes
* QC157496 - Fixed a null pointer exception within WDateField resetData()

## Enhancements
* Enhanced TransformXMLInterceptor to support non-HTML agents.
* Minor performance turning for TransformXMLInterceptor.
* Dependency management improvement for Saxon so that it does not need to be the default XSLT engine.

# Release 1.2.5
## API Changes
* Added a new ServiceLoader API to inject custom configuration into WComponents using the interface ConfigurationLoader.

## Bug Fixes
* Fixed a bug which prevented `WField`'s `inputWidth` setting from rendering correctly #854.
* Fixed a bug in rendering of `WPartialDateField` and polyfilled `WDateField` #852.
* Fixed UI bugs in WMenus #866, #867.
* Fixed a Sass bug which caused ListLayouts with layout FLAT to lose intra-component space #869.
* Fixed a bug which prevented a control which opens a WDialog from running a server Action #875.
* Fixed a bug which caused image editor to fail if multiple files were attached at once #876.
* Fixed a bug which prevented timeout warnings from rendering a session elapsed message correctly #890.
* Fixed a bug where `WTree` was adding an open item request to expanded rows #762.
* Fixed a bug where `resetData()` was not handled correctly in `WNumberField`, `WDateField`
  and `WPartialDateField` #896.
* Fixed a bug which could cause a tabset to get into an invalid state #899.

## Enhancements
* Added a mechanism to add and remove multiple HTML class attribute values to a component #856.
* Added a mechanism to mark a WButton as having an action only in the client #878.
* Plugged a minor potential vulnerability in WLink #895.
* Added a lot of Sass configuration options to `_common.scss` along with additional commentary #689.
* Projects can override a new method `getUiVersionKey()` in `WApplication` if different versions of the same Application
  need to be registered by `UIRegistry` #894.
* New constructors in `WPartialDateField` to allow a padding character to be passed in #573.
* Provide access to the backing `HttpServletRequest` in `ServletRequest` and a new helper method in `ServletUtil` to
  determine the user's device #803.

# Release 1.2.4
## Bug Fixes
* Fixed bug in XSLT of inline errors #847
* Fixed bug in i18n of timeout warnings #846

# Release 1.2.3
## API Changes
* Javascript API i18n module now returns the message key instead of an empty string if the translation is not found.
* Deprecated `WDialog(WComponent, WButton)` as part of #407. Replaced with use of other constructors along with new
  accessors `setTrigger(AjaxTrigger)` and `AjaxTrigger getTrigger()`.

## Bug Fixes
* Fixed bug which could result in dialogs being mis-positioned #805.
* Fixed a11y of combo boxes #808, #809.
* Fixed a bug in which the loading shim was removed before the page was ready to use #822.
* Fixed bugs in `WTextArea` in RichText mode #825.
* Fixed bugs which prevented correct output of `WLabel`, `WAbbrText` and `WHeading` if content escaping was turned off.
* Fixed a bug in `ComponentModel` triggered when data is `java.util.Stack` #838.
* FIxed XSLT bug which could result in a `WFigure` losing its figcaption under some circumstances #841.

## Enhancements
* JavaScript API added a utility module to centralize determination of toggle points for responsive UI updates.
* Rewrote the JavaScript i18n module so that it is a thin wrapper around [i18next](http://i18next.com/) instead of
  custom code.
* `WDialog` may be launched by any component which implements `AjaxTrigger` #407.
* Updated client implementation of `WSuggestions`/combo boxes to improve accessibility #808, #809.
* Moved i18n to JavaScript and improved load-time reliability #819, #732, #639, #689.

# Release 1.2.2
## Bug Fixes
* Fixed a bug in `com.github.bordertech.wcomponents.subordinate.AbstractCompare` which resulted in Subordinate controls
  returning an incorrect value if the control was in a read-only state #780.
* Fixed a newly introduced bug which caused textareas to fail to accept newlines in IE11 #785.
* Fixed several IE CSS issues.
* Fixed a bug which caused the incorrect HTML className to be set using HtmlIconUtils to place a custom icon "AFTER"
  element content.

## Enhancements
* Added mechanism to convert tabsets to accordions on small screens #783.
* Allow placeholder to be set on relevant components #702.
* Added new component WToggleButton which renders a single checkable component in a button form. This may be used as a
  WSubordinateControl trigger or a WAjaxControl trigger #428.

# Release 1.2.1
## API Changes
* The new (in 1.2.0) class `HtmlClassUtil` has been refactored to a properties enum and is now `HtmlClassProperties`
  (same package). If you have already started using `HtmlClassUtil.HtmlClassName` should be replaced with
  `HtmlClassProperties`.
* The Seleniun API has been rewritten to better support client-side testing of WComponents. see
  https://github.com/BorderTech/wcomponents/wiki/Testing for more information.

## Bug Fixes
* Updated DataListInterceptor and ServletUtil to allow DataLists to be sent as HTML rather than XML (#747).
* Fixed various bugs in WTree client code (#768, #769, #770).
* Fixed an issue in JavaScript `dom/shed` which could result in false negsatives when testing if a UI artefact is hidden
  as exposed in `dom/getFilteredGroup` (#771).
* Fixed accessibility issue with WAI-ARIA based controls (#765).
* Fixed several client-side menu bugs (#755, #746, #741).
* Fixed a bug which caused client-side validation of WDateField to throw an error (#757).
* Fixed a bug which caused the work-around for IE's interesting form submission policy to fail (#740).
* Fixed a bug in client-side code of WSubordinateControl which could result in a subordinate target being in the
  incorrect state if its controller was disabled or enabled by another WSubordinateControl (#758)

## Enhancements
* Updated `HtmlClassProperties` to add a menu icon to the enum.
* Added a util class `com.github.bordertech.wcomponents.util.HtmlIconUtil` which supplies helpers to attach the ICON
  classNames supplied by `HtmlClassProperties` and icons from Font Awesome which are not part of the common set.
* Updated how CSS is loaded and applied to remove some mis-matches between JavaScript loading and CSS media queries
  (#746).
* Removed all deprecated media queries from Sass (min/max-device-width) (#745).
* Updated "public" CSS classes to provide more options to components and WTemplates.
* Updated the mechanism to collapse a menu (Type BAR) to a single submenu so that it is applied on request (setting
  HTML class value `wc-respond`, may be applied to any WMenu of Type BAR or FLYOUT anywhere in the UI and is applied
  based on viewport size rather than based on screen dimensions at load time (#520).
* Reinstated Selenium tests (#10).

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
