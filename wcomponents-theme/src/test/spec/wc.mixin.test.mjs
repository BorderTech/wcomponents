import mixin from "wc/mixin.mjs";

describe("wc/mixin", function() {
	it("mixes in a simple object", () => {
		const source = {
				foo: "bar",
				sheep: {
					species: "ovine",
					noise: "baa"
				}
			},
			target = {
				foo: "kung",
				num: 7,
				sheep: {
					noise: "maaaa",
					flavour: "delicious"
				}
			},
			expected = {
				foo: "bar",
				num: 7,
				sheep: {
					species: "ovine",
					noise: "baa",
					flavour: "delicious"
				}
			},
			actual = mixin(source, target);
		expect(actual).toEqual(expected);
	});

	it("can handle a missing target", () => {
		const source = {
				foo: "bar",
				num: 7,
				sheep: {
					species: "ovine",
					noise: "baa",
					flavour: "delicious"
				}
			},
			actual = mixin(source);
		expect(actual).toEqual(source);
	});

	it("doesn't return source when there is no target", () => {
		const source = {
				foo: "bar",
				num: 7,
				sheep: {
					species: "ovine",
					noise: "baa",
					flavour: "delicious"
				}
			},
			actual = mixin(source);
		expect(actual).not.toBe(source);
	});

	it("returns target unharmed when there is no source", () => {
		const target = {
				foo: "bar",
				num: 7,
				sheep: {
					species: "ovine",
					noise: "baa",
					flavour: "delicious"
				}
			},
			actual = mixin(null, target);
		expect(actual).toBe(target);
	});

	it("returns something when there is no source and no target", () => {
		expect(mixin() instanceof Object).toBeTrue();
	});

	it("Returns an empty object when there is no source and no target", () => {
		expect(mixin()).toEqual({});
	});

	it("Honors the shallow mixin flag", () => {
		const source = {
				foo: "bar",
				sheep: {
					species: "ovine",
					flavour: "delicious"
				}
			},
			target = {
				foo: "kung",
				num: 7,
				sheep: {
					noise: "maaaa"
				}
			},
			expected = {
				foo: "bar",
				sheep: {
					species: "ovine",
					flavour: "delicious"
				},
				num: 7
			};
		expect(mixin(source, target, true)).toEqual(expected);
	});
});
