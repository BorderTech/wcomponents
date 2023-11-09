import "global-jsdom/register";
import jsdom from "global-jsdom";
import fs from "fs";
import path from "path";
import {getResoucePath, fudgeDimensions} from "./specUtils.mjs";
import JasmineDOM from "@testing-library/jasmine-dom";

const cache = {};
const reset = jsdom(null, { url: "http://localhost" });

function mockAjax() {
	const translationRe = /(translation\/.+json)/;

	// @ts-ignore
	return import("jasmine-ajax").then(() => {
		jasmine.Ajax.install();

		jasmine.Ajax.stubRequest(/.*\/aria-1.rdf/).andReturn(getResponse("aria-1.rdf", true));
		jasmine.Ajax.stubRequest(/.*\/note.xml.*/).andReturn(getResponse("note.xml"));
		jasmine.Ajax.stubRequest(/.*\/note.txt.*/).andReturn(getResponse("note.txt"));
		jasmine.Ajax.stubRequest(/.*\/icao.html.*/).andReturn(getResponse("icao.html"));

		jasmine.Ajax.stubRequest(translationRe).andReturn({
			status: 200,
			statusText: "HTTP/1.1 200 OK",
			contentType: getMimeType("foo.json"),
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

function getResponse(fileName, srcDir = false) {
	return {
		status: 200,
			statusText: "HTTP/1.1 200 OK",
		contentType: getMimeType(fileName),
		get responseText() {
		const key = fileName;
		if (!cache[key]) {
			const resourcePath = getResoucePath(fileName, srcDir);
			console.log("Mock response with:", resourcePath);
			cache[key] = fs.readFileSync(resourcePath, "utf8");
		}
		return cache[key];
	}
	}
}

function getMimeType(fileName) {
	const map = {
		txt: "text/plain",
		json: "text/json",
		xml: "text/xml",
		html: "text/html",
		rdf: "text/xml;charset=UTF-8"
	};
	const ext = path.extname(fileName).replace(/^\./, "");
	return map[ext];
}

beforeAll(() => {
	jasmine.addMatchers(JasmineDOM);
	fudgeDimensions(window);
	window["getJasmineRequireObj"] = global.getJasmineRequireObj = () => jasmine;  // some plugins need this, like jasmine-ajax
	return mockAjax().then(() => {
		return import("wc/i18n/i18n.mjs").then(({default: i18n}) => {
			return i18n.translate("");
		});
	});
});

afterAll(() => {
	reset();
});

beforeEach(() => {
	globalThis.document.documentElement.lang = "en";
});
