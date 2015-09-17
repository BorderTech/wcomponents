package com.github.bordertech.wcomponents.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import junit.framework.AssertionFailedError;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

/**
 * ValidatorApp provides a quick way to validate arbitrary XML output against the schema. The ValidatorApp should be run
 * from the command-line.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class ValidatorApp extends JFrame {

	/**
	 * The input text area, for users to enter in XML to be validated.
	 */
	private final JTextArea in = new JTextArea(25, 80);
	/**
	 * The output text area, where validation errors are displayed.
	 */
	private final JTextArea out = new JTextArea(10, 80);

	/**
	 * Creates a ValidatorApp.
	 */
	public ValidatorApp() {
		super("Theme 2x XHTML Validator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		in.setLineWrap(true);
		in.setWrapStyleWord(true);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(new JLabel("XML to validate:"), BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().add(in);
		topPanel.add(scrollPane, BorderLayout.CENTER);

		JButton validateButton = new JButton("Validate");

		validateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				validateXml();
			}
		});

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(validateButton, BorderLayout.NORTH);
		bottomPanel.add(new JScrollPane(out), BorderLayout.CENTER);

		getContentPane().add(topPanel, BorderLayout.CENTER);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
		setVisible(true);
	}

	/**
	 * Validates the XML contained in the input text area and displays the errors (if any) in the output text area.
	 */
	private void validateXml() {
		String xhtml = in.getText();

		try {
			assertSchemaMatch(xhtml);
			out.setText("Valid!");
			out.setCaretPosition(0);
		} catch (AssertionFailedError error) {
			out.setText(error.getMessage().replaceAll("cvc-complex-type.2.4.a:", "\n").trim());
			out.setCaretPosition(0);
		} catch (Exception e) {
			out.setText(e.getMessage());
		}
	}

	/**
	 * Asserts that the given xhtml matches the schema.
	 *
	 * @param xhtml the xhtmlto validate.
	 *
	 * @throws SAXException if there is a parsing error
	 */
	public void assertSchemaMatch(final String xhtml) throws SAXException {
		// Load the schema.
		Object schema = getClass().getResource("/schema/ui/v1/schema.xsd").toString();

		// Validate the xhtml.
		Validator validator;
		StringReader reader = new StringReader(xhtml);
		validator = new Validator(reader);
		validator.useXMLSchema(true);
		validator.setJAXP12SchemaSource(schema);

		validator.assertIsValid();
	}

	/**
	 * A namespace context that handles the "ui" namespace.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class XmlLayoutTestNamespaceContext implements NamespaceContext {

		/**
		 * The backing context, used for resolving namespaces which this context does not know about.
		 */
		private final NamespaceContext backing;

		/**
		 * Creates a XmlLayoutTestNamespaceContext.
		 *
		 * @param backing the backing context, used for resolving unknown namespaces.
		 */
		XmlLayoutTestNamespaceContext(final NamespaceContext backing) {
			this.backing = backing;
		}

		/**
		 * Returns the namespace uri for the given prefix.
		 *
		 * @param prefix the namespace prefix
		 * @return the namespace uri, or null if an unknown prefix was supplied.
		 */
		@Override
		public String getNamespaceURI(final String prefix) {
			if ("ui".equals(prefix)) {
				return "https://github.com/bordertech/wcomponents/namespace/ui/v1.0";
			} else if ("html".equals(prefix)) {
				return "http://www.w3.org/1999/xhtml";
			} else if (backing != null) {
				return backing.getNamespaceURI(prefix);
			}

			return null;
		}

		/**
		 * @return an iteration of the namespace prefixes in this contex.t
		 */
		@Override
		public Iterator<String> getPrefixes() {
			Set<String> prefixes = new HashSet<>(2);
			prefixes.add("ui");
			prefixes.add("html");

			if (backing != null) {
				for (Iterator<?> i = backing.getPrefixes(); i.hasNext();) {
					prefixes.add((String) i.next());
				}
			}

			return prefixes.iterator();
		}
	}

	/**
	 * Main entry method.
	 *
	 * @param args the program command-line arguments (ignored).
	 */
	public static void main(final String[] args) {
		// Need to register the "ui" prefix with XMLUnit so that we can use it in XPath expressions
		NamespaceContext context = XMLUnit.getXpathNamespaceContext();
		context = new XmlLayoutTestNamespaceContext(context);
		XMLUnit.setXpathNamespaceContext(context);

		new ValidatorApp();
	}
}
