# Common Sass source

These files comprise Sass which generates CSS which is used by more than one type of WComponent. The
partials are added from `../_common.scss`. The CSS in these files is _mostly_ structural so one
probably doesn't want to override or exclude them.

The files are broken up into common units so if one **knows** that the common CSS will never be
used the import can be removed. This can be dangerous.

Sass for individual WComponents is found in the source folder `../components/`.
