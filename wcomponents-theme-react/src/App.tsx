import { useEffect, useState } from "react";
import { WComponent } from "./WComponent.tsx";
import { getClientLayout, type WComponentNode } from "./data.ts";

export default function App() {
	const [application, setApplication] = useState<WComponentNode | null>(null);

	useEffect(() => {
		getClientLayout()
			.then((r) => setApplication(r))
			.catch((e) => console.error(e));
	}, []);

	console.log("render");
	return (
		<>
			{!application && <p>No valid XML application structure</p>}
			<div>{application && <WComponent wcNode={application} />}</div>
		</>
	);
}
