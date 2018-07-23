# Change log

## Release in-progress

### API Changes

### Enhancements

### Bug Fixes

* Fixed XSLT flaw which caused JavaScript Error if WDataTable has row selection and pagination #1556.

## Release 1.5.8

### Enhancements

* Remove style CSS from default theme combo, ensure combo button is same height as combo text input #1552.

### Bug Fixes

* Restore tab cursor #1553.
* Fixed missing WColumn alignment in themes #1551.

## Release 1.5.7

### Bug Fixes

* Restored theme build-time concatenation of XSLT to work around an application container flaw #1547.

## Release 1.5.6

### Enhancements

* Ensure the xml preamble and opening `xsl:stylesheet` tag is consistent in all XSLT files - help to reduce likelihood of namespace issues in transformed HTML.
* Enforce Sass lint at theme build time. See wcomponents-theme/.sass-lint.yml for default rules.
* Updated all internal uses of `org.apache.commons.lang.*` to use ``org.apache.commons.lang3.*` which is the direct dependency in WComponents. #1539

### Bug Fixes

* Updated WMenuGroup XSLT to prevent double separators #1544.
* Update version of npm sass module to fix build failure on Windows #1541.
* Fixed a flaw which would cause themes to fail to build if the inherit file had a terminating empty line. Part of #1492.

    Requires themes which inherit from any theme other than wcomponents-default to replace the theme in `inherit.txt` with a Maven property in POM.xml of `theme.inherit`. May be a path to a ZIP or directory tree (relative and absolute paths are both acceptable).

    ``` xml
<properties>
  <theme.inherit>/PATH/TO/the_parent_theme</theme.inherit>
</properties>
    ```

    The previous method of trying to guess the parent theme from a name assuming a theme path is partially supported by using the previous `inherit.txt` value along with a second Maven property `theme.inheritance.dir`. This, it is plain, is a bit of a pointless waste of time since the path could be added directly to the `theme.inherit` property.

    ``` xml
<properties>
  <theme.inherit>the_parent_theme</theme.inherit>
  <theme.inheritance.dir>/some/path</theme.inheritance.dir>
