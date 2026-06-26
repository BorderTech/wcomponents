import cookie from "wc/dom/cookie.mjs";

const { beforeEach, describe, expect, it } = globalThis;

describe("wc/dom/cookie", function() {
	const testProp = "foobar" + Date.now(),
		testVal = "barFoo";

	beforeEach(function() {
		cookie.erase(testProp);
		if (cookie.read(testProp)) throw new Error("Something wrong");
	});

	it("testPutGet", function() {
		cookie.create(testProp, testVal, 10);

		expect(cookie.read(testProp)).toBe(testVal);
	});

	it("testPutGetErase", function() {
		cookie.create(testProp, testVal, 10);
		cookie.erase(testProp);

		expect(cookie.read(testProp)).toBeFalsy();
	});

	it("testPutGetSession", function() {
		cookie.create(testProp, testVal);

		expect(cookie.read(testProp)).toBe(testVal);
	});

	it("testPutGetEraseSession", function() {
		cookie.create(testProp, testVal);
		cookie.erase(testProp);

		expect(cookie.read(testProp)).toBeFalsy();
	});
});
