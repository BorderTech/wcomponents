import fs from "node:fs";

import { getAllByRole, findByText } from "@testing-library/dom";

import "wc/ui/table.mjs";
import { getButton, getResoucePath } from "../helpers/specUtils.mjs";

const { afterAll, beforeAll, describe, document, expect, it, MouseEvent, setTimeout } = globalThis;

describe("wc/ui/table", () => {
	const selectId = "eg-_3a3a";
	const deleteId = "eg-_3a3b";
	const editId = "eg-_3a3c";
	const nextPageId = "eg-_3a.pagination.3";

	let ownerDocument;
	let testHolder;

	beforeAll(function() {
		const dynamicPath = getResoucePath("uiTableDynamic.html", false);
		const testContent = fs.readFileSync(dynamicPath, "utf8");
		ownerDocument = document;
		testHolder = ownerDocument.body;
		// @ts-ignore
		testHolder.innerHTML = testContent;
	});


	afterAll(() => {
		testHolder.innerHTML = "";
	});

	it("Select button should be enabled when action constraint met", function(done) {
		let selectButton = getButton(testHolder, selectId);
		let deleteButton = getButton(testHolder, deleteId);
		let editButton = getButton(testHolder, editId);
		const nextPage = getButton(testHolder, nextPageId);

		expect(selectButton.hasAttribute("disabled")).withContext(`button ${selectId} should be disabled`).toBeTruthy();
		expect(deleteButton.hasAttribute("disabled")).withContext(`button ${deleteButton} should be disabled`).toBeTruthy();
		expect(editButton.hasAttribute("disabled")).withContext(`button ${editButton} should be disabled`).toBeTruthy();
		let rows = getAllByRole(testHolder, "row");
		clickAndWait(rows[rows.length - 1]).then(() => {
			expect(selectButton.hasAttribute("disabled")).withContext(`button ${selectId} should be enabled`).toBeFalsy();
			expect(deleteButton.hasAttribute("disabled")).withContext(`button ${deleteId} should be enabled`).toBeFalsy();
			expect(editButton.hasAttribute("disabled")).withContext(`button ${editId} should be enabled`).toBeFalsy();
			clickAndWait(nextPage).then(() => {
				findByText(testHolder, "Hudson").then(tableCell => {
					return clickAndWait(tableCell).then(() => {
						selectButton = getButton(testHolder, selectId);
						deleteButton = getButton(testHolder, deleteId);
						editButton = getButton(testHolder, editId);

						expect(selectButton.hasAttribute("disabled")).withContext(`button ${selectId} should be enabled`).toBeFalsy();
						expect(deleteButton.hasAttribute("disabled")).withContext(`button ${deleteId} should be enabled`).toBeFalsy();
						expect(editButton.hasAttribute("disabled")).withContext(`button ${editId} should be disabled`).toBeTruthy();
						setTimeout(done, 250);
					}).catch(done.fail);
				}).catch(done.fail);
			}).catch(done.fail);
		}).catch(done.fail);
	});

	function clickAndWait(element) {
		return new Promise(win => {
			setTimeout(win, 50);
			const clickEvent = new MouseEvent("click", {
				bubbles: true,
				cancelable: true,
				view: ownerDocument.view
			});
			element.dispatchEvent(clickEvent);
		});
	}
});
