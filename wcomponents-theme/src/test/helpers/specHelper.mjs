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

		jasmine.Ajax.stubRequest(/.*\/aria-1.rdf/).andReturn(getResponse("aria-1.rdf", { srcDir: true }));
		jasmine.Ajax.stubRequest(/.*\/note.xml.*/).andReturn(getResponse("note.xml", {}));
		jasmine.Ajax.stubRequest(/.*\/note.txt.*/).andReturn(getResponse("note.txt", {}));
		jasmine.Ajax.stubRequest(/.*\/icao.html.*/).andReturn(getResponse("icao.html", {}));

		jasmine.Ajax.stubRequest(translationRe).andReturn({
			status: 200,
			statusText: getStatusText(200),
			contentType: getMimeType("foo.json"),
			get responseText() {
				const request = jasmine.Ajax.requests.mostRecent();
				const match = RegExp(translationRe).exec(request.url);
				if (match) {
					const subPath = match[1];
					return getResponseText(subPath, false);
				}
				return "";
			}
		});
	});
}

function getResponse(fileName, { srcDir = false, status = 200 }) {
	return {
		status,
		statusText: getStatusText(status),
		contentType: getMimeType(fileName),
		get responseText() {
			return getResponseText(fileName, srcDir);
		}
	};
}

function getResponseText(fileName, srcDir) {
	const key = fileName;
	if (!cache[key]) {
		const resourcePath = getResoucePath(fileName, srcDir);
		console.log("Mock response with:", resourcePath);
		try {
			cache[key] = fs.readFileSync(resourcePath, "utf8");
		} catch (ex) {
			return ex.message;
		}
	}
	return cache[key];
}

function getStatusText(status) {
	const map = {
		200: "HTTP/1.1 200 OK",
		404: "Not Found",
		407: "Proxy Authentication Required",
		418: "I'm a teapot",
		500: "Internal Server Error"
	};
	return map[status];
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
