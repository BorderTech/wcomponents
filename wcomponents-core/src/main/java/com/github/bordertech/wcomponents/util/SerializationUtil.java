package com.github.bordertech.wcomponents.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Helper methods related to serialization. These are mainly useful for testing framework components.
 *
 * @author James Gifford
 */
public final class SerializationUtil {

	/**
	 * No instance methods here.
	 */
	private SerializationUtil() {
	}

	/**
	 * Takes a copy of an input object via serialization.
	 *
	 * @param in the object to copy
	 * @return the copied object
	 */
	public static Object pipe(final Object in) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(in);
			os.close();

			byte[] bytes = bos.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream is = new ObjectInputStream(bis);
			Object out = is.readObject();
			return out;
		} catch (Exception ex) {
			throw new SystemException("Failed to pipe " + in, ex);
		}
	}
}
