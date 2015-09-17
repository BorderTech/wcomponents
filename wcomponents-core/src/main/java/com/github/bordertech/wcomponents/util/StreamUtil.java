package com.github.bordertech.wcomponents.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Static utility methods related to working with Streams.
 *
 * @author Martin Shevchenko
 */
public final class StreamUtil {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(StreamUtil.class);

	/**
	 * No instance methods here.
	 */
	private StreamUtil() {
	}

	/**
	 * Default value is 2048.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 2048;

	/**
	 * Copies information from the input stream to the output stream, using the default copy buffer size.
	 *
	 * @param in the source stream.
	 * @param out the destination stream.
	 * @throws IOException if there is an error reading or writing to the streams.
	 */
	public static void copy(final InputStream in, final OutputStream out)
			throws IOException {
		copy(in, out, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Copies information from the input stream to the output stream using a specified buffer size.
	 *
	 * @param in the source stream.
	 * @param out the destination stream.
	 * @param bufferSize the buffer size.
	 * @throws IOException if there is an error reading or writing to the streams.
	 */
	public static void copy(final InputStream in, final OutputStream out, final int bufferSize)
			throws IOException {
		final byte[] buf = new byte[bufferSize];
		int bytesRead = in.read(buf);

		while (bytesRead != -1) {
			out.write(buf, 0, bytesRead);
			bytesRead = in.read(buf);
		}

		out.flush();
	}

	/**
	 * Returns a byte array containing all the information contained in the given input stream.
	 *
	 * @param in the stream to read from.
	 * @return the stream contents as a byte array.
	 * @throws IOException if there is an error reading from the stream.
	 */
	public static byte[] getBytes(final InputStream in) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		copy(in, result);
		result.close();
		return result.toByteArray();
	}

	/**
	 * Closes an OutputStream, logging any exceptions.
	 *
	 * @param stream the stream to close.
	 */
	public static void safeClose(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				LOG.error("Failed to close resource stream", e);
			}
		}
	}

	/**
	 * This method returns the contents of a {@link InputStream} as a byte[].
	 *
	 * @param is The {@link InputStream} to be read into a byte[]
	 * @return A byte[] representing the contents of the given <em>is</em>.
	 * @throws IOException If there is an error reading from the <em>is</em>.
	 */
	public static byte[] streamToByteArray(final InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(is, baos);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}
}
