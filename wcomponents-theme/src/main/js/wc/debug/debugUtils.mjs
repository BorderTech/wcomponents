import getVisibleText from "wc/ui/getVisibleText.mjs";
const IMG_QS = "img[alt]";

function isContentEmpty(element) {
	let content = getVisibleText(element, false, true);
	if (!content) {
		// is there an image with an alt attribute?
		const images = element.querySelectorAll(IMG_QS);
		for (const image of images) {
			content = image.getAttribute("alt");
			if (content?.trim()) {
				return false;
			}
		}
		return true;
	}
	return false;
}

function flagBad(tags, testFunc, container) {
	const inside = container || globalThis.document;

	if (!inside.querySelectorAll) {
		// nothing gets in here.
		return;
	}

	let candidates;
	if (container) {
		if (container.matches(tags)) {
			candidates = [container];
		}
	}
	if (!candidates) {
		candidates = Array.from(inside.querySelectorAll(tags));
	}

	if (candidates && candidates.length) {
		candidates.forEach(element => testFunc(element));
	}
}

export default {
	isContentEmpty,
	flagBad
};
