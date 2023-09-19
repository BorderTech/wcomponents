import cookie from "wc/dom/cookie.mjs";

describe("wc/dom/cookie", function() {
	const testProp = "foobar" + (new Date()).getTime(),
		testVal = "barFoo";

	beforeEach(function() {
		cookie.erase(testProp);
		expect(cookie.read(testProp)).toBeFalsy();  // Tests should start in clean state
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
