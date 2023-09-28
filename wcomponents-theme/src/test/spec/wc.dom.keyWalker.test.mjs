import keyWalker from "wc/dom/keyWalker.mjs";
import {setUpExternalHTML} from "../helpers/specUtils.mjs";
import domTesting from "@testing-library/dom";

describe("wc/dom/keyWalker", () => {

	let testHolder,
		groupedElements,
		treeRoot;

	// do not reject any node.
	function simpleFilter() {
		return NodeFilter.FILTER_ACCEPT;
	}

	/**
	 * @param {Element} el
	 * @return {number}
	 */
	function enabledFilter (el) {
		if (el.getAttribute("aria-disabled") === "true" || el.hasAttribute("disabled")) {
			return NodeFilter.FILTER_REJECT;
		}
		return NodeFilter.FILTER_ACCEPT;
	}

	/**
	 * @param {Element} el
	 * @return {number}
	 */
	function hiddenFilter(el) {
		if (el.hasAttribute("hidden")) {
			return NodeFilter.FILTER_REJECT;
		}
		return NodeFilter.FILTER_ACCEPT;
	}

	/**
	 * @param {Element} el
	 * @return {number}
	 */
	function treeFilter (el) {
		let result = enabledFilter(el);
		if (result === NodeFilter.FILTER_REJECT) {
			return result;
		}
		result = hiddenFilter(el);
		if (result === NodeFilter.FILTER_REJECT) {
			return result;
		}
		const role = el.getAttribute("role");
		if (role) {
			if (role === "group" || role === "treeitem") {
				return NodeFilter.FILTER_ACCEPT;
			}
			return NodeFilter.FILTER_REJECT;
		}
		return NodeFilter.FILTER_SKIP;
	}

	function makeGroupConfig(cycle, filter) {
		return {
			root: groupedElements,
			cycle: !!cycle,
			filter: (filter || simpleFilter)
		};
	}

	function makeTreeConfig(cycle, depthFirst, filter) {
		return {
			root: treeRoot,
			cycle: !!cycle,
			depthFirst: !!depthFirst,
			filter: (typeof filter === "function" ? filter : treeFilter)
		};
	}

	/**
	 * @param {Element} el
	 * @return {number}
	 */
	function mockClosedBranchNodesFilter(el) {
		const result = treeFilter(el);
		if (result !== NodeFilter.FILTER_REJECT) {
			if (el.getAttribute("role") === "group") {
				return NodeFilter.FILTER_REJECT;
			}
		}
		return result;
	}

	beforeAll(() => {
		return setUpExternalHTML("domKeyWalker.html").then(dom => {
			testHolder = dom.window.document.body;
		});
	});

	beforeEach(function() {
		if (!groupedElements) {
			let container = domTesting.getByTestId(testHolder, "domKeyWalkerGroup");
			groupedElements = container.getElementsByTagName("span");
		}
		expect(groupedElements.length).withContext("Could not get group to traverse").toBeGreaterThan(0);
		if (!treeRoot) {
			treeRoot = domTesting.getByTestId(testHolder, "domKeyWalkerTree");
		}
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	/*
	 * Tests of walking (linear) groups.
	 */
	it("testGetTargetFirstFromOther", function() {
		const start = groupedElements[1],
			expected = groupedElements[0],
			actual = keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.FIRST);
		expect(actual).withContext("getTarget should return the first member of the group").toBe(expected);
	});

	it("testGetTargetFirstFromFirst", function() {
		const start = groupedElements[0],
			expected = groupedElements[0],
			actual = keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.FIRST);
		expect(actual).withContext("getTarget should return the first member of the group").toBe(expected);
	});

	it("testGetTargetLastFromOther", function() {
		const start = groupedElements[1],
			expected = groupedElements[groupedElements.length - 1],
			actual = keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.LAST);
		expect(actual).withContext("getTarget should return the last member of the group").toBe(expected);
	});

	it("testGetTargetLastFromLast", function() {
		const start = groupedElements[groupedElements.length - 1],
			expected = groupedElements[groupedElements.length - 1],
			actual = keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.LAST);
		expect(actual).withContext("getTarget should return the last member of the group").toBe(expected);
	});

	it("testGetNextFromNotLast", function() {
		const start = groupedElements[0],
			expected = groupedElements[1],
			actual = keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("getTarget should return the next member of the group").toBe(expected);
	});

	it("testGetNextFromLastNoCycle", function() {
		const start = groupedElements[groupedElements.length - 1],
			actual = keyWalker.getTarget(makeGroupConfig(false), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("getTarget NEXT from last should return null").toBeNull();
	});

	it("testGetNextFromLastWithCycle", function() {
		const start = groupedElements[groupedElements.length - 1],
			expected = groupedElements[0],
			actual = keyWalker.getTarget(makeGroupConfig(true), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("getTarget should return the first member of the group").toBe(expected);
	});

	it("testGetPreviousFromNotFirst", function() {
		const start = groupedElements[1],
			expected = groupedElements[0],
			actual = keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("getTarget should return the previous member of the group").toBe(expected);
	});

	it("testGetPreviousFromFirstNoCycle", function() {
		const start = groupedElements[0],
			actual = keyWalker.getTarget(makeGroupConfig(false), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("getTarget PREVIOUS from first should return null").toBeNull();
	});

	it("testGetPreviousFromFirstWithCycle", function() {
		const start = groupedElements[0],
			expected = groupedElements[groupedElements.length - 1],
			actual = keyWalker.getTarget(makeGroupConfig(true), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("getTarget should return the last member of the group").toBe(expected);
	});

	it("testGetNextWithFilter", function() {
		const start = domTesting.queryByTestId(testHolder, "beforeDisabled"),
			expected = domTesting.queryByTestId(testHolder, "afterDisabled"),
			actual = keyWalker.getTarget(makeGroupConfig(false, enabledFilter), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("getTarget NEXT with filter should skip the disabled item.").toBe(expected);
	});

	it("testGetPreviousWithFilter", function() {
		const expected = domTesting.queryByTestId(testHolder, "beforeDisabled"),
			start = domTesting.queryByTestId(testHolder, "afterDisabled"),
			actual = keyWalker.getTarget(makeGroupConfig(false, enabledFilter), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("getTarget PREVIOUS with filter should skip the disabled item.").toBe(expected);
	});

	it("testGetLastWithFilter", function() {
		const start = groupedElements[0],
			expected = domTesting.queryByTestId(testHolder, "beforeDisabled"),
			_filter = function(el) {
				if (el.getAttribute("aria-hidden") === "true" || el.getAttribute("aria-disabled") === "true") {
					return NodeFilter.FILTER_REJECT;
				}
				return NodeFilter.FILTER_ACCEPT;
			},
			actual = keyWalker.getTarget(makeGroupConfig(false, _filter), start, keyWalker.MOVE_TO.LAST);
		expect(actual).withContext("getTarget LAST with filter should skip the filtered items.").toBe(expected);
	});

	it("testGetFirstWithFilter", function() {
		const start = groupedElements[groupedElements.length - 1],
			expected = groupedElements[1],
			_filter = function(el) {
				if (el.id === "domKeyWalkerGroupR1") {
					return NodeFilter.FILTER_REJECT;
				}
				return NodeFilter.FILTER_ACCEPT;
			},
			actual = keyWalker.getTarget(makeGroupConfig(false, _filter), start, keyWalker.MOVE_TO.FIRST);
		expect(actual).withContext("getTarget LAST with filter should skip the filtered items.").toBe(expected);
	});

	it("testGetTargetFallbackFilter", function() {
		const start = groupedElements[1],
			expected = groupedElements[0],
			config = makeGroupConfig();
		config.filter = null;
		const actual = keyWalker.getTarget(config, start, keyWalker.MOVE_TO.FIRST);
		expect(actual).toBe(expected);
	});

	it("testGetTargetNextFallbackFilter", function() {
		const start = groupedElements[1],
			expected =groupedElements[2],
			config = makeGroupConfig();
		config.filter = null;
		const actual = keyWalker.getTarget(config, start, keyWalker.MOVE_TO.NEXT);
		expect(actual).toBe(expected);
	});

	it("testGetTargetNextFallbackFilterDoesNotFindDisabledHidden", function() {
		const start = domTesting.queryByTestId(testHolder, "beforeDisabled"),
			config = makeGroupConfig();
		config.filter = null;
		const actual = keyWalker.getTarget(config, start, keyWalker.MOVE_TO.NEXT);
		expect(actual).toBeNull();
	});

	it("testGetTargetNextFallbackFilterWithCycle", function() {
		const start = domTesting.queryByTestId(testHolder, "beforeDisabled"),
			expected = groupedElements[0],
			config = makeGroupConfig(true);
		config.filter = null;
		const actual = keyWalker.getTarget(config, start, keyWalker.MOVE_TO.NEXT);
		expect(actual).toBe(expected);
	});

	it("testGetTargetGroupWithNoDirection", function () {
		expect(keyWalker.getTarget(makeGroupConfig(), groupedElements[0], null)).toBeNull();
	});

	/*
	 * TREE TESTS.
	 */
	it("testGetTargetTreeFirstNotFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.FIRST);
		expect(actual).withContext("FIRST FAILED with Tree").toBe(expected);
	});

	it("testGetTargetTreeFirstFromFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree1"),
			expected = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.FIRST);
		expect(actual).withContext("FIRST from FIRST FAILED with Tree").toBe(expected);
	});

	it("testGetTargetTreeLastNotLast", function() {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree7"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST);
		expect(actual).withContext("LAST FAILED with Tree").toBe(expected);
	});

	it("testGetTargetTreeLastFromLast", function() {
		const start = domTesting.queryByTestId(testHolder, "tree7"),
			expected = domTesting.queryByTestId(testHolder, "tree7"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST);
		expect(actual).withContext("LAST from LAST FAILED with Tree").toBe(expected);
	});

	it("testGetTargetTreeFirstNotFirstSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree32"),
			expected = domTesting.queryByTestId(testHolder, "tree31"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.FIRST);
		expect(actual).withContext("FIRST in sub branch FAILED with Tree").toBe(expected);
	});

	it("testGetTargetTreeFirstFromFirstSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree31"),
			expected = domTesting.queryByTestId(testHolder, "tree31"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.FIRST);
		expect(actual).withContext("FIRST from FIRST in sub branch FAILED with Tree").toBe(expected);
	});

	it("testGetTargetTreeLastNotLastSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree32"),
			expected = domTesting.queryByTestId(testHolder, "tree33"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST);
		expect(actual).withContext("LAST in sub brnanch FAILED with Tree").toBe(expected);
	});

	it("testGetTargetTreeLastFromLastSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree33"),
			expected = domTesting.queryByTestId(testHolder, "tree33"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST);
		expect(actual).withContext("LAST from LAST in sub branch FAILED with Tree").toBe(expected);
	});

	it("testPreviousInTreeSimple", function() {
		const start = domTesting.queryByTestId(testHolder, "tree6"),
			expected = domTesting.queryByTestId(testHolder, "tree5"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("simple PREVIOUS FAILED with Tree").toBe(expected);
	});

	it("testGetTargetPreviousTreeOverBranchNoDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree3"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("Branch PREVIOUS no depth first failed").toBe(expected);
	});

	it("testGetTargetPreviousTreeOverBranchDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree33"),
			actual = keyWalker.getTarget(makeTreeConfig(false, true), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("Branch PREVIOUS with depth first failed").toBe(expected);
	});

	it("testNextInTreeSimple", function() {
		const start = domTesting.queryByTestId(testHolder, "tree5"),
			expected = domTesting.queryByTestId(testHolder, "tree6"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("simple NEXT FAILED with Tree").toBe(expected);
	});

	it("testGetTargetNextTreeOverBranchNoDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree3"),
			expected = domTesting.queryByTestId(testHolder, "tree4"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("Branch next no depth first failed").toBe(expected);
	});

	it("testGetTargetNextTreeOverBranchDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree3"),
			expected = domTesting.queryByTestId(testHolder, "tree31"),
			actual = keyWalker.getTarget(makeTreeConfig(false, true), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("Branch next with depth first failed").toBe(expected);
	});

	it("testPreviousInTreeFirstNode", function() {
		const start = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("PREVIOUS from first in Tree should be null").toBeNull();
	});

	it("testNextInTreeLastNode", function() {
		const start = domTesting.queryByTestId(testHolder, "tree7"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("NEXT from last in Tree should be null").toBeNull();
	});

	it("testPreviousInTreeFirstNodeWithCycle", function() {
		const start = domTesting.queryByTestId(testHolder, "tree1"),
			expected = domTesting.queryByTestId(testHolder, "tree7"),
			actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("PREVIOUS from first in Tree with cycle FAILED").toBe(expected);
	});

	it("testNextInTreeLastNodeWithCycle", function() {
		const start = domTesting.queryByTestId(testHolder, "tree7"),
			expected = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("NEXT from first in Tree with cycle FAILED").toBe(expected);
	});

	it("testPreviousInTreeFirstNodeSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree31"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("PREVIOUS from first in Tree sub branch should be null").toBeNull();
	});

	it("testNextInTreeLastNodeSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree33"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("NEXT from last in Tree sub branch should be null").toBeNull();
	});

	it("testPreviousInTreeFirstNodeWithCycleSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree31"),
			expected = domTesting.queryByTestId(testHolder, "tree33"),
			actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("PREVIOUS from first in Tree sub branch with cycle FAILED").toBe(expected);
	});

	it("testNextInTreeLastNodeWithCycleSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree33"),
			expected = domTesting.queryByTestId(testHolder, "tree31"),
			actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("NEXT from first in Tree sub branch with cycle FAILED").toBe(expected);
	});

	it("testPreviousInTreeFirstNodeSubBranchDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree31"),
			expected = domTesting.queryByTestId(testHolder, "tree3"),
			actual = keyWalker.getTarget(makeTreeConfig(false, true), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).withContext("NEXT from first in Tree sub branch with depth first FAILED").toBe(expected);
	});

	it("testNextInTreeLastNodeSubBranchDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree33"),
			expected = domTesting.queryByTestId(testHolder, "tree4"),
			actual = keyWalker.getTarget(makeTreeConfig(false, true), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).withContext("NEXT from first in Tree sub branch with depth first FAILED").toBe(expected);
	});

	it("testGetTargetTreeParentSimple", function() {
		const start = domTesting.queryByTestId(testHolder, "tree33"),
			expected = domTesting.queryByTestId(testHolder, "tree3"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PARENT);
		expect(actual).withContext("Simple PARENT failed").toBe(expected);
	});

	it("testGetTargetTreeChildSimple", function() {
		const start = domTesting.queryByTestId(testHolder, "tree3"),
			expected = domTesting.queryByTestId(testHolder, "tree31"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.CHILD);
		expect(actual).withContext("Simple CHILD failed").toBe(expected);
	});

	it("testGetTargetTreeLastChildSimple", function() {
		const start = domTesting.queryByTestId(testHolder, "tree3"),
			expected = domTesting.queryByTestId(testHolder, "tree33"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST_CHILD);
		expect(actual).withContext("Simple LAST_CHILD failed").toBe(expected);
	});

	it("testGetTargetTreeTop", function() {
		const start = domTesting.queryByTestId(testHolder, "tree3"),
			expected = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.TOP);
		expect(actual).withContext("Simple TOP failed").toBe(expected);
	});

	it("testGetTargetTreeTopFromSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree33"),
			expected = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.TOP);
		expect(actual).withContext("Simple TOP from sub branch failed").toBe(expected);
	});

	it("testGetTargetTreeEnd", function() {
		const start = domTesting.queryByTestId(testHolder, "tree3"),
			expected = domTesting.queryByTestId(testHolder, "tree7"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.END);
		expect(actual).withContext("Simple END failed").toBe(expected);
	});

	it("testGetTargetTreeEndFromSubBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree33"),
			expected = domTesting.queryByTestId(testHolder, "tree7"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.END);
		expect(actual).withContext("Simple END from sub branch failed").toBe(expected);
	});

	it("testGetTargetTreePreviousIntoSubBranchDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree33"),
			actual = keyWalker.getTarget(makeTreeConfig(false, true), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).toBe(expected);
	});

	it("testGetTargetTreeNextOverClosedBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree2"),
			expected = domTesting.queryByTestId(testHolder, "tree4"),
			actual = keyWalker.getTarget(makeTreeConfig(false, false, mockClosedBranchNodesFilter), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).toBe(expected);
	});

	it("testGetTargetTreeNextOverClosedBranchDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree2"),
			expected = domTesting.queryByTestId(testHolder, "tree4"),
			actual = keyWalker.getTarget(makeTreeConfig(false, true, mockClosedBranchNodesFilter), start, keyWalker.MOVE_TO.NEXT);
		expect(actual).toBe(expected);
	});

	it("testGetTargetTreePreviousOverClosedBranch", function() {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree2"),
			actual = keyWalker.getTarget(makeTreeConfig(false, false, mockClosedBranchNodesFilter), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).toBe(expected);
	});

	it("testGetTargetTreePreviousOverClosedBranchDepthFirst", function() {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree2"),
			actual = keyWalker.getTarget(makeTreeConfig(false, true, mockClosedBranchNodesFilter), start, keyWalker.MOVE_TO.PREVIOUS);
		expect(actual).toBe(expected);
	});

	it("testGetTargetChildNoChildren", function() {
		const start = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.CHILD);
		expect(actual).withContext("CHILD without children should be null").toBeNull();
	});

	it("testGetTargetlastChildNoChildren", function() {
		const start = domTesting.queryByTestId(testHolder, "tree1"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST_CHILD);
		expect(actual).withContext("LAST_CHILD without children should be null").toBeNull();
	});

	it("testGetTargetParentTopLevel", function() {
		const start = domTesting.queryByTestId(testHolder, "domKeyWalkerTree"),
			actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PARENT);
		expect(actual).withContext("PARENT from top should be null").toBeNull();
	});

	/* tests skipping disabled or hidden */
	it("testGetNextAcrossDisabled", function () {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree6"),
			disabled = domTesting.queryByTestId(testHolder, "tree5");
		try {
			disabled.setAttribute("aria-disabled", "true");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.NEXT);
			expect(actual).toBe(expected);
		} finally {
			disabled.removeAttribute("aria-disabled");
		}
	});

	it("testGetNextAcrossHidden", function () {
		const start = domTesting.queryByTestId(testHolder, "tree4"),
			expected = domTesting.queryByTestId(testHolder, "tree6"),
			hidden = domTesting.queryByTestId(testHolder, "tree5");
		try {
			hidden.setAttribute("hidden", "hidden");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.NEXT);
			expect(actual).toBe(expected);
		} finally {
			hidden.removeAttribute("hidden");
		}
	});

	it("testGetPreviousAcrossDisabled", function () {
		const start = domTesting.queryByTestId(testHolder, "tree6"),
			expected = domTesting.queryByTestId(testHolder, "tree4"),
			disabled = domTesting.queryByTestId(testHolder, "tree5");
		try {
			disabled.setAttribute("aria-disabled", "true");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PREVIOUS);
			expect(actual).toBe(expected);
		} finally {
			disabled.removeAttribute("aria-disabled");
		}
	});

	it("testGetPreviousAcrossHidden", function () {
		const start = domTesting.queryByTestId(testHolder, "tree6"),
			expected = domTesting.queryByTestId(testHolder, "tree4"),
			hidden = domTesting.queryByTestId(testHolder, "tree5");
		try {
			hidden.setAttribute("hidden", "hidden");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.PREVIOUS);
			expect(actual).toBe(expected);
		} finally {
			hidden.removeAttribute("hidden");
		}
	});

	it("testGetFirstWithDisabled", function () {
		const start = domTesting.queryByTestId(testHolder, "tree6"),
			expected = domTesting.queryByTestId(testHolder, "tree2"),
			disabled = domTesting.queryByTestId(testHolder, "tree1");
		try {
			disabled.setAttribute("aria-disabled", "true");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.FIRST);
			expect(actual).toBe(expected);
		} finally {
			disabled.removeAttribute("aria-disabled");
		}
	});

	it("testGetFirstWithHidden", function () {
		const start = domTesting.queryByTestId(testHolder, "tree6"),
			expected = domTesting.queryByTestId(testHolder, "tree2"),
			hidden = domTesting.queryByTestId(testHolder, "tree1");
		try {
			hidden.setAttribute("hidden", "hidden");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.FIRST);
			expect(actual).toBe(expected);
		} finally {
			hidden.removeAttribute("hidden");
		}
	});

	it("testGetLastWithDisabled", function () {
		const start = domTesting.queryByTestId(testHolder, "tree2"),
			expected = domTesting.queryByTestId(testHolder, "tree6"),
			disabled = domTesting.queryByTestId(testHolder, "tree7");
		try {
			disabled.setAttribute("aria-disabled", "true");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST);
			expect(actual).toBe(expected);
		} finally {
			disabled.removeAttribute("aria-disabled");
		}
	});

	it("testGetLastWithHidden", function () {
		const start = domTesting.queryByTestId(testHolder, "tree2"),
			expected = domTesting.queryByTestId(testHolder, "tree6"),
			hidden = domTesting.queryByTestId(testHolder, "tree7");
		try {
			hidden.setAttribute("hidden", "hidden");
			const actual = keyWalker.getTarget(makeTreeConfig(), start, keyWalker.MOVE_TO.LAST);
			expect(actual).toBe(expected);
		} finally {
			hidden.removeAttribute("hidden");
		}
	});

	it("testPreviousInTreeFirstNodeWithCycleAndDisabled", function() {
		const start = domTesting.queryByTestId(testHolder, "tree1"),
			expected = domTesting.queryByTestId(testHolder, "tree6"),
			disabled = domTesting.queryByTestId(testHolder, "tree7");
		try {
			disabled.setAttribute("aria-disabled", "true");
			const actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.PREVIOUS);
			expect(actual).toBe(expected);
		} finally {
			disabled.removeAttribute("aria-disabled");
		}
	});

	it("testPreviousInTreeFirstNodeWithCycleAndHidden", function() {
		const start = domTesting.queryByTestId(testHolder, "tree1"),
			expected = domTesting.queryByTestId(testHolder, "tree6"),
			hidden = domTesting.queryByTestId(testHolder, "tree7");
		try {
			hidden.setAttribute("hidden", "hidden");
			const actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.PREVIOUS);
			expect(actual).toBe(expected);
		} finally {
			hidden.removeAttribute("hidden");
		}
	});

	it("testNextInTreeLastNodeWithCycleAndDisabled", function() {
		const start = domTesting.queryByTestId(testHolder, "tree6"),
			expected = domTesting.queryByTestId(testHolder, "tree1"),
			disabled = domTesting.queryByTestId(testHolder, "tree7");
		try {
			disabled.setAttribute("aria-disabled", "true");
			const actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.NEXT);
			expect(actual).toBe(expected);
		} finally {
			disabled.removeAttribute("aria-disabled");
		}
	});

	it("testNextInTreeLastNodeWithCycleAndHidden", function() {
		const start = domTesting.queryByTestId(testHolder, "tree6"),
			expected = domTesting.queryByTestId(testHolder, "tree1"),
			hidden = domTesting.queryByTestId(testHolder, "tree7");
		try {
			hidden.setAttribute("hidden", "hidden");
			const actual = keyWalker.getTarget(makeTreeConfig(true), start, keyWalker.MOVE_TO.NEXT);
			expect(actual).toBe(expected);
		} finally {
			hidden.removeAttribute("hidden");
		}
	});

	/* tests which should result in nothing */
	it("testGetTargetTreeWithNoStart", function () {
		expect(keyWalker.getTarget(makeTreeConfig(true), null, keyWalker.MOVE_TO.NEXT)).toBeNull();
	});

	it("testGetTargetTreeWithNoDirection", function () {
		expect(keyWalker.getTarget(makeTreeConfig(true), domTesting.queryByTestId(testHolder, "tree1"), null)).toBeNull();
	});

	/*
	 * Exception tests
	 */
	it("testGetTargetParentThrowsException", function() {
		const start = groupedElements[1];
		const doBadThing = () => keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.PARENT);
		expect(doBadThing).withContext("controller.MOVE_TO.PARENT should throw a ReferenceError").toThrowError();
	});

	it("testGetTargetChildThrowsException", function() {
		const start = groupedElements[1];
		const doBadThing = () => keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.CHILD);
		expect(doBadThing).withContext("controller.MOVE_TO.CHILD should throw a ReferenceError").toThrowError();
	});

	it("testGetTargetLastChildThrowsException", function() {
		const start = groupedElements[1];
		const doBadThing = () => keyWalker.getTarget(makeGroupConfig(), start, keyWalker.MOVE_TO.LAST_CHILD);
		expect(doBadThing).withContext("controller.MOVE_TO.LAST_CHILD should throw a ReferenceError").toThrowError();
	});

	it("testGetTargetTreeNonsenseDirectionThrowsError", function () {
		const start = domTesting.getByTestId(testHolder, "tree1");
		const doBadThing = () => keyWalker.getTarget(makeTreeConfig(), start, -1);
		expect(doBadThing).withContext("direction -1 should throw a ReferenceError").toThrowError();
	});

	it("testGetTargetNoConf", function () {
		// @ts-ignore
		const doBadThing = () => keyWalker.getTarget();
		expect(doBadThing).withContext("Expected a TypeError").toThrowError();
	});

	it("testGetTargetNoConfRoot", function () {
		// @ts-ignore
		const doBadThing = () => keyWalker.getTarget({});
		expect(doBadThing).withContext("Expected a TypeError").toThrowError();
	});

	it("testGetTargetRootNotElement", function () {
		const conf = { root: { nodeType: 8 } };
		// @ts-ignore
		const doBadThing = () => keyWalker.getTarget(conf);
		expect(doBadThing).withContext("Expected a TypeError").toThrowError();
	});
});
