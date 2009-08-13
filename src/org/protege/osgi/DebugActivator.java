package org.protege.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class DebugActivator implements BundleActivator {
    private PackageViewer servlets;
    private PackageViewer jung;
    

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	    ServiceReference packageReference = context.getServiceReference(PackageAdmin.class.getName());
	    if (packageReference != null) {
	        PackageAdmin packageAdmin = (PackageAdmin) context.getService(packageReference);
	        boolean servletsStarted = startServlets(context, packageAdmin);
	        boolean jungStarted = startJung(context, packageAdmin);
	        if (!servletsStarted && !jungStarted) {
	            context.getBundle().stop();
	        }
	    }
	}
	
	private boolean startServlets(BundleContext context, 
	                              PackageAdmin packageAdmin) {
	    boolean success = false;
	    try {
	        Class<?> c = Class.forName("org.protege.osgi.servlet.Servlets");
	        servlets = (PackageViewer) c.newInstance();
	        servlets.initialize(context, packageAdmin);
	        success = true;
	    }
	    catch (Throwable t) {
	        System.out.println("Could not start servlet based debug" + t);
	    }
	    return success;
	}
	
	private boolean startJung(BundleContext context,
	                          PackageAdmin packages) {
	    boolean success = false;
	    try {
	        Class<?> c = Class.forName("org.protege.osgi.graph.MainFrame");
	        jung = (PackageViewer) c.newInstance();
	        jung.initialize(context, packages);
	        success = true;
	    }
	    catch (Throwable t) {
	        System.out.println("Could not start swing based debug " + t);
	    }
	    return success;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	    servlets.dispose();
	}
}
