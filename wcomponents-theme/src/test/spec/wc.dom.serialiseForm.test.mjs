import serialize from "wc/dom/serialize.mjs";
import {getSelect, setUpExternalHTML} from "../helpers/specUtils.mjs";

describe("wc/dom/serialize", () => {
	let ownerDocument;
	const TEMP_CONTAINER_ID = "tempContainerId",
		STRING_EXPECTED = "T3=%C2%A9%0AZ&H1=x&H2=&PWD=&T1=&T2=YES&My%20Name=me&S1=abc&S2=abc&S2=abc&S3=YES&S4=",
		SERIALIZED_OBJ_EXPECTED = {
			T3: ["%C2%A9%0AZ"],
			H1: ["x"],
			H2: [""],
			PWD: [""],
			T1: [""],
			T2: ["YES"],
			"My%20Name": ["me"],
			S1: ["abc"],
			S2: ["abc", "abc"],
			S3: ["YES"],
			S4: [""]
		},  // note:the expected result has empty string values rather than null values because they are conversions of serialised FORM elements so the value of an empty INPUT (for example) is "" not null
		DESERIALIZED_OBJ_EXPECTED = {
			T3: [decodeURIComponent("%C2%A9%0AZ")],
			H1: ["x"],
			H2: [""],
			PWD: [""],
			T1: [""],
			T2: ["YES"],
			"My Name": ["me"],
			S1: ["abc"],
			S2: ["abc", "abc"],
			S3: ["YES"],
			S4: [""]
		},
		INPUTS = "input[type='hidden']";

	beforeAll(() => {
		return setUpExternalHTML("domSerialiseForm.html").then(dom => {
			ownerDocument = dom.window.document;
		});
	});


	/**
	 * Tests our library methods for serialising HTML forms
	 * InFORMed by here: http://people.n0i.net/altblue/webbie/form-serialization/
	 */
	it("testSerialiseForm", function() {
		const form = ownerDocument.getElementById("testForm");
		let element = getSelect(ownerDocument.body, "S5");

		element.selectedIndex = -1;
		element = getSelect(ownerDocument.body, "S2");
		element.options[1].selected = true;
		element.options[2].selected = true;
		let result = serialize.serialize(form);
		expect(result).withContext("Form should serialise to this ").toEqual(STRING_EXPECTED);  // objectEqual
	});

	it("testSerialiseFormAsObject", function() {
		const form = ownerDocument.getElementById("testForm");
		let element = getSelect(ownerDocument.body, "S5");

		element.selectedIndex = -1;
		element = getSelect(ownerDocument.body, "S2");
		element.options[1].selected = true;
		element.options[2].selected = true;
		let result = serialize.serialize(form, null, true);
		expect(result).withContext("Form should serialise to this ").toEqual(SERIALIZED_OBJ_EXPECTED);  // objectEqual
	});

	it("testSerialiseFormAsObjectReverse", function() {
		const form = ownerDocument.getElementById("testForm");
		let element = getSelect(ownerDocument.body, "S5");

		element.selectedIndex = -1;
		element = getSelect(ownerDocument.body, "S2");
		element.options[1].selected = true;
		element.options[2].selected = true;
		let result = /** @type Object */(serialize.serialize(form, null, true));
		// result = result.replace("%0D%0A", "%0A");  // replace \n\r with \n so browsers behave the same

		for (let prop in result) {
			for (let i = 0; i < result[prop].length; ++i) {
				result[prop][i] = (result[prop][i]).replace("%0D%0A", "%0A");
			}
		}

		result = serializedObjectToString(result);
		expect(result).withContext("Form should serialise to this ").toEqual(STRING_EXPECTED);  // objectEqual
	});

	it("testDeserializeStringGotResults", function() {
		const result = setupDeserializer(STRING_EXPECTED);
		expect(result["actual"].childNodes.length).withContext("deserialize should have got some childNodes in the actual result container").toBeGreaterThan(0);
	});

	it("testDeserializeReturnValueWithObject", function() {
		const result = serialize.deserialize(SERIALIZED_OBJ_EXPECTED);
		expect(DESERIALIZED_OBJ_EXPECTED).withContext("Deserialize return value should equal this object ").toEqual(result);
	});

	it("testDeserializeReturnValueWithPriorDeserializedObject", function() {
		const result = serialize.deserialize(DESERIALIZED_OBJ_EXPECTED);
		expect(DESERIALIZED_OBJ_EXPECTED).withContext("Deserialize return value should equal this object ").toEqual(result);
	});

	it("testDeserializeStringGotExpectedNumberOfResults", function() {
		const result = setupDeserializer(STRING_EXPECTED);
		expect(result["actual"].childNodes.length).withContext("Number of expected results should match number of actual results").toBe(result["expected"].childNodes.length);
	});

	it("testDeserializeStringFoundAnExpectedResult", function() {
		const result = setupDeserializer(STRING_EXPECTED),
			expectedInputs = result["expected"].querySelectorAll(INPUTS);

		Array.prototype.forEach.call(expectedInputs, _compare);
		function _compare(next) {
			const inpArray = findInputsLikeThis(next, result["actual"]);
			expect(inpArray.length).withContext("Should have found a hidden input with name " + next.name + " and value " + next.value).toBeGreaterThan(0);
		}
	});

	it("testDeserializeStringGotExpectedResults", function() {
		const result = setupDeserializer(STRING_EXPECTED),
			expectedInputs = result["expected"].querySelectorAll(INPUTS);

		Array.prototype.forEach.call(expectedInputs, _compare);

		function _compare(next) {
			const expectedArray = findInputsLikeThis(next, result["expected"]),
				inpArray = findInputsLikeThis(next, result["actual"]);
			expect(inpArray.length).withContext("Should have found same number of hidden inputs with name " + next.name + " and value " + next.value).toBe(expectedArray.length);
		}
	});

	it("testDeserializeObjectgGotResults", function() {
		const result = setupDeserializer(SERIALIZED_OBJ_EXPECTED);
		expect(result["actual"].childNodes.length).withContext("deserialize object should have got some childNodes in the actual result container").toBeGreaterThan(0);
	});

	it("testDeserializeObjectGotExpectedNumberOfResults", function() {
		const result = setupDeserializer(SERIALIZED_OBJ_EXPECTED);
		expect(result["actual"].childNodes.length).withContext("Number of expected results should match number of actual results").toBe(result["expected"].childNodes.length);
	});

	it("testDeserializeObjectFoundAnExpectedResult", function() {
		const result = setupDeserializer(SERIALIZED_OBJ_EXPECTED),
			expectedInputs = result["expected"].querySelectorAll(INPUTS);

		Array.prototype.forEach.call(expectedInputs, _compare);

		function _compare(next) {
			const inpArray = findInputsLikeThis(next, result["actual"]);
			expect(inpArray.length).withContext("Should have found a hidden input with name " + next.name + " and value " + next.value).toBeGreaterThan(0);
		}
	});

	it("testDeserializeObjectGotExpectedResults", function() {
		const result = setupDeserializer(SERIALIZED_OBJ_EXPECTED),
			expectedInputs = result["expected"].querySelectorAll(INPUTS);

		Array.prototype.forEach.call(expectedInputs, _compare);

		function _compare(next) {
			const expectedArray = findInputsLikeThis(next, result["expected"]),
				inpArray = findInputsLikeThis(next, result["actual"]);
			expect(inpArray.length).withContext("Should have found same number of hidden inputs with name " + next.name + " and value " + next.value).toBe(expectedArray.length);
		}
	});

	it("testDeserializeserializeDeserialize", function() {
		const tempContainer = makeTempContainer();
		serialize.deserialize(STRING_EXPECTED, tempContainer);
		let result = serialize.serialize(tempContainer.querySelectorAll(INPUTS));
		expect(result).withContext("Reserializing a deserialized string should get back to the same string").toBe(STRING_EXPECTED);
	});

	it("testDeserializeserializeDeserializeWithObject", function() {
		const tempContainer = makeTempContainer();
		serialize.deserialize(SERIALIZED_OBJ_EXPECTED, tempContainer);
		const result = /** @type Object */(serialize.serialize(tempContainer.querySelectorAll(INPUTS), false, true));
		expect(SERIALIZED_OBJ_EXPECTED).withContext("Reserializing a deserialized object should get back to the same object").toEqual(result);
	});

	it("testDeserializeserializeDeserializeWithStringToObject", function() {
		const tempContainer = makeTempContainer();
		serialize.deserialize(STRING_EXPECTED, tempContainer);
		const result = /** @type Object */(
			serialize.serialize(tempContainer.querySelectorAll(INPUTS), false, true));
		expect(SERIALIZED_OBJ_EXPECTED).withContext("Reserializing a deserialized string to an object should get back to the object").toEqual(result);
	});

	it("testDeserializeserializeDeserializeWithObjectToString", function() {
		const tempContainer = makeTempContainer();
		serialize.deserialize(SERIALIZED_OBJ_EXPECTED, tempContainer);
		const result = serialize.serialize(tempContainer.querySelectorAll(INPUTS));
		expect(result).withContext("Reserializing a deserialized object to a string should get back to the string").toBe(STRING_EXPECTED);
	});

	it("testAreDifferent", function() {
		const obj1 = { abc: ["1", "2", "3"] },
			obj2 = { abc: ["1", "2", "3"] };
		expect(serialize.areDifferent(obj1, obj2)).toBeFalse();
	});

	it("testAreDifferentWithOrderDifference", function() {
		const obj1 = {abc: ["1", "2", "3"]},
			obj2 = {abc: ["3", "2", "1"]};
		expect(serialize.areDifferent(obj1, obj2)).toBeFalse();
	});

	it("testAreDifferentWithDifference", function() {
		const obj1 = {abc: ["1", "2", "3"]},
			obj2 = {abc: ["1", "2", "4"]};
		expect(serialize.areDifferent(obj1, obj2)).toBeTrue();
	});

	it("testAreDifferentWithAdditionalField", function() {
		const obj1 = {abc: ["1", "2", "3"]},
			obj2 = {abc: ["1", "2", "3"], def: ["1", "2", "3"]};
		expect(serialize.areDifferent(obj1, obj2)).toBeTrue();
	});

	function setupDeserializer(input) {
		const form = ownerDocument.getElementById("testForm");
		let element = getSelect(ownerDocument.body, "S5"),
			expectedResult = makeHiddenFields(input),
			result = {}, serializedForm, tempContainer;
		element.selectedIndex = -1;
		element = getSelect(ownerDocument.body, "S2");
		element.options[1].selected = true;
		element.options[2].selected = true;
		if (input.constructor === String) {
			serializedForm = serialize.serialize(form);
		} else {
			serializedForm = serialize.serialize(form, false, true);
		}

		// up to this point has been tested with
		tempContainer = makeTempContainer();
		serialize.deserialize(serializedForm, tempContainer);
		result["serializedForm"] = serializedForm;
		result["expected"] = expectedResult;
		result["actual"] = tempContainer;
		return result;
	}

	/*
	 * reverse serialized object tester to test the serialized object tester
	 */
	function serializedObjectToString(obj) {
		let result = "";
		for (let prop in obj) {
			result += arrayReplacer(prop, obj[prop]);
		}
		return result.substring(0, result.length - 1);
	}

	function arrayReplacer(key, value) {
		let result = "";
		if (Array.isArray(value)) {
			for (let i = 0; i < value.length; ++i) {
				let next = value[i];
				result += (key.toString() + ((next !== null) ? ("=" + next.toString()) : "") + "&");
			}
		} else {
			result = key.toString() + ((value !== null) ? ("=" + value.toString()) : "") + "&";
		}
		return result;
	}

	function makeTempContainer() {
		const result = ownerDocument.createElement("div");
		result.id = TEMP_CONTAINER_ID;
		// ownerDocument.getElementById("testForm").appendChild(result);
		return result;
	}

	function makeHiddenFields(input) {
		const wrapper = ownerDocument.createElement("div");
		function _makeField(_name, _value) {
			const field = ownerDocument.createElement("input");
			field.type = "hidden";
			field.name = decodeURIComponent(_name);
			field.value = decodeURIComponent(_value);
			wrapper.appendChild(field);
		}

		if (input.constructor === String) {
			input = input.split("&");
			for (let i = 0; i < input.length; ++i) {
				let n = input[i].split("=");
				_makeField(n[0], (n[1] || ""));
			}
		} else {
			for (let n in input) {
				for (let i = 0; i < input[n].length; ++i) {
					_makeField(n, input[n][i]);
				}
			}
		}

		return wrapper;
	}

	function findInputsLikeThis(input, container) {
		return Array.from(container.querySelectorAll(INPUTS)).filter(next => {
			return next.name === input.name && next.value === input.value;
		});
	}
});
