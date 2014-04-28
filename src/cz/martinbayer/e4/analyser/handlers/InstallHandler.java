package cz.martinbayer.e4.analyser.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.BackingStoreException;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.utils.StringUtils;

public class InstallHandler {

	public static final String COMMAND_ID = "cz.martinbayer.e4.analyser.command.install";
	public static final String INSTALL_PARAM_APP_STARTING = "cz.martinbayer.e4.analyser.command.install.parameter.appstart";
	@Inject
	private Logger logger;

	@Execute
	public void execute(
			MApplication app,
			final IProvisioningAgent agent,
			final Shell parent,
			final UISynchronize sync,
			final IWorkbench workbench,
			@Preference(nodePath = ContextVariables.Property.PROJECT_OPERATION) final IEclipsePreferences prefs) {
		InstallHandler.install(prefs, app, parent, sync, agent, workbench,
				logger);
	}

	protected static Collection<IInstallableUnit> getInstallableUnits(
			URI repositoryLoc, IProgressMonitor monitor)
			throws ProvisionException, OperationCanceledException {
		BundleContext bundleContext = FrameworkUtil.getBundle(
				InstallHandler.class).getBundleContext();
		ServiceReference<?> reference = bundleContext
				.getServiceReference(IProvisioningAgent.SERVICE_NAME);

		final IProvisioningAgent agent = (IProvisioningAgent) bundleContext
				.getService(reference);
		IMetadataRepository repository = initiateRepositories(agent,
				repositoryLoc, monitor);

		Collection<IInstallableUnit> toInstall = repository.query(
				QueryUtil.ALL_UNITS, monitor).toUnmodifiableSet();
		return toInstall;
	}

	private Iterator<IInstallableUnit> getServiceIU() throws ProvisionException {
		BundleContext ctx = FrameworkUtil.getBundle(InstallHandler.class)
				.getBundleContext();

		ServiceReference<IProvisioningAgentProvider> sr = ctx
				.getServiceReference(IProvisioningAgentProvider.class);
		IProvisioningAgentProvider agentProvider = ctx.getService(sr);
		URI p2InstanceURI = null; // myself
		final IProvisioningAgent agent = agentProvider
				.createAgent(p2InstanceURI);

		IProfileRegistry regProfile = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);

		IProfile profileSelf = regProfile.getProfile(IProfileRegistry.SELF);

		// get service interface first
		IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery("id == $0",
				"de.vogella.osgi.quote");

		// This is what you need:
		IQueryResult<IInstallableUnit> allIUs = profileSelf.query(query,
				new NullProgressMonitor());

