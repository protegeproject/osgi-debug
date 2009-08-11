package org.protege.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;

public interface PackageViewer {
    void initialize(BundleContext context, PackageAdmin packages) throws Exception;
    
    void dispose();
}
