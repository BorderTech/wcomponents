require(["lib/i18next", "wc/array/diff", "wc/timers"], function(i18next, arrayDiff, timers) {

	var timer, checked = {};

	i18next.on("loaded", onload);
	queueCheck();

	function onload() {
		queueCheck();
	}


	function checkLoaded(languages) {
		var i, nextLang, langs;
		try {
			langs = languages || i18next.languages;
			if (langs) {
				for (i = 0; i < langs.length; i++) {
					nextLang = langs[i];
					if (nextLang && !checked.hasOwnProperty(nextLang)) {
						checked[nextLang] = true;
						checkMissing(nextLang);
					}
				}
			}
		} catch (ex) {
			console.warn(ex);  // don't die checking missing translations and other debug fluff
		}
	}

	function queueCheck() {
		if (timer) {
			timers.clearTimeout(timer);
		}
		timer = timers.setTimeout(checkLoaded, 1337);  // this does not need to happen in a hurry
	}

	/**
	 * Check for missing translations in this language's resource bundle.
	 * @param {string} lang
	 */
	function checkMissing(lang) {
		var bundle = i18next.getResourceBundle(lang),
			missingKeys, bundleKeys, defaultKeys, fallbackLang, defaultBundle;
		if (bundle && i18next.options) {
			fallbackLang = i18next.options.fallbackLng;
			if (fallbackLang && fallbackLang !== lang) {
				defaultBundle = i18next.getResourceBundle(fallbackLang);
				if (defaultBundle) {
					defaultKeys = Object.keys(defaultBundle);
					bundleKeys = Object.keys(bundle);
					missingKeys = arrayDiff(defaultKeys, bundleKeys);
					if (missingKeys.length) {
						handleMissing(missingKeys, lang);
					}
				}
			}
		}
	}

	/*
	 * TODO display this in a more prominent manner.
	 * We need a debug manager utility where I can just call something like debugManager.displayWarning("Foo is bar");
	 */
	function handleMissing(missingKeys, lang) {
		console.warn("Missing translations ", lang, missingKeys.join());
	}
});
