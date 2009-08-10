package org.protege.osgi;

import javax.swing.JFrame;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.protege.osgi.graph.MainPanel;
import org.protege.osgi.servlet.Servlets;

public class DebugActivator implements BundleActivator {
    private PackageAdmin packageAdmin;
    private Servlets servlets;
    private boolean doSwing = true;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	    ServiceReference packageReference = context.getServiceReference(PackageAdmin.class.getName());
	    if (packageReference != null) {
	        packageAdmin = (PackageAdmin) context.getService(packageReference);
	        servlets = new  Servlets(context, packageAdmin);
	        if (doSwing) {
	            JFrame frame = new JFrame("OSGi Debug Frame");
	            MainPanel panel = new MainPanel(context, packageAdmin);
	            frame.setContentPane(panel);
	            frame.pack();
	            frame.setVisible(true);
	        }
	    }
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	    packageAdmin = null;
	    servlets.dispose();
	}
}
