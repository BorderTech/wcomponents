import "global-jsdom/register";

beforeAll(() => {
		return import("wc/i18n/i18n.mjs").then(mod => {
			const i18n = mod.default;
			const url = new URL(import.meta.url);
			if (url.protocol === "file:") {
				return import("i18next-fs-backend").then(({ default: Backend }) => {
					return i18n.initialize({
						backend: Backend,
						options: {
							fallbackLng: "en",
							backend: {
								loadPath: 'src/test/resource/translation/{{lng}}.json'
							}
						}
					}).then(() => {
						return i18n.translate('');
					});
				});
			}
			return i18n.translate('');
		});
});

beforeEach(() => {
	globalThis.document.documentElement.lang = "en";
});
