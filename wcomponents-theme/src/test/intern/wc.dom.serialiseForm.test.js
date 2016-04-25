define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var serialize, Widget, testHolder,
			urlResource = "@RESOURCES@/domSerialiseForm.html",
			TEMP_CONTAINER_ID = "tempContainerId",
			STRING_EXPECTED = "T3=%C2%A9%0AZ&H1=x&H2=&PWD=&T1=&T2=YES&My%20Name=me&S1=abc&S2=abc&S2=abc&S3=YES&S4=",
			SERIALIZED_OBJ_EXPECTED = { T3: ["%C2%A9%0AZ"],
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
			INPUTS;

		registerSuite({
			name: "wc/dom/serialize",
			setup: function() {
				var result = testutils.setupHelper(["wc/dom/serialize", "wc/dom/Widget"]).then(function(arr) {
					serialize = arr[0];
					Widget = arr[1];
					testHolder = testutils.getTestHolder();
					INPUTS = new Widget("INPUT", "", { type: "hidden" });
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			/**
			 * Tests our library methods for serialising HTML forms
			 * InFORMed by here: http://people.n0i.net/altblue/webbie/form-serialization/
			 */
			testSerialiseForm: function() {
				var form = document.getElementById("testForm"),
					element = document.getElementById("S5"),
					result;

				element.selectedIndex = -1;
				element = document.getElementById("S2");
				element.options[1].selected = true;
				element.options[2].selected = true;
				result = serialize.serialize(form);
				result = fixIESerialized(result);
				assert.isTrue(testutils.objectEqual(STRING_EXPECTED, result), "Form should serialise to this ");
			},
			testSerialiseFormAsObject: function() {
				var form = document.getElementById("testForm"),
					element = document.getElementById("S5"),
					result;

				element.selectedIndex = -1;
				element = document.getElementById("S2");
				element.options[1].selected = true;
				element.options[2].selected = true;
				result = serialize.serialize(form, null, true);
				result = fixIESerialized(result);
				assert.isTrue(testutils.objectEqual(SERIALIZED_OBJ_EXPECTED, result), "Form should serialise to this ");
			},
			testSerialiseFormAsObjectReverse: function() {
				var form = document.getElementById("testForm"),
					element = document.getElementById("S5"),
					result;

				element.selectedIndex = -1;
				element = document.getElementById("S2");
				element.options[1].selected = true;
				element.options[2].selected = true;
				result = serialize.serialize(form, null, true);
				// result = result.replace("%0D%0A", "%0A");  // replace \n\r with \n so browsers behave the same

				for (var prop in result) {
					for (var i = 0; i < result[prop].length; ++i) {
						result[prop][i] = (result[prop][i]).replace("%0D%0A", "%0A");
					}
				}

				result = serializedObjectToString(result);
				assert.isTrue(testutils.objectEqual(STRING_EXPECTED, result), "Form should serialise to this ");
			},
			testDeserializeStringGotResults: function() {
				var result = setupDeserializer(STRING_EXPECTED);
				assert.isTrue(result["actual"].childNodes.length > 0, "deserialize should have got some childNodes in the actual result container");
			},
			testDeserializeReturnValueWithObject: function() {
				var result = serialize.deserialize(SERIALIZED_OBJ_EXPECTED);
				assert.isTrue(testutils.objectEqual(DESERIALIZED_OBJ_EXPECTED, result), "Deserialize return value should equal this object ");
			},
			testDeserializeReturnValueWithPriorDeserializedObject: function() {
				var result = serialize.deserialize(DESERIALIZED_OBJ_EXPECTED);
				assert.isTrue(testutils.objectEqual(DESERIALIZED_OBJ_EXPECTED, result), "Deserialize return value should equal this object ");
			},
			testDeserializeStringGotExpectedNumberOfResults: function() {
				var result = setupDeserializer(STRING_EXPECTED);
				assert.strictEqual(result["expected"].childNodes.length, result["actual"].childNodes.length, "Number of expected results should match number of actual results");
			},
			testDeserializeStringFoundAnExpectedResult: function() {
				var result = setupDeserializer(STRING_EXPECTED),
					expectedInputs = INPUTS.findDescendants(result["expected"]);

				Array.prototype.forEach.call(expectedInputs, _compare);
				function _compare(next) {
					var inpArray = findInputsLikeThis(next, result["actual"]);
					assert.isTrue(inpArray.length > 0, "Should have found a hidden input with name " + next.name + " and value " + next.value);
				}
			},
			testDeserializeStringGotExpectedResults: function() {
				var result = setupDeserializer(STRING_EXPECTED),
					expectedInputs = INPUTS.findDescendants(result["expected"]);

				Array.prototype.forEach.call(expectedInputs, _compare);

				function _compare(next) {
					var expectedArray = findInputsLikeThis(next, result["expected"]),
						inpArray = findInputsLikeThis(next, result["actual"]);
					assert.strictEqual(expectedArray.length, inpArray.length, "Should have found same number of hidden inputs with name " + next.name + " and value " + next.value);
				}
			},
			testDeserializeObjectgGotResults: function() {
				var result = setupDeserializer(SERIALIZED_OBJ_EXPECTED);
				assert.isTrue(result["actual"].childNodes.length > 0, "deserialize object should have got some childNodes in the actual result container");
			},
			testDeserializeObjectGotExpectedNumberOfResults: function() {
				var result = setupDeserializer(SERIALIZED_OBJ_EXPECTED);
				assert.strictEqual(result["expected"].childNodes.length, result["actual"].childNodes.length, "Number of expected results should match number of actual results");
			},
			testDeserializeObjectFoundAnExpectedResult: function() {
				var result = setupDeserializer(SERIALIZED_OBJ_EXPECTED),
					expectedInputs = INPUTS.findDescendants(result["expected"]);

				Array.prototype.forEach.call(expectedInputs, _compare);

				function _compare(next) {
					var inpArray = findInputsLikeThis(next, result["actual"]);
					assert.isTrue(inpArray.length > 0, "Should have found a hidden input with name " + next.name + " and value " + next.value);
				}
			},
			testDeserializeObjectGotExpectedResults: function() {
				var result = setupDeserializer(SERIALIZED_OBJ_EXPECTED),
					expectedInputs = INPUTS.findDescendants(result["expected"]);

				Array.prototype.forEach.call(expectedInputs, _compare);

				function _compare(next) {
					var expectedArray = findInputsLikeThis(next, result["expected"]),
						inpArray = findInputsLikeThis(next, result["actual"]);
					assert.strictEqual(expectedArray.length, inpArray.length, "Should have found same number of hidden inputs with name " + next.name + " and value " + next.value);
				}
			},
			testDeserializeserializeDeserialize: function() {
				var tempContainer = makeTempContainer(), result;
				serialize.deserialize(STRING_EXPECTED, tempContainer);
				result = serialize.serialize(INPUTS.findDescendants(tempContainer));
				result = fixIESerialized(result);
				assert.strictEqual(STRING_EXPECTED, result, "Reserializing a deserialized string should get back to the same string");
			},
			testDeserializeserializeDeserializeWithObject: function() {
				var tempContainer = makeTempContainer(), result;
				serialize.deserialize(SERIALIZED_OBJ_EXPECTED, tempContainer);
				result = serialize.serialize(INPUTS.findDescendants(tempContainer), false, true);
				result = fixIESerialized(result);
				assert.isTrue(testutils.objectEqual(SERIALIZED_OBJ_EXPECTED, result), "Reserializing a deserialized object should get back to the same object");
			},
			testDeserializeserializeDeserializeWithStringToObject: function() {
				var tempContainer = makeTempContainer(), result;
				serialize.deserialize(STRING_EXPECTED, tempContainer);
				result = serialize.serialize(INPUTS.findDescendants(tempContainer), false, true);
				result = fixIESerialized(result);
				assert.isTrue(testutils.objectEqual(SERIALIZED_OBJ_EXPECTED, result), "Reserializing a deserialized string to an object should get back to the object");
			},
			testDeserializeserializeDeserializeWithObjectToString: function() {
				var tempContainer = makeTempContainer(), result;
				serialize.deserialize(SERIALIZED_OBJ_EXPECTED, tempContainer);
				result = serialize.serialize(INPUTS.findDescendants(tempContainer));
				result = fixIESerialized(result);
				assert.strictEqual(STRING_EXPECTED, result, "Reserializing a deserialized object to a string should get back to the string");
			},
			testAreDifferent: function() {
				var obj1 = {abc: [1, 2, 3]},
					obj2 = {abc: [1, 2, 3]};
				assert.isFalse(serialize.areDifferent(obj1, obj2));
			},
			testAreDifferentWithOrderDifference: function() {
				var obj1 = {abc: [1, 2, 3]},
					obj2 = {abc: [3, 2, 1]};
				assert.isFalse(serialize.areDifferent(obj1, obj2));
			},
			testAreDifferentWithDifference: function() {
				var obj1 = {abc: [1, 2, 3]},
					obj2 = {abc: [1, 2, 4]};
				assert.isTrue(serialize.areDifferent(obj1, obj2));
			},
			testAreDifferentWithAdditionalField: function() {
				var obj1 = {abc: [1, 2, 3]},
					obj2 = {abc: [1, 2, 3], def: [1, 2, 3]};
				assert.isTrue(serialize.areDifferent(obj1, obj2));
			}
		});


		function fixIESerialized(input) {
			var result = input, prop, i;
			if (input.constructor === String) {
				result = result.replace("%0D%0A", "%0A");  // replace \n\r with \n so browsers behave the same
			}
			else {
				for (prop in result) {
					for (i = 0; i < result[prop].length; ++i) {
						result[prop][i] = (result[prop][i]).replace("%0D%0A", "%0A");
					}
				}
			}
			return result;
		}

		function setupDeserializer(input) {
			var form = document.getElementById("testForm"),
				element = document.getElementById("S5"),
				expectedResult = makeHiddenFields(input),
				result = {}, serializedForm, tempContainer;
			element.selectedIndex = -1;
			element = document.getElementById("S2");
			element.options[1].selected = true;
			element.options[2].selected = true;
			if (input.constructor === String) {
				serializedForm = serialize.serialize(form);
			}
			else {
				serializedForm = serialize.serialize(form, false, true);
			}
			serializedForm = fixIESerialized(serializedForm);

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
			var result = "", prop;
			for (prop in obj) {
				result += arrayReplacer(prop, obj[prop]);
			}
			return result.substring(0, result.length - 1);
		}

		function arrayReplacer(key, value) {
			var i, len, result = "", next;
			if (Array.isArray(value)) {
				for (i = 0, len = value.length; i < len; ++i) {
					next = value[i];
					result += (key.toString() + ((next !== null) ? ("=" + next.toString()) : "") + "&");
				}
			}
			else {
				result = key.toString() + ((value !== null) ? ("=" + value.toString()) : "") + "&";
			}
			return result;
		}

		function makeTempContainer() {
			var result = document.createElement("div");
			result.id = TEMP_CONTAINER_ID;
			// document.getElementById("testForm").appendChild(result);
			return result;
		}

		function makeHiddenFields(input) {
			var wrapper = document.createElement("div"), n, i;
			function _makeField(_name, _value) {
				var field;
				field = document.createElement("input");
				field.type = "hidden";
				field.name = decodeURIComponent(_name);
				field.value = decodeURIComponent(_value);
				wrapper.appendChild(field);
			}

			if (input.constructor === String) {
				input = input.split("&");
				for (i = 0; i < input.length; ++i) {
					n = input[i].split("=");
					_makeField(n[0], (n[1] || ""));
				}
			}
			else {
				for (n in input) {
					for (i = 0; i < input[n].length; ++i) {
						_makeField(n, input[n][i]);
					}
				}
			}

			return wrapper;
		}

		function findInputsLikeThis(input, container) {
			var nodes = INPUTS.findDescendants(container);

			function _filter(next) {
				return (next.name === input.name && next.value === input.value);
			}

			nodes = Array.prototype.filter.call(nodes, _filter);
			return nodes;
		}
	});
