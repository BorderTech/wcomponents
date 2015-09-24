package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.InterceptorComponent;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import com.github.bordertech.wcomponents.servlet.ServletUtil;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.SerializationUtil;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests to check the performance of WComponent graph serialization. This test does not check for correct serialization
 * - see {@link Serialization_Test} instead.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(PerformanceTests.class)
public class SerializationPerformance_Test extends AbstractWComponentTestCase {

	/**
	 * The number of repetitions to use for testing serialization time. This should be set to be greater than the
	 * minimum number of invocations required to trigger JIT compilation.
	 */
	private static final int NUM_REPETITIONS = 2000;

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SerializationPerformance_Test.class);

	@Test
	public void testSerializationSize() throws Exception {
		WComponent nonRegistered = new AllComponents();
		nonRegistered.setLocked(true);
		WComponent registered = UIRegistry.getInstance().getUI(AllComponents.class.getName());

		UIContext nonRegisteredContext = createUIContext();
		UIContext registeredContext = createUIContext();

		// Test clean session
		int nonRegisteredSize = getUISize(nonRegisteredContext, nonRegistered);
		int registeredSize = getUISize(registeredContext, registered);

		LOG.info("Optimised size - clean session: " + registeredSize);
		LOG.info("default size - clean session: " + nonRegisteredSize);
		assertLessThan("Optimised size should be smaller than default", registeredSize,
				nonRegisteredSize);

		// Test used session - 50% components with models
		createUserModels(nonRegistered, nonRegisteredContext, 50);
		createUserModels(registered, registeredContext, 50);

		nonRegisteredSize = getUISize(nonRegisteredContext, nonRegistered);
		registeredSize = getUISize(registeredContext, registered);

		LOG.info("Optimised size - 50% models: " + registeredSize);
		LOG.info("default size - 50% models: " + nonRegisteredSize);
		assertLessThan("Optimised size should be smaller than default", registeredSize,
				nonRegisteredSize);

		// Test used session - 100% components with models
		createUserModels(nonRegistered, nonRegisteredContext, 100);
		createUserModels(registered, registeredContext, 100);

		nonRegisteredSize = getUISize(nonRegisteredContext, nonRegistered);
		registeredSize = getUISize(registeredContext, registered);

		LOG.info("Optimised size - 100% models: " + registeredSize);
		LOG.info("default size - 100% models: " + nonRegisteredSize);
		assertLessThan("Optimised size should be smaller than default", registeredSize,
				nonRegisteredSize);
	}

	@Test
	public void testSerializationSizeScaling() throws Exception {
		WComponent registered1 = UIRegistry.getInstance().getUI(AllComponents.class.getName());
		WComponent registered10 = UIRegistry.getInstance().getUI(AllComponents10.class.getName());

		UIContext context1 = createUIContext();
		UIContext context10 = createUIContext();

		// Test used session - 50% components with models
		createUserModels(registered1, context1, 50);
		createUserModels(registered10, context10, 50);

		int registered1Size = getUISize(context1, registered1);
		int registered10Size = getUISize(context10, registered10);

		LOG.info("Optimised size - 50% models 1x: " + registered1Size);
		LOG.info("Optimised size - 50% models 10x: " + registered10Size);
		assertLessThan("Size scaling should be O(n)", registered10Size, registered1Size * 10);
	}

	@Test
	public void testSerializationTime() throws Exception {
		WComponent nonRegistered = new AllComponents();
		nonRegistered.setLocked(true);
		WComponent registered = UIRegistry.getInstance().getUI(AllComponents.class.getName());

		UIContext nonRegisteredContext = createUIContext();
		UIContext registeredContext = createUIContext();

		// Test clean session
		long nonRegisteredTime = serializeSession(nonRegistered, nonRegisteredContext,
				NUM_REPETITIONS);
		long registeredTime = serializeSession(registered, registeredContext, NUM_REPETITIONS);

		LOG.info("Optimised time - clean session: " + (registeredTime / 1000000.0) + "ms");
		LOG.info("default time - clean session: " + (nonRegisteredTime / 1000000.0) + "ms");
		assertLessThan("Optimised time should be less than default time", registeredTime,
				nonRegisteredTime);

		// Test used session - 50% components with models
		createUserModels(nonRegistered, nonRegisteredContext, 50);
		createUserModels(registered, registeredContext, 50);

		nonRegisteredTime = serializeSession(nonRegistered, nonRegisteredContext, NUM_REPETITIONS);
		registeredTime = serializeSession(registered, registeredContext, NUM_REPETITIONS);

		LOG.info("Optimised time - 50% models: " + (registeredTime / 1000000.0) + "ms");
		LOG.info("default time - 50% models: " + (nonRegisteredTime / 1000000.0) + "ms");
		assertLessThan("Optimised time should be less than default time", registeredTime,
				nonRegisteredTime);

		// Test used session - 100% components with models
		createUserModels(nonRegistered, nonRegisteredContext, 100);
		createUserModels(registered, registeredContext, 100);

		nonRegisteredTime = serializeSession(nonRegistered, nonRegisteredContext, NUM_REPETITIONS);
		registeredTime = serializeSession(registered, registeredContext, NUM_REPETITIONS);

		LOG.info("Optimised time - 100% models: " + (registeredTime / 1000000.0) + "ms");
		LOG.info("default time - 100% models: " + (nonRegisteredTime / 1000000.0) + "ms");
		assertLessThan("Optimised time should be less than default time", registeredTime,
				nonRegisteredTime);
	}

	@Test
	public void testSerializationTimeScaling() throws Exception {
		WComponent registered1 = UIRegistry.getInstance().getUI(AllComponents.class.getName());
		WComponent registered10 = UIRegistry.getInstance().getUI(AllComponents10.class.getName());

		UIContext context1 = createUIContext();
		UIContext context10 = createUIContext();

		// Test used session - 50% components with models
		createUserModels(registered1, context1, 50);
		createUserModels(registered10, context10, 50);

		long registered1Time = serializeSession(registered1, context1, NUM_REPETITIONS);
		long registered10Time = serializeSession(registered10, context10, NUM_REPETITIONS);

		LOG.info("Optimised time - 50% models 1x: " + (registered1Time / 1000000.0) + "ms");
		LOG.info("Optimised time - 50% models 10x: " + (registered10Time / 1000000.0) + "ms");
		assertLessThan("Time scaling should be O(n)", registered10Time, registered1Time * 10);
	}

	/**
	 * Serializes a component tree.
	 *
	 * @param uic the UIContext to serialize
	 * @param comp the component to serialize.
	 * @return an approximation of the serialized session size.
	 * @throws IOException on error
	 */
	private int getUISize(final UIContext uic, final WComponent comp) throws IOException {
		sendRequest(comp, uic);
		return serialize(uic).length;
	}

	/**
	 * Serializes an object to a byte array.
	 *
	 * @param obj the object to serialize.
	 * @return the serialized object.
	 * @throws IOException an IO exception
	 */
	private static byte[] serialize(final Serializable obj) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.close();

		return bos.toByteArray();
	}

	/**
	 * Serializes the session the given number of times and returns the elapsed time.
	 *
	 * @param comp the root component.
	 * @param uic the UIContext to serialize.
	 * @param count the number of times to serialize the session.
	 * @return the elapsed time, in milliseconds.
	 * @throws Exception an exception
	 */
	private long serializeSession(final WComponent comp, final UIContext uic, final long count)
			throws Exception {
		sendRequest(comp, uic);

		// JIT warm-up
		for (int i = 0; i < count; i++) {
			SerializationUtil.pipe(uic);
		}

		return time(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < count; i++) {
					SerializationUtil.pipe(uic);
				}
			}
		}) / count;
	}

	/**
	 * Invokes WComponent request processing, so that this test case can more closely match a production scenario.
	 *
	 * @param comp the component to invoke request processing on.
	 * @param uic the user context to use.
	 */
	private void sendRequest(final WComponent comp, final UIContext uic) {
		PrintWriter writer = new PrintWriter(new NullWriter());
		uic.setEnvironment(new WServlet.WServletEnvironment("", "http://localhost", ""));
		uic.setUI(comp);

		InterceptorComponent root = ServletUtil.createInterceptorChain(new MockHttpServletRequest());
		root.attachUI(comp);

		Response response = new MockResponse();
		root.attachResponse(response);

		setActiveContext(uic);
		MockRequest request = new MockRequest();

		try {
			root.serviceRequest(request);
			root.preparePaint(request);
			root.paint(new WebXmlRenderContext(writer));
		} finally {
			resetContext();
		}
	}

	/**
	 * Creates user models for a certain percentage of components.
	 *
	 * @param component the branch of the UI hierarchy to modify.
	 * @param uic the user context that the models will be stored in.
	 * @param percent the percentage of components to set a model on.
	 */
	private void createUserModels(final WComponent component, final UIContext uic, final int percent) {
		List<WComponent> components = new ArrayList<>();
		findWComponents(component, components);
		int componentsWithModel = 0;

		setActiveContext(uic);

		// Rather than loop through the first percentage of components in the list,
		// we loop through the whole lot, so that there is a decent mix of clean/dirty components.
		for (int i = 0; i < components.size(); i++) {
			if ((componentsWithModel * 100 / (i + 1)) < percent) {
				components.get(i).setTag("SerializationPerformance_Test.tag" + i);
				componentsWithModel++;
			}
		}

		resetContext();
	}

	/**
	 * Finds all WComponents in the given WComponent hierarchy.
	 *
	 * @param comp the branch to traverse.
	 * @param result the list to add found components to.
	 */
	private void findWComponents(final WComponent comp, final List<WComponent> result) {
		result.add(comp);

		if (comp instanceof Container) {
			for (int i = 0; i < ((Container) comp).getChildCount(); i++) {
				findWComponents(((Container) comp).getChildAt(i), result);
			}
		}
	}

	/**
	 * Asserts that <code>first</code> is less than <code>second</code>.
	 *
	 * @param text the assertion text.
	 * @param first the first parameter to check.
	 * @param second the second parameter to check.
	 */
	private static void assertLessThan(final String text, final long first, final long second) {
		Assert.assertTrue(text + ": " + first + " < " + second, first < second);
	}

	/**
	 * AllComponents instantiated with 10 repetitions. This needs to be created as a subclass as the UIRegistry uses the
	 * class name.
	 */
	public static final class AllComponents10 extends AllComponents {

		/**
		 * Creates an AllComponents with 10 repetitions.
		 */
		public AllComponents10() {
			super(10);
		}
	}
}
