import type { WComponentNode } from "../data.ts";
import { WComponentSet } from "../WComponent.tsx";

// TODO: This component is a stub and does not yet fully implement wc.ui.decoratedlabel.xsl
export default function WLabelChild(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;
	const localName = wcNode.tagName.slice(3);

	return (
		<span id={wcNode.id} className={`wc-${localName} wc_dlbl_seg`}>
			<WComponentSet xmlNodes={wcNode.children} />
		</span>
	);
}
