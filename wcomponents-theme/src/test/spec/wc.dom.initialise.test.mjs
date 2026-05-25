import initialise, {setView} from "wc/dom/initialise.mjs";
import {setUpExternalHTML} from "../helpers/specUtils.mjs";

describe("wc/dom/initialise", () => {
	let origWindow;

	beforeAll(() => {
		origWindow = window;
	});

	afterAll(() => {
		setView(origWindow);
	});

	beforeEach(() => {
		return setUpExternalHTML("domUsefulDom.html").then(dom => {
			setView(dom.window);
		});
	});

	/*
	 * NOTE on async tests.
	 * We can call the deferred object resolver from the initialise callback and
	 * assume that all callbacks are called, even in the test of that
	 * assumption, because if the resolver is not called the test will timeout and
	 * therefore fail.
	 */


	it("should return the registered object", function() {
		const obj = {};
		expect(initialise.register(obj)).toBe(obj);
	});

	it("addInitRoutine should return null when add fails", function() {
		expect(initialise.addInitRoutine(7)).toBeNull();
	});

	it("addCallback should return null when add fails", function() {
		expect(initialise.addCallback(7)).toBeNull();
	});

	it("should provide a meaningful toString", function() {
		const before = initialise.toString();
		initialise.addBodyListener(function() {});  // adding a listener should change the toString
		const after = initialise.toString();
		expect(after).withContext("toString not good enough").not.toEqual(before);
	});

	it("calls all the subscribers", function(done) {
		let ptr = 0;
		const expected = 3;

		initialise.addBodyListener({
			initialise: () => {
				console.log("addBodyListener");
				subscriber();
			}
		});

		initialise.addCallback(() => {
			console.log("addCallback");
			subscriber();
		});
		initialise.addInitRoutine(() => {
			console.log("addInitRoutine");
			subscriber();
		});

		function subscriber() {
			ptr++;
			if (ptr === expected) {
				done();
			}
		}
	});

	it("should run the callbacks in order", function(done) {
		let ptr = 0;
		const expected = 3;

		/**
		 * @param {number} expectedOrder
		 * @param {string} name
		 * @return {function(): void}
		 */
		function callbackFactory(expectedOrder, name) {
			return function() {
				expect(ptr++).withContext(`${name} not called in expected order`).toBe(expectedOrder);
				if (ptr === expected) {
					done();
				}
			};
		}

		const bodyListener = callbackFactory(1, "bodyListener");
		const postInit = callbackFactory(2, "postInit");
		const initRoutine = callbackFactory(0, "initRoutine");

		initialise.addBodyListener({
			initialise: bodyListener
		});
		initialise.addCallback(postInit);
		initialise.addInitRoutine(initRoutine);
	});

	it("executes subscribers which are added by subscribers", function(done) {
		/**
		 * Tests a scenario that caused real world problems, it is important to maintain
		 * this test.
		 * A subscriber to initialise causes a new subscriber to be added to initialise.
		 * We need to make sure that new subscriber is subsequently executed.
		 */
		let ptr = 0;
		const expected = 4;

		/**
		 * @param {number} expectedOrder
		 * @param {string} name
		 * @param {function} [func]
		 * @return {function(): void}
		 */
		function callbackFactory(expectedOrder, name, func) {
			return function() {
				if (func) {
					initialise.addInitRoutine(func);
				}
				ptr++;
				if (ptr === expected) {
					done();
				}
			};
		}

		const initRoutineTwo = callbackFactory(3, "initRoutineTwo");
		const postInit = callbackFactory(2, "postInit");
		const initRoutine = callbackFactory(0, "initRoutine");
		const bodyListener = callbackFactory(1, "bodyListener", initRoutineTwo);

		initialise.addBodyListener({
			initialise: bodyListener
		});
		initialise.addCallback(postInit);
		initialise.addInitRoutine(initRoutine);
	});
});
