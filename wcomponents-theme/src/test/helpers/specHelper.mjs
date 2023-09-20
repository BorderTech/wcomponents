import "global-jsdom/register";
import fs from "fs";
import {getResoucePath} from "./specUtils.mjs";

let rdf;
function fudgeDimensions() {
	// Allows you to set style on an element and have it report an offset dimension
	Object.defineProperties(window.HTMLElement.prototype, {
		offsetLeft: {
			get () { return parseFloat(this.style.marginLeft) || 0 }
		},
		offsetTop: {
			get () { return parseFloat(this.style.marginTop) || 0 }
		},
		offsetHeight: {
			get () { return parseFloat(this.style.height) || 0 }
		},
		offsetWidth: {
			get () { return parseFloat(this.style.width) || 0 }
		}
	});
}

function mockAriaRdf() {
	// @ts-ignore
	return import("jasmine-ajax").then(() => {
		jasmine.Ajax.install();
		jasmine.Ajax.stubRequest(/.*\/aria-1.rdf/).andReturn({
			status: 200,
			statusText: 'HTTP/1.1 200 OK',
			contentType: 'text/xml;charset=UTF-8',
			get responseText() {
				if (!rdf) {
					const rdfPath = getResoucePath("aria-1.rdf", true);
					console.log("Mock response with:", rdfPath);
					rdf = fs.readFileSync(rdfPath, "utf8");
				}
				return rdf;
			}
		});
	});
}

function mocki18n() {
	return import("wc/i18n/i18n.mjs").then(mod => {
		const i18n = mod.default;
		const url = new URL(import.meta.url);
		if (url.protocol === "file:") {
			return import("i18next-fs-backend").then(({ default: Backend }) => {
				return i18n.initialize({
					backend: Backend,
					options: {
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
}

beforeAll(() => {
	fudgeDimensions();
	window["getJasmineRequireObj"] = global.getJasmineRequireObj = () => jasmine;  // some plugins need this, like jasmine-ajax
	return Promise.all([mocki18n(), mockAriaRdf()]);

});

beforeEach(() => {
	globalThis.document.documentElement.lang = "en";
});
