import i18next from "i18next";
import arrayDiff from "wc/array/diff.mjs";
import debounce from "wc/debounce.mjs";

const checked = {};
const queueCheck = debounce(languages => {
	try {
		const langs = languages || i18next.languages;
		if (langs) {
			for (let i = 0; i < langs.length; i++) {
				let nextLang = langs[i];
				if (nextLang && !checked.hasOwnProperty(nextLang)) {
					checked[nextLang] = true;
					checkMissing(nextLang);
				}
			}
		}
	} catch (ex) {
		console.warn(ex);  // don't die checking missing translations and other debug fluff
	}
}, 1337);  // this does not need to happen in a hurry

i18next.on("loaded", () => queueCheck());
queueCheck();

/**
 * Check for missing translations in this language's resource bundle.
 * @param {string} lang
 */
function checkMissing(lang) {
	const bundle = i18next.getResourceBundle(lang);
	if (bundle && i18next.options) {
		const fallbackLang = i18next.options.fallbackLng;
		if (fallbackLang && fallbackLang !== lang) {
			const defaultBundle = i18next.getResourceBundle(fallbackLang);
			if (defaultBundle) {
				const defaultKeys = Object.keys(defaultBundle);
				const bundleKeys = Object.keys(bundle);
				const missingKeys = arrayDiff(defaultKeys, bundleKeys);
				if (missingKeys.length) {
					handleMissing(missingKeys, lang);
				}
			}
		}
	}
}

/**
 * TODO display this in a more prominent manner.
 * We need a debug manager utility where I can just call something like debugManager.displayWarning("Foo is bar");
 * @param {string[]} missingKeys
 * @param {string} lang
 */
function handleMissing(missingKeys, lang) {
	console.warn("Missing translations ", lang, missingKeys.join());
}
