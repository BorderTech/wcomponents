## Layout information for application developers

Many HTML elements have intrinsic layout. It is not the purpose of this document to describe these. You should, however, be familiar with the HTML elements which are generated from the transform of the WComponents XML, which is described in the documentation for each component, and the layout, flow and nesting requirements and limitations of each of these.

Documentation of HTML5 can be found at [The WHATWG web site](http://www.whatwg.org/specs/web-apps/current-work/).

There are a group of WComponents which are fundamentally used solely to create layout. These may be necessary but are often overused or used in ways and combinations which are less than optimal. The native (or implementation) layout of some components mean that it may be more appropriate to place them directly into an application or parent component rather than wrap them in a layout component, this is especially true of the most used layout	component: [WPanel](./components/wc.ui.panel.html).

> entia non sunt multiplicanda praeter necessitatem  
_[William of Ockham](http://en.wikipedia.org/wiki/William_of_Ockham)_

The fundamental rule for all web applications is to make them as light as possible by using as few components as necessary and no more. This Occam's Razor approach will serve you well in making efficient, usable interfaces.

Each of the layout components outlined below have instrinsic layout and occupy a block of space in the UI. They will usually fill the horizontal space available to them. The only exception to this is [WColumn](./components/wc.ui.row.html) and the columns of a [ColumnLayout](./components/wc.ui.columnLayout.html). This means that you will not usually have to add any of these to another layout component unless you need horizontal flow or the spacing which some supply.

### WPanel

[WPanel](./components/wc.ui.panel.html) is the most fundamental layout component. It is also one which has most
flexibility and the most variations in its transformed output. In some cases even
the top level HTML element of the component changes according to the [WPanel](./components/wc.ui.panel.html) type
or the child layout element. A [WPanel](./components/wc.ui.panel.html) is always a block: it will fill the
available horizontal space.

#### WPanel types with specific layout implications

Some [WPanel](./components/wc.ui.panel.html) Types have specific layout implications though the details are
implementation specific. These are documented in the JavaDoc. These Types are:

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

### WFieldLayout

[WFieldLayout](./components/wc.ui.fieldLayout.html) is a component which is principally designed for laying out groups of interactive form controls such as text fields and the labels associated with these controls.

### WRow, WColumn and ColumnLayout

[WRow](./components/wc.ui.row.html) and [ColumnLayout](./components/wc.ui.columnLayout.html) produce a row of columns. [ColumnLayout](./components/wc.ui.columnLayout.html) provides a shorthand mechanism to implement a repeated set of [WRow/WColumn](./components/wc.ui.row.html) functionality along with the ability to space rows using vgap.

[WColumn](./components/wc.ui.row.html) creates columns. These columns have width and alignment. Column width is set as a percentage and is a guide. Please see the [WColumn](./components/wc.ui.row.html) documentation as this is entirely theme dependent and gets complicated very quickly.

[WRow](./components/wc.ui.row.html) provides a means to space the columns in the row using hgap.

### ListLayout

[ListLayout](./components/wc.ui.listLayout.html) will create a HTML list (unordered or ordered). It has settings
to make the list flow horizontally or vertically and be LEFT, CENTER or
RIGHT aligned.

### BorderLayout, FlowLayout, GridLayout

These layouts are approximate emulations of Swing layouts. [BorderLayout](./components/wc.ui.borderLayout.html), [GridLayout](./components/wc.ui.gridLayout.html)
and [FlowLayout](./components/wc.ui.flowLayout.html) support VGAP and HGAP so that the vertical and horizontal spacing
of the layout can be customised. [FlowLayout](./components/wc.ui.flowLayout.html) supports setting the vertical alignment
of the cells in the flow to top (default), middle, baseline	or bottom.

### WCollapsible

[WCollapsible](./components/wc.ui.collapsible.html) is, from a layout perspective, merely a special kind of content
container. It has a heading which also functions to show and hide the content of the component. It can be considered
interchangeable with [WPanel](./components/wc.ui.panel.html) in respect of being a container for content, but a panel
which is able to shrink to only its heading.

### WTable

A [WTable](./components/wc.ui.table.html) should always be considered a layout component for its data.

### WTabSet

A [WTabSet](./components/wc.ui.tabset.html) is a layout component with many independent areas of content. Tabs may be arranged above the content, to the
left or right of the content or as an accordion interleaved with the content. In all cases except accordion only one
tab's content may be visible at a time. The accordion is able to have 0..n tabs open.

### WContainer

WContainer is not a layout component per-se as it has no XML output of its own. This lack of output makes it ideal for
grouping other components which do not need to be referenced as a group.
