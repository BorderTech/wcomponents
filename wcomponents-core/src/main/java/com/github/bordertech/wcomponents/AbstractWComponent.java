package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.layout.UIManager;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.velocity.VelocityTemplateManager;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * AbstractWComponent is the parent class of all standard WComponents.
 * </p>
 * <p>
 * WComponent trees (UIs) are intended to be shared between sessions in order to reduce their memory footprint. To
 * archive this a class called UIContext has been introduced to store WComponent information specific to an individual
 * session. Each session has its own UIContext instance which is passed to the component tree whenever it needs to
 * handle events and paint.
 * </p>
 * <p>
 * The attributes of a WComponent have an initial shared value that can be overridden on a per session basis. We call
 * this a private attribute value. The methods that manipulate a components attributes will normally have two method
 * signatures. One will manipulate the shared value and does not require a UIContext to be passed. The other will
 * manipulate the private session based value and will include a UIContext as the first parameter. The methods that
 * access a components attributes only require one method signature. Accessor methods will include a UIContext as their
 * first parameter. They will return the private attribute value if one exists else the shared value.
 * </p>
 * <p>
 * The shared/private concept makes WComponents very flexible but has the dangerous ramification that it is easy to
 * accidentally dynamically share attribute values and even whole chunks of UI with everyone. To reduce this risk, it is
 * possible to lock a component and all its children. When the lock is set, it becomes impossible to update a shared
 * value. Trying to update a shared value will result in a runtime exception. The intention is that shared component
 * trees will be held in a registry. Adding a component tree to a registry would be a sensible time to lock it.
 * </p>
 *
 * @author James Gifford, Martin Shevchenko
 * @since 1.0.0
 */
