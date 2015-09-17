package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.util.Util;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WDataTableRowRenderer is used by the table's repeater to render row data. This class is intended for internal use
 * only.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link WTableRowRenderer} instead.
 */
@Deprecated
public final class WDataTableRowRenderer extends WDataRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WDataTableRowRenderer.class);

	/**
	 * The table that this renderer belongs to.
	 */
	private final WDataTable table;

	/**
	 * The row header.
	 */
	private final WDecoratedLabel rowHeader = new WDecoratedLabel(new WText() {
		@Override
		public String getData() {
			UIContext uic = UIContextHolder.getCurrent();
			int row = getRowIndex((SubUIContext) uic);
			return table.getDataModel().getRowHeader(row);
		}
	});

	/**
	 * A Map of expanded renderers.
	 */
	private final Map<Class<? extends WComponent>, WComponent> expandedRenderers = new HashMap<>();

	/**
	 * Creates a WDataTableRowRenderer.
	 *
	 * @param table the table that this renderer belongs to.
	 */
	protected WDataTableRowRenderer(final WDataTable table) {
		this.table = table;
		add(rowHeader);
	}

	/**
	 * Retrieves the component that is used to render the given column.
	 *
	 * @param columnIndex the column index.
	 * @return the component used to render the given column.
	 */
	public WComponent getRenderer(final int columnIndex) {
		int childIndex = columnIndex + 1; // the first child is the row header
		return getChildAt(childIndex);
	}

	/**
	 * @return the table that this row renderer belongs to.
	 */
	public WDataTable getTable() {
		return table;
	}

	/**
	 * @return the row header.
	 */
	public WDecoratedLabel getRowHeader() {
		return rowHeader;
	}

	/**
	 * <p>
	 * The preparePaintComponent method has been overridden to ensure that expanded row renderers have been correctly
	 * initialised.</p>
	 *
	 * <p>
	 * Expanded row renderers are lazily instantiated and added to the shared structure as needed. This means for the
	 * first use of a renderer, it will not have been part of the WComponent tree, and would not have had its
	 * preparePaintComponent called. We therefore add the renderer to the tree here, and manually call its
	 * preparePaint.</p>
	 *
	 * @param request the Request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		Class<? extends WComponent> rowRendererClass = getRowRendererClass();

		if (rowRendererClass != null && !expandedRenderers.containsKey(rowRendererClass)) {
			getExpandedTreeNodeRenderer(rowRendererClass).preparePaint(request);
		}
	}

	/**
	 * <p>
	 * This is called to lazily add expanded renderers as necessary. To save memory, only one instance of a renderer
	 * class is ever added to the row renderer instance. The RendererWrapper ensures that data binding occurs at the
	 * right time.</p>
	 *
	 * @param rendererClass the renderer class.
	 * @return the expanded renderer for the given row.
	 */
	public WComponent getExpandedTreeNodeRenderer(final Class<? extends WComponent> rendererClass) {
		if (rendererClass == null) {
			return null;
		}

		// If we already have an additional renderer for this class, return it.
		// This is synchronized, as it updates the shared model using locking/unlocking, which is a bit dodgy.
		synchronized (expandedRenderers) {
			WComponent renderer = expandedRenderers.get(rendererClass);

			if (renderer != null) {
				return renderer;
			}

			// Not found, create a new instance of the given class
			renderer = new RendererWrapper(this, rendererClass, -1);
			expandedRenderers.put(rendererClass, renderer);
			setLocked(false);
			add(renderer);
			setLocked(true);

			return renderer;
		}
	}

	/**
	 * @param uic the SubUIContext to determine the row index for
	 * @return the row index for the specified SubUIContext.
	 */
	private int getRowIndex(final SubUIContext uic) {
		// SubUIContext does not cater for pagination
		return (Integer) table.getRepeater().getBeanList().get(uic.getRowIndex());
	}

	/**
	 * Retrieves the renderer class for a given row.
	 *
	 * @return the renderer class for a given row (if it's an expanded row), otherwise null.
	 */
	private Class<? extends WComponent> getRowRendererClass() {
		UIContext uic = UIContextHolder.getCurrent();

		if (table.getDataModel() instanceof TreeTableDataModel && uic instanceof SubUIContext) {
			int row = getRowIndex((SubUIContext) uic);
			TreeTableDataModel dataModel = (TreeTableDataModel) table.getDataModel();

			TableTreeNode node = dataModel.getNodeAtLine(row);
			return node.getRendererClass();
		}

		return null;
	}

	/**
	 * The renderer wrapper is responsible for ensuring that the renderer is only used when needed (ie. it is only
	 * involved in processing of certain rows), and ensuring that data is passed to / from the renderer when required.
	 */
	private static final class RendererWrapper extends WBeanContainer implements BeanProvider {

		/**
		 * The table that this wrapper belongs to.
		 */
		private final WDataTableRowRenderer rowRenderer;

		/**
		 * The actual renderer used to render the column/expanded content.
		 */
		private final WComponent renderer;

		/**
		 * The index of the column which this wrapper renders, or -1 if this is an expended renderer.
		 */
		private final int columnIndex;

		/**
		 * Creates a RendererWrapper.
		 *
		 * @param rowRenderer the row renderer.
		 * @param rendererClass the column/expanded content renderer class.
		 * @param columnIndex the index of the column renderer, or -1 if the renderer is for expanded content.
		 */
		private RendererWrapper(final WDataTableRowRenderer rowRenderer,
				final Class<? extends WComponent> rendererClass, final int columnIndex) {
			this.rowRenderer = rowRenderer;
			this.columnIndex = columnIndex;
			WComponent rendererComponent = null;

			// Not found, create a new instance of the given class
			try {
				rendererComponent = rendererClass.newInstance();

				if (rendererComponent instanceof BeanProviderBound) {
					((BeanProviderBound) rendererComponent).setBeanProvider(this);
				}

				add(rendererComponent);
			} catch (Exception e) {
				LOG.error("Failed to instantiate renderer: " + rendererClass.getName(), e);
				setVisible(false);
			}

			renderer = rendererComponent;
		}

		/**
		 * Creates a RendererWrapper.
		 *
		 * @param rowRenderer the row renderer.
		 * @param renderer the column/expanded content renderer component.
		 * @param columnIndex the index of the column renderer, or -1 if the renderer is for expanded content.
		 */
		private RendererWrapper(final WDataTableRowRenderer rowRenderer, final WComponent renderer,
				final int columnIndex) {
			this.rowRenderer = rowRenderer;
			this.columnIndex = columnIndex;
			this.renderer = renderer;

			if (renderer instanceof BeanProviderBound) {
				((BeanProviderBound) renderer).setBeanProvider(this);
			}

			add(renderer);
		}

		/**
		 * Column Renderers are invisible for expanded rows with an expanded renderer set.
		 *
		 * @return true if this component is visible, false if invisible.
		 */
		@Override
		public boolean isVisible() {
			if (!super.isVisible()) {
				return false;
			}

			Class<? extends WComponent> rendererClass = rowRenderer.getRowRendererClass();

			if (columnIndex >= 0) {
				// column renderer
				return rendererClass == null;
			} else {
				// expanded content renderer
				return renderer != null && Util.equals(renderer.getClass(), rendererClass);
			}
		}

		/**
		 * Provides data to a component rendering a column.
		 *
		 * @param beanProviderBound the component rendering the column.
		 * @return a bean value for component that is rendering the specified row/column
		 */
		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			UIContext uic = UIContextHolder.getCurrent();

			// Make sure we have the correct uic
			while (uic instanceof SubUIContext && !((SubUIContext) uic).isInContext(
					(WComponent) beanProviderBound)) {
				uic = ((SubUIContext) uic).getParentContext();
			}

			if (!(uic instanceof SubUIContext)) {
				LOG.error("Unable to handle UIContext type: " + uic.getClass().getName());
				return null;
			}

			int row = rowRenderer.getRowIndex((SubUIContext) uic);

			if (columnIndex < 0) {
				// This is not a column renderer, it must be an additional row renderer
				TreeTableDataModel dataModel = (TreeTableDataModel) rowRenderer.table.getDataModel();
				return dataModel.getNodeAtLine(row).getData();
			} else {
				return rowRenderer.table.getDataModel().getValueAt(row, columnIndex);
			}
		}

		/**
		 * Some renderers may not be bean provider bound, or not bean-aware. We need to make sure that the data is set
		 * correctly for these columns.
		 *
		 * @param request the request being responded to.
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);

			TableDataModel model = rowRenderer.table.getDataModel();
			UIContext uic = UIContextHolder.getCurrent();
			int row = rowRenderer.getRowIndex((SubUIContext) uic);

			// Update input read-only status
			if (renderer instanceof Input) {
				((Input) renderer).setReadOnly(!model.isCellEditable(row, columnIndex));
			}

			if (!(renderer instanceof BeanProviderBound)) {
				Object bean = null;

				if (columnIndex >= 0) {
					bean = model.getValueAt(row, columnIndex);
				} else if (model instanceof TreeTableDataModel) {
					TreeTableDataModel treeModel = (TreeTableDataModel) model;
					bean = treeModel.getNodeAtLine(row).getData();
				}

				if (renderer instanceof BeanBound) {
					((BeanBound) renderer).setBean(bean);
				} else if (renderer instanceof DataBound) {
					((DataBound) renderer).setData(bean);
				}
			}
		}
	}

	/**
	 * Adds a column to the renderer. This method is called by {@link WDataTable} to keep the renderer's and table's
	 * columns in sync.
	 *
	 * @param column the column to add.
	 * @param columnIndex the index of the column.
	 */
	void addColumn(final WTableColumn column, final int columnIndex) {
		WComponent renderer = column.getRenderer();

		if (renderer == null) {
			Class<? extends WComponent> rendererClass = column.getRendererClass();

			if (!(BeanProviderBound.class.isAssignableFrom(rendererClass))
					&& !(BeanBound.class.isAssignableFrom(rendererClass))
					&& !(DataBound.class.isAssignableFrom(rendererClass))) {
				throw new IllegalArgumentException(
						"Column renderers must be BeanProvider-, Bean- or Data-Bound");
			}

			add(new RendererWrapper(this, rendererClass, columnIndex));
		} else {
			if (!(renderer instanceof BeanProviderBound)
					&& !(renderer instanceof BeanBound)
					&& !(renderer instanceof DataBound)) {
				throw new IllegalArgumentException(
						"Column renderers must be BeanProvider-, Bean- or Data-Bound");
			}

			add(new RendererWrapper(this, renderer, columnIndex));
		}
	}
}
