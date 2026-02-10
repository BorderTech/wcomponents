import type { WComponentNode } from "../data.ts";

// Does nothing. Use this for WComponents/XML which shouldn't render anything.
export default function WNoOp(_props: { wcNode: WComponentNode }) {
	return <></>;
}
