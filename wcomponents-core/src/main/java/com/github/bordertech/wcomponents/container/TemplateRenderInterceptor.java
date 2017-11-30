package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.TemplateFunction;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Render templates on the server (i.e. Mustache).
 */
public class TemplateRenderInterceptor extends InterceptorComponent {

	private static final String RESOUCRCE_BUNDLE_BASE_NAME = ConfigurationProperties.getI18nThemeResourceBundleBaseName();

	private static final Map<String, ResourceBundle> RESOURCES = new HashMap<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final RenderContext renderContext) {

		UIContext uic = UIContextHolder.getCurrent();

		// Generate the HTML
		StringWriter outputBuffer = new StringWriter();
		PrintWriter outputWriter = new PrintWriter(outputBuffer);
		WebXmlRenderContext outputContext = new WebXmlRenderContext(outputWriter, uic.getLocale());
		super.paint(outputContext);

		String html = outputBuffer.toString();

		// Only process TEMPLATE if has I18N brackets
		if (html.contains("{{#i18n")) {
			// Create a new instance of factory to avoid caching the page.
			// https://github.com/spullara/mustache.java/issues/117
			final MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile(new StringReader(html), UUID.randomUUID().toString());
			StringWriter templateWriter = new StringWriter();
			mustache.execute(templateWriter, new I18NContext(getResourceBundle()));
			html = templateWriter.toString();
		}

		// Get the OUTPUT writer
		WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
		PrintWriter writer = webRenderContext.getWriter();
		writer.print(html);
	}

	/**
	 * The context scope including the translate function.
	 */
	public static class I18NContext {

		private final ResourceBundle res;

		/**
		 * The translation function.
		 */
		private final TemplateFunction i18n = new TemplateFunction() {
			@Override
			public String apply(final String input) {
				if (res.containsKey(input)) {
					return res.getString(input);  // return translation
				} else {
					return input;  // return untranslated label
				}
			}
		};

		/**
		 * @param res the resource bundle
		 */
		public I18NContext(final ResourceBundle res) {
			this.res = res;
		}

		/**
		 * @return the resource bundle
		 */
		public ResourceBundle getRes() {
			return res;
		}

		/**
		 *
		 * @return the i18n resource bundle
		 */
		public TemplateFunction getI18n() {
			return i18n;
		}

	}

	/**
	 * @return the current resource bundle
	 */
	private static ResourceBundle getResourceBundle() {
		Locale locale = I18nUtilities.getEffectiveLocale();
		String key = locale.toString();
		// Check if we have already loaded it
		ResourceBundle bundle = RESOURCES.get(key);
		if (bundle == null) {
			try {
				bundle = ResourceBundle.getBundle(RESOUCRCE_BUNDLE_BASE_NAME, locale);
				RESOURCES.put(key, bundle);
			} catch (Exception e) {
				throw new SystemException("Could not load theme resource bundle for locale [" + key + "].");
			}
		}
		return bundle;
	}

}
