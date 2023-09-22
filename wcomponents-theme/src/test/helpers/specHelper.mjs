import "global-jsdom/register";
import jsdom from "global-jsdom";
import fs from "fs";
import {getResoucePath, fudgeDimensions} from "./specUtils.mjs";

const cache = {};
const reset = jsdom(null, { url: "http://localhost" });

function mockAjax() {
	const translationRe = /(translation\/.+json)/;

	// @ts-ignore
	return import("jasmine-ajax").then(() => {
		jasmine.Ajax.install();
		jasmine.Ajax.stubRequest(/.*\/aria-1.rdf/).andReturn({
			status: 200,
			statusText: 'HTTP/1.1 200 OK',
			contentType: 'text/xml;charset=UTF-8',
			get responseText() {
				if (!cache["rdf"]) {
					const rdfPath = getResoucePath("aria-1.rdf", true);
					console.log("Mock response with:", rdfPath);
					cache["rdf"] = fs.readFileSync(rdfPath, "utf8");
				}
				return cache["rdf"];
			}
		});

		jasmine.Ajax.stubRequest(translationRe).andReturn({
			status: 200,
			statusText: 'HTTP/1.1 200 OK',
			contentType: 'text/json;charset=UTF-8',
			get responseText() {
				const request = jasmine.Ajax.requests.mostRecent();
				const match = RegExp(translationRe).exec(request.url);
				if (match) {
					const subPath = match[1];
					if (!cache[subPath]) {
						const resourcePath = getResoucePath(subPath, false);
						console.log("Mock response with:", resourcePath);
						cache[subPath] = fs.readFileSync(resourcePath, "utf8");
					}
					return cache[subPath];
				}
				return "";
			}
		});
	});
}

beforeAll(() => {
	fudgeDimensions(window);
	window["getJasmineRequireObj"] = global.getJasmineRequireObj = () => jasmine;  // some plugins need this, like jasmine-ajax
	return mockAjax().then(() => {
		return import("wc/i18n/i18n.mjs").then(({default: i18n}) => {
			return i18n.translate('');
		});
	});
});

afterAll(() => {
	reset();
});

beforeEach(() => {
	globalThis.document.documentElement.lang = "en";
});
