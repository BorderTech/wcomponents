package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.ComparableComparator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple table data model that is bound to a list of beans.
 * <p>
 * The simplest use of this model is to define the column bean properties for a list of beans that is not expandable,
 * via the {@link #SimpleBeanBoundTableModel(String[])} constructor.
 * </p>
 * <p>
 * The model also allows tree like structures (ie expandable rows) to be defined by using {@link LevelDetails}. Each
 * {@link LevelDetails} determines which "beanProperty" will be used to expand the next level. The defined
 * "beanProperty" is usually another list of beans. There are different helper constructors that will help define these
 * levels based on the bean properties passed in.
 * </p>
 * <p>
 * If the data is hierarchic, then the one {@link LevelDetails} can be defined, and the
 * {@link #setIterateFirstLevel(boolean)} can be set to true. The model will then iterate this level down the bean list
 * using the level's bean property. The maximum number of iterations can be set via {@link #setMaxIterations(int)}.
 * </p>
 * <p>
 * The model supports sorting. A comparator for a particular column can be added via
 * {@link #setComparator(int, Comparator)}.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimpleBeanBoundTableModel extends AbstractBeanBoundTableModel {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SimpleBeanBoundTableModel.class);

	/**
	 * A simple comparator that compares comparables, for use in sorting e.g. columns containing Strings.
	 */
	public static final ComparableComparator COMPARABLE_COMPARATOR = new ComparableComparator();

	/**
	 * The comparators used for sorting, keyed by column index.
	 */
	private Map<Integer, Comparator<Object>> comparators;

	/**
	 * Indicates whether this model is globally editable.
	 */
	private boolean editable;

	/**
	 * Indicates whether rows are globally selectable.
	 */
	private boolean selectable;

	/**
	 * Defined levels.
	 */
	private final List<LevelDetails> levels = new ArrayList<>();

	/**
	 * Iterate on the first expandable level.
	 */
	private boolean iterateFirstLevel;

	/**
	 * Maximum iterations.
	 */
	private int maxIterations = -1;

	/**
	 * Define the column bean properties for the top level.
	 *
	 * @param columnBeanProperties the column bean properties
	 */
	public SimpleBeanBoundTableModel(final String[] columnBeanProperties) {
		if (columnBeanProperties == null || columnBeanProperties.length == 0) {
			throw new IllegalArgumentException("ColumnBeanProperties must be provided");
		}

		// Top Level
		this.levels.add(new LevelDetails(".", columnBeanProperties));
	}

	/**
	 * Define the column bean properties for the top level along with the bean property of the first expandable level.
	 * <p>
	 * The expandable level will use the same column bean properties as the top level.
	 * </p>
	 * <p>
	 * To iterate down multiple levels on this bean property, set {@link #setIterateFirstLevel(boolean)} to true.
	 * </p>
	 *
	 * @param columnBeanProperties the column bean properties
	 * @param levelBeanProperty the bean property for the expandable level
	 */
	public SimpleBeanBoundTableModel(final String[] columnBeanProperties,
			final String levelBeanProperty) {
		this(columnBeanProperties, new LevelDetails(levelBeanProperty, columnBeanProperties));
	}

	/**
	 * Define the column bean properties for the top level along with the bean properties and column bean properties of
	 * the expandable levels.
	 *
	 * @param columnBeanProperties the top level column bean properties
	 * @param levelBeanProperties the bean properties for the expandable levels
	 * @param levelColumnBeanProperties the column bean properties for the expandable levels
	 */
	public SimpleBeanBoundTableModel(final String[] columnBeanProperties,
			final String[] levelBeanProperties,
			final String[][] levelColumnBeanProperties) {
		this(columnBeanProperties);
		if (levelBeanProperties == null || levelBeanProperties.length == 0) {
			throw new IllegalArgumentException("levelBeanProperties must be provided");
		}
		if (levelColumnBeanProperties == null || levelColumnBeanProperties.length == 0) {
			throw new IllegalArgumentException("levelColumnBeanProperties must be provided");
		}
		if (levelColumnBeanProperties.length != levelBeanProperties.length) {
			throw new IllegalArgumentException("level details must have the same length");
		}

		for (int i = 0; i < levelBeanProperties.length; i++) {
			LevelDetails level = new LevelDetails(levelBeanProperties[i],
					levelColumnBeanProperties[i]);
			this.levels.add(level);
		}
	}

	/**
	 * Define the column bean properties for the top level along with the expandable levels.
	 *
	 * @param columnBeanProperties the top level column bean properties
	 * @param levels the expandable levels
	 */
	public SimpleBeanBoundTableModel(final String[] columnBeanProperties,
			final LevelDetails... levels) {
		this(columnBeanProperties);

		// Expandable Levels
		for (LevelDetails level : levels) {
			if (level != null) {
				this.levels.add(level);
			}
		}

	}

	/**
	 * @param row ignored
	 * @return true if expandable levels defined
	 */
	@Override
	public boolean isExpandable(final List<Integer> row) {
		return levels.size() > 1;
	}

	/**
	 * @param row ignored
	 * @return true if the model is globally selectable, otherwise false
	 */
	@Override
	public boolean isSelectable(final List<Integer> row) {
		return selectable;
	}

	/**
	 * Sets whether the rows are globally selectable.
	 *
	 * @param selectable true if the rows are globally selectable, otherwise false
	 */
	public void setSelectable(final boolean selectable) {
		this.selectable = selectable;
	}

	/**
	 * @return the maximum iterations on the first level. -1 indicates no limit.
	 */
	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * @param maxIterations the maximum iterations on the first level. -1 indicates no limit.
	 */
	public void setMaxIterations(final int maxIterations) {
		this.maxIterations = maxIterations;
	}

	/**
	 * @return returns true if iterate on the first level
	 */
	public boolean isIterateFirstLevel() {
		return iterateFirstLevel;
	}

	/**
	 * @param iterateFirstLevel true if iterate on first level
	 */
	public void setIterateFirstLevel(final boolean iterateFirstLevel) {
		this.iterateFirstLevel = iterateFirstLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSortable(final int col) {
		return comparators != null && comparators.containsKey(col);
	}

	/**
	 * Sets the comparator for the given column, to enable sorting.
	 *
	 * @param col the column to set the comparator on.
	 * @param comparator the comparator to set.
	 */
	public void setComparator(final int col, final Comparator comparator) {
		synchronized (this) {
			if (comparators == null) {
				comparators = new HashMap<>();
			}
		}

		if (comparator == null) {
			comparators.remove(col);
		} else {
			comparators.put(col, comparator);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final List<Integer> row, final int col) {
		Object rowBean = getRowBean(row);
		if (rowBean == null) {
			return null;
		}

		// Row has renderer
		if (col == -1) {
			return rowBean;
		}

		int lvlIndex = getLevelIndex(row);
		LevelDetails level = levels.get(lvlIndex);

		if (col >= level.getColumnBeanProperties().length) {
			LOG.warn("Requested a col [" + col + "] that is not defined for level with ["
					+ level.getColumnBeanProperties().length + "] columns");
			return null;
		}

		String property = level.getColumnBeanProperties()[col];

		Object value = getBeanPropertyValue(property, rowBean);
		return value;
	}

	/**
	 * Indicates whether the given cell is editable. This model only supports editability at a global level. See
	 * {@link #setEditable(boolean)}.
	 *
	 * @param row ignored.
	 * @param col ignored.
	 * @return true if the given cell is editable, false otherwise.
	 */
	@Override
	public boolean isCellEditable(final List<Integer> row, final int col) {
		return editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValueAt(final Object value, final List<Integer> row, final int col) {
		if (!isEditable()) {
			throw new IllegalStateException("Attempted to set a value on an uneditable model");
		}

		Object rowBean = getRowBean(row);
		if (rowBean == null) {
			return;
		}

		int lvlIndex = getLevelIndex(row);
		LevelDetails level = levels.get(lvlIndex);

		if (col >= level.getColumnBeanProperties().length) {
			LOG.warn("Requested a col [" + col + "] that is not defined for level with ["
					+ level.getColumnBeanProperties().length + "] columns");
			return;
		}

		String property = level.getColumnBeanProperties()[col];

		setBeanPropertyValue(property, rowBean, (Serializable) value);
	}

	/**
	 * Indicates whether the data in this model is editable.
	 *
	 * @return true if the data in this model is editable, false otherwise.
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets whether the data in this model is editable. By default, the data is not editable.
	 *
	 * @param editable true if the data is editable, false if it is read-only.
	 */
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] sort(final int col, final boolean ascending) {
		if (!isSortable(col)) {
			throw new IllegalStateException(
					"Attempted to sort on column " + col + ", which is not sortable");
		}

		return sort(comparators.get(col), col, ascending);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		List<?> root = getBeanList();
		return root == null ? 0 : root.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildCount(final List<Integer> row) {
		// Should not occur
		if (row == null || row.isEmpty()) {
			return 0;
		}

		// No expandable levels defined, so always 0
		if (levels.size() == 1) {
			return 0;
		}

		// Check iterations
		if (isIterateFirstLevel() && getMaxIterations() > -1 && row.size() > getMaxIterations()) {
			// Reached limit of iterations
			return 0;
		}

		// Index for current level
		int lvlIndex = getLevelIndex(row);

		// Check there is a Next Level
		int nxtIdx = isIterateFirstLevel() ? 1 : lvlIndex + 1;
		if (nxtIdx >= levels.size()) {
			return 0;
		}
		LevelDetails nxtLevel = levels.get(nxtIdx);

		// Get row bean
		Object rowBean = getRowBean(row);
		if (rowBean == null) {
			return 0;
		}

		// Data for next level
		Object lvlData = getBeanPropertyValue(nxtLevel.getLevelBeanProperty(), rowBean);
		if (lvlData == null) {
			return 0;
		}

		// If row per item, then count children
		if (nxtLevel.isRowPerListItem()) {
			return getSize(lvlData);
		} else {
			// One child
			return 1;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends WComponent> getRendererClass(final List<Integer> row) {
		int idx = getLevelIndex(row);
		LevelDetails level = levels.get(idx);
		return level.getRenderer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getRowKey(final List<Integer> row) {
		Object bean = getRowBean(row);
		return bean;
	}

	/**
	 * @param row the row index
	 * @return the bean for this row
	 */
	protected Object getRowBean(final List<Integer> row) {
		// Should not happen
		if (row == null || row.isEmpty()) {
			return null;
		}

		// Get root bean (ie top level)
		Object rootData = getTopRowBean(row);
		if (row.size() == 1) {
			return rootData;
		}

		// Build bean util request
		String util = buildBeanUtilString(row);

		// Get value
		Object value = getBeanPropertyValue(util, rootData);

		return value;
	}

	/**
	 * Return the top level bean for this row index.
	 *
	 * @param row the row index
	 * @return the root row bean (ie top level) for this index
	 */
	protected Object getTopRowBean(final List<Integer> row) {
		// Get root level
		List<?> lvl = getBeanList();
		if (lvl == null || lvl.isEmpty()) {
			return null;
		}

		// Get root row bean (ie top level)
		int rowIdx = row.get(0);
		Object rowData = lvl.get(rowIdx);
		return rowData;
	}

	/**
	 * @param row the row index
	 * @return the bean util syntax string to retrieve the row bean
	 */
	protected String buildBeanUtilString(final List<Integer> row) {
		StringBuffer cmd = new StringBuffer();

		boolean append = false;

		for (int i = 1; i < row.size(); i++) {
			int idx = isIterateFirstLevel() ? 1 : i;
			LevelDetails level = levels.get(idx);
			if (append) {
				cmd.append('.');
			}
			cmd.append(level.getLevelBeanProperty());
			if (level.isRowPerListItem()) {
				int rowIdx = row.get(i);
				cmd.append('[');
				cmd.append(rowIdx);
				cmd.append(']');
			}
			append = true;
			// Level is not a row per list item or has it own renderer
			if (!level.isRowPerListItem() || level.getRenderer() != null) {
				// Cant have children
				if (i != row.size() - 1) {
					throw new IllegalStateException(
							"Invalid row index as it was trying to access children for a level that cannot have children");
				}
				break;
			}
		}

		return cmd.toString();
	}

	/**
	 * @return the bean data as a list
	 */
	public List<?> getBeanList() {
		return (List<?>) super.getBeanValue();
	}

	/**
	 * Get the bean property value.
	 *
	 * @param property the bean property
	 * @param bean the bean
	 * @return the bean property value
	 */
	protected Object getBeanPropertyValue(final String property, final Object bean) {
		if (bean == null) {
			return null;
		}

		if (".".equals(property)) {
			return bean;
		}

		try {
			Object data = PropertyUtils.getProperty(bean, property);
			return data;
		} catch (Exception e) {
			LOG.error("Failed to get bean property " + property + " on " + bean, e);
			return null;
		}
	}

	/**
	 * Set the bean property value.
	 *
	 * @param property the bean property
	 * @param bean the bean
	 * @param value the value to set
	 */
	protected void setBeanPropertyValue(final String property, final Object bean,
			final Serializable value) {
		if (bean == null) {
			return;
		}

		if (".".equals(property)) {
			LOG.error("Set of entire bean is not supported by this model");
			return;
		}
		try {
			PropertyUtils.setProperty(bean, property, value);
		} catch (Exception e) {
			LOG.error("Failed to set bean property " + property + " on " + bean, e);
		}
	}

	/**
	 * @param data the array or list of beans
	 * @return the number of beans (ie size)
	 */
	protected int getSize(final Object data) {
		if (data == null) {
			return 0;
		}

		// Array
		if (data instanceof Object[]) {
			return ((Object[]) data).length;
		} else if (data instanceof List<?>) { // List
			return ((List<?>) data).size();
		}

		return 0;
	}

	/**
	 * @param row the row index
	 * @return the index of the level
	 */
	protected int getLevelIndex(final List<Integer> row) {
		int idx = isIterateFirstLevel() && row.size() > 1 ? 1 : row.size() - 1;
		return idx;
	}

	/**
	 * The details of an expandable level. A level can either have "columns" or a "renderer".
	 * <p>
	 * For levels that have a "renderer", if the data for the level is a list, then a row can be rendered for "each"
	 * item in the list. However, if the data is not a list or the renderer will handle rendering the "list" then the
	 * rowPerListItem flag needs to be set to false via {@link LevelDetails#LevelDetails(String, Class, boolean)}
	 * constructor.
	 * </p>
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public static final class LevelDetails implements Serializable {

		/**
		 * The bean property for this level's data (usually a list of beans).
		 */
		private final String levelBeanProperty;
		/**
		 * The column bean properties for the level.
		 */
		private final String[] columnBeanProperties;
		/**
		 * The custom renderer for this row.
		 */
		private final Class<? extends WComponent> renderer;
		/**
		 * Indicate if the level has as a row per item in the list.
		 */
		private final boolean rowPerListItem;

		/**
		 * @param levelBeanProperty the bean property for this level's data (usually a list of beans)
		 * @param columnBeanProperties the column bean properties for the level
		 */
		public LevelDetails(final String levelBeanProperty, final String[] columnBeanProperties) {
			this.levelBeanProperty = levelBeanProperty;
			this.columnBeanProperties = columnBeanProperties;
			this.rowPerListItem = true;
			this.renderer = null;
		}

		/**
		 * @param levelBeanProperty the bean property for this level's data (usually a list of beans)
		 * @param renderer the custom renderer for this level
		 */
		public LevelDetails(final String levelBeanProperty,
				final Class<? extends WComponent> renderer) {
			this(levelBeanProperty, renderer, true);
		}

		/**
		 * @param levelBeanProperty the bean property for this level's data (usually a list of beans)
		 * @param renderer the custom renderer for this level
		 * @param rowPerListItem true if row per item in list, otherwise false
		 */
		public LevelDetails(final String levelBeanProperty,
				final Class<? extends WComponent> renderer,
				final boolean rowPerListItem) {
			this.levelBeanProperty = levelBeanProperty;
			this.renderer = renderer;
			this.rowPerListItem = rowPerListItem;
			this.columnBeanProperties = null;
		}

		/**
		 * @return the bean property for this level's data (usually a list of beans)
		 */
		public String getLevelBeanProperty() {
			return levelBeanProperty;
		}

		/**
		 * @return the custom renderer for this level, null if not defined.
		 */
		public Class<? extends WComponent> getRenderer() {
			return renderer;
		}

		/**
		 * For levels that have a "renderer", if the data for the level is a list, then a row can be rendered for "each"
		 * item in the list. However, if the data is not a list or the renderer will handle rendering the "list" then
		 * the rowPerListItem will be false.
		 *
		 * @return true if row per item in list, otherwise false
		 */
		public boolean isRowPerListItem() {
			return rowPerListItem;
		}

		/**
		 * @return the column bean properties for the level, or null if the level has a custom renderer
		 */
		public String[] getColumnBeanProperties() {
			return columnBeanProperties;
		}

	}

}
