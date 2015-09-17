package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.ComparableComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple tree based table data model which that takes in root node and an array of bean properties in its
 * constructor. Note that use of this data model is discouraged, as the table data will be stored in the user's session.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link SimpleBeanBoundTableModel} instead.
 */
@Deprecated
public class SimpleBeanTreeTableDataModel extends AbstractTreeTableDataModel {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SimpleBeanTreeTableDataModel.class);

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
	 * The bean properties for each column.
	 */
	private final String[] properties;

	/**
	 * Creates a SimpleBeanBasedTableDataModel containing the given data.
	 *
	 * @param properties the bean properties for each column.
	 * @param root the tree root node.
	 */
	public SimpleBeanTreeTableDataModel(final String[] properties, final TableTreeNode root) {
		super(root);
		this.properties = properties;
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

		if (comparator != null) {
			comparators.put(col, comparator);
		} else {
			comparators.remove(col);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final TableTreeNode row, final int col) {
		Object bean = row.getData();
		String property = properties[col];

		if (bean != null) {
			if (".".equals(property)) {
				return bean;
			} else {
				try {
					return PropertyUtils.getProperty(bean, property);
				} catch (Exception e) {
					LOG.error("Failed to read bean property " + property + " from " + bean, e);
				}
			}
		}

		return null;
	}

	/**
	 * Indicates whether the given cell is editable. This model only supports editability at a global level. See
	 * {@link #setEditable(boolean)}.
	 *
	 * @param row ignored.
	 * @param col ignored.
	 * @return true if the table is globally editable, false otherwise.
	 */
	@Override
	public boolean isCellEditable(final int row, final int col) {
		return editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValueAt(final Object value, final int row, final int col) {
		if (!editable) {
			throw new IllegalStateException("Attempted to set a value on an uneditable model");
		}

		TableTreeNode node = getNodeAtLine(row);
		Object bean = node.getData();
		String property = properties[col];

		if (bean != null) {
			if (".".equals(property)) {
				LOG.error("Set of entire bean is not supported by this model");
				//node.setData(value);
			} else {
				try {
					PropertyUtils.setProperty(bean, property, value);
				} catch (Exception e) {
					LOG.error("Failed to set bean property " + property + " on " + bean, e);
				}
			}
		}
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
			throw new IllegalStateException("Attempted to sort on column "
					+ col + ", which is not sortable");
		}

		// Obtains the list of top level nodes, sorts them & re-add them in order
		TableTreeNode root = getRootNode();
		List<TableTreeNode> topLevelNodes = new ArrayList<>(root.getChildCount());

		for (int i = 0; i < root.getChildCount(); i++) {
			topLevelNodes.add((TableTreeNode) root.getChildAt(i));
		}

		Comparator<TableTreeNode> comp = new Comparator<TableTreeNode>() {
			@Override
			public int compare(final TableTreeNode obj1, final TableTreeNode obj2) {
				Comparator backing = comparators.get(col);
				return backing.compare(obj1.getData(), obj2.getData());
			}
		};

		if (ascending) {
			Collections.sort(topLevelNodes, comp);
		} else {
			Collections.sort(topLevelNodes, Collections.reverseOrder(comp));
		}

		root.removeAll();

		for (TableTreeNode node : topLevelNodes) {
			root.add(node);
		}

		return null;
	}
}
