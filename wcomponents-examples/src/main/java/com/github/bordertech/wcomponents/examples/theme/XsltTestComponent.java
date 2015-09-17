package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * A utility component which can be used to run XSL transformations on the server.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class XsltTestComponent extends WPanel {

	/**
	 * The text entry field for the path to the XSLT file.
	 */
	private final WTextField xsltTextField = new WTextField();
	/**
	 * The text entry field for the path to the input XML document.
	 */
	private final WTextField inputTextField = new WTextField();
	/**
	 * The text entry field for the path to the output file.
	 */
	private final WTextField outputTextField = new WTextField();

	private final WTextArea console = new WTextArea();

	/**
	 * Creates an XsltTestComponent.
	 */
	public XsltTestComponent() {
		this.setLayout(new FlowLayout(Alignment.VERTICAL));
		add(xsltTextField);
		add(inputTextField);
		add(outputTextField);

		WButton go = new WButton();
		go.setText("Go");
		go.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				runTransform();
			}
		});
		add(go);

		console.setColumns(100);
		console.setRows(30);
		add(console);
	}

	/**
	 * Runs the transform, according to the values defined in the input fields.
	 */
	private void runTransform() {
		File xsltFile = new File(xsltTextField.getText());
		File inputFile = new File(inputTextField.getText());
		File outputFile = new File(outputTextField.getText());

		try {
			console.setText("Processing...");
			transform(xsltFile, inputFile, outputFile);
			console.setText("Done");
		} catch (Exception ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			console.setText("Error:\n" + ex.toString() + "\n" + writer.toString());
		}
	}

	/**
	 * Runs an XSLT transform.
	 *
	 * @param xsltFile the XSL template to use.
	 * @param inputFile the file to transform.
	 * @param outputFile where to place the transformed output.
	 * @throws Exception if there is an I/O error, or error during transformation.
	 */
	private void transform(final File xsltFile, final File inputFile, final File outputFile) throws
			Exception {
		FileReader xsltIn = null;
		FileReader in = null;
		FileWriter out = null;

		try {
			xsltIn = new FileReader(xsltFile);
			Source xsltSource = new StreamSource(xsltIn);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(xsltSource);
			// transformer.setOutputProperty("disable-empty-element-collapsing", "true");

			in = new FileReader(inputFile);
			Source source = new StreamSource(in);
			out = new FileWriter(outputFile);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		} finally {
			if (xsltIn != null) {
				xsltIn.close();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
}
