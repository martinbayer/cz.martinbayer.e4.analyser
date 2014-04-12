package cz.martinbayer.e4.analyser.persistence;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

@Deprecated
public class SerializationHelper {
	public static List<ClassLoader> getClassLoaders(BundleContext ctx,
			IEclipseContext eclipseContext) {
		ServiceReference<?> logser = ctx.getServiceReference(LogService.class);
		LogService ls = (LogService) ctx.getService(logser);
		try {
			ServiceReference<?>[] activatorsRefs = ctx.getServiceReferences(
					BundleActivator.class.getName(), null);
			if (activatorsRefs == null) {
				return new ArrayList<>();
			}
			ArrayList<ClassLoader> loaders = new ArrayList<ClassLoader>();
			for (ServiceReference<?> sr : activatorsRefs) {
				BundleActivator service = (BundleActivator) ctx.getService(sr);
				loaders.add(service.getClass().getClassLoader());
			}
			return loaders;
		} catch (InvalidSyntaxException e) {
			ls.log(LogService.LOG_ERROR, "Unable to get class loaders", e);
		}
		return new ArrayList<>();
	}

	public static List<Bundle> getBundles(BundleContext ctx,
			IEclipseContext eclipseContext) {
		ServiceReference<?> logser = ctx.getServiceReference(LogService.class);
		LogService ls = (LogService) ctx.getService(logser);
		try {
			ServiceReference<?>[] bundleRefs = ctx.getServiceReferences(
					Bundle.class.getName(), null);
			if (bundleRefs == null) {
				return new ArrayList<>();
			}
			ArrayList<Bundle> bundles = new ArrayList<Bundle>();
			for (ServiceReference<?> sr : bundleRefs) {
				Bundle b = (Bundle) ctx.getService(sr);
				bundles.add(b);
			}
			return bundles;
		} catch (InvalidSyntaxException e) {
			ls.log(LogService.LOG_ERROR, "Unable to get class loaders", e);
		}
		return new ArrayList<>();
	}
}
