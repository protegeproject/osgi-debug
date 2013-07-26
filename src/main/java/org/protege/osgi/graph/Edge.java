package org.protege.osgi.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWire;


public class Edge {
    private Bundle importer;
    private Bundle exporter;
    private Set<String> packages = new HashSet<String>();
    
    public Edge(Bundle exporter, Bundle importer, Set<String> packages) {
        this.importer = importer;
        this.exporter = exporter;
        this.packages = packages;
    }

    public Bundle getImporter() {
        return importer;
    }

    public Bundle getExporter() {
        return exporter;
    }

    public Set<String> getPackages() {
        return Collections.unmodifiableSet(packages);
    }
    
    

}
