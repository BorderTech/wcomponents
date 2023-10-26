import xmlString from "wc/xml/xmlString.mjs";
describe("wc/xml/xmlString", () => {
	let testXmlString;

	beforeAll(function() {
		testXmlString = `
			<?xml version="1.0"?>
			<note>
				<to>Tove</to>
				<from>Jani</from>
				<heading>Reminder</heading>
				<body>Don't forget me this weekend!</body>
			</note>`;
	});

	it("deserializes a string to an XML document object", () => {
		/* Convert a String to an XML DOM object with DOM methods etc */
		const result = xmlString.from(testXmlString),
			from = result.getElementsByTagName("from")[0];
		expect(result.documentElement.tagName).toBe("note");
		expect(from.tagName).toBe("from");
	});

	it("serializes an XML document object", function() {
		/* Convert an XML DOM to a String representation of the XML.
		 * Note, this test depends entirely on a working xmlTransformer.from
		 * ie the previous test must pass for this test to pass.  So if both of these
		 * tests fail then fix the other one first. */
		const whitespaceRe = /\s/g,
			xmlHeaderRe = /<\?xmlversion.*\?>/,
			expected = testXmlString.replace(whitespaceRe, "").replace(xmlHeaderRe, "");
		let xmlDom = xmlString.from(testXmlString);
		let result = xmlString.to(xmlDom).replace(whitespaceRe, "").replace(xmlHeaderRe, "");
		expect(result).toBe(expected);
	});

	it("Behaves sensibly when falsy arg is passed in", () => {
		expect(xmlString.to(null)).toBeNull();
	});
});
