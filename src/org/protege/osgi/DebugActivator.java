package org.protege.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.protege.osgi.servlet.Servlets;

public class DebugActivator implements BundleActivator {
    private BundleContext bundleContext;
    private PackageAdmin packageAdmin;
    private Servlets servlets;


	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	    bundleContext = context;
        ServiceReference packageReference = context.getServiceReference(PackageAdmin.class.getName());
        if (packageReference != null) {
            packageAdmin = (PackageAdmin) context.getService(packageReference);
        }
        servlets = new  Servlets(context, packageAdmin);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	    packageAdmin = null;
	    this.bundleContext = null;
	    servlets.dispose();
	}
}