</properties>
    ```

## Release 1.5.5

### Bug Fixes

* Fixed an error which caused WSelectToggle to fail #1529.
* Removed superfluous layout CSS from WFigure which could result in unexpected display in IE 11.
* Partial (cosmetic) fix of a flaw which could result in visible suggestions for a combo if it was in read-only mode #1527.

## Release 1.5.4

### API Changes

* Added support for autocomplete to WDropdown, WEmailField, WNumberField, WPasswordField, WPhoneNumberField, WTextArea
  and WTextField #1007.
* Changed message property name
  `public static final String DEFAULT_MULTI_FORM_COMPONENT_TIP = "bordertech.wcomponents.message.multiFormComponent.tip";`
  to `public static final String DEFAULT_MULTIDROPDOWN_TIP = "bordertech.wcomponents.message.multiDropdown.tip";`
  as the tip is not suitable for use with WMultiTextField #1508.
* Added message property
  `public static final String DEFAULT_MULTITEXTFIELD_TIP = "bordertech.wcomponents.message.multiTextField.tip";` with
  default (en) value of "Enter a value" #1508.

### Enhancements

* Removed some style (rather than structure) CSS for mandatory indicators. Style should be the remit of themes.
* Added JavaScript unit tests for missing modules.

### Bug Fixes

* Fixed XSLT error which resulted in WToggleButton not reporting its state #1525.
* Fixed missing custom class on WDefinitionList #1519.
* Fixed XSLT error causing WHeading content to include text equating to the value of its margin classes #1514.
* Fixed potential flaw caused by application-level custom CSS source order #1516.
* Fixed a flaw which caused selenium tests to fail if no class was defined in the URL #1510.
* Fixed missing inner labels on WMultiDropdown and WMultiTextField #1508.
* Fixed issue causing 'required' placeholders to be left when making a field optional #1506.
* Fixed missing name attribute on WDateField #1507

## Release 1.5.3

### Enhancements

* Improved handling of the publication of selected state changes of native radio buttons and check boxes.
* Update package.json to latest working node module versions and removed tildes to prevent unwanted updates.
* Added support for success and info level diagnostics/field indicators as part of #1496.
* removed some Sass for inputs and buttons which attempted to provide style rather than structure.
* provided config options to allow client validation on change/blue #1495.
* Further build simplification required for move away from ANT/Maven #1318, #1492.
* Simplified all XSLT and XSLT build as step towards #639 and #1492.
* Added a default shim background #1486.
* Removed duplicate focus code #1488.
* Removed superfluous loading screen code #1487.
* Removed unused code from `wc/ui/feedback` and `wc/dom/messageBox` #1489.
* Moved font-awesome out of WComponents CSS and into a link element to improve efficiency and maintainability towards #639.
* Fixed minor ANT build inconsistencies.
* Prevent WTable pagination buttons from working when they are disabled or read-only #1490.
* Updated .gitignore to exclude eclipse-like artefacts built during Maven lifecycle.

### Bug Fixes

* Allow JavaScript module config to keep existing config properties when calling `set` #1502.
* Fixed inconsistency of rendering field indicators #1496.
* Fixed icon element inconsistencies #1497.
* Fixed a number of client validation issues #1495
* Fixed timyMCE init routine #1485.

## Release 1.5.2

### Enhancements

* Allow override of Sass compiler from Maven POM or Settings.
* Removed unused Sass and JavaScript.
* Enable use of a JavaScript Sass compiler ((sass)[https://www.npmjs.com/package/sass]) instead of Vaadin Sass Compiler.
* Server-side validation of File(s) uploaded using WFileWidget and WMultiFileWidget #1079.

### Bug Fixes

* Fixed a Sass bug hidden by a flaw in the Vaadin Sass Compiler.
* Change order of load of `wc/common` and `wc/loader/style` to enable themes to override the initial style loader config.
* Fix an error in Sass for WFieldLayout which was masked by Vaadin Sass Compiler
* Fix a potential negative zero error in Sass of WSection (responsive)

## Release 1.5.1

### API Changes

* Changed WProgressBar API to better implement HTML spec:
  - Deprecated WProgressBar members as follows:
    - UnitType - not supported - `public void setUnitType(UnitType)` is now a no-op and `public WProgressBar.UnitType getUnitType()` will now always return `null`
    - text - not required use toolTip instead - `public void setText(String)` is now a no-op and `public String getText()` will now always return `null`
    - Constructors which reference UnitType
  - Added constructors `public WProgressBar(ProgressBarType)` and `public WProgressBar(ProgressBarType, int)`
  - Put type checks around constructors and setters so that `ProgressBarType` cannot be made `null` and `max` and `value` cannot be negative.

### Enhancements

* Changed the way themes are built to remove a vast amount of complexity and wasted styles. Improves maintainability and moves towards separation of style and structure in themes.
* Updated renderer of WProgressBar to output HTML as part of #639.
* Added JUnits for WProgressBar.
* Updated renderers of WButton and WPrintButton (which has been un-deprecated) to output HTML as part of #639.

### Bug Fixes

* Fixed a XSLT issue which caused inconsistent placement of label hints #1476.
* Fixed a Sass issue which could cause unexpected wrapping of content of read-only Inputs #1469.

## Release 1.5.0

### Enhancements

* Enforce Java 1.8 as minimum compiler version and set source and target properties to 1.8 #1307.

## Release 1.4.24

### Enhancements

* Updated WTree Sass to remove unnecessary left padding from default implementation #1461.
* Removed some duplicate code from tabset Sass.
* Fixed inconsistency in detecting a disabled link.

### Bug Fixes

* Fixed an error which could result in incorrect CSS overrides of WCheckBoxSelect and WRadioButtonSelect #1462.

## Release 1.4.23

### API Changes

### Enhancements

* ImageEdit press buttons now also respond to clicks.

### Bug Fixes

* Fixed a bug which could result in incorrect determination of selected items using module wc/dom/getFilteredGroup #943.
* Updated defaultSubmit module to bring it into line with the HTML spec and fix an issue found in compliant browsers #1122.
* Fixed action constraints not working across pages on a paginated table #1078
* Fixed a flaw which resulted in WSkipLinks not being transformed #1420.
* Fix date field causes AJAX loop if it is trigger and target #1455

## Release 1.4.22

### API Changes

### Enhancements

### Bug Fixes

* Image editor accepts jpeg or jpg as a valid jpg filename extension #1443
* Fixed several keyevent and combo JavaScript bugs #1430, #1442, #1444
* fixed a Sass error which caused disabled icon buttons to have hover effects if invite is implemented.
* Change the way table actions are registered to rectify #1433.

## Release 1.4.21

### Enhancements

* Updated the theme axe-core dependency to the latest version (2.6.1) and re-written the `wc/debug/a11y` module to leverage the new API. The module is not included by default, even in debug mode.

### Bug Fixes

* Fixed a Sass error which could result in incorrect margins in some circumstances #1437

## Release 1.4.20

### API Changes

### Enhancements

* ImageEdit enhancements:
 - Prevent loading of "large" images in the editor because this could cause the page to become unresponsive. Instead skip the edit phase and handle as normal.
 - If an image is scaled to render in the editor this should be considered as a user change when determining whether or not to pass through the original image unchanged.
 - If an image size exceeds validation constraints then attempt to remedy this automatically
* Removed unnecessary wrapping elements from the inner options of WCheckBoxSelect and WRadioButtonSelect which will improve their accessibilty and responsiveness.
* Fixed some long-standing issues in the XSLT by refactoring the readOnly helper thereby reducing its ridiculous complexity. It was useful when XSLT was going to the client but had become mere technical debt making the cinversion from XSLT to alternate rendering more complex.
* Added a Sass var to allow configuration of the maximum number of supported columns within WCheckBoxSelect and WRadioButtonSelect when their Layout is COLUMN.

### Bug Fixes

* Fixed a Sass error which resulted in an unexpected number of sub-row depths in some WTables.
* Fixed a flaw which caused some fieldsets to not have a correct legend child.

## Release 1.4.19

### API Changes

### Enhancements
* Client side: wc/dom/getLabelsForElement an enhancement to load elements defined by 'aria-labelledby' attribute #1401.
* Improve UX of image editor validation #1431.

### Bug Fixes

* Fixed a Sass issue which could result in messages not wrapping within some components depending on Sass build order #1422.

## Release 1.4.18

### Bug Fixes
* Work around IE11 bug which causes focus to be set to the body when a scrollbar is clicked (QC171025).
  This caused combobox lists to be closed when trying to scroll with mouse.
  Combobox lists now close when an interactive component is focused, instead of when ANYTHING else is focused.

## Release 1.4.17

### Bug Fixes
* MustacheFactory cannot switch off caching which can have adverse memory usage. As per the issue http://spullara/mustache.java#117 the recommended way to provide this functionality is to create a new MustacheFactory on every call to compile the template thus implicitly getting a clean cache every time #1290.

## Release 1.4.16

### Bug Fixes

* Restored WFieldWarningIndicator and WFieldErrorIndicator to WField for backwards compatibility.

## Release 1.4.15

### Enhancements

* Client side: wc/config now caters for common "fetch, test, override" pattern of module configuration.
* Client side: imageEditor can now be configured through the "wc/config" module.

### Bug Fixes

* Update field indicator renderer to allow them to appear for Inputs in a read-only state. This is for backwards compatibility.
* Client side: wc/dom/initialise could theoretically reset its observer instance before all subscribers had been called, this is now resolved.
* Fix position of messages relative to calendar launch button in polyfill of WDateField #1405.
* Fix white-space of error and warning messages inside combos, WMultiSelectPair, WMultiDropdown and WMultiTextField #1404.

## Release 1.4.14

### Bug Fixes

* Removed a cause of potential NullPointerException from com.github.bordertech.wcomponents.qa.findbugs.CheckGetComponentModel and
  com.github.bordertech.wcomponents.examples.table.FilterableTableExample #1392, #1393.
* Fix an error in the wcomponent-theme POM #1390.
* Fixed regressions from 1.4.12 which could result in messages not being rendered correctly #1370.
* Fixed an issue which could result in unexpected element alignment due to an error in the whitespace filter #1381.
* Change the way dropdown typeahead (selectboxSearch) works to better align with the way most modern browsers fire the change event QC169945.

## Release 1.4.13

### Bug Fixes

* Fix validation messages being merged with data in several components QC170096 (broken in 1.4.12).
* Fix schema validation (broken in 1.4.12).
* Update default field validator to fallback to `toolTip` if the input is both unlabelled _and_ has no `accessibleText` #1372.

## Release 1.4.12

### Bug Fixes

* Fixed a flaw in message handling which resulted in error and warning messages not being applied correctly in almost all AJAX-related scenarios #1370
* Fixed a number of errors in messaging and validation in the client code as part of #1370, fixing these resolves #1121

## Release 1.4.11

### Enhancements

* Updated the theme-parent POM to enforce Maven version checking as building with 3.0.x will fail in a very opaque manner.
* Moved the Java version check from a slightly convoluted ANT check to Maven.

### Bug Fixes

* Added a workaround to width calculation errors in UC Browser on Android OSs #1354.
* Fixed a flaw which could result in duplicate IDs in responsive menus #1357.
* Fixed a flaw in placeholder manipulation during mandatory/optional toggling #1360.
* Fixed a11y issues caused by labelling of mandatory fields #1359.

## Release 1.4.10

### Enhancements

* Allow more granular control over features available in image editor
* Apply config overrides when image editor opened instead of page load so that it is easier to set config "in time"
* Implemented new features in image editor:
  * center image
  * reset image
* Update WAI-ARIA analog class to treat RETURN as equivalent to SPACE for all analogs #1351, QC 169101.

### Bug Fixes

* Provided Sass to work around an IE feature in which buttons with element descendants show unexpected cursor behaviour QC 168545.

## Release 1.4.9

### Enhancements

* Moved placeholder text determination to the renderers to reduce the reliance on client side i18n.
* Improved render performance by removing a superfluous call to `hasTabIndex()` in order to set a `tabIndex` attribute. `hasTabIndex()` will always
  return `false` so this was a waste of everyone's time and clock #373.

### Bug Fixes

* Fixed issue which could result in a label:input pair ending up in an invalid state after AJAX of specific inputs #1337.
* Fix issue which could result in labels for individual mandatory WRadioButtons being decorated with a mandatory indicator #1335.
* Fix issue where tree menu icons were not expanded after round trip #1325.
* Updated timeout warning artefact and JavaScript to overcome an accessibility issue #1333.
* Fixed a CSS error which caused items at the top level of a WMenu of types BAR or FLYOUT to render incorrectly if they had anything other than
  simple text content #1330.
* Fixed a flaw in update calculation which resulted in unsaved changes warnings not appearing in very specific circumstances #1237.
* Fixed an error which could cause out-of-viewport labels to be rendered in viewport #1326.
* Fixed some errors in examples which caused a race which could result in null pointer exceptions #1327.
* Clean up XSLT, schema references and broken examples of non-resizeable dialogs as `resizable` has been mandatory for several releases #606.
* Fixed an omission which could result in an application being able to avoid the `submitOnChange` warning text in labels #1255.

## Release 1.4.8

### Enhancements

* Added "application/dicom" to mimemap.json #1321.
* Removed colormap.xml as it is not being used: use colormap.json instead.
* More Sass workarounds for UC Browser issues #1295.

### Bug Fixes

* Fixed an XSLT bug which caused WTable's expand-all button to fail when row expansion is LAZY or DYNAMIC #1319.

## Release 1.4.7

### Bug Fixes

* Fix CSS bug which resulted in a calendar date picker not rendering as expected when in a dialog box #1309.
* Fix a bug which would cause a self-opening dialog to attempt to reopen itself on click #1311.
* Fix a bug which could cause erroneous ajax requests #1312.

### Enhancements

* WTabSet:
  - Added methods to WTabSet to allow easier/less verbose creation of tabs by making the TabMode argument optional.
  - Deprecated the unused members `setShowHeadOnly` and `isShowHeadOnly` which have never been implemented and which were a hangover from a very old and rather poor design concept. No replacement: never implemented.
  - Deprecated `setActionOnChange` and `getActionOnChange` as changing tabs should not have a side effect _and_ these actions are inconsistent unless the (no longer supported) `TabMode.SERVER` is used for **all** tabs in the tabset. No replacement: a tabset should not have an action on tab change other than show the relevant tab.
  - Added `protected addTab(WTab)` as a replacement for the deprecated `public add(WTab)`.
* WImageEditor:
  - Can now render the editor controls inline (as opposed to the default, in a popup).

## Release 1.4.6

### API Changes

### Bug Fixes

* Fixed a race condition which could result in modal dialogs not being modal #1296
* Improved Mustache memory handling #1290
* Fixed a JavaScript flaw which could result in error messages being misplaced in some circumstances #1288.

### Enhancements

* Updated Sass to produce better rendering in UC Browser #1295:
  - added a has test to detemine if a browser does not support CSS fle;
  - added a has test `has("uc")`;
  - added UC pattern Sass to fix major rendering issues.
* Enhanced the SubordinateBuilder & associated classes to remove the final declaration allowing for extendibility.
* Added yet more enhanced AJAX error handling.

## Release 1.4.5

### Bug Fixes

* Debounce / throttle rapidly repeated requests for the same theme resources to prevent superfluous network requests #1274
* Fixed a flaw which caused WPartialDateField's calendar to render poorly on some mobile devices #1280.

### Enhancements

* Added Sass to allow re-implementation of support for WField.inputWidth from 1 - 99 in sub-themes based only on Sass variables #1278.
* Added a close icon to the header of the timeout warnings. These warnings can be closed by clicking anywhere (or pressing ESCAPE) but that is not immediately obvious and the close icon is merely a visual queue to indicate the box may be dismissed. Note that for screenreaders etc the box is exposed as an alert which has implicit ESCAPE to dismiss.

## Release 1.4.4

### API Changes

* JS only: deprecated the use of wc/i18n/i18n as a loader plugin (in favor of async methods).

### Bug Fixes

* Fix flaw which caused WButton with a message to not stop ajax submit if the button was an ajax trigger **and** the user chooses to cancel the button action # 1266.
* Fix flaw which prevented WShuffler acting as an ajax trigger #1267.

### Enhancements

* Better handling of rejected promises in Subscribers to the Observer module.
* Upgraded FabricJS 1.7.11 -> 1.7.14 to fix issues in Internet Explorer 11.
* Allow custom AJAX error handlers so that we can handle *any* response format conceivable, e.g. XML, JSON, protobuf, binary.

## Release 1.4.3

### API Changes

* Deprecated `AbstractInput.setSubmitOnChange` and `AbstractInput.isSubmitOnChange` and all overrides thereof. This is a source of significant a11y
  difficulty and should be removed #1255.
* Client side:
  - shed.js subscribers can return a promise and shed.notify will resolve when all subscriber promises complete.
  - i18n translation methods can now take an array of keys to translate.

### Bug Fixes

* Fixed an XSLT error which caused read-only WNumberField to appear to be editable #1262.
* Removed a potential source of null pointer exceptions in WRadioButton.handleRequest which had been masked by most WRadioButtons not having
  `submitOnChange` set #1258.
* Updated load-time focus requests (module `wc/ui/onloadFocusControl`) so that the focus request is not honoured if the load is a full page load (not
  ajax) and there is a message box (`WMessageBox`, `WValidationErrors`) visible on the page #1253.
* Fixed a flaw which could result in a fieldset not having a legend under some circumstances #1257.
* Updated components which automatically try to refocus themselves during or after handleRequest so that the focus will only be set if the component
  is the trigger for the current ajax request. This means the old `submitOnChange` focus will not be implemented. This fixes a major a11y flaw #501.
* Removed IE 11 specific dialog Sass which proved to be not only superfluous but actually harmful #1247.
* Fixed an XSLT bug which caused incorrect TAB key behaviour in some menus #1249.

### Enhancements

* Improved JavaScript unit tests; added mechanism to do local automated testing with optional coverage; improved the intern test skeleton.
* Added new eslint rules (eslint:recommended) and fixed a pile of stylistic issues in JavaScript.
* Rewrote several synchronous i18n calls to use the async version (fixed at least one more definite race condition).
* Removed a workaround for a [Firefox issue](https://bugzilla.mozilla.org/show_bug.cgi?id=984869) as that issue is now resolved #1250.

## Release 1.4.2

### Bug Fixes

* Replaced support for `WDropdown` `optionWidth` on native dropdowns (should not be there) but in a more configurable and potentially responsive way
  #1243.
* Updated theme resource build to split core and implementation copy into separate steps #1222.
* Fixed an error which caused the maximise button in dialog frames to sometimes display the wrong state #1229.
* Fixed an XSLT error which caused incorrect render of `WPrintButton` when the button contained an image #1232.

### Enhancements

* Added a Selenium WebElement extension for WMultiDropdown #605.

## Release 1.4.1

### Bug Fixes

* Prevent race condition in IE when using AJAX module very early (e.g. i18n).
* Update fabricjs to latest version.
* Image edit produces files with filename extension consistent with mime type.
* Edited images maintain the original image dimensions (unless they are cropped).
  - Image redaction objects are scaled accordingly.
* Fixed redaction checkbox non-functional.
* Fixed an error in the Sass vars which are used to build WFieldLayout CSS for various conditions of support.

## Release 1.4.0

### API Changes

* Default template render mode to on (previously off). This improves UI performance for most users. #1158.
* Selenium performance API changes #1138:-
  - The methods in TreeUtil (i.e. findWComponent and findWComponents) that use a path to find components have been
    changed to search only visible components. New find methods that provide a visible only boolean flag have been
    included to toggle this. This change will effect selenium tests that use byWComponentPath with invisible
    components in their path. Either change the path to use only visible components or use the new constructor
    on byWComponentPath that allows the visible only flag to be toggled.
  - SeleniumWComponentsWebDriver and SeleniumWComponentWebElement provide new findImmediate methods that can be used
    to find an element without the wait implicit. This assumes the page is already loaded and will return immediately
    if the element is not present. The findElement methods now default to not wait for the page. New findElement methods
    provide a wait boolean flag to toggle this.
  - SeleniumWComponentsWebDriver and SeleniumWComponentWebElement provide a new helper method getUserContextSession()
    to retrieve the user context.
  - SeleniumWComponentTestCase provides a new helper method getUserContextSession() to retrieve the user context.
* Client side API: removed `wc/template.registerHelper`.

### Bug Fixes

* Change Sass linter from scss-lint to sass-lint, removed some custom rules and refactored Sass to be more "standard" #1203.
* Reverted a change to URL handling which resulted in URLs becoming mal-formed under some circumstances.
* Improved the Sass to CSS build to ensure implementation CSS is placed after default CSS in the output #1160.
* Modified `HtmlSanitizerUtil` and `HtmlToXMLUtil` to handle escaping brackets'; `WTextArea` now defaults to `santizeOutput` on #1158.
* Remove client handlebars i18n support #1158.
* Fixed position of server-generated error messages for simple WInputs #1161.
* Fixed partial text matches in dropdown typeahead #1164.
* Improve AJAX error handling when the response "lies" and says it is "200 OK" when it isn't #1163.
* Fixed issue in ByWComponentPath that was leaving the user context on the Thread. Unit tests that check a component's
  state may have depended on this wrong behaviour. These tests will need to be changed to push and pop the User Context
  when checking the components state #1138.
* Fixed image editor (part 2), this should hopefully fix the regressions originally addressed in #1206.
* AJAX Controls and Subordinate Controls are now registered on the User Context. This allows WComponent Applications to
  be run on multiple servlets. AjaxHelper and SubordinateControlHelper methods no longer include the “request” parameter
  as it is not required. This should have no impact to projects as these methods are only called by framework code.
  UIContextHolder has new helper methods to retrieve the Primary User Context #1077.

### Enhancements

* Improved efficiency of XML escaping and sanitization as part of #1158.
* Removed PetStore #1190.
* Selenium performance enhancements #1138:-
  - Upgrade JUNIT 4.8.2 to 4.12 and Surefire Plugin 2.18 to 2.20.
  - WebDriverCache now uses a concept of a pool of drivers. Improves performance for parallel selenium tests.
  - Provide a new ServerStartStopListener for JUNITs to start and stop the web server for selenium tests.
  - Improved selenium utility classes and helper methods.
  - WComponent example module demonstrates running selenium tests in parallel.
* Improve performance of Selenium tests and helpers #1138.
* Improve performance of TemplateRendererInterceptor by switching to Mouchstache template engine #1207.
* Switched (back) to Mustache on the client to match the server implementation.

## Release 1.4.0-beta-2

### Bug Fixes

* Fixed a flaw which could result in images inside links getting the incorrect URL under some conditions #1150.
* Fixed a flaw which could result in a WSubMenu being stuck open when a not-submitting WMenuItem is clicked #1137.
* Fixed an accessibility flaw which could result in AT attempting to read icons #1136.
* Fixed a JavaScript flaw which could result in unhandled events on buttons in some conditions #1135.
* Fixed a newly introduced bug which caused failure of WMenuItem selection #1134.
* Fixed a flaw which could result in non-selectable menu items being set as selected in WMenu #1133.

### Enhancements

* Simplified Sass and removed unnecessary icon CSS as part of the fix for #1136. This included removing some Sass which
  is no longer used but had not been cleaned up at the time.
* Added a new generic class to HTMLCLassProperties. Class `wc-border` will apply the theme default border to any
  component which does not already have a border style set by a more specific (or later equally specific) selector.
* improved efficiency of CSS by deleting WComponents custom icon classes and using fa classes instead. This involved
  changing the string values of elements in the HTMLClassProperties enum.
* Simplified delayed, self-actuating Ajax triggers, removed module wc/ui/ajax/delayedTrigger.
* Updated the new space class to fix some redundancy.

## Release 1.4.0-beta

### API Changes

* Deprecated `com.github.bordertech.wcomponents.util.ConfigurationProperties.DEVELOPER_DEBUG_CLIENT_SIDE` in favour of
  `com.github.bordertech.wcomponents.util.ConfigurationProperties.DEVELOPER_DEBUG_ENABLED` as part of #1012.
  * Deprecated `com.github.bordertech.wcomponents.util.ConfigurationProperties.getDeveloperDebugClientSide()` in favour
    of `com.github.bordertech.wcomponents.util.ConfigurationProperties.getDeveloperDebugEnabled()`
  * Deprecated `com.github.bordertech.wcomponents.util.DebugUtil.isDebugStructureEnabled()` in favour of
    `com.github.bordertech.wcomponents.util.DebugUtil.isDebugFeaturesEnabled()`.
  * Removed all internal references to the deprecated members.
* Deprecated `com.github.bordertech.wcomponents.lde.DevToolkit`; it is marked for removal due to functional and a11y
  problems.
* `WMenuItem` & `WSubMenu`: added public method `boolean isTopLevelItem()` required by the renderer of each.
* `WSubMenu`: `getSelectability()`, `setSelectability(Boolean)`, `setSelectable(boolean)` and `setSelected(boolean)` are
  all deprecated (always ignored in client UI).
* `WMenuItemGroup`: `isDisabled()` will return true if the group is nested in a `WSubMenu` or `WMenu` which is disabled.
  This brings it into line with `WMenuItem` and `WSubMenu` and provides for no UI change as previously this calculation
  was in the client UI.
* Margins and intra-component spaces (`hgap`, `vgap`) have been converted from `int` to values of an enum.
  They were historically set as `int` but were output into the UI as an enumerated list of options based on
  nearest-high-neighbour calculations. This was done to improve consistency and enhance responsive design. These
  calculations have been removed from the client UI to Java. A temporary backwards compatibility layer
  has been provided and all former `int` based constructors and getters have been deprecated. The values used for the
  conversion points are as per the former conversion client code so there will be **no visible or functional change**.
  * Added new utility class `SpaceUtil` used to manage consistent inter- and intra- component spaces.
  *  `Margin`, `BorderLayout`, `ColumnLayout`, `GridLayout`,`FlowLayout`, `ListLayout`, `WList`, `WRow`
    * All current `int`-based constructors are deprecated in favour of Space based constructors.
  *  `Margin`
    * `int getAll()` deprecated in favour of `SpaceUtil.Size getMargin()`
    * `int getNorth()` deprecated in favour of `SpaceUtil.Size getTop()`
    * `int getEast()` deprecated in favour of `SpaceUtil.Size getRight()`
    * `int getSouth()` deprecated in favour of `SpaceUtil.Size getBottom()`
    * `int getWest()` deprecated in favour of `SpaceUtil.Size getLeft()`
  * `BorderLayout`, `ColumnLayout`, `GridLayout`:
    * `int getHgap()` deprecated in favour of `SpaceUtil.Size getHorizontalGap()`
    * `int getVgap()` deprecated in favour of `SpaceUtil.Size getVerticalGap()`
  * `FlowLayout`, `ListLayout`, `WList`, `WRow`:
    * `int getGap()` deprecated in favour of `SpaceUtil.Size getSpace()`

### Bug Fixes

* `WMessages.isHidden()` will return `true` if it has no messages #1082.
* Fixed a bug in `com.github.bordertech.wcomponents.test.selenium.ByLabel` which resulted in WLabels for compound inputs
  being ignored when finding elements.
* Fixed HTML error in `wcomponents/lde` resource `DevToolkit_header.vm` found during optimisations for #1012.
* Fixed a significant network performance problem caused by loading JavaScript modules included in the layer #1068.
  * Fixed all imports between the `wc/dom` and `wc/ui` namespaces so that no dom-level modules require anything in the
  `wc/ui` space;
  * removed the circular dependency between `formUpdateManager` and `cancelUpdate`;
  * removed most other (managed) circular dependencies; and
  * improved reuse of Widgets.
* Fixed a bug which could cause a (caught) exception when a WTree's item was selected if the WTree has client expansion
  and was not an ajax trigger (found during testing for PR #1086).
* Removed yellow fade as it caused potential a11y problems #1104.

### Enhancements

* Made debug modes consistent between client and server code #1012.
* Renderer updates as part of #639:
  * `WAbbrTextRender` changed from XML to HTML as a performance improvement, no UI or functional change.
  * `WDataTableRenderer` no longer renders any internal disabled states with no UI change as these were ignored.
  * `WFieldErrorIndicatorRenderer` will not render with no UI change as these were ignored.
  * `WLabelRenderer` is aware of the nature and state of its labeled component and is able to correctly set the state of
     a `WLabel` in the UI without cross-reference to its component. This was required to fix several possible errors
     caused when a `WLabel` was in an AJAX response.
  * `WMenuItemRenderer` and `WSubMenuRenderer` will only output the `accessKey` member if the component is at the top
    level of its `WMenu` with no UI change as `accessKey` settings on other instances were ignored.
  * `WTabGroupRenderer` will no longer paint `WTabGroup` as `ui:tabgroup` but pass through to its `WTabs` with no UI
    change as these were ignored.
* Selenium test elements improved and extended.
* Converted any XSLT calculation which relied on cross-element lookups to use javascript or improved Java renderers (as
  described above) to prevent errors when the referenced element was not available in an ajax response #639 et al.
* Changed the require config `baseUrl` to make it shorter as this makes adding application level modules easier. The new
  BaseUrl only uses path settings which are also available in Java.
* Added new JavaScript module `wc/ui/getForm` as we have several areas where we need to get the form from a particular
  element.
* Update XSLT build to improve performance and protect component integrity #689.

## Release 1.3.4

### Bug Fixes

* Fixed imageeditor issues:
  * overlay confuses image validation #1048.
  * disallow save when no image has been captured from video stream #1062.
* fix phantom vertical scroll in some browsers (QC154504) #1073.
* Fixed JS loading issue #1068.
* Fixed layout of WCheckBoxSelect/WRadioButtonSelect with LAYOUT_COLUMN and COLUMN_COUNT >= options.

## Release 1.3.3

### Bug Fixes

* Fixed bug in able actions which could result in action buttons being in the incorrect (enabled) state if the table
  is loaded via Ajax #1049.
* Fixed a bug in modalShim which resulted in access keys being stripped from inside WDialog content #1051 (QC 162189).
* Fixed an XSLT issue which could result in double-output of some HTML elements embedded in WComponents XML #1056
  (QC 162143)
* Remove exposure of the `size` attribute when rendering `WNumberField` #1010.
* Fixed bug which caused WMultiFileWidget to not fire its internal file-select Ajax if the component is in a read-only
  state #1060.
* Fixed bug which could result in WMultiSelectPair having mismatched select elements #1066.
* Fixed a typographic error in the XSLT for WLink.

### Enhancements
* Improve efficiency of `Input` renderers when the rendered input is in a read-only state #781.

## Release 1.3.2

### Bug Fixes

* Fixed a bug in XSLT which caused processor errors if an instance of Input has more than one validation error #1042.
* Fixed a bug in subordinate JavaScript which caused WMultiSelectPair to fail as a subordinate trigger #1044.

### Enhancements
* Reduced logging "noise" when the corrupt characters flag is enabled #1039

## Release 1.3.1

### API Changes

* Deprecated xslt server side property. Will always return true.

## Release 1.3.0

### API Changes

* 'UIContext' interface has been changed to include a new request scope scratch map #192.

### Bug Fixes

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

### Enhancements

* Output component name hint for Inputs in a read-only state to improve Selenium testing #1029.
* Improve performance by not calling unnecessary preparePaint in DataListInterceptor #975.
* Improve performance and type-safety of XSLT.
* `WBeanComponent` has been changed that when a `BeanProvider` is being used it can now use the new request scope
  scratch map to hold the bean. To opt into this functionality set the runtime parameter
  `bordertech.wcomponents.bean.provider.request.scope.enabled` to true #192.
* ServletResponse changed to provide access to the backing httpServletResponse #803.

## Release 1.2.15

### Enhancements
* Allow handlebars templates to be rendered on the server to complete theme i18n.
* Backport interceptor chain fix #993.
* Fix loading shim "stuck" on IE8

## Release 1.2.14

### Enhancements
* Remove corrupt characters before processing the XSLT #980. Runtime parameter needs to be set to true:-
      bordertech.wcomponents.xslt.allow.corrupt.characters

## Release 1.2.13

### Bug Fixes

* Fixed a bug which could cause dialogs to be mis-positioned on mobiles #977.

## Release 1.2.12

### Bug Fixes

* Fixed issues which could cause resize and positioning errors in WDialog #958.
* Fixed WDialog that was not handling its open state correctly #963 QC160323.
* Fixed issues which could cause a WDialog to prevent refocus of the opener control when the dialog was closed #965.
* Fixed an issue which could result in a dialog opener to _not_ open its dialog if the opener contained an image #967.
* Fixed an issue which resulted in WShuffler outputting the incorrect HTML element when in read-only mode #972.
* Fixed an issue which could result in the content of a table header overlaying the sort icon and/or the sort icon being mis-positioned when the header content wrapped #973.

### Enhancements

* New DialogOpenTrigger interface to identify components that can open a dialog. WDialog can also have an action
  set via setTriggerOpenAction(action) to run when the dialog is opened via a trigger #963.

## Release 1.2.11

### Bug Fixes

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

### Enhancements
* Allow TinyMCE global configuration to be overriden without a theme rebuild.

## Release 1.2.9

### Bug Fixes

* Fixed table row selection/dialog bug #929.

## Release 1.2.8

### Bug Fixes

* Mitigated a11y issues in WTable (#926).

### Enhancements

* Added a workaround for a CSS bug in IE11 (#924).

## Release 1.2.7

### API Changes

* Deprecated the following WPanel Types (part of #689, #639):
  * Type.ACTION: deprecated in favour of WSection to remove API ambiguity.
  * Type.BANNER: deprecated in favour of WSection as never implemented but JavaDoc points to WSection equivalence
  * Type.BLOCK: deprecated in favour of Type.PLAIN and Margin as no longer serves any useful purpose
  * Type.CHROME: deprecated in favour of WSection to remove API ambiguity.

### Bug Fixes

* Applied a workaround for buggy implementation of placeholders in textareas in IE11 #911.
* Fixed bug in WSuggestion where user text was not updated after first use (QC158630).
* Fixed i18n race condition causing placeholder, message title (all theme messages) not to display in some browsers (QC158400).
* Reverted date field to previous behavior of attempting to honor invalid input value after round trip (QC157989).

### Enhancements

* Added CSS support for an additional class `wc-neg-margin` on WMenu Type.BAR to force the "docking" style if it is not possible to make the menubar the first child of a WPanel with no layout #916.
* Updated JavaScript Widgets used to determine WDateField and WPartialDateField to remove some potential access errors when either of these components is in a read-only state #907.

## Release 1.2.6

### Bug Fixes

* QC157496 - Fixed a null pointer exception within WDateField resetData()

### Enhancements

* Enhanced TransformXMLInterceptor to support non-HTML agents.
* Minor performance turning for TransformXMLInterceptor.
* Dependency management improvement for Saxon so that it does not need to be the default XSLT engine.

## Release 1.2.5

### API Changes

* Added a new ServiceLoader API to inject custom configuration into WComponents using the interface ConfigurationLoader.

### Bug Fixes

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

### Enhancements

* Added a mechanism to add and remove multiple HTML class attribute values to a component #856.
* Added a mechanism to mark a WButton as having an action only in the client #878.
* Plugged a minor potential vulnerability in WLink #895.
* Added a lot of Sass configuration options to `_common.scss` along with additional commentary #689.
* Projects can override a new method `getUiVersionKey()` in `WApplication` if different versions of the same Application
  need to be registered by `UIRegistry` #894.
* New constructors in `WPartialDateField` to allow a padding character to be passed in #573.
* Provide access to the backing `HttpServletRequest` in `ServletRequest` and a new helper method in `ServletUtil` to
  determine the user's device #803.

## Release 1.2.4

### Bug Fixes

* Fixed bug in XSLT of inline errors #847
* Fixed bug in i18n of timeout warnings #846

## Release 1.2.3

### API Changes

* Javascript API i18n module now returns the message key instead of an empty string if the translation is not found.
* Deprecated `WDialog(WComponent, WButton)` as part of #407. Replaced with use of other constructors along with new
  accessors `setTrigger(AjaxTrigger)` and `AjaxTrigger getTrigger()`.

### Bug Fixes

* Fixed bug which could result in dialogs being mis-positioned #805.
* Fixed a11y of combo boxes #808, #809.
* Fixed a bug in which the loading shim was removed before the page was ready to use #822.
* Fixed bugs in `WTextArea` in RichText mode #825.
* Fixed bugs which prevented correct output of `WLabel`, `WAbbrText` and `WHeading` if content escaping was turned off.
* Fixed a bug in `ComponentModel` triggered when data is `java.util.Stack` #838.
* FIxed XSLT bug which could result in a `WFigure` losing its figcaption under some circumstances #841.

### Enhancements

* JavaScript API added a utility module to centralize determination of toggle points for responsive UI updates.
* Rewrote the JavaScript i18n module so that it is a thin wrapper around [i18next](http://i18next.com/) instead of
  custom code.
* `WDialog` may be launched by any component which implements `AjaxTrigger` #407.
* Updated client implementation of `WSuggestions`/combo boxes to improve accessibility #808, #809.
* Moved i18n to JavaScript and improved load-time reliability #819, #732, #639, #689.

## Release 1.2.2

### Bug Fixes

* Fixed a bug in `com.github.bordertech.wcomponents.subordinate.AbstractCompare` which resulted in Subordinate controls
  returning an incorrect value if the control was in a read-only state #780.
* Fixed a newly introduced bug which caused textareas to fail to accept newlines in IE11 #785.
* Fixed several IE CSS issues.
* Fixed a bug which caused the incorrect HTML className to be set using HtmlIconUtils to place a custom icon "AFTER"
  element content.

### Enhancements

* Added mechanism to convert tabsets to accordions on small screens #783.
* Allow placeholder to be set on relevant components #702.
* Added new component WToggleButton which renders a single checkable component in a button form. This may be used as a
  WSubordinateControl trigger or a WAjaxControl trigger #428.

## Release 1.2.1

### API Changes

* The new (in 1.2.0) class `HtmlClassUtil` has been refactored to a properties enum and is now `HtmlClassProperties`
  (same package). If you have already started using `HtmlClassUtil.HtmlClassName` should be replaced with
  `HtmlClassProperties`.
* The Seleniun API has been rewritten to better support client-side testing of WComponents. see
  https://github.com/BorderTech/wcomponents/wiki/Testing for more information.

### Bug Fixes

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

### Enhancements

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

## Release 1.2.0

### API Changes

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

### Bug Fixes

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

### Enhancements

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

## Release 1.1.8

### Bug fixes

* Table constraints enforced #647
  [Issues](https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.8)

## Release 1.1.7

### Bug fixes

* Table pagination select mis-calculation #641
* Fix multiformcomponent plus icon #644
  [Issues]([https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.7)

## Release 1.1.6

### Bug fixes

* #626, #629, #631, #634, #635
  [Issues](https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.6)

## Release 1.1.5

### Bug fixes

* #610 #611 #612 #613 #616
  [Issues](https://github.com/BorderTech/wcomponents/issues?q=milestone%3A1.1.5)

## Release 1.1.4

### Bug fixes

* #562 #567 #574 #576 #577 #579 #583 #586 #588 #590 #592 #593 #601
  [Issues](https://github.com/BorderTech/wcomponents/issues?utf8=✓&q=is%3Aissue+milestone%3A1.1.4+)

## Release 1.1.3

### Bug fixes

* #542 #543 #546 #548 #549 #552 #554 #556

## Release 1.1.2

### Bug fixes

* #517 #521 #523 #525 #526 #527 #528 #529 #530 #531 #532 #533 #534

## Release 1.1.1

### API Changes

* WAudio and WVideo Controls.ALL, and Controls.DEFAULT have been **deprecated** as they no have no impact on the
  controls (#503);
* WAudio and WVideo Controls.NONE have been **deprecated** as they are not compatible with WCAG requirements that media
  be able to be turned off. These were always incompatible with autoplay to prevent a11y failure. This means that any
  media component with Controls.NONE has never been able to work and **will never** be able to work (#503).

### Enhancements

* Re-instated client side support for disabled state in WAudio and WVideo but this only applies when Controls.PLAY_PAUSE
  is used as there is no native disabled support in audio or video elements. The play/pause button **will not** be
  disabled if autoplay is set as this causes an a11y failure.

### Major Bug fixes

* Worked around an issue which prevented Sass from compiling on Linux with glibc below v13 and a related issue which
  prevented Sass from compiling correctly on Windows (#494).

## Release 1.1.0

### API Changes

These are API changes and enhancements which _may_ have some impact on current users.

* `WFilterControl` has been removed. This WComponent was originally designed as a low-quality row filter for `WDataTable`. It has been deprecated for some time. WDataTable is deprecated and slated for _imminent_ removal (though not in the next major release) (#306).
* Handle multi part AJAX requests. Tightened up the com.github.bordertech.wcomponents.Request interface for the return type of getParameters and getFiles (#341).

#### New components

* Created new WTree component which is to be used for vertical and horizontal trees (#263).
* Created a new WTemplate component that supports velocity, handlebars and plain text. Projects can also implement their own templating engines (#94).

### Enhancements
For full details see [closed enhancements](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Aenhancement).

* Added a new property to WSuggestions: WSuggestions.Autocomplete. This is an enum which currently supports two settings `LIST` and `BOTH` but has the potential to support all settings of the HTML attribute [aria-autocomplete](https://www.w3.org/TR/wai-aria/states_and_properties#aria-autocomplete) if required. This has the usual accessors get/setAutocomplete. The default is WSuggestions.Autocomplete.BOTH which provides the same functionality as in previous versions of WSuggestions. When WSuggestions.Autocomplete.LIST is used then the client implementation will attempt to force selection of an option from the suggestions list rather than allowing free-text input (#379).
* Make server side transform disabled by default and do not do a server side transform if a theme content path has been set (#377).
* Improved mobile/responsive aspects of client rendering (#344).
* Simplified access to client code, especially CSS (#172).

### Major Bug fixes

For bug fixes in this release see [closed bugs 2016 Q1](https://github.com/BorderTech/wcomponents/issues?utf8=✓&q=is%3Aissue+milestone%3A%222016+Q1%22+is%3Aclosed+label%3Abug).
For all bug fixes see [closed bugs](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)

* Fix WTree AJAX actions. Introduced new AjaxInternalTrigger marker interface to indicate which components use AJAX to maintain their internal state (#447).
* Fix WTab in a WTabGroup causing a ClassCastException (#442).
* Fix WTable to allow setting row headers (#384).
* Fix an issue in WTable pagination where a table with many pages could cause some browsers to fail to render a view (#409).
* Fixed a bug which caused an AJAX trigger to fail when the trigger was inside a WDialog and the target was outside the WDialog (#457).

### Other

* The schema has been updated to make all UI element names lowercase. Why is this not an API change? because the schema is not part of the API - it is an internal contract.

## Release 1.0.4

### Major Bug fixes

For all bug fixes see [closed bugs](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)

## Release 1.0.3

### API Changes

These are API changes and enhancements which _may_ have some impact on current users.

* `WDataTable.PaginationMode.SERVER` has been modified to implement `WDataTable.PaginationMode.DYNAMIC` to overcome an a11y problem inherent in `WDataTable.PagainationMode.SERVER`. Whilst `WDataTable` is deprecated it is being retained (for the foreseeable future) for backwards compatibility and therefore must meet a11y requirements. `WDataTable.PaginationMode.SERVER` has been individually deprecated and **will be removed** in a near future release.
* `WSubMenu` child count has changed. This was necessary to allow correct AJAX loading of sub-menus (#250). Applications should use `getMenuItems` instead of `getChildAt`.

#### JavaScript API changes

* Client-side support of `WFilterControl` has been removed. This applied _only_ to (the deprecated) `WDataTable` but caused significant overhead during XSLT processing of all tables. The old code is available if a custom theme requires it.

### Enhancements

These are backwards-compatible API changes which are transparent for all current users. For full details see [closed enhancements](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Aenhancement).

* `WTable` API allows for setting the location of the pagination controls using `setPaginationLocation(WTable.PaginationLocation location)` (#297).
* `WTable` API allows for sub-row group selection (select all/select none) when a table has multiple row selection **and** row expansion enabled. The optional extra functionality allows for a select all/none control for every row which has one or more selectable sub-rows (#257).
* `WMessageBox` API allows for setting message box title string using `setTitleText(String title)` (#280).
* `WApplication` API allows for custom `js` and `css` resources to be easily loaded (#172).
* `AbstractWComponent` API allows for custom HTML class attribute values to be added to any WComponent for improved application-level customisation (#172).
* `WColumn` and `ColumnLayout` may now be created without specifying a width. The column widths _should_ then be specified in [application level CSS](https://github.com/BorderTech/wcomponents/wiki/Adding-custom-CSS). This is primarily aimed at applications which require responsive design (#172, #180).
* Added improved guards against clickjacking (#240).

### Major Bug fixes

For all bug fixes see [closed bugs](https://github.com/BorderTech/wcomponents/issues?q=is%3Aissue+is%3Aclosed+label%3Abug)

* Fixed loading `WSubMenu` via AJAX. Items can now be dynamically added when the `WSubMenu` is opened (#250). **NOTE** Any application using `WSubMenu.getChildAt` **must** now use `WSubMenu.getMenuItems` as the child count _will_ be different.
* Fixed WTabSet ACCORDION interactions (#277).
* Fixed `WShuffler` to work correctly when used as an `AjaxTrigger` (#323).
* Fixed AJAX-enabled, drag & drop, multi-file upload in Firefox (#245).

## Release 1.0.2

### API Changes

* Updated `WAbbrText`, `WAbbrTextRenderer` and associated tests (#157)
    * Removed `WAbbrText.AbbrTextModel` This custom component model is completely superfluous. The refactoring of `WAbbrText` brings the component into line with other WComponents and standardises API calls. **NOTE** If you have extended this model (well stop it you will go blind) you will now have to implement your own component model.
    * Deprecated `WAbbrText.getAbbrText` in favour of `WAbbrText.getToolTip` (inherited from `AbstractWComponent`) and `WAbbrText.setAbbrText` in favour of `WAbbrText.setToolTIp` (inherited from `AbstractWComponent`).
    * Updated schema and XSLT for ui:abbr to change attribute `description` to `toolTip` in line with other components.
* Method scope issue in `WCardManager`. Removed `removeAll(flag)` and `remove(child, flag)`. (#59)
* `WebUtilities.updateBeanValue(component)` changed to ignore invisible components by default. `WebUtilities.updateBeanValue(component, flag)` added to provide this option. (#46)
* Removed deprecated com.github.bordertech.wcomponents.test.util.TreeUtil. Use com.github.bordertech.wcomponents.util.TreeUtil instead.
* Removed deprecated `com.github.bordertech.wcomponents.test.util.WComponentTreeVisitor`. Use `com.github.bordertech.wcomponents.util.WComponentTreeVisitor` instead.

### Enhancements

* `WMenu` API allows for `getItems(boolean recursive)` (#262).
* `WTabSet` implements `SubordinateTarget` (#159).
* `WTab` implements `SubordinateTarget` (#158).

### Major Bug fixes

* `AbstractWComponentTestCase` now works with variable arguments on setter methods (#188)
