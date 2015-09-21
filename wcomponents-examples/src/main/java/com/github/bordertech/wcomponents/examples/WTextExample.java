package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.io.Serializable;
import java.util.Date;

/**
 * Example {@link WText} usage showing various modes of operation:
 * <ul>
 * <li>{@link #sharedOnlyText} a WText with application-wide text.</li>
 * <li>{@link #dynamicText} a WText with user-specific text.</li>
 * <li>{@link #beanBoundText} a WText bound to a bean.</li>
 * <li>{@link #beanProviderBoundText} a WText bound to a {@link BeanProvider}.</li>
 * </ul>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTextExample extends WPanel {

	/**
	 * The default WText text to display.
	 */
	private static final String SHARED_TEXT = "Example shared text";

	/**
	 * Example WText with text that is set once for the application and doesn't change.
	 */
	private final WText sharedOnlyText = new WText(SHARED_TEXT);
	/**
	 * Example WText with text that is set per user.
	 */
	private final WText dynamicText = new WText(SHARED_TEXT);
	/**
	 * Example WText with text that is obtained from a bean.
	 */
	private final WText beanBoundText = new WText(SHARED_TEXT);
	/**
	 * Example WText with text that is obtained from a bean provided by a {@link BeanProvider}.
	 */
	private final WText beanProviderBoundText = new WText(SHARED_TEXT);

	/**
	 * Creates a WTextExample.
	 */
	public WTextExample() {
		beanBoundText.setBeanProperty("beanAttribute");
		beanProviderBoundText.setBeanProperty("innerBean.innerAttribute");

		setLayout(new FlowLayout(Alignment.VERTICAL));

		// Static text example
		add(new WHeading(WHeading.MAJOR, "The following line of text is from the static model."));
		add(sharedOnlyText);
		add(new WHorizontalRule());

		// Dynamic text example
		add(new WHeading(WHeading.MAJOR, "The following line of text is from the dynamic model."));
		add(dynamicText);
		add(new WHorizontalRule());

		// Bean-bound example
		add(new WHeading(WHeading.MAJOR, "The following line of text is from a Bean."));
		add(beanBoundText);
		WButton button1 = new WButton("Load Bean bound bean");
		add(button1);

		button1.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				ExampleBean result = fakeServiceCall("for bean loaded at " + new Date());
				beanBoundText.setBean(result);
			}
		});

		add(new WHorizontalRule());

		// Bean provider-bound example
		add(new WHeading(WHeading.MAJOR, "The following line of text is from a BeanProvider."));
		add(beanProviderBoundText);
		WButton button2 = new WButton("Load BeanProvider bound bean");
		add(button2);

		beanProviderBoundText.setBeanProvider(new ExampleBeanProvider());

		button2.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				beanProviderBoundText.setBeanId("123456");
			}
		});

		// A button that just causes a trip to the server (no action)
		add(new WHorizontalRule());
		add(new WButton("Refresh page"));
	}

	/**
	 * Fakes a service call and just returns dummy data.
	 *
	 * @param text the text to insert in the dummy data.
	 * @return a ExampleBean containing the specified text.
	 */
	private static ExampleBean fakeServiceCall(final String text) {
		ExampleBean exampleBean = new ExampleBean();
		exampleBean.setBeanAttribute("(beanAttribute) " + text);

		ExampleBean.DummyInnerBean dummyInnerBean = new ExampleBean.DummyInnerBean();
		dummyInnerBean.setInnerAttribute("(innerBean.innerAttribute) " + text);
		exampleBean.setInnerBean(dummyInnerBean);

		return exampleBean;
	}

	/**
	 * Override of preparePaintComponent to set the dynamic (user specific) text.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		String dateStr = "The current date is " + new Date();
		dynamicText.setText(dateStr);
	}

	/**
	 * An example implementation of the BeanProvider interface.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class ExampleBeanProvider implements BeanProvider, Serializable {

		/**
		 * This method is called whenever the BeanProviderBound component wants to access the Bean. The id (cache key,
		 * primary key, etc.) is retrieved from the component. This implementation just "fakes" a service call.
		 *
		 * @param beanProviderBound the provider bound component that wishes to retrieve the bean.
		 * @return the bean, or null if the bean id was null.
		 */
		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			Object beanId = beanProviderBound.getBeanId();
			return beanId == null ? null : fakeServiceCall("for bean with id " + beanId);
		}
	}

	/**
	 * An example bean.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class ExampleBean {

		/**
		 * A simple example of a bean attribute.
		 */
		private String beanAttribute;
		/**
		 * An example nested bean.
		 */
		private DummyInnerBean innerBean;

		/**
		 * An example bean.
		 *
		 * @author Yiannis Paschalidis
		 */
		public static final class DummyInnerBean {

			/**
			 * A simple example of a bean attribute.
			 */
			private String innerAttribute;

			/**
			 * @return Returns the innerAttribute.
			 */
			public String getInnerAttribute() {
				return innerAttribute;
			}

			/**
			 * @param innerAttribute The innerAttribute to set.
			 */
			public void setInnerAttribute(final String innerAttribute) {
				this.innerAttribute = innerAttribute;
			}
		}

		/**
		 * @return Returns the innerBean.
		 */
		public DummyInnerBean getInnerBean() {
			return innerBean;
		}

		/**
		 * @param innerBean The innerBean to set.
		 */
		public void setInnerBean(final DummyInnerBean innerBean) {
			this.innerBean = innerBean;
		}

		/**
		 * @return Returns the beanAttribute.
		 */
		public String getBeanAttribute() {
			return beanAttribute;
		}

		/**
		 * @param beanAttribute The beanAttribute to set.
		 */
		public void setBeanAttribute(final String beanAttribute) {
			this.beanAttribute = beanAttribute;
		}
	}
}
