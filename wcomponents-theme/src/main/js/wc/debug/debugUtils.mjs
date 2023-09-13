import getVisibleText from "wc/ui/getVisibleText.mjs";
const IMG_QS = "img[alt]";

function isContentEmpty(element) {
	let content = getVisibleText(element, false, true);
	if (!content) {
		// is there an image with an alt attribute?
		const images = element.querySelectorAll(IMG_QS);
		for (let i = 0; i < images.length; ++i) {
			content = images[i].getAttribute("alt");
			if (content && content.trim()) {
				return false;
			}
		}
		return true;
	}
	return false;
}

function flagBad(tags, testFunc, container) {
	const inside = container || document;

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
		candidates.forEach(testFunc);
	}
}

export default {
	isContentEmpty,
	flagBad
};