		// Let's output it:
		Iterator<IInstallableUnit> iterator = allIUs.iterator();
		return iterator;
	}

	private static IMetadataRepository initiateRepositories(
			IProvisioningAgent agent, URI repositoryLoc,
			IProgressMonitor monitor) throws ProvisionException,
			OperationCanceledException {
		// get the repository managers and define our repositories
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		manager.addRepository(repositoryLoc);
		artifactManager.addRepository(repositoryLoc);

		// Load and query the metadata
		IMetadataRepository metadataRepo = manager.loadRepository(new File(
				repositoryLoc).toURI(), monitor);
		return metadataRepo;
	}

	public static final void install(final IEclipsePreferences prefs,
			MApplication app, final Shell parent, final UISynchronize sync,
			final IProvisioningAgent agent, final IWorkbench workbench,
			final Logger logger) {
		final String repoLoc = prefs.get(
				ContextVariables.Property.PLUGINS_REPO, "");
		boolean appStarting = (boolean) app.getContext().get(
				InstallHandler.INSTALL_PARAM_APP_STARTING);
		final String value;
		if (!appStarting || StringUtils.isEmtpy(repoLoc)) {
			InputDialog dialog = new InputDialog(parent,
					"Insert repository location",
					"Insert the value for repository location", repoLoc, null);
			dialog.create();
			int status = dialog.open();

			value = dialog.getValue();
			if (status != Dialog.OK || value == null) {
				return;
			}
		} else {
			value = repoLoc;
		}
		Job j = new Job("Install Job") {
			private boolean doInstall = false;

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				logger.info("installation started");
				try {
					// create uri
					URI uri = null;
					try {
						File repoFolder = new File(value);

						if (!repoFolder.exists()) {
							logger.info("repository not found:"
									+ repoFolder.getPath());
							throw new FileNotFoundException(
									"File wasn't found:" + value);
						} else {
							if (repoLoc == null || !repoLoc.equals(value)) {
								prefs.put(
										ContextVariables.Property.PLUGINS_REPO,
										value);
								prefs.flush();
							}
						}
						logger.info("repository found:" + repoFolder.getPath());
						uri = repoFolder.toURI();
					} catch (final FileNotFoundException e) {
						sync.syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(parent, "URI invalid",
										e.getMessage());
							}
						});
						return Status.CANCEL_STATUS;
					} catch (BackingStoreException e) {
						logger.warn("Unable to store preferences");
					}
					Collection<IInstallableUnit> toInstall = getInstallableUnits(
							uri, monitor);
					logger.info("count of installable units:"
							+ toInstall.size());
					HashSet<IInstallableUnit> processors = new HashSet<IInstallableUnit>();
					for (IInstallableUnit unit : toInstall) {
						String serviceClass = unit.getProperty("service.name");
						if (IProcessorItemWrapper.SERVICE_NAME
								.equals(serviceClass)) {
							processors.add(unit);
						}
					}
					/* 1. Prepare update plumbing */

					final ProvisioningSession session = new ProvisioningSession(
							agent);
					final InstallOperation installOperation = new InstallOperation(
							session, processors);

					// set location of artifact and metadata repo
					installOperation.getProvisioningContext()
							.setArtifactRepositories(new URI[] { uri });
					installOperation.getProvisioningContext()
							.setMetadataRepositories(new URI[] { uri });

					/* 2. check for updates */

					// run update checks causing I/O
					final IStatus installStatus = installOperation
							.resolveModal(monitor);

					// failed to find updates (inform user and exit)
					if (installStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
						sync.syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog
										.openWarning(parent, "No updates",
												"No processors to install nor update found");
							}
						});
						return Status.CANCEL_STATUS;
					}

					/*
					 * 3. Ask if updates should be installed and run
					 * installation
					 */

					// found updates, ask user if to install?
					if ((installStatus.getSeverity() != IStatus.ERROR)) {
						sync.syncExec(new Runnable() {
							@Override
							public void run() {
								doInstall = MessageDialog
										.openQuestion(
												parent,
												"Really install new processors?",
												installOperation
														.getResolutionDetails());
							}
						});
					}

					if (doInstall) {
						final ProvisioningJob installProvisioningJob = installOperation
								.getProvisioningJob(monitor);
						// updates cannot run from within Eclipse IDE!!!
						if (installProvisioningJob == null) {
							System.err
									.println("Running update from within Eclipse IDE? This won't work!!!");
							throw new NullPointerException();
						}

						JobChangeAdapter adapter = new JobChangeAdapter() {
							@Override
							public void done(IJobChangeEvent event) {
								if (event.getResult().isOK()) {
									sync.syncExec(new Runnable() {

										@Override
										public void run() {
											boolean restart = MessageDialog
													.openQuestion(
															parent,
															"New processors installed and updated.",
															"Application needs to be restarted, do you want to restart?");
											if (restart) {
												workbench.restart();
											}
										}
									});

								}
								super.done(event);
							}
						};
						// register a job change listener to track
						// installation progress and notify user upon success
						installProvisioningJob.addJobChangeListener(adapter);
						installProvisioningJob.schedule();
					}
				} catch (ProvisionException e1) {
					e1.printStackTrace();
				} catch (OperationCanceledException e1) {
					e1.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		j.schedule();

	}
}
