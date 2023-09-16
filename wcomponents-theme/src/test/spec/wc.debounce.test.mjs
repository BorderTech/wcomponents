import debounce from "wc/debounce.mjs";

describe("wc/debounce", () => {
	let testObj;
	const delay = 5;

	beforeEach(() => {
		testObj = {
			setTestVal: function(value) {
				this.testVal += value;
			},
			testVal: 0
		};
		testObj["setTestValDebounced"] = debounce(testObj.setTestVal, delay);
	});

	it("Calls the underlying function only once, passing through args and 'this'", () => {
		return new Promise(win => {
			const total = 20;
			for (let i = 0; i < total; i++) {
				testObj.setTestValDebounced(i);
			}
			setTimeout(() => {
				expect(testObj.testVal).toBe(total - 1);
				win();
				}, delay * 2);
		});
	});
});
