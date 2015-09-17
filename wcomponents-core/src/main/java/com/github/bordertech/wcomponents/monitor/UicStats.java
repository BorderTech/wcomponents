package com.github.bordertech.wcomponents.monitor;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * UicStats provides methods for analysing a {@link UIContext}.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class UicStats {

	/**
	 * The UIContext to analyse.
	 */
	private final UIContext uic;

	/**
	 * The overall serialized size, which contributes to the user session.
	 */
	private final int overallSerializedSize;

	/**
	 * Statistics by WComponent tree.
	 */
	private final Map<WComponent, Map<WComponent, Stat>> statsByWCTree = new HashMap<>();

	private Set<WComponent> rootWCsDetected;

	/**
	 * Creates a UicStats.
	 *
	 * @param uic the UIContext to analyse.
	 */
	public UicStats(final UIContext uic) {
		this.uic = uic;
		overallSerializedSize = this.getSerializationSize(uic);

		detectRootWCs(uic);

		// TODO: Don't forget to detect entries in uic not referenced.
	}

	/**
	 * @return the top-level component in the UI.
	 */
	public WComponent getUI() {
		return uic.getUI();
	}

	/**
	 * @return the overall serialized size of the UIContext.
	 */
	public int getOverallSerializedSize() {
		return overallSerializedSize;
	}

	/**
	 * @return the set of root WComponents in the UIContext.
	 */
	public Set<WComponent> getRootWCs() {
		return rootWCsDetected;
	}

	/**
	 * @return an iterator over the wcomponents that have been analysed via <code>createWCTreeStats</code>.
	 */
	public Iterator<WComponent> getWCsAnalysed() {
		return statsByWCTree.keySet().iterator();
	}

	/**
	 * Retrieves the map wchich contains all the WComponent instances that make up the WComponent tree starting from the
	 * given root component.
	 *
	 * @param root the root component.
	 * @return the map of Stats for the components under the given root component.
	 */
	public Map<WComponent, Stat> getWCTreeStats(final WComponent root) {
		return statsByWCTree.get(root);
	}

	/**
	 * Analyses each root WComponent found in the UIContext.
	 */
	public void analyseAllRootWCs() {
		for (WComponent root : rootWCsDetected) {
			analyseWC(root);
		}
	}

	/**
	 * Creates statistics for the given WComponent within the uicontext being analysed.
	 *
	 * @param comp the component to create stats for.
	 */
	public void analyseWC(final WComponent comp) {
		if (comp == null) {
			return;
		}

		statsByWCTree.put(comp, createWCTreeStats(comp));
	}

	/**
	 * Finds all instances that make up the tree (static and dynamic) and gathers stats.
	 *
	 * @param root the root component to start from.
	 * @return the statistics for components in the given subtree.
	 */
	private Map<WComponent, Stat> createWCTreeStats(final WComponent root) {
		Map<WComponent, Stat> statsMap = new HashMap<>();

		UIContextHolder.pushContext(uic);

		try {
			addStats(statsMap, root);
		} finally {
			UIContextHolder.popContext();
		}

		return statsMap;
	}

	/**
	 * Recursively adds statistics for a component and its children to the stats map.
	 *
	 * @param statsMap the stats map to add to.
	 * @param comp the component to analyse.
	 */
	private void addStats(final Map<WComponent, Stat> statsMap, final WComponent comp) {
		Stat stat = createStat(comp);
		statsMap.put(comp, stat);

		if (comp instanceof Container) {
			Container container = (Container) comp;
			int childCount = container.getChildCount();

			for (int i = 0; i < childCount; i++) {
				WComponent child = container.getChildAt(i);
				addStats(statsMap, child);
			}
		}
	}

	/**
	 * Creates statistics for a component in the given context.
	 *
	 * @param comp the component.
	 * @return the stats for the given component in the given context.
	 */
	private Stat createStat(final WComponent comp) {
		Stat stat = new Stat();

		stat.setClassName(comp.getClass().getName());

		stat.setName(comp.getId());
		if (stat.getName() == null) {
			stat.setName("Unknown");
		}

		if (comp instanceof AbstractWComponent) {
			Object obj = AbstractWComponent.replaceWComponent((AbstractWComponent) comp);

			if (obj instanceof AbstractWComponent.WComponentRef) {
				stat.setRef(obj.toString());
			}
		}

		ComponentModel model = (ComponentModel) uic.getModel(comp);

		stat.setModelState(Stat.MDL_NONE);

		if (model != null) {
			if (comp.isDefaultState()) {
				stat.setModelState(Stat.MDL_DEFAULT);
			} else {
				addSerializationStat(model, stat);
			}
		}

		return stat;
	}

	/**
	 * Determines the serialized size of an object by serializng it to a byte array.
	 *
	 * @param obj the object to find the serialized size of.
	 * @return the serialized size of the given object, or -1 on error.
	 */
	private int getSerializationSize(final Object obj) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.close();

			byte[] bytes = bos.toByteArray();
			return bytes.length;
		} catch (IOException ex) {
			// Unable to serialize so cannot determine size.
			return -1;
		}
	}

	/**
	 * @param model the component model
	 * @param stat the statistics
	 */
	private void addSerializationStat(final ComponentModel model, final Stat stat) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(model);
			oos.close();

			byte[] bytes = bos.toByteArray();
			stat.setSerializedSize(bytes.length);

			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			ois.readObject();

			stat.setModelState(Stat.MDL_SERIALIZABLE);
		} catch (Exception ex) {
			stat.setModelState(Stat.MDL_UN_SERIALIZABLE);
			stat.setComment(ex.getMessage());
		}
	}

	/**
	 * Stores statistics on a component in a UI context.
	 *
	 * @author Martin Shevchenko
	 */
	public static class Stat {

		/**
		 * Indicates that the component has no model (default or session).
		 */
		private static final int MDL_NONE = 0;
		/**
		 * Indicates that the component has a default model only.
		 */
		private static final int MDL_DEFAULT = 1;
		/**
		 * Indicates that the component has a serializable session model.
		 */
		private static final int MDL_SERIALIZABLE = 2;
		/**
		 * Indicates that the component has an unserializable session model (which is BAD).
		 */
		private static final int MDL_UN_SERIALIZABLE = 3;

		/**
		 * An textual enumeration of possible model states.
		 */
		private static final String[] MODEL_DESCS = new String[]{"&#160;", "Default", "Serializable", "Un-Serializable"};

		/**
		 * The component class name.
		 */
		private String className;
		/**
		 * The component id.
		 */
		private String name;
		/**
		 * Component reference.
		 */
		private String ref;
		/**
		 * State of the model.
		 */
		private int modelState = MDL_NONE;
		/**
		 * Serialization size.
		 */
		private int serializedSize;
		/**
		 * Comment for the statistic.
		 */
		private String comment;

		/**
		 * @return the component class name
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * @param className the component name
		 */
		public void setClassName(final String className) {
			this.className = className;
		}

		/**
		 * @return the component id
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the component id
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * @return the component reference
		 */
		public String getRef() {
			return ref;
		}

		/**
		 * @param ref the component reference
		 */
		public void setRef(final String ref) {
			this.ref = ref;
		}

		/**
		 * @return the model state
		 */
		public int getModelState() {
			return modelState;
		}

		/**
		 * @param modelState the model state
		 */
		public void setModelState(final int modelState) {
			this.modelState = modelState;
		}

		/**
		 * @return the comment
		 */
		public String getComment() {
			return comment;
		}

		/**
		 * @param comment the comment
		 */
		public void setComment(final String comment) {
			this.comment = comment;
		}

		/**
		 * @return the the serialized size
		 */
		public int getSerializedSize() {
			return serializedSize;
		}

		/**
		 * @param serializedSize the serialized size
		 */
		public void setSerializedSize(final int serializedSize) {
			this.serializedSize = serializedSize;
		}

		/**
		 * @return the model state as a string
		 */
		public String getModelStateAsString() {
			return MODEL_DESCS[modelState];
		}
	}

	/**
	 * @param uuic the user context
	 */
	private void detectRootWCs(final UIContext uuic) {
		rootWCsDetected = new HashSet<>();

		WComponent root = WebUtilities.getTop(uuic.getUI());
		rootWCsDetected.add(root);

		// TODO: Should report if the ui is not a root component.
		// Find any other root components that are storing information in the context.
		for (Iterator it = uuic.getComponents().iterator(); it.hasNext();) {
			WComponent wc = (WComponent) it.next();
			root = WebUtilities.getTop(wc);
			rootWCsDetected.add(root);
		}
	}
}
