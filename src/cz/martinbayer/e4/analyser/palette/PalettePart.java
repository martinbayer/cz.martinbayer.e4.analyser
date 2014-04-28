/**
 * 
 */
package cz.martinbayer.e4.analyser.palette;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.analyser.processorsPool.ProcessorsPool;
import cz.martinbayer.e4.analyser.Activator;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.handlers.InstallHandler;

/**
 * @author Martin Bayer
 */
public class PalettePart {

	private Logger logger = LoggerFactory.getInstance(getClass());

	public static final String ID = "cz.martinbayer.e4.analyser.palette.PalettePart";

	private TreeViewer viewer;

	@PostConstruct
	public void postConstruct(
			Composite parent,
			MApplication application,
			EMenuService menuService,
			@Preference(nodePath = ContextVariables.Property.PROJECT_OPERATION) final IEclipsePreferences prefs,

			final IProvisioningAgent agent, final UISynchronize sync,
			final IWorkbench workbench) {

		logger.debug("palette initialized");
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ProcessorsContentProvider());
		viewer.setLabelProvider(new ProcessorsLabelProvider());

		// processors pool must be initialized with bundlecontext
		ProcessorsPool.getInstance().initialize(Activator.getContext());
		List<IProcessorItemWrapper<IE4LogsisLog>> procs = ProcessorsPool
				.getInstance().getProcessors();
		initProcessorsContext(procs, application);

		List<RootPaletteItem> roots = PaletteItemResolver
				.resolveProcessors(ProcessorsPool.getInstance().getProcessors());

		initOtherItems(roots);
		viewer.setInput(roots);
		viewer.expandAll();
		viewer.addSelectionChangedListener(new PaletteItemSelectedListener(
				application));
		handlePluginsInstallation(prefs, application, parent.getShell(), sync,
				agent, workbench, logger);
		application.getContext().set(InstallHandler.INSTALL_PARAM_APP_STARTING,
				false);
	}

	private void handlePluginsInstallation(final IEclipsePreferences prefs,
			MApplication app, final Shell parent, final UISynchronize sync,
			final IProvisioningAgent agent, final IWorkbench workbench,
			final Logger logger) {
		InstallHandler.install(prefs, app, parent, sync, agent, workbench,
				logger);
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
			List<IProcessorItemWrapper<IE4LogsisLog>> procs,
			MApplication application) {
		for (IProcessorItemWrapper<IE4LogsisLog> proc : procs) {
			// inject main eclipse context to the processor
			ContextInjectionFactory.inject(proc, application.getContext());
		}
	}

	private void initOtherItems(List<RootPaletteItem> roots) {
		roots.add(new OtherPaletteItems());
	}

	@Focus
	public void setFocus() {
		viewer.getTree().setFocus();
	}
}
