package org.protege.osgi.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphBuilder {
    private BundleContext context;
    private PackageAdmin packages;
    private String packageName;
    
    public GraphBuilder(BundleContext context, PackageAdmin packages, String packageName)  {
        this.context = context;
        this.packages = packages;
        this.packageName = packageName;
    }
    
    public DirectedGraph<Bundle, Edge> getGraph() {
        DirectedSparseGraph<Bundle, Edge> graph = new DirectedSparseGraph<Bundle, Edge>();
        for (Bundle exporting : context.getBundles()) {
            graph.addVertex(exporting);
            Map<Bundle, Set<ExportedPackage>> map = new HashMap<Bundle, Set<ExportedPackage>>();
            ExportedPackage[] exports = packages.getExportedPackages(exporting);
            if (exports == null) {
                continue;
            }
            for (ExportedPackage export : exports) {
                /*
                if (packageName != null && !export.getName().equals(packageName)) {
                    continue;
                }
                */
                for (Bundle importing : export.getImportingBundles()) {
                    if (importing.equals(exporting)) {
                        continue;
                    }
                    Set<ExportedPackage> wires = map.get(importing);
                    if (wires == null)  {
                        wires = new HashSet<ExportedPackage>();
                        map.put(importing, wires);
                    }
                    wires.add(export);
                }
            }
            for (Entry<Bundle, Set<ExportedPackage>> entry : map.entrySet()) {
                graph.addEdge(new Edge(exporting,entry.getKey(), entry.getValue()), 
                              new Pair<Bundle>(exporting, entry.getKey()));
            }
        }
        return graph;
    }
}
