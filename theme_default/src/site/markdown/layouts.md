## Theme layout components

One of the most fundamental aspects of UI development is layout. This is also one which is often difficult, especially
for JAVA developers who may be unfamiliar with web technologies and common HTML layout rules and paradigms.

The following notes are the assumptions around transforming and styling layout components. This is most important if you
are changing the transform or style of [WPanel][]. The [WPanel][] type property (`@type` in XSLT and passed through as a
class name in the default XSLT) has some remarks in the JavaDoc which application developers will use to determine which
panel type to use. You should conform with these notes when creating your theme.

### General remarks regarding width settings

Wherever possible we use percentage widths. This allows a UI to remain in a reasonable state of alignment over many
viewport sizes. We also suggest applying a min-width to the body element for intranet/extranet applications when the
range of common viewport sizes is known (or at least able to be guessed at.

Some layout components support horizontal spacing between containers using a hgap attribute. This attribute allows any
non-negative integer. We have used several means to display these in the past and have finally gone back to inline
styles with the hgap as a margin or padding (depending on the display rule) expressed in pixels. This is one area where
your implementation can make a totally different choice if you are willing to get your hands dirty in XSLT.

### WPanel

[WPanel][] is the most fundamental layout component. It is also one which has most flexibility and the most variations
in its transformed output. In some cases even the top level HTML element of the component changes according to the
[WPanel][] type attribute or the child layout element. A [WPanel][] should always be styled as a block box. Unless
otherwise stated the default transform for [WPanel][] will output a DIV element with the id of the [WPanel][] and a
class including the string value of the [WPanel's][WPanel] type attribute.

#### WPanel types with specific layout notes in JavaDoc

The following are remarks based on the JavaDoc for WPanel.Type. The Type is output into the XML as the `@type` attribute.

<dl>
 	<dt>Type BLOCK</dt>
 	<dd>This panel should have padding one all sides to removed the content from the edge of the panel. This panel type
 	is frequently used by Java developers when they need space around a block of components. The actual spacing can be
 	implementation specific.</dd>

 	<dt>Type BOX</dt>
 	<dd>This panel should have a border. It can be assumed that any element with a border will have padding to remove the content from the border.</dd>

 	<dt>Type FEATURE</dt>
 	<dd>This panel should have a background color and a border. It can be assumed that any element with a border will have padding to remove the content from the border.</dd>

 	<dt>Type CHROME</dt>
 	<dd>This panel type should have a visible title set by the title attribute. The panel of this type should attempt to be hierarchy aware. The title element is not specified and the description of the panel indicates that it is decorative. The default transform outputs a SPAN element. It is possible that we may change this to a H1 element and change the panel's root element to a SECTION element as this panel type is most frequently used to build up important sections of a UI.</dd>

 	<dt>Type HEADER</dt>
 	<dd>This panel type produces a HEADER element and WAR-ARIA landmark role of 'banner'. The JavaDoc indicates that this panel type will create a header/banner and should only be used once per page. This is a guideline and cannot be enforced.</dd>

 	<dt>Type FOOTER</dt>
 	<dd>This panel type produces a FOOTER element.The JavaDoc indicates that this panel type will create a footer and should only be used once per screen in an appropriate place to make the page footer block. This is a guideline and cannot be enforced.</dd>

 	<dt>Type ACTION</dt>
 	<dd>This panel type should have a header as per CHROME. The JavaDoc indicates that it is not hierarchy aware and can therefore be placed in any part of the UI. It also recommends that it only be used once per screen. The main difference between CHROME and ACTION from the styling perspective is that you may	create styles for nested CHROME panels (eg .chrome .chrome\{\}) but should not do so for ACTION panels.</dd>
</dl>

#### WPanel child elements

The following elements are all allowed child elements of ui:panel in the schema:

* [ui:content](./components/wc.ui.content.html)
* [ui:borderLayout](./components/wc.ui.borderLayout.html)
* [ui:columnLayout](./components/wc.ui.columnLayout.html)
* [ui:flowLayout](./components/wc.ui.flowLayout.html)
* [ui:gridLayout](./components/wc.ui.gridLayout.html)
* [ui:listLayout](./components/wc.ui.listLayout.html)

### WFieldLayout

[WFieldLayout][] is a component which is principally designed for laying out groups of interactive form controls such as
text fields and the labels associated with these controls.

The transform and CSS for [WFieldLayout][] is relatively straightforward but both get complex when we come to the child 
elements. This is especially the case for [WField's][WField] CSS which has to allow for [WFieldLayout's][WFieldLayout] 
labelWidth attribute, [WField's][WField] inputWidth attribute and the need to move and style legends and legend proxies 
as if they were labels.

There is a separate document about this. **TODO: COMPLETE THIS**

### WRow and WColumn

[WRow][] is a holder for columns and is merely a DIV. [WColumn][WRow] produces the columns and these columns have width and alignment. Column width is set as a percentage of available width. The column width can be any non-negative integer so we output them as inline styles.

We provide hgap on [WRow][] allowing the columns in that row to be spaced apart.

### WCollapsible

[WCollapsible](./components/wc.ui.collapsible.html) is a layout component with a heading element which acts as a trigger to expand and collapse the content. For this we use a DETAILS element and the heading is the SUMMARY element.

This has some implications for developers as the content model of a SUMMARY is phrasing content which limits the range of components which can be placed in the heading component but this limitation is not able to be enforced in the WComponents API.

At the time of writing webkit based browsers have native support for the DETAILS element but it is weak. Collapse/expand is only triggered by mouse click and cannot be driven from the keyboard. We capture this in JavaScript and add the required key events to make the component accessible in all browsers.

### WTable

A [WTable][] should always be considered a layout component for its data. The table CSS is relatively straightforward but the transform is a little complex due to the range of options.

#### Table types

WComponents supports two rendering models for [WTable][]. These are output in the XML as the type attribute and are: table (or empty) or hierarchic. The difference is in how sub-rows are displayed. When the table type is hierarchic the sub-rows are indented with respect to their parent, otherwise they align with their parent. 

### WTabset

[WTabset][] is a layout component with many independent areas of content. Tabs may be arranged above the content, to the
left or right of the content or as an accordion interleaved with the content. In all cases except accordion only one 
tab's content may be visible at a time. The accordion is able to have 0..n tabs open.

The style and behaviour of a [WTabset][] and its tabs are bound to their role attribute and associated aria-* attributes. The
exception to this is for display of right and left tabset with the tabs along the side of the content. The functionality and main styling is still reliant upon the
roles and attributes but there is no attribute which can be used to set the tabs beside the content.

<!-- references -->
[WPanel]: ./components/wc.ui.panel.html "WPanel documentation"
[WFieldLayout]: ./components/wc.ui.fieldLayout.html "WFieldLayout documentation"
[WTabset]: ./components/wc.ui.tabset.html "WTabset documentation"
[WTable]: ./components/wc.ui.table.html "WTable documentation"
[WField]: ./components/wc.ui.field.html "WField documentation"
[WRow]: ./components/wc.ui.row.html "WRow and WColumn documentation"