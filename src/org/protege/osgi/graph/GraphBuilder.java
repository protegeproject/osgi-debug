package org.protege.osgi.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphBuilder {
    private BundleContext context;
    private String packageName;
    
    public GraphBuilder(BundleContext context, String packageName)  {
        this.context = context;
        this.packageName = packageName;
    }
    
    public DirectedGraph<Bundle, Edge> getGraph() {
        DirectedSparseGraph<Bundle, Edge> graph = new DirectedSparseGraph<Bundle, Edge>();
        for (Bundle exporting : context.getBundles()) {
            graph.addVertex(exporting);
            BundleWiring wiring = exporting.adapt(BundleWiring.class);
            Map<Bundle, Set<BundleWire>> map = new HashMap<Bundle, Set<BundleWire>>();
            List<BundleWire> exports = wiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);
            if (exports == null) {
                continue;
            }
            for (BundleWire export : exports) {
                Bundle importing = export.getRequirerWiring().getBundle();
                if (importing.equals(exporting)) {
                    continue;
                }
                Set<BundleWire> wires = map.get(importing);
                if (wires == null)  {
                    wires = new HashSet<BundleWire>();
                    map.put(importing, wires);
                }
                wires.add(export);
            }
            for (Entry<Bundle, Set<BundleWire>> entry : map.entrySet()) {
                graph.addEdge(new Edge(exporting,entry.getKey(), entry.getValue()), 
                              new Pair<Bundle>(exporting, entry.getKey()));
            }
        }
        return graph;
    }
}
