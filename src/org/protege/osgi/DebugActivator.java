package org.protege.osgi;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DebugActivator implements BundleActivator {
    private Logger log = Logger.getLogger(DebugActivator.class);
    
    private PackageViewer servlets;
    private PackageViewer jung;   

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        boolean servletsStarted = startServlets(context);
        boolean jungStarted = startJung(context);
        if (!servletsStarted && !jungStarted) {
            log.warn("Could not start OSGi debug bundle");
        }
    }
	
	private boolean startServlets(BundleContext context) {
	    boolean success = false;
	    try {
	        Class<?> c = Class.forName("org.protege.osgi.servlet.Servlets");
	        servlets = (PackageViewer) c.newInstance();
	        servlets.initialize(context);
	        log.info("Servlet based OSGi debug services started");
	        success = true;
	    }
	    catch (Throwable t) {
	        log.info("Could not start servlet based debug" + t);
	        if (log.isDebugEnabled()) {
	            log.debug("Exception details", t);
	        }
	    }
	    return success;
	}
	
	private boolean startJung(BundleContext context) {
	    boolean success = false;
	    try {
	        Class<?> c = Class.forName("org.protege.osgi.graph.MainFrame");
	        jung = (PackageViewer) c.newInstance();
	        jung.initialize(context);
	        log.info("Grahical based OSGi debug services started");
	        success = true;
	    }
	    catch (Throwable t) {
	        log.info("Could not start swing based debug " + t);
	        if (log.isDebugEnabled()) {
	            log.debug("Exception details", t);
	        }
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
