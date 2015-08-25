## Design guidelines for layout

Layouts will always be determined by screen design and one would hope that a specification would include layout
determinants. All too frequently this is not the case. Given the broad range of layout components there is never just
one way to create a layout.

When deciding on components to use one ought aim to minimise the number of components used and their nesting. Every
component used will increase the bandwidth requirements of your application and reduce its performance in the browser.
We have successfully implemented applications with over 27000 DOM nodes in the final transformed page, but pages with
this weight cause significant performance issues in some browsers.

The following are notes and guidance rather than hard and fast rules. They are based on experience with web UIs and WComponents, each layout should be approached
on its own merits.

### Screen level layout

When laying out a complete screen, or the base classes for applications with
multiple similar screens, one should first look at the core layout. In many cases
the layout will be column based.

We think of a UI as a stack of components on top of each other like pieces of
paper. The place to start is the bottom of the stack and build up to the details.

Only after determining the correct base layers should one move onto laying out
the details of the page. Again for each section of content treat the section as
a set of layers and consider the simplest possible layout for each section.

Please see [the walk-through example](./examples/threeColUIExample.html) showing
how we approach this layering technique.

### Form control layout

Wherever possible form controls should be laid out in a WFieldLayout. This will
provide optimum usability and accessibility. The labels will be positioned
according to common accessibility practices and the layout will have a
consistency and coherence difficult to obtain with other layouts.

... **TO BE CONINUED**...

## Common scenarios

There are a small number of scenarios which have recurred in many of the
applications using WComponents. Whilst we cannot provide a one size fits all
solution to any layout issue, these scenarios may help.

### Examples of layouts

* [Methods for vertical layouts](./examples/layout_vertical.html)
* [Methods for horizontal layouts](./examples/layout_horizontal.html)
* [GridBag layout emulation](./examples/layout_gridbag.html)
* [Layouts of forms compared](./examples/layout_forms.html)
* [Layout of sub forms](./examples/layout_subforms.html)
* [Adding multiple inline controls to WField](./examples/layout_field_multipleFormControls.html)
* [Date range compound controls](./examples/compound_controls_date.html)
