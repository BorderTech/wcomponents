package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.util.Config;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This launcher is intended to be run in a stand-alone environment, i.e. directly From a jar file rather than through
 * an IDE. It provides basic controls to interact with the LDE.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class StandaloneLauncher {

	/**
	 * The singleton instance of the StandaloneLauncher UI.
	 */
	private static final StandaloneLauncher INSTANCE = new StandaloneLauncher();

	/**
	 * The WComponent LDE launcher.
	 */
	private final MyLauncher launcher = new MyLauncher();

	/**
	 * This text area is used to display the log output.
	 */
	private final JTextArea log = new JTextArea();

	/**
	 * Creates a StandaloneLauncher.
	 */
	private StandaloneLauncher() {
		final JFrame frame = new JFrame("WComponent stand-alone LDE");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		frame.setContentPane(content);

		content.add(new JLabel("Log:"), BorderLayout.NORTH);

		log.setColumns(120);
		log.setRows(30);
		log.setFont(new Font("Monospaced", Font.PLAIN, 12));
		content.add(new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		content.add(buttonPanel, BorderLayout.SOUTH);

		JButton button = new JButton("Clear log");
		buttonPanel.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				log.setText("");
			}
		});

		button = new JButton("Launch browser");
		buttonPanel.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				// Use of reflections here is required as we still need to compile under java 1.5.
				//Desktop.getDesktop().open(index);
				try {
					Class<?> clazz = Class.forName("java.awt.Desktop");
					Object instance = clazz.getMethod("getDesktop").invoke(null);
					clazz.getMethod("browse", URI.class).invoke(instance, new URI(
							getInstance().launcher.getUrl()));
				} catch (ClassNotFoundException e) {
					//Desktop isn't available in Java 1.5, so we'll just use a
					// windows specific cmdline for now
					String cmd = "rundll32 url.dll,FileProtocolHandler "
							+ getInstance().launcher.getUrl();

					try {
						Runtime.getRuntime().exec(cmd);
					} catch (Exception e2) {
						LogFactory.getLog(getClass()).error("Failed to launch browser", e);
					}
				} catch (Exception e) {
					LogFactory.getLog(getClass()).error("Failed to launch browser", e);
				}
			}
		});

		button = new JButton("Exit");
		buttonPanel.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				// Trigger closing of the frame rather than forcefully terminating the VM.
				// This allows clean-up routines registered in window listeners to run.
				WindowEvent closeEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeEvent);
			}
		});

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * @return the Singleton instance of the StandaloneLauncher.
	 */
	public static StandaloneLauncher getInstance() {
		return INSTANCE;
	}

	/**
	 * Logs a message to the console (text area).
	 *
	 * @param message the message to log.
	 */
	public void log(final String message) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					log(message);
				}
			});
		} else {
			log.append(message);
		}
	}

	/**
	 * The entry point when the launcher is run as a java application.
	 *
	 * @param args command-line arguments, ignored.
	 * @throws Exception on error
	 */
	public static void main(final String[] args) throws Exception {
		// Set the logger to use the text area logger
		System.setProperty("org.apache.commons.logging.Log",
				"com.github.bordertech.wcomponents.lde.StandaloneLauncher$TextAreaLogger");

		// Set the port number to a random port
		Configuration internalWComponentConfig = Config.getInstance();
		CompositeConfiguration config = new CompositeConfiguration(new MapConfiguration(
				new HashMap<String, Object>()));
		config.addConfiguration(internalWComponentConfig); // Internal WComponent config next
		config.setProperty("bordertech.wcomponents.lde.server.port", 0);
		Config.setConfiguration(config);

		getInstance().launcher.run();
		getInstance().log("LDE now running on " + getInstance().launcher.getUrl() + '\n');
	}

	/**
	 * Extension of PlainLauncher to make getUrl() public.
	 */
	public static final class MyLauncher extends PlainLauncher {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getUrl() {
			return super.getUrl();
		}
	}

	/**
	 * A commons-logging Log implementation which logs to the StandaloneLauncher log.
	 */
	public static final class TextAreaLogger implements Log {

		/**
		 * The TRACE log-level should be used to output even more fine-grained information than DEBUG.
		 */
		public static final int TRACE = 0;
		/**
		 * The DEBUG log-level should be used for fine-grained information useful for debugging an application.
		 */
		public static final int DEBUG = 1;
		/**
		 * The INFO log-level is used to output application processing at a course level.
		 */
		public static final int INFO = 2;
		/**
		 * The WARN log-level is used to log information about potentially damaging situations.
		 */
		public static final int WARN = 3;
		/**
		 * The ERROR log-level is used to log application errors where the application can recover.
		 */
		public static final int ERROR = 4;
		/**
		 * The FATAL log-level is used to log application errors where the application can not recover.
		 */
		public static final int FATAL = 5;

		/**
		 * The current log level.
		 */
		private int logLevel = DEBUG;

		/**
		 * Creates a TextAreaLogger.
		 *
		 * @param name the logger name.
		 */
		public TextAreaLogger(final String name) {
			// Warning, commons logging requires this (currently unused) constructor.
		}

		/**
		 * Sets the log level.
		 *
		 * @param level the new log level to set.
		 */
		public void setLogLevel(final int level) {
			logLevel = level;
		}

		/**
		 * Logs a message.
		 *
		 * @param message the message to log.
		 * @param throwable the throwable which caused the message to be logged (if applicable).
		 * @param level the logging level to log the message at.
		 */
		private void log(final Object message, final Throwable throwable, final int level) {
			if (level < logLevel) {
				return;
			}

			if (message != null) {
				String prefix = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date()) + ": ";
				StandaloneLauncher.getInstance().log(prefix + message.toString() + '\n');
			}

			if (throwable != null) {
				StringWriter writer = new StringWriter();
				PrintWriter printWriter = new PrintWriter(writer);
				throwable.printStackTrace(printWriter);
				printWriter.close();

				StandaloneLauncher.getInstance().log(writer.toString());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void debug(final Object message, final Throwable cause) {
			log(message, cause, DEBUG);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void debug(final Object message) {
			log(message, null, DEBUG);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void error(final Object message, final Throwable cause) {
			log(message, cause, ERROR);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void error(final Object message) {
			log(message, null, ERROR);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void fatal(final Object message, final Throwable cause) {
			log(message, cause, FATAL);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void fatal(final Object message) {
			log(message, null, FATAL);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void info(final Object message, final Throwable cause) {
			log(message, cause, INFO);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void info(final Object message) {
			log(message, null, INFO);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void warn(final Object message, final Throwable cause) {
			log(message, cause, WARN);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void warn(final Object message) {
			log(message, null, WARN);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDebugEnabled() {
			return logLevel <= DEBUG;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isErrorEnabled() {
			return logLevel <= ERROR;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isFatalEnabled() {
			return logLevel <= FATAL;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isInfoEnabled() {
			return logLevel <= INFO;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isWarnEnabled() {
			return logLevel <= WARN;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isTraceEnabled() {
			return logLevel <= TRACE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void trace(final Object message, final Throwable cause) {
			log(message, cause, TRACE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void trace(final Object message) {
			log(message, null, TRACE);
		}
	}
}
