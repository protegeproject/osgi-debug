package org.protege.osgi;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.packageadmin.PackageAdmin;
import org.protege.osgi.graph.MainFrame;
import org.protege.osgi.servlet.Servlets;

public class DebugActivator implements BundleActivator {
    private static Logger log = Logger.getLogger(DebugActivator.class);
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
	        startServlets(context, packageAdmin);
	        startJung(context, packageAdmin);
	    }
	}
	
	private void startServlets(BundleContext context, 
	                           PackageAdmin packageAdmin) {
	    try {
	        Class<?> c = Class.forName("org.protege.osgi.servlet.Servlets");
	        servlets = (PackageViewer) c.newInstance();
	        servlets.initialize(context, packageAdmin);
	    }
	    catch (Throwable t) {
	        log.error("Could not start servlet based debug", t);
	        log.info("Trying swing based debug");
	    }
	}
	
	private void startJung(BundleContext context,
	                       PackageAdmin packages) {
	       try {
	            Class<?> c = Class.forName("org.protege.osgi.graph.MainFrame");
	            jung = (PackageViewer) c.newInstance();
	            jung.initialize(context, packages);
	        }
	        catch (Throwable t) {
	            log.error("Could not start swing based debug", t);
	        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	    servlets.dispose();
	}
}
