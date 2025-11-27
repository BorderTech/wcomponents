import type { WComponentNode } from "../data.ts";
import { useEffect } from "react";

export default function WScript(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;
	console.log("script");

	useEffect(() => {
		const script = document.createElement("script");
		script.innerHTML = wcNode.value;

		if (wcNode.attributes["src"]) {
			script.setAttribute("src", wcNode.attributes["src"]);
		}

		document.body.appendChild(script);
		return () => {
			document.body.removeChild(script);
		};
	}, [wcNode.attributes, wcNode.value]);

	return <></>;
}
