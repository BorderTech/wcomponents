package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel.LevelDetails;
import com.github.bordertech.wcomponents.SimpleBeanTreeTableDataModel;
import com.github.bordertech.wcomponents.TableTreeNode;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WInvisibleContainer;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WNamingContext;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPanel.PanelMode;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WRadioButtonSelect.Layout;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.table.PersonBean.TravelDoc;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Example of {@link WTable} using large amounts of data.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TableLoadPerformance extends WApplication {

	/**
	 * Table load options.
	 */
	private enum TABLES {
		/**
		 * Load both tables.
		 */
		BOTH("Both"),
		/**
		 * Load WTable.
		 */
		WTABLE("WTable"),
		/**
		 * Load WDataTable.
		 */
		WDATATABLE("WDataTable");

		/**
		 * The option description.
		 */
		private final String desc;

		/**
		 * @param desc the option description
		 */
		TABLES(final String desc) {
			this.desc = desc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return desc;
		}
	}

	/**
	 * Messages.
	 */
	private final WMessages messages = new WMessages();
	/**
	 * Load options panel.
	 */
	private final WPanel optionsPanel = new WPanel(WPanel.Type.CHROME);

	/**
	 * Number of rows to load.
	 */
	private final WNumberField numRows = new WNumberField();
	/**
	 * Number of expanded rows.
	 */
	private final WNumberField numDocs = new WNumberField();
	/**
	 * Which table to load.
	 */
	private final WRadioButtonSelect rbOptions = new WRadioButtonSelect(TABLES.values());

	/**
	 * Load tables button.
	 */
	private final WButton loadButton = new WButton("Load test");

	/**
	 * WTable instance.
	 */
	private final WTable table = new WTable();
	/**
	 * WTable shim for elapse times.
	 */
	private final MyTimingShim tableShim = new MyTimingShim(table);
	/**
	 * WTable results panel.
	 */
	private final WPanel tableResultsPanel = new WPanel();
	/**
	 * WTable results.
	 */
	private final MyResults tableResults = new MyResults(tableShim);
	/**
	 * WTable ajax panel.
	 */
	private final WPanel tableAjaxPanel = new WPanel();
	/**
	 * WTable eager panel to load table so hr, prep and paint all timed.
	 */
	private final WPanel tableEagerPanel = new WPanel();

	/**
	 * WDataTable instance.
	 */
	private final WDataTable datatable = new WDataTable();
	/**
	 * WDataTable shim for elapse times.
	 */
	private final MyTimingShim dataShim = new MyTimingShim(datatable);
	/**
	 * WDataTable results panel.
	 */
	private final WPanel dataResultsPanel = new WPanel();
	/**
	 * WDataTable results.
	 */
	private final MyResults dataResults = new MyResults(dataShim);
	/**
	 * WDataTable ajax panel.
	 */
	private final WPanel dataAjaxPanel = new WPanel();
	/**
	 * WDataTable eager panel to load table so hr, prep and paint all timed.
	 */
	private final WPanel dataEagerPanel = new WPanel();

	/**
	 * Panel to hold table layout.
	 */
	private final WPanel tablePanel = new WPanel();
	/**
	 * Table layout panel.
	 */
	private final WPanel tableLayout = new WPanel();

	/**
	 * Panel to hold ajax controls.
	 */
	private final WPanel ajaxPanel = new WPanel() {
		@Override
		public boolean isHidden() {
			return true;
		}
	};

	/**
	 * Ajax to load tables.
	 */
	private final WAjaxControl ajax1 = new WAjaxControl(loadButton,
			new AjaxTarget[]{ajaxPanel, optionsPanel,
				tablePanel});

	/**
	 * Ajax to load WTable results.
	 */
	private final WAjaxControl ajax2 = new WAjaxControl(null,
			new AjaxTarget[]{ajaxPanel, tableResultsPanel}) {
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);
			if (AjaxHelper.isCurrentAjaxTrigger(this)) {
				ajax3.setVisible(isLoadWDataTable());
				tableResults.setVisible(isLoadWTable());
				tableResults.preparePaintComponent(request);
				setVisible(false);
			}
		}
	};

	/**
	 * Ajax to load WDataTable.
	 */
	private final WAjaxControl ajax3 = new WAjaxControl(null,
			new AjaxTarget[]{ajaxPanel, dataAjaxPanel}) {
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);
			if (AjaxHelper.isCurrentAjaxTrigger(this)) {
				ajax4.setVisible(true);
				dataShim.setVisible(isLoadWDataTable());
				setVisible(false);
			}
		}
	};

	/**
	 * Ajax to load WDataTable results.
	 */
	private final WAjaxControl ajax4 = new WAjaxControl(null,
			new AjaxTarget[]{ajaxPanel, dataResultsPanel}) {
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);
			if (AjaxHelper.isCurrentAjaxTrigger(this)) {
				dataResults.setVisible(isLoadWDataTable());
				dataResults.preparePaintComponent(request);
				setVisible(false);
			}
		}

	};

	/**
	 * Construct example.
	 */
	public TableLoadPerformance() {
		WPanel root = new WPanel();
		root.setMargin(new Margin(12));
		add(root);

		// Load options
		optionsPanel.setTitleText("Load test WTable and WDataTable");
		root.add(optionsPanel);

		optionsPanel.add(messages);

		WFieldLayout optionLayout = new WFieldLayout();
		optionLayout.setLabelWidth(20);
		optionsPanel.add(optionLayout);

		numRows.setNumber(500);
		numRows.setMandatory(true);
		numRows.setMinValue(0);
		numRows.setMaxValue(50000);
		optionLayout.addField("Number of rows", numRows);

		numDocs.setNumber(1);
		numDocs.setMandatory(true);
		numDocs.setMinValue(0);
		numDocs.setMaxValue(10);
		WField docsField = optionLayout.addField("Rows in expandable section", numDocs);
		docsField.setVisible(false);

		rbOptions.setMandatory(true);
		rbOptions.setSelected(TABLES.BOTH);
		rbOptions.setButtonLayout(Layout.FLAT);
		optionLayout.addField("Table", rbOptions);

		optionLayout.addField((WLabel) null, loadButton);

		loadButton.setAction(new ValidatingAction(messages.getValidationErrors(), optionLayout) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				tableLayout.reset();

				if (isLoadWDataTable() && numRows.getValue().intValue() > 2000) {
					messages.info(
							"Only use WTable with more than 2000 rows as WDataTable becomes unresponsive");
					return;
				}

				startLoad();
			}
		});

		// Table layouts
		root.add(tablePanel);
		tableLayout.setLayout(new ColumnLayout(new int[]{50, 50}, 6, 6));
		tableLayout.setMargin(new Margin(12, 0, 12, 0));
		tableLayout.setVisible(false);
		tablePanel.add(tableLayout);

		WPanel leftPanel = new WPanel(WPanel.Type.CHROME);
		leftPanel.setTitleText("WTable");
		tableLayout.add(leftPanel);

		WPanel rightPanel = new WPanel(WPanel.Type.CHROME);
		rightPanel.setTitleText("WDataTable");
		tableLayout.add(rightPanel);

		// WTable
		WNamingContext leftContext = new WNamingContext("tb");
		leftPanel.add(leftContext);

		leftContext.add(tableResultsPanel);
		tableResultsPanel.add(tableResults);

		leftContext.add(tableAjaxPanel);
		tableAjaxPanel.add(tableEagerPanel);
		tableEagerPanel.add(tableShim);

		tableEagerPanel.setIdName("eg1");
		tableEagerPanel.setMode(PanelMode.EAGER);
		tableEagerPanel.add(new WText("\u00A0"));

		// WDateTable
		WNamingContext rightContext = new WNamingContext("dt");
		rightPanel.add(rightContext);

		rightContext.add(dataResultsPanel);
		dataResultsPanel.add(dataResults);

		rightContext.add(dataAjaxPanel);
		dataAjaxPanel.add(dataEagerPanel);
		dataEagerPanel.add(dataShim);

		dataEagerPanel.setIdName("eg2");
		dataEagerPanel.setMode(PanelMode.EAGER);
		dataEagerPanel.add(new WText("\u00A0"));

		tableResults.setVisible(false);
		dataResults.setVisible(false);
		tableShim.setVisible(false);
		dataShim.setVisible(false);

		// Ajax triggers
		ajax1.setIdName("jx1");
		ajax2.setIdName("jx2");
		ajax3.setIdName("jx3");
		ajax4.setIdName("jx4");

		ajaxPanel.add(ajax1);
		tableEagerPanel.add(ajax2);
		ajaxPanel.add(ajax3);
		dataEagerPanel.add(ajax4);

		ajax2.setDelay(1);
		ajax2.setVisible(false);
		ajax3.setDelay(1);
		ajax3.setVisible(false);
		ajax4.setDelay(1);
		ajax4.setVisible(false);
		add(ajaxPanel);

		setupWTable();
		setupWDataTable();

	}

	/**
	 * Setup WTable.
	 */
	private void setupWTable() {
		WTextField col1 = new WTextField();
		col1.setIdName("my1");
		col1.setReadOnly(true);
		WTextField col2 = new WTextField();
		col2.setIdName("my2");
		col2.setReadOnly(true);
		WDateField col3 = new WDateField();
		col3.setIdName("my3");
		col3.setReadOnly(true);

		table.addColumn(new WTableColumn("COL1", col1));
		table.addColumn(new WTableColumn("COL2", col2));
		table.addColumn(new WTableColumn("COL3", col3));

		table.setExpandMode(WTable.ExpandMode.CLIENT);
		table.setIdName("wt");

		LevelDetails level = new LevelDetails("documents", TravelDocPanel.class, true);
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth"}, level);
		table.setTableModel(model);
	}

	/**
	 * Setup WTable.
	 */
	private void setupWDataTable() {
		WTextField col1 = new WTextField();
		col1.setIdName("my1");
		col1.setReadOnly(true);
		WTextField col2 = new WTextField();
		col2.setIdName("my2");
		col2.setReadOnly(true);
		WDateField col3 = new WDateField();
		col3.setIdName("my3");
		col3.setReadOnly(true);

		datatable.addColumn(new WTableColumn("COL1", col1));
		datatable.addColumn(new WTableColumn("COL2", col2));
		datatable.addColumn(new WTableColumn("COL3", col3));

		datatable.setExpandMode(WDataTable.ExpandMode.CLIENT);
		datatable.setIdName("wdt");
	}

	/**
	 * Start load.
	 */
	private void startLoad() {
		tableLayout.setVisible(true);

		List<PersonBean> beans = ExampleDataUtil.createExampleData(numRows.getNumber().intValue(),
				numDocs.getNumber()
				.intValue());

		if (isLoadWTable()) {
			table.setBean(beans);
		}

		if (isLoadWDataTable()) {
			TableTreeNode tree = createTree(beans);
			datatable.setDataModel(new SimpleBeanTreeTableDataModel(
					new String[]{"firstName", "lastName",
						"dateOfBirth"}, tree));
		}

		if (isLoadWTable()) {
			ajax2.setVisible(true);
			tableShim.setVisible(isLoadWTable());
		} else {
			ajax4.setVisible(true);
			dataShim.setVisible(isLoadWDataTable());
		}

	}

	/**
	 * @return true is loading WTable
	 */
	private boolean isLoadWTable() {
		return TABLES.BOTH.equals(rbOptions.getSelected()) || TABLES.WTABLE.equals(rbOptions.
				getSelected());
	}

	/**
	 * @return true is loading WDataTable
	 */
	private boolean isLoadWDataTable() {
		return TABLES.BOTH.equals(rbOptions.getSelected()) || TABLES.WDATATABLE.equals(rbOptions.
				getSelected());
	}

	/**
	 * @param beans the beans to load
	 * @return the tree of nodes required by WDataTable
	 */
	private TableTreeNode createTree(final List<PersonBean> beans) {
		TableTreeNode top = new TableTreeNode(null);

		// Build tree
		for (PersonBean bean : beans) {
			TableTreeNode row = new TableTreeNode(bean);
			List<TravelDoc> docs = bean.getDocuments();
			if (docs != null) {
				for (TravelDoc doc : docs) {
					TableTreeNode expandNode = new ExpandNode(doc);
					row.add(expandNode);
				}
			}
			top.add(row);
		}

		return top;
	}

	/**
	 * The expandable node required by WDataTable.
	 */
	public static final class ExpandNode extends TableTreeNode {

		/**
		 * @param document the travel document
		 */
		public ExpandNode(final Serializable document) {
			super(document, TravelDocPanel.class, false);
		}
	}

	/**
	 * Results panel.
	 */
	private static final class MyResults extends WPanel {

		/**
		 * Shim holding the results.
		 */
		private final MyTimingShim shim;
		/**
		 * Handle request results.
		 */
		private final WText hreq = new WText();
		/**
		 * Prepare paint results.
		 */
		private final WText prep = new WText();
		/**
		 * Paint results.
		 */
		private final WText paint = new WText();
		/**
		 * Total elapse.
		 */
		private final WText total = new WText();

		/**
		 * @param shim the shim holding the results
		 */
		private MyResults(final MyTimingShim shim) {
			this.shim = shim;

			WDefinitionList def = new WDefinitionList(WDefinitionList.Type.COLUMN);
			add(def);

			def.addTerm("Handle request", hreq);
			def.addTerm("Prepare paint", prep);
			def.addTerm("Paint", paint);
			def.addTerm("Total", total);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);

			hreq.setText(formatValue(shim.getHrElapse()));
			prep.setText(formatValue(shim.getPrepElapse()));
			paint.setText(formatValue(shim.getPaintElapse()));

			int tot = 0;
			Long val = shim.getHrElapse();
			if (val != null) {
				tot += val.intValue();
			}
			val = shim.getPrepElapse();
			if (val != null) {
				tot += val.intValue();
			}
			val = shim.getPaintElapse();
			if (val != null) {
				tot += val.intValue();
			}
			total.setText(tot == 0 ? null : formatValue(Long.valueOf(tot)));
		}

		/**
		 * @param value the value to format
		 * @return the formatted value
		 */
		private String formatValue(final Long value) {
			if (value == null) {
				return null;
			}
			String val = String.valueOf(value.intValue());
			return val + " ms";
		}
	}

	/**
	 * Shim that wraps the tables so the elapse times can be determined.
	 */
	private static final class MyTimingShim extends WContainer {

		/**
		 * Invisible container to hold the table.
		 */
		private final WInvisibleContainer inv = new WInvisibleContainer();
		/**
		 * The content being timed.
		 */
		private final WComponent content;

		/**
		 * @param content the content being timed.
		 */
		private MyTimingShim(final WComponent content) {
			this.content = content;
			add(inv);
			inv.add(content);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void handleRequest(final Request request) {
			long start = new Date().getTime();
			content.serviceRequest(request);
			long finish = new Date().getTime();
			long elapse = finish - start;
			getOrCreateComponentModel().setAttribute("elapse_hr", elapse);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			long start = new Date().getTime();
			content.preparePaint(request);
			long finish = new Date().getTime();
			long elapse = finish - start;
			getOrCreateComponentModel().setAttribute("elapse_prep", elapse);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void paintComponent(final RenderContext renderContext) {
			long start = new Date().getTime();
			content.paint(renderContext);
			long finish = new Date().getTime();
			long elapse = finish - start;
			getOrCreateComponentModel().setAttribute("elapse_paint", elapse);
		}

		/**
		 * @return the handle request elapse time
		 */
		public Long getHrElapse() {
			return (Long) getComponentModel().getAttribute("elapse_hr");
		}

		/**
		 * @return the prepare paint elapse time
		 */
		public Long getPrepElapse() {
			return (Long) getComponentModel().getAttribute("elapse_prep");
		}

		/**
		 * @return the paint elapse time
		 */
		public Long getPaintElapse() {
			return (Long) getComponentModel().getAttribute("elapse_paint");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void afterPaint(final RenderContext renderContext) {
			// After painting, make the table not visible so it does not get included the following ajax requests.
			// Because
			// WDataTable is so slow affects all AJAX.
			setVisible(false);
		}
	};

	/**
	 * An example component to display travel document details. Expects that the supplied bean is a {@link TravelDoc}.
	 */
	public static final class TravelDocPanel extends WBeanContainer {

		/**
		 * Creates a TravelDocPanel.
		 */
		public TravelDocPanel() {
			MyWText documentNumber = new MyWText();
			documentNumber.setBeanProperty("documentNumber");

			MyWText countryOfIssue = new MyWText();
			countryOfIssue.setBeanProperty("countryOfIssue");

			MyWText txt = new MyWText();
			txt.setText("\u00A0");

			add(documentNumber);
			add(txt);
			add(countryOfIssue);
		}
	}

	/**
	 * WText that by passes I18N processing.
	 */
	public static final class MyWText extends WText {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText() {
			// Bypass I18N (Performance hit)
			return (String) getData();
		}

	}

}
