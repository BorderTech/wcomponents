# wcomponents internationalization

This module contains default (English) bundles for WComponents internal messages.

There are two bundles, one for messages that are stored and used on the server and another for messages that are delivered to the client (i.e. the user's browser).

## Other Languages and Locales
We may accept translations for other languages and locales. This does not mean that we will **maintain** translations.

We only maintain the default English messages!!!

Please provide your own translations locally OR contribute new translations here in a pull request (see [Contributing](https://github.com/BorderTech/wcomponents/wiki/Contributing)).
Translation formats are explained here [Theme i18n](https://github.com/BorderTech/wcomponents/wiki/Theme-i18n) and here [i18n](https://github.com/BorderTech/wcomponents/wiki/i18n).

## How to Update or Create a Translation

### Using pot files
Professional translators often use tools compatible with [gettext](https://www.gnu.org/software/gettext/manual/gettext.html) pot files, e.g. [Poedit](https://poedit.net/).

These are found in the directory `dist/pot` and should be provided to the translator.

The translator will return a `.po.` or `.mo` file which developers can use to generate resource bundles in the correct format (see [Further reading](#further-reading)).

## Information for Developers
`dist/pot` contains pot files which are automatically generated on every WComponents build (see `dist/README.txt`). They allow you to provide messages to a translator in a format they can consume.

`src/main/resources` contains the actual message bundles used by WComponents at runtime.

`src/main/resources/com/github/bordertech/wcomponents` contains the MAINTAINED and framework provided default message bundles.

`src/main/resources/*` any other subdirectories contain message bundles for other locales which have been contributed and are not maintained by the WComponents team.

## Further reading
* [Theme i18n](https://github.com/BorderTech/wcomponents/wiki/Theme-i18n)
* [i18n](https://github.com/BorderTech/wcomponents/wiki/i18n)
