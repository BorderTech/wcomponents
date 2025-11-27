import { getWComponentNodeFromElement, type WComponentNode } from "../data.ts";
import { WComponentSet } from "../WComponent.tsx";

export default function WDefinitionList(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let className = "wc-definitionlist";
	className += wcNode.className ? ` ${wcNode.className}` : "";
	className += wcNode.attributes["type"] ? ` wc-definitionlist-type-${wcNode.attributes["type"]}` : "";

	return (
		<dl id={wcNode.id} className={className}>
			{wcNode.children.map((term) => {
				const wcNode = getWComponentNodeFromElement(term);
				return wcNode && <WTerm wcNode={wcNode} />;
			})}
		</dl>
	);
}

function WTerm(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return (
		<>
			<dt>{wcNode.attributes["text"]}</dt>
			{wcNode.children.map((data) => {
				const wcNode = getWComponentNodeFromElement(data);
				return wcNode && <WData wcNode={wcNode} />;
			})}
		</>
	);
}

function WData(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return (
		<dd>
			<WComponentSet xmlNodes={wcNode.children} />
		</dd>
	);
}
