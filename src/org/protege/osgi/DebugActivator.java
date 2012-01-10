package org.protege.osgi;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DebugActivator implements BundleActivator {
    private static final Logger log = Logger.getLogger(DebugActivator.class);
    
    public static final String PROTEGE_APPLICATION = "org.protege.editor.core.application";
    
    private PackageViewer servlets; 

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
    	boolean isProtege = isProtege(context);
        boolean servletsStarted = startServlets(context);
        boolean jungStarted = false;
        if (!isProtege) {
        	jungStarted = startJung(context);
        }
        if (!servletsStarted && !isProtege && !jungStarted) {
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
	
	public static boolean startJung(BundleContext context) {
	    boolean success = false;
	    PackageViewer jung;  
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
	
	private static boolean isProtege(BundleContext context) {
		for (Bundle b : context.getBundles()) {
			if (PROTEGE_APPLICATION.equals(b.getSymbolicName())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	    servlets.dispose();
	}
}
