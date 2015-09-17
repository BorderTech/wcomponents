package com.github.bordertech.wcomponents.util.error.impl;

import com.github.bordertech.wcomponents.Message;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.error.SystemFailureMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Simple {@link SystemFailureMapper} which just returns a hard coded message.
 *
 * @author Brian Kavanagh, 2009
 */
public class DefaultSystemFailureMapper implements SystemFailureMapper {

	/**
	 * Logging debugging interface object.
	 */
	private static final Log LOG = LogFactory.getLog(DefaultSystemFailureMapper.class);

	/**
	 * This method converts a java Throwable into a "user friendly" error message.
	 *
	 * @param throwable the Throwable to convert
	 * @return A {@link Message} containing the hard coded description "The system is currently unavailable."
	 */
	@Override
	public Message toMessage(final Throwable throwable) {
		LOG.error("The system is currently unavailable", throwable);
		return new Message(Message.ERROR_MESSAGE, InternalMessages.DEFAULT_SYSTEM_ERROR);
	}
}
