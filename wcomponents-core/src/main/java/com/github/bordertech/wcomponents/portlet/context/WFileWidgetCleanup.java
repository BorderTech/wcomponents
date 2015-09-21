package com.github.bordertech.wcomponents.portlet.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.io.FileCleaner;

/**
 * This context listener is used to stop the reaper {@link Thread} that is started by the jakarta commons file upload
 * API.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WFileWidgetCleanup implements ServletContextListener {

	/**
	 * @param sce the servlet context event.
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		FileCleaner.exitWhenFinished();
	}

	/**
	 * Does nothing.
	 *
	 * @param sce the servlet context event.
	 */
	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		// does nothing.
	}
}
