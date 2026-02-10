import type { WComponentNode } from "../data.ts";
import { useEffect } from "react";

// Handles mounting and unmounting <script> tags from the DOM.
// We need to handle this manually because React doesn't like rendering <script> tags directly.
export default function WScript(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	useEffect(() => {
		// After the app renders, add the script tag.
		const script = document.createElement("script");
		script.innerHTML = wcNode.value;

		if (wcNode.attributes["src"]) {
			script.setAttribute("src", wcNode.attributes["src"]);
		}

		document.body.appendChild(script);
		return () => {
			// After this component unmounts, remove the script tag.
			document.body.removeChild(script);
		};
	}, [wcNode.attributes, wcNode.value]);

	return <></>;
}
