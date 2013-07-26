package org.protege.osgi;

import org.osgi.framework.BundleContext;

public interface PackageViewer {
    void initialize(BundleContext context) throws Exception;
    
    void dispose();
}
