## WComponent Accessibility

WComponents aims to be able to create accessible rich Internet applications which meet
[WCAG 2.0](http://www.w3.org/TR/WCAG20/) level AA accessibility guidelines and implement the guidelines of
[WAI-ARIA](http://www.w3.org/TR/wai-aria/). We take a very serious approach to accessibility and have worked hard to
make each individual component as accessible as possible. This does not mean that all applications built with
WComponents will be automatically WCAG 2.0 AA compliant. It is perfectly possible to create applications which do not
meet even level A compliance, but it does mean Java developers start with a fighting chance.

### What is accessibility?

Accessibility is a catch all descriptor for techniques of UI design and development which maximises the ability for all
users to use an application. It uses a set of guidelines and techniques to reduce the barriers to using an application
and is for everyone, not specifically those with a disability. It is a fundamental facet of good computer human
interaction technique.

Accessibility of web based applications is mandated in many jurisdictions. In Australia web accessibility has been
tested in the courts and deemed to be required under the _Disability Discrimination Act 1992_ and possibly other
legislation. In addition equality of access to information, systems and the scientific, social and cultural life of the
community is basic human right covered by article 27 of the
[Universal Declaration of Human Rights](http://www.un.org/en/documents/udhr/).

Accessibility is not just about disability and is certainly not merely a set of check-list items covering visual
impairment. Accessible interfaces help everyone be more productive and improve the users' experience of your
applications.

### What is WCAG?

The World Wide Web Consortium's (W3C) [Web Content Accessibility Guidelines](http://www.w3.org/TR/WCAG20/) (WCAG) are
a set of guidelines for maximising accessibility of web sites and applications. These guidelines are used as a basis
for determining accessibility for many statutory obligations, including those in Australia.

### How does this effect you?

#### Java application developers

WComponents is used to create web applications. In Australia all content delivered over a web interface, even Intranet
applications, are covered by these requirements. As a JAVA developer the accessibility requirements may be somewhat
removed from you. WComponents tries to reduce the need for a detailed knowledge of web development techniques and
requirements but cannot completely divorce you from making your applications accessible.

#### Front-end/theme developers

All front end code must result in a component which can be deemed to be accessible at
[WCAG 2.0](http://www.w3.org/TR/WCAG20/) level AA. Any new components or changes to existing components must be able to
demonstrate that they *do not* trigger *any* failure techniques and can be shown to be in accordance with the sufficient
techniques for level AA.

This means (but is not limited to):

* all rendered HTML *must* be in accordance with the specification (by default we use HTML5 and the _only_ specification
is that of the [WHATWG](https://html.spec.whatwg.org/));
* all components must be operable without a mouse/pointing device and keyboard mappings should comply with WAI-ARIA
authoring practices where available;
* all informative content must be able to be correctly interpreted by a screen reader and braille display;
* all animations must be able to be stopped by the user if they repeat more than three times; and
* all timed events must provide a mechanism by which the user may extend the time allowed for completion in accordance
with the limitations explicit in the WCAG 2.0 guidelines.
