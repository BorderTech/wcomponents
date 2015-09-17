package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WRepeater_Test - unit tests for {@link WRepeater}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WRepeater_Test extends AbstractWComponentTestCase {

	/**
	 * Test data for the repeater.
	 */
	private static final String[] ROW_DATA = new String[]{
		"WRepeater_Test.row1",
		"",
		"WRepeater_Test.row3"
	};

	@Test
	public void testSetBeanList() {
		WRepeater repeater = new WRepeater();
		Assert.assertEquals("Default bean list should be empty", 0, repeater.getBeanList().size());

		repeater.setLocked(true);
		setActiveContext(createUIContext());
		repeater.setBeanList(Arrays.asList(ROW_DATA));
		Assert.assertEquals("Incorrect bean list size", 3, repeater.getBeanList().size());
		Assert.assertEquals("Incorrect row data 0", ROW_DATA[0], repeater.getBeanList().get(0));
		Assert.assertEquals("Incorrect row data 1", ROW_DATA[1], repeater.getBeanList().get(1));
		Assert.assertEquals("Incorrect row data 2", ROW_DATA[2], repeater.getBeanList().get(2));

		resetContext();
		Assert.assertEquals("Bean list for other UI context should be empty", 0, repeater.
				getBeanList().size());
	}

	@Test
	public void testValidate() {
		WTextField textField = new WTextField();
		textField.setMandatory(true);

		WRepeater repeater = new WRepeater();
		repeater.setRepeatedComponent(textField);
		repeater.setLocked(true);

		setActiveContext(createUIContext());
		List<Diagnostic> diags = new ArrayList<>();

		repeater.preparePaint(new MockRequest());
		repeater.validate(diags);
		Assert.assertTrue("Should not have any validation errors for zero rows", diags.isEmpty());

		repeater.setBeanList(Arrays.asList(ROW_DATA));
		repeater.preparePaint(new MockRequest());
		repeater.validate(diags);
		Assert.assertEquals("Should have one validation error", 1, diags.size());
	}

	@Test
	public void testHandleRequest() {
		WRepeater repeater = new WRepeater();
		repeater.setRepeatedComponent(new WTextField());
		repeater.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);
		MockRequest request = new MockRequest();
		repeater.setBeanList(Arrays.asList(ROW_DATA));
		repeater.preparePaint(request);

		Assert.assertEquals("Incorrect row value before request", ROW_DATA[0], repeater.
				getBeanList().get(0));
		UIContext rowContext = repeater.getRowContexts().get(0);

		setActiveContext(rowContext);
		String name = repeater.getRepeatedComponent().getId();
		String newValue = ROW_DATA[0] + ".new";
		request.setParameter(name, newValue);

		setActiveContext(uic);
		repeater.handleRequest(request);
		DataBound component = (DataBound) repeater.getRepeatedComponent();

		setActiveContext(rowContext);
		Assert.assertEquals("Incorrect row value after request", newValue, component.getData());
	}

	@Test
	public void testDataChange() {
		final String text1 = "WRepeater_Test.testDataChange.text1";
		final String text2 = "WRepeater_Test.testDataChange.text2";

		WTextField textField = new WTextField();
		textField.setText(text1);

		WRepeater repeater = new WRepeater(textField);
		repeater.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);
		repeater.setBeanList(Arrays.asList(ROW_DATA));

		SubUIContext lastRowContext = (SubUIContext) repeater.getRowContexts().get(
				ROW_DATA.length - 1);
		setActiveContext(lastRowContext);
		textField.setText(text2);
		String lastRowId = textField.getId();
		Assert.assertEquals("Last row should have user text", text2, textField.getText());

		// Rows which are present in the new bean list should retain their data when setBeanList is called
		setActiveContext(uic);
		repeater.setBeanList(Arrays.asList(ROW_DATA[ROW_DATA.length - 1], ROW_DATA[0]));

		setActiveContext(repeater.getRowContexts().get(0));
		Assert.assertEquals("Row 1 should have previous user text", text2, textField.getText());
		Assert.assertTrue("Row 1 id should not have changed", lastRowId.equals(textField.getId()));
		setActiveContext(repeater.getRowContexts().get(1));
		Assert.assertEquals("Row 2 should have bean text", ROW_DATA[0], textField.getText());

		// Rows which are not present in the new bean list should lose their data when setBeanList is called
		repeater.setBeanList(Arrays.asList("x"));
		repeater.setBeanList(Arrays.asList(ROW_DATA));

		setActiveContext(repeater.getRowContexts().get(0));
		Assert.assertEquals("Row 0 should have bean text", ROW_DATA[0], textField.getText());
		setActiveContext(repeater.getRowContexts().get(1));
		Assert.assertEquals("Row 1 should have bean text", null, textField.getText());
		setActiveContext(repeater.getRowContexts().get(2));
		Assert.assertEquals("Row 2 should have bean text", ROW_DATA[2], textField.getText());
	}

	@Test
	public void testDataChangeDuringRequest() {
		WRepeater repeater = new WRepeater();

		// We need a component which always retrieves its bean
		repeater.setRepeatedComponent(new WText() {
			@Override
			public void handleRequest(final Request request) {
				super.handleRequest(request);
				getBean();
			}

			@Override
			public void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				getBean();
			}
		});

		repeater.setLocked(true);

		UIContext uic = createUIContext();
		List<String> data = new ArrayList<>(Arrays.asList(ROW_DATA));

		setActiveContext(uic);
		repeater.setBeanList(data);
		MockRequest request = new MockRequest();

		repeater.handleRequest(request);

		String newValue = ROW_DATA[0] + ".new";
		data.add(newValue);

		repeater.preparePaint(request);
		SubUIContext lastRowContext = (SubUIContext) repeater.getRowContexts().get(data.size() - 1);

		Assert.assertEquals("Incorrect data for row context", newValue, repeater.
				getRowBeanForSubcontext(lastRowContext));

		DataBound component = (DataBound) repeater.getRepeatedComponent();
		setActiveContext(lastRowContext);
		Assert.assertEquals("Incorrect renderer value", newValue, component.getData());
	}

	@Test
	public void testRepeaterDefaultIds() {
		WNamingContext context = new WNamingContext("TEST");

		WComponent repeated = new WBeanComponent();
		WRepeater repeater = new WRepeater(repeated);
		context.add(repeater);

		context.setLocked(true);
		setActiveContext(new UIContextImpl());

		repeater.setBeanList(Arrays.asList("A", "B", "C"));

		String prefix = "TEST" + WComponent.ID_CONTEXT_SEPERATOR + WComponent.ID_FRAMEWORK_ASSIGNED_SEPERATOR;

		// Repeater ID
		Assert.assertEquals("Incorrect default id for repeater", prefix + "0", repeater.getId());
		String repeaterId = repeater.getId();

		// Repeat root ID
		Assert.assertEquals("Incorrect default id for repeater root", repeaterId + "-r", repeater.
				getRepeatRoot()
				.getId());

		// Row IDs
		for (UIContext uic : repeater.getRowContexts()) {
			// Id has uic row render id in it
			int row = ((SubUIContext) uic).getContextId();
			String idPrefix = repeaterId + WComponent.ID_CONTEXT_SEPERATOR + "r" + row
					+ WComponent.ID_CONTEXT_SEPERATOR + WComponent.ID_FRAMEWORK_ASSIGNED_SEPERATOR + "0";

			try {
				UIContextHolder.pushContext(uic);
				Assert.assertEquals("Incorrect default id for repeated component", idPrefix,
						repeated.getId());
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	@Test
	public void testRepeaterIdsWithIdnames() {
		WNamingContext context = new WNamingContext("TEST");

		WComponent repeated = new WBeanComponent();
		WRepeater repeater = new WRepeater(repeated);
		context.add(repeater);

		repeater.setIdName("R");
		repeated.setIdName("X");

		context.setLocked(true);
		setActiveContext(new UIContextImpl());

		repeater.setBeanList(Arrays.asList("A", "B", "C"));

		// Repeater ID
		Assert.assertEquals("Incorrect id for repeater with idname",
				"TEST" + WComponent.ID_CONTEXT_SEPERATOR + "R",
				repeater.getId());
		String repeaterId = repeater.getId();

		// Repeat root ID
		Assert.assertEquals("Incorrect id for repeater root", repeaterId + "-r", repeater.
				getRepeatRoot().getId());

		// Row IDs
		for (UIContext uic : repeater.getRowContexts()) {
			// Id has uic row render id in it
			int row = ((SubUIContext) uic).getContextId();
			String repeatedId = repeaterId + WComponent.ID_CONTEXT_SEPERATOR + "r" + row
					+ WComponent.ID_CONTEXT_SEPERATOR + "X";
			try {
				UIContextHolder.pushContext(uic);
				Assert.assertEquals("Incorrect id for repeated component with idname", repeatedId,
						repeated.getId());
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	@Test
	public void testRepeaterInternalIds() {
		WNamingContext context = new WNamingContext("TEST");

		WComponent repeated = new WBeanComponent();
		WRepeater repeater = new WRepeater(repeated);
		context.add(repeater);

		context.setLocked(true);
		setActiveContext(new UIContextImpl());

		repeater.setBeanList(Arrays.asList("A", "B", "C"));

		// Repeater ID
		Assert.assertEquals("Incorrect internal id for repeater",
				WComponent.DEFAULT_INTERNAL_ID + "0",
				repeater.getInternalId());
		String repeaterId = repeater.getInternalId();

		// Repeat root ID
		Assert.assertEquals("Incorrect internal id for repeater root", repeaterId + "r", repeater.
				getRepeatRoot()
				.getInternalId());

		// Row IDs
		for (UIContext uic : repeater.getRowContexts()) {
			// Id has uic row render id in it
			int row = ((SubUIContext) uic).getContextId();
			String repeatedId = repeaterId + "r" + row + "a";
			try {
				UIContextHolder.pushContext(uic);
				Assert.assertEquals("Incorrect internal id for repeated component", repeatedId,
						repeated.getInternalId());
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	@Test
	public void testNamingContextAccessors() {
		assertAccessorsCorrect(new WRepeater(), "namingContext", false, true, false);
	}

	@Test
	public void testNamingContextIdAccessor() {
		String id = "test";
		NamingContextable naming = new WRepeater();
		naming.setIdName(id);
		Assert.assertEquals("Incorrect component id", id, naming.getId());
		Assert.assertEquals("Naming context should match component id", id, naming.
				getNamingContextId());
	}

}
