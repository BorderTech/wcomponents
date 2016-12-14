package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * When you have a situation where you have a list of a repeated data type that you want to render or edit, then this ui
 * component can help.
 * <p>
 * Typical usage:
 * </p>
 *
 * <blockquote>
 * <pre>
 * WRepeater repeater = new WRepeater();
 * repeater.setRepeatedComponent(new MyRepeatedComponent());
 * ...
 * repeater.setData(myBeanList);
 * </pre>
 * </blockquote>
 *
 * @author Ming Gao
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WRepeater extends WBeanComponent implements Container, AjaxTarget, NamingContextable {

	/**
	 * This key is used to access the rowId-to-row-bean mapping table from the scratch map.
	 */
	private static final String SCRATCHMAP_DATA_BY_ROW_ID_KEY = "WRepeater.dataByRowId";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WRepeater.class);

	/**
	 * Row context name pattern. Must contain only letters, digits or underscores.
	 */
	private static final Pattern ROW_ID_CONTEXT_NAME_PATTERN = Pattern.compile("[0-9a-zA-Z_]*");

	/**
	 * Creates a WRepeater without any repeated component. The method {@link #setRepeatedComponent(WComponent)} must be
	 * called to configure the repeater.
	 */
	public WRepeater() {
		// Do nothing
	}

	/**
	 * Creates a WRepeater which will repeat the given component.
	 *
	 * @param repeatedComponent the component to repeat.
	 */
	public WRepeater(final WComponent repeatedComponent) {
		add(new WRepeatRoot(this, repeatedComponent));
	}

	/**
	 * Set the wcomponent instance capable of handling a row. The component must implement at least one of the
	 * {@link DataBound}, {@link BeanBound} or {@link BeanProviderBound} interfaces. The data entries in the bean list
	 * passed to this repeater must be compatible with the component.
	 *
	 * @param repeatedComponent the component to repeat.
	 */
	public void setRepeatedComponent(final WComponent repeatedComponent) {
		removeAll();
		add(new WRepeatRoot(this, repeatedComponent));
	}

	/**
	 * Remember the list of beans that hold the data object for each row.
	 *
	 * @param beanList the list of data objects for each row.
	 */
	public void setBeanList(final List beanList) {
		RepeaterModel model = getOrCreateComponentModel();
		model.setData(beanList);

		// Clean up any stale data.
		HashSet rowIds = new HashSet(beanList.size());

		for (Object bean : beanList) {
			rowIds.add(getRowId(bean));
		}

		cleanupStaleContexts(rowIds);

		// Clean up cached component IDs
		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null && getRepeatRoot() != null) {
			clearScratchMaps(this);
		}
	}

	/**
	 * Recursively clears cached component scratch maps. This is called when the bean list changes, as the beans may
	 * have changed.
	 *
	 * @param node the component branch to clear cached data in.
	 */
	protected void clearScratchMaps(final WComponent node) {
		UIContext uic = UIContextHolder.getCurrent();

		uic.clearRequestScratchMap(node);
		uic.clearScratchMap(node);

		if (node instanceof WRepeater) {
			WRepeater repeater = (WRepeater) node;
			List<UIContext> rowContextList = repeater.getRowContexts();
			WComponent repeatedComponent = repeater.getRepeatedComponent();

			for (UIContext rowContext : rowContextList) {
				UIContextHolder.pushContext(rowContext);

				try {
					clearScratchMaps(repeatedComponent);
				} finally {
					UIContextHolder.popContext();
				}
			}

			// Make sure the repeater's scratch map has not been repopulated by processing its children
			uic.clearRequestScratchMap(node);
			uic.clearScratchMap(node);
		} else if (node instanceof Container) {
			Container container = (Container) node;

			for (int i = 0; i < container.getChildCount(); i++) {
				clearScratchMaps(container.getChildAt(i));
			}
		}
	}

	/**
	 * Retrieves the list of dataBeans that holds the data object for each row. The list returned will be the same
	 * instance as the one supplied via the setBeanList method. Will never return null, but it can return an empty list.
	 *
	 * @return the list of dataBeans that holds the data object for each row
	 */
	public List getBeanList() {
		List beanList = (List) getData();

		if (beanList == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(beanList);
	}

	/**
	 * Override updateBeanValue to update the bean value for all WBeanComponents Updates the bean value with the value
	 * returned by {@link #getData()}.
	 */
	@Override
	public void updateBeanValue() {
		List<?> beanList = this.getBeanList();
		WComponent renderer = getRepeatedComponent();

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);
			UIContext rowContext = getRowContext(rowData, i);

			UIContextHolder.pushContext(rowContext);

			try {
				WebUtilities.updateBeanValue(renderer);
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	// =========================================================================
	// === Start implementation of DataBound
	/**
	 * Remember the list of dataBeans that holds the data object for each row.
	 *
	 * @param dataBean Must be a java.util.List of dataBeans.
	 */
	@Override
	public void setData(final Object dataBean) {
		setBeanList((List) dataBean);
	}

	// === End implementation of DataBound
	// =========================================================================
	// === Start validation
	/**
	 * Validates each row.
	 *
	 * @param diags the list of SfpDiagnostics to add to.
	 */
	@Override
	public void validate(final List<Diagnostic> diags) {
		// Validate each row.
		List beanList = this.getBeanList();
		WComponent row = getRepeatedComponent();

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);
			UIContext rowContext = getRowContext(rowData, i);

			UIContextHolder.pushContext(rowContext);

			try {
				row.validate(diags);
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Visually marks any fields or blocks that have errors in the given diag list.
	 *
	 * @param diags the list of SfpDiagnostics from the last validation pass.
	 */
	@Override
	public void showErrorIndicators(final List<Diagnostic> diags) {
		// Show error indicators for each row.
		List beanList = this.getBeanList();
		WComponent row = getRepeatedComponent();

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);
			UIContext rowContext = getRowContext(rowData, i);

			UIContextHolder.pushContext(rowContext);

			try {
				row.showErrorIndicators(diags);
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	// === End validation
	// =========================================================================
	// === Start override methods from WComponent
	/**
	 * Override handleRequest to process the request for each row.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		assertConfigured();

		//
		// Service the request for each row.
		//
		List beanList = getBeanList();
		HashSet rowIds = new HashSet(beanList.size());
		WComponent row = getRepeatedComponent();

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);
			rowIds.add(getRowId(rowData));

			// Each row has its own context. This is why we can reuse the same
			// WComponent instance for each row.
			UIContext rowContext = getRowContext(rowData, i);

			try {
				UIContextHolder.pushContext(rowContext);
				row.serviceRequest(request);
			} finally {
				UIContextHolder.popContext();
			}
		}

		cleanupStaleContexts(rowIds);
	}

	/**
	 * Removes any stale contexts from the row context map.
	 *
	 * @param rowIds the current set of row Ids.
	 */
	protected void cleanupStaleContexts(final Set<?> rowIds) {
		RepeaterModel model = getOrCreateComponentModel();

		if (model.rowContextMap != null) {
			for (Iterator<Map.Entry<Object, SubUIContext>> i = model.rowContextMap.entrySet().
					iterator(); i.hasNext();) {
				Map.Entry<Object, SubUIContext> entry = i.next();
				Object rowId = entry.getKey();

				if (!rowIds.contains(rowId)) {
					i.remove();
				}
			}
			if (model.rowContextMap.isEmpty()) {
				model.rowContextMap = null;
			}
		}
	}

	/**
	 * Removes any component models that are in their default state.
	 * <p>
	 * The subContexts are held onto as they hold the "render id" for the row. This is needed to be able to match the
	 * rendered row back to the subcontext.
	 * </p>
	 */
	@Override
	public void tidyUpUIContext() {
		RepeaterModel model = getComponentModel();

		if (model.rowContextMap != null) {
			for (Iterator<Map.Entry<Object, SubUIContext>> i = model.rowContextMap.entrySet().
					iterator(); i.hasNext();) {
				Map.Entry<Object, SubUIContext> entry = i.next();
				SubUIContext subContext = entry.getValue();

				UIContextHolder.pushContext(subContext);

				try {
					getRepeatRoot().tidyUpUIContextForTree();
				} finally {
					UIContextHolder.popContext();
				}
			}
		}

		super.tidyUpUIContext();
	}

	/**
	 * Override preparePaintComponent to prepare each row for painting.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		assertConfigured();

		List beanList = getBeanList();

		List<Integer> used = new ArrayList<>();

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);

			// Each row has its own context. This is why we can reuse the same
			// WComponent instance for each row.
			UIContext rowContext = getRowContext(rowData, i);

			// Check the context has not been used for another row
			Integer subId = ((SubUIContext) rowContext).getContextId();
			if (used.contains(subId)) {
				Object rowId = ((SubUIContext) rowContext).getRowId();
				String msg = "The row context for row id ["
						+ rowId
						+ "] has already been used for another row. "
						+ "Either the row ID is not unique or the row bean has not implemented equals/hashcode "
						+ "or no rowIdBeanProperty set on the repeater that uniquely identifies the row.";
				throw new SystemException(msg);
			}
			used.add(subId);

			UIContextHolder.pushContext(rowContext);

			try {
				prepareRow(request, i);
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Prepares a single row for painting.
	 *
	 * @param request the request being responded to.
	 * @param rowIndex the row index.
	 */
	protected void prepareRow(final Request request, final int rowIndex) {
		WComponent row = getRepeatedComponent();
		row.preparePaint(request);
	}

	/**
	 * Allows a subclass to provide the ID used in the row naming context.
	 * <p>
	 * The returned ID must only contain letters, digits or underscores.
	 * </p>
	 *
	 * @param rowBean the row's bean
	 * @param rowId the row id
	 * @return the unique row id or null to use the row context id
	 */
	protected String getRowIdName(final Object rowBean, final Object rowId) {
		return null;
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Holds the extrinsic state information of a WRepeater.
	 */
	public static class RepeaterModel extends BeanAndProviderBoundComponentModel {

		/**
		 * Map rowIds to their subcontext.
		 */
		private Map<Object, SubUIContext> rowContextMap;
		/**
		 * Row id property.
		 */
		private String rowIdProperty;
		/**
		 * Row context id sequence. Give each row context a unique ID.
		 */
		private int rowContextIdSequence;
	}

	/**
	 * Sets the bean property that can be used to obtain each row's unique id.
	 *
	 * @param rowIdProperty the row id property, using jakarta PropertyUtils notation
	 */
	public void setRowIdProperty(final String rowIdProperty) {
		getOrCreateComponentModel().rowIdProperty = rowIdProperty;
	}

	/**
	 * Creates a new component model which is appropriate for this component.
	 *
	 * @return a new RepeaterModel.
	 */
	@Override
	protected RepeaterModel newComponentModel() {
		return new RepeaterModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RepeaterModel getComponentModel() {
		return (RepeaterModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RepeaterModel getOrCreateComponentModel() {
		return (RepeaterModel) super.getOrCreateComponentModel();
	}

	// --------------------------------
	/**
	 * Ensures that the repeater has been correctly configured. A SystemException will be thrown if the repeater is not
	 * configured correctly.
	 */
	private void assertConfigured() {
		if (getRepeatRoot() == null) {
			throw new SystemException(
					"WRepeater not configured correctly. No repeated component supplied.");
		}
	}

	/**
	 * Retrieves the UIContext for a row, where the row index is not known.
	 *
	 * @param rowBean the row's bean.
	 * @return The context for the given row.
	 */
	public UIContext getRowContext(final Object rowBean) {
		return getRowContext(rowBean, getBeanList().indexOf(rowBean));
	}

	/**
	 * Retrieves the UIContext for a row.
	 *
	 * @param rowBean the row's bean.
	 * @param rowIndex the row index.
	 * @return The context for the given row.
	 */
	public UIContext getRowContext(final Object rowBean, final int rowIndex) {
		RepeaterModel model = getOrCreateComponentModel();
		Object rowId = getRowId(rowBean);

		if (model.rowContextMap == null) {
			model.rowContextMap = new HashMap<>();
		}

		SubUIContext rowContext = model.rowContextMap.get(rowId);

		if (rowContext == null) {
			int seq = model.rowContextIdSequence++;

			// Just for the first row, check rowId has implemented equals/hashcode. Assumes each row will use the same
			// row id class.
			if (seq == 0) {
				try {
					if (rowId.getClass() != rowId.getClass().getMethod("equals", Object.class).
							getDeclaringClass()
							|| rowId.getClass() != rowId.getClass().getMethod("hashCode").
							getDeclaringClass()) {
						LOG.warn("Row id class ["
								+ rowId.getClass().getName()
								+ "] has not implemented equals or hashcode. This can cause errors when matching a row context. "
								+ "Implement equals/hashcode on the row bean or refer to setRowIdBeanProperty method on WRepeater.");
					}
				} catch (Exception e) {
					LOG.info(
							"Error checking equals and hashcode implementation on the row id. " + e.
							getMessage(), e);
				}
			}

			// Get the row render id name (used in naming context for each row)
			String renderId = getRowIdName(rowBean, rowId);
			if (renderId == null) {
				// Just use the context id as the row naming context
				rowContext = new SubUIContext(this, seq);
			} else {
				// Check ID is properly formed
				// Must only contain letters, digits and or underscores
				Matcher matcher = ROW_ID_CONTEXT_NAME_PATTERN.matcher(renderId);
				if (!matcher.matches()) {
					throw new IllegalArgumentException(
							"Row idName ["
							+ renderId
							+ "] must start with a letter and followed by letters, digits and or underscores.");
				}
				rowContext = new SubUIContext(this, seq, renderId);
			}
			rowContext.setRowId(rowId);
			model.rowContextMap.put(rowId, rowContext);
		}

		rowContext.setRowIndex(rowIndex); // just incase it has changed
		return rowContext;
	}

	/**
	 * Returns the row data for the given row context.
	 *
	 * @param subContext the row context.
	 * @return the data bean for the given row context.
	 */
	public Object getRowBeanForSubcontext(final SubUIContext subContext) {
		if (subContext.repeatRoot != getRepeatRoot()) {
			// TODO: Is this still necessary?
			throw new IllegalArgumentException("SubUIContext is not for this WRepeater instance.");
		}

		// We need to get the list using the parent context
		// so that e.g. caching in the scratch map works properly.
		UIContextHolder.pushContext(subContext.getParentContext());

		try {
			return getRowData(subContext.getRowId());
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * Returns the row data corresponding to the given id.
	 *
	 * @param rowId the row id
	 * @return the row data with the given id, or null if not found.
	 */
	private Object getRowData(final Object rowId) {
		// We cache row id --> row bean mapping per request for performance (to avoid nested loops)
		Map dataByRowId = (Map) getScratchMap().get(SCRATCHMAP_DATA_BY_ROW_ID_KEY);

		if (dataByRowId == null) {
			dataByRowId = createRowIdCache();
		}

		Object data = dataByRowId.get(rowId);

		if (data == null && !dataByRowId.containsKey(rowId)) {
			// Ok, new data has probably been added. We need to cache the new data.
			dataByRowId = createRowIdCache();
			data = dataByRowId.get(rowId);
		}

		return data;
	}

	/**
	 * Creates a temporary cache of mappings from row id to row data. The mappings will be stored in this repeater's
	 * scratch map under {@link #SCRATCHMAP_DATA_BY_ROW_ID_KEY}.
	 *
	 * @return the newly created map
	 */
	private Map createRowIdCache() {
		List<?> data = (List<?>) getData();
		Map<Object, Object> dataByRowId = new HashMap<>(data.size());

		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null) {
			uic.getScratchMap(this).put(SCRATCHMAP_DATA_BY_ROW_ID_KEY, dataByRowId);
		}

		for (int i = 0; i < data.size(); i++) {
			Object bean = data.get(i);
			dataByRowId.put(getRowId(bean), bean);
		}

		return dataByRowId;
	}

	/**
	 * Retrieves the row id for the given row.
	 *
	 * @param rowBean the row's data.
	 * @return the id for the given row. Defaults to the row data.
	 */
	protected Object getRowId(final Object rowBean) {
		String rowIdProperty = getComponentModel().rowIdProperty;

		if (rowIdProperty == null || rowBean == null) {
			return rowBean;
		}

		try {
			return PropertyUtils.getProperty(rowBean, rowIdProperty);
		} catch (Exception e) {
			LOG.error("Failed to read row property \"" + rowIdProperty + "\" on " + rowBean, e);
			return rowBean;
		}
	}

	/**
	 * Retrieves the row contexts for all rows.
	 *
	 * @return A list containing a UIContext for each row. Will never return null, but it can return an empty list.
	 */
	public List<UIContext> getRowContexts() {
		List<?> beanList = this.getBeanList();
		List<UIContext> contexts = new ArrayList<>(beanList.size());

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);
			contexts.add(this.getRowContext(rowData, i));
		}

		return Collections.unmodifiableList(contexts);
	}

	/**
	 * Retrieves the repeat root, creating one if necessary.
	 *
	 * @return the repeat root.
	 */
	protected WRepeatRoot getRepeatRoot() {
		return getChildCount() == 0 ? null : (WRepeatRoot) getChildAt(0);
	}

	/**
	 * Retrieves the repeated component, that is used to render each row.
	 *
	 * @return the repeated component
	 */
	public WComponent getRepeatedComponent() {
		return getRepeatRoot().getRepeatedComponent();
	}

	/**
	 * Retrieves the repeated component for the specified row.
	 *
	 * @param row the row number.
	 * @return the repeated component for the specified row.
	 */
	public WComponent getRepeatedComponent(final int row) {
		return getRepeatRoot().getRepeatedComponent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getBeanList().size() + "x " + getRepeatedComponent().getClass().
				getSimpleName();
		return toString(text, -1, -1);
	}

	/**
	 * A naming context is only considered active if it has been set active via {@link #setNamingContext(boolean)} and
	 * also has an id name set via {@link #setIdName(String)}.
	 *
	 * @param context set true if this is a naming context.
	 */
	public void setNamingContext(final boolean context) {
		setFlag(ComponentModel.NAMING_CONTEXT_FLAG, context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamingContext() {
		return isFlagSet(ComponentModel.NAMING_CONTEXT_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamingContextId() {
		return getId();
	}

	/**
	 * Component to hold the repeated component and provide the correct row data.
	 */
	public static class WRepeatRoot extends WBeanComponent implements BeanProvider, Container,
			NamingContextable {

		/**
		 * Parent repeater.
		 */
		private final WRepeater repeater;

		/**
		 * Creates a WRepeatRoot.
		 *
		 * @param repeater the parent repeater
		 * @param repeatedComponent the component to repeat.
		 */
		public WRepeatRoot(final WRepeater repeater, final WComponent repeatedComponent) {
			this.repeater = repeater;
			if (repeatedComponent instanceof BeanProviderBound) {
				((BeanProviderBound) repeatedComponent).setBeanProvider(this);
			} else if (!(repeatedComponent instanceof DataBound) && !(repeatedComponent instanceof BeanBound)) {
				throw new SystemException(
						"The repeated component created by the factory must implement the "
						+ "BeanBound, BeanProviderBound or DataBound interface.");
			}

			this.add(repeatedComponent);
		}

		/**
		 * WRepeatRoot will act as a provider for a provider bound repeated component.
		 *
		 * @param beanProviderBound expected to be the repeated component.
		 * @return the Bean for the provider bound component.
		 */
		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			UIContext uic = UIContextHolder.getCurrent();

			if (uic instanceof SubUIContext) {
				return repeater.getRowBeanForSubcontext((SubUIContext) uic);
			} else {
				LOG.error("Unable to handle UIContext type: " + uic.getClass().getName());
				return null;
			}
		}

		/**
		 * @return the component which is being repeated.
		 */
		public WComponent getRepeatedComponent() {
			return getChildAt(0);
		}

		/**
		 * Override setData in order to push data to non-{@link BeanProviderBound} renderers each time the data is
		 * changed.
		 *
		 * @param rowData the data for this row.
		 */
		@Override
		public void setData(final Object rowData) {
			Object renderer = getRepeatedComponent();

			if (!(renderer instanceof BeanProviderBound)) {
				if (renderer instanceof BeanBound) {
					((BeanBound) renderer).setBean(rowData);
				} else {
					// Renderer *MUST* be databound
					((DataBound) renderer).setData(rowData);
				}
			}
		}

		/**
		 * Override WComponent internal id to include a row index.
		 *
		 * @return the internal id for this WComponent in the given context.
		 */
		@Override
		public String getInternalId() {
			UIContext uic = UIContextHolder.getCurrent();

			String repeaterId;
			String suffix;

			if (uic instanceof SubUIContext) {
				SubUIContext subContext = (SubUIContext) uic;
				UIContextHolder.pushContext(subContext.getParentContext());
				try {
					repeaterId = repeater.getInternalId();
					suffix = String.valueOf(subContext.getContextId());
				} finally {
					UIContextHolder.popContext();
				}
			} else {
				repeaterId = repeater.getInternalId();
				suffix = "";
			}

			StringBuffer iidBuf = new StringBuffer();
			iidBuf.append(repeaterId);
			iidBuf.append("r");
			iidBuf.append(suffix);
			return iidBuf.toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getId() {
			UIContext uic = UIContextHolder.getCurrent();

			// Parent Id
			String repeaterId = null;
			if (uic instanceof SubUIContext) {
				SubUIContext subContext = (SubUIContext) uic;
				UIContextHolder.pushContext(subContext.getParentContext());
				try {
					repeaterId = repeater.getId();
				} finally {
					UIContextHolder.popContext();
				}
			} else {
				repeaterId = repeater.getId();
			}

			// Row Suffix
			String suffix = getIdName();

			// Build Id
			StringBuffer id = new StringBuffer();
			id.append(repeaterId);
			id.append(ID_CONTEXT_SEPERATOR);
			id.append(suffix);

			return id.toString();
		}

		/**
		 * Override WComponent name to include a row index.
		 *
		 * @return the name for this WComponent in the given context.
		 */
		@Override
		public String getIdName() {
			UIContext uic = UIContextHolder.getCurrent();

			if (uic instanceof SubUIContext) {
				SubUIContext subContext = (SubUIContext) uic;
				StringBuffer nameBuf = new StringBuffer();
				nameBuf.append("r");
				nameBuf.append(subContext.getRowRenderId());
				return nameBuf.toString();
			}

			return "r";
		}

		/**
		 * WRepeatRoot is part of the tree, but we don't want any request handling or painting to be performed on it
		 * automatically.
		 *
		 * @return false
		 */
		@Override
		public boolean isVisible() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override // to make public
		public int getChildCount() {
			return super.getChildCount();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override // to make public
		public WComponent getChildAt(final int index) {
			return super.getChildAt(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override // to make public
		public List<WComponent> getChildren() {
			return super.getChildren();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override // to make public
		public int getIndexOfChild(final WComponent childComponent) {
			return super.getIndexOfChild(childComponent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isNamingContext() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNamingContextId() {
			return getId();
		}
	}

	/**
	 * The SubUIContext stores the state information for the repeated component hierarchy for a single row in the
	 * repeater.
	 *
	 * @author Martin Shevchenko
	 */
	public static class SubUIContext extends UIContextDelegate {

		/**
		 * The row index in the repeater.
		 */
		private int rowIndex = -1;
		/**
		 * The row render id to keep the ids unique for each sub context and process handle request correctly.
		 */
		private final String rowRenderId;
		/**
		 * The row bean in the repeater.
		 */
		private Object rowId;
		/**
		 * The repeater's repeat root.
		 */
		private final WRepeatRoot repeatRoot;
		/**
		 * The context id.
		 */
		private final int contextId;

		/**
		 * A map of Component models, keyed by the component which they belong to.
		 */
		private final Map<WebComponent, WebModel> componentModels = new HashMap<>();

		/**
		 * Creates a SubUIContext.
		 *
		 * @param repeater the repeater which this SubUIContext belongs to.
		 * @param contextId the context unique id
		 */
		public SubUIContext(final WRepeater repeater, final int contextId) {
			this(repeater, contextId, String.valueOf(contextId));
		}

		/**
		 * Creates a SubUIContext.
		 *
		 * @param repeater the repeater which this SubUIContext belongs to.
		 * @param contextId the context unique id
		 * @param rowRenderId the rows unique render id
		 */
		public SubUIContext(final WRepeater repeater, final int contextId, final String rowRenderId) {
			super(UIContextHolder.getCurrent());
			repeatRoot = repeater.getRepeatRoot();
			this.contextId = contextId;
			this.rowRenderId = rowRenderId;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void invokeLater(final Runnable runnable) {
			// Override to invoke the runnable against this context
			getBacking().invokeLater(this, runnable);
		}

		/**
		 * Retrieves the row id for this SubUIContext.
		 *
		 * @return the row id
		 */
		public Object getRowId() {
			return rowId;
		}

		/**
		 * @return the parent UIContext.
		 */
		protected UIContext getParentContext() {
			return getBacking();
		}

		/**
		 * Sets the row index, in the case that the repeater has rows added/removed/moved.
		 *
		 * @param rowIndex the new row index for this sub context.
		 */
		protected void setRowIndex(final int rowIndex) {
			this.rowIndex = rowIndex;
		}

		/**
		 * Sets the row bean, in the case that the repeater has rows added/removed/moved.
		 *
		 * @param rowId the row id for this sub context.
		 */
		protected void setRowId(final Object rowId) {
			this.rowId = rowId;
		}

		/**
		 * Retrieves the row index for this SubUIContext.
		 *
		 * @return the row index, or -1 if uninitialised.
		 */
		public int getRowIndex() {
			return rowIndex;
		}

		/**
		 * @return the row render id to keep each row unique so that handle request processing is handled correctly.
		 */
		public String getRowRenderId() {
			return rowRenderId;
		}

		/**
		 * @return the context unique id
		 */
		public int getContextId() {
			return contextId;
		}

		/**
		 * Retrieves the component model for the given component.
		 *
		 * If the component is not being repeated by the WRepeater, the parent context will be queried for the model.
		 *
		 * @param component the component to retrieve the model for.
		 * @return the component model for the given component.
		 */
		@Override
		public WebModel getModel(final WebComponent component) {
			WebModel model = componentModels.get(component);

			if (model == null) {
				// Try the parent.
				model = getParentContext().getModel(component);
			}

			return model;
		}

		/**
		 * Sets the component model for the given component.
		 *
		 * If the component is not being repeated by the WRepeater, the parent context will be given the model.
		 *
		 * @param component the component to set the model for.
		 * @param model the component model for the given component.
		 */
		@Override
		public void setModel(final WebComponent component, final WebModel model) {
			// Need to figure out which context should store the model.
			if (component instanceof WRepeatRoot) {
				// The repeat root must always be stored locally.
				componentModels.put(component, model);
			} else if (isInContext(component)) {
				// The component that we are being asked to store a model for
				// is inside a repeater and is from this context.
				componentModels.put(component, model);
			} else {
				// The component is not from this context, so ask the parent
				// context to store it.
				getParentContext().setModel(component, model);
			}
		}

		/**
		 * Indicates whether the given component is inside this context's repeatRoot.
		 *
		 * @param component the component to check
		 * @return true if the given component belongs in this context, false otherwise.
		 */
		protected boolean isInContext(final WebComponent component) {

			if (component instanceof WRepeatRoot) {
				if (component == this.repeatRoot) {
					return false;
				}
			} else if (component instanceof WRepeater) {
				WRepeatRoot root = ((WRepeater) component).getRepeatRoot();
				if (root == this.repeatRoot) {
					return false;
				}
			}

			UIContextHolder.pushContext(this);
			WRepeatRoot root = null;

			try {
				root = WebUtilities.getAncestorOfClass(WRepeatRoot.class, (WComponent) component);
			} finally {
				UIContextHolder.popContext();
			}

			return root != null && root == this.repeatRoot;
		}

		/**
		 * Removes the component model for the given component.
		 *
		 * If the component is not being repeated by the WRepeater, the parent context will be asked to remove the
		 * model.
		 *
		 * @param component the component to remove the model for.
		 */
		@Override
		public void removeModel(final WebComponent component) {
			if (componentModels.remove(component) == null) {
				// Not from this context, better try the parent context.
				getParentContext().removeModel(component);
			}
		}

		/**
		 * @return the components which have models in this SubUIContext.
		 */
		@Override
		public Set getComponents() {
			return componentModels.keySet();
		}

		/**
		 * Throws an UnsupportedOperationException, as the environment can not be set on a SubUIContext.
		 *
		 * @param environment the environment to set.
		 */
		@Override
		public void setEnvironment(final Environment environment) {
			throw new UnsupportedOperationException("Cannot set environment on SubUIContext");
		}

		/**
		 * Retrieves the scratch map with phase scope for the given component.
		 *
		 * The scratch map is stored under one further level of indirection; by this sub-ui context. This allows each
		 * row to have its own scratch map.
		 *
		 * @param component the component to retrieve the scratch map for.
		 * @return the scratch map for the given component.
		 */
		@Override
		public Map getScratchMap(final WComponent component) {
			if (isInContext(component)) {
				Map sharedScratchMap = getParentContext().getScratchMap(component);
				Map map = (Map) sharedScratchMap.get(this);

				if (map == null) {
					map = new HashMap();
					sharedScratchMap.put(this, map);
				}

				return map;
			} else {
				return getParentContext().getScratchMap(component);
			}
		}

		/**
		 * Clears the scratch map with phase scope for the given component.
		 *
		 * @param component the component to clear the scratch map for.
		 */
		@Override
		public void clearScratchMap(final WComponent component) {
			if (isInContext(component)) {
				Map sharedScratchMap = getParentContext().getScratchMap(component);
				sharedScratchMap.remove(this);
			} else {
				getParentContext().clearScratchMap(component);
			}
		}

		/**
		 * Doesn't do anything - the real UI Context will clear the scratch map.
		 */
		@Override
		public void clearScratchMap() {
			// Don't do anything - the real UI Context will clear it out
		}

		/**
		 * Retrieves the scratch map with request scope for the given component.
		 *
		 * The scratch map is stored under one further level of indirection; by this sub-ui context. This allows each
		 * row to have its own scratch map.
		 *
		 * @param component the component to retrieve the scratch map for.
		 * @return the scratch map for the given component.
		 */
		@Override
		public Map getRequestScratchMap(final WComponent component) {
			if (isInContext(component)) {
				Map sharedScratchMap = getParentContext().getRequestScratchMap(component);
				Map map = (Map) sharedScratchMap.get(this);

				if (map == null) {
					map = new HashMap();
					sharedScratchMap.put(this, map);
				}

				return map;
			} else {
				return getParentContext().getRequestScratchMap(component);
			}
		}

		/**
		 * Clears the scratch map for the given component.
		 *
		 * @param component the component to clear the scratch map for.
		 */
		@Override
		public void clearRequestScratchMap(final WComponent component) {
			if (isInContext(component)) {
				Map sharedScratchMap = getParentContext().getRequestScratchMap(component);
				sharedScratchMap.remove(this);
			} else {
				getParentContext().clearRequestScratchMap(component);
			}
		}

		/**
		 * Doesn't do anything - the real UI Context will clear the scratch map.
		 */
		@Override
		public void clearRequestScratchMap() {
			// Don't do anything - the real UI Context will clear it out
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setFocussed(final WComponent component) {
			super.setFocussed(component, this);
		}
	}
}
