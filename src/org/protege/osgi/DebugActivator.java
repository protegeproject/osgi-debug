package org.protege.osgi;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.packageadmin.PackageAdmin;

public class DebugActivator implements BundleActivator {
    private BundleContext bundleContext;
    private HttpServlet classLoaderServlet;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	    bundleContext = context;
	    classLoaderServlet = new ClassLoaderServlet(context);
	    ServiceReference [] serviceReferences = context.getServiceReferences(HttpService.class.getName(), null);
	    if (serviceReferences != null) {
	        for (ServiceReference sr : serviceReferences) {
	            HttpService service = (HttpService) context.getService(sr);
	            registerServlet(service);
	        }
	    }
	    context.addServiceListener(new ServiceListener() {

	        public void serviceChanged(ServiceEvent event) {
	            if (event.getType() == ServiceEvent.REGISTERED) {
	                try {
	                    ServiceReference sr = event.getServiceReference();
	                    Object o = bundleContext.getService(sr);
	                    if (o != null && o instanceof HttpService) {
	                        registerServlet((HttpService) o);
	                    }
	                }
	                catch (NamespaceException e) {
	                    e.printStackTrace();
	                }
	                catch (ServletException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        
	    }, "(" +  Constants.OBJECTCLASS + "=" + HttpService.class.getName() + ")");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

	private void registerServlet(HttpService httpService) throws ServletException, NamespaceException {
	    if (httpService != null)  {
	        Dictionary params = new Hashtable();
	        httpService.registerServlet("/debug", classLoaderServlet, params, null);
	    }
	}
}
