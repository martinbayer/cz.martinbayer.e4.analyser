/**
 * 
 */
package cz.martinbayer.e4.analyser.palette;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.model.IXMLog;
import cz.martinbayer.analyser.processorsPool.ProcessorsPool;
import cz.martinbayer.e4.analyser.Activator;
import cz.martinbayer.e4.analyser.LoggerFactory;

/**
 * @author Martin Bayer
 */
public class PalettePart {

	private Logger logger = LoggerFactory.getInstance(getClass());

	public static final String ID = "cz.martinbayer.e4.analyser.palette.PalettePart";

	Label l;

	private TreeViewer viewer;

	@PostConstruct
	public void postConstruct(Composite parent, MApplication application) {
		logger.debug("initialized");
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ProcessorsContentProvider());
		viewer.setLabelProvider(new ProcessorsLabelProvider());

		// processors pool must be initialized with bundlecontext
		ProcessorsPool.getInstance().initialize(Activator.getContext());
		List<IProcessorItemWrapper<IXMLog>> procs = ProcessorsPool
				.getInstance().getProcessors();
		initProcessorsContext(procs, application);

		for (IProcessorItemWrapper<IXMLog> p : procs) {
			p.getProcessorLogic().getProcessor().run();
		}
		List<RootPaletteItem> roots = PaletteItemResolver
				.resolveProcessors(ProcessorsPool.getInstance().getProcessors());

		initOtherItems(roots);
		viewer.setInput(roots);
		viewer.expandAll();
		viewer.addSelectionChangedListener(new PaletteItemSelectedListener(
				application));
	}

	/**
	 * Initialize all processors with main IEclipseContext of the application
	 * Particular processor's {@link IProcessorItemWrapper} instance class must
	 * contain injected method for handling this IEclipseContext injection.
	 * Example of the method in {@link IProcessorItemWrapper} instance:
	 * 
	 * <pre>
	 * &#064;Inject
	 * public void setContext(IEclipseContext ctx) {
	 * 	Activator.getContext().registerService(IEclipseContext.class, ctx, null);
	 * }
	 * </pre>
	 * 
	 * @param procs
	 *            - collection of {@link IProcessorItemWrapper} instances -
	 *            context will be injected to all of them
	 * @param application
	 *            - {@link MApplication} instance - its context will be injected
	 *            to processor wrappers
	 */
	private void initProcessorsContext(
			List<IProcessorItemWrapper<IXMLog>> procs, MApplication application) {
		for (IProcessorItemWrapper<IXMLog> w : procs) {
			// inject main eclipse context to the processor
			ContextInjectionFactory.inject(w, application.getContext());
		}
	}

	private void initOtherItems(List<RootPaletteItem> roots) {
		roots.add(new OtherPaletteItems());
	}

	@Focus
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