public abstract class AbstractWComponent implements WComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractWComponent.class);

	/**
	 * Indicates whether this component is locked. Trying to update a shared value will result in a runtime exception
	 * when a component is locked.
	 */
	private boolean locked = false;

	/**
	 * The shared model for this component.
	 */
	private final ComponentModel sharedModel = newComponentModel();

	/**
	 * ID pattern.
	 */
	private static final Pattern ID_PATTERN = Pattern.compile(ID_VALIDATION_PATTERN);

	// ================================
	// Identification
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInternalId() {
		String iid = null;

		// As determining the internal id involves a fair bit of tree traversal, it is cached in the scratch map.
		// Try to retrieve the cached id first.
		Map scratchMap = getScratchMap();

		if (scratchMap != null) {
			iid = (String) scratchMap.get("iid");
		}

		if (iid == null) {
			// An id is a function of a component's position in the component tree.
			// The id will remain constant so long as the component tree does not
			// change.
			Container parent = getParent();

			if (parent == null) {
				return DEFAULT_INTERNAL_ID;
			}

			iid = parent.getInternalId();
			final int nameLen = iid.length();
			StringBuffer nameBuf = new StringBuffer(nameLen + 3);
			nameBuf.append(iid);
			nameBuf.append(getIndexOfChild(parent, this));

			if (iid.charAt(nameLen - 1) <= '9') {
				// last char was a number, change current to letters
				for (int i = nameLen; i < nameBuf.length(); i++) {
					nameBuf.setCharAt(i, (char) (nameBuf.charAt(i) + ('a' - '0')));
				}
			}

			iid = nameBuf.toString();

			if (scratchMap != null) {
				scratchMap.put("iid", iid);
			}
		}

		return iid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdName() {
		ComponentModel model = getComponentModel();
		return model.getIdName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIdName(final String idName) {
		// Not allow empty or null
		if (Util.empty(idName)) {
			throw new IllegalArgumentException("idName cannot be null or empty");
		}

		// Must start with a letter and followed by letters, digits and or underscores
		Matcher matcher = ID_PATTERN.matcher(idName);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"idName "
					+ idName
					+ " must start with a letter and followed by letters, digits and or underscores.");
		}

		ComponentModel model = getOrCreateComponentModel();
		model.setIdName(idName);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated no longer used. use {@link #getId()} instead.
	 */
	@Override
	@Deprecated
	public String getName() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		// As determining the name involves a fair bit of tree traversal, it is cached in the scratch map.
		// Try to retrieve the cached name first.
		Map scratchMap = getScratchMap();
		if (scratchMap != null) {
			String name = (String) scratchMap.get("name");
			if (name != null) {
				return name;
			}
		}

		// Get ID name
		String idName = getIdName();

		String name;
		// No ID name, so generate an ID
		if (idName == null) {
			name = generateId();
		} else { // Has ID name, so derive the full context name
			name = deriveId(idName);
		}

		if (scratchMap != null) {
			scratchMap.put("name", name);
		}

		// Log warning if an Active Naming Context has no id name
		if (this instanceof NamingContextable && ((NamingContextable) this).isNamingContext() && idName == null) {
			LOG.warn(
					"NamingContext [ID:" + name + "] does not have an id name set. Will be ignored.");
		}

		return name;
	}

	/**
	 * @return the generated unique id
	 */
	private String generateId() {
		// Direct parent
		Container parent = getParent();

		// No Parent
		if (parent == null) {
			return DEFAULT_NO_ID;
		}

		// Check if parent is an active naming context
		boolean parentIsNamingContext = WebUtilities.isActiveNamingContext(parent);

		// Get ID prefix
		String prefix;
		if (parentIsNamingContext) {
			prefix = ((NamingContextable) parent).getNamingContextId();
		} else {
			prefix = parent.getId();
		}

		// Setup name buffer
		int prefixLen = prefix.length();
		StringBuffer nameBuf = new StringBuffer(prefixLen + 3);

		// Append prefix (if required)
		if (prefixLen != 0) {
			nameBuf.append(prefix);
			// Parent is a NamingContext, so include context separator
			if (parentIsNamingContext) {
				nameBuf.append(ID_CONTEXT_SEPERATOR);
			}
		}

		// Use the component's position in the tree (ie index) to build a unique ID
		int idx = getIndexOfChild(parent, this);

		// Parent has a generated ID (ie no id name)
		// (NamingContexts must have an id name, so never null)
		if (parent.getIdName() == null) {
			// Generate ID - Append the index
			nameBuf.append(idx);
			if (prefix.charAt(prefixLen - 1) <= '9') {
				// last char was a number, change current to letters
				for (int i = prefixLen; i < nameBuf.length(); i++) {
					nameBuf.setCharAt(i, (char) (nameBuf.charAt(i) + ('a' - '0')));
				}
			}
		} else { // Parent has an id name assigned
			// Generate ID (with separator)
			nameBuf.append(ID_FRAMEWORK_ASSIGNED_SEPERATOR);
			nameBuf.append(idx);
		}

		return nameBuf.toString();
	}

	/**
	 * Derive the full id from its naming context.
	 *
	 * @param idName the component id name
	 * @return the derived id in its context
	 */
	private String deriveId(final String idName) {
		// Find parent naming context
		NamingContextable parent = WebUtilities.getParentNamingContext(this);

		// No Parent
		if (parent == null) {
			return idName;
		}

		// Get ID prefix
		String prefix = parent.getNamingContextId();

		// No Prefix, just use id name
		if (prefix.length() == 0) {
			return idName;
		}

		// Add Prefix
		StringBuffer nameBuf = new StringBuffer(prefix.length() + idName.length() + 1);
		nameBuf.append(prefix);
		nameBuf.append(ID_CONTEXT_SEPERATOR);
		nameBuf.append(idName);

		return nameBuf.toString();
	}

	/**
	 * Register this component's ID in its naming context.
	 */
	void registerInContext() {
		if (!ConfigurationProperties.getCheckDuplicateIds()) {
			return;
		}

		// Register Component if it has an ID name set
		if (getIdName() != null) {
			// Find parent context
			NamingContextable context = WebUtilities.getParentNamingContext(this);

			if (context == null) {
				// If this is the top context, then register itself
				if (WebUtilities.isActiveNamingContext(this)) {
					this.registerId(this);
				} else {
					LOG.warn("Component with id name [" + getIdName()
							+ "] is not in a naming context and cannot be verified for duplicate id.");
				}
				return;
			}
			// Assume context is AbstractWComponent
			((AbstractWComponent) context).registerId(this);
		}
	}

	/**
	 * Helper method to be used by a {@link NamingContextable} to register a component.
	 *
	 * @param component the component to register
	 */
	void registerId(final WComponent component) {
		if (!WebUtilities.isActiveNamingContext(this)) {
			throw new SystemException("Can only register a component on an active NamingContext");
		}

		// Get registered id names
		ComponentModel model = getOrCreateComponentModel();
		Map<String, WComponent> ids = model.getContextIds();
		if (ids == null) {
			ids = new HashMap<>();
			model.setContextIds(ids);
		}

		// Get id name
		String idName = component.getIdName();

		// Check if already used
		WComponent mapped = ids.get(idName);

		// Not in map
		if (mapped == null) {
			// Save ID / Component
			ids.put(idName, component);
		} else if (mapped != component) { // Check is same component
			String contextName = getId();
			throw new SystemException("Duplicate ID. ID \"" + idName + "\" for " + component.
					getClass().getName()
					+ " is already in use by " + mapped.getClass().getName() + " in context \"" + contextName + "\".");
		}

	}

	/**
	 * Clear the ID register. Usually called when a naming context is being painted. This allows the IDs to be
	 * refreshed.
	 */
	void clearIdRegister() {
		ComponentModel model = getOrCreateComponentModel();
		model.setContextIds(null);
	}

	/**
	 * <p>
	 * Retrieves a short-lived map which can be used to cache data during request processing. This map will be
	 * guaranteed to be cleared at the end of processing a request, but may also be cleared during request processing.
	 * Do not rely on the contents of this map to exist at any time.
	 * </p>
	 * <p>
	 * This method will return <code>null</code> if called outside of request processing.
	 * </p>
	 *
	 * @return a map which can be used to temporarily cache data, or null
	 */
	protected Map getScratchMap() {
		UIContext uic = UIContextHolder.getCurrent();
		return uic == null ? null : uic.getScratchMap(this);
	}

	// ================================
	// Action/Event handling
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void serviceRequest(final Request request) {
		ArrayList<WComponent> visibles = new ArrayList<>();
		collateVisible(this, visibles);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Calling handleRequest for " + visibles.size() + " components");
		}

		for (int i = 0; i < visibles.size(); i++) {
			WComponent next = visibles.get(i);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Calling handleRequest on " + next.getId() + " " + next.getClass());
			}

			next.handleRequest(request);
		}

		invokeLaters();
	}

	/**
	 * Collates all the visible components in this branch of the WComponent tree. WComponents are added to the
	 * <code>list</code> in depth-first order, as this list is traversed in order during the request handling phase.
	 *
	 * @param component the current branch to collate visible items in.
	 * @param list the list to add the visible components to.
	 */
	private static void collateVisible(final WComponent component, final List<WComponent> list) {
		if (component.isVisible()) {

			if (component instanceof Container) {

				final int size = ((Container) component).getChildCount();

				for (int i = 0; i < size; i++) {
					collateVisible(((Container) component).getChildAt(i), list);
				}
			}

			list.add(component);
		}
	}

	/**
	 * The framework calls this method at the end of the serviceRequest method. The default implementation is that only
	 * a root wcomponent actually runs them.
	 */
	protected void invokeLaters() {
		if (getParent() == null) {
			UIContext uic = UIContextHolder.getCurrent();

			if (uic != null) {
				uic.doInvokeLaters();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invokeLater(final Runnable runnable) {
		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null) {
			uic.invokeLater(runnable);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final Request request) {
		// NOP - classes to override.
	}

	// ---------------------------------
	// Non-WComponent web interface methods
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void forward(final String url) {
		invokeLater(new Runnable() {
			@Override
			public void run() {
				throw new ForwardException(url);
			}
		});
	}

	// ================================
	// Painting
	/**
	 * Associates a Velocity template with this component by supplying a resource url.
	 *
	 * @param templateUrl the location of the velocity template resource.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	void setTemplate(final String templateUrl) {
		getOrCreateComponentModel().setTemplateUrl(templateUrl);
	}

	/**
	 * Directly associates Velocity mark-up with this component. The mark-up will be used for rendering.
	 *
	 * @param markUp Velocity mark-up.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	void setTemplateMarkUp(final String markUp) {
		getOrCreateComponentModel().setTemplateMarkUp(markUp);
	}

	/**
	 * Retrieves Velocity mark-up which has been explicitly associated with this component.
	 *
	 * @return the Velocity mark-up, or null if no mark-up has been set explicitly.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	public String getTemplateMarkUp() {
		return getComponentModel().getTemplateMarkUp();
	}

	/**
	 * Retrieves the resource url of the Velocity template associated with this component.
	 *
	 * @return the location of the Velocity template resource, or null if there is no template.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	public String getTemplate() {
		return getComponentModel().getTemplateUrl();
	}

	/**
	 * Associates a velocity template with this component. A simple mapping is applied to the given class to derive the
	 * name of a velocity template.
	 * <p>
	 * For instance, com.github.bordertech.wcomponents.WTextField would map to the template
	 * com/github/bordertech/wcomponents/WTextField.vm
	 * </p>
	 *
	 * @param clazz the class to use to retrieve the template.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	void setTemplate(final Class clazz) {
		setTemplate(VelocityTemplateManager.toTemplateResourceName(clazz));
	}

	/**
	 * Prepares this component and all child componenents for painting (e.g. rendering to XML). This implementation
	 * calls {@link #preparePaintComponent(Request)}, then calls {@link #preparePaint(Request)} on all its children.
	 * Note that the this component's {@link #preparePaintComponent(Request)} is called before the childrens'
	 * {@link #preparePaintComponent(Request)} is called.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public final void preparePaint(final Request request) {
		// Don't prepare if it's invisible.
		if (!isVisible()) {
			return;
		}

		// Prepare this component.
		preparePaintComponent(request);

		// Prepare its children.
		final ArrayList<WComponent> children = (ArrayList<WComponent>) getComponentModel().
				getChildren();

		if (children != null) {
			final int size = children.size();

			for (int i = 0; i < size; i++) {
				children.get(i).preparePaint(request);
			}

			// Chances are that the WComponent tree is fairly stable now, so take
			// the opportunity to trim the child list to size if we have one.
			// This is just a memory optimization - it won't prevent adding more
			// children later, of course.
			children.trimToSize();
		}
	}

	/**
	 * Subclasses may override this method to place the component in the correct state before it is painted. When
	 * overriding this method, it is good practice to also call the superclass implementation.
	 *
	 * @param request the request being responded to.
	 */
	protected void preparePaintComponent(final Request request) {
		// NOP
	}

	/**
	 * Renders the component. If the component is visible then paint calls:
	 * <ol>
	 * <li>{@link #beforePaint(RenderContext) beforePaint(...)}</li>
	 * <li>{@link #paintComponent(RenderContext) paintComponent(...)}</li>
	 * <li>{@link #afterPaint(RenderContext) afterPaint(...)}</li>
	 * </ol>
	 *
	 * @param renderContext the RenderContext to send the output to.
	 */
	@Override
	public final void paint(final RenderContext renderContext) {
		if (isVisible()) {
			// If painting a NamingContext, reset its registered IDs
			if (WebUtilities.isActiveNamingContext(this)) {
				clearIdRegister();
			}

			registerInContext();

			beforePaint(renderContext);
			paintComponent(renderContext);
			afterPaint(renderContext);
		}
	}

	/**
	 * Indicates whether structural debugging info should be output for this component.
	 *
	 * @return false debug structure is never enabled for this component.
	 */
	protected boolean isDebugStructure() {
		return false;
	}

	/**
	 * Subclasses may override this method to output content before the component has been painted. When overriding this
	 * method, it is good practice to call the superclass implementation
	 * <b>after</b> emitting any additional content.
	 *
	 * @param renderContext the context to render to.
	 */
	protected void beforePaint(final RenderContext renderContext) {
		// NOP
	}

	/**
	 * Subclasses may override this method to output content after the component has been painted. When overriding this
	 * method, it is good practice to call the superclass implementation
	 * <b>before</b> emitting any additional content.
	 *
	 * @param renderContext the context to render to.
	 */
	protected void afterPaint(final RenderContext renderContext) {
		// NOP
	}

	/**
	 * This is where most of the painting work is normally done. If a layout has been supplied either directly or by
	 * supplying a velocity template, then painting is delegated to the layout manager. If there is no layout, the
	 * default behaviour is to paint the child components in sequence.
	 *
	 * @param renderContext the context to render to.
	 */
	protected void paintComponent(final RenderContext renderContext) {
		Renderer renderer = UIManager.getRenderer(this, renderContext);

		if (getTemplate() != null || getTemplateMarkUp() != null) {
			Renderer templateRenderer = UIManager.getTemplateRenderer(renderContext);
			templateRenderer.render(this, renderContext);
		} else if (renderer == null) {
			// Default is juxtaposition
			List<WComponent> children = getComponentModel().getChildren();

			if (children != null) {
				final int size = children.size();

				for (int i = 0; i < size; i++) {
					children.get(i).paint(renderContext);
				}
			}
		} else {
			renderer.render(this, renderContext);
		}
	}

	// ================================
	// Validation
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(final List<Diagnostic> diags) {
		// Don't validate if it's invisible.
		if (!isVisible()) {
			return;
		}

		// Also check the validation override flag.
		if (!isValidate()) {
			return;
		}

		// Also don't validate if it's disabled.
		if (this instanceof Disableable) {
			Disableable dis = (Disableable) this;

			if (dis.isDisabled()) {
				return;
			}
		}

		// Also don't validate if it's readonly.
		if (this instanceof Input) {
			Input input = (Input) this;

			if (input.isReadOnly()) {
				return;
			}
		}

		// Validate this component.
		validateComponent(diags);

		// Validate children
		List<WComponent> children = getComponentModel().getChildren();

		if (children != null) {
			final int size = children.size();

			for (int i = 0; i < size; i++) {
				children.get(i).validate(diags);
			}
		}
	}

	/**
	 * Subclasses may override to provide validation.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	protected void validateComponent(final List<Diagnostic> diags) {
		// NOP
	}

	/**
	 * Create and return an error diagnostic associated to this WComponent.
	 *
	 * @param message the error message, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message.
	 * @return an error diagnostic for this component.
	 */
	protected Diagnostic createErrorDiagnostic(final String message, final Serializable... args) {
		return createErrorDiagnostic(this, message, args);
	}

	/**
	 * Create and return an error diagnostic associated to the given error source.
	 *
	 * @param source the source of the error.
	 * @param message the error message, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message.
	 * @return an error diagnostic for this component.
	 */
	protected Diagnostic createErrorDiagnostic(final WComponent source, final String message,
			final Serializable... args) {
		return new DiagnosticImpl(Diagnostic.ERROR, source, message, args);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showErrorIndicators(final List<Diagnostic> diags) {
		// Don't show indicators if it's invisible.
		if (isVisible()) {
			// Show indicators for this component.
			showErrorIndicatorsForComponent(diags);

			// Show indicators for its children.
			List<WComponent> children = getComponentModel().getChildren();

			if (children != null) {
				final int size = children.size();

				for (int i = 0; i < size; i++) {
					children.get(i).showErrorIndicators(diags);
				}
			}
		}
	}

	/**
	 * <p>
	 * This does not affect the diag list at all. The ValidatableComponent should visually mark any fields or blocks
	 * that have errors in the given diag list.
	 * </p>
	 *
	 * @param diags the list of diagnostics for this component.
	 */
	protected void showErrorIndicatorsForComponent(final List<Diagnostic> diags) {
		// NOP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showWarningIndicators(final List<Diagnostic> diags) {
		// Don't show indicators if it's invisible.
		if (isVisible()) {
			// Show indicators for this component.
			showWarningIndicatorsForComponent(diags);

			// Show indicators for its children.
			List<WComponent> children = getComponentModel().getChildren();

			if (children != null) {
				final int size = children.size();

				for (int i = 0; i < size; i++) {
					children.get(i).showWarningIndicators(diags);
				}
			}
		}
	}

	/**
	 * <p>
	 * This does not affect the diag list at all. The ValidatableComponent should visually mark any fields or blocks
	 * that have warnings in the given diag list.
	 * </p>
	 *
	 * @param diags the list of diagnostics for this component.
	 */
	protected void showWarningIndicatorsForComponent(final List<Diagnostic> diags) {
		// NOP
	}

	// ================================
	// Attributes
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocked(final boolean lock) {
		this.locked = lock;

		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).setLocked(lock);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLocked() {
		return locked;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInitialised() {
		return isFlagSet(ComponentModel.INITIALISED_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitialised(final boolean flag) {
		setFlag(ComponentModel.INITIALISED_FLAG, flag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValidate() {
		return isFlagSet(ComponentModel.VALIDATE_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValidate(final boolean flag) {
		setFlag(ComponentModel.VALIDATE_FLAG, flag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVisible() {
		return isFlagSet(ComponentModel.VISIBLE_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(final boolean visible) {
		setFlag(ComponentModel.VISIBLE_FLAG, visible);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHidden() {
		return isFlagSet(ComponentModel.HIDE_FLAG);
	}

	/**
	 * <p>
	 * Sets the client visibility of this component. Hidden components take part in event handling and painting, but are
	 * not <a href="https://html.spec.whatwg.org/multipage/dom.html#palpable-content-2">palpable</a> on the client.
	 * <p>
	 *
	 * @param hidden true for hidden, false for displayed.
	 */
	public void setHidden(final boolean hidden) {
		setFlag(ComponentModel.HIDE_FLAG, hidden);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrackingEnabled(final boolean track) {
		setFlag(ComponentModel.TRACKABLE_FLAG, track);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrackingEnabled() {
		return isFlagSet(ComponentModel.TRACKABLE_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTracking() {
		if (isTrackingEnabled()) {
			// Check if id name set
			String idName = getIdName();
			if (idName != null) {
				return true;
			}
			LOG.warn(
					"Trying to track a component that has no id name set. Tracking will be ignored. [" + getId() + " "
					+ this.getClass().getName() + "]");
		}
		return false;
	}

	/**
	 * Sets or clears one or more component flags in the component model for the given context..
	 *
	 * @param mask the bit mask for the flags to set/clear.
	 * @param flag true to set the flag(s), false to clear.
	 */
	protected void setFlag(final int mask, final boolean flag) {
		// Only store the flag value if it is not the default.
		if (flag != isFlagSet(mask)) {
			ComponentModel model = getOrCreateComponentModel();
			model.setFlags(switchFlag(model.getFlags(), mask, flag));
		}
	}

	/**
	 * A utility method to set or clear one or more bits in the given set of flags.
	 *
	 * @param flags the current set of flags.
	 * @param mask the bit mask for the flags to set/clear.
	 * @param value true to set the flag(s), false to clear.
	 * @return the new set of flags.
	 */
	private static int switchFlag(final int flags, final int mask, final boolean value) {
		int newFlags = value ? flags | mask : flags & ~mask;
		return newFlags;
	}

	/**
	 * Indicates whether any of the given flags are set. This is normally used to only check a single flag at a time.
	 *
	 * @param mask the bit mask for the flags to check.
	 * @return true if any flags are set, false otherwise.
	 */
	protected boolean isFlagSet(final int mask) {
		ComponentModel model = getComponentModel();
		return isFlagSet(model.getFlags(), mask);
	}

	/**
	 * A utility method check whether any of the given flags are set. This is normally used to only check a single flag
	 * at a time.
	 *
	 * @param flags the current set of flags.
	 * @param mask the bit mask for the flags to set/clear.
	 * @return true if any flags are set, false otherwise.
	 */
	private static boolean isFlagSet(final int flags, final int mask) {
		boolean isSet = (flags & mask) != 0;
		return isSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasTabIndex() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTabIndex() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	void setLabel(final WLabel label) {
		getOrCreateComponentModel().setLabel(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WLabel getLabel() {
		return getComponentModel().getLabel();
	}

	// --------------------------------
	// Focus management
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocussed() {
		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null) {
			uic.setFocussed(this, uic);
		}
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new model appropriate for the type of component. Subclasses can override, and should narrow the return
	 * type.
	 *
	 * @return a new ComponentModel.
	 */
	protected ComponentModel newComponentModel() {
		return new ComponentModel();
	}

	/**
	 * <p>
	 * Performs initialisation that is required on this components model, and potentially its children. Subclasses can
	 * override.
	 * </p>
	 * <p>
	 * Note that the user's component model will automatically be populated from the shared model.
	 * </p>
	 */
	protected void initialiseComponentModel() {
		// NOP
	}

	/**
	 * Check if this component has a model on this user's context.
	 *
	 * @param uic the user context
	 * @return true if has no component model
	 */
	protected boolean hasNoComponentModel(final UIContext uic) {
		WebModel model = uic.getModel(this);
		return model == null;
	}

	/**
	 * Returns the effective component model for this component. Subclass may override this method to narrow the return
	 * type to their specific model type.
	 *
	 * @return the effective component model
	 */
	protected ComponentModel getComponentModel() {
		UIContext effectiveContext = UIContextHolder.getCurrent();

		if (effectiveContext == null) {
			return sharedModel;
		} else {
			ComponentModel model = (ComponentModel) effectiveContext.getModel(this);

			if (model == null) {
				return sharedModel;
			} else if (model.getSharedModel() == null) {
				// The reference to the sharedModel has disappeared,
				// probably due to session serialization
				model.setSharedModel(sharedModel);
			}

			return model;
		}
	}

	/**
	 * Returns the shared component model for this component. Subclass may override this method to narrow the return
	 * type to their specific model type.
	 *
	 * @return the shared component model
	 */
	protected ComponentModel getDefaultModel() {
		return sharedModel;
	}

	/**
	 * Retrieves the model for this component so that it can be modified. If this method is called during request
	 * processing, and a session specific model does not yet exist, then a new model is created. Subclasses may override
	 * this method to narrow the return type to their specific model type.
	 *
	 * @return the model for this component
	 */
	protected ComponentModel getOrCreateComponentModel() {
		ComponentModel model = getComponentModel();

		if (locked && model == sharedModel) {
			UIContext effectiveContext = UIContextHolder.getCurrent();

			if (effectiveContext != null) {
				model = newComponentModel();
				model.setSharedModel(sharedModel);

				effectiveContext.setModel(this, model);
				initialiseComponentModel();
			}
		}

		return model;
	}

	/**
	 * <p>
	 * Resets this component and its children to their initial state for the given user context / session.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> The exception to this rule is if this component has been dynamically added to the UI, then the
	 * parent component will differ from the initial state.
	 * </p>
	 */
	@Override
	public void reset() {
		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null) {
			// Note: The parent component is stored as a field within AbstractWComponent's
			// component model, which previously caused problems with code like the following:
			//
			// ...
			// WContainer parent = new WContainer();
			// WComponent child = new WTextField();
			// ...
			// parent.add(uic, child);
			// ...
			// child.reset(uic);
			// ...
			//
			// In this scenario, the parent still kept a reference to the child component,
			// but the child component has lost its reference to the parent, which caused
			// ancestor lookups to fail, and duplicate IDs to be generated.
			//
			// This implementation therefore does a top-down reset, clearing this
			// component's model before any child models. This allows child components
			// to correctly check for dynamic parenting in their reset methods and
			// restore the reference to the parent component if necessary.

			final int childCount = getChildCount();
			List<WComponent> children = null;

			if (childCount > 0) {
				children = new ArrayList<>(getComponentModel().getChildren());
			}

			// Keep a reference to the current (possibly dynamic) parent before it is potentially lost
			Container dynamicParent = getParent();

			// Reset this component's data first.
			this.removeComponentModel();
			uic.clearScratchMap(this);
			uic.clearRequestScratchMap(this);

			// Now reset all descendant components
			if (children != null) {
				for (WComponent child : children) {
					child.reset();
				}
			}

			// At this point, everything in the sub-tree where the reset
			// method was initially called has now been reset.
			// If the component was dynamically added
			// and the dynamic parent still has this component as a child
			if (dynamicParent != null && getParent() != dynamicParent && getIndexOfChild(
					dynamicParent, this) != -1) {
				// then re-instate the reference to the dynamic parent.
				getOrCreateComponentModel().setParent(dynamicParent);
			}
		}
	}

	/**
	 * Reset this component to its initial state.
	 */
	protected void removeComponentModel() {
		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null) {
			uic.removeModel(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tidyUpUIContextForTree() {
		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null) {
			tidyUpUIContext();

			List<WComponent> children = getComponentModel().getChildren();

			if (children != null) {
				final int size = children.size();

				for (int i = 0; i < size; i++) {
					children.get(i).tidyUpUIContextForTree();
				}
			}
		}
	}

	/**
	 * Removes the user-specific component model if this component is in its default state.
	 */
	protected void tidyUpUIContext() {
		if (isDefaultState()) {
			removeComponentModel();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDefaultState() {
		return getComponentModel().equals(sharedModel);
	}

	// ================================
	// Structure
	/**
	 * @return the number of child components currently contained within this component.
	 */
	int getChildCount() {
		ComponentModel model = getComponentModel();
		return (model.getChildren() == null ? 0 : model.getChildren().size());
	}

	/**
	 * Retrieves a child component by its index.
	 *
	 * @param index the index of the child component to be retrieved.
	 * @return the child component at the given index.
	 */
	WComponent getChildAt(final int index) {
		ComponentModel model = getComponentModel();
		return model.getChildren().get(index);
	}

	/**
	 * Retrieves the index of the given child.
	 *
	 * @param childComponent the child component to retrieve the index for.
	 * @return the index of the given child component, or -1 if the component is not a child of this component.
	 */
	int getIndexOfChild(final WComponent childComponent) {
		ComponentModel model = getComponentModel();
		List<WComponent> children = model.getChildren();

		return children == null ? -1 : children.indexOf(childComponent);
	}

	/**
	 * Retrieves the children of this component.
	 *
	 * @return a list containing the children of this component, or an empty list.
	 */
	List<WComponent> getChildren() {
		List<WComponent> children = getComponentModel().getChildren();

		return children != null && !children.isEmpty()
				? Collections.unmodifiableList(children)
				: Collections.<WComponent>emptyList();
	}

	/**
	 * Internal utility method to find the index of a child within a container. This method makes use of the additional
	 * methods offered by the AbstractWComponent implementation (if available), otherwise it falls back the methods
	 * declared in the {@link WComponent} interface.
	 *
	 * @param parent the container to search for the child in
	 * @param childComponent the component to search for.
	 * @return the index of the <code>childComponent</code> in <code>parent</code>, or -1 if <code>childComponent</code>
	 * is not a child of <code>parent</code>.
	 */
	private static int getIndexOfChild(final Container parent, final WComponent childComponent) {
		if (parent instanceof AbstractWComponent) {
			return ((AbstractWComponent) parent).getIndexOfChild(childComponent);
		} else {
			// We have to do this the hard way...
			for (int i = 0; i < parent.getChildCount(); i++) {
				if (childComponent == parent.getChildAt(i)) {
					return i;
				}
			}
		}

		return -1;
	}

	/**
	 * Adds the given component as a child of this component.
	 *
	 * @param component the component to add.
	 */
	void add(final WComponent component) {
		assertAddSupported(component);
		assertNotReparenting(component);

		if (!(this instanceof Container)) {
			throw new UnsupportedOperationException("Components can only be added to a container");
		}

		ComponentModel model = getOrCreateComponentModel();

		if (model.getChildren() == null) {
			model.setChildren(new ArrayList<WComponent>(1));
		}

		model.getChildren().add(component);

		if (isLocked()) {
			component.setLocked(true);
		}

		if (component instanceof AbstractWComponent) {
			((AbstractWComponent) component).getOrCreateComponentModel().setParent((Container) this);
			((AbstractWComponent) component).addNotify();
		}
	}

	/**
	 * Adds the given component as a child of this component. The tag is used to identify the child in a velocity
	 * template.
	 *
	 * @param component the component to add.
	 * @param tag the tag used to identify the component.
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	void add(final WComponent component, final String tag) {
		add(component);
		component.setTag(tag);
	}

	/**
	 * Some components may wish to throw an Exception if certain types of components can not be added. This
	 * implementation does nothing, and is here so that e.g. sub-classes do not have to override multiple add methods.
	 *
	 * @param componentToAdd the component being added.
	 */
	protected void assertAddSupported(final WComponent componentToAdd) {
		// NOP
	}

	/**
	 * Removes the given component from this component's list of children.
	 *
	 * @param aChild child component
	 */
	void remove(final WComponent aChild) {
		ComponentModel model = getOrCreateComponentModel();

		if (model.getChildren() == null) {
			model.setChildren(copyChildren(getComponentModel().getChildren()));
		}

		if (model.getChildren().remove(aChild)) {
			// Deallocate children list if possible, to reduce session size.
			if (model.getChildren().isEmpty()) {
				model.setChildren(null);
			}

			// The child component has been successfully removed so clean up the context.
			aChild.reset();

			// If the parent has been set in the shared model, we must override
			// it in the session model for the component to be considered removed.
			// This unfortunately means that the model will remain in the user's session.
			if (aChild.getParent() != null && aChild instanceof AbstractWComponent) {
				((AbstractWComponent) aChild).getOrCreateComponentModel().setParent(null);
				((AbstractWComponent) aChild).removeNotify();
			}
		}
	}

	/**
	 * Removes all of the children from this component.
	 */
	void removeAll() {
		while (getChildCount() > 0) {
			remove(getChildAt(0));
		}
	}

	/**
	 * Creates a copy of the given list of components.
	 *
	 * @param children the list to copy.
	 * @return a copy of the list.
	 */
	private static List<WComponent> copyChildren(final List<WComponent> children) {
		ArrayList<WComponent> copy;

		if (children == null) {
			copy = new ArrayList<>(1);
		} else {
			copy = new ArrayList<>(children);
		}

		return copy;
	}

	/**
	 * Ensure that the given component is not about to be re-parented. If it is then this method throws a runtime
	 * exception to inform the developer of their mistake.
	 *
	 * @param component the component to check.
	 */
	private static void assertNotReparenting(final WComponent component) {
		if (component.getParent() != null) {
			throwReparentingException();
		}
	}

	/**
	 * Called when an attempt is made to add a component to more than one parent. Throws a runtime exception.
	 */
	private static void throwReparentingException() {
		String msg = "Reparenting error. A wcomponent instance can only be added as a child to one parent wcomponent.";
		LOG.error(msg);
		throw new IllegalStateException(msg);
	}

	/**
	 * Notifies this component that it now has a parent component. Subclasses can override this to perform any
	 * additional processing required. The default implementation does nothing.
	 */
	protected void addNotify() {
		// NOP
	}

	/**
	 * Notifies this component that it no longer has a parent component. Subclasses can override this to perform any
	 * additional processing required. The default implementation does nothing.
	 */
	protected void removeNotify() {
		// NOP
	}

	/**
	 * @return the current parent of this component.
	 */
	@Override
	public Container getParent() {
		ComponentModel model = getComponentModel();
		return model.getParent();
	}

	// ----------------------------
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	@Override
	public String getTag() {
		ComponentModel model = getComponentModel();
		return model.getTag();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	@Override
	public void setTag(final String tag) {
		ComponentModel model = getOrCreateComponentModel();
		model.setTag(tag);
	}

	// ================================
	// Environment
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment getEnvironment() {
		UIContext uic = UIContextHolder.getCurrent();
		return uic == null ? null : uic.getEnvironment();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnvironment(final Environment environment) {
		UIContext uic = UIContextHolder.getCurrent();

		if (uic != null) {
			uic.setEnvironment(environment);
		}
	}

	// ------------------------------
	// Header support
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Headers getHeaders() {
		UIContext uic = UIContextHolder.getCurrent();

		return uic == null ? null : uic.getHeaders();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBaseUrl() {
		Environment env = getEnvironment();
		String baseUrl = null;

		if (env != null) {
			baseUrl = env.getBaseUrl();
		}

		return (baseUrl == null ? "" : baseUrl);
	}

	// ================================
	// Component Attributes
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttribute(final String key, final Serializable value) {
		ComponentModel model = getOrCreateComponentModel();
		model.setAttribute(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable getAttribute(final String key) {
		ComponentModel model = getComponentModel();
		return model.getAttribute(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable removeAttribute(final String key) {
		ComponentModel model = getOrCreateComponentModel();
		return model.removeAttribute(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setToolTip(final String text, final Serializable... args) {
		ComponentModel model = getOrCreateComponentModel();
		model.setToolTip(text, args);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTip() {
		ComponentModel model = getComponentModel();
		return I18nUtilities.format(null, model.getToolTip());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessibleText(final String text, final Serializable... args) {
		ComponentModel model = getOrCreateComponentModel();
		model.setAccessibleText(text, args);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAccessibleText() {
		ComponentModel model = getComponentModel();
		return I18nUtilities.format(null, model.getAccessibleText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHtmlClass(final String text) {
		getOrCreateComponentModel().setHtmlClass(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHtmlClass(final HtmlClassProperties className) {
		getOrCreateComponentModel().setHtmlClass(className);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHtmlClass(final String className) {
		if (!Util.empty(className)) {
			getOrCreateComponentModel().addHtmlClass(className);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHtmlClass(final HtmlClassProperties className) {
		if (null != className) {
			getOrCreateComponentModel().addHtmlClass(className);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHtmlClass() {
		ComponentModel model = getComponentModel();
		return I18nUtilities.format(null, model.getHtmlClass());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set getHtmlClasses() {
		ComponentModel model = getComponentModel();
		return model.getHtmlClasses();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHtmlClass(final String className) {
		getOrCreateComponentModel().removeHtmlClass(className);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHtmlClass(final HtmlClassProperties className) {
		getOrCreateComponentModel().removeHtmlClass(className);
	}

	// ================================
	// Debugging
	/**
	 * Creates a String representation of this component; usually for debugging purposes.
	 *
	 * @return a String representation of this component.
	 */
	@Override
	public String toString() {
		return toString(null);
	}

	/**
	 * Creates a String representation of this component, for debugging purposes.
	 *
	 * @param details some additional details to display in the output.
	 * @return a String representation of this component.
	 */
	final String toString(final String details) {
		return toString(details, 0, getChildCount() - 1);
	}

	/**
	 * Creates a String representation of this component, for debugging purposes.
	 *
	 * @param details some additional details to display in the output
	 * @param childStartIndex the start index of children to include in the output
	 * @param childEndIndex the end index of children to include in the output
	 * @return a String representation of this component
	 */
	final String toString(final String details, final int childStartIndex, final int childEndIndex) {
		// The simple class name will be empty for anonymous subclasses,
		// which is not particularly useful, so we recurse until we find a suitable one.
		String className = null;

		for (Class<?> clazz = getClass(); Util.empty(className) && clazz != null; clazz = clazz.
				getSuperclass()) {
			className = clazz.getSimpleName();
		}

		StringBuffer buf = new StringBuffer(className == null ? "?" : className);

		if (!isDefaultState()) {
			buf.append("<user model>");
		}

		buf.append(isVisible() ? "" : "<invisible>");
		buf.append(!isHidden() ? "" : "<hidden>");

		if (this instanceof Disableable) {
			buf.append(!((Disableable) this).isDisabled() ? "" : "<disabled>");
		}

		if (details != null) {
			buf.append('(').append(details).append(')');
		}

		if (this instanceof Container && childStartIndex >= 0 && childEndIndex < getChildCount()
				&& childStartIndex <= childEndIndex) {
			WComponent[] children = new WComponent[childEndIndex - childStartIndex + 1];

			for (int i = childStartIndex; i <= childEndIndex; i++) {
				children[i - childStartIndex] = getChildAt(i);
			}

			buf.append(childrenToString(children));
		}

		return buf.toString();
	}

	/**
	 * Creates a String representation of a single child component to use with {@link #toString()}..
	 *
	 * @param children the children to output
	 * @return a String representation of the given component.
	 */
	final String childrenToString(final WComponent... children) {
		if (!(this instanceof Container)) {
			return "";
		}

		if (children == null || children.length == 0) {
			return "[]";
		}

		StringBuffer buf = new StringBuffer("\n[");

		for (int i = 0; i < children.length; i++) {
			buf.append(i == 0 ? "\n   " : ",\n   ");
			buf.append(children[i].toString().replaceAll("\n", "\n   "));
		}

		buf.append("\n]");
		return buf.toString();
	}

	// ================================
	// Serialization
	/**
	 * <p>
	 * This class is used to hold a reference to the shared singleton instance of a wcomponent for the purpose of
	 * serialisation. Serialization of WComponent session information is tricky because of the separation of the session
	 * state data (the UIContext) and the shared application definition (the wcompontent stored in the UIRegistry). When
	 * serializing a UIContext, we don't want to serialize the entire shared application definition in each users
	 * session. The problem is that the data stored in the UIContext is keyed using the shared WComponent instances, so
	 * we must convert them all to references in order to prevent the entire application from being serialized.
	 * </p>
	 * <p>
	 * This extends WComponent in order to fulfil the readResolve contract, however it overrides writeObject/readObject
	 * to only write out the data needed to find the shared instance.
	 * </p>
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class WComponentRef extends AbstractWComponent {

		/**
		 * The UIRegistry key under which the UI root component is registered. This is assumed to be the fully qualified
		 * class name of the root component.
		 */
		private String repositoryKey;

		/**
		 * The location path of the component in the UI tree, specified as child indices.
		 */
		private int[] nodeLocation;

		/**
		 * Creates a WComponentRef.
		 *
		 * @param repositoryKey the UIRegistry key under which the UI root component is registered.
		 * @param nodeLocation the location of the component in the UI tree.
		 */
		public WComponentRef(final String repositoryKey, final int[] nodeLocation) {
			this.repositoryKey = repositoryKey;
			this.nodeLocation = nodeLocation;
		}

		/**
		 * Implement writeObject to only write out the repositoryKey and nodeLocation fields.
		 *
		 * @param out the ObjectOutputStream to write to.
		 * @throws IOException if there is an error writing to the stream.
		 * @see java.io.Serializable
		 */
		private void writeObject(final ObjectOutputStream out) throws IOException {
			out.writeObject(repositoryKey);
			out.writeInt(nodeLocation.length);

			for (int i = 0; i < nodeLocation.length; i++) {
				out.writeInt(nodeLocation[i]);
			}
		}

		/**
		 * Implement readObject to only read in the repositoryKey and nodeLocation fields.
		 *
		 * @param in the ObjectInputStream to read from.
		 * @throws IOException if there is an error reading from the stream.
		 * @throws ClassNotFoundException if the class can't be found.
		 * @see java.io.Serializable
		 */
		private void readObject(final ObjectInputStream in) throws IOException,
				ClassNotFoundException {
			repositoryKey = (String) in.readObject();
			int len = in.readInt();
			nodeLocation = new int[len];

			for (int i = 0; i < nodeLocation.length; i++) {
				nodeLocation[i] = in.readInt();
			}
		}

		/**
		 * Implement readResolve so that on deserialization, the WComponent that is referred to by this WComponentRef is
		 * returned. The WComponent
		 *
		 * @return the WComponent instance that is registered with the registry.
		 * @throws ObjectStreamException never, but Serializable requires this method signature to declare it.
		 * @see java.io.Serializable
		 */
		private Object readResolve() throws ObjectStreamException {
			if (repositoryKey == null || nodeLocation == null) {
				// Should not happen
				throw new IllegalStateException(
						"Unable to resolve component in repository '" + repositoryKey + '\'');
			}

			WComponent comp = UIRegistry.getInstance().getUI(repositoryKey);

			for (int i = 0; comp != null && i < nodeLocation.length; i++) {
				comp = ((Container) comp).getChildAt(nodeLocation[i]);
			}

			// Component not found - BAD!
			if (comp == null) {
				throw new IllegalStateException("Unable to resolve component: " + toString());
			}

			return comp;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append(repositoryKey);

			for (int i = 0; i < nodeLocation.length; i++) {
				buf.append(',').append(nodeLocation[i]);
			}

			return buf.toString();
		}
	}

	/**
	 * Implement writeReplace so that on serialization, WComponents that are registered in the UIRegistry write a
	 * reference to the registered component rather than the component itself. This ensures that, on deserialization,
	 * only one copy of the registered component will be present in the VM.
	 *
	 * @return the WComponent instance that is registered with the registry.
	 * @throws ObjectStreamException never, but Serializable requires this method signature to declare it.
	 * @see java.io.Serializable
	 */
	protected Object writeReplace() throws ObjectStreamException {
		WComponent top = WebUtilities.getTop(this);
		String repositoryKey;
		if (top instanceof WApplication) {
			repositoryKey = ((WApplication) top).getUiVersionKey();
		} else {
			repositoryKey = top.getClass().getName();
		}

		if (UIRegistry.getInstance().isRegistered(repositoryKey)
				&& top == UIRegistry.getInstance().getUI(repositoryKey)) {
			// Calculate the node location.
			// The node location is a list of "shared" child indexes of each
			// ancestor going right back to the top node.
			ArrayList<Integer> reversedIndexList = new ArrayList<>();
			WComponent node = this;
			Container parent = node.getParent();

			try {
				while (parent != null) {
					int index = getIndexOfChild(parent, node);
					reversedIndexList.add(index);
					node = parent;
					parent = node.getParent();
				}
			} catch (Exception ex) {
				LOG.error("Unable to determine component index relative to top.", ex);
			}

			final int depth = reversedIndexList.size();
			int[] nodeLocation = new int[depth];

			for (int i = 0; i < depth; i++) {
				Integer index = reversedIndexList.get(depth - i - 1);
				nodeLocation[i] = index.intValue();
			}

			WComponentRef ref = new WComponentRef(repositoryKey, nodeLocation);

			if (LOG.isDebugEnabled()) {
				LOG.debug(
						"WComponent converted to reference. Ref = " + ref + ". Component = " + getClass().
						getName());
			}

			return ref;
		} else {
			LOG.debug(
					"WComponent not accessible via the repository, so it will be serialised. Component = "
					+ getClass().getName());

			return this;
		}
	}

	/**
	 * A Utility method which returns the (replaced) serialized form of a WComponent. This method is only exposed for
	 * internal instrumentation (by UicStats).
	 *
	 * @param component the component to serialize.
	 * @return the serialized form of the component.
	 */
	public static Object replaceWComponent(final AbstractWComponent component) {
		try {
			return component.writeReplace();
		} catch (ObjectStreamException ignored) {
			// Will not occur, but writeReplace method signature must contain it
			return component;
		}
	}
}
