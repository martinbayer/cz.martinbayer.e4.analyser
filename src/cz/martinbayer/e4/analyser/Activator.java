package cz.martinbayer.e4.analyser;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		// Thread.sleep(10000);
		Activator.context = bundleContext;
		Bundle[] bundles = context.getBundles();
		ServiceReference<?> logser = bundleContext
				.getServiceReference(LogService.class);
		LogService ls = (LogService) bundleContext.getService(logser);

		for (Bundle b : bundles) {
			Class<?> clzz = null;
			try {
				clzz = b.loadClass("cz.martinbayer.analyser.processors.IProcessorItemWrapper");
			} catch (ClassNotFoundException ex) {
				// do not catch the error, it only means that the bundle is not
				// processor bundle
			}
			if (clzz != null && b.getState() == Bundle.STARTING) {
				try {
					b.start();
					ls.log(LogService.LOG_INFO,
							"Bundle started: " + b.getSymbolicName());
				} catch (Exception e) {
					// ls.log(LogService.LOG_ERROR, "Unable to start bundle",
					// e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
