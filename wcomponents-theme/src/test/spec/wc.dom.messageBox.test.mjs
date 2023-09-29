import {iconClasses} from "wc/dom/messageBox.mjs";
import i18n from "wc/i18n/i18n.mjs";
import domTesting from "@testing-library/dom";

/*
 * Unit tests for wc/dom/messageBox
 */
describe("wc/dom/messageBox", () => {
	const testBoxId = "messageboxtest-box1";
	/**
	 * @param {Element|ShadowRoot} element
	 * @return {HTMLSlotElement}
	 */
	const getMessageSlot = (element) => {
		return /** @type HTMLSlotElement */(element.querySelector(".wc_messages slot"));
	};

	/**
	 * @param {Element|ShadowRoot} element
	 * @return {HTMLSlotElement}
	 */
	const getIconSlot = (element) => {
		return /** @type HTMLSlotElement */(element.querySelector("slot[name='icon']"));
	};

	const checkIcon = (heading, type) => {
		const iconName = iconClasses[type];
		const iconElement = heading.querySelector(`i.${iconName}`);
		expect(iconElement).toBeTruthy();
	};

	let fixture;
	let strings;

	beforeAll(() => {
		const keys = ["messagetitle_error", "messagetitle_warn", "messagetitle_info", "messagetitle_success"];
		return i18n.translate(keys).then(messages => {
			strings = {
				messagetitle_error: messages[keys.indexOf("messagetitle_error")],
				messagetitle_warn: messages[keys.indexOf("messagetitle_warn")],
				messagetitle_info: messages[keys.indexOf("messagetitle_info")],
				messagetitle_success: messages[keys.indexOf("messagetitle_success")]
			};
		});
	});

	beforeEach(() => {
		fixture = document.body.appendChild(document.createElement("div"));
	});

	afterEach(() => {
		fixture.parentElement.removeChild(fixture);
	});

	it("should default to an error box", () => {
		fixture.innerHTML = `<wc-messagebox data-testid="${testBoxId}"></wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		expect(element.getAttribute("type")).toBe("error");
	});

	it("should use a provided heading", () => {
		const customTitle = "Vexations!";
		fixture.innerHTML = `<wc-messagebox title="${customTitle}" type="error" data-testid="${testBoxId}"></wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		// @ts-ignore
		return domTesting.findByRole(element.shadowRoot, "heading").then(heading => {
			expect(heading.textContent).toBe(customTitle);
			checkIcon(heading, element.getAttribute("type"));
		});
	});

	it("should provide a default error heading", () => {
		fixture.innerHTML = `<wc-messagebox type="error" data-testid="${testBoxId}"></wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		// @ts-ignore
		return domTesting.findByRole(element.shadowRoot, "heading").then(heading => {
			expect(heading.textContent).toBeTruthy();
			expect(heading.textContent).toBe(strings["messagetitle_error"]);
			checkIcon(heading, element.getAttribute("type"));
		});
	});

	it("should provide a default warn heading", () => {
		fixture.innerHTML = `<wc-messagebox type="warn" data-testid="${testBoxId}"></wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		// @ts-ignore
		return domTesting.findByRole(element.shadowRoot, "heading").then(heading => {
			expect(heading.textContent).toBeTruthy();
			expect(heading.textContent).toBe(strings["messagetitle_warn"]);
			checkIcon(heading, element.getAttribute("type"));
		});
	});

	it("should provide a default info heading", () => {
		fixture.innerHTML = `<wc-messagebox type="info" data-testid="${testBoxId}"></wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		// @ts-ignore
		return domTesting.findByRole(element.shadowRoot, "heading").then(heading => {
			expect(heading.textContent).toBeTruthy();
			expect(heading.textContent).toBe(strings["messagetitle_info"]);
			checkIcon(heading, element.getAttribute("type"));
		});
	});

	it("should provide a default warn heading", () => {
		fixture.innerHTML = `<wc-messagebox type="success" data-testid="${testBoxId}"></wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		// @ts-ignore
		return domTesting.findByRole(element.shadowRoot, "heading").then(heading => {
			expect(heading.textContent).toBeTruthy();
			expect(heading.textContent).toBe(strings["messagetitle_success"]);
			checkIcon(heading, element.getAttribute("type"));
		});
	});

	it("renders a provided icon", () => {
		const iconClass = "fa fa-window-close-o";
		fixture.innerHTML = `
			<wc-messagebox data-testid="${testBoxId}">
				<i aria-hidden="true" class="${iconClass}" style="float:right;" slot="icon"></i>
				<div is="wc-message">Message 0</div>
				<div is="wc-message">Message 1</div>
			</wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		// @ts-ignore
		return domTesting.findByRole(element.shadowRoot, "heading").then(heading => {
			const iconSlot = getIconSlot(heading);
			expect(iconSlot.assignedElements().length).toBe(1);
			expect(iconSlot.assignedElements()[0].className).toBe(iconClass);
			checkIcon(heading, element.getAttribute("type"));
		});
	});

	it("renders the contained messages", () => {
		fixture.innerHTML = `
			<wc-messagebox type="success" data-testid="${testBoxId}">
				<i aria-hidden="true" class="fa fa-window-close-o" style="float:right;" slot="icon"></i>
				<div is="wc-message">Message 0</div>
				<div is="wc-message">Message 1</div>
			</wc-messagebox>`;
		const element = domTesting.getByTestId(fixture, testBoxId);
		// @ts-ignore
		return domTesting.findByRole(element.shadowRoot, "heading").then(heading => {
			checkIcon(heading, element.getAttribute("type"));
			const messageSlot = getMessageSlot(element.shadowRoot);
			expect(messageSlot.assignedElements().length).toBe(2);
			for (let i = 0; i < messageSlot.assignedNodes.length; i++) {
				let messageElement = messageSlot[i];
				expect(messageElement.textContent).toBe(`Message ${i}`);
			}
		});
	});
});
